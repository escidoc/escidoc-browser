package org.escidoc.browser.ui.mainpage;

import org.escidoc.browser.ui.MainSite;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;


/**
 * This is the Header of the page.
 * Main Search comes here, also the login ability
 * @author ARB
 *
 */
public class HeaderContainer extends VerticalLayout {   
	private final Button login;
	private MainSite mainSite;
	private int appHeight;

	public HeaderContainer(MainSite mainSite, int appHeight) {
		this.mainSite=mainSite;
		this.appHeight = appHeight;
		// found at myTheme/layouts/header.html
		final CustomLayout custom = new CustomLayout("header");
		addComponent(custom);


		// Login
		this.login = new Button("Login", this, "onClick");
		this.login.setStyleName(BaseTheme.BUTTON_LINK);
		this.login.setWidth("60px");
		this.login.setHeight("15px");
		//login.setCaption("Login");
		this.login.setImmediate(true);

		custom.addComponent(login, "login");

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


}
