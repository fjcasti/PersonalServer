package com.jsantos.ps.store.backup.restore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.jsantos.ps.store.DataFolder;
import com.jsantos.ps.webapp.CTX;

public class ZipFileReader {
	public static void main(String[] args) throws FileNotFoundException, SQLException, SAXException, IOException, ParserConfigurationException {
		new ZipFileReader().unzip("/home/jsantos/store/backup_personalserver2009-10-05_00-55-04.zip", "/home/jsantos/store/", "personalserver");
	}
	
	/*public void unzip(String zipNamePath, String storeDir, String userName) throws FileNotFoundException, SQLException, SAXException, IOException, ParserConfigurationException{
		new File(storeDir + "tmp/" + userName + "/").mkdir();
		String tmpUserStoreDir = storeDir + "tmp/" + userName + "/";
		new File(tmpUserStoreDir).mkdirs();
		
		String databaseBackupFile = null;
		try {
			FileInputStream fis = new FileInputStream(zipNamePath);
			
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				int size;
				byte[] buffer = new byte[2048];

				if (entry.getName().startsWith("database_backup") && entry.getName().endsWith(".xml")) databaseBackupFile = tmpUserStoreDir + entry.getName();
				System.out.println(tmpUserStoreDir + entry.getName());
				FileOutputStream fos = new FileOutputStream(tmpUserStoreDir + entry.getName());
				BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length);
				while ((size = zis.read(buffer, 0, buffer.length)) != -1) bos.write(buffer, 0, size);
				bos.flush();
				bos.close();
			}
			zis.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*if (null != databaseBackupFile){
			new DatabaseBackupRestorer().restoreDatabase(databaseBackupFile);
			File userStore = new File(storeDir + userName);
			if (userStore.exists()) deleteDirectory(userStore);
			new File(tmpUserStoreDir).renameTo(userStore);
			System.out.println("backup restored successfully!");
		}
		else{
			System.err.println("Database backupFile not found, stopping recovery!");
		}
	}*/
	
	public void unzip(String zipNamePath, String storeDir, String userName) {
		
		Enumeration entries;
	    ZipFile zipFile;
	    String tmpUserStoreDir = storeDir + "tmp/" + userName + "/";
		new File(tmpUserStoreDir).mkdirs();
		String databaseBackupFile = null;
	    
		
	    try {
	        zipFile = new ZipFile(zipNamePath);
	        entries = zipFile.entries();
	       //listEntries(zipFile.entries());
		    
	              
	        while(entries.hasMoreElements()) {
	          ZipEntry entry = (ZipEntry)entries.nextElement();
	        
	          if (entry.getName().contains("database_backup") && entry.getName().endsWith(".xml")) databaseBackupFile = tmpUserStoreDir + entry.getName();
	         
	         String dir = entry.getName().substring(0, entry.getName().lastIndexOf("/"));
	         new File(tmpUserStoreDir + dir).mkdirs();
	    
	        
	                
	          copyInputStream(zipFile.getInputStream(entry),
	             new BufferedOutputStream(new FileOutputStream(tmpUserStoreDir + entry.getName())));
	        
	        }

	        zipFile.close();
	      } catch (IOException ioe) {
	        System.err.println("Unhandled exception:");
	        ioe.printStackTrace();
	        return;
	      }
		  
	    restoreData(tmpUserStoreDir,databaseBackupFile);
		
		
	}

	public boolean deleteDirectory(File path) {
		if( path.exists() ) {
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					deleteDirectory(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		}
		return( path.delete() );
	}
	
	public static final void copyInputStream(InputStream in, OutputStream out)
			  throws IOException
			  {
			    byte[] buffer = new byte[1024];
			    int len;

			    while((len = in.read(buffer)) >= 0)
			      out.write(buffer, 0, len);

			    in.close();
			    out.close();
			  }
	
	public void restoreData(String tmpUserStoreDir,String databaseBackupFile){
		
		 if (null != databaseBackupFile){
				try {
					
					new DatabaseBackupRestorer().restoreDatabase(databaseBackupFile);
					File userStore = new File(DataFolder.getFilesFolderPath());
					if (userStore.exists()) deleteDirectory(userStore);
					new File(tmpUserStoreDir + CTX.getCfg().getData_dir() +"/"+ CTX.getCfg().getPath_files()).renameTo(userStore);
					System.out.println( "move " + tmpUserStoreDir + CTX.getCfg().getData_dir() + CTX.getCfg().getPath_files() +" to "+DataFolder.getFilesFolderPath());
					deleteDirectory(new File(tmpUserStoreDir));
					System.out.println("backup restored successfully!");
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else{
				System.err.println("Database backupFile not found, stopping recovery!");
			}
		
		
	}
	
}	
