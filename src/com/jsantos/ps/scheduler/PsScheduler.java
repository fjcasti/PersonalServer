package com.jsantos.ps.scheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.jsantos.ps.store.database.DS;


public class PsScheduler {
	
		
	static private org.quartz.Scheduler scheduler;
		
	 static public void start() throws org.quartz.SchedulerException, SQLException {
		 
		 Connection conn = DS.getConnection();
		 
		  try{
			 scheduler = StdSchedulerFactory.getDefaultScheduler(); 
			 PreparedStatement pst = conn.prepareStatement("select * from joblist");
			 ResultSet rs = pst.executeQuery();
			
		
			 
			 while (rs.next()) {
				 //System.out.println(rs.getString("jobclass"));
				
			JobDetail jobDetail = JobBuilder
					.newJob(PsScheduler.getJobClass(rs.getString("jobclass")))
			        .withIdentity(rs.getString("jobname"), rs.getString("jobgroupname"))
			        .build();
			
            
	         CronTrigger trigger = TriggerBuilder.newTrigger()
	                .withIdentity(rs.getString("triggername"), rs.getString("triggergroupname"))
	                .withSchedule(CronScheduleBuilder.cronSchedule(rs.getString("cronstring")))
	                .build(); 
	                
	         
	         scheduler.scheduleJob(jobDetail, trigger);
	         
	         
			 }
	            
	         scheduler.start();
			 
			 } catch (Exception ex)
		  	
			 { }
		  finally{
	        
				conn.close();
			 }	
	 }
	 static public void stop(){  
	        try {  
	            scheduler.shutdown();  
	        } catch (Exception ex) {  
	            // Nada  
	        }             
	    }
	 
	 static public Class<Job> getJobClass(String jobclass){
		 
		 Class a = null;
		 jobclass = "com.jsantos.ps.scheduler."+jobclass;
		try {
			a = PsScheduler.class.getClassLoader().loadClass(jobclass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return a;	 
	 }

}
