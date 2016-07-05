package com.jsantos.util.logger;

public interface ILogger {

    public void error(String strMessage);
    
    public void debug(String strMessage);
    
    public void info(String strMessage);
    
    public void warn(String strMessage);

    public void fatal(String strMessage);
    
    public void trace(String strMessage);
 
    
}
