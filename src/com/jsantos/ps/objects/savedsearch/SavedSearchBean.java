package com.jsantos.ps.objects.savedsearch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.jsantos.ps.store.database.DS;
import com.jsantos.ps.store.database.Sequence;
import com.jsantos.ps.timeline.TimeLineAction;
import com.jsantos.ps.timeline.TimeLineBean;

public class SavedSearchBean {
	String objectKey = null;
	String searchSql = null;
	String description = null;
	
	public static SavedSearchBean findByObjectKey(String objectKey) throws SQLException{
		SavedSearchBean retValue = null;
		String sql = "select o.objectKey, sql, description from objectlist o, savedsearch s where o.objectkey=s.objectkey and o.objectkey=?";
		Connection conn = DS.getConnection();
		try{
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, objectKey);
			ResultSet rs = pst.executeQuery();
			if (rs.next()){
				retValue = new SavedSearchBean();
				retValue.objectKey = objectKey;
				retValue.searchSql = rs.getString("sql");
				retValue.description = rs.getString("description");
			}
		}
		finally{
			conn.close();
		}
		return retValue;
		
	}
	
	public void save() throws SQLException{
		if (null == objectKey){ // insert
			objectKey = UUID.randomUUID().toString();
			Connection conn = DS.getConnection();
			conn.setAutoCommit(false);
			try{
				String sql ="insert into objectlist (objecttype, objectkey, description, textcontent) values('savedsearch', ?, ?,?)";
				PreparedStatement pst = conn.prepareStatement(sql);
				pst.setString(1, objectKey);
				pst.setString(2, description);
				pst.setString(3, description);
				pst.execute();
				pst.close();
				
				sql = "insert into savedsearch (objectkey, sql) values (?, ?)";
				pst = conn.prepareStatement(sql);
				pst.setString(1, objectKey);
				pst.setString(2, searchSql);
				pst.execute();
				pst.close();
	
				TimeLineBean.insertEntry(objectKey, TimeLineAction.CREATE, conn);
				
				conn.commit();
				conn.setAutoCommit(true);
			}
			finally{
				conn.close();
			}
		}
		else{ //update
			Connection conn = DS.getConnection();
			conn.setAutoCommit(false);
			try{
				String sql = "update savedsearch set sql=? where objectkey=?";
				PreparedStatement pst = conn.prepareStatement(sql);
				pst.setString(1, searchSql);
				pst.setString(2, objectKey);
				pst.execute();
				pst.close();
				
				sql = "update objectlist set lastmodified=current_timestamp, description=? where objectkey=?";
				pst = conn.prepareStatement(sql);
				pst.setString(1, description);
				pst.setString(2, objectKey);
				pst.execute();
				pst.close();
	
				TimeLineBean.insertEntry(objectKey, TimeLineAction.WRITE, conn);
				
				conn.commit();
				conn.setAutoCommit(true);
			}
			finally{
				conn.close();
			}
			
		}
		
	}
	
	
	public String getObjectKey() {
		return objectKey;
	}
	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}
	public String getSearchSql() {
		return searchSql;
	}
	public void setSearchSql(String searchSql) {
		this.searchSql = searchSql;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
