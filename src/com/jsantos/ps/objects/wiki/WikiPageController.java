package com.jsantos.ps.objects.wiki;

import java.sql.SQLException;

import org.zkforge.ckez.CKeditor;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;

import com.jsantos.ps.mainwindow.LoginWindow;
import com.jsantos.ps.objects.ObjectsHelper;
import com.jsantos.ps.webapp.MainFilter;
import com.jsantos.ps.webapp.SessionHelper;
import com.jsantos.util.logger.Logger;


public class WikiPageController  implements Composer, EventListener{
	private static final String MODULE = WikiPageController.class.getSimpleName();
	Tabpanel htmlViewPanel = null;
	Tabpanel editViewPanel = null;
	Tab titleTab = null;
	Tab editTab = null;
	CKeditor fckEditor = null;
	Button saveButton = null;
	WikiPageBean wikiPage = new WikiPageBean();
	Textbox textboxTitle = null;
	Div smallDiv = null;
	
	@Override
	public void doAfterCompose(Component tabbox) throws Exception {
		Logger.logInfo(MODULE, " Method Called :doAfterCompose() Start ");
		// grab references
		htmlViewPanel 	= (Tabpanel)tabbox.getFellow("htmlView");
		editViewPanel 	= (Tabpanel)tabbox.getFellow("editView");
		titleTab 		= (Tab)tabbox.getFellow("titleTab");
		editTab 		= (Tab)tabbox.getFellow("editTab");
 		saveButton 		= (Button)tabbox.getFellow("saveButton");
 		textboxTitle 	= (Textbox)tabbox.getFellow("textboxTitle");
		smallDiv 		= (Div)tabbox.getFellow("smallDiv");
 		boolean bNew = "true".equals(Executions.getCurrent().getParameter("new"));
 		String objectkey = Executions.getCurrent().getParameter("oid");
 		wikiPage.setObjectkey(objectkey);
 		Logger.logInfo(MODULE, " objectkey :  "+objectkey);
 		if(!bNew){
 			Logger.logInfo(MODULE, " Inside if(!bNew) ");
	
			if (wikiPage.findByKey()){
				titleTab.setLabel(wikiPage.description);
				new Html(wikiPage.getHtmlsrc()).setParent(htmlViewPanel);
			}
 		}
 		else{
 			Logger.logInfo(MODULE, " Inside else of if(!bNew) ");
 			setEditor();
 			editTab.setSelected(true);
 		}
 		
 		if(! SessionHelper.isVerifiedUser() ){
 			
 			saveButton.setVisible(false);
 			editTab.setVisible(false);
 			editViewPanel.setVisible(false);
 			((Tab)tabbox.getFellow("historyTab")).setVisible(false);
 			//(Tabpanel)tabbox.getFellow("editView");
 			 			
 		}
 		
 		if( ObjectsHelper.isPublicEditable(wikiPage.getObjectkey())){
 			
 			saveButton.setVisible(true);
 			editTab.setVisible(true);
 			editViewPanel.setVisible(true);
 			((Tab)tabbox.getFellow("historyTab")).setVisible(true);
 			//(Tabpanel)tabbox.getFellow("editView");
 			 			
 		}
 		
		
		saveButton.addEventListener("onClick", this);
		tabbox.addEventListener("onSelect", this);
		Logger.logInfo(MODULE, " Method Called doAfterCompose() End :");

	}

	public static String findObjectDetailByID(String objectkey){
		Logger.logInfo(MODULE, " Method Called :findObjectDetailByID()");
		Logger.logInfo(MODULE, " Wikipage objectkey :  "+objectkey);
		String result = "Sorry ! No detail Available";
		WikiPageBean wikiPage = new WikiPageBean();
 		wikiPage.setObjectkey(objectkey);
	
		try {
			if (wikiPage.findByKey()){
					result = wikiPage.getHtmlsrc();
			}
		} catch (SQLException e) {
			Logger.logInfo(MODULE, " Error while finding Object detail by ID, Reason :  "+e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	@Override
	public void onEvent(Event event) throws Exception {
		Logger.logInfo(MODULE, " Method Called :onEvent() "+event.getName());
		//System.out.println("Event: " + event + " Target: " + event.getTarget());
		if (event.getTarget() == saveButton){
			wikiPage.setDescription(textboxTitle.getText());
			wikiPage.setHtmlsrc(fckEditor.getValue());
			Logger.logInfo(MODULE, " wikiPage.getObjectkey() :"+wikiPage.getObjectkey());
			wikiPage.save();
			Executions.sendRedirect(MainFilter.OID_MARKER + wikiPage.getObjectkey());
			/*
			htmlViewPanel.getChildren().clear();
			titleTab.setLabel(wikiPage.description);
			new Html(wikiPage.getHtmlsrc()).setParent(htmlViewPanel);
			titleTab.setSelected(true);
			editTab.setSelected(false);
			*/
		}
		if ("onSelect".equals(event.getName()) && editTab==event.getTarget()){
			setEditor();
		}
	}
	
	void setEditor(){
		Logger.logInfo(MODULE, " Method Called :setEditor() ");
		textboxTitle.setText(wikiPage.description);
		if (null != fckEditor) fckEditor.detach();
		fckEditor = new CKeditor();
		Logger.logInfo("wikipagecontroller", fckEditor.getHeight());		
		editViewPanel.insertBefore(fckEditor, smallDiv);
		fckEditor.setValue(wikiPage.getHtmlsrc());
		fckEditor.addEventListener("onChange", this);
	}

}
