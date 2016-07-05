package com.jsantos.ps.webapp;

import java.util.Hashtable;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import javax.xml.xpath.XPathExpressionException;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import com.jsantos.ps.store.StoreConfig;


public class WebApp implements ServletContextListener{
	//public static String STORE_PATH = "/home/jsantos/store/"; //TODO: make this configurable.
	private static Hashtable<String, BasicDataSource> dataSources = null;
	public static Cfg adminSettings = null;
	public static Hashtable<String, StoreConfig> stores = null;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) { //set things to null so they are recovered by the gc
		dataSources = null;
		adminSettings = null;
		stores = null;
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		/*try{
			//System.out.println("context initialized, creating link to store");
			//String newPath = event.getServletContext().getRealPath("store");
			//String cmd = "ln -s " + STORE_PATH + " " + newPath;
			//System.out.println(cmd);
			//Runtime.getRuntime().exec(cmd).waitFor();
			
			dataSources= new Hashtable<String, BasicDataSource>();		

			adminSettings = new Cfg();
			adminSettings.loadConfig();
			stores = new Hashtable<String, StoreConfig>();
			for (String path:adminSettings.getStoreList()){
				System.out.println("Registering store at: " + path);
				stores.put(path, new StoreConfig(path));
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		*/
	}
	
	public static DataSource getDataSourceForStore(String storePath) throws XPathExpressionException{
		DataSource retValue  = dataSources.get(storePath); 
		if (null != retValue) return retValue;
		else{
			StoreConfig storeConfig = stores.get(storePath);
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName("org.postgresql.Driver");
			dataSource.setUsername(storeConfig.getUserName());
			dataSource.setPassword(storeConfig.getPassword());
			dataSource.setUrl("jdbc:postgresql://localhost/" + storeConfig.getUserName());
			return  dataSource;
		}
	}
	
}
