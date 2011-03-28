package org.escidoc.browser.ui.mainpage;

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.SessionHandlerImpl;
import org.escidoc.browser.ui.MainSite;

import com.vaadin.service.ApplicationContext;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;


/**
 * This is the Header of the page.
 * Main Search comes here, also the login ability
 * @author ARB
 *
 */
public class HeaderContainer extends VerticalLayout {   
	private Button login;
	private Button logout;
	private MainSite mainSite;
	private int appHeight;
	private BrowserApplication app;

	public HeaderContainer(MainSite mainSite, int appHeight, BrowserApplication app) {
		this.mainSite=mainSite;
		this.appHeight = appHeight;
		this.app= app;
		
		buildheader();
	}
	
	private void buildheader(){
		// found at myTheme/layouts/header.html
		final CustomLayout custom = new CustomLayout("header");
		addComponent(custom);


		// Logout
		this.logout = new Button("Log Out", this, "onClickLogout");
		this.logout.setStyleName(BaseTheme.BUTTON_LINK);
		this.logout.setWidth("60px");
		this.logout.setHeight("15px");
		this.logout.setImmediate(true);

		// Login
		this.login = new Button("Login", this, "onClick");
		this.login.setStyleName(BaseTheme.BUTTON_LINK);
		this.login.setWidth("60px");
		this.login.setHeight("15px");
		this.login.setImmediate(true);
			SessionHandlerImpl session = app.getSessionHandler();

		
		Label lbl = new Label("test");
		custom.addComponent(lbl,"test "+(String)session.geteSDBHandlervalue());
		
		
		System.out.println ("Header "+session.geteSDBHandlervalue());
		//if (app.isLoggedin())
			custom.addComponent(login, "login");
		//else
			//custom.addComponent(logout, "login");

		}

		/**
		 * Handle the Login Event!
		 * At the moment a new window is opened to escidev6 for login
		 * TODO consider including the window of login from the remote server in a 
		 * iframe within the MainContent Window
		 * @param event
		 */
		public void onClick(Button.ClickEvent event) {
			this.getWindow().open(new ExternalResource("http://escidev6.fiz-karlsruhe.de:8080/aa/login?target=http://localhost:8084/browser/s#HandleLogin"));
			this.login.setCaption("Loggedin!");

		}

		public void onClickLogout(Button.ClickEvent event) {
			BrowserApplication app = (BrowserApplication)(this.getApplication());
			app.getSessionHandler().doLogout();		
			this.login.setCaption("Login!");
			this.login.detach();

		}


	}
