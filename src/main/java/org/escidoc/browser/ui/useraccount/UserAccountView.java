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
package org.escidoc.browser.ui.useraccount;

import java.net.URISyntaxException;

import org.escidoc.browser.controller.UserAccountController;
import org.escidoc.browser.model.internal.UserProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.UserAccountRepository;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.View;
import org.escidoc.browser.ui.view.helpers.ResourceProperties;
import org.escidoc.browser.ui.view.helpers.ResourcePropertiesUserAccountView;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.aa.useraccount.Attribute;
import de.escidoc.core.resources.aa.useraccount.Grant;

@SuppressWarnings("serial")
public class UserAccountView extends View {

    Router router;

    UserProxy userProxy;

    UserAccountRepository ur;

    private UserAccountController uac;

    private Panel attributePanel;

    private Button addPreferenceButton;

    private ResourceProperties resoucePropertiesView;

    private Repositories repositories;

    public UserAccountView(Router router, UserProxy userProxy, UserAccountRepository ur, UserAccountController uac) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(userProxy, "userProxy is null: %s", userProxy);
        Preconditions.checkNotNull(ur, "ur is null: %s", ur);
        Preconditions.checkNotNull(uac, "uac is null: %s", uac);

        this.router = router;
        this.userProxy = userProxy;
        this.ur = ur;
        this.uac = uac;
        this.repositories = router.getRepositories();
        init();
    }

    public void init() {
        setImmediate(false);
        setStyleName(Runo.PANEL_LIGHT);
        Panel contentPanel = createContentPanel();
        contentPanel.setContent(buildVlContentPanel());
        setContent(contentPanel);
    }

    private static final class OnRemoveGrant implements Button.ClickListener {

        private Grant grant;

        private UserProxy userProxy;

        private Repositories repos;

        private Window mainWindow;

        public OnRemoveGrant(Grant grant, UserProxy userProxy, Repositories repos, Window mainWindow) {
            Preconditions.checkNotNull(grant, "grant is null: %s", grant);
            Preconditions.checkNotNull(userProxy, "userProxy is null: %s", userProxy);
            Preconditions.checkNotNull(repos, "repo is null: %s", repos);
            Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);

            this.grant = grant;
            this.repos = repos;
            this.userProxy = userProxy;
            this.mainWindow = mainWindow;
        }

        @Override
        public void buttonClick(final ClickEvent event) {
            try {
                revokeGrantInServer();
                updateView(event);
                showSuccessMessage();
            }
            catch (EscidocClientException e) {
                showErrorMessage(e);
            }
        }

        private void showErrorMessage(EscidocClientException e) {
            mainWindow.showNotification("Error Message", "Something wrong happens. Cause: " + e.getMessage(),
                Notification.TYPE_ERROR_MESSAGE);
        }

        private void showSuccessMessage() {
            mainWindow.showNotification("",
                "Sucessfully revoke " + grant.getXLinkTitle() + " from " + userProxy.getName(),
                Notification.TYPE_TRAY_NOTIFICATION);
        }

        private void revokeGrantInServer() throws EscidocClientException {
            repos.user().revokeGrant(userProxy.getId(), grant);
        }

        private static void updateView(final ClickEvent event) {
            VerticalLayout component = (VerticalLayout) event.getButton().getParent().getParent();
            component.removeComponent(event.getButton().getParent());
        }
    }

    private final class OnAddAttribute implements ClickListener {

        private final Button addAttributeButton;

        private final UserAccountAttributes attributeTable;

        private OnAddAttribute(Button addAttributeButton, UserAccountAttributes attributeTable) {
            this.addAttributeButton = addAttributeButton;
            this.attributeTable = attributeTable;
        }

        @Override
        public void buttonClick(@SuppressWarnings("unused")
        final com.vaadin.ui.Button.ClickEvent event) {
            addAttributeButton.setEnabled(false);
            final HorizontalLayout hl = new HorizontalLayout();
            final TextField key = new TextField();
            key.setCaption("Name");
            key.setImmediate(false);
            key.setWidth("-1px");
            key.setHeight("-1px");
            key.setInvalidAllowed(false);
            key.setRequired(true);

            final TextField value = new TextField();
            value.setCaption("Value");
            value.setImmediate(false);
            value.setWidth("-1px");
            value.setHeight("-1px");
            value.setInvalidAllowed(false);
            value.setRequired(true);

            final Button btnadd = new Button();
            btnadd.setIcon(new ThemeResource("images/assets/plus.png"));
            btnadd.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(@SuppressWarnings("unused")
                final com.vaadin.ui.Button.ClickEvent event) {
                    if (isNotValid(key, value)) {
                        showMessage();
                    }
                    else {
                        try {
                            ur.createAttribute(userProxy, new Attribute(key.getValue().toString(), value
                                .getValue().toString()));
                            router.getMainWindow().showNotification("Attribute added successfully ",
                                Window.Notification.TYPE_TRAY_NOTIFICATION);
                            hl.removeAllComponents();
                            addAttributeButton.setEnabled(true);
                            attributeTable.createItem(attributeTable.getTableContainer(), key.getValue().toString(),
                                key.getValue().toString(), value.getValue().toString());
                        }
                        catch (final EscidocClientException e) {
                            router.getMainWindow().showNotification(
                                ViewConstants.ERROR_CREATING_USER_ATTRIBUTE + e.getLocalizedMessage(),
                                Window.Notification.TYPE_ERROR_MESSAGE);
                        }
                    }
                }
            });
            hl.addComponent(key);
            hl.addComponent(value);
            hl.addComponent(btnadd);
            hl.setComponentAlignment(btnadd, Alignment.BOTTOM_RIGHT);
            attributePanel.addComponent(hl);
        }
    }

    private static Panel createContentPanel() {
        Panel contentPanel = new Panel();
        contentPanel.setStyleName(Runo.PANEL_LIGHT);
        contentPanel.setImmediate(false);
        contentPanel.setWidth("100.0%");
        contentPanel.setHeight("100.0%");
        return contentPanel;
    }

    private VerticalLayout buildVlContentPanel() {
        // common part: create layout
        VerticalLayout vlContentPanel = new VerticalLayout();
        vlContentPanel.setImmediate(false);
        vlContentPanel.setWidth("100.0%");
        vlContentPanel.setHeight("100.0%");
        vlContentPanel.setMargin(false, true, false, true);

        vlContentPanel.addComponent(buildVlResourceProperties());
        // pnlCreateContext
        Panel panel = buildPanel();

        vlContentPanel.addComponent(panel);
        vlContentPanel.setExpandRatio(panel, 1f);

        return vlContentPanel;
    }

    private Panel buildPanel() {
        // common part: create layout
        Panel panel = new Panel();
        panel.setImmediate(false);
        panel.setWidth("100.0%");
        panel.setHeight("100.0%");

        try {
            VerticalLayout verticalLayout = buildVerticalLayout();
            buildEditUserForm(verticalLayout);
            panel.addComponent(verticalLayout);
        }
        catch (EscidocClientException e) {
            router.getMainWindow().showNotification(ViewConstants.ERROR_CREATING_RESOURCE + e.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }
        catch (URISyntaxException e) {
            router.getMainWindow().showNotification(ViewConstants.ERROR_CREATING_RESOURCE + e.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }

        return panel;
    }

    private static VerticalLayout buildVerticalLayout() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setImmediate(false);
        verticalLayout.setWidth("100.0%");
        verticalLayout.setHeight("100.0%");
        verticalLayout.setMargin(false);
        verticalLayout.setSpacing(false);
        return verticalLayout;
    }

    private void buildEditUserForm(VerticalLayout vlAccCreateContext) throws EscidocClientException, URISyntaxException {
        final Form form = new Form();
        form.setImmediate(true);

        vlAccCreateContext.addComponent(buildRolesView());

        vlAccCreateContext.addComponent(buildPreferencesView());
        if (uac.hasAccessOnAttributes(router.getApp().getCurrentUser().getUserId())) {
            vlAccCreateContext.addComponent(buildAttributesView());
        }

    }

    private Component buildAttributesView() throws EscidocClientException {
        attributePanel = new Panel(ViewConstants.ATTRIBUTES);

        final UserAccountAttributes attributeTable = buildAttributeTable();
        final Button addAttributeButton = buildAddAttributeButton(attributeTable);

        attributePanel.addComponent(attributeTable);
        attributePanel.addComponent(addAttributeButton);
        return attributePanel;
    }

    private UserAccountAttributes buildAttributeTable() throws EscidocClientException {
        UserAccountAttributes userAccountAttributes =
            new UserAccountAttributes(userProxy, ur.getAttributes(userProxy), ur, uac);
        userAccountAttributes.buildTable();
        return userAccountAttributes;
    }

    private Button buildAddAttributeButton(final UserAccountAttributes attributeTable) {
        final Button addAttributeButton = new Button();
        addAttributeButton.setDescription("Add new Attribute");
        addAttributeButton.setIcon(new ThemeResource("images/assets/plus.png"));
        addAttributeButton.addListener(new OnAddAttribute(addAttributeButton, attributeTable));
        return addAttributeButton;
    }

    static boolean isNotValid(final TextField key, final TextField value) {
        return key.getValue().toString().length() < 1 || lessThanTwoChars(value);
    }

    private static boolean lessThanTwoChars(final TextField key) {
        return key.getValue().toString().length() < 2;
    }

    void showMessage() {
        router.getMainWindow().showNotification(
            "Both the name and the value are required, please do not leave them blank",
            Window.Notification.TYPE_ERROR_MESSAGE);
    }

    private Panel buildPreferencesView() throws EscidocClientException {
        final Panel preferencePanel = new Panel(ViewConstants.PREFERENCES);

        final UserAccountPreferences userPreferenceTable = buildPreferenceTable();
        addPreferenceButton = buildAddPreferenceButton(preferencePanel, userPreferenceTable);

        preferencePanel.addComponent(userPreferenceTable);
        preferencePanel.addComponent(addPreferenceButton);
        return preferencePanel;
    }

    private UserAccountPreferences buildPreferenceTable() throws EscidocClientException {
        final UserAccountPreferences userPrefTable =
            new UserAccountPreferences(userProxy, ur.getPreferences(userProxy), ur, uac);
        userPrefTable.buildTable();
        return userPrefTable;
    }

    private Button buildAddPreferenceButton(final Panel preferencePanel, final UserAccountPreferences userPrefTable) {
        final Button addPreferenceButton = new Button();
        addPreferenceButton.setDescription("Add new Preference");
        addPreferenceButton.setIcon(new ThemeResource("images/assets/plus.png"));
        addPreferenceButton.addListener(new OnAddPreference(this, preferencePanel, userPrefTable, addPreferenceButton));
        return addPreferenceButton;
    }

    private VerticalLayout buildVlResourceProperties() {
        // common part: create layout
        VerticalLayout vlResourceProperties = new VerticalLayout();
        vlResourceProperties.setImmediate(false);
        vlResourceProperties.setWidth("100.0%");
        vlResourceProperties.setHeight("100.0%");
        vlResourceProperties.setMargin(false);

        // creating the properties / without the breadcrump
        resoucePropertiesView = new ResourcePropertiesUserAccountView(userProxy, router, uac);
        resoucePropertiesView.buildViews();
        vlResourceProperties.addComponent(resoucePropertiesView.getContentLayout());
        return vlResourceProperties;
    }

    private Panel buildRolesView() throws EscidocClientException {
        return new UserRolesView(userProxy, uac, repositories, router);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((userProxy == null) ? 0 : userProxy.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UserAccountView other = (UserAccountView) obj;
        if (userProxy == null) {
            if (other.userProxy != null) {
                return false;
            }
        }
        else if (!userProxy.equals(other.userProxy)) {
            return false;
        }
        return true;
    }
}