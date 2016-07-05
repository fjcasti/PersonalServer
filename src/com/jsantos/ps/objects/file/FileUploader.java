package com.jsantos.ps.objects.file;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.SQLException;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Messagebox;

import com.jsantos.ps.store.DataFolder;
import com.jsantos.ps.store.database.DS;
import com.jsantos.ps.webapp.CTX;
import com.jsantos.ps.webapp.MainFilter;
import com.jsantos.ps.webapp.SessionHelper;

public class FileUploader{

	public static void onUpload(org.zkoss.util.media.Media[] media) throws InterruptedException {
		if (media != null) {
			try {
				if (media != null) {
					for (int i = 0; i < media.length; i++) {
						byte[] uploadedFile = org.apache.commons.io.IOUtils.toByteArray(media[i].getStreamData());
						MessageDigest md = MessageDigest.getInstance("MD5");
						String md5 = new BigInteger(1,md.digest(uploadedFile)).toString(16);
						//System.out.println(md5);
						OutputStream out = new FileOutputStream(DataFolder.getFilesFolderPath() + md5);
						out.write(uploadedFile);
						out.close();
						String fileRefKey = updateDatabase(md5, media[i].getContentType(), media[i].getName());
						Messagebox.show("upload succesfully!");
						Executions.getCurrent().sendRedirect(MainFilter.OID_MARKER + fileRefKey);
					}
				}
			} catch (Exception e) {
				showError("ERROR " + e.getMessage());
			}
		}
	}

	public static String updateDatabase(String md5, String contentType, String fileName) throws SQLException{
		FileRefBean fileRefBean = new FileRefBean();
		fileRefBean.setMd5(md5);
		fileRefBean.setMimeType(contentType);
		fileRefBean.setFileName(fileName);
		Connection conn = DS.getConnection();
		try{
			fileRefBean.save(conn);
		}
		finally{
			conn.close();
		}
		return fileRefBean.getObjectKey();
	}

	static void showError(String mensagem) {
		try {
			Messagebox.show(mensagem, "XXX", Messagebox.OK, Messagebox.ERROR);
		}catch(Exception e){
		}
	}

}
