package com.jsantos.ps.finder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.zkoss.zhtml.Td;
import org.zkoss.zhtml.Text;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;

import com.jsantos.ps.store.database.DS;
import com.jsantos.util.logger.Logger;


public class SearchBoxController extends GenericForwardComposer implements Composer, EventListener {
	private static final String MODULE = SearchBoxController.class.getSimpleName();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<String, String> tagListAdvancedSearch = null;
	Textbox textboxSearch = null;
	Div resultsDiv = null;
	Combobox typeComboboxAdvancedSearch = null;
	Combobox tagCombobox = null;
	Button advancedButton = null;
	Button addTagAdvancedSearch = null;
	Vlayout advancedForm = null;
	Div tagHolderAdvancedSearch = null;
	Button searchButton = null;
	
	public void doAfterCompose(Component mainComponent) throws Exception {
		super.doAfterCompose(mainComponent);
		Logger.logInfo(MODULE, "Method Called : doAfterCompose()");		

		tagListAdvancedSearch  = new HashMap<String, String>();
		textboxSearch.focus();
		textboxSearch.addEventListener("onChaning", this);
		
		String inSessionsearchQuery = (String)session.getAttribute("searchQuery");
		if(inSessionsearchQuery!=null && inSessionsearchQuery.trim().length()>0){
			Logger.logInfo(MODULE, " Calling Search() based on previous searchQuery :"+inSessionsearchQuery);
			if(resultsDiv != null) resultsDiv.getChildren().clear();
			search();
		}
	}
	
	public void getPreviousTwoSearchResult(ResultSet rs,ObjectList objectList){
		Logger.logInfo(MODULE, "Method Called : getPreviousTwoSearchResult()");	
		try {
			while(rs.next()){
				objectList.addItem(rs.getString("objecttype"),rs.getString("objectkey"), rs.getString("description"));
			}
			objectList.setParent(resultsDiv);
		} catch (SQLException e) {
			Logger.logError(MODULE, "Error  : "+e.getMessage());	
			e.printStackTrace();
		}
	}
	
	/*public void onChanging$textboxSearch(Event event) throws SQLException{
		
		if(!advancedForm.isVisible()){
		String searchQuery = textboxSearch.getValue();
		if(searchQuery.trim().equals("")){
			if(resultsDiv != null)
			resultsDiv.getChildren().clear();
		}else
		if (shouldSearch(searchQuery))
			search();
		
		
		System.out.println(advancedForm.isVisible());
		System.out.println(shouldSearch(textboxSearch.getText()));
		System.out.println(((Textbox)event.getTarget()).getText());
		
		if(advancedForm.isVisible() && shouldSearch(textboxSearch.getText())){
			search();
			
		}
	}*/
	
	public void onOK$textboxSearch(Event event) throws SQLException{
		search();
	}
	
	public void onClick$advancedButton(Event event) throws WrongValueException, SQLException{
		if(advancedForm.isVisible()){
			advancedForm.setVisible(false);
			search();
		}
		else{
			advancedForm.setVisible(true);
			fillTypeCombobox();
			};
	}
	
	public void onClick$addTagAdvancedSearch(Event event){
		if(!tagCombobox.getSelectedItem().getLabel().equals(""))
		addTagAdvancedSearch(tagCombobox.getSelectedItem().getLabel());
	}
	
	public void onChange$typeComboboxAdvancedSearch(Event event) throws WrongValueException, SQLException{
		search();
	}
	public void onChange$tagCombobox(Event event) throws WrongValueException, SQLException{
		search();
	}
	
	public void onClick$searchButton(Event event) throws WrongValueException, SQLException{
		//session.removeAttribute("searchQuery");
		search();
		
	}

	public void onClick(Event event){
		if (event.getTarget() instanceof Image){
			delTagAdvancedSearch( ((Image) event.getTarget()).getAttribute("TAG_DESCRIPTION").toString());
		}
	}
	
	
	boolean shouldSearch(String searchQuery){
		
		if (2<searchQuery.length()) return true;
		
		return false;
	}
	
	
	
	/*private String getFilterByTag(){
		
		String tags="";
		if(tagListAdvancedSearch.size() > 0){
		Iterator it = tagListAdvancedSearch.entrySet().iterator();
		while (it.hasNext()) {
		Map.Entry e = (Map.Entry)it.next();
			//System.out.println(e.getKey() + " " + e.getValue());
		tags += "'" + e.getValue().toString() + "',";
		
		}
		if(!tags.equals("")){
		tags = tags.substring(0, tags.length()-1);//como quitar la coma
		}
	return " and objectkey in (select topk from link where frompk in (select objectkey from objectlist where objecttype='tag' and textcontent in ("+tags+"))) ";
		}
		return "";
	}*/
	
	void search() throws SQLException{
		Logger.logInfo(MODULE, " Method Called :search() ");
		String searchQuery = textboxSearch.getText();
		String prevSearchQuery = (String)session.getAttribute("searchQuery");
		System.out.println("presentsearchQuery 	: "+searchQuery);
		System.out.println("prevSearchQuery 	: "+prevSearchQuery);
		
		if(searchQuery.equalsIgnoreCase(prevSearchQuery)){
			session.setAttribute("searchQuery",searchQuery);
			Logger.logDebug(MODULE, "Not performing search, Same Search string found.");
			return;
		}
		
		String sql = "select objectkey, objecttype, description from objectlist where ";
		
		Vector<String> sqlFilters = new Vector<String>();
		
		String sqlUserControl = "";
		
		if(session.getAttribute("searchQuery")!=null && searchQuery.trim().length()==0){
			searchQuery = (String)session.getAttribute("searchQuery");
			Logger.logInfo(MODULE, " Taking SearchQuery  from Session :"+searchQuery);
			session.removeAttribute("objectList");
		}
		
		String sqlFinal = " group by objecttype, objectkey, description order by objecttype desc limit 20";
		
		//contol de la caja de text de busqueda
		if(!searchQuery.trim().equals("")){
			//HACER: este parámetro configurable en ini o .properties
			searchQuery = searchQuery.toLowerCase();
			searchQuery = searchQuery.replaceAll("[^a-z A-Z0-9]+","");
			sqlFilters.add("LCASE(description) LIKE ('%"+ searchQuery +"%') "+
					    "or LCASE(textcontent) LIKE ('%"+ searchQuery +"%') " );
		}
		
		//control del combobox del tipo de objetco
		if(typeComboboxAdvancedSearch.getSelectedItem() != null && !typeComboboxAdvancedSearch.getSelectedItem().getValue().equals("")){
			sqlFilters.add(" objecttype = '"+typeComboboxAdvancedSearch.getSelectedItem().getValue()+"'");
		}
		
		//control del combobox de tags
		String tags="";
		if(tagListAdvancedSearch.size() > 0){
			
			Iterator<?> it = tagListAdvancedSearch.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry e = (Map.Entry)it.next();
				tags += "'" + e.getValue().toString() + "',";
			}
			if(!tags.equals("")){
				tags = tags.substring(0, tags.length()-1);
			}
			sqlFilters.add(" objectkey in (select topk from link where frompk in (select objectkey from objectlist where objecttype='tag' and textcontent in ("+tags+"))) ");
		}
		
		if(!sqlFilters.isEmpty()){
			
			for(int i=0;i<sqlFilters.size();i++){
				if(i>0) sql += " and ";
				sql += (String)sqlFilters.get(i);
				
			}
			sql += sqlFinal;
					
			if(resultsDiv != null) resultsDiv.getChildren().clear();
			Label label = new Label("Search results for: " + searchQuery);
			label.setParent(resultsDiv);
			label.setSclass("fieldLabel");
			ObjectList objectList = new ObjectList();	
						
			Connection conn = DS.getConnection();			
			PreparedStatement pst = conn.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			boolean isRecordAvailable = false;
			while(rs.next()){
				isRecordAvailable = true;
				objectList.addItem(rs.getString("objecttype"),rs.getString("objectkey"), rs.getString("description"));
			}
			
			session.setAttribute("searchQuery",searchQuery);
			if(!isRecordAvailable){
				objectList.addItem(null, null, "No Result found");
			}
			objectList.setParent(resultsDiv);
			if(session.getAttribute("objectList")!=null){
				ObjectList prevObjectList = (ObjectList)session.getAttribute("objectList");
				label = new Label("Previous search result");
				label.setParent(resultsDiv);
				label.setSclass("fieldLabel");				
				prevObjectList.setParent(resultsDiv);
				session.setAttribute("prevObjectList",prevObjectList);
			}
			session.setAttribute("objectList",objectList);
			Logger.logInfo(MODULE, " Sql :  "+sql);
		}
	}
	
	void fillTypeCombobox() throws SQLException{
		tagCombobox.getChildren().clear();
		
		Connection conn = DS.getConnection();
		try{
			String sql = "select description from objectlist  where objectlist.objecttype='tag'";
			
			PreparedStatement pst = conn.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				tagCombobox.appendItem(rs.getString("description"));
			}
		}
		finally{
			conn.close();
		}
		
	}
	
	
	void addTagAdvancedSearch(String stag){
		tagListAdvancedSearch.put(stag, stag);
		actualiceTagAdvancedSearch();
		try {
			search();
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void delTagAdvancedSearch(String stag) {
		tagListAdvancedSearch.remove(stag);
		actualiceTagAdvancedSearch();
		try {
			search();
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	void actualiceTagAdvancedSearch(){
		tagHolderAdvancedSearch.getChildren().clear();
		
		Iterator it = tagListAdvancedSearch.entrySet().iterator();
		while (it.hasNext()) {
		Map.Entry e = (Map.Entry)it.next();
			//System.out.println(e.getKey() + " " + e.getValue());
		
			Td td = new Td();
			td.setParent(tagHolderAdvancedSearch);
			td.setSclass("z-label");
			td.setStyle("background-color:lightyellow;font-size:small");
		
			Image image = new Image();
			image.setParent(td);
			image.setSrc("/img/icons/delete.png");
			image.addEventListener("onClick", this);
			image.setAttribute("TAG_DESCRIPTION", e.getValue().toString());
		
			Text text = new Text(e.getValue().toString());
		
			text.setParent(td);
		}
	}
}
