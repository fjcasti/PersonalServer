package com.jsantos.util.logger;

 

public class Logger implements ILogger{
   

    public static void logError(String strModule, String strMessage) {        
        System.out.println("[ "+ strModule +" ] : " + strMessage);
    }
    public static void logDebug(String strModule, String strMessage){
    	System.out.println("[ "+ strModule +" ] : " + strMessage);
    }
    public static void logInfo(String strModule, String strMessage){
    	System.out.println("[ "+ strModule +" ] : " + strMessage);
    }
    public static void logWarn(String strModule, String strMessage){
    	System.out.println("[ "+ strModule +" ] : "+ strMessage);
    }
    public static void logFatal(String strModule, String strMessage){
    	System.out.println("[ "+ strModule +" ] : "+ strMessage);
    }

    public static void logTrace(String strModule,String strMessage){
    	System.out.println("[ "+ strModule +" ] : "+ strMessage);
    }

    public static void logError(String strModule, Object strMessage) {        
        System.out.println("[ "+ strModule +" ] : " + strMessage);
    }
    public static void logDebug(String strModule, Object strMessage){
    	System.out.println("[ "+ strModule +" ] : " + strMessage);
    }
    public static void logInfo(String strModule, Object strMessage){
    	System.out.println("[ "+ strModule +" ] : " + strMessage);
    }
    public static void logWarn(String strModule, Object strMessage){
    	System.out.println("[ "+ strModule +" ] : "+ strMessage);
    }
    public static void logFatal(String strModule, Object strMessage){
    	System.out.println("[ "+ strModule +" ] : "+ strMessage);
    }

    public static void logTrace(String strModule,Object strMessage){
    	System.out.println("[ "+ strModule +" ] : "+ strMessage);
    }

    public static void setLogger(ILogger newlogger) {
    }

    public static ILogger getLogger(){
        return new Logger();
    }
	@Override
	public void error(String strMessage) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void debug(String strMessage) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void info(String strMessage) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void warn(String strMessage) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void fatal(String strMessage) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void trace(String strMessage) {
		// TODO Auto-generated method stub
		
	}
}
