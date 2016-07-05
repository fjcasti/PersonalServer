package com.raj.webservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Properties;

import javax.xml.xpath.XPathExpressionException;

import com.jsantos.ps.mainwindow.LoginWindow;
import com.jsantos.ps.store.DataFolder;
import com.jsantos.util.logger.Logger;

public class LoginWS implements ILogin{

 	private static final String CLASS = LoginWS.class.getSimpleName();

	@Override
	public boolean wsDoAuthentication(String username, String password) {
		Logger.logInfo(CLASS, " Method Called :wsDoAuthentication() ");
		Properties properties = new Properties();
		try {
			String dataFolderPath = DataFolder.findDataFolderPath();
			Logger.logInfo(CLASS, "dataFolderPath :"+dataFolderPath);
			File dataFolder = new File(dataFolderPath);
			if (!dataFolder.exists()) return false;
			String configFilePath = dataFolderPath + "config.properties";
			Logger.logInfo(CLASS, "configFilePath :"+configFilePath);
			File configFile = new File(configFilePath);
			if (!configFile.exists()) return false;
		    properties.load(new FileInputStream(configFile));
		    String storedPassword = properties.getProperty("password");
		    Logger.logInfo(CLASS, "storedPassword :"+storedPassword);
		    if (null == storedPassword) return false;
		    return storedPassword.equals(password);
		} catch (IOException e) {
		}
		return false;
	}
 
	@Override
	public String getGreetings() {
		return "Welcome to Personal Server.";
	}
}
