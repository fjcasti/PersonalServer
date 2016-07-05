package com.jsantos.ps.store.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class ScriptRunner {
	public static void runScript(File scriptFile, DataSource datasource) throws IOException, SQLException{
		String script= readFileAsString(scriptFile);
		String[] statements = script.split(";");
		
		Connection conn = datasource.getConnection();
		try{
			Statement st = conn.createStatement();
			for (String sql:statements) st.execute(sql);
			st.close();
		}
		finally{
			conn.close();
		}
	}
	
	
	private static String readFileAsString(File file) throws java.io.IOException{
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }


}
