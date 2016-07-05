package com.jsantos.ps.objects.file;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import org.zkoss.zul.Messagebox;

import com.jsantos.ps.store.DataFolder;
import com.jsantos.ps.store.database.DS;
import com.jsantos.ps.timeline.TimeLineAction;
import com.jsantos.ps.timeline.TimeLineBean;


public class FileRefBean {
	private String objectKey= null;
	private String fileName = null;
	private Date created = null;
	private String mimeType = null;
	private String md5 = null;
	private int version = 0;
	
	public boolean findByKey(String objectKey) throws SQLException{
		boolean found = false;
		Connection conn = DS.getConnection();
		try{
			String sql = "select r.filename, obj.created, d.mimetype, d.md5, obj.version from fileref r, filedata d, objectlist obj where r.filedatakey=d.filedatakey and obj.objectkey=r.objectkey and  r.objectkey=?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, objectKey);
			ResultSet rs = pst.executeQuery();
			if (rs.next()){
				found = true;
				this.objectKey = objectKey;
				fileName = rs.getString("filename");
				created = rs.getDate("created");
				mimeType = rs.getString("mimetype");
				md5 = rs.getString("md5").trim();
				version = rs.getInt("version");
			}
		}
		finally{
			conn.close();
		}
		return found;
	}

	public boolean findByFilename(String filename) throws SQLException{
		boolean found = false;
		Connection conn = DS.getConnection();
		try{
			String sql = "select r.filename from fileref r where r.filename=?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, filename);
			ResultSet rs = pst.executeQuery();
			if (rs.next()){
				found = true;
			}
		}
		finally{
			conn.close();
		}
		return found;
		
		
	}

	public String save(Connection conn) throws SQLException{
		
		
		/*if(findByFilename(this.fileName)){
			int resp = 0;
			try {
				resp = Messagebox.show("there is already a file named '"+this.fileName+"', you want to overwrite it or save it under another name?", null,
						   Messagebox.YES+Messagebox.NO+Messagebox.CANCEL, Messagebox.QUESTION);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{}
		return "";
		
				
		if (resp == 0){
		*/
		
		String fileDataKey = null;
		PreparedStatement pst = conn.prepareStatement("select * from filedata where md5=?");
		pst.setString(1, md5);
		ResultSet rs = pst.executeQuery();
		if (rs.next()){
			fileDataKey = rs.getString("filedatakey");
			pst.close();
		}
		else{
			pst.close();
			fileDataKey = UUID.randomUUID().toString();
			pst = conn.prepareStatement("insert into filedata (filedatakey, md5, mimetype) values (?,?,?)");
			pst.setString(1, fileDataKey);
			pst.setString(2, md5);
			pst.setString(3, mimeType);
			pst.execute();
			pst.close();
		}
		objectKey = UUID.randomUUID().toString();
		pst = conn.prepareStatement("insert into fileref (objectkey, filename, filedatakey) values (?,?,?)");
		pst.setString(1, objectKey);
		pst.setString(2, fileName);
		pst.setString(3, fileDataKey);
		pst.execute();
		pst.close();

		pst = conn.prepareStatement("insert into objectlist (objectkey, version, objecttype, CREATED, LASTMODIFIED, LASTSHOWN, description, htmlsrc, textcontent) values (?,?,'fileref',?,?,?,?,null,?)");
		pst.setString(1, objectKey);
		pst.setInt(2, version);
		pst.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
		pst.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));
		pst.setTimestamp(5, new Timestamp(new java.util.Date().getTime()));
		pst.setString(6, fileName);
		pst.setString(7, fileName);
		pst.execute();
		pst.close();
		
		
		TimeLineBean.insertEntry(objectKey, TimeLineAction.CREATE, conn);
		return objectKey;
		//}else{return "";}*/
			
	}
	
	
	public boolean delete() throws SQLException{
		boolean sw = false;
		Connection conn = DS.getConnection();
		conn.setAutoCommit(false);
		
		try{
			
			String sql = "select r.filedatakey,d.md5 from fileref r, filedata d, objectlist obj where r.filedatakey=d.filedatakey and obj.objectkey=r.objectkey and  r.objectkey=?";
			PreparedStatement consulta = conn.prepareStatement(sql);
			consulta.setString(1, objectKey);
			ResultSet rs = consulta.executeQuery();
			if (rs.next()){
							
				PreparedStatement pst = conn.prepareStatement("delete from objectlist where objectkey = ? ");
				pst.setString(1, objectKey);
				pst.execute();
				pst = conn.prepareStatement("delete from objecthistory where objectkey = ? ");
				pst.setString(1, objectKey);
				pst.execute();
				pst = conn.prepareStatement("delete from link where topk = ? ");
				pst.setString(1, objectKey);
				pst.execute();
				pst = conn.prepareStatement("delete from fileref where objectkey = ? ");
				pst.setString(1, objectKey);
				pst.execute();
				pst = conn.prepareStatement("delete from timeline where objectkey = ? ");
				pst.setString(1, objectKey);
				pst.execute();
				pst = conn.prepareStatement("delete from filedata where filedatakey = ? ");
				pst.setString(1, rs.getString("filedatakey"));
				pst.execute();
							
				conn.commit();
				
				File fichero = new File(DataFolder.getFilesFolderPath()+rs.getString("md5").trim());
				sw = fichero.delete();
				
				
				
			}//fin rx.next
		}
		catch (SQLException sqle){
			conn.rollback();
			throw sqle;
		}
		finally{
			conn.setAutoCommit(true);
			
		
			sw = true;
			conn.close();
			
			System.out.println("delete: "+objectKey);
		}
		return sw;
	}
	
	
	
	public boolean rename(String newName) throws SQLException{
		
		if(this.getObjectKey() != null){
			Connection conn = DS.getConnection();
			conn.setAutoCommit(false);
			
			try{
								
					PreparedStatement pst = conn.prepareStatement("update fileref  set filename = ? where objectkey = ?");
					pst.setString(1, newName);
					pst.setString(2, objectKey);
					pst.execute();
					pst = conn.prepareStatement("update objectlist  set description = ?, textcontent  = ? where objectkey = ?");
					pst.setString(1, newName);
					pst.setString(2, newName);
					pst.setString(3, objectKey);
					pst.execute();
					
					
					
					conn.commit();
					return true;
										
			}
			catch (SQLException sqle){
				conn.rollback();
				throw sqle;
			}
			finally{
				conn.setAutoCommit(true);
				conn.close();
				
				System.out.println("rename: "+objectKey);
			}
			
			
			
			
			
			
			
		}
		
		return false;
	}
	
	
	
	
	//getters and setters
	
	public String getObjectKey() {
		return objectKey;
	}

	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getCreated() {
		return created;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getMd5() {
		return md5;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	
}
