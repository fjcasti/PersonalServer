package com.jsantos.ps.store.backup.create;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Vector;

public class TableDumper {
	
	public void dump(String tableName, IndentedWriter out, Connection conn, Vector<String> cdataColumns, Vector<String> omitFields) throws IOException, SQLException{
		String sql = "select * from " + tableName;
		
		out.println("<table name=\"" + tableName + "\" >");
		out.indentLevel ++ ;
		
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		
		outputTableStructure(out, rs);
		
		while (rs.next()){
			out.println("<record>");
			out.indentLevel++ ;
			for (int i=1; i<=rs.getMetaData().getColumnCount(); i++){
				String columnName = rs.getMetaData().getColumnName(i);
				if (!omitFields.contains(columnName.toLowerCase())){
					out.print("<field name=\"" + columnName + "\">");
					
				
					
					if (cdataColumns.contains((tableName + "." + columnName).toLowerCase()))
						{
						
						out.print("<![CDATA[");
						
						}
					String data = rs.getString(i);
					if (null !=data) data= data.replaceAll("]]>", "]]]]><![CDATA[>");
					switch(rs.getMetaData().getColumnType(i)){
					case Types.TIMESTAMP:
							out.print(DatabaseBackup.dateFormat.format(rs.getTimestamp(i)));
						break;
					default:
						out.print(data);
					}
					if (cdataColumns.contains((tableName + "." + columnName).toLowerCase())) out.print("]]>");
					out.println("</field>");
				}
			}
			out.indentLevel--;
			out.println("</record>");
		}
		
		out.indentLevel--;
		out.println("</table>");
	}
	
	void outputTableStructure(IndentedWriter out, ResultSet rs) throws IOException, SQLException{
		out.println("<fieldtypes>");
		out.indentLevel ++;
		for (int i=1; i<=rs.getMetaData().getColumnCount(); i++){
			out.println("<fieldtype name=\"" + rs.getMetaData().getColumnName(i) + "\" javasqltype=\"" + rs.getMetaData().getColumnType(i) + "\" />");
		}
		out.indentLevel--;
		out.println("</fieldtypes>");
	}
}
