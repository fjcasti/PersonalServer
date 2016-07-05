package com.jsantos.ps.webapp;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Environment {
	public static boolean isDebug(){
		try{
	      Context initContext = new InitialContext();
	      Context env = (Context) initContext.lookup("java:/comp/env");
	      String debug = (String)env.lookup("debug");
	      //System.out.println("prod= " + prod);
	      return "true".equals(debug);
		}
	    catch (NamingException ne){
	    	return false;
	    }
	}

}
