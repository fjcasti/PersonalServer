package com.jsantos.ps.objects.tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

import com.jsantos.ps.finder.ObjectList;
import com.jsantos.ps.mainwindow.LoginWindow;
import com.jsantos.ps.objects.link.LinkBean;
import com.jsantos.ps.store.database.DS;
import com.jsantos.util.logger.Logger;

public class TagViewerController implements Composer, EventListener{
	private static final String MODULE = TagViewerController.class.getSimpleName();
	String oid = null;
	Div divSearchResult = null;
	Label labelTagName = null;
	private static JSONArray jsonArray= null;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		Logger.logInfo(MODULE, " Method Called :doAfterCompose() ");
		divSearchResult = (Div)comp.getFellow("searchresult");
		labelTagName = (Label)comp.getFellow("tagname");
	
		oid = Executions.getCurrent().getParameter("oid");
		TagBean tagBean = TagBean.findTagByObjectKey(oid);
		labelTagName.setValue(tagBean.getDescription());
		buildTaggedObjectList(oid).setParent(divSearchResult);
	}
	
	@Override
	public void onEvent(Event arg0) throws Exception {
	}

	
	static ObjectList buildTaggedObjectList(String fromOid) throws SQLException{
		Logger.logInfo(MODULE, " Method Called :buildTaggedObjectList() ");
		ObjectList objectList = new ObjectList();
		jsonArray = new JSONArray();
		Connection conn = DS.getConnection();
		try{
			String sql = "select o.objectkey, o.objecttype, o.description from objectlist o where o.objectkey in (select topk from link where frompk=?)";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, fromOid);
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
					
				String objectTypeStr ="" , objectKeyStr="" , description ="";
				JSONObject jBlacklistSSID = new JSONObject();
				objectTypeStr = rs.getString("objecttype");
				objectKeyStr = rs.getString("objectkey");
				description = rs.getString("description");
				
				jBlacklistSSID.put("objecttype",objectTypeStr);
				jBlacklistSSID.put("objectkey",objectKeyStr);
				jBlacklistSSID.put("description",description);
				jsonArray.push(jBlacklistSSID);

				//objectList.addItem(rs.getString("objecttype"),rs.getString("objectkey"), rs.getString("description"));
				objectList.addItem(objectTypeStr, objectKeyStr, description);				
			}
		}
		finally{
			conn.close();
		}
		return objectList;
	}

	public static String findObjectDetailByID(String objectID){
		try{
			buildTaggedObjectList(objectID);
			Logger.logDebug(MODULE, "jPatternArray.toJSONString() : "+jsonArray.toJSONString());
			return jsonArray.toJSONString();
		}catch(Exception ex){
			return "Error in fetching Tag Detail";			
		}
	}
}
