package com.jsantos.ps.mainwindow;

import javax.sql.DataSource;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.jsantos.ps.store.DataFolder;
import com.jsantos.ps.webapp.SessionHelper;
import com.jsantos.ps.webapp.WebApp;

public class LoginWindowController implements Composer, EventListener{
	Textbox textboxName = null;
	Textbox textboxPassword = null;
	Button buttonOk = null;
	Label lb1 = null;
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		textboxName = (Textbox)comp.getFellow("textboxName");
		textboxPassword = (Textbox)comp.getFellow("textboxPassword");
		buttonOk = (Button)comp.getFellow("buttonOk");
		textboxPassword.addEventListener(Events.ON_OK, this);
		lb1 = (Label)comp.getFellow("lb1");
		
		buttonOk.addEventListener("onClick", this);
		textboxName.setFocus(true);
		
	
		if (!DataFolder.checkConfigProperties())
		{
		 lb1.setValue("This is the first time you run your personal server, the user name and password that you write now, it will be the user and password that you'll use from now on");
		}else if (Executions.getCurrent().getParameter("bad_password") != null ){
			
			lb1.setValue("Username or password are incorrect!");
			
			
		}
	}
	@Override
	public void onEvent(Event event) throws Exception {
		/*
		DataSource dataSource = WebApp.dataSources.getDataSource(textboxName.getText());
		if (null == dataSource) throw new RuntimeException("datasource doesnt exist for: " + textboxName.getText());
		Sessions.getCurrent().setAttribute("USER_DATASOURCE", dataSource);
		SessionHelper.setUser(textboxName.getText());

		Executions.getCurrent().sendRedirect("index.jsp");
		*/
		
		if("onClick".equals(event.getName()) || Events.ON_OK.equals(event.getName())){
			if (!DataFolder.checkConfigProperties())
			{
				DataFolder.createConfigProperties(textboxName.getValue(), textboxPassword.getValue());	
			}
			
			LoginWindow.checkLogin(textboxName.getValue(), textboxPassword.getValue());		
			
		}
	}

}
