package com.jsantos.ps.objects.wiki;

import java.io.File;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.habitsoft.xhtml.dtds.XhtmlEntityResolver;
import com.jsantos.ps.objects.link.LinkBean;
import com.jsantos.ps.webapp.MainFilter;
import com.jsantos.util.XmlUtils;
import com.jsantos.util.logger.Logger;

public class XHtmlParser {
	private static final String MODULE = XHtmlParser.class.getSimpleName();
	Vector<LinkBean>links = new Vector<LinkBean>();
	
	
	public String cleanUpHtml(String htmlkey, String htmlSrc, Connection conn) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, SQLException{
		Logger.logInfo(MODULE, " Method Called :doAfterCompose() ");
		System.out.println("original htmlsrc :"+htmlSrc);

		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html>" + htmlSrc + "</html>";
		
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		documentFactory.setNamespaceAware(false); 
		DocumentBuilder builder = documentFactory.newDocumentBuilder();
		builder.setEntityResolver(new XhtmlEntityResolver());

		InputSource is = new InputSource(new StringReader(xml));
		Document doc = builder.parse(is);
		
		NodeList nodes = XmlUtils.runXPath(doc, "//a");
	    for (int i = 0; i < nodes.getLength(); i++) {
	        String href = nodes.item(i).getAttributes().getNamedItem("href").getFirstChild().getNodeValue();
	        
	        if (isInternalLink(href)){ 
	        	System.out.println("Internal link: " + href);
	        	LinkBean linkBean = new LinkBean();
	        	linkBean.setFromPk(htmlkey);
	        	linkBean.setToPk(MainFilter.findOidFromUrlString(href));
	        	linkBean.setVerb("htmllink");
	        	linkBean.setFlags(0);
	        	links.add(linkBean);
	        	Element element = (Element)nodes.item(i);
	        	element.setAttribute("href", normalizeLink(href));
	        }
	    }
	    return XmlUtils.writeXml(XmlUtils.createFragment(doc, doc.getDocumentElement()));
	}

	public void saveLinks(String htmlkey, Connection conn) throws SQLException{
		markExistingLinks(htmlkey, conn);
		for(LinkBean linkBean:links){
			System.out.println("saving link: from " + linkBean.getFromPk() + " to " + linkBean.getToPk());
			linkBean.save(conn);
		}
		removeUnmarkedLinks(htmlkey, conn);
	}
	
	String normalizeLink(String href){
		File file = new File(href);
		return file.getName();
	}
	
	boolean isInternalLink(String href){
		if (href.contains(MainFilter.OID_MARKER)) return true;
		return false;
	}
	
	void markExistingLinks(String htmlkey, Connection conn) throws SQLException{
		PreparedStatement st = conn.prepareStatement("update link set flags=1 where frompk=?");
		st.setString(1, htmlkey);
		st.execute();
		st.close();
	}

	
	void removeUnmarkedLinks(String htmlkey, Connection conn) throws SQLException{
		PreparedStatement st = conn.prepareStatement("delete from link where flags=1 and frompk=?");
		st.setString(1, htmlkey);
		st.execute();
		st.close();
	}
	
	public class XHTMLEntityResolver implements EntityResolver {

		  private Hashtable entities = new Hashtable();
		  
		  // fill the list of URLs
		  public XHTMLEntityResolver() {
		    
		    // The XHTML 1.0 DTDs
		    this.addMapping("-//W3C//DTD XHTML 1.0 Strict//EN",
		     "http://www.cafeconleche.org/DTD/xhtml1-strict.dtd");
		    this.addMapping("-//W3C//DTD XHTML 1.0 Transitional//EN",
		     "http://www.cafeconleche.org/DTD/xhtml1-transitional.dtd");
		    this.addMapping("-//W3C//DTD XHTML 1.0 Frameset//EN",
		     "http://www.cafeconleche.org/DTD/xhtml1-frameset.dtd");

		    // The XHTML 1.0 entity sets
		    this.addMapping("-//W3C//ENTITIES Latin 1 for XHTML//EN",
		     "http://www.cafeconleche.org/DTD/xhtml-lat1.ent");
		    this.addMapping("-//W3C//ENTITIES Symbols for XHTML//EN",
		     "http://www.cafeconleche.org/DTD/xhtml-symbol.ent");
		    this.addMapping("-//W3C//ENTITIES Special for XHTML//EN",
		     "http://www.cafeconleche.org/DTD/xhtml-special.ent");
		   
		  }

		  private void addMapping(String publicID, String URL) {
		    entities.put(publicID, URL);
		  }
		  
		  public InputSource resolveEntity(String publicID, 
		   String systemID) throws SAXException {
		     
		    if (entities.contains(publicID)) {
		      String url = (String) entities.get(publicID);
		      InputSource local = new InputSource(url);
		      return local;
		    }
		    else return null;
		    
		  }
		    
		}
	
}
