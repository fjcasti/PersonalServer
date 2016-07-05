package com.jsantos.ps.tests;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import com.jsantos.ps.store.backup.create.IndentedWriter;
import com.jsantos.ps.store.backup.create.TableDumper;
import com.jsantos.ps.store.database.DS;

public class TestTableDumper {
	
	public static void main(String[] args) throws SQLException, IOException{
		Connection conn = DS.getConnection();
		IndentedWriter out = new IndentedWriter(new FileWriter("table.dump"));
		new TableDumper().dump("objectlist", out, conn, new Vector<String>(), new Vector<String>());
		out.close();
		
		System.out.println("dump completed");
	}
}
