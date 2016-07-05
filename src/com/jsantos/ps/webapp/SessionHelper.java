package com.jsantos.ps.webapp;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;

import com.jsantos.ps.mainwindow.LoginWindow;

public class SessionHelper {

	public static String getUser(){
		return (String)Sessions.getCurrent().getAttribute("USER");
	}
	
	public static String getUserStorePath() throws SQLException{
		return (String)Sessions.getCurrent().getAttribute("USER_STORE_PATH");
	}
	
	public static String getUserDatasource(){
		return (String)Sessions.getCurrent().getAttribute("USER_DATASOURCE");
	}
	
	public static void logOut(){
		invalidateCookie();
		Sessions.getCurrent().setAttribute("USER", null);
		Sessions.getCurrent().invalidate();
		Executions.sendRedirect("nlogin.zul");
	}
	
	public static boolean isVerifiedUser()  {
		
		//if(null == getUser() || null == getUserDatasource()){ //a completar
			if(null == getUser() ){
			
				return false;
			
		}
		return true;
	}
	
	public static void invalidateCookie(){
		
		Cookie cookie = new Cookie("PSAUTH", null + ":" + null);
		cookie.setMaxAge(0);
		((HttpServletResponse) Executions.getCurrent().getNativeResponse()).addCookie(cookie);
		
	}
	
}//fin de clase
