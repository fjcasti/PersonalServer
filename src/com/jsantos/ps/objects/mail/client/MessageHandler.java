package com.jsantos.ps.objects.mail.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import com.jsantos.ps.objects.file.FileRefBean;
import com.jsantos.ps.objects.link.LinkBean;
import com.jsantos.ps.objects.mail.MailBean;
import com.jsantos.ps.objects.tag.TagBean;
import com.jsantos.ps.store.database.DS;
import com.jsantos.ps.webapp.SessionHelper;
import com.jsantos.ps.webapp.WebApp;
import com.jsantos.util.FileUtils;
import com.sun.mail.imap.IMAPMessage;

public class MessageHandler {
	
	public void saveMessage(Session session, IMAPMessage msg) throws Exception{
		MailParts mailParts = new MailParts();
		categorizeParts(msg, mailParts);
		
		Connection conn = DS.getConnection();
		conn.setAutoCommit(false);
		try{
			Vector<TmpFiles>allTmpFiles = new Vector<TmpFiles>();
			Vector<String> tags = getTags(msg);
			saveMessageRecursive(session, msg, null, conn, allTmpFiles, tags);
			
			//everything seems fine with the database and all files are on the disk. Now rename the files to the final location.
			System.out.println("renaming tmp files");
			for (TmpFiles tmpFiles:allTmpFiles){
				tmpFiles.msgTmpFile.file.renameTo(new File(SessionHelper.getUserStorePath() + tmpFiles.msgTmpFile.md5));
				for (TmpFile tmpFile:tmpFiles.attachmentTmpFiles){
					tmpFile.file.renameTo(new File(SessionHelper.getUserStorePath() + tmpFile.md5));
				}
				for (TmpFile tmpFile:tmpFiles.attachedMailTmpFiles){
					tmpFile.file.renameTo(new File(SessionHelper.getUserStorePath() + tmpFile.md5));
				}
			}
			conn.commit();
		}
		catch(Exception e){ //should anything at all happen, roll database back, lets forget about the files. There will be no references to them.
			conn.rollback(); //TODO:clean up the leftover files in tmp and on the real file store
			throw e;
		}
		finally{
			conn.close();
		}
	}

	Vector<String> getTags(IMAPMessage msg) throws MessagingException{
		Vector<String> tags = new Vector<String>();
		String[] tagsHeader = msg.getHeader("X-Tags");
		if (null!=tagsHeader){
			for (int i=0; i<tagsHeader.length;i++){
				StringTokenizer st = new StringTokenizer(tagsHeader[i], " ");
				while (st.hasMoreElements()) tags.add(st.nextToken());
			}
		}
		if (0<tags.size()) {
			System.out.print("Found Tags: ");
			for (String tag:tags) System.out.print("[" + tag + "]");
			System.out.println("");
		}
		return tags;
	}
	
	
	public void saveMessageRecursive(Session session, MimeMessage msg, String parentKey, Connection conn, Vector<TmpFiles> allTmpFiles, Vector<String> tags) throws MessagingException, IOException, SQLException{
		
		MailParts mailParts = new MailParts();
		categorizeParts(msg, mailParts);
		
		if (!MailBean.existsMessageId(msg.getMessageID())){
			TmpFiles tmpFiles = new TmpFiles();
			
			tmpFiles.addMsg(msg);
			MailBean emailBean = new MailBean(msg, mailParts);
			System.out.println("Saving message: " + msg.getSubject());
			emailBean.save(conn);
			
			for(String tag:tags)TagBean.addTagToObject(emailBean.getObjectkey(), tag, conn);
			
			if (null != parentKey){
				LinkBean linkBean = new LinkBean();
				linkBean.setFromPk(parentKey);
				linkBean.setToPk(emailBean.getObjectkey());
				linkBean.setVerb("attachment");
				linkBean.save(conn);
			}			

			for (Part p:mailParts.fileAttachments) {
				TmpFile tmpFile = tmpFiles.addAttachmentPart(p);
				FileRefBean fileRefBean = new FileRefBean();
				fileRefBean.setMd5(tmpFile.md5);
				fileRefBean.setMimeType(parseContentType(p.getContentType()));
				fileRefBean.setFileName(p.getFileName());
				if (null == p.getFileName()) System.out.println("Filename is null");
				System.out.println("Saving attachment: " + fileRefBean.getFileName());
				fileRefBean.save(conn);
				
				LinkBean linkBean = new LinkBean();
				linkBean.setFromPk(emailBean.getObjectkey());
				linkBean.setToPk(fileRefBean.getObjectKey());
				linkBean.setVerb("attachment");
				linkBean.save(conn);
				
				for(String tag:tags)TagBean.addTagToObject(fileRefBean.getObjectKey(), tag, conn);
				
			}

			for (Part p:mailParts.mailAttachments)  {
				TmpFile tmpFile = tmpFiles.addAttachmedEmailPart(p);
				//FileRefBean fileRefBean = new FileRefBean();
				//fileRefBean.setMd5(tmpFile.md5);
				//fileRefBean.setMimeType(p.getContentType());
				//fileRefBean.setFileName(p.getFileName());
				//System.out.println("Saving attached mail: " + p.getFileName());
				//fileRefBean.save(conn);

				InputStream source = new FileInputStream(tmpFile.file);
				MimeMessage message = new MimeMessage(session, source);
				saveMessageRecursive(session,message, emailBean.getObjectkey(), conn, allTmpFiles, tags);
			}
			allTmpFiles.add(tmpFiles);
		}
		else{
			System.out.println("Message not saved with messgeId: " + msg.getMessageID());
		}
	}

	String parseContentType(String contentType){
		System.out.println("Parsing contenttype: " + contentType);
		String retValue = null;
		if (null!=contentType){ 
			if (-1 == contentType.indexOf(";")) retValue = contentType;
			else retValue = contentType.substring(0, contentType.indexOf(';'));
		}
		System.out.println("Parsed contenttype: " + retValue);
		return retValue;
	}
	
	private void categorizeParts(Part p, MailParts mailParts) throws MessagingException, IOException {
		
		if (null == p.getFileName() && p.isMimeType("text/html")){
			mailParts.htmlPart = p;
		}
		else if (null == p.getFileName() && p.isMimeType("text/*")) {
			mailParts.textPart = p;
		}
		else if (p.isMimeType("multipart/alternative")) {
			Multipart mp = (Multipart)p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				Part bp = mp.getBodyPart(i);
				if (null == p.getFileName() && bp.isMimeType("text/plain")) if (null == mailParts.textPart) mailParts.textPart = bp;
				if (null == p.getFileName() && bp.isMimeType("text/html")) if (null == mailParts.htmlPart) mailParts.htmlPart = bp;
			}
		} 
		else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart)p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				categorizeParts(mp.getBodyPart(i), mailParts);
			}
		}
		else if (null != p.getFileName() && 0 < p.getFileName().length()){
			if (p.isMimeType("message/rfc822")){
				mailParts.mailAttachments.add(p);
			}
			else {
				mailParts.fileAttachments.add(p);
			}
		}
	}
	
	
	class TmpFiles{
		TmpFile msgTmpFile;
		Vector<TmpFile> attachmentTmpFiles = new Vector<TmpFile>();
		Vector<TmpFile> attachedMailTmpFiles = new Vector<TmpFile>();
		
		void addMsg(MimeMessage msg) throws IOException, MessagingException{
			msgTmpFile = new TmpFile(msg);
		}
		
		TmpFile addAttachmentPart(Part p) throws IOException, MessagingException{
			TmpFile tmpFile = new TmpFile((MimeBodyPart)p);
			attachedMailTmpFiles.add(tmpFile);
			return tmpFile;
		}

		TmpFile addAttachmedEmailPart(Part p) throws IOException, MessagingException{
			TmpFile tmpFile = new TmpFile((MimeBodyPart)p);
			attachedMailTmpFiles.add(tmpFile);
			return tmpFile;
		}
	}
	
	class TmpFile{
		File file;
		String md5;
		
		public TmpFile(MimeMessage msg) throws IOException, MessagingException{
			buildFileName();
			FileUtils.saveStream(file.getCanonicalPath(), msg.getRawInputStream(), true);
			md5 = FileUtils.calculateMD5Checksum(file);
		}
		
		public TmpFile(MimeBodyPart p) throws IOException, MessagingException{
			buildFileName();
			p.saveFile(file);
			md5 = FileUtils.calculateMD5Checksum(file);
		}
		
		void buildFileName(){
			String tmpFileName = System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID().toString();
			file = new File( tmpFileName);
		}
	}
	
	
}
