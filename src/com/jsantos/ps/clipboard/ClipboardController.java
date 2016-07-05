package com.jsantos.ps.clipboard;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.Request;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.jsantos.ps.objects.file.FileUploader;
import com.jsantos.ps.webapp.CTX;

public class ClipboardController implements Composer, EventListener{
	
	Checkbox isPublicCheckbox;
	Textbox textboxClipboard;
	Button buttonClipboardUpload;
	Label labelPublicLink;
	Toolbarbutton toolbarbuttonFileName;
	Window thiswin;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		thiswin = (Window)comp;
		isPublicCheckbox = (Checkbox)comp.getFellow("isPublicCheckbox");
		textboxClipboard = (Textbox)comp.getFellow("textboxClipboard");
		buttonClipboardUpload = (Button)comp.getFellow("buttonClipboardUpload");
		labelPublicLink = (Label)comp.getFellow("labelPublicLink");
		toolbarbuttonFileName = (Toolbarbutton)comp.getFellow("toolbarbuttonFileName");
		
		if(CTX.getCfg().clipboard.isPublic){
			isPublicCheckbox.setChecked(true);
			toolbarbuttonFileName.setHref("/FileServerServlet/?objectkey=clipboard" );}

		textboxClipboard.addEventListener("onChange", this);
		buttonClipboardUpload.setUpload("true");
		buttonClipboardUpload.addEventListener("onUpload", this);
		isPublicCheckbox.addEventListener("onCheck", this);
		toolbarbuttonFileName.setDisabled(true);
		
		

		
			if(CTX.getCfg().clipboard.getDataType() != null){
			if(CTX.getCfg().clipboard.getDataType().equals(Clipboard.TYPE_TEXT)){
				textboxClipboard.setText(CTX.getCfg().clipboard.getText());
				
				
			}else
			if(CTX.getCfg().clipboard.getDataType().equals(Clipboard.TYPE_FILE)){
				toolbarbuttonFileName.setDisabled(false);
				toolbarbuttonFileName.setHref("/FileServerServlet/?objectkey=clipboard" );
				toolbarbuttonFileName.setLabel(CTX.getCfg().clipboard.getFile().getName());
				
			}
			}
		
		
		//String clipBoardText = (String) CTX.getAttribute("clipboard");
		//if(clipBoardText != null && !clipBoardText.isEmpty()){textboxClipboard.setText(clipBoardText);}
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
	
		if(event.getTarget() == textboxClipboard){
			
			CTX.getCfg().clipboard.setText(textboxClipboard.getText());
			toolbarbuttonFileName.setDisabled(false);
			toolbarbuttonFileName.setLabel("empty");
			
		}
		if (event.getName().equals("onUpload")){
			
			UploadEvent uploadEvent = (UploadEvent)event;
			Clipboard.onUpload(uploadEvent.getMedias());
			toolbarbuttonFileName.setLabel(CTX.getCfg().clipboard.getFile().getName());
			toolbarbuttonFileName.setDisabled(false);
			textboxClipboard.setText(null);
			
		}
		
		if (event.getTarget() == isPublicCheckbox){
			//System.out.println(Executions.getCurrent().getLocalAddr());
			String surl = ((HttpServletRequest)Executions.getCurrent().getNativeRequest()).getRequestURL().toString();
			if(isPublicCheckbox.isChecked()){
				CTX.getCfg().clipboard.setPublic(true);
				labelPublicLink.setValue(surl.substring(0, surl.lastIndexOf("/"))+"/FileServerServlet/?objectkey=clipboard");
			}else{
				labelPublicLink.setValue("");
				CTX.getCfg().clipboard.setPublic(false);
			}
			
		}
		
	}
	
	

}
