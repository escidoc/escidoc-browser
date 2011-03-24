package org.escidoc.browser.ui.mainpage;

import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.maincontent.SearchResults;
import org.escidoc.browser.ui.maincontent.SearchSimple;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;


/**
 * This is the Header of the page.
 * Main Search comes here, also the login ability
 * @author ARB
 *
 */
public class HeaderContainer extends VerticalLayout implements Property.ValueChangeListener {   
	private final TextField searchfld= new TextField();
	private final Button login;
	private MainSite mainSite;
	private int appHeight;

	public HeaderContainer(MainSite mainSite, int appHeight) {
		this.mainSite=mainSite;
		this.appHeight = appHeight;
		// found at myTheme/layouts/header.html
		final CustomLayout custom = new CustomLayout("header");
		addComponent(custom);

		searchfld.setInputPrompt("Search");
		searchfld.addListener(this);

		searchfld.setWidth("100px");
		searchfld.setHeight("20px");
		searchfld.setImmediate(true);

		// Login
		this.login = new Button("Login", this, "onClick");
		this.login.setStyleName(BaseTheme.BUTTON_LINK);
		this.login.setWidth("60px");
		this.login.setHeight("15px");
		//login.setCaption("Login");
		this.login.setImmediate(true);

		custom.addComponent(login, "login");
		custom.addComponent(searchfld, "searchfld");

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

	/**
	 * Handle the event from the Search Form
	 * Normally call the main MainContent Window, the element TabSheet
	 * and bind to it
	 */
	@Override
	public void valueChange(ValueChangeEvent event) {

		SearchResults smpSearch = new SearchResults(mainSite, appHeight);
		this.mainSite.openTab(smpSearch, "Search Results");
		this.getWindow().showNotification("WHAT UP"+(String) searchfld.getValue());

	}

}
