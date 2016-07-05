package com.raj.webservice;

import com.jsantos.ps.finder.FinderController2;
import com.jsantos.ps.objects.contact.ContactPageController;
import com.jsantos.ps.objects.tag.TagViewerController;
import com.jsantos.ps.objects.wiki.WikiPageController;
import com.jsantos.ps.webapp.Constants;
import com.jsantos.util.logger.Logger;

public class SearchWS implements ISearch {

	private static final String CLASS = SearchWS.class.getSimpleName();

	@Override
	public String getRecentSearchItemsList() {
		return FinderController2.recentSeearchNodeItemsList();//"HI BEAUTIFUL";//finder.recentSeearchNodeItemsList();
	}

	@Override
	public String getObjectDetailByID(String objectType, String objectID) {
		Logger.logInfo(CLASS, " Method Called :getObjectDetailByID() ");
		Logger.logInfo(CLASS, " objectType 	: "+objectType);
		Logger.logInfo(CLASS, " objectID 	: "+objectID);
		
		if(objectType.equalsIgnoreCase(Constants.CONTACT)){
			return ContactPageController.findObjectDetailByID(objectID);
			
		}else if(objectType.equalsIgnoreCase(Constants.HTML)){
			return WikiPageController.findObjectDetailByID(objectID);
			
		}else if(objectType.equalsIgnoreCase(Constants.FILEREF)){
			
		}else if(objectType.equalsIgnoreCase(Constants.MAIL)){
			
		}else if(objectType.equalsIgnoreCase(Constants.TAG)){
			return TagViewerController.findObjectDetailByID(objectID);
			
		}else if(objectType.equalsIgnoreCase(Constants.SAVED_SEARCH)){
			
		}else{
			return "No detail available";
		}
		return "Item not Recognized";
	}
}
