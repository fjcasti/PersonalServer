package com.jsantos.ps.clipboard;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Messagebox;

import com.jsantos.ps.store.DataFolder;
import com.jsantos.ps.webapp.CTX;
import com.jsantos.ps.webapp.MainFilter;

public class Clipboard {
	
	public static final String TYPE_FILE = "type_file";
	public static final String TYPE_TEXT = "type_text";
	
	String dataType;
	String uuid;
	String text;
	Media file;
	Boolean isPublic = false;
	
	
	public void setText(String cad){text = cad;dataType = this.TYPE_TEXT; file = null;}
	public void setFile(Media _media){file = _media;dataType = this.TYPE_FILE;text = null;}
	public String getDataType(){return dataType;}
	public Media getFile(){return file;}
	public String getText(){return text;}
	public void setPublic(Boolean sw){isPublic = sw;}
	public Boolean isPublic(){return isPublic;}
	
	public static void onUpload(org.zkoss.util.media.Media[] media) throws InterruptedException {
		if (media != null) {
			try {
				if (media != null) {
					for (int i = 0; i < media.length; i++) {
						byte[] uploadedFile = org.apache.commons.io.IOUtils.toByteArray(media[i].getStreamData());
						//System.out.println(md5);
						OutputStream out = new FileOutputStream(CTX.getCfg().getPath_files() + media[i].getName());
						out.write(uploadedFile);
						out.close();
						CTX.getCfg().clipboard.setFile(media[i]);
						Messagebox.show("upload succesfully!");
						
					}
				}
			} catch (Exception e) {
				//showError("ERROR " + e.getMessage());
			}
		}
	}

}
