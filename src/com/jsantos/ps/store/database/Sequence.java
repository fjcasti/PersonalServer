package com.jsantos.ps.store.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sequence {
	public static long nextVal(String sequenceName) throws SQLException{
		long retValue = 0;
		
		Connection conn = DS.getConnection();
		try{
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select nextval('" + sequenceName + "')");
			rs.next();
			retValue = rs.getLong(1);
		}
		finally{
			conn.close();
		}
		return retValue;
	}
}
