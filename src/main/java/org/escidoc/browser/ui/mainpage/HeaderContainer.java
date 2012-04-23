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
 * Copyright 2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui.mainpage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.controller.UserAccountController;
import org.escidoc.browser.layout.LayoutDesign;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.LogoutListener;
import org.escidoc.browser.ui.maincontent.SearchResultsView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.Application.UserChangeEvent;
import com.vaadin.Application.UserChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.PopupVisibilityEvent;
import com.vaadin.ui.PopupView.PopupVisibilityListener;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

import de.escidoc.core.client.exceptions.EscidocClientException;

/**
 * This is the Header of the page. Main Search comes here, also the login ability
 * 
 * @author ARB
 * 
 */
@SuppressWarnings("serial")
public class HeaderContainer extends VerticalLayout implements UserChangeListener, PopupVisibilityListener {

    private final static Logger LOG = LoggerFactory.getLogger(HeaderContainer.class);

    // The HTML file can be found at myTheme/layouts/header.html
    private final CustomLayout custom = new CustomLayout(ViewConstants.HEADER);

    private final TextField searchField = new TextField(ViewConstants.SEARCH);

    private Button login;

    private Button logout;

    private final BrowserApplication app;

    private final EscidocServiceLocation serviceLocation;

    private final CurrentUser user;

    private final Repositories repositories;

    private final LayoutDesign layout;

    private final Router router;

    private HorizontalLayout headerContainer;

    private HorizontalLayout hl;

    public HeaderContainer(final Router router, final LayoutDesign layout, final BrowserApplication app,
        final EscidocServiceLocation serviceLocation, final Repositories repositories) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(layout, "mainSite is null: %s", layout);
        Preconditions.checkNotNull(app, "app is null: %s", app);
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        this.router = router;
        this.app = app;
        this.serviceLocation = serviceLocation;
        this.user = app.getCurrentUser();
        this.layout = layout;
        this.repositories = repositories;
        this.setMargin(false);

    }

    public void init() {
        app.addListener(this);
        addNormalLayout();
        // addCustomLayout();
        createLoginComponent();
        createLogoutComponent();
        createSearchForm();
        setUser(user);
    }

    private void addNormalLayout() {
        headerContainer = new HorizontalLayout();
        headerContainer.setStyleName(Reindeer.LAYOUT_WHITE);
        headerContainer.setWidth("100%");
        headerContainer.setHeight("55px");
        headerContainer.addStyleName("header");
        HorizontalLayout hlLogo = new HorizontalLayout();

        Resource res = new ThemeResource("images/escidoc-logo-klein.jpg");
        Embedded e = new Embedded(null, res);
        e.setHeight(50 + "px");
        hlLogo.addComponent(e);

        headerContainer.addComponent(hlLogo);
        headerContainer.setExpandRatio(hlLogo, 0.2f);

        hl = new HorizontalLayout();
        headerContainer.addComponent(hl);
        headerContainer.setExpandRatio(hl, 0.8f);
        this.addComponent(headerContainer);
        headerContainer.setComponentAlignment(hl, Alignment.MIDDLE_RIGHT);

    }

    public void setUser(final CurrentUser user) {
        Component name;
        if (user.getLoginName().equals(ViewConstants.GUEST)) {
            name = new Label(ViewConstants.GUEST);
        }
        else {
            name = new Button(user.getLoginName());
            name.setStyleName(BaseTheme.BUTTON_LINK);
            name.addStyleName("paddingright10");
            ((Button) name).addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(@SuppressWarnings("unused")
                ClickEvent event) {
                    ResourceProxy userProxy;
                    try {
                        userProxy = repositories.user().findById(user.getUserId());
                        UserAccountController controller = new UserAccountController(repositories, router, userProxy);
                        router.openControllerView(controller, Boolean.FALSE);
                    }
                    catch (EscidocClientException e) {
                        LOG.error("Can not find user " + e.getMessage());
                        router.getMainWindow().showNotification(ViewConstants.ERROR, e.getMessage(),
                            Window.Notification.TYPE_ERROR_MESSAGE);
                    }
                }
            });
        }

        custom.addComponent(new Label("<b>" + ViewConstants.CURRENT_USER + "</b>", Label.CONTENT_XHTML), "login-name");
        Label lblCurrentUser = new Label("<b>" + ViewConstants.CURRENT_USER + "</b>", Label.CONTENT_XHTML);
        lblCurrentUser.addStyleName("paddingright10");
        hl.addComponent(lblCurrentUser);
        hl.setComponentAlignment(lblCurrentUser, Alignment.MIDDLE_RIGHT);

        name.addStyleName("paddingright10");
        custom.addComponent(name, "username");
        hl.addComponent(name);
        hl.setComponentAlignment(name, Alignment.MIDDLE_RIGHT);

        if (user.isGuest()) {
            custom.removeComponent(logout);
            hl.removeComponent(logout);
            custom.addComponent(login, "login");
            hl.addComponent(login);
            hl.setComponentAlignment(login, Alignment.MIDDLE_RIGHT);

        }
        else {
            custom.addComponent(logout, "logout");
            logout.addStyleName("paddingright10");
            hl.addComponent(logout);
            hl.setComponentAlignment(logout, Alignment.MIDDLE_RIGHT);

        }
    }

    private void addCustomLayout() {
        addComponent(custom);
    }

    private void createLoginComponent() {
        login = new Button(ViewConstants.LOGIN, this, "onClick");
        configureButton(login);
    }

    private static void configureButton(final Button button) {
        button.setStyleName(BaseTheme.BUTTON_LINK);
        button.setWidth("60px");
        button.setHeight("15px");
        button.setImmediate(true);
        button.addStyleName("paddingright10");
    }

    private void createLogoutComponent() {
        logout = new Button(ViewConstants.LOGOUT, new LogoutListener(app));
        configureButton(logout);
        logout.addStyleName("paddingleft10");
    }

    private void createSearchForm() {
        final Form form = new Form();
        searchField.setImmediate(true);
        form.getLayout().addComponent(searchField);
        form.addStyleName("paddingright10");
        custom.addComponent(form, "form");
        hl.addComponent(form);
        hl.setComponentAlignment(form, Alignment.MIDDLE_RIGHT);

        final Button btnSearch = new Button("Go", this, "onClickSearch");
        btnSearch.setClickShortcut(KeyCode.ENTER);
        btnSearch.addStyleName("primary");
        btnSearch.removeStyleName("v-button");
        custom.addComponent(btnSearch, "btnSearch");
        btnSearch.addStyleName("paddingright10");
        hl.addComponent(btnSearch);
        hl.setComponentAlignment(btnSearch, Alignment.MIDDLE_RIGHT);

        // Create the content for the popup
        final Label content =
            new Label(
                "<ul><li>&raquo; The default search operator is AND</li><li>&raquo; To search for a phrase place the text in double quotes</li></ul>",
                Label.CONTENT_RAW);
        // The PopupView popup will be as large as needed by the content
        content.setWidth("300px");

        // Construct the PopupView with simple HTML text representing the
        // minimized view
        final PopupView popup = new PopupView("?", content);
        popup.setHideOnMouseOut(true);
        popup.addListener(this);
        custom.addComponent(popup, "searchTip");
        popup.addStyleName("paddingright10");
        hl.addComponent(popup);
        hl.setComponentAlignment(popup, Alignment.MIDDLE_RIGHT);
    }

    /**
     * Handle the Login Event! At the moment a new window is opened to escidev6 for login TODO consider including the
     * window of login from the remote server in a iframe within the MainContent Window
     * 
     * @param event
     */
    public void onClick(@SuppressWarnings("unused")
    final Button.ClickEvent event) {
        redirectToLoginView();
    }

    public void onClickSearch(final Button.ClickEvent event) {
        final String searchString = (String) searchField.getValue();
        if (validate(searchString)) {
            Preconditions.checkNotNull(router, "router is null: %s", router);
            router.openTab(new SearchResultsView(router, layout, searchString, serviceLocation, repositories),
                "Search results for: " + (String) searchField.getValue());
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
    private static boolean validate(final String searchString) {
        final Pattern p = Pattern.compile("[A-Za-z0-9_.\\s\":#]{3,}");
        final Matcher m = p.matcher(searchString);
        return m.matches();
    }

    private void redirectToLoginView() {
        getWindow().open(new ExternalResource(serviceLocation.getLoginUri(this.getApplication().getURL().toString())));
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
    public void popupVisibilityChange(final PopupVisibilityEvent event) {
        // if (!event.isPopupVisible()) {
        // getWindow().showNotification("Popup closed");
        // }
    }

}