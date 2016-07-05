package com.jsantos.ps.timeline;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.jsantos.ps.store.database.DS;

public class TimeLineBean {
	public static void insertEntry(String objectKey, String action) throws SQLException{
		Connection conn = DS.getConnection();
		try{
			PreparedStatement pst = conn.prepareStatement("insert into timeline (created, objectkey, action) values (?,?,?)");
			pst.setTimestamp(1, new Timestamp(new Date().getTime()));
			pst.setString(2, objectKey);
			pst.setString(3, action);
			pst.execute();
		}	
		finally{
			conn.close();
		}
	}
	
	public static void insertEntry(String objectKey, String action, Connection conn) throws SQLException{
		PreparedStatement pst = conn.prepareStatement("insert into timeline (created, objectkey, action) values (?,?,?)");
		pst.setTimestamp(1, new Timestamp(new java.util.Date().getTime()));
		pst.setString(2, objectKey);
		pst.setString(3, action);
		pst.execute();
	}
	
}
