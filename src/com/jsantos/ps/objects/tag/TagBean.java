package com.jsantos.ps.objects.tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.zkoss.zul.Messagebox;

import com.jsantos.ps.objects.link.LinkBean;
import com.jsantos.ps.store.database.DS;

public class TagBean {
	String objectKey = null;
	String description = null;
	
	static TagBean findTagByName(String name) throws SQLException{
		TagBean retValue = null;
		Connection conn = DS.getConnection();
		try{
			String sql = "select * from objectlist where objecttype='tag' and textcontent=?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, name);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				retValue = new TagBean();
				retValue.objectKey = rs.getString("objectkey");
				retValue.description = rs.getString("description");
			}
		}
		finally{
			conn.close();
		}
		return retValue;
	}

	static TagBean findTagByObjectKey(String oid) throws SQLException{
		TagBean retValue = null;
		Connection conn = DS.getConnection();
		try{
			String sql = "select * from objectlist where objectkey=?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, oid);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				retValue = new TagBean();
				retValue.objectKey = rs.getString("objectkey");
				retValue.description = rs.getString("description");
			}
		}
		finally{
			conn.close();
		}
		return retValue;
	}
	
	public static void addTagToObject(String targetKey, String tagName) throws SQLException{
		Connection conn = DS.getConnection();
		try{
			addTagToObject(targetKey, tagName, conn);
		}
		finally{
			conn.close();
		}
	}
	
	public static void addTagToObject(String targetKey, String tagName, Connection conn) throws SQLException{
		TagBean tagBean = findTagByName(tagName);
		if (null == tagBean) tagBean = createNewTag(tagName, conn);

		LinkBean linkBean = new LinkBean();
		linkBean.setFromPk(tagBean.getObjectKey());
		linkBean.setToPk(targetKey);
		linkBean.setVerb("tag");
		linkBean.save(conn);
	}
	

	public static void removeTagFromObject(String targetKey, String tagName) throws SQLException{
		TagBean tagBean = findTagByName(tagName);
		LinkBean.removeLink(tagBean.getObjectKey(), targetKey);
	}
	
	public boolean delete() throws SQLException{
		boolean sw = false;
		Connection conn = DS.getConnection();
		try{
			String sql = "select objectkey,description from objectlist  where objectkey in (select topk   from link where frompk in (select objectkey  from objectlist  where description = 'system')) and objectkey = ?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, objectKey);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				Messagebox.show("this tag can not be deleted!, belongs to the system");
				
			}else{
				
				pst = conn.prepareStatement("delete from objectlist where objectkey = ? ");
				pst.setString(1, objectKey);
				pst.execute();
				pst = conn.prepareStatement("delete from link where topk = ? ");
				pst.setString(1, objectKey);
				pst.execute();
				pst = conn.prepareStatement("delete from link where frompk = ? ");
				pst.setString(1, objectKey);
				pst.execute();
				pst = conn.prepareStatement("delete from timeline where objectkey = ? ");
				pst.setString(1, objectKey);
				pst.execute();				
			}			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			conn.close();
			sw = true;
		}
				
		return sw;
	}
	
	static TagBean createNewTag(String tagName, Connection conn) throws SQLException{
		TagBean tagBean = new TagBean();
		tagBean.setDescription(tagName);
		tagBean.objectKey = UUID.randomUUID().toString();
		PreparedStatement pst = conn.prepareStatement("insert into objectlist (objectkey, objecttype, CREATED, LASTMODIFIED, LASTSHOWN, description, htmlsrc, textcontent, version) values (?,'tag',?,?,?,?,?,?,0)");
		pst.setString(1, tagBean.objectKey);
		pst.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
		pst.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
		pst.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));
		pst.setString(5, tagName);
		pst.setString(6, null);
		pst.setString(7, tagName);
		pst.execute();
		pst.close();
		return tagBean;
	}
	
	public String getObjectKey() {
		return objectKey;
	}

	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
}
