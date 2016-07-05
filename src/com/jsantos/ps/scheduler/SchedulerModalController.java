package com.jsantos.ps.scheduler;


import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Window;

import com.jsantos.ps.webapp.CTX;

public class SchedulerModalController extends Window implements Composer, EventListener {

	Spinner easyMinSpinner;
	Timebox easyTimeboxInput;
	Spinner easyDayOfWeekSpinner;
	
	Button okButton;
	Window thiswin;

	
	Radiogroup sv1;
	String cronString;
	String humanCron;
	
	
	
	
	/*Spinner minSpinner;
	Spinner hourSpinner;
	Spinner daysSpinner;
	Spinner weekSpinner;
	Spinner monthSpinner;
	Datebox dateboxInput;
	Timebox timeboxInput;
	
	Checkbox monday;
	Checkbox tuesday;
	Checkbox wednesday;
	Checkbox thusday;
	Checkbox friday;
	Checkbox saturday;
	Checkbox sunday;
	*/
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		
		
		thiswin = (Window)comp;
		sv1 = (Radiogroup)comp.getFellow("sv1");
		
		okButton = (Button)comp.getFellow("okButton");
		okButton.addEventListener("onClick", this);
		
		easyMinSpinner = (Spinner)comp.getFellow("easyMinSpinner");
		easyTimeboxInput = (Timebox)comp.getFellow("easyTimeboxInput");
		easyDayOfWeekSpinner = (Spinner)comp.getFellow("easyDayOfWeekSpinner");
		
		
		
		/*
		minSpinner = (Spinner)comp.getFellow("minSpinner");
		hourSpinner = (Spinner)comp.getFellow("hourSpinner");
		daysSpinner = (Spinner)comp.getFellow("daysSpinner");
		weekSpinner = (Spinner)comp.getFellow("weekSpinner");
		monthSpinner = (Spinner)comp.getFellow("monthSpinner");
		
		dateboxInput = (Datebox)comp.getFellow("dateboxInput");
		timeboxInput = (Timebox)comp.getFellow("timeboxInput");
		
		monday = (Checkbox)comp.getFellow("monday");
		tuesday = (Checkbox)comp.getFellow("tuesday");
		wednesday = (Checkbox)comp.getFellow("wednesday");
		thusday = (Checkbox)comp.getFellow("thusday");
		friday = (Checkbox)comp.getFellow("friday");
		saturday = (Checkbox)comp.getFellow("saturday");
		sunday = (Checkbox)comp.getFellow("sunday");
		
		
		*/
		
		
	}
	
	
	@Override
	public void onEvent(Event event) throws Exception {
		if(event.getTarget() == okButton){
			
			cronString = "";
			
			if(sv1.getSelectedItem() != null){
				switch(sv1.getSelectedIndex()){
					
				case 0: 
					cronString = "0 0/"+ easyMinSpinner.getText() + " * * * ?";
					humanCron = "Run every "+ easyMinSpinner.getText() + " minutes";
					break;
				case 1:
					
					Date date = easyTimeboxInput.getValue();
					cronString = date.getSeconds() + " " + date.getMinutes() + " " + date.getHours() + " * * ?";
					humanCron = "Run at " + easyTimeboxInput.getText() + " every day";
					break;
					
				case 2:
					cronString = "0 0 0 * * "+ easyDayOfWeekSpinner.getText();
					humanCron = "Run every " + easyDayOfWeekSpinner.getText() + " of week";
					break;
					
				}//del switch
				
			}
			
			/*
			if(!minSpinner.getText().equals("0")){
				cronString += "0/"+minSpinner.getText()+" ";
			}else{cronString+="0"+" ";}
			
			if(!hourSpinner.getText().equals("0")){
				cronString +="0/"+hourSpinner.getText()+" ";
			}else{cronString+="*"+" ";}
			
			if(!daysSpinner.getText().equals("0")){
				cronString +="1/"+daysSpinner.getText()+" ";
			}else{cronString+="*"+" ";}
			
			if(!weekSpinner.getText().equals("0")){
				cronString +="0/"+weekSpinner.getText()+" ";
			}else{cronString+="*"+" ";}
			
			if(!monthSpinner.getText().equals("0")){
				cronString +="0/"+monthSpinner.getText()+" ";
			}else{cronString+="*"+" ";}
			
			cronString+="?"+" ";
						
			*/
			
			
			Label lbl1 = (Label) thiswin.getParent().getFellow("cronString");
			Label lbl2 = (Label) thiswin.getParent().getFellow("cronHuman");
			lbl1.setValue(cronString);
			lbl2.setValue(humanCron);
			thiswin.detach();
			
		}
			
	}

	

}
