 
package com.jsantos.util.logger;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DefaultLogger implements ILogger {
    
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");

    public void error(String strMessage) {
        System.out.println("[ " + dateToString(new Date())+" ]" + " [ ERROR ] " + strMessage);
    }
    public void debug(String strMessage) {
        System.out.println("[ " + dateToString(new Date())+" ]" + " [ DEBUG ] " + strMessage);
    }
    public void info(String strMessage) {
        System.out.println("[ " + dateToString(new Date())+" ]" + " [ INFO ] " + strMessage);
    }
    public void warn(String strMessage) {
        System.out.println("[ " + dateToString(new Date())+" ]" + " [ WARN ] " + strMessage);
    }
    public void fatal(String strMessage) {
        System.out.println("[ " + dateToString(new Date())+" ]" + " [ FATAL ] " + strMessage);
    }
    public void trace(String strMessage) {
        System.out.println("[ " + dateToString(new Date())+" ]" + " [ TRACE ] " + strMessage);
    }

    protected String dateToString(Date date){
    	return sdf.format(date);
    }
    
}
