/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or https://www.escidoc.org/license/ESCIDOC.LICENSE .
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *
 * Copyright ${year} Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
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

import com.google.common.base.Preconditions;
import com.vaadin.Application;
import com.vaadin.Application.UserChangeEvent;
import com.vaadin.Application.UserChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.PopupVisibilityEvent;
import com.vaadin.ui.PopupView.PopupVisibilityListener;
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
public class HeaderContainer extends VerticalLayout implements UserChangeListener, PopupVisibilityListener {

    // The HTML file can be found at myTheme/layouts/header.html
    private final CustomLayout custom = new CustomLayout(ViewConstants.HEADER);

    private final TextField searchField = new TextField(ViewConstants.SEARCH);

    private Button login;

    private Button logout;

    private final Application app;

    private final EscidocServiceLocation serviceLocation;

    private final CurrentUser user;

    private final MainSite mainSite;

    private final int appHeight;

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
        this.setMargin(false);

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
        custom.addComponent(new Label("<b>" + ViewConstants.CURRENT_USER + user.getLoginName() + "</b>",
            Label.CONTENT_XHTML), "login-name");

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
        final Form form = new Form();
        searchField.setImmediate(true);
        form.getLayout().addComponent(searchField);
        custom.addComponent(form, "form");

        final Button btnSearch = new Button("Go", this, "onClickSearch");
        btnSearch.removeStyleName("v-button");
        custom.addComponent(btnSearch, "btnSearch");

        // Create the content for the popup
        Label content =
            new Label(
                "<ul><li>&raquo; The default search operator is OR</li><li>&raquo; To search for a phrase place the text in double quotes</li></ul>",
                Label.CONTENT_RAW);
        // The PopupView popup will be as large as needed by the content
        content.setWidth("300px");

        // Construct the PopupView with simple HTML text representing the
        // minimized view
        PopupView popup = new PopupView("?", content);
        popup.setHideOnMouseOut(true);
        popup.addListener(this);
        custom.addComponent(popup, "searchTip");

    }

    /**
     * Handle the Login Event! At the moment a new window is opened to escidev6 for login TODO consider including the
     * window of login from the remote server in a iframe within the MainContent Window
     * 
     * @param event
     */
    public void onClick(final Button.ClickEvent event) {
        redirectToLoginView();
    }

    public void onClickSearch(final Button.ClickEvent event) {
        final String searchString = (String) searchField.getValue();
        if (validate(searchString)) {
            final SearchResultsView srchRes = new SearchResultsView(mainSite, searchString, serviceLocation);
            mainSite.openTab(srchRes, "Search results for: " + (String) searchField.getValue());
        }
        else {
            searchField.setComponentError(new UserError("Must be letters and numbers"));
        }
    }

    /**
     * Handle Search Query Validation Check string length Any possible injections
     * 
     * @param searchString
     * @return boolean
     */
    private boolean validate(final String searchString) {
        final Pattern p = Pattern.compile("[A-Za-z0-9_.\\s\":#]{3,}");
        final Matcher m = p.matcher(searchString);
        return m.matches();
    }

    private void redirectToLoginView() {
        getWindow().open(new ExternalResource(serviceLocation.getLoginUri()));
    }

    @Override
    public void applicationUserChanged(final UserChangeEvent event) {
        final Object object = event.getNewUser();
        if (!(object instanceof CurrentUser)) {
            return;
        }
        custom.removeAllComponents();
        custom.addComponent(new Label("<b>" + ViewConstants.CURRENT_USER + user.getLoginName() + "</b>",
            Label.CONTENT_XHTML), "login-name");
        if (((CurrentUser) object).isGuest()) {
            custom.addComponent(login, "login");
        }
        else {
            custom.addComponent(logout, "logout");
        }
    }

    @Override
    public void popupVisibilityChange(PopupVisibilityEvent event) {
        // if (!event.isPopupVisible()) {
        // getWindow().showNotification("Popup closed");
        // }
    }

}