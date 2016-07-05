package com.jsantos.ps.store;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;
import java.util.UUID;

import com.jsantos.ps.webapp.CTX;

public class DataFolder {
	
	

	public static String findDataFolderPath(){
		//String dataFolderPath = CTX.getRealPath("/") + "../../../psdata/data_folder/";
		//String dataFolderPath = "Datafolder not set";
		
		//if (Environment.isDebug()) dataFolderPath = System.getProperty("user.home")+"/deleteme/database_from_mediabox/data_folder/";
		//return dataFolderPath;
	
		return getDataFolderPath();
	}
	
	public static String getDataFolderPath(){
		return CTX.getCfg().getUser_home() + "/"+CTX.getCfg().getData_dir()+"/";
		
	}
	public static String getFilesFolderPath(){
		return System.getProperty("user.home") + "/"+CTX.getCfg().getData_dir()+"/"+CTX.getCfg().getPath_files()+"/";
	}
	
	public static String getBackupFolderPath(){
		
		Properties properties = new Properties();
		String backupPath = null;
		try {
						
			File configFile = new File(getDataFolderPath()+ "config.properties");
			properties.load(new FileInputStream(configFile));
		    backupPath = properties.getProperty("backup_path");
		    if (null ==backupPath || 0==backupPath.length()) backupPath = System.getProperty("user.home") + "/"+CTX.getCfg().getData_dir()+"/"+CTX.getCfg().getPath_backups()+"/";
			   
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return backupPath;

	}

	public static String getConfigProperty(String key){
		
		Properties properties = new Properties();
		String value = null;
		try {
						
			File configFile = new File(getDataFolderPath()+ "config.properties");
			properties.load(new FileInputStream(configFile));
		    value = properties.getProperty(key);
			   
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return value;

	}
	
	
	
	public static boolean checkConfigProperties(){
		
		return new File(getDataFolderPath()+"/config.properties").exists();
	}
	
	public static boolean checkDataFolderPath(){
		System.out.println(getDataFolderPath());
		return new File(getDataFolderPath()).exists();
		
	
		
	}
	
	public static boolean createDataFolder(){
		Boolean sw = true;
		String uh = System.getProperty("user.home");
		sw = new File(uh+"/"+CTX.getCfg().getData_dir()).mkdir();
		sw = new File(uh+"/"+CTX.getCfg().getData_dir()+"/"+CTX.getCfg().getPath_files()).mkdir();
		sw = new File(uh+"/"+CTX.getCfg().getData_dir()+"/"+CTX.getCfg().getPath_backups()).mkdir();   
		return sw;
		
	}
	
	public static void createConfigProperties(String user, String pass){
				
		Properties properties = new Properties();
		try {
		
			properties.setProperty("password",pass);
			properties.setProperty("user",user);
			properties.setProperty("backup_path",System.getProperty("user.home")+"/"+CTX.getCfg().getData_dir()+"/"+CTX.getCfg().getPath_backups()+"/");
			
			File configFile = new File(System.getProperty("user.home")+"/"+CTX.getCfg().getData_dir()+"/"+"config.properties");
			properties.store(new FileWriter(configFile), null);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

	public static void addPropertyToConfig(String key, String value){
		Properties properties = new Properties();
		
		try {
			File configFile = new File(getDataFolderPath()+ "config.properties");
			properties.load(new FileInputStream(configFile));
			properties.setProperty(key, value);
			properties.store(new FileWriter(configFile),null);
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static boolean checkPathPermisions(String path){
		
		
		String filename = UUID.randomUUID().toString();
		try {
			File pathFile = new File(path);
			if(!pathFile.exists()){
				if(!pathFile.mkdir()) {return false;}
			}	
			
			File file = new File(path + filename);
			if(!file.createNewFile()){return false;}
			Writer  output = new BufferedWriter(new FileWriter(file));
			output.write("test");
			output.close();
			if(!file.delete()){return false;}
			return true;
						
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		return false;
	}
	
	
	
}//fin de clase
