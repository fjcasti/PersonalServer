package com.raj.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface ILogin {

	@WebMethod(operationName="wsDoAuthentication")
	public boolean wsDoAuthentication(@WebParam(name="username")String username,@WebParam(name="password")String password);
	
	@WebMethod(operationName="wsGetGreetings")
	public String getGreetings();
}
