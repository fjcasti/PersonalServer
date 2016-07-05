package com.jsantos.ps.objects.wiki;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Messagebox;

import com.jsantos.ps.store.database.DS;
import com.jsantos.ps.timeline.TimeLineAction;
import com.jsantos.ps.timeline.TimeLineBean;
import com.jsantos.util.logger.Logger;

public class WikiPageBean {
	private static final String MODULE = WikiPageBean.class.getSimpleName();
	String description = null;
	String htmlsrc = null;
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
				description = rs.getString("description");
				htmlsrc = rs.getString("htmlsrc");
				version = rs.getInt("version");
				found = true;
			}
		}
		finally{
			conn.close();
		}
		return found;
	}
	
	
	
	public void save() throws SQLException, XPathExpressionException, ParserConfigurationException, SAXException, IOException{
		Logger.logInfo(MODULE, " Method Called :save() ");
		if (null == description || 0 == description.trim().length()) throw new RuntimeException("Please specify title");
		String objectkey1 = Executions.getCurrent().getParameter("oid");
		Connection conn = DS.getConnection();
		conn.setAutoCommit(false);
		XHtmlParser parser = new XHtmlParser();
		if(htmlsrc!=null &&  htmlsrc.length()>0){
			htmlsrc = htmlsrc.replaceAll("&gt;", "");
			htmlsrc = htmlsrc.replaceAll("&lt;", "");
			htmlsrc = htmlsrc.replaceAll("[\\p{Punct}[^<>.=,\"\"/]{}&&]", "");
		}
		htmlsrc = parser.cleanUpHtml(objectKey, htmlsrc, conn);
		Logger.logInfo(MODULE, "Cleaned htmlsrc: " + htmlsrc);
		Logger.logInfo(MODULE, "objectKey :"+objectKey);
		Logger.logInfo(MODULE, "objectKey1 :"+objectkey1);
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
				pst.setString(1, description);
				pst.setString(2, htmlsrc);
				pst.setString(3, getTextContent());
				pst.setInt(4,version+1);
				pst.setString(5, objectKey);
				pst.execute();
				pst.close();
				
				TimeLineBean.insertEntry(objectKey, TimeLineAction.WRITE, conn);
			}
			else{ //create
				PreparedStatement pst = conn.prepareStatement("insert into objectlist (objectkey, version , objecttype, CREATED, LASTMODIFIED, LASTSHOWN, description, htmlsrc, textcontent) values (?,?,'html',?,?,?,?,?, ?)");
				objectKey = UUID.randomUUID().toString();
				pst.setString(1, objectKey);
				pst.setInt(2, version);
				pst.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
				pst.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));
				pst.setTimestamp(5, new Timestamp(new java.util.Date().getTime()));
				pst.setString(6, description);
				pst.setString(7, htmlsrc);
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
		text.append(description);
		text.append(" ");
		text.append(htmlsrc.replaceAll("\\<.*?>",""));
		System.out.println(text.toString());
		return text.toString();

	}
	
	public String getObjectkey() {
		return objectKey;
	}

	public void setObjectkey(String htmlkey) {
		this.objectKey = htmlkey;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String filename) {
		this.description = filename;
	}

	public String getHtmlsrc() {
		return htmlsrc;
	}

	public void setHtmlsrc(String htmlsrc) {
		this.htmlsrc = htmlsrc;
	}
	
	public int getVersion(){
		return version;
	}
	
}
