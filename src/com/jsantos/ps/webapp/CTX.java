package com.jsantos.ps.webapp;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

//import org.quartz.SchedulerException;

import com.jsantos.ps.objects.mail.MailAccountBean;
import com.jsantos.ps.objects.mail.client.IMAPClient;
import com.jsantos.ps.scheduler.PsScheduler;
import com.jsantos.ps.store.DataFolder;
import com.jsantos.ps.store.database.DS;

public class CTX implements ServletContextListener{
	private static ServletContext servletContext;
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		servletContext = sce.getServletContext();
		try {
			
				setAttribute("CONFIG",new Cfg());
				setAttribute("DATASOURCE", DS.createDataSource(servletContext));
				System.out.println("Checking database...");
				DS.getConnection().close();
				System.out.println("database is available.");
				//PsScheduler.start();
				
				this.getCfg().loadMailAccount();
				
				
				
				
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//		} catch (SchedulerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
		
	public static void setServletContext(ServletContext servletContext) {
		if(CTX.servletContext != null)
			throw new RuntimeException("ServletContext is already set. Cannot be set again");
		CTX.servletContext = servletContext;
	}

	public static void setAttribute(String key, Object value){
		servletContext.setAttribute(key, value);
	}
	
	public static Object getAttribute(String key){
		return servletContext.getAttribute(key);
		
	}
	
	public static DataSource getDataSource(){
		return (DataSource)servletContext.getAttribute("DATASOURCE");
	}
	
	public static Cfg getCfg(){
		return (Cfg)servletContext.getAttribute("CONFIG");
		
	}
	
	public static String getRealPath(String uri){
		return servletContext.getRealPath(uri);
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public static void checkMail(){
		IMAPClient imapClient = new IMAPClient();
		imapClient.getMail();
	}
	
}
