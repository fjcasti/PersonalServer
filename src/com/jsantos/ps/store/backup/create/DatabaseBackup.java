package com.jsantos.ps.store.backup.create;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.jsantos.ps.store.database.DS;

public class DatabaseBackup {
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
	public Vector<String> tableOrder;

	public static void main(String[] args) throws Exception {
		new DatabaseBackup().createBackup("database_backup_"+new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())+".xml");
	}

	public void createBackup(String filePath) throws Exception{
		Vector<String> cdataColumns = new Vector<String>();
		tableOrder = new Vector<String>();
		
		cdataColumns.add("objectlist.htmlsrc");
		cdataColumns.add("objectlist.textcontent");
		cdataColumns.add("objectlist.search_vector");
		cdataColumns.add("objectlist.description");
		cdataColumns.add("mail.fullheaders");

		cdataColumns.add("mail.messageid");
		cdataColumns.add("mail.fromaddress");
		cdataColumns.add("mail.toaddress");
		cdataColumns.add("mail.cc");
		cdataColumns.add("mail.bcc");
		
		cdataColumns.add("fileref.filename");
		cdataColumns.add("savedsearch.sql");
		
		Vector<String> omitFields = new Vector<String>();
		omitFields.add("search_vector");
		
		
		IndentedWriter out = new IndentedWriter(new FileWriter(filePath));
		out.println("<database>");
		out.indentLevel++;
		out.println("<version>2.0</version>");
		out.println("<dateTime>"+dateFormat.format(new Date())+"</dateTime>");
		
		TableDumper dumper = new TableDumper();
		Connection conn = DS.getConnection();
		
		 ResultSet rs = conn.getMetaData().getTables("DATABASE.H2.DB","PUBLIC", "%", null);
		    while (rs.next()) {
		    	tableOrder.add(rs.getString("TABLE_NAME"));
		    	//System.out.println(rs.getString("TABLE_NAME"));
		      
		    }  
		//Statement st = conn.createStatement();
		//ResultSet rs = st.executeQuery("SELECT relname FROM pg_class WHERE relname !~ '^(pg_|sql_)' AND relkind = 'r'");
		//while(rs.next()){
		//	dumper.dump(rs.getString("relname"), out, conn, cdataColumns);
		//	
		//}
		for (int i=0; i<tableOrder.size(); i++){
			dumper.dump(tableOrder.get(i), out, conn, cdataColumns, omitFields);
		}
		conn.close();
		
		out.indentLevel--;
		out.print("</database>");
		out.close();
		System.out.println("Backup created.");
	}
	
	
	
	
	
	void pgBackup() throws IOException, InterruptedException{
        String path = "posgres_backup.sql";

        String user = "postgres";
        String dbase = "personalserver";
        String password = "jjsoft";

        Process p;
        ProcessBuilder pb;

        pb = new ProcessBuilder("pg_dump", "-v", "-D", "-f", path, "-U", user, dbase);
        pb.environment().put("PGPASSWORD", password);
        pb.redirectErrorStream(true);
        p = pb.start();      
        p.waitFor();
	
	}

}
