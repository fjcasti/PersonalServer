package com.jsantos.ps.store.backup;



import java.io.File;
import java.sql.SQLException;

import org.quartz.SchedulerException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Progressmeter;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.jsantos.ps.scheduler.JobBean;
import com.jsantos.ps.scheduler.PsScheduler;
import com.jsantos.ps.store.DataFolder;
import com.jsantos.ps.store.backup.create.ZipFileCreator;
import com.jsantos.ps.store.backup.restore.ZipFileReader;
import com.jsantos.ps.webapp.GeneralProgressBarController;
import com.jsantos.ps.webapp.SessionHelper;

public class BackupModalController implements Composer, EventListener{
	
	Textbox textboxBackupPath = null;
	Button saveBackupButton = null;
	Button runBackupNow = null;
	Button schedulerButton = null;
	Label errorMsg = null;
	JobBean localBackup = null;
	Component thisWindow = null;
	Progressmeter pgmeter = null;
	Label curr_step = null;
	Button pgbCloseButton = null;
	GeneralProgressBarController pgb = null;
	Grid backupListGrid = null;
	Button restoreButton = null;
	Radiogroup radiogroup = null;
	Label lblCronString = null;
	Label lblCronHuman = null;
	
	
	@Override
	public void doAfterCompose(Component cmp) throws Exception {
		
		thisWindow = cmp;
		textboxBackupPath = (Textbox)cmp.getFellow("textboxBackupPath");
		
		schedulerButton = (Button)cmp.getFellow("schedulerButton");
		saveBackupButton = (Button)cmp.getFellow("saveBackupButton");
		runBackupNow = (Button)cmp.getFellow("runBackupNow");
		errorMsg = (Label)cmp.getFellow("errorMsg");
		backupListGrid = (Grid)cmp.getFellow("backupListGrid");
		restoreButton = (Button)cmp.getFellow("restoreButton");
		radiogroup= (Radiogroup)cmp.getFellow("radiogroup");
		lblCronString = (Label)cmp.getFellow("cronString");
		lblCronHuman = (Label)cmp.getFellow("cronHuman");
		
		saveBackupButton.addEventListener("onClick", this);
		runBackupNow.addEventListener("onClick", this);
		restoreButton.addEventListener("onClick", this);
		schedulerButton.addEventListener("onClick", this);
		
		textboxBackupPath.setValue(DataFolder.getBackupFolderPath());
		localBackup = JobBean.findJobByName("LocalBackup");
		if(localBackup != null){
			lblCronHuman.setValue(localBackup.getCronHuman());
			
		}
		
		
		fillBackuplistGrid();
		
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		
		Window schedulerWindow = null;
				
		if(event.getTarget() == schedulerButton){
						
			schedulerWindow = (Window)Executions.createComponents("/imp/schedulerModal.zul", thisWindow, null);
			schedulerWindow.doModal();
			 
		}
		else if(!DataFolder.checkPathPermisions(textboxBackupPath.getValue().trim())){
				errorMsg.setValue("You don't have permissions for this folder (don't forget the last slash)");
			}
			else if (event.getTarget() == saveBackupButton){
				
				
				saveJobBackup();
				
			}
			else if (event.getTarget() == runBackupNow){
				errorMsg.setValue("");
				DataFolder.addPropertyToConfig("backup_path", textboxBackupPath.getValue().trim());
				//pgb = ((GeneralProgressBarController) Executions.createComponents("/imp/generalProgressBar.zul", null, null));
				//pgb.setTitle("Making backup...");
				
				//pgb.setValue(50);
			
				ltoZipBackup(pgb);
				
				
				
				
				
			}
			else if (event.getName().equals("onActualize")){
				
			
				/*
				 * int cant = ((Integer) event.getData()).intValue();
				if(cant >= 100){
					pgbCloseButton.setVisible(true);
					pgb.setTitle(pgb.getTitle() + " Done!");
				}
				
				pgmeter.setValue( cant);
				curr_step.setValue(String.valueOf(cant));
				*/
				
			}else if (event.getTarget() == restoreButton){
				
				if(radiogroup.getSelectedItem() == null){
					Messagebox.show("You must select one backup to restore");
					
				}
				else
				if(Messagebox.OK == Messagebox.show("Are you sure you want to restore this backup?",
						"Restore backup",
						Messagebox.OK |  Messagebox.CANCEL,
						Messagebox.QUESTION))
				{
					System.out.println(DataFolder.getBackupFolderPath()+radiogroup.getSelectedItem().getValue());	

					new ZipFileReader().unzip(DataFolder.getBackupFolderPath()+"/"+radiogroup.getSelectedItem().getValue(), DataFolder.getBackupFolderPath(), SessionHelper.getUser());
													
				}
				
				
				
			}
		
	}
	
	private void saveJobBackup() {
		
			errorMsg.setValue("");
			DataFolder.addPropertyToConfig("backup_path", textboxBackupPath.getValue().trim());
			
			if(localBackup == null){ localBackup = new JobBean();};
			
			localBackup.setJobName("LocalBackup");
			localBackup.setJobGroupName("GroupBackups");
			localBackup.setTriggerName("LocalBackup");
			localBackup.setTriggerGroupName("GroupBackups");
			localBackup.setJobClass("JobLocalBackup");
			
			if(!lblCronString.getValue().equals("")){
			localBackup.setCronString(lblCronString.getValue());
			localBackup.setCronHuman(lblCronHuman.getValue());
			}
			try {
				localBackup.save();
				PsScheduler.stop();
				PsScheduler.start();
			} catch (SQLException e) {
				
				e.printStackTrace();
			} catch (SchedulerException e) {
				
				e.printStackTrace();
			}
			
			
			thisWindow.detach();
		}//fin funcion
	
		private void fillBackuplistGrid(){
			
			
			File[] files = new File(DataFolder.getBackupFolderPath()).listFiles();
			Rows rows = backupListGrid.getRows();
			Components.removeAllChildren(rows);
			for(File f:files){
				
				String name = f.getName();
				if(name.contains("backup") && name.contains("zip") ){
				String[] partes = name.split("__");
					
				Row r = new Row();
				r.setParent(rows);
				
				Radio ra = new Radio();
				ra.setValue(name);
				ra.setRadiogroup(radiogroup);
				ra.setParent(r);
				
				Label fecha = new Label();
				fecha.setValue(partes[0]);
				fecha.setParent(r);
				
				Label user = new Label();
				user.setValue(partes[1]);
				user.setParent(r);
				
				Label size = new Label();
				size.setValue(String.valueOf(f.length() / (1024 * 1024)) + "Mg");
				size.setParent(r);
				
				}
			}
			
			
		}
		
		public void setCronString(String str){}
		public void setHumanCron(String str){}
		
		public void ltoZipBackup(Component pgb){
			
			Executions.getCurrent().getDesktop().enableServerPush(true);
			
			//new ZipFileCreator().createBackup(DataFolder.getBackupFolderPath(), SessionHelper.getUser());
			ZipFileCreator zfc = new ZipFileCreator(DataFolder.getBackupFolderPath(), SessionHelper.getUser(),pgb);
			
			new Thread(zfc).start();
			//zfc.createBackup();
			//new DatabaseBackup().createBackup(DataFolder.getBackupFolderPath()+"database_backup.xml");
			fillBackuplistGrid();
			
			
		}

}//fin clase
