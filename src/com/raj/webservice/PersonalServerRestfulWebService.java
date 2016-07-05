package com.raj.webservice;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Service;

@Service("PersonalServerRestfulWebService")
public class PersonalServerRestfulWebService  implements IPersonalServerRestfulWebService{
	ILogin iLogin;
	ISearch iSearch;
	public PersonalServerRestfulWebService(){
		iLogin =  new LoginWS();
		iSearch =  new SearchWS();
	}
	
	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("wsGetGreetings")
	public String getGreetings() {
		return iLogin.getGreetings();
	}
	
	@Override
	@POST
	@Path("wsDoAuthentication")
	public boolean wsDoAuthentication(@FormParam("username")String username,@FormParam("password")String password) {
		return iLogin.wsDoAuthentication(username, password);
	}
	
	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("wsGetRecentSearchItemsList")
	public String getRecentSearchItemsList() {
		return iSearch.getRecentSearchItemsList();
	}

	@Override
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("wsGetObjectDetailByID")
	public String getObjectDetailByID(@FormParam("objectType")String objectType,@FormParam("objectID")String objectID) {
		return iSearch.getObjectDetailByID(objectType,objectID);
	}
}
