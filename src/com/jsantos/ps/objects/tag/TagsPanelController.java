package com.jsantos.ps.objects.tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.zkoss.zhtml.Td;
import org.zkoss.zhtml.Text;
import org.zkoss.zhtml.Tr;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Messagebox;

import com.jsantos.ps.objects.ObjectsHelper;
import com.jsantos.ps.objects.wiki.WikiPageBean;
import com.jsantos.ps.store.database.DS;
import com.jsantos.ps.webapp.SessionHelper;

public class TagsPanelController implements Composer, EventListener{
	Tr tagList = null;
	Combobox newTagCombo = null;
	Combobox systemTagCombobox = null;
	String objectKey = null;
	Hlayout tagCombos = null;
	Image deleteButton = null;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		tagList = (Tr)comp.getFellow("tagList");
		newTagCombo = (Combobox)comp.getFellow("typeCombobox");
		systemTagCombobox = (Combobox)comp.getFellow("systemTagCombobox");
		tagCombos = (Hlayout)comp.getFellow("tagCombos");
		deleteButton = (Image)comp.getFellow("deleteButton");
		objectKey = Executions.getCurrent().getParameter("oid");
		
		newTagCombo.addEventListener("onChanging", this);
		newTagCombo.addEventListener("onOK", this);
		systemTagCombobox.addEventListener("onOK", this);
		deleteButton.addEventListener("onClick", this);
		deleteButton.setAttribute("id", "deleteButton");
		
		updateObjectTags();
		if(SessionHelper.isVerifiedUser()){
		fillTypeCombobox();
		fillSystemTagsCombobox();
		}else{
			tagCombos.setVisible(false);
			deleteButton.setVisible(false);
		}
		
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		if ("onChanging".equals(event.getName())){
			//TODO: update candidate list
		}
		
		if ("onOK".equals(event.getName())){
						
			Combobox combo = (Combobox) event.getTarget();
			TagBean.addTagToObject(objectKey, combo.getText());
			combo.setText("");
			updateObjectTags();
		}
		if ("onClick".equals(event.getName()) && event.getTarget() instanceof Image){

			if("deleteButton".equals(event.getTarget().getAttribute("id"))){
				if(ObjectsHelper.deleteFromObjectKey(objectKey)){
					Executions.sendRedirect("ps_oid__1");
				}
				
			}
			else
			{
				
				String description = (String)event.getTarget().getAttribute("TAG_DESCRIPTION");
				TagBean.removeTagFromObject(objectKey, description);
				updateObjectTags();
				fillTypeCombobox();
			}
					
		}
	
	}

		
		
	
	
	void updateObjectTags() throws SQLException{
		tagList.getChildren().clear();
		Connection conn = DS.getConnection();
		boolean verifiedUser = SessionHelper.isVerifiedUser();
		
		try{
			String sql = "select description from link l, objectlist o where l.frompk=o.objectkey and l.topk=? and l.verb='tag'";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, objectKey);
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				Td td = new Td();
				td.setParent(tagList);
				td.setSclass("z-label");
				td.setStyle("background-color:lightyellow");
				if(verifiedUser){
					Image image = new Image();
					image.setParent(td);
					image.setSrc("/img/icons/delete.png");
					image.addEventListener("onClick", this);
					image.setAttribute("TAG_DESCRIPTION", rs.getString("description"));
				}
				Text text = new Text(rs.getString("description"));
				text.setParent(td);
			}
		}
		finally{
			conn.close();
		}
	}
	
	void fillTypeCombobox() throws SQLException{
		newTagCombo.getChildren().clear();
		Connection conn = DS.getConnection();
		try{
			String sql = "select description from objectlist  where objectkey NOT in (select topk   from link where frompk in (select objectkey  from objectlist  where description = 'system')) and objectlist.objecttype='tag'";
			
			PreparedStatement pst = conn.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				newTagCombo.appendItem(rs.getString("description"));
			}
		}
		finally{
			conn.close();
		}
		
		
	}
	
	void fillSystemTagsCombobox() throws SQLException{
		systemTagCombobox.getChildren().clear();
		Connection conn = DS.getConnection();
		try{
			String sql = "select description from objectlist  where objectkey in (select topk   from link where frompk in (select objectkey  from objectlist  where description = 'system'))";
			
			PreparedStatement pst = conn.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				systemTagCombobox.appendItem(rs.getString("description"));
			}
		}
		finally{
			conn.close();
		}
		
		
	}
	
}
