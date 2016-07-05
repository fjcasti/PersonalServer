package com.jsantos.ps.objects.mail.client;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;

import org.zkoss.zul.Messagebox;

import com.jsantos.ps.store.DataFolder;
import com.jsantos.ps.webapp.CTX;
import com.sun.mail.imap.IMAPMessage;

public class IMAPClient {
    String mbox = "INBOX";
    int maxMsg = 50;

	public static void main(String[] args) throws Exception{
		new IMAPClient().getMail();
	}

	public void getMail() {
		// fix for error thrown by javamail com.sun.mail.util.DecodingException: BASE64Decoder: Error in encoded stream: needed at least 2 valid base64 characters, but only got 0 before padding character (=)
		System.getProperties().setProperty("mail.mime.base64.ignoreerrors", "true");
		
		// fix for javamail not decoding filenames from thunderbird
		System.getProperties().setProperty("mail.mime.decodetext.strict", "false");
		System.getProperties().setProperty("mail.mime.decodefilename", "true");
		
		
		
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
		
			
		try {
		
			if(null != CTX.getCfg().mailaccount){
			
			Date startDate = dateFormat.parse("01022011");
		
		
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");
		
		
		
			Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore("imaps");
			//store.connect("imap.gmail.com", "ps.controlled@gmail.com", "LetPirilonIn");
			
			//store.connect("imap.gmail.com", "javier.idcglobal@gmail.com", "LetPirilonIn");
			
			
			String email_server = CTX.getCfg().mailaccount.getMailServer();
			if (email_server == null) {
				Messagebox.show("no email server configured");
				return;
			}

			String email_username = CTX.getCfg().mailaccount.getUserName();
			if (email_username == null){
				Messagebox.show("no email username configured");
				return;
			}

			String email_password = CTX.getCfg().mailaccount.getUserPass();
//			if (email_password == null) throw new MessagingException("No configured email password");
			store.connect(email_server, email_username, email_password);
			

			Folder folder = store.getDefaultFolder();
			if (folder == null) throw new MessagingException("No default folder");
			//folder = folder.getFolder(mbox);
			folder = folder.getFolder("ebay");
			if (folder == null) throw new MessagingException("Invalid folder");

			folder.open(Folder.READ_WRITE);
			int totalMessages = folder.getMessageCount();
			
			System.out.println("totalMessages: " + totalMessages);
			
			//Message[] msgs = folder.search(new ReceivedDateTerm(ComparisonTerm.GT, startDate));
			Message[] msgs = folder.getMessages();
			
			//for (int i=0; i<msgs.length; i++){
			for (int i=0; i<maxMsg; i++){
				
				//if (i!=2) continue;
				
				new MessageHandlerDebugMessageStructure().saveMessage((IMAPMessage)msgs[i], null);
				new MessageHandler().saveMessage(session, (IMAPMessage)msgs[i]);
			}

		
		}//fin del if del CTX.getCfg().mailaccount
		else
		{ Messagebox.show("no email configured, check preferences");}	
			
			
			
			
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			//System.exit(1);
		} catch (AuthenticationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
			//System.exit(2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

/*	
	void saveAttachmentss(){
		if (saveAttachments && level != 0 && !p.isMimeType("multipart/*")) {
			String disp = p.getDisposition();
			// many mailers don't include a Content-Disposition
			if (disp == null || disp.equalsIgnoreCase(Part.ATTACHMENT)) {
				if (filename == null)
					filename = "Attachment" + attnum++;
				pr("Saving attachment to file " + filename);
				try {
					File f = new File(filename);
					if (f.exists())
						// XXX - could try a series of names
						throw new IOException("file exists");
				} catch (IOException ex) {
					pr("Failed to save attachment: " + ex);
				}
				pr("---------------------------");
			}
		}
	}
*/

	//part text = (String)p.getContent();
	
	void showMessageInfo(IMAPMessage msg) throws MessagingException{
		 System.out.println("From: " + InternetAddress.toString(msg.getFrom()));
	      System.out.println("Reply-to: " + InternetAddress.toString(msg.getReplyTo()));
	      System.out.println("To: " + InternetAddress.toString(msg.getRecipients(Message.RecipientType.TO)));
	      System.out.println("Cc: " + InternetAddress.toString(msg.getRecipients(Message.RecipientType.CC)));
	      System.out.println("Bcc: " + InternetAddress.toString(msg.getRecipients(Message.RecipientType.BCC)));
	      System.out.println("Subject: " + msg.getSubject());

	      System.out.println("Sent: " + msg.getSentDate());
	      System.out.println("Received: " + msg.getReceivedDate());

	      if (msg.isSet(Flags.Flag.DELETED)) {
	        System.out.println("Deleted");
	      }
	      if (msg.isSet(Flags.Flag.ANSWERED)) {
	        System.out.println("Answered");
	      }
	      if (msg.isSet(Flags.Flag.DRAFT)) {
	        System.out.println("Draft");
	      }
	      if (msg.isSet(Flags.Flag.FLAGGED)) {
	        System.out.println("Marked");
	      }
	      if (msg.isSet(Flags.Flag.RECENT)) {
	        System.out.println("Recent");
	      }
	      if (msg.isSet(Flags.Flag.SEEN)) {
	        System.out.println("Read");
	      }
	      if (msg.isSet(Flags.Flag.USER)) {
	        // We don't know what the user flags might be in advance
	        // so they're returned as an array of strings
	        String[] userFlags = msg.getFlags().getUserFlags();
	        for (int j = 0; j < userFlags.length; j++) {
	          System.out.println("User flag: " + userFlags[j]);
	        }
	      }		
	}
	
}
