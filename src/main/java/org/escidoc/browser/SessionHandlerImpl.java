package org.escidoc.browser;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;


public class SessionHandlerImpl extends HttpServlet  {
	HttpSession session;

	public void doGet(HttpServletRequest req, HttpServletResponse res) 
	throws IOException, ServletException {
		res.setContentType("text/html");
		this.session = req.getSession(true);
		if(session.isNew()) {
			session.setAttribute("armand", "WHATEVER");
		}
	}



	public void setSession(String sessionName, String sessValue){
		this.session.setAttribute(sessionName, sessValue);
	}

	public String getSessionValue(String sessionName){
		return (String) session.getAttribute(sessionName);

	}
	public HttpSession getSession() {
		return session;
	}
	public void setSession(HttpSession session) {
		this.session = session;
	}

}
