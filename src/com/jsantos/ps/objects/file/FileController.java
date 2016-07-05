package com.jsantos.ps.objects.file;


import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

import com.jsantos.ps.webapp.MainFilter;

public class FileController  implements Composer, EventListener{
	//Label labelFileName = null;
	Textbox textboxFileName = null;
	Label labelMimeType = null;
	Label labelCreated = null;
	Label labelVersion = null;
	Toolbarbutton linkDownload = null;
	FileRefBean fileRefBean = null;

	
	
	@Override
	public void doAfterCompose(Component mainComp) throws Exception {
		//labelFileName = (Label)mainComp.getFellow("labelFileName");
		textboxFileName = (Textbox)mainComp.getFellow("textboxFileName");
		labelMimeType = (Label)mainComp.getFellow("labelMimeType");
		labelCreated = (Label)mainComp.getFellow("labelCreated");
		labelVersion = (Label)mainComp.getFellow("labelVersion");
		linkDownload = (Toolbarbutton)mainComp.getFellow("linkDownload");
		
		String oid = Executions.getCurrent().getParameter("oid");
		
		fileRefBean = new FileRefBean();
		if (fileRefBean.findByKey(oid)){
			textboxFileName.setValue(fileRefBean.getFileName());
			labelMimeType.setValue(fileRefBean.getMimeType());
			labelCreated.setValue(fileRefBean.getCreated().toString());
			labelVersion.setValue(String.valueOf(fileRefBean.getVersion()));
			linkDownload.setHref("/FileServerServlet/?objectkey=" + fileRefBean.getObjectKey());
			addFileViewer();
			textboxFileName.addEventListener("onChange", this);
		}
		
		
	}
	
	
	public void addFileViewer(){
		
		if(null !=fileRefBean.getMimeType()){
		
		if (fileRefBean.getMimeType().contains("image")){ 
			Image image = new Image("/FileServerServlet/?objectkey=" + fileRefBean.getObjectKey());
			image.setWidth("100%");
			linkDownload.getParent().appendChild(image);
		}
		
		if (fileRefBean.getMimeType().contains("audio/mpeg")){ 
			//Xspf
			//linkDownload.getParent().appendChild(image);
		}
		
		
		
		
		}
		
	}

	@Override
	public void onEvent(Event event) throws Exception {
		
		if(event.getTarget() == textboxFileName){
			
			if(Messagebox.OK == Messagebox.show("Do you want to rename this file?",
					"Rename File",
					Messagebox.OK |  Messagebox.CANCEL,
					Messagebox.QUESTION))
			{
				if(fileRefBean.rename(textboxFileName.getValue())){
					Messagebox.show("file name changed!");
					Executions.sendRedirect(MainFilter.OID_MARKER + fileRefBean.getObjectKey());
				};
			}
			
			
		}
		
	}

}
