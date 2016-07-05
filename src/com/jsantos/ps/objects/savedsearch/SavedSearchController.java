package com.jsantos.ps.objects.savedsearch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

import com.jsantos.ps.finder.ObjectList;
import com.jsantos.ps.objects.link.LinkBean;
import com.jsantos.ps.store.database.DS;
import com.jsantos.ps.webapp.MainFilter;
import com.jsantos.util.logger.Logger;

public class SavedSearchController  implements Composer, EventListener{
	private static final String MODULE = SavedSearchController.class.getSimpleName();
	Label descriptionLabel = null;
	Div editorDiv = null;
	Toolbarbutton editToolbarbutton = null;
	Textbox descriptionTextbox = null;
	Textbox sqlTextbox = null;
	Button saveButton = null;
	Button testButton = null;
	Div resultsDiv = null;
	boolean tested = false;
	
	SavedSearchBean savedSearchBean = null;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		Logger.logInfo(MODULE, "Method Called :doAfterCompose() ");
		descriptionLabel = (Label)comp.getFellow("labeldescription");
		editorDiv = (Div)comp.getFellow("editordiv");
		editToolbarbutton = (Toolbarbutton)comp.getFellow("editbutton");
		
		descriptionTextbox = (Textbox)comp.getFellow("descriptiontextbox");
		sqlTextbox = (Textbox)comp.getFellow("sql");
		saveButton = (Button)comp.getFellow("save");
		testButton = (Button)comp.getFellow("test");
		resultsDiv = (Div)comp.getFellow("results");
		
		saveButton.addEventListener("onClick", this);
		testButton.addEventListener("onClick", this);
		sqlTextbox.addEventListener("onChanging", this);
		editToolbarbutton.addEventListener("onClick", this);
		descriptionTextbox.addEventListener("onChanging", this);
		
		String sObjectKey = Executions.getCurrent().getParameter("oid");
		if (null != sObjectKey){
			savedSearchBean = SavedSearchBean.findByObjectKey(sObjectKey);
			if (null!= savedSearchBean){
				descriptionTextbox.setText(savedSearchBean.getDescription());
				descriptionLabel.setValue(savedSearchBean.getDescription());
				sqlTextbox.setText(savedSearchBean.getSearchSql());
				search(savedSearchBean.getSearchSql());
			}
		}
		
		String paramNew = Executions.getCurrent().getParameter("new");
		if (null != paramNew){
			editorDiv.setVisible(true);
		}
	}

	@Override
	public void onEvent(Event event) throws Exception {
		Logger.logInfo(MODULE, " Method Called :onEvent(Event event) ");
		if (descriptionTextbox == event.getTarget() && event instanceof InputEvent){
			descriptionLabel.setValue(((InputEvent)event).getValue());
		}
		if (editToolbarbutton == event.getTarget()){
			if (editorDiv.isVisible()) {
				editorDiv.setVisible(false);
				editToolbarbutton.setLabel("Edit");
			}
			else{
				editorDiv.setVisible(true);
				editToolbarbutton.setLabel("Close Editor");
			}
		}
		if (sqlTextbox == event.getTarget()){
			tested=false;
		}
		if (testButton == event.getTarget()){
			search(sqlTextbox.getText());
			tested=true;
		}
		else if (saveButton == event.getTarget()){
			if (null == savedSearchBean) savedSearchBean = new SavedSearchBean();
			
			if (null == descriptionTextbox.getText() || 0==descriptionTextbox.getText().trim().length()){
				Messagebox.show("Please specify title");
				return;
			}
			if (null == sqlTextbox.getText() || 0==sqlTextbox.getText().trim().length()){
				Messagebox.show("No Sql content");
				return;
			}
			if (!tested){
				Messagebox.show("You need to test the sql before saving...");
				return;
			}
			savedSearchBean.setDescription(descriptionTextbox.getText());
			savedSearchBean.setSearchSql(sqlTextbox.getText());
			savedSearchBean.save();
			Executions.sendRedirect(MainFilter.OID_MARKER + savedSearchBean.getObjectKey());
		}
	}
	
	void search(String sql) throws SQLException{
		Logger.logInfo(MODULE, " Method Called :search(String sql) ");
		resultsDiv.getChildren().clear();
		ObjectList objectList = new ObjectList();

		Connection conn = DS.getConnection();
		try{
			sql = "select * from objectlist where objectkey in (" + sql + ")";
			System.out.println(sql);
			PreparedStatement pst = conn.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				objectList.addItem(rs.getString("objecttype"),rs.getString("objectkey"), rs.getString("description")) ;
			}
		}
		finally{
			conn.close();
		}
		objectList.setParent(resultsDiv);
	}
}
