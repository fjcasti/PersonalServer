package com.jsantos.ps.finder;

import java.util.HashMap;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Image;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;

import com.jsantos.ps.objects.link.LinkBean;

@SuppressWarnings("serial")
public class ObjectList extends Listbox implements EventListener{

	public ObjectList(){
		Listhead listhead = new Listhead();
		listhead.setParent(this);
		Listheader header = new Listheader();
		header.setParent(listhead);
		header.setWidth("30px");
		header = new Listheader();
		header.setParent(listhead);
	}
	
	public void addItem(String objectType, String objectkey, String description){
		String url = LinkBean.buildUrlFromObjectListRecord(objectkey);
		String link = LinkBean.buildLinkFromObjectListRecord(objectkey, description);
		Listitem item = new Listitem();
		item.addEventListener("onClick", this);
		item.setAttribute("URL", url);
		item.setParent(this);
		
		Listcell listcell = new Listcell();
		listcell.setParent(item);
		Image image = new Image("/img/icons/" + objectType + ".png");
		image.setParent(listcell);
		//Html html = new Html("<img src=\"img/icons/" + objectType + ".png\" title=\"" + objectType + "\" />");
		//html.setParent(listcell);
		listcell = new Listcell();		
		listcell.setParent(item);
		//////////
		Listcell listcellCb = new Listcell();
		Checkbox cb = new Checkbox();
		cb.setParent(listcellCb);
		Html htmlCb = new Html();
		htmlCb.setParent(listcellCb);
		//////////
		Html html = new Html(link);
		html.setParent(listcell);
		//Text text = new Text(description);
		//text.setParent(listcell);
	
	}
	
	public Image findIconFile(String mimetype){
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("image/jpeg", "Image-jpg-16.png");
		hashMap.put("audio/mpeg", "Mp3-16.png");
	return null;	
	}

	@Override
	public void onEvent(Event arg0) throws Exception {
		System.out.println("Redirecting to: " + (String)arg0.getTarget().getAttribute("URL"));
		Executions.getCurrent().sendRedirect((String)arg0.getTarget().getAttribute("URL"));
		
	}
}
