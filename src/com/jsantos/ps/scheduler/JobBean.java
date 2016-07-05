package com.jsantos.ps.scheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.quartz.SchedulerException;

import com.jsantos.ps.store.database.DS;

public class JobBean {
	
	String jobKey = null;
	String jobName = null;
	String jobGroupName = null;
	String triggerName = null;
	String triggerGroupName = null;
	String cronString = null;
	String jobClass = null;
	String cronHuman= null;
	
	
	
	public static JobBean findJobByName(String name) throws SQLException{
		JobBean retValue = null;
		Connection conn = DS.getConnection();
		try{
			String sql = "select * from joblist where jobname=?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, name);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				retValue = new JobBean();
				retValue.jobKey = rs.getString("jobkey");
				retValue.jobName = name ;
				retValue.jobGroupName = rs.getString("jobgroupname");
				retValue.triggerName = rs.getString("triggername");
				retValue.triggerGroupName = rs.getString("triggergroupname");
				retValue.cronString = rs.getString("cronstring");
				retValue.jobClass = rs.getString("jobclass");
				retValue.cronHuman = rs.getString("cronhuman");
			}
		}
		finally{
			conn.close();
		}
		return retValue;
	}

	static JobBean findTagByObjectKey(String key) throws SQLException{
		JobBean retValue = null;
		Connection conn = DS.getConnection();
		try{
			String sql = "select * from joblist where jobkey=?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, key);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				retValue = new JobBean();
				retValue.jobKey = rs.getString("jobkey");
				retValue.jobName = rs.getString("jobname");
				retValue.jobGroupName = rs.getString("jobGroupName");
				retValue.triggerName = rs.getString("triggerName");
				retValue.triggerGroupName = rs.getString("triggerGroupName");
				retValue.cronString = rs.getString("cronstring");
				retValue.jobClass = rs.getString("jobclass");
				retValue.cronHuman = rs.getString("cronhuman");
			}
		}
		finally{
			conn.close();
		}
		return retValue;
	}

	public void save() throws SQLException {
		
		Connection conn = DS.getConnection();
		conn.setAutoCommit(false);
		
		try{
			PreparedStatement pst = conn.prepareStatement("select count(*) from joblist where jobkey=?");
			pst.setString(1, jobKey);
			ResultSet rs = pst.executeQuery();
			
			if (rs.next()){ //update
					//System.out.println("update job " + jobName);			
												
				pst = conn.prepareStatement("update joblist set jobname=?, jobgroupname=?, triggername=?, triggergroupname=?,cronstring=?,jobclass=?,cronhuman=? where jobkey=?");
				pst.setString(1, jobName);
				pst.setString(2, jobGroupName);
				pst.setString(3, triggerName);
				pst.setString(4, triggerGroupName);
				pst.setString(5, cronString);
				pst.setString(6, jobClass);
				pst.setString(7, cronHuman);
				pst.setString(8, jobKey);
				
				
				pst.executeUpdate();
				
				pst.close();
								
			}
			else{ //create
				pst = conn.prepareStatement("insert into joblist (jobkey, jobname , jobgroupname, triggername, triggergroupname, cronstring, jobclass, cronhuman) values (?,?,?,?,?,?,?,?)");
				jobKey = UUID.randomUUID().toString();
				pst.setString(1, jobKey);
				pst.setString(2, jobName);
				pst.setString(3, jobGroupName);
				pst.setString(4, triggerName);
				pst.setString(5, triggerGroupName);
				pst.setString(6, cronString);
				pst.setString(7, jobClass);
				pst.setString(8, cronHuman);
				
				pst.execute();
				pst.close();
				
				
			}
			
			conn.commit();
			
		}
		
		finally{
			conn.setAutoCommit(true);
			conn.close();
			
		}
	}
	
	
	
	
	
	//getter and setters
	public String getJobKey() {
		return jobKey;
	}

	public void setJobKey(String jobKey) {
		this.jobKey = jobKey;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobGroupName() {
		return jobGroupName;
	}

	public void setJobGroupName(String jobGroupName) {
		this.jobGroupName = jobGroupName;
	}

	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	public String getTriggerGroupName() {
		return triggerGroupName;
	}

	public void setTriggerGroupName(String triggerGroupName) {
		this.triggerGroupName = triggerGroupName;
	}

	public String getCronString() {
		return cronString;
	}

	public void setCronString(String cronString) {
		this.cronString = cronString;
	}
	
	public String getJobClass() {
		return jobClass;
	}

	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}

	public String getCronHuman() {
		return cronHuman;
	}

	public void setCronHuman(String cronHuman) {
		this.cronHuman = cronHuman;
	}
	
}
