package com.jsantos.ps.mainwindow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.xpath.XPathExpressionException;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;

import com.jsantos.ps.store.DataFolder;
import com.jsantos.util.logger.Logger;

public class LoginWindow {
	private static final String MODULE = LoginWindow.class.getSimpleName();
	public static boolean checkLogin(HttpServletRequest request, HttpServletResponse response) throws XPathExpressionException, IOException, SQLException, NoSuchAlgorithmException{
		Logger.logInfo(MODULE, " Method Called : checkLogin(req,res)");
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		
		
		return checkLogin(userName,password);

	}
	
	public static boolean checkLogin(String userName, String password) throws XPathExpressionException, IOException, SQLException, NoSuchAlgorithmException{
		
		Logger.logInfo(MODULE, " Method Called :checkLogin(user,pwd) ");
		if (validatePassword(userName, password)){
			initializeSession(userName);
			Cookie cookie = new Cookie("PSAUTH", userName + ":" + hashMD5("password"));
			cookie.setMaxAge(1296000);
			((HttpServletResponse) Executions.getCurrent().getNativeResponse()).addCookie(cookie);
			if (Executions.getCurrent().getHeader("user-agent").contains("Android")) Executions.sendRedirect("mobilesearch.zul");
			else Executions.sendRedirect("ps_oid__1");
		}
		else {
			Sessions.getCurrent().invalidate();
			Executions.sendRedirect("nlogin.zul?bad_password=true");
		}
		
		return true;

	}
	
	
	
	//mantenemos esta funcion por compatibilidad
	static boolean validatePassword(HttpServletRequest request, String userName, String password){
		Logger.logInfo(MODULE, " Method Called :validatePassword(req,res) ");
		return validatePassword(userName,password);
	}
	
	

	public static boolean validatePassword(String userName, String password){
		Logger.logInfo(MODULE, " Method Called :validatePassword(req,user,pwd) ");
		Properties properties = new Properties();
		try {
			String dataFolderPath = DataFolder.findDataFolderPath();
			System.out.println("dataFolderPath //------> :"+dataFolderPath);
			File dataFolder = new File(dataFolderPath);
			if (!dataFolder.exists()) return false;
			String configFilePath = dataFolderPath + "config.properties";
			System.out.println("configFilePath //------> :"+configFilePath);
			File configFile = new File(configFilePath);
			if (!configFile.exists()) return false;
		    properties.load(new FileInputStream(configFile));
		    String storedPassword = properties.getProperty("password");
		    System.out.println("storedPassword //------> :"+storedPassword);
		    if (null == storedPassword) return false;
		    return storedPassword.equals(password);
		} catch (IOException e) {
		}
		return false;
	}

	/*
	static String findDataFolderPath(HttpServletRequest request, String userName){
		String dataFolderPath = request.getSession().getServletContext().getRealPath("/") + "../../../psdata/" + userName + "/";
		if (Environment.isDebug()) dataFolderPath = "/home/jsantos/deleteme/database_from_mediabox/" + userName + "/";
		return dataFolderPath;
		
	}
	*/
	
	public static void initializeSession(String userName) throws SQLException, IOException{
		Logger.logInfo(MODULE, " Method Called :initializeSession() ");
		System.out.println("username: " +userName);
		System.out.println(Sessions.getCount());
		Sessions.getCurrent(true).setAttribute("USER", userName);
		Sessions.getCurrent().setAttribute("USER_STORE_PATH", DataFolder.findDataFolderPath()+"/files/");
		/*
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUsername("sa");
		//dataSource.setPassword();

		String dataFolderPath = findDataFolderPath(request, userName);
			
		//String filePath = request.getSession().getServletContext().getRealPath("/") + "../../database.h2.db";

		new File(dataFolderPath).mkdirs();


		//String filePath = request.getSession().getServletContext().getRealPath("/") + "WEB-INF/database.h2.db";

		String databaseFilePath = dataFolderPath + "database.h2.db";

		System.out.println("Database filepath: " + databaseFilePath);
		dataSource.setUrl("jdbc:h2:" + new File(databaseFilePath).getCanonicalPath());

		checkDbContents(dataSource, request);

		request.getSession().setAttribute("USER_DATASOURCE", dataSource);
		request.getSession().setAttribute("USER", userName);
		request.getSession().setAttribute("USER_STORE_PATH", dataFolderPath + "files/");
		
		new File(dataFolderPath + "files/").mkdirs();
		*/
	}
	

	/*
	static void checkDbContents(DataSource datasource, HttpServletRequest request) throws SQLException, IOException{
		boolean retValue = false;
		Connection conn = datasource.getConnection();
		try{
			Statement st = conn.createStatement();
			st.executeQuery("select * from OBJECTLIST");
		}
		catch (SQLException sqle){
			//try to create the database structure
			File scriptFile = new File(request.getSession().getServletContext().getRealPath("/") + "WEB-INF/create_tables.sql");
			ScriptRunner.runScript(scriptFile, datasource);
		}
		finally{
			conn.close();
		}
	}
	*/

	public static Cookie getAuthCookie(HttpServletRequest request){
		Logger.logInfo(MODULE, " Method Called :getAuthCookie() ");
		Cookie[] cookies = request.getCookies();
		if (null == cookies) return null;
		for (Cookie cookie:cookies) if (cookie.getName().equals("PSAUTH")) return cookie;
		return null;
		
	}
	
	public static String getUserNameFromCookie(Cookie cookie){
		if (!cookie.getValue().contains(":")) throw new RuntimeException("Cookie must contain user name!");
		return cookie.getValue().split(":")[0];
	}
	
	public static boolean validateCookie(Cookie cookie) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		if (null == cookie.getValue()) return false;
		if (!cookie.getValue().contains(":"))return false;
		String[] userpass= cookie.getValue().split(":");
		if (2 != userpass.length) return false;
		String userName = userpass[0];
		String hashedPassword = userpass[1];
		if (hashedPassword.equals(hashMD5("password"))) return true;
		return false;
	}

	private static String convertToHex(byte[] data) { 
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) { 
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do { 
				if ((0 <= halfbyte) && (halfbyte <= 9)) 
					buf.append((char) ('0' + halfbyte));
				else 
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while(two_halfs++ < 1);
		} 
		return buf.toString();
	} 

	public static String hashMD5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException  { 
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		byte[] md5hash = new byte[32];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		md5hash = md.digest();
		return convertToHex(md5hash);
	} 	

}
