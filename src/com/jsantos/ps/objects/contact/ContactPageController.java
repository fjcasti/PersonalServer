package com.jsantos.ps.objects.contact;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.ImageIO;

import net.sourceforge.cardme.engine.VCardEngine;
import net.sourceforge.cardme.io.VCardWriter;
import net.sourceforge.cardme.vcard.EncodingType;
import net.sourceforge.cardme.vcard.VCard;
import net.sourceforge.cardme.vcard.VCardImpl;
import net.sourceforge.cardme.vcard.VCardVersion;
import net.sourceforge.cardme.vcard.features.AddressFeature;
import net.sourceforge.cardme.vcard.features.BirthdayFeature;
import net.sourceforge.cardme.vcard.features.EmailFeature;
import net.sourceforge.cardme.vcard.features.ExtendedFeature;
import net.sourceforge.cardme.vcard.features.OrganizationFeature;
import net.sourceforge.cardme.vcard.features.PhotoFeature;
import net.sourceforge.cardme.vcard.features.TelephoneFeature;
import net.sourceforge.cardme.vcard.features.TitleFeature;
import net.sourceforge.cardme.vcard.features.URLFeature;
import net.sourceforge.cardme.vcard.types.AddressType;
import net.sourceforge.cardme.vcard.types.BirthdayType;
import net.sourceforge.cardme.vcard.types.EmailType;
import net.sourceforge.cardme.vcard.types.ExtendedType;
import net.sourceforge.cardme.vcard.types.FormattedNameType;
import net.sourceforge.cardme.vcard.types.NameType;
import net.sourceforge.cardme.vcard.types.OrganizationType;
import net.sourceforge.cardme.vcard.types.PhotoType;
import net.sourceforge.cardme.vcard.types.TelephoneType;
import net.sourceforge.cardme.vcard.types.TitleType;
import net.sourceforge.cardme.vcard.types.URLType;
import net.sourceforge.cardme.vcard.types.VersionType;
import net.sourceforge.cardme.vcard.types.media.ImageMediaType;
import net.sourceforge.cardme.vcard.types.parameters.AddressParameterType;
import net.sourceforge.cardme.vcard.types.parameters.BirthdayParameterType;
import net.sourceforge.cardme.vcard.types.parameters.EmailParameterType;
import net.sourceforge.cardme.vcard.types.parameters.ParameterTypeStyle;
import net.sourceforge.cardme.vcard.types.parameters.TelephoneParameterType;
import net.sourceforge.cardme.vcard.types.parameters.URLParameterType;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;

import com.jsantos.ps.objects.ObjectsHelper;
import com.jsantos.ps.objects.wiki.WikiPageBean;
import com.jsantos.ps.webapp.MainFilter;
import com.jsantos.ps.webapp.SessionHelper;
import com.jsantos.util.logger.Logger;

public class ContactPageController extends GenericForwardComposer<Component>{
	private static final String MODULE = ContactPageController.class.getSimpleName(); 
	Button saveButton = null;
	Button tableButton = null;
	ContactBean contactBean = new ContactBean();
	Include vcardZul ;
	static Tabpanel htmlViewPanel = null;
	Tabpanel editViewPanel = null;
	Tab titleTab = null;
	Tab editTab = null;
	
	private static String  generalNameValue;
	private static String  generalMobilePhoneValue;
	private static String  generalHomePhoneValue;
	private static String  generalEmailValue;
	private static String  generalWebsiteValue;
	private static String  generalAddressValue;
	private static String  generalCityValue;
	private static String  generalStateProvinceValue;
	private static String  generalZipPostalCodeValue;
	private static String  generalCountryRegionValue;
	private static BufferedImage	generalImageValue;
	String  vcardStringValue;
	private static String honorPrefixValue;
	
	private static String  privateAddressValue;
	private static String  privateCityValue;
	private static String  privateStateProvinceValue;
	private static String  privateZipPostalValue;
	private static String  privateCountryRegionValue;
	private static String  privateWebsiteValue;
	private static Date privateBirthdayValue;
	
	private static String  workJobTitleValue;
	private static String  workDepartmentValue;
	private static String  workAddressValue;
	private static String  workCityValue;
	private static String  workStateProvinceValue;
	private static String  workZipPostalCodeValue;
	private static String  workCountryRegionValue;
	private static String  workWebsiteValue;
	
	private static String  otherFacebookValue;
	private static String  otherTwitterValue;
	private static String  otherGoogleValue;
	private static String  otherOtherValue;
	private static String  otherCommentsValue;
		
	public void doAfterCompose(Component thiswin) throws Exception {
		super.doAfterCompose(thiswin);
		Logger.logInfo(MODULE, "Method Called :doAfterCompose()");
		
		saveButton 		= (Button)thiswin.getFellow("saveButton");
		htmlViewPanel 	= (Tabpanel)thiswin.getFellow("htmlView");
		editViewPanel 	= (Tabpanel)thiswin.getFellow("editView");
		titleTab 		= (Tab)thiswin.getFellow("titleTab");
		editTab 		= (Tab)thiswin.getFellow("editTab");
		
 		boolean bNew = "true".equals(Executions.getCurrent().getParameter("new"));
 		String objectkey = Executions.getCurrent().getParameter("oid");
 		contactBean.setObjectkey(objectkey);
 		Logger.logInfo(MODULE, " objectkey :  "+objectkey);
 		if(!bNew){
 			Logger.logInfo(MODULE, " Inside if(!bNew) ");
			if (contactBean.findByKey()){
				titleTab.setLabel(contactBean.userName);
				String parsedvcardDetail = parseVcardString(contactBean.getContactDetail());		
				new Html(parsedvcardDetail).setParent(htmlViewPanel);
			}
 		}
 		else{
 			Logger.logInfo(MODULE, " Inside else of if(!bNew) ");
 			editTab.setSelected(true);
 		}
		
 		if(! SessionHelper.isVerifiedUser() ){ 			
 			editTab.setVisible(false);
 			editViewPanel.setVisible(false);
 			saveButton.setVisible(false);
 		}
 		
 		if( ObjectsHelper.isPublicEditable(contactBean.getObjectkey())){ 			
 			editTab.setVisible(true);
 			editViewPanel.setVisible(true);
 			saveButton.setVisible(true);
 		}
 		
		saveButton.addEventListener("onClick", this);
		thiswin.addEventListener("onSelect", this);
		editTab.addEventListener("onSelect", this);
	}
	
	public static String findObjectDetailByID(String objectkey){
		Logger.logInfo(MODULE, " Method Called :findObjectDetailByID()");
		Logger.logInfo(MODULE, " Wikipage objectkey :  "+objectkey);
		String result = "Sorry ! No detail Available";
		ContactBean contactBean = new ContactBean();
		contactBean.setObjectkey(objectkey);
	
		try {
			if (contactBean.findByKey()){				
				result = parseVcardString(contactBean.getContactDetail());
			}
		} catch (SQLException e) {
			Logger.logInfo(MODULE, "Error while finding Contact Object detail by ID, Reason : "+e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	
	public void onEvent(Event event) throws Exception {
		Logger.logInfo(MODULE, " Method Called :onEvent() "+event.getName());
		
		Textbox generalName 		 = (Textbox) vcardZul.getAttribute("generalName");
		Textbox generalMobilePhone 	 = (Textbox) vcardZul.getAttribute("generalMobilePhone");
		Textbox generalHomePhone 	 = (Textbox) vcardZul.getAttribute("generalHomePhone");
		Textbox generalEmail 		 = (Textbox) vcardZul.getAttribute("generalEmail");
		Textbox generalWebsite 		 = (Textbox) vcardZul.getAttribute("generalWebsite");
		Textbox generalAddress 		 = (Textbox) vcardZul.getAttribute("generalAddress");
		Textbox generalCity 		 = (Textbox) vcardZul.getAttribute("generalCity");
		Textbox generalStateProvince = (Textbox) vcardZul.getAttribute("generalStateProvince");
		Textbox generalZipPostalCode = (Textbox) vcardZul.getAttribute("generalZipPostalCode");
		Textbox generalCountryRegion = (Textbox) vcardZul.getAttribute("generalCountryRegion");
		Image generalImage 		 	 = (Image) 	 vcardZul.getAttribute("generalImage");
		Combobox honorPrefix 		 = (Combobox)vcardZul.getAttribute("honorPrefix");
		
		Textbox privateAddress 			= (Textbox)vcardZul.getAttribute("privateAddress");
		Textbox privateCity 			= (Textbox)vcardZul.getAttribute("privateCity");
		Textbox privateStateProvince 	= (Textbox)vcardZul.getAttribute("privateStateProvince");
		Textbox privateZipPostal 		= (Textbox)vcardZul.getAttribute("privateZipPostal");
		Textbox privateCountryRegion	= (Textbox)vcardZul.getAttribute("privateCountryRegion" );
		Textbox privateWebsite 			= (Textbox)vcardZul.getAttribute("privateWebsite");
		Datebox privateBirthday 		= (Datebox)vcardZul.getAttribute("privateBirthday");

		Textbox workJobTitle	= (Textbox)vcardZul.getAttribute("workJobTitle");
		Textbox workDepartment	= (Textbox)vcardZul.getAttribute("workDepartment");
		Textbox workAddress  	= (Textbox)vcardZul.getAttribute("workAddress");
		Textbox workCity 		= (Textbox)vcardZul.getAttribute("workCity");
		Textbox workStateProvince = (Textbox)vcardZul.getAttribute("workStateProvince");
		Textbox workZipPostalCode = (Textbox)vcardZul.getAttribute("workZipPostalCode");
		Textbox workCountryRegion = (Textbox)vcardZul.getAttribute("workCountryRegion");
		Textbox workWebsite 	  = (Textbox)vcardZul.getAttribute("workWebsite");
	
		Textbox otherFacebook 	= (Textbox)vcardZul.getAttribute("otherFacebook");
		Textbox otherTwitter 	= (Textbox)vcardZul.getAttribute("otherTwitter");
		Textbox otherGoogle 	= (Textbox)vcardZul.getAttribute("otherGoogle");
		Textbox otherOther 		= (Textbox)vcardZul.getAttribute("otherOther");
		Textbox otherComments	= (Textbox)vcardZul.getAttribute("otherComments");
		
		if( "onSelect".equals(event.getName()) &&  editTab==event.getTarget()){
			Logger.logInfo(MODULE, " Editing vcard data");
			generalName.setValue(generalNameValue);
			vcardZul.setAttribute("generalName",generalName);
			
			generalMobilePhone.setValue(generalMobilePhoneValue);
			vcardZul.setAttribute("generalMobilePhone",generalMobilePhone);
			
			generalHomePhone.setValue(generalHomePhoneValue);
			vcardZul.setAttribute("generalHomePhone",generalHomePhone );
			
			generalEmail.setValue(generalEmailValue);
			vcardZul.setAttribute("generalEmail",generalEmail );
			
			generalWebsite.setValue(generalWebsiteValue);
			vcardZul.setAttribute("generalWebsite",generalWebsite );
			
			generalAddress.setValue(generalAddressValue);
			vcardZul.setAttribute("generalAddress",generalAddress );
			
			generalCity.setValue(generalCityValue);
			vcardZul.setAttribute("generalCity",generalCity );
			
			generalStateProvince.setValue(generalStateProvinceValue);
			vcardZul.setAttribute("generalStateProvince",generalStateProvince );
			
			generalZipPostalCode.setValue(generalZipPostalCodeValue);
			vcardZul.setAttribute("generalZipPostalCode",generalZipPostalCode );
			
			generalCountryRegion.setValue(generalCountryRegionValue);
			vcardZul.setAttribute("generalCountryRegion",generalCountryRegion );
			
			if(generalImageValue!=null){
				generalImage.setContent(generalImageValue);
				vcardZul.setAttribute("generalImage",generalImage );
			}
			honorPrefix.setValue(honorPrefixValue);
			vcardZul.setAttribute("honorPrefix",honorPrefix );
		
			privateAddress.setValue(privateAddressValue);
			vcardZul.setAttribute("privateAddress", privateAddress);
			
			privateCity.setValue(privateCityValue);
			vcardZul.setAttribute("privateCity",privateCity);
			
			privateStateProvince.setValue(privateStateProvinceValue);
			vcardZul.setAttribute("privateStateProvince",privateStateProvince );
			
			privateZipPostal.setValue(privateZipPostalValue);
			vcardZul.setAttribute("privateZipPostal",privateZipPostal );
			
			privateCountryRegion.setValue(privateCountryRegionValue);
			vcardZul.setAttribute("privateCountryRegion",privateCountryRegion );
			
			privateWebsite.setValue(privateWebsiteValue);
			vcardZul.setAttribute("privateWebsite",privateWebsite );
			
			privateBirthday.setValue(privateBirthdayValue);
			vcardZul.setAttribute("privateBirthday",privateBirthday );

			workJobTitle.setValue(workJobTitleValue);
			vcardZul.setAttribute("workJobTitle", workJobTitle);
			
			workDepartment.setValue(workDepartmentValue);
			vcardZul.setAttribute("workDepartment",workDepartment);
			
			workAddress.setValue(workAddressValue);
			vcardZul.setAttribute("workAddress",workAddress );
			
			workCity.setValue(workCityValue);
			vcardZul.setAttribute("workCity",workCity );
			
			workStateProvince.setValue(workStateProvinceValue);
			vcardZul.setAttribute("workStateProvince",workStateProvince );
			
			workZipPostalCode.setValue(workZipPostalCodeValue);
			vcardZul.setAttribute("workZipPostalCode",workZipPostalCode );
			
			workCountryRegion.setValue(workCountryRegionValue);
			vcardZul.setAttribute("workCountryRegion",workCountryRegion );
			
			workWebsite.setValue(workWebsiteValue);
			vcardZul.setAttribute("workWebsite",workWebsite );
		
			otherFacebook.setValue(otherFacebookValue);
			vcardZul.setAttribute("otherFacebook", otherFacebook);
			
			otherTwitter.setValue(otherTwitterValue);
			vcardZul.setAttribute("otherTwitter",otherTwitter);
			
			otherGoogle.setValue(otherGoogleValue);
			vcardZul.setAttribute("otherGoogle",otherGoogle );
			
			otherOther.setValue(otherOtherValue);
			vcardZul.setAttribute("otherOther",otherOther );
			
			otherComments.setValue(otherCommentsValue);
			vcardZul.setAttribute("otherComments",otherComments );			
		}
		
		if(event.getTarget() == saveButton){
			Logger.logInfo(MODULE, " Saving vcard data");
			VCard vcard = null;
			
			vcard = new VCardImpl();			
			vcard.setVersion(new VersionType(VCardVersion.V3_0));
			
			NameType name = new NameType();
			name.setGivenName(generalName.getValue());
			name.addHonorificPrefix(honorPrefix.getValue());
			vcard.setName(name);
			vcard.setFormattedName(new FormattedNameType("John \"Johny\" Doe"));
			
			TelephoneFeature mobileTelephone = new TelephoneType();
			if(generalMobilePhone.getValue()!=null){
				mobileTelephone.setTelephone(generalMobilePhone.getValue());
			}
			mobileTelephone.addTelephoneParameterType(TelephoneParameterType.CELL);
			vcard.addTelephoneNumber(mobileTelephone);
			
			TelephoneFeature homeTelephone = new TelephoneType();
			homeTelephone.setTelephone(generalHomePhone.getValue());
			homeTelephone.addTelephoneParameterType(TelephoneParameterType.HOME);
			vcard.addTelephoneNumber(homeTelephone);
			
			EmailFeature email = new EmailType();
			email.setEmail(generalEmail.getValue());
			email.addEmailParameterType(EmailParameterType.INTERNET);
			vcard.addEmail(email);
			
			URLFeature generalUrl = new URLType(generalWebsite.getValue());
			generalUrl.addURLParameterType(URLParameterType.PREF);
			vcard.addURL(generalUrl);	
			
			AddressFeature generalAddressFeature = new AddressType();
			generalAddressFeature.setCharset("UTF-8");
			generalAddressFeature.addAddressParameterType(AddressParameterType.PREF);
			generalAddressFeature.setCountryName(generalCountryRegion.getValue());
			generalAddressFeature.setLocality(generalCity.getValue());
			generalAddressFeature.setRegion(generalStateProvince.getValue());
			generalAddressFeature.setPostalCode(generalZipPostalCode.getValue());
			generalAddressFeature.setStreetAddress(generalAddress.getValue());
			vcard.addAddress(generalAddressFeature);
			
			
			AddressFeature privateAddressFeature = new AddressType();
			privateAddressFeature.setCharset("UTF-8");
			privateAddressFeature.addAddressParameterType(AddressParameterType.HOME);
			privateAddressFeature.setCountryName(privateCountryRegion.getValue());
			privateAddressFeature.setLocality(privateCity.getValue());
			privateAddressFeature.setRegion(privateStateProvince.getValue());
			privateAddressFeature.setPostalCode(privateZipPostal.getValue());
			privateAddressFeature.setStreetAddress(privateAddress.getValue());
			vcard.addAddress(privateAddressFeature);			
			Datebox dateBox = new Datebox(privateBirthday.getValue());
			Date date = dateBox.getValue();			
			Calendar birthday = Calendar.getInstance();
			birthday.clear();
			birthday.setTime(date);
			vcard.setBirthday(new BirthdayType(birthday));
			
			URLFeature privateUrl = new URLType(privateWebsite.getValue());
			privateUrl.addURLParameterType(URLParameterType.HOME);
			vcard.addURL(privateUrl);
			
			AddressFeature workAddressFeature = new AddressType();
			workAddressFeature.setCharset("UTF-8");
			workAddressFeature.addAddressParameterType(AddressParameterType.WORK);
			workAddressFeature.setCountryName(workCountryRegion.getValue());
			workAddressFeature.setLocality(workCity.getValue());
			workAddressFeature.setRegion(workStateProvince.getValue());
			workAddressFeature.setPostalCode(workZipPostalCode.getValue());
			workAddressFeature.setStreetAddress(workAddress.getValue());
			vcard.addAddress(workAddressFeature);
			
			TitleFeature jobTitle = new TitleType();
			jobTitle.setTitle(workJobTitle.getValue());
			vcard.setTitle(jobTitle);
			
			
			OrganizationFeature organizationFeature = new OrganizationType();
			organizationFeature.addOrganization(workDepartment.getValue());
			vcard.setOrganizations(organizationFeature);

			URLFeature workUrl = new URLType(workWebsite.getValue());
			workUrl.addURLParameterType(URLParameterType.WORK);
			vcard.addURL(workUrl);	
			
			EmailFeature facebookEmail = new EmailType();
			facebookEmail.addEmailParameterType(EmailParameterType.HOME);
			facebookEmail.setEmail(otherFacebook.getValue());			
			vcard.addEmail(facebookEmail);

			EmailFeature twitterEmail = new EmailType();
			twitterEmail.addEmailParameterType(EmailParameterType.TYPE);
			twitterEmail.setEmail(otherTwitter.getValue());			
			vcard.addEmail(twitterEmail);

			EmailFeature googlePlusEmail = new EmailType();
			googlePlusEmail.addEmailParameterType(EmailParameterType.WORK);
			googlePlusEmail.setEmail(otherGoogle.getValue());			
			vcard.addEmail(googlePlusEmail);
			
			ExtendedFeature otherOtherVal = new ExtendedType();
			otherOtherVal.setExtensionData(otherOther.getValue());
			otherOtherVal.setExtensionName("X-OTHERS");
			vcard.addExtendedType(otherOtherVal);
			
			ExtendedFeature otherCommentsVal = new ExtendedType();
			otherCommentsVal.setExtensionData(otherComments.getValue());
			otherCommentsVal.setExtensionName("X-COMMENTS");
			vcard.addExtendedType(otherCommentsVal);
			
			PhotoFeature photo = new PhotoType();
			photo.setCompression(true);
			photo.setEncodingType(EncodingType.BINARY);
			photo.setImageMediaType(ImageMediaType.PNG);
			byte[] tuxPicture = null;
			if(generalImage.getContent()!=null){
				tuxPicture = generalImage.getContent().getByteData();//Util.getFileAsBytes(new File(generalImage.getContext()));
				photo.setPhoto(tuxPicture);
				vcard.addPhoto(photo);				
			}
			
			VCardWriter writer =null;
			try{
				writer = new VCardWriter();
				writer.setVCard(vcard);			
				contactBean.setUserName(generalName.getValue());
				contactBean.setContactDetail(writer.buildVCardString());
				contactBean.save();
			}catch(Exception ex){
				Messagebox.show("Error :  "+ex.getMessage());
				return;
			}			
			Executions.sendRedirect(MainFilter.OID_MARKER + contactBean.getObjectkey());
		}
	}
	
	public static String parseVcardString(String vcardDetail){
		Logger.logInfo(MODULE, "Method : parseVcardString(String)");
		
		StringWriter allDetail 		= new StringWriter();		
		StringWriter generalDetail 	= new StringWriter();
		StringWriter privateDetail 	= new StringWriter();
		StringWriter workDetail 	= new StringWriter();
		StringWriter otherDetail 	= new StringWriter();
		
		VCardEngine engn = new  VCardEngine();
		Iterator it2 = null;
		Iterator it1 = null;
		
		try {
			
			VCard vcard = engn.parse(vcardDetail);
			
			it1 = vcard.getName().getHonorificPrefixes();
			while(it1.hasNext()){
				honorPrefixValue = (String)it1.next();
			}
			generalNameValue = vcard.getName().getGivenName();
		
			BufferedImage bImageFromConvert = null;
			it1= vcard.getPhotos();
			while(it1.hasNext()){
				PhotoFeature photo = (PhotoType)it1.next();
				byte[] bytes = photo.getPhoto();
				ByteArrayInputStream in = new ByteArrayInputStream(bytes);
				bImageFromConvert = ImageIO.read(in);
			}
			
			if(bImageFromConvert!=null){
				Image generalImage = new Image();
				generalImage.setContent(bImageFromConvert);
				generalImage.setParent(htmlViewPanel);
				Separator separator1= new Separator();
				separator1.setParent(htmlViewPanel);
			}
			generalImageValue = bImageFromConvert;

			it1 = vcard.getTelephoneNumbers();
			while(it1.hasNext()){
				TelephoneFeature teltype = (TelephoneType) it1.next();
				it2 = teltype.getTelephoneParameterTypes();
				while(it2.hasNext()){
					TelephoneParameterType type = (TelephoneParameterType)it2.next();
					if(type.equals(TelephoneParameterType.CELL)){ 
						generalMobilePhoneValue = teltype.getTelephone();
						
					}else if(type == TelephoneParameterType.HOME){
						generalHomePhoneValue = teltype.getTelephone();

					}
				}
			}
			
			it1 = vcard.getEmails();
			int i=0;
			while(it1.hasNext()){
				i++;
				EmailFeature emailtype = (EmailType) it1.next();
				it2 = emailtype.getEmailParameterTypes();
				
				while(it2.hasNext()){
					EmailParameterType type = (EmailParameterType)it2.next();
					if(type.equals(EmailParameterType.HOME)){
						otherFacebookValue = emailtype.getEmail();
					} else if(type.equals(EmailParameterType.TYPE)){
						otherTwitterValue = emailtype.getEmail();
					} else if(type.equals(EmailParameterType.WORK)){
						otherGoogleValue = emailtype.getEmail();
					} else if(type.equals(EmailParameterType.INTERNET)){
						generalEmailValue = emailtype.getEmail();
					} 
				}
			}
			
			it1= vcard.getExtendedTypes();
			while(it1.hasNext()){
				ExtendedFeature labelType = (ExtendedType) it1.next();
				if(labelType.getExtensionName().equalsIgnoreCase("X-OTHERS")){
					otherOtherValue = labelType.getExtensionData();					
					
				}else if(labelType.getExtensionName().equalsIgnoreCase("X-COMMENTS")){
					otherCommentsValue = labelType.getExtensionData();
					
				}
			}
			
			it2=null;
			it1 = vcard.getURLs();
			i=0;
			while(it1.hasNext()){
				i++;
				URLFeature urltype = (URLType)it1.next();
				if(urltype.getURL()!=null){
					switch(i){
					case 1:
						generalWebsiteValue = urltype.getURL().toString(); 
						break;
					case 2:
						privateWebsiteValue = urltype.getURL().toString(); 
						break;
					case 3:
						workWebsiteValue = urltype.getURL().toString(); 
						break;
					}
				}
			}		
			
			workJobTitleValue = vcard.getTitle().getTitle();
			OrganizationFeature organization = vcard.getOrganizations();
			it1 = organization.getOrganizations();
			if(it1.hasNext()){
				workDepartmentValue = (String)it1.next();
			}
			
			it1=null;
			it2=null;
			it1 = vcard.getAddresses();
			while(it1.hasNext()){
				AddressFeature addresstype = (AddressType)it1.next();
				it2 = addresstype.getAddressParameterTypes();
				while(it2.hasNext()){
					AddressParameterType type = (AddressParameterType)it2.next();
					if(type.equals(AddressParameterType.PREF)){ 
						generalAddressValue = addresstype.getStreetAddress();
						generalCityValue	= addresstype.getLocality();
						generalStateProvinceValue = addresstype.getRegion();
						generalZipPostalCodeValue = addresstype.getPostalCode();
						generalCountryRegionValue = addresstype.getCountryName();

					}else if(type.equals(AddressParameterType.HOME)){
						workAddressValue = addresstype.getStreetAddress();
						workCityValue	= addresstype.getLocality();
						workStateProvinceValue = addresstype.getRegion();
						workZipPostalCodeValue = addresstype.getPostalCode();
						workCountryRegionValue = addresstype.getCountryName();
						
				}else if(type.equals(AddressParameterType.WORK)){
						privateAddressValue = addresstype.getStreetAddress();
						privateCityValue	= addresstype.getLocality();
						privateStateProvinceValue 	= addresstype.getRegion();
						privateZipPostalValue 		= addresstype.getPostalCode();
						privateCountryRegionValue 	= addresstype.getCountryName();
					
					}
				}
			}
			
			
			privateBirthdayValue = vcard.getBirthDay().getBirthday().getTime();
			SimpleDateFormat  formatter = new SimpleDateFormat("YYYY-MM-DD");
			String result ="";
			try {
				SimpleDateFormat parseFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
				Date date = parseFormat.parse(privateBirthdayValue.toString());
				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				result = format.format(date);
				//birthDate = (Date)formatter.parse(formatter.format(privateBirthdayValue));
			} catch (ParseException e) {
				Logger.logError(MODULE, "Error while parsing date, Reason :"+e.getMessage());
			}
			generalDetail.append("<b><u>General Detail</u></b>");
			generalDetail.append("<br><b>Name			: </b>"+honorPrefixValue+" "+generalNameValue);
			generalDetail.append("<br><b>Mobile Phone	: </b>"+generalMobilePhoneValue);
			generalDetail.append("<br><b>Home Phone		: </b>"+generalHomePhoneValue);
			generalDetail.append("<br><b>Email			: </b>"+generalEmailValue);
			generalDetail.append("<br><b>Website		: </b>"+(generalWebsiteValue==null?"":generalWebsiteValue));
			generalDetail.append("<br><b>Address		: </b>"+generalAddressValue);
			generalDetail.append("<br><b>City			: </b>"+generalCityValue);
			generalDetail.append("<br><b>State/Province	: </b>"+generalStateProvinceValue);
			generalDetail.append("<br><b>Zip/PostalCode	: </b>"+generalZipPostalCodeValue);
			generalDetail.append("<br><b>Country/Region	: </b>"+generalCountryRegionValue);
		
			privateDetail.append("<br><br><b><u>Private Detail</u></b>");
			privateDetail.append("<br><b>Address		: </b>"+privateAddressValue);
			privateDetail.append("<br><b>City			: </b>"+privateCityValue);
			privateDetail.append("<br><b>State/Province	: </b>"+privateStateProvinceValue);
			privateDetail.append("<br><b>Zip/PostalCode	: </b>"+privateZipPostalValue);
			privateDetail.append("<br><b>Country/Region	: </b>"+privateCountryRegionValue);	
			privateDetail.append("<br><b>Website		: </b>"+(privateWebsiteValue==null?"":privateWebsiteValue));
			privateDetail.append("<br><b>Birthdate		: </b>"+result);

			workDetail.append("<br><br><b><u>Work Detail</u></b>");
			workDetail.append("<br><b>Job Title			: </b>"+workJobTitleValue);
			workDetail.append("<br><b>Department		: </b>"+workDepartmentValue);
			workDetail.append("<br><b>Address			: </b>"+workAddressValue);
			workDetail.append("<br><b>City				: </b>"+workCityValue);
			workDetail.append("<br><b>State/Province	: </b>"+workStateProvinceValue);
			workDetail.append("<br><b>Zip/PostalCode	: </b>"+workZipPostalCodeValue);
			workDetail.append("<br><b>Country/Region	: </b>"+workCountryRegionValue);	
			workDetail.append("<br><b>Website			: </b>"+(workWebsiteValue==null?"":workWebsiteValue));

			workDetail.append("<br><br><b><u>Work Detail</u></b>");
			otherDetail.append("<br><b>Facebook		: </b>"+otherFacebookValue);
			otherDetail.append("<br><b>Twitter		: </b>"+otherTwitterValue);						
			otherDetail.append("<br><b>Google+		: </b>"+otherGoogleValue);
			otherDetail.append("<br><b>Others		: </b>"+otherOtherValue);
			otherDetail.append("<br><b>Comments		: </b>"+otherCommentsValue);

			allDetail.append(generalDetail.toString());
			allDetail.append(privateDetail.toString());
			allDetail.append(workDetail.toString());
			allDetail.append(otherDetail.toString());
			return allDetail.toString();
		} catch (IOException e) {
			Logger.logError(MODULE, "Error : "+e.getMessage());
			return allDetail.toString();
		}
		
	}
}
