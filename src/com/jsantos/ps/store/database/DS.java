package com.jsantos.ps.store.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import com.jsantos.ps.store.DataFolder;
import com.jsantos.ps.webapp.CTX;

public class DS {
	public static Connection getConnection() throws SQLException{
		return getDataSource().getConnection();
	}

	public static DataSource getDataSource(){
		return CTX.getDataSource();
	}

	
	public static BasicDataSource createDataSource(ServletContext servletContext) throws SQLException, IOException{
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		
		//dataSource.setDriverClassName("net.sf.log4jdbc.DriverSpy");
		dataSource.setUsername("sa");
			
		DataFolder.createDataFolder();
		String dataFolderPath = DataFolder.findDataFolderPath();

		dataFolderPath = dataFolderPath + "_casti";
		String databaseFilePath = dataFolderPath + "database.h2.db";

		System.out.println("Database filepath: " + databaseFilePath);
		System.out.println("url : "+dataSource.getUrl());
		System.out.println("username : "+dataSource.getUsername());
		System.out.println("password : "+dataSource.getPassword());
		//dataSource.setUrl("jdbc:h2:" + new File(databaseFilePath).getCanonicalPath());
		dataSource.setUrl("jdbc:h2:" + new File(databaseFilePath).getCanonicalPath());
		
		try {

			Class.forName("org.h2.Driver");
			Connection connection = DriverManager.getConnection("jdbc:h2:" + new File(databaseFilePath).getCanonicalPath(), dataSource.getUsername(),dataSource.getPassword());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		checkDbContents(dataSource, servletContext);

		return dataSource;
		
	}
	
	
	
	static void checkDbContents(DataSource datasource, ServletContext servletContext) throws SQLException, IOException{
		
		Connection conn = datasource.getConnection();
		try{
			Statement st = conn.createStatement();
			st.executeQuery("select * from OBJECTLIST");
									
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null, "OBJECTHISTORY", null);
			if (!tables.next()) {
				File scriptFile = new File(servletContext.getRealPath("/") + "WEB-INF/upgrade_v1_to_v2.sql");
				ScriptRunner.runScript(scriptFile, datasource);
				System.out.println("upgrading database from v1 to v2.");
			}
			tables = dbm.getTables(null, null, "DBINFO", null);
			if (!tables.next()) {
				File scriptFile = new File(servletContext.getRealPath("/") + "WEB-INF/upgrade_v2_to_v3.sql");
				ScriptRunner.runScript(scriptFile, datasource);
				System.out.println("upgrading database from v2 to v3.");
			}
			tables = dbm.getTables(null, null, "MAILACCOUNTS", null);
			if (!tables.next()) {
				File scriptFile = new File(servletContext.getRealPath("/") + "WEB-INF/upgrade_add_mailaccounts.sql");
				ScriptRunner.runScript(scriptFile, datasource);
				System.out.println("upgrading database adding MAILACCOUNTS table");
			}
						
			
		}
		catch (SQLException sqle){
			//try to create the database structure
			
			File scriptFile = new File(servletContext.getRealPath("/") + "WEB-INF/create_tables.sql");
			ScriptRunner.runScript(scriptFile, datasource);
			scriptFile = new File(servletContext.getRealPath("/") + "WEB-INF/upgrade_v1_to_v2.sql");
			ScriptRunner.runScript(scriptFile, datasource);
			scriptFile = new File(servletContext.getRealPath("/") + "WEB-INF/upgrade_v2_to_v3.sql");
			ScriptRunner.runScript(scriptFile, datasource);
		}
		finally{
			conn.close();
		}
	}
	
}
