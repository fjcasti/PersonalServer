package com.jsantos.ps.store.backup.restore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.jsantos.ps.store.DataFolder;
import com.jsantos.ps.store.backup.create.DatabaseBackup;
import com.jsantos.ps.store.database.DS;

public class DatabaseBackupRestorer {

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException, SQLException{
		new DatabaseBackupRestorer().restoreDatabase("/home/jsantos/store/personalserver/database_backup_2009-10-05_00-55-04.xml");
	}
	
	public void restoreDatabase(String databaseBackupFilePath) throws SQLException, FileNotFoundException, SAXException, IOException, ParserConfigurationException{
		emptyDatabase();
		System.out.println("Start restore ");
		new DatabaseBackupRestorer().parse(new FileInputStream(databaseBackupFilePath));
		System.out.println("database Restore finished from file " + databaseBackupFilePath);
		
	}
	
	static void emptyDatabase() throws SQLException{
		Connection conn = DS.getConnection();
		Statement st = conn.createStatement();
		
		/*for (int i=DatabaseBackup.tableOrder.length-1; i>=0; i--){
			String sql = "truncate table " + DatabaseBackup.tableOrder[i] + " cascade";
			st.execute(sql);
		}*/
		
		 ResultSet rs = conn.getMetaData().getTables("DATABASE.H2.DB","PUBLIC", "%", null);
		    while (rs.next()) {
		    	
		    	//String sql = "truncate table " + rs.getString("TABLE_NAME") + " cascade";
		    	String sql = "truncate table " + rs.getString("TABLE_NAME");
		    	st.execute(sql);
		      
		    }  
		
		
		conn.close();
	}
	
	public void parse(InputStream is) throws SAXException, IOException, ParserConfigurationException{
		SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(is, new BackupHandler());
	}
	
	class BackupHandler extends DefaultHandler{
		Stack<Tag> stack = new Stack<Tag>();
		Connection conn = null;
		Hashtable<String, String> currentRow = null;
		Hashtable<String, Integer> currentTableColumnTypes = null;
		
		@Override
		public void startDocument() throws SAXException {
			try {
				conn = DS.getConnection();
			} catch (SQLException e) {
				throw new SAXException(e);
			}
		}

		@Override
		public void endDocument() throws SAXException {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new SAXException(e);
			}
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			Tag tag = new Tag(qName, attributes);
			stack.push(tag);
			if ("record".equals(qName)) currentRow = new Hashtable<String,String>();
			if ("table".equals(qName)) {
				currentTableColumnTypes = new Hashtable<String, Integer>();
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			stack.peek().content += new String(ch, start, length);
		}


		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			try{
				if ("record".equals(qName)) {
					printStack(stack);
					insertRecord(currentRow, currentTableColumnTypes, stack.get(stack.size()-2).attributes.get("name"));
					for (String fieldName:currentRow.keySet()){
						System.out.println(fieldName + ": \t" + currentRow.get(fieldName));
					}
					currentRow = null;
				}
				if ("fieldtype".equals(qName)) currentTableColumnTypes.put(stack.peek().attributes.get("name"), Integer.parseInt(stack.peek().attributes.get("javasqltype")));
				if ("field".equals(qName)) currentRow.put(stack.peek().attributes.get("name"), stack.peek().content);
				if ("table".equals(qName)) currentTableColumnTypes = null;
				stack.pop();
			}
			catch (SQLException sqle){
				throw new SAXException(sqle);
			}
			catch (ParseException pe){
				throw new SAXException(pe);
			}
		}
	}
	
	void insertRecord(Hashtable<String,String> values, Hashtable<String, Integer> columns, String table) throws SQLException, ParseException{
		String sql = "insert into " + table + " (";
		
		for (Iterator<String> i = columns.keySet().iterator(); i.hasNext(); ){
			sql += i.next();
			if (i.hasNext()) sql += ",";
		}
		sql += ") values (";

		for (Iterator<String> i = columns.keySet().iterator(); i.hasNext(); ){
			sql += "?";
			i.next();
			if (i.hasNext()) sql += ",";
		}
		sql += ")";
		System.out.println(sql);
		
		Connection conn = DS.getConnection();
		PreparedStatement pst = conn.prepareStatement(sql);
		int nCol =0;
		for (Iterator<String> i = columns.keySet().iterator(); i.hasNext(); ){
			String fieldName = i.next();
			nCol ++;
			int type = columns.get(fieldName);
			String fieldValue = values.get(fieldName);
			if (null == fieldValue || 0 == fieldValue.trim().length()) pst.setNull(nCol, type);
			else{
				fieldValue = fieldValue.trim();
				switch(type){
				
				case Types.BINARY:
					pst.setString(nCol, fieldValue);
					
				break;
				case Types.VARCHAR:
					pst.setString(nCol, fieldValue);
				break;
				case Types.INTEGER:
					pst.setInt(nCol, Integer.parseInt(fieldValue));
				break;
				case Types.TIMESTAMP:
					fieldValue = fieldValue.substring(0, "yyyy-MM-dd HH:mm:ss.SS".length());
					pst.setTimestamp(nCol, new Timestamp(DatabaseBackup.dateFormat.parse(fieldValue).getTime()));
				break;
				case Types.CHAR:
					pst.setString(nCol, fieldValue);
				break;
				case Types.OTHER:
					pst.setString(nCol, fieldValue);
				break;
				case Types.CLOB:
					pst.setString(nCol, fieldValue);
				
				break;
				default:
					throw new SQLException("Type unknown with java.sql.Type: " + type);
				}
			}
		}
		pst.execute();
		conn.close();
		
		
	}
	
	class Tag{
		String qName;
		Hashtable<String, String> attributes;
		String content = "";
		
		public Tag(String qName, Attributes attributes){
			this.qName = qName;
			this.attributes = new Hashtable<String, String>();
			for (int att = 0; att<attributes.getLength(); att++) this.attributes.put(attributes.getQName(att), attributes.getValue(att));
		}
	}
	
	void printStack(Stack<Tag> stack){
		for (int i=0; i<stack.size(); i++) {
			System.out.print(stack.get(i).qName + "(");
			for (String name: stack.get(i).attributes.keySet()) System.out.print(name + "= " + stack.get(i).attributes.get(name));
			System.out.print(") ");
		}
		System.out.println("");
	}
	
	
}
