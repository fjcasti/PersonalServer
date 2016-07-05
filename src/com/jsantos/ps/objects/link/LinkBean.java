package com.jsantos.ps.objects.link;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import com.jsantos.ps.store.database.DS;
import com.jsantos.ps.store.database.Sequence;
import com.jsantos.ps.webapp.MainFilter;

public class LinkBean {
	String linkKey = null;
	String fromPk = null;
	String toPk = null;
	String verb = null;
	int flags = 0;

	public static String buildLinkFromObjectListRecord(String objectkey, String description){
		StringBuffer link = new StringBuffer("<a href=\"");
		link.append(MainFilter.OID_MARKER);
		link.append(objectkey);
		link.append("\">");
		link.append(description);
		link.append("</a>");
		
		return link.toString();
	}

	public static String buildUrlFromObjectListRecord(String objectkey){
		StringBuffer url = new StringBuffer(MainFilter.OID_MARKER);
		url.append(objectkey);
		return url.toString();
	}
	
	

	public static boolean linkExists(String fromPk, String toPk, Connection conn) throws SQLException{
		boolean retValue = false;
		String sql = "select count(*) from link where frompk=? and toPk=?";
		PreparedStatement pst = conn.prepareStatement(sql);
		pst.setString(1, fromPk);
		pst.setString(2, toPk);
		ResultSet rs = pst.executeQuery();
		rs.next();
		if (0<rs.getInt(1)) retValue = true;
		return retValue;
	}
	
	
	public static boolean linkExists(String fromPk, String toPk) throws SQLException{
		Connection conn = DS.getConnection();
		try{
			return linkExists(fromPk, toPk);
		}
		finally{
			conn.close();
		}
	}
	
	public static void removeLink(String fromPk, String toPk) throws SQLException{
		Connection conn = DS.getConnection();
		try{
			String sql = "delete from link where frompk=? and toPk=?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, fromPk);
			pst.setString(2, toPk);
			pst.execute();
		}
		finally{
			conn.close();
		}
		
	}

	static boolean targetExists(String topk, Connection conn) throws SQLException{
		boolean retValue = false;
		PreparedStatement pst = conn.prepareStatement("select * from objectlist where objectkey=?");
		pst.setString(1,topk);
		ResultSet rs = pst.executeQuery();
		if (rs.next()) retValue = true;
		pst.close();
		rs.close();
		return retValue;
	}
	
	public void save(Connection conn) throws SQLException{
		if (!targetExists(toPk, conn)) throw new SQLException("Link target doesn't exist");
		if (!linkExists(fromPk, toPk, conn)){
			linkKey = UUID.randomUUID().toString();
			String sql = "insert into link (linkkey, frompk, topk, verb, created, flags) values (?,?,?,?,?,?)";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, linkKey);
			pst.setString(2, fromPk);
			pst.setString(3, toPk);
			pst.setString(4, verb);
			pst.setTimestamp(5, new Timestamp(new java.util.Date().getTime()));
			pst.setInt(6, flags);
			pst.execute();
		}
	}

	public String getFromPk() {
		return fromPk;
	}

	public void setFromPk(String fromPk) {
		this.fromPk = fromPk;
	}

	public String getToPk() {
		return toPk;
	}

	public void setToPk(String toPk) {
		this.toPk = toPk;
	}

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}

	public String getLinkKey() {
		return linkKey;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}
	
	
	
}
