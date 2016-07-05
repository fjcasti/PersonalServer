package com.jsantos.ps.objects.mail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.zkoss.zhtml.Td;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;

import com.jsantos.ps.finder.ObjectList;
import com.jsantos.ps.objects.link.LinkBean;
import com.jsantos.ps.store.database.DS;


public class MailController  implements Composer, EventListener{
	Label fromLabel = null;
	Label subjectLabel = null;
	Div mailBodyDiv = null;
	Td attachments = null;
		
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		fromLabel = (Label)comp.getFellow("fromLabel");
		subjectLabel = (Label)comp.getFellow("subjectLabel");
		mailBodyDiv = (Div)comp.getFellow("mailBodyDiv");
		attachments = (Td)comp.getFellow("attachments");

		String objectkey = Executions.getCurrent().getParameter("oid");
 		MailBean bean = new MailBean();
 		bean.loadFromDatabase(objectkey);
 		
 		fromLabel.setValue(bean.getFrom());
 		subjectLabel.setValue(bean.getSubject());
 		if (null != bean.getHtmlsrc() && 0<bean.getHtmlsrc().length()) new Html(bean.getHtmlsrc()).setParent(mailBodyDiv);
 		else new Html(bean.getTextContent().replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\r\n", "<br />")).setParent(mailBodyDiv);
 		
		attachments.getChildren().clear();
		ObjectList objectList = new ObjectList();

		Connection conn = DS.getConnection();
		try{
			String sql = "select objectkey, objecttype, description from objectlist where objectkey in (select topk from link where frompk=?)";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, objectkey);
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				objectList.addItem(rs.getString("objecttype"),rs.getString("objectkey"), rs.getString("description")) ;
			}
		}
		finally{
			conn.close();
		}
		objectList.setParent(attachments);
 		
 		
	}

	@Override
	public void onEvent(Event arg0) throws Exception {
		
	}

}
