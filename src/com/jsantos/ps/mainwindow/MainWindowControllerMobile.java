package com.jsantos.ps.mainwindow;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Include;

import com.jsantos.ps.timeline.TimeLineAction;
import com.jsantos.ps.timeline.TimeLineBean;

public class MainWindowControllerMobile implements Composer{
	
	public void doAfterCompose(Component mainComponent) throws Exception {
		Include centerArea = (Include)mainComponent.getFellow("centerArea");
		
		String oid = Executions.getCurrent().getParameter("oid");
		if (null != oid){
			String objectType = MainWindowController.findObjectType(oid);
			
			centerArea.setSrc(MainWindowController.buildObjectTypeHandler(objectType, oid));
			TimeLineBean.insertEntry(oid, TimeLineAction.READ);
		}
	}
	
}
