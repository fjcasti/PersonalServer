package com.jsantos.ps.objects.mail.client;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IMAPNestedMessage;

public class MessageHandlerDebugMessageStructure {
	
	public void saveMessage(IMAPMessage msg, Long parentKey) throws MessagingException, IOException{
		//msg.writeTo(System.out);
		System.out.println(indent(parentKey) + "==================================================================");
		System.out.println(indent(parentKey) + "messageID: " + msg.getMessageID());
		System.out.println(indent(parentKey) + "Subject: " + msg.getSubject());
		//System.out.println(msg.getHeader(name, delimiter))
		
		//showMessageInfo(msg);
		
		MailParts mailParts = new MailParts();
		categorizeParts(msg, mailParts);
		
		
		if (null != mailParts.textPart) System.out.println(indent(parentKey) + "text part not null");
		if (null != mailParts.htmlPart) System.out.println(indent(parentKey) + "html part not null");
		
		for (Part p:mailParts.fileAttachments) System.out.println(indent(parentKey) + "file attachment: " + p.getFileName());
		for (Part p:mailParts.mailAttachments)  {
			System.out.println(indent(parentKey) + "mail attachment: " + p.getFileName());
			if (p.getContent() instanceof IMAPNestedMessage){
				IMAPNestedMessage nestedMessage = (IMAPNestedMessage)p.getContent();
				saveMessage(nestedMessage, 5l);
			}
		}
		/*
		System.out.println("Text:");
		System.out.println(mailParts.textPart.getContent());
		System.out.println("------------------------------------------------------------------");
		System.out.println("Html:");
		System.out.println(mailParts.htmlPart.getContent());
		*/
		System.out.println(indent(parentKey) + "==================================================================");
		
	}
	
	String indent(Long l){
		if (null !=l) return "     ";
		else return "";
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
	
}
