package com.pirilabs.psh2.tests;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Composer;

public class TestDB implements Composer{

	public String testDB() throws SQLException{
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUsername("sa");
		//dataSource.setPassword();
		dataSource.setUrl("jdbc:h2:tcp://localhost/~test");
		
		Connection conn = dataSource.getConnection();
		
		PreparedStatement st = conn.prepareStatement("insert into link (linkkey, frompk, topk, verb, created, flags)values (2, ?, ?, 'hi', ?, 0)");
		st.setString(1, UUID.randomUUID().toString());
		st.setString(2, UUID.randomUUID().toString());
		st.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
		st.execute();
		
		
		return null;
	}

	@Override
	public void doAfterCompose(Component arg0) throws Exception {
		testDB();
	}

}
