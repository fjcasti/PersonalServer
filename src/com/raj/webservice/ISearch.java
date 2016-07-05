package com.raj.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface ISearch {

	@WebMethod(operationName="wsGetRecentSearchItemsList")
	public String getRecentSearchItemsList();
	
	
	
	@WebMethod(operationName="wsGetObjectDetailByID")
	public String getObjectDetailByID(@WebParam(name="objectType")String objectType,@WebParam(name="objectID")String objectID);
	
}
