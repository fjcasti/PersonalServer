package com.jsantos.ps.webapp;

import java.sql.SQLException;

import org.quartz.SchedulerException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Progressmeter;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.jsantos.ps.scheduler.JobBean;
import com.jsantos.ps.scheduler.PsScheduler;
import com.jsantos.ps.store.DataFolder;

public class PreferencesController implements Composer, EventListener{

	Textbox textboxMailServer;
	Textbox textboxUserName;
	Textbox textboxUserPass;
	Button saveAccountButton;
	Button schedulerButton;
	Window thiswin;
	JobBean checkMailJob = null;
	Label lblCronString = null;
	Label lblCronHuman = null;
	
	@Override
	public void doAfterCompose(Component cmp) throws Exception {
		// TODO Auto-generated method stub
		
		thiswin = (Window)cmp;
		textboxMailServer = (Textbox)cmp.getFellow("textboxMailServer");
		textboxUserName = (Textbox)cmp.getFellow("textboxUserName");
		textboxUserPass = (Textbox)cmp.getFellow("textboxUserPass");
		saveAccountButton = (Button)cmp.getFellow("saveAccountButton");
		schedulerButton = (Button)cmp.getFellow("schedulerButton");
		lblCronString = (Label)cmp.getFellow("cronString");
		lblCronHuman = (Label)cmp.getFellow("cronHuman");
		
		saveAccountButton.addEventListener("onClick", this);
		schedulerButton.addEventListener("onClick", this);
		
		if(null != CTX.getCfg().mailaccount){		
		
		textboxMailServer.setText(CTX.getCfg().mailaccount.getMailServer());
		textboxUserName.setText(CTX.getCfg().mailaccount.getUserName());
		textboxUserPass.setText(CTX.getCfg().mailaccount.getUserPass());
		}
		
		checkMailJob = JobBean.findJobByName("CheckMail");
		if(checkMailJob != null){
			lblCronHuman.setValue(checkMailJob.getCronHuman());
			
		}
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		if(event.getTarget() == schedulerButton){
			
			Window window = (Window)Executions.createComponents("/imp/schedulerModal.zul", thiswin, null);
			window.doModal();
			 
		}
		if (event.getTarget() == saveAccountButton){
			CTX.getCfg().mailaccount.setMailServer(textboxMailServer.getText());
			CTX.getCfg().mailaccount.setUserName(textboxUserName.getText());
			CTX.getCfg().mailaccount.setUserPass(textboxUserPass.getText());
			CTX.getCfg().mailaccount.save();
			saveJobBackup();
	  
			}
		
	}
	
	private void saveJobBackup() {
		
		
		
		if(checkMailJob == null){ checkMailJob = new JobBean();};
		
		checkMailJob.setJobName("CheckMail");
		checkMailJob.setJobGroupName("GroupCheks");
		checkMailJob.setTriggerName("CheckMail");
		checkMailJob.setTriggerGroupName("GroupCheks");
		checkMailJob.setJobClass("JobCheckMail");
		
		if(!lblCronString.getValue().equals("")){
			checkMailJob.setCronString(lblCronString.getValue());
			checkMailJob.setCronHuman(lblCronHuman.getValue());
		}
		try {
			checkMailJob.save();
			PsScheduler.stop();
			PsScheduler.start();
		} catch (SQLException e) {
			
			e.printStackTrace();
		} catch (SchedulerException e) {
			
			e.printStackTrace();
		}
		
		
		thiswin.detach();
	}//fin funcion

}
