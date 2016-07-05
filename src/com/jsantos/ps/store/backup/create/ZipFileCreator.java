package com.jsantos.ps.store.backup.create;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Progressmeter;
import org.zkoss.zul.Window;

import com.jsantos.ps.store.DataFolder;
import com.jsantos.ps.webapp.CTX;
import com.jsantos.ps.webapp.GeneralProgressBarController;
import com.jsantos.ps.webapp.SessionHelper;

public class ZipFileCreator implements Runnable{
	
	private long totalSize;
	private long writedSize;
	private String backupFolderPath;
    private String user;
    //private final GeneralProgressBarController pgb;
    //private final Desktop desktop;
	

	public static void main(String[] args) throws Exception{
		ZipFileCreator zfc = new ZipFileCreator(DataFolder.getBackupFolderPath(),  System.getProperty("user.name"),null);
		zfc.createBackup();
		System.out.println("and zipped done");
	}
	
	public ZipFileCreator(String backupFolderPath_,String user_,Component pgb_){
		this.backupFolderPath = backupFolderPath_;
		this.user = user_;
		//this.desktop = pgb_.getDesktop();
		//this.pgb =  (GeneralProgressBarController) pgb_;
		
		
		
	}
	
	

	public void createBackup(){
		try {
			createBackup (backupFolderPath,user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void createBackup(String storeDir, String userName) throws Exception{
		
		String currentDateString = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
		String databaseBackupFileName = "database_backup_" + currentDateString + ".xml";
		String psdata = CTX.getCfg().getData_dir();
		String filesDir = CTX.getCfg().getPath_files();
		String filesStoreDir = DataFolder.getFilesFolderPath();
		totalSize = 0;
		writedSize = 0;
		
		
		
		
		
		DatabaseBackup databaseBackup = new DatabaseBackup();
		databaseBackup.createBackup(storeDir  + databaseBackupFileName);
		String backupFileName = currentDateString +"__"+ userName +"__backup"+".zip";
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(storeDir + backupFileName));
        
       
        totalSize += new File(storeDir  +  databaseBackupFileName).length();
        totalSize += new File(DataFolder.getDataFolderPath() + "/" + "config.properties").length();
        String[] dataFiles = new File(filesStoreDir).list();
        for (int i=0; i<dataFiles.length; i++)
    	{
        	totalSize += new File(filesStoreDir + dataFiles[i]).length();	
    	
    	}
    
        
        
        addFile(storeDir  +  databaseBackupFileName,psdata+"/"+ databaseBackupFileName, out);
        addFile(DataFolder.getDataFolderPath() + "/" + "config.properties",psdata+"/"+ "config.properties", out);
               
        
        if(dataFiles != null){
        
        for (int i=0; i<dataFiles.length; i++)
        	{
        	   	addFile(filesStoreDir + dataFiles[i], psdata + "/" + filesDir + "/" + dataFiles[i], out);
        	//System.out.println(dataFiles[i]);
        	}
        }
        
        new File(storeDir  +  databaseBackupFileName).delete();
        out.close();
      
	}
	
	void addFile(String filePath, String fileName, ZipOutputStream out) throws IOException{
        
		if(new File(filePath).isDirectory()) return;
		FileInputStream in = new FileInputStream(filePath);
        out.putNextEntry(new ZipEntry(fileName));
		byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
        	//if(null != pgb) actualizeProgress(len);
            out.write(buf, 0, len);
        }
        in.close();
	}

	private void actualizeProgress(int len) {
		writedSize += len;
		int cant = Math.round((writedSize *100)/totalSize);
		//pgb.setValue(cant);
		if(cant == 100){
			//pgb.detach();
		}
	}

	@Override
	public void run() {
		try {
			//Executions.activate(desktop);
			createBackup();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			//Executions.deactivate(desktop);
		}
		
	}
	
	public String getBackupFolderPath() {
		return backupFolderPath;
	}

	public void setBackupFolderPath(String backupFolderPath) {
		this.backupFolderPath = backupFolderPath;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}
