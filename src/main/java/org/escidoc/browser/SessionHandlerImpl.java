package org.escidoc.browser;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;
import com.vaadin.terminal.gwt.server.WebApplicationContext;


public class SessionHandlerImpl  {
	HttpSession session;

	public SessionHandlerImpl(HttpSession session) {
		this.session = session;
	}

	/**
	 * Here we set a cookie in case of a succesfull login
	 * The cookie should contain the username?
	 * TODO Ask Michael for description of is inside the esceidochandler
	 */
	public void doLogin(String handler){
		session.setAttribute("eSDB-handler", handler);
		System.out.println("Setting the cookie, doch" + handler);
	}
	public String geteSDBHandlervalue(){
		try  {
			return (String) session.getAttribute("eSDB-handler");
		}catch(Exception e){
			return "Problem";
		}
	}
	public boolean isLoggedin(){
		if (session.getAttribute("eSDB-handler") == null)
			return false;
		else
			return true;
		//session.getAttribute("eSDB-handler");
	}

	public void doLogout() {
		session.setAttribute("eSDB-handler", null);		
	}

	

}
