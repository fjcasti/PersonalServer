package com.jsantos.ps.objects.mail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.jsantos.ps.objects.link.LinkBean;
import com.jsantos.ps.objects.mail.client.MailParts;
import com.jsantos.ps.store.database.DS;
import com.jsantos.ps.store.database.Sequence;
import com.jsantos.ps.timeline.TimeLineAction;
import com.jsantos.ps.timeline.TimeLineBean;

public class MailBean {
	String objectkey = null;
	String parentObjectKey = null;
	String messageId = null;
	String from = null;
	String to = null;
	String cc = null;
	String bcc = null;
	String subject = null;
	String fullHeaders = null;
	String textContent = null;
	String htmlsrc = null;
	int	   version = 0; 

	public MailBean(){
		
	}
	
	public MailBean(MimeMessage msg, MailParts mailParts) throws MessagingException, IOException{
		messageId = msg.getMessageID();
		from = InternetAddress.toString(msg.getFrom());
		to = InternetAddress.toString(msg.getRecipients(Message.RecipientType.TO));
		cc = InternetAddress.toString(msg.getRecipients(Message.RecipientType.CC));
		bcc = InternetAddress.toString(msg.getRecipients(Message.RecipientType.BCC));
		subject = msg.getSubject();
		//fullHeaders = msg.getAllHeaderLines();
		if (null !=mailParts.textPart && 0<mailParts.textPart.getSize()) textContent = (String)mailParts.textPart.getContent();
		if (null !=mailParts.htmlPart && 0<mailParts.htmlPart.getSize()) htmlsrc = (String)mailParts.htmlPart.getContent();
		
	}
	
	
	public static boolean existsMessageId(String messageId) throws SQLException{
		boolean retValue = false;
		Connection conn = DS.getConnection();
		try{
			String sql = "select * from mail where messageid=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, messageId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) retValue = true;
		}
		finally{
			conn.close();
		}
		return retValue;
	}
	
	public void loadFromDatabase(String objectkey) throws SQLException{
		Connection conn = DS.getConnection();
		try{
			String sql = "select * from mail m, objectlist o where m.objectkey=o.objectkey and o.objectkey=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, objectkey);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				messageId = rs.getString("messageid");
				from = rs.getString("fromaddress");
				to = rs.getString("toaddress");
				cc = rs.getString("cc");
				bcc = rs.getString("bcc");
				subject = rs.getString("description");
				fullHeaders = rs.getString("fullheaders");
				textContent = rs.getString("textcontent");
				htmlsrc = rs.getString("htmlsrc");
				version = rs.getInt("version");
			}
		}
		finally{
			conn.close();
		}
	}
	
	
	public void save(Connection conn) throws SQLException{
		objectkey = UUID.randomUUID().toString();
		{
			String description  = subject;
			if (null == description || 0 == description.length()) description = "No Subject";
			
			PreparedStatement pst = conn.prepareStatement("insert into objectlist (objectkey, objecttype, CREATED, LASTMODIFIED, LASTSHOWN, description, htmlsrc, textcontent,version) values (?,'mail',?,?,?,?,?,?,?)");
			objectkey = UUID.randomUUID().toString();
			pst.setString(1, objectkey);
			pst.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
			pst.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
			pst.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));
			pst.setString(5, description);
			pst.setString(6, htmlsrc);
			pst.setString(7, textContent);
			pst.setInt(8, version);
			pst.execute();
			pst.close();
			
		}
		{
			PreparedStatement pst = conn.prepareStatement("insert into mail (objectkey, messageid, fromaddress, toaddress, cc, bcc, fullheaders) values (?,?,?,?,?,?,?)");
			pst.setString(1, objectkey);
			pst.setString(2, messageId);
			pst.setString(3, from);
			pst.setString(4, to);
			pst.setString(5, cc);
			pst.setString(6, bcc);
			pst.setString(7, fullHeaders);
			pst.execute();
			pst.close();
		}
		if (null != parentObjectKey){
			LinkBean linkBean = new LinkBean();
			
			linkBean.setFromPk(parentObjectKey);
			linkBean.setToPk(objectkey);
			linkBean.setVerb("attachment");
			linkBean.save(conn);
			
		}
		TimeLineBean.insertEntry(objectkey, TimeLineAction.CREATE, conn);
	}

	
	
	public String getParentObjectKey() {
		return parentObjectKey;
	}



	public void setParentObjectKey(String parentObjectKey) {
		this.parentObjectKey = parentObjectKey;
	}



	public String getObjectkey() {
		return objectkey;
	}
	public void setObjectkey(String objectkey) {
		this.objectkey = objectkey;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}
	public String getBcc() {
		return bcc;
	}
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getFullHeaders() {
		return fullHeaders;
	}
	public void setFullHeaders(String fullHeaders) {
		this.fullHeaders = fullHeaders;
	}
	public String getTextContent() {
		return textContent;
	}
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}
	public String getHtmlsrc() {
		return htmlsrc;
	}
	public void setHtmlsrc(String htmlsrc) {
		this.htmlsrc = htmlsrc;
	}


}
