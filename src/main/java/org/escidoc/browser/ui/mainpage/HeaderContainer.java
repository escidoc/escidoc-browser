package org.escidoc.browser.ui.mainpage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.LogoutListener;
import org.escidoc.browser.ui.maincontent.SearchResultsView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.Application;
import com.vaadin.Application.UserChangeEvent;
import com.vaadin.Application.UserChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Form;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

/**
 * This is the Header of the page. Main Search comes here, also the login ability
 * 
 * @author ARB
 * 
 */
@SuppressWarnings("serial")
public class HeaderContainer extends VerticalLayout implements UserChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(BrowserApplication.class);

    // The HTML file can be found at myTheme/layouts/header.html
    private final CustomLayout custom = new CustomLayout(ViewConstants.HEADER);

    private Button login;

    private Button logout;

    private final Application app;

    private final EscidocServiceLocation serviceLocation;

    private final CurrentUser user;

    private final MainSite mainSite;

    private final int appHeight;

    TextField srchField = new TextField("Search");

    public HeaderContainer(final MainSite mainSite, final int appHeight, final BrowserApplication app,
        final EscidocServiceLocation serviceLocation, final CurrentUser user) {

        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);
        Preconditions.checkArgument(appHeight > 0, "appHeight is zero or negative: %s");
        Preconditions.checkNotNull(app, "app is null: %s", app);
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(user, "user is null: %s", user);

        this.app = app;
        this.serviceLocation = serviceLocation;
        this.user = user;
        this.mainSite = mainSite;
        this.appHeight = appHeight;

    }

    public void init() {
        app.addListener(this);
        addCustomLayout();
        createLoginComponent();
        createLogoutComponent();
        createSearchForm();
        setUser(user);
    }

    public void setUser(final CurrentUser user) {
        if (user.isGuest()) {
            custom.removeComponent(logout);
            custom.addComponent(login, "login");
        }
        else {
            custom.addComponent(logout, "logout");
        }
    }

    private void addCustomLayout() {
        addComponent(custom);
    }

    private void createLoginComponent() {
        login = new Button(ViewConstants.LOGIN, this, "onClick");
        configureButton(login);
    }

    private void configureButton(final Button button) {
        button.setStyleName(BaseTheme.BUTTON_LINK);
        button.setWidth("60px");
        button.setHeight("15px");
        button.setImmediate(true);
    }

    private void createLogoutComponent() {
        logout = new Button(ViewConstants.LOGOUT, new LogoutListener(app));
        configureButton(logout);
    }

    private void createSearchForm() {

        Form form = new Form();
        // form.setWidth("200px");
        srchField.setImmediate(true);
        form.getLayout().addComponent(srchField);
        custom.addComponent(form, "form");

        Button btnSearch = new Button("Go", this, "onClickSearch");
        btnSearch.removeStyleName("v-button");
        custom.addComponent(btnSearch, "btnSearch");
    }

    /**
     * Handle the Login Event! At the moment a new window is opened to escidev6 for login TODO consider including the
     * window of login from the remote server in a iframe within the MainContent Window
     * 
     * @param event
     */
    public void onClick(final Button.ClickEvent event) {
        redirectToLoginView();
        // login.setCaption(ViewConstant.LOGOUT);
    }

    public void onClickSearch(final Button.ClickEvent event) {
        String searchString = (String) srchField.getValue();
        if (validate(searchString)) {
            SearchResultsView srchRes = new SearchResultsView(mainSite, appHeight, searchString, serviceLocation);
            mainSite.openTab(srchRes, "Search results for: " + (String) srchField.getValue());
        }
        else {
            srchField.setComponentError(new UserError("Must be letters and numbers"));
        }
        // login.setCaption(ViewConstant.LOGOUT);
    }

    /**
     * Handle Search Query Validation Check string length Any possible injections
     * 
     * @param searchString
     * @return boolean
     */
    private boolean validate(String searchString) {
        Pattern p = Pattern.compile("[A-Za-z0-9_.\\s]{3,}");
        Matcher m = p.matcher(searchString);
        if (m.matches())
            return true;
        return false;
    }

    private void redirectToLoginView() {
        getWindow().open(new ExternalResource(serviceLocation.getLoginUri()));
    }

    @Override
    public void applicationUserChanged(final UserChangeEvent event) {
        final Object newUser = event.getNewUser();
        if (!(newUser instanceof CurrentUser)) {
            return;
        }

        if (((CurrentUser) newUser).isGuest()) {
            custom.removeAllComponents();
            custom.addComponent(login, "login");
        }
        else {
            custom.removeAllComponents();
            custom.addComponent(logout, "logout");
        }

    }
}