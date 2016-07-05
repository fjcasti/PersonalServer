package com.jsantos.ps.webapp;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Progressmeter;
import org.zkoss.zul.Window;

public class GeneralProgressBarController extends Window implements Composer, EventListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2863826907010320535L;
	Progressmeter curr_met = null;
	Button closeButton = null;
	Label curr_step = null;
	
	
		
	
	@Override
	public void onEvent(Event arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doAfterCompose(Component cmp) throws Exception {
	
		curr_met = (Progressmeter)cmp.getFellow("curr_met");
		closeButton = (Button)cmp.getFellow("closeButton");
		curr_step = (Label)cmp.getFellow("curr_step");
		System.out.println("asdf");
		
			
	}
	
	public void setValue(int cant){
		
		curr_met.setValue(cant);
		curr_step.setValue(String.valueOf(cant));
	}
	
	public void showClose(Boolean sw){
		
		closeButton.setVisible(sw);
	}
	
	
	

}
