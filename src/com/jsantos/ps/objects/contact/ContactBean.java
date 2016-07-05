package com.jsantos.ps.objects.contact;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.jsantos.ps.objects.wiki.XHtmlParser;
import com.jsantos.ps.store.database.DS;
import com.jsantos.ps.timeline.TimeLineAction;
import com.jsantos.ps.timeline.TimeLineBean;
import com.jsantos.util.logger.Logger;

public class ContactBean {
	private static final String MODULE = ContactBean.class.getSimpleName();
	String userName = null;
	String contactDetail = null;
	String objectKey = null;
	int    version = 0;

	public boolean findByKey() throws SQLException{
		Logger.logInfo(MODULE, " Method Called :findByKey() ");
		boolean found = false;
		Connection conn = DS.getConnection();
		try{
			PreparedStatement pst = conn.prepareStatement("select * from objectlist where objectkey=?");
			pst.setString(1, objectKey);
			ResultSet rs = pst.executeQuery();
			if (rs.next()){
				userName = rs.getString("description");
				contactDetail = rs.getString("htmlsrc");
				version = rs.getInt("version");
				found = true;
			}
		}
		finally{
			conn.close();
		}
		return found;
	}
	
	public void getTables(){
		Connection conn;
		try {
			conn = DS.getConnection();
			conn.setAutoCommit(false);
			Statement st =  conn.createStatement();
			ResultSet rs =  st.executeQuery("SELECT * FROM INFORMATION_SCHEMA.TABLES ");
			System.out.println("rs : " + rs);
			while(rs.next()){
				String data = rs.getString(3);
				System.out.println(" Table Name : "+data);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
		
	public void save() throws SQLException, XPathExpressionException, ParserConfigurationException, SAXException, IOException{
		Logger.logInfo(MODULE, " Method Called :save() ");
		if (null == userName || 0 == userName.trim().length()) throw new RuntimeException("Need a name, please");
		
		Connection conn = DS.getConnection();
		conn.setAutoCommit(false);
		XHtmlParser parser = new XHtmlParser();
		/*contactDetail = parser.cleanUpHtml(objectKey, contactDetail, conn);*/
		try{
			PreparedStatement st = conn.prepareStatement("select count(*) from objectlist where objectkey=?");
			st.setString(1, objectKey);
			ResultSet rs = st.executeQuery();
			rs.next();
			if (0<rs.getInt(1)){ //update
				//guardamos la version anterior en el history
				PreparedStatement pst = conn.prepareStatement("insert into objecthistory  select * from objectlist where objectkey = ? ");
				pst.setString(1, objectKey);
				pst.execute();				
				// y guardamos la nueva en objectlist
								
				pst = conn.prepareStatement("update objectlist set description=?, htmlsrc=?, textcontent=?, version=? where objectkey=?");
				pst.setString(1, userName);
				pst.setString(2, contactDetail);
				pst.setString(3, getTextContent());
				pst.setInt(4,version+1);
				pst.setString(5, objectKey);
				pst.execute();
				pst.close();
				
				TimeLineBean.insertEntry(objectKey, TimeLineAction.WRITE, conn);
			}
			else{ //create
				PreparedStatement pst = conn.prepareStatement("insert into objectlist (objectkey, version , objecttype, CREATED, LASTMODIFIED, LASTSHOWN, description, htmlsrc, textcontent) values (?,?,'contact',?,?,?,?,?, ?)");
				objectKey = UUID.randomUUID().toString();
				pst.setString(1, objectKey);
				pst.setInt(2, version);
				pst.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
				pst.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));
				pst.setTimestamp(5, new Timestamp(new java.util.Date().getTime()));
				pst.setString(6, userName);
				pst.setString(7, contactDetail);
				pst.setString(8, getTextContent());
				pst.execute();
				pst.close();
				
				TimeLineBean.insertEntry(objectKey, TimeLineAction.CREATE, conn);
			}
			parser.saveLinks(objectKey, conn);
			conn.commit();
			
		}
		catch (SQLException sqle){
			conn.rollback();
			throw sqle;
		}
		finally{
			conn.setAutoCommit(true);
			conn.close();
		}
	}
	
	
	
	
	
	public boolean delete() throws SQLException{
		boolean sw = false;
		Connection conn = DS.getConnection();
		conn.setAutoCommit(false);
		
		try{
			PreparedStatement pst = conn.prepareStatement("delete from objectlist where objectkey = ? ");
			pst.setString(1, objectKey);
			pst.execute();
			pst = conn.prepareStatement("delete from objecthistory where objectkey = ? ");
			pst.setString(1, objectKey);
			pst.execute();
			pst = conn.prepareStatement("delete from link where topk = ? ");
			pst.setString(1, objectKey);
			pst.execute();
			pst = conn.prepareStatement("delete from timeline where objectkey = ? ");
			pst.setString(1, objectKey);
			pst.execute();
			
			conn.commit();
		}
		catch (SQLException sqle){
			conn.rollback();
			
			throw sqle;
		}
		finally{
			conn.setAutoCommit(true);
			
			sw = true;
			conn.close();
			
			System.out.println("delete: "+objectKey);
		}
		return sw;
	}
	
	
	
	String getTextContent(){
		StringBuffer text = new StringBuffer();
		text.append(userName);
		text.append(" ");
		text.append(contactDetail.replaceAll("\\<.*?>",""));
		return text.toString();

	}
	
	public String getObjectkey() {
		return objectKey;
	}

	public void setObjectkey(String htmlkey) {
		this.objectKey = htmlkey;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getContactDetail() {
		return contactDetail;
	}


	public void setContactDetail(String contactDetail) {
		this.contactDetail = contactDetail;
	}


	public int getVersion(){
		return version;
	}

}
