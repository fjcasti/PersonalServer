package com.jsantos.ps.webapp;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jsantos.ps.mainwindow.LoginWindow;
import com.jsantos.ps.objects.ObjectsHelper;
import com.jsantos.ps.objects.tag.TagBean;
import com.jsantos.ps.objects.wiki.WikiPageBean;
import com.jsantos.ps.store.DataFolder;
import com.jsantos.ps.store.database.DS;
import com.jsantos.util.logger.Logger;

public class MainFilter implements Filter{
	public static final String OID_MARKER = "ps_oid__";
	private static final String MODULE = MainFilter.class.getSimpleName();
	public void destroy() {;}
	
	public void init(FilterConfig config) throws ServletException {;}

	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {

		//System.out.println(((HttpServletRequest)request).getRequestURI());
		//showRequest((HttpServletRequest)request);
		boolean forwarded = false;		
		String uri =((HttpServletRequest) request).getRequestURI();
			
		if (!forwarded) forwarded = forwardLogin(uri, (HttpServletRequest)request, (HttpServletResponse)response);
		if (!forwarded) forwarded = forwardObjectViewer(uri, (HttpServletRequest)request, (HttpServletResponse)response);
        
        if (!forwarded) chain.doFilter(request, response);
        
        return;
    }

	
	
	boolean forwardLogin(String uri, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		Logger.logInfo(MODULE, "Method Called :forwardLogin() ");
		try{
			if (!uri.contains("nlogin.zul")) {
				if (uri.contains(".zul") || uri.contains(".jsp") || uri.contains(OID_MARKER)){
					if (null == request.getSession().getAttribute("USER") || null == request.getSession().getAttribute("USER_DATASOURCE")){
						Cookie authCookie = LoginWindow.getAuthCookie(request);
						if (null != authCookie && LoginWindow.validateCookie(authCookie)){
							Logger.logInfo(MODULE, " NEW USER ");
							request.getSession().setAttribute("USER",LoginWindow.getUserNameFromCookie(authCookie));
							//request.getSession().setAttribute("USER_STORE_PATH", DataFolder.getDataFolderPath());
							return false;
						}
						else if (uri.contains(OID_MARKER)){
							Logger.logInfo(MODULE, " OID_MARKER ");
							String oid = findOidFromUrlString(uri);
							if (ObjectsHelper.isPublic(oid)) return false;
							else{
								Logger.logInfo(MODULE, " GOTO LOGIN ");
								dispatchUrl("/nlogin.zul", request, response);
								return true;
							}
						}
						else{
							dispatchUrl("/nlogin.zul", request, response);
							return true;
						}
					}
				}
			}
			return false;
		}
		catch(SQLException e) {throw new ServletException(e);}
		catch(NoSuchAlgorithmException e){throw new ServletException(e);}
	}
	
	
	
	public static String findOidFromUrlString(String href){
		Logger.logInfo(MODULE, "Method Called : findOidFromUrlString()");
		Logger.logInfo(MODULE, "href : "+href);
		File file = new File(href);
		return file.getName().substring(MainFilter.OID_MARKER.length(), file.getName().length()); 
	}
	
	boolean forwardObjectViewer(String uri, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		Logger.logInfo(MODULE, "Method Called : forwardObjectViewer()");
		if(!( uri.endsWith(".js") || uri.endsWith(".jpg") || uri.endsWith(".jpeg") || uri.endsWith(".gif") || uri.endsWith(".css") || uri.endsWith(".png"))){
			Logger.logInfo(MODULE, "Method Called : forwardObjectViewer() first If()");
			if (uri.startsWith(request.getContextPath() + "/" + OID_MARKER)){
				Logger.logInfo(MODULE, "Method Called : forwardObjectViewer() second If()");
				String oid = findOidFromUrlString(uri);
				//String oid = uri.substring(("/personalserver/" + OID_MARKER).length(), uri.length());
				String finalUrl = "/imp/mainwindow.zul?oid=" + oid; 
				if (null != request.getHeader("user-agent") && request.getHeader("user-agent").contains("Android")){
					finalUrl = "/imp/mainwindowmobile.zul?oid=" + oid; 
				}
				Logger.logInfo(MODULE, "Method Called : forwardObjectViewer() dispatching");
				dispatchUrl(finalUrl, request, response);
				return true;
			}
		}
		Logger.logInfo(MODULE, "Method Called : forwardObjectViewer() outside of if");
		return false;
	}
	
	void dispatchUrl(String finalUrl, HttpServletRequest request, ServletResponse response) throws ServletException, IOException{
		Logger.logInfo(MODULE, "Method Called : dispatchUrl()");
		request.getRequestDispatcher(finalUrl).forward(request, response);
	}
	
	void showRequest(HttpServletRequest request){
		if (((HttpServletRequest)request).getRequestURI().endsWith(".gif") || ((HttpServletRequest)request).getRequestURI().endsWith(".jpg")) return;
		System.out.println("==========================================================================");
		System.out.println("getAuthType: " + request.getAuthType());
		System.out.println("getCharacterEncoding: " + request.getCharacterEncoding());
		System.out.println("getContentLength: " + request.getContentLength());
		System.out.println("getContentType: " + request.getContentType());
		System.out.println("getContextPath: " + request.getContextPath());
//		System.out.println("getDateHeader: " + request.getDateHeader(arg0));
//		System.out.println("getHeader: " + request.getHeader(arg0));
//		System.out.println("getIntHeader: " + request.getIntHeader(arg0));
		System.out.println("getLocalAddr: " + request.getLocalAddr());
		System.out.println("getLocalName: " + request.getLocalName());
		System.out.println("getLocalPort: " + request.getLocalPort());
		System.out.println("getMethod: " + request.getMethod());
//		System.out.println("getParameter: " + request.getParameter(arg0));
		System.out.println("getPathInfo: " + request.getPathInfo());
		System.out.println("getPathTranslated: " + request.getPathTranslated());
		System.out.println("getProtocol: " + request.getProtocol());
		System.out.println("getQueryString: " + request.getQueryString());
//		System.out.println("getRealPath: " + request.getRealPath(arg0));
		System.out.println("getRemoteAddr: " + request.getRemoteAddr());
		System.out.println("getRemoteHost: " + request.getRemoteHost());
		System.out.println("getRemotePort: " + request.getRemotePort());
		System.out.println("getRemoteUser: " + request.getRemoteUser());
		System.out.println("getRequestedSessionId: " + request.getRequestedSessionId());
		System.out.println("getRequestURI: " + request.getRequestURI());
		System.out.println("getScheme: " + request.getScheme());
		System.out.println("getServerName: " + request.getServerName());
		System.out.println("getServerPort: " + request.getServerPort());
		System.out.println("getServletPath: " + request.getServletPath());
		System.out.println("request: " + request.toString());
		System.out.println("==========================================================================");
	}
	
	 
}
