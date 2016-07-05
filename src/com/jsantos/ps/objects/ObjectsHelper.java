package com.jsantos.ps.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Messagebox;

import com.jsantos.ps.objects.file.FileRefBean;
import com.jsantos.ps.objects.tag.TagBean;
import com.jsantos.ps.objects.wiki.WikiPageBean;
import com.jsantos.ps.store.database.DS;

public class ObjectsHelper {
	
	
	
static public boolean deleteFromObjectKey(String objectKey) throws SQLException, InterruptedException{
	boolean sw = false;
	
		
		String objecttype = "";
		Connection conn = DS.getConnection();
		PreparedStatement st = conn.prepareStatement("select objecttype, description from objectlist where objectkey=?");
		st.setString(1, objectKey);
		ResultSet rs = st.executeQuery();
		if(rs.next()){
				objecttype = rs.getString("objecttype");		
				if(Messagebox.OK == Messagebox.show("Are you sure you want to delete the \""+rs.getString("description")+"\" node?",
						"Delete node",
						Messagebox.OK |  Messagebox.CANCEL,
						Messagebox.QUESTION))
				{
		
				if(objecttype.equals("html")){
					WikiPageBean tmpobj = new WikiPageBean();
					tmpobj.setObjectkey(objectKey);
					sw = tmpobj.delete();
				}
				else
				if(objecttype.equals("fileref")){
					FileRefBean tmpobj = new FileRefBean();
					tmpobj.setObjectKey(objectKey);
					sw = tmpobj.delete();
				}
				else
				if(objecttype.equals("tag")){
					TagBean tmpobj = new TagBean();
					tmpobj.setObjectKey(objectKey);
					sw = tmpobj.delete();
				}
			}//fin del msgbox
		}//fin del if del type
		conn.close();
		
	if(sw) {Messagebox.show("Deleting succesfully");}else{Messagebox.show("Error deleting!");}
	return sw;
}//fin de la funcion

public static boolean isPublic(String oid)throws SQLException{
	boolean retValue = false;
	Connection conn = DS.getConnection();
	try{
		String sql = "select textcontent  from objectlist "+ 
					 "where objectkey in ( "+
				     "select frompk from link where topk = ?) "+
				     "and objectlist.textcontent  in ('PUBLIC','PUBLIC_EDITABLE') ";
		PreparedStatement pst = conn.prepareStatement(sql);
		pst.setString(1, oid);
		ResultSet rs = pst.executeQuery();
		if (rs.next()) {
			retValue = true;
			System.out.println (oid + " is public ...");
		}
	}
	finally{
		conn.close();
	}
	return retValue;
}

public static boolean isPublicEditable(String oid)throws SQLException{
	boolean retValue = false;
	Connection conn = DS.getConnection();
	try{
		String sql = "select textcontent  from objectlist "+ 
					 "where objectkey in ( "+
				     "select frompk from link where topk = ?) "+
				     "and objectlist.textcontent = 'PUBLIC_EDITABLE' ";
		PreparedStatement pst = conn.prepareStatement(sql);
		pst.setString(1, oid);
		ResultSet rs = pst.executeQuery();
		if (rs.next()) {
			retValue = true;
			System.out.println (oid + " is public_editable ...");
		}
	}
	finally{
		conn.close();
	}
	return retValue;
}



}//fin de clase
