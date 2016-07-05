package com.jsantos.ps.finder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;

import com.jsantos.ps.objects.link.LinkBean;
import com.jsantos.ps.store.database.DS;
import com.jsantos.ps.webapp.SessionHelper;
import com.jsantos.util.logger.Logger;

public class FinderController2 implements Composer, EventListener {
	public static final String MODULE = FinderController2.class.getSimpleName();
	Tabpanel recentTabPanel = null;
	Tabpanel linkingHereTabpanel = null;
	private static JSONArray jsonArray= null;

	@Override
	public void doAfterCompose(Component cmp) throws Exception {
		Logger.logInfo(MODULE, "Method Called : doAfterCompose()");
		recentTabPanel = (Tabpanel) cmp.getFellow("recentTabPanel");
		((Tab) cmp.getFellow("recentTab")).setSelected(true);
		linkingHereTabpanel = (Tabpanel) cmp.getFellow("linkingHereTabPanel");

		Div divBookmarks = (Div)cmp.getFellow("boolmarks");
		ObjectList bookmarkList = buildBookmarkList();
		if (0<bookmarkList.getChildren().size()){
			Label bookMarks = new Label("Bookmarks:");
			bookMarks.setSclass("fieldLabel");
			divBookmarks.appendChild(bookMarks);
			divBookmarks.appendChild(bookmarkList);
		}

		ObjectList objectList = recentNodeList();
		objectList.setParent(recentTabPanel);
		
		String oid = Executions.getCurrent().getParameter("oid");
		linkingHereNodeList(oid).setParent(linkingHereTabpanel);
	}

	@Override
	public void onEvent(Event arg0) throws Exception {
		// TODO Auto-generated method stub
	}

	ObjectList buildBookmarkList() throws SQLException {
		Logger.logInfo(MODULE, "Method Called : buildBookmarkList()");
		ObjectList objectList = new ObjectList();
		String sql = null;
		if(SessionHelper.isVerifiedUser()){
			sql = "select objecttype, objectkey, description, created from objectlist where objectkey in (select topk from link where frompk in (select objectkey from objectlist where objecttype='tag' and textcontent='BOOKMARK')) order by created";
		}else{
			sql = "select objecttype, objectkey, description, created from objectlist where objectkey in (select topk from link where frompk in (select objectkey from objectlist where objecttype='tag' and textcontent='BOOKMARK') ) and objectkey in (select topk from link where frompk in (select objectkey from objectlist where objecttype='tag' and textcontent = 'PUBLIC' or textcontent = 'PUBLIC_EDITABLE' ) ) order by created";
		}

		Connection conn = DS.getConnection();
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				objectList.addItem(rs.getString("objecttype"),rs.getString("objectkey"), rs.getString("description"));
			}
		} finally {
			conn.close();
		}
		return objectList;
	}

	ObjectList recentNodeList() throws SQLException {
		Logger.logInfo(MODULE, "Method Called : recentNodeList()");
		ObjectList objectList = new ObjectList();
		String cadSql = null;
		jsonArray = new JSONArray();
		Connection conn = DS.getConnection();
		try {
			Statement st = conn.createStatement();
				cadSql="select o.objecttype, o.objectkey, description, max(t.created) as mc from timeline t, objectlist o where t.objectkey=o.objectkey and o.objectkey in (select l.topk from link l where frompk in (select objectkey from objectlist o where textcontent = 'public')) group by o.objecttype, o.objectkey, description order by mc desc limit 20";
			if(SessionHelper.isVerifiedUser()){			
				cadSql = "select o.objecttype, o.objectkey, description, max(t.created) as mc from timeline t, objectlist o where t.objectkey=o.objectkey  group by o.objecttype, o.objectkey, description order by mc desc limit 20";
			}
			
			ResultSet rs = st.executeQuery(cadSql);
			while (rs.next()) {
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
		} finally {
			conn.close();
		}
		return objectList;
	}

	ObjectList linkingHereNodeList(String topk) throws SQLException {
		Logger.logInfo(MODULE, "Method Called : linkingHereNodeList()");
		ObjectList objectList = new ObjectList();

		Connection conn = DS.getConnection();
		try {
			String sql = "select o.objectkey, o.objecttype, o.description from objectlist o where o.objectkey in (select frompk from link where topk=?) group by o.objectkey, description, o.objecttype ";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, topk);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				objectList.addItem(rs.getString("objecttype"),rs.getString("objectkey"), rs.getString("description"));
			}
		} finally {
			conn.close();
		}
		return objectList;
	}

	public static String recentSeearchNodeItemsList() {
		Logger.logDebug(MODULE, "jPatternArray.toJSONString() : "+jsonArray.toJSONString());
		return jsonArray.toJSONString();
	}
}
