package com.jsantos.ps.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.jsantos.util.XmlUtils;

public class StoreConfig {

	String storePath = null;
	Document doc = null;
	
	public StoreConfig(String storePath) throws FileNotFoundException, XPathExpressionException, SAXException, IOException, ParserConfigurationException{
		this.storePath = storePath;
		loadConfig();
	}
	
	void loadConfig() throws FileNotFoundException, SAXException, IOException, ParserConfigurationException, XPathExpressionException{
		File file = new File(storePath + "store_config.xml");
		if (!file.exists()) System.out.println("File: " + file.getCanonicalPath() + " not found!");
		else doc = XmlUtils.buildXmlDoc(new FileInputStream(storePath + "store_config.xml"));
	}

	void saveConfig() throws IOException{
		FileWriter out = new FileWriter(storePath + "store_config.xml");
		out.write(XmlUtils.writeXml(doc));
	}

	
	public String getStoreName() throws XPathExpressionException {
		return XmlUtils.getFirstMatchingElement(doc, "/store/name").getFirstChild().getNodeValue();
	}
	
	public String getUserName() throws XPathExpressionException {
		return XmlUtils.getFirstMatchingElement(doc, "/store/username").getFirstChild().getNodeValue();
	}

	public String getPassword() throws XPathExpressionException {
		return XmlUtils.getFirstMatchingElement(doc, "/store/password").getFirstChild().getNodeValue();
	}
}
