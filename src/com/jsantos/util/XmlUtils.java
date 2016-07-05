package com.jsantos.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class XmlUtils {
	
	public static Document buildXmlDoc(String xml) throws SAXException, IOException, ParserConfigurationException{
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		documentFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = documentFactory.newDocumentBuilder();
		return builder.parse(new StringBufferInputStream(xml));
		
	}

	public static Document buildXmlDoc(InputStream in) throws SAXException, IOException, ParserConfigurationException{
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		documentFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = documentFactory.newDocumentBuilder();
		return builder.parse(in);
	}
	
	public static Element getFirstMatchingElement(Document doc, String xPath) throws XPathExpressionException{
		NodeList nodeList = runXPath(doc, xPath);
		if (0<nodeList.getLength()) return (Element)nodeList.item(0);
		return null;
	}
	
	public static NodeList runXPath(String xml, String sXPath) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException{
		Document doc = buildXmlDoc(xml);
		return runXPath(doc, sXPath);
	}
	
	public static NodeList runXPath(Document doc, String sXPath) throws XPathExpressionException{
		XPathFactory xpathFactory = XPathFactory.newInstance();
	    XPath xpath = xpathFactory.newXPath();
	    XPathExpression expr = xpath.compile(sXPath);

	    Object result = expr.evaluate(doc, XPathConstants.NODESET);
	    NodeList nodes = (NodeList) result;
	    return nodes;
	}
	
	public static String writeXml(Document doc) {
		//OutputFormat outputFormat = new OutputFormat();
		//outputFormat.setDoctype(null, null);
		try{
			StringWriter buff = new StringWriter();
			XMLSerializer serializer = new XMLSerializer();
			serializer.setOutputCharStream(buff);
			//serializer.setOutputFormat(outputFormat);
			serializer.serialize(doc); 
			return buff.toString();
		}
		catch (IOException ioe){
			return ioe.toString();
		}
	}
	
	public static DocumentFragment createFragment(Document doc, Node fromNode){
		DocumentFragment docfrag = doc.createDocumentFragment();
		NodeList nodeList = fromNode.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++){
			docfrag.appendChild(nodeList.item(i).cloneNode(true));
		}
		return docfrag;
	}
	
	public static String writeXml(DocumentFragment docFrag) {
		try{
			OutputFormat outputFormat = new OutputFormat();
			outputFormat.setOmitXMLDeclaration(true);
			StringWriter buff = new StringWriter();
			XMLSerializer serializer = new XMLSerializer();
			serializer.setOutputFormat(outputFormat);
			serializer.setOutputCharStream(buff);
			serializer.serialize(docFrag); 
			return buff.toString();
		}
		catch (IOException ioe){
			return ioe.toString();
		}
	}
}
