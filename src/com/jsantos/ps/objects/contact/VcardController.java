package com.jsantos.ps.objects.contact;
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Textbox;

import com.jsantos.util.logger.Logger;

public class VcardController extends GenericForwardComposer<Component>{
	private static final String MODULE = VcardController.class.getSimpleName();
	private Textbox generalName;
	private Textbox generalMobilePhone;
	private Textbox generalHomePhone;
	private Textbox generalEmail;
	private Textbox generalWebsite;
	private Textbox generalAddress;
	private Textbox generalCity;
	private Textbox generalStateProvince;
	private Textbox generalZipPostalCode;
	private Textbox generalCountryRegion;
	private Image	generalImage;
	private Textbox vcardString;
	private Combobox honorPrefix;
	
	private Textbox privateAddress;
	private Textbox privateCity;
	private Textbox privateStateProvince;
	private Textbox privateZipPostal;
	private Textbox privateCountryRegion;
	private Textbox privateWebsite;
	private Datebox privateBirthday;
	
	private Textbox workJobTitle;
	private Textbox workDepartment;
	private Textbox workAddress;
	private Textbox workCity;
	private Textbox workStateProvince;
	private Textbox workZipPostalCode;
	private Textbox workCountryRegion;
	private Textbox workWebsite;
	
	private Textbox otherFacebook;
	private Textbox otherTwitter;
	private Textbox otherGoogle;
	private Textbox otherOther;
	private Textbox otherComments;
	
	Component temp;
	Include vcardZul ;
	
	public void doAfterCompose(Component thiswin) throws Exception {				
		super.doAfterCompose(thiswin);
		
		Logger.logInfo(MODULE, "Method Called :doAfterCompose() ");
	
		vcardZul.setAttribute("generalName",generalName);
		vcardZul.setAttribute("generalMobilePhone",generalMobilePhone);
		vcardZul.setAttribute("generalHomePhone",generalHomePhone );
		vcardZul.setAttribute("generalEmail",generalEmail );
		vcardZul.setAttribute("generalWebsite",generalWebsite );
		vcardZul.setAttribute("generalAddress",generalAddress );
		vcardZul.setAttribute("generalCity",generalCity );
		vcardZul.setAttribute("generalStateProvince",generalStateProvince );
		vcardZul.setAttribute("generalZipPostalCode",generalZipPostalCode );
		vcardZul.setAttribute("generalCountryRegion",generalCountryRegion );
		vcardZul.setAttribute("generalImage",generalImage );
		vcardZul.setAttribute("honorPrefix",honorPrefix );
	
		vcardZul.setAttribute("privateAddress", privateAddress);
		vcardZul.setAttribute("privateCity",privateCity);
		vcardZul.setAttribute("privateStateProvince",privateStateProvince );
		vcardZul.setAttribute("privateZipPostal",privateZipPostal );
		vcardZul.setAttribute("privateCountryRegion",privateCountryRegion );
		vcardZul.setAttribute("privateWebsite",privateWebsite );
		vcardZul.setAttribute("privateBirthday",privateBirthday );

		vcardZul.setAttribute("workJobTitle", workJobTitle);
		vcardZul.setAttribute("workDepartment",workDepartment);
		vcardZul.setAttribute("workAddress",workAddress );
		vcardZul.setAttribute("workCity",workCity );
		vcardZul.setAttribute("workStateProvince",workStateProvince );
		vcardZul.setAttribute("workZipPostalCode",workZipPostalCode );
		vcardZul.setAttribute("workCountryRegion",workCountryRegion );
		vcardZul.setAttribute("workWebsite",workWebsite );
	
		vcardZul.setAttribute("otherFacebook", otherFacebook);
		vcardZul.setAttribute("otherTwitter",otherTwitter);
		vcardZul.setAttribute("otherGoogle",otherGoogle );
		vcardZul.setAttribute("otherOther",otherOther );
		vcardZul.setAttribute("otherComments",otherComments );

 	}

	@Override
	public void onEvent(Event event) throws Exception {
		
		// TODO Auto-generated method stub
		Logger.logInfo(MODULE, "Method Called :onEvent(), event is :"+event);
		
	}
	
	public void setReadOnly(Component comp){
		Logger.logInfo(MODULE, "Method Called :setReadOnly()");
		Component compo = null;
		
		if(comp.getChildren().size() > 0){
		Iterator iter = comp.getChildren().iterator();
		while(iter.hasNext()){
			compo = (Component) iter.next();
			if(compo instanceof Textbox){
				((Textbox)compo).setReadonly(true);
			}//del if
			setReadOnly((compo));
		}//dei while
		}
	}
	
}
