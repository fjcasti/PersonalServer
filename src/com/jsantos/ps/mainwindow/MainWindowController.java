package com.jsantos.ps.mainwindow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Include;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.jsantos.ps.finder.SearchBoxController;
import com.jsantos.ps.objects.file.FileUploader;
import com.jsantos.ps.objects.mail.client.IMAPClient;
import com.jsantos.ps.store.database.DS;
import com.jsantos.ps.timeline.TimeLineAction;
import com.jsantos.ps.timeline.TimeLineBean;
import com.jsantos.ps.webapp.CTX;
import com.jsantos.ps.webapp.SessionHelper;
import com.jsantos.util.logger.Logger;

public class MainWindowController  implements Composer, EventListener{
	private static final String MODULE = MainWindowController.class.getSimpleName();
	Include centerArea = null;
	Menuitem menuitemNewNote = null;
	Menuitem menuitemNewFile = null;
	Menuitem menuitemSavedSearch = null;
	Menuitem menuitemNewContact = null;
	Menuitem menuitemClipboard = null;
	//Button logOutButton = null;
	Menuitem logOutButton = null;
	Menu menuNew = null;
	Menu menuSystem = null;
	Menuitem menuitemBackup = null;
	Menuitem menuitemPreferences = null;
	
	@Override
	public void doAfterCompose(Component mainComponent) throws Exception {
		Logger.logInfo(MODULE, "Method Called :doAfterCompose() ");
		centerArea = (Include)mainComponent.getFellow("centerArea");
		menuitemNewNote = (Menuitem)mainComponent.getFellow("menuitemNewNote");
		menuitemNewFile = (Menuitem)mainComponent.getFellow("menuitemNewFile");
		menuitemSavedSearch = (Menuitem)mainComponent.getFellow("menuitemSavedSearch");
		menuitemNewContact = (Menuitem)mainComponent.getFellow("menuitemNewContact");
		menuitemClipboard = (Menuitem)mainComponent.getFellow("menuitemClipboard");
		//logOutButton = (Button)mainComponent.getFellow("logOutButton");
		logOutButton = (Menuitem)mainComponent.getFellow("logOutButton");
		menuNew = (Menu)mainComponent.getFellow("menuNew");
		menuSystem = (Menu)mainComponent.getFellow("menuSystem");
		menuitemBackup = (Menuitem)mainComponent.getFellow("menuitemBackup");
		menuitemPreferences = (Menuitem)mainComponent.getFellow("menuitemPreferences");
		
		
		menuitemNewNote.addEventListener("onClick", this);
		//menuitemNewFile.addEventListener("onClick", this);
		menuitemNewFile.setUpload("true");
		menuitemNewFile.addEventListener("onUpload", this);
		menuitemSavedSearch.addEventListener("onClick", this);
		menuitemNewContact.addEventListener("onClick", this);
		menuitemBackup.addEventListener("onClick", this);
		menuitemPreferences.addEventListener("onClick", this);
		menuitemClipboard.addEventListener("onClick", this);
		logOutButton.addEventListener("onClick", this);
		mainComponent.getFellow("check_mail").addEventListener("onClick", this);
		
		
		String oid = Executions.getCurrent().getParameter("oid");
		if (null != oid){
			String objectType = findObjectType(oid);
			
			centerArea.setSrc(buildObjectTypeHandler(objectType, oid));
			TimeLineBean.insertEntry(oid, TimeLineAction.READ);
		}
		if(!SessionHelper.isVerifiedUser()){
			logOutButton.setLabel("Log in");
			logOutButton.setImage("/img/Login_in-32.png");
			menuNew.setVisible(false);
			menuSystem.setVisible(false);
		}
		
	}
	
	
	public static String buildObjectTypeHandler(String objectType, String oid){
		Logger.logInfo(MODULE, "ObjectType : "+objectType+", oid : "+oid);
		if (null == objectType) return null;
		if ("html".equals(objectType)) return "/imp/wikipage.zul?oid=" + oid;
		else if ("fileref".equals(objectType)) return "/imp/file.zul?oid" + oid;
		else if ("mail".equals(objectType)) return "/imp/mail.zul?oid" + oid;
		else if ("savedsearch".equals(objectType)) return "/imp/saved_search.zul?oid" + oid;
		else if ("tag".equals(objectType)) return "/imp/tagviewer.zul?oid" + oid;
		else if ("contact".equals(objectType)) return "/imp/contact.zul?oid" + oid;		
		else throw new RuntimeException("object type not handled: " + objectType);
	}
	
	
	@Override
	public void onEvent(Event event) throws Exception {
		//System.out.println(event.getTarget().toString());
		if (event.getTarget() == menuitemNewNote){
			centerArea.setSrc("/imp/wikipage.zul?new=true");
		}else
			
		if (event.getTarget() == menuitemNewContact){
			centerArea.setSrc("/imp/contact.zul?new=true");
		}else
			
		if (event.getTarget() == menuitemBackup){
		((Window) Executions.createComponents("/imp/backupModal.zul", null, null)).doModal();
  
		}else
			
		if (event.getTarget() == menuitemPreferences){
			((Window) Executions.createComponents("/imp/preferencesModal.zul", null, null)).doModal();
	  	}else
	  		
	  	
	  		
		if (event.getTarget() == menuitemClipboard){
			((Window) Executions.createComponents("/imp/clipboardModal.zul", null, null)).doModal();
	  
		}else
		/*
		if (event.getTarget() == menuitemNewFile){
			FileUploader.upload();
		}
		*/
			
		if (event.getTarget() == logOutButton){
			SessionHelper.logOut();
		}else
			
		if (event.getTarget() == menuitemSavedSearch){
			centerArea.setSrc("/imp/saved_search.zul?new=true");
		}else
		
		if (event.getTarget() == menuitemSavedSearch){
			centerArea.setSrc("/imp/saved_search.zul?new=true");
		}else
		
		if (event.getName().equals("onUpload")){
			//Messagebox.show("file uploaded");
			UploadEvent uploadEvent = (UploadEvent)event;
			FileUploader.onUpload(uploadEvent.getMedias());
		}else
			
		if ("check_mail".equals(event.getTarget().getId())){
			CTX.checkMail();
		}
	}
	
	public static String findObjectType(String sOid) throws SQLException{
		
		String retValue = null;
		Connection conn = DS.getConnection();
		try{
			PreparedStatement pst = conn.prepareStatement("select * from objectlist where objectkey=?");
			pst.setString(1, sOid);
			ResultSet rs = pst.executeQuery();
			if (rs.next()){
				retValue = rs.getString("objecttype");
			}
		}
		finally{
			conn.close();
		}
		return retValue;
	}

}
