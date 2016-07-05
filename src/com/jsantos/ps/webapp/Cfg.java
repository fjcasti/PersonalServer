package com.jsantos.ps.webapp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.jsantos.ps.clipboard.Clipboard;
import com.jsantos.ps.objects.mail.MailAccountBean;
import com.jsantos.util.FileUtils;
import com.jsantos.util.XmlUtils;

public class Cfg {
	Document doc = null;
	String path_files = null;
	String path_backups = null;
	String data_dir = null;
	String user_home = null;
	public Clipboard clipboard = null;
	public MailAccountBean mailaccount = null;
	
	
	public Cfg(){
		path_files = "files";
		path_backups = "backups";
		data_dir = ".psdata";
		user_home = System.getProperty("user.home");
		clipboard = new Clipboard();
		
	}
	
	
	
	public void loadMailAccount(){
		
		
		try {
			this.mailaccount = MailAccountBean.findAccountByName("gmail");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public boolean loadConfig()  {
		try{
			doc = XmlUtils.buildXmlDoc(new FileInputStream("personalserver_config.xml"));
			return true;
		}
		catch(FileNotFoundException e){
			//doc = XmlUtils.buildXmlDoc(FileUtils.loadFile("personalserver_config.xml", Cfg.class));
			//System.getProperty("user.home");
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
		return false;
	}
	
	void saveConfig() throws IOException{
		FileWriter out = new FileWriter("~/.personalserver_config.xml");
		out.write(XmlUtils.writeXml(doc));
	}

	public Vector<String> getStoreList() throws XPathExpressionException{
		Vector<String> retValue = new Vector<String>();
		NodeList nodeList = XmlUtils.runXPath(doc, "/config/stores/store");
		for (int i=0; i<nodeList.getLength(); i++) {
			retValue.add(((Element)nodeList.item(i)).getAttribute("path"));
		}
		return retValue;
	}
	
	
	
	
	
	
	
	
	
	
	//getters and setters
	
	public String getPath_files() {
		return path_files;
	}

	public void setPath_files(String path_files) {
		this.path_files = path_files;
	}

	public String getPath_backups() {
		return path_backups;
	}

	public void setPath_backups(String path_backups) {
		this.path_backups = path_backups;
	}

	public String getData_dir() {
		return data_dir;
	}

	public void setData_dir(String data_dir) {
		this.data_dir = data_dir;
	}
	
	public String getUser_home() {
		return user_home;
	}

	public void setUser_home(String user_name) {
		this.user_home = user_name;
	}
	
}
