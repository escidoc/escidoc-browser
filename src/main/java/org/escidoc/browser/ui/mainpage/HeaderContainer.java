package org.escidoc.browser.ui.mainpage;

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.ViewConstant;
import org.escidoc.browser.ui.listeners.LogoutListener;

import com.google.common.base.Preconditions;
import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

/**
 * This is the Header of the page. Main Search comes here, also the login
 * ability
 * 
 * @author ARB
 * 
 */
@SuppressWarnings("serial")
public class HeaderContainer extends VerticalLayout {

    // The HTML file can be found at myTheme/layouts/header.html
    private final CustomLayout custom = new CustomLayout(ViewConstant.HEADER);

    private Button login;

    private Button logout;

    private final ClickListener logoutListener;

    private final Application app;

    private final EscidocServiceLocation serviceLocation;

    public HeaderContainer(final MainSite mainSite, final int appHeight,
        final BrowserApplication app,
        final EscidocServiceLocation serviceLocation) {
        Preconditions.checkNotNull(app, "app is null: %s", app);
        Preconditions.checkNotNull(serviceLocation,
            "serviceLocation is null: %s", serviceLocation);
        this.app = app;
        this.serviceLocation = serviceLocation;
        logoutListener = new LogoutListener(app);
    }

    public void init() {
        addCustomLayout();
        addLoginComponent();
        addLogoutComponent();
    }

    private void addCustomLayout() {
        addComponent(custom);
    }

    private void addLoginComponent() {
        login = new Button(ViewConstant.LOGIN, this, "onClick");
        login.setStyleName(BaseTheme.BUTTON_LINK);
        login.setWidth("60px");
        login.setHeight("15px");
        login.setImmediate(true);
        custom.addComponent(login, "login");
    }

    private void addLogoutComponent() {
        logout = new Button(ViewConstant.LOGOUT, logoutListener);
        logout.setStyleName(BaseTheme.BUTTON_LINK);
        logout.setWidth("60px");
        logout.setHeight("15px");
        logout.setImmediate(true);
        custom.addComponent(logout, "logout");
    }

    /**
     * Handle the Login Event! At the moment a new window is opened to escidev6
     * for login TODO consider including the window of login from the remote
     * server in a iframe within the MainContent Window
     * 
     * @param event
     */
    public void onClick(final Button.ClickEvent event) {
        redirectToLoginView();
        // login.setCaption(ViewConstant.LOGOUT);
    }

    private void redirectToLoginView() {
        getWindow().open(new ExternalResource(serviceLocation.getLoginUri()));
    }
}