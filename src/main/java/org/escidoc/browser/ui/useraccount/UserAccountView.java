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
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui.useraccount;

import com.google.common.base.Preconditions;

import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

import org.escidoc.browser.controller.UserAccountController;
import org.escidoc.browser.model.internal.UserProxy;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.repository.internal.UserAccountRepository;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.View;

import java.net.URISyntaxException;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.aa.useraccount.Attribute;
import de.escidoc.core.resources.aa.useraccount.Preference;

@SuppressWarnings("serial")
public class UserAccountView extends View {

    private final class OnAddPreference implements ClickListener {
        private final Panel preferencePanel;

        private final UserAccountPreferences userPrefTable;

        private final Button addPreference;

        private OnAddPreference(Panel preferencePanel, UserAccountPreferences userPrefTable, Button addPreference) {
            this.preferencePanel = preferencePanel;
            this.userPrefTable = userPrefTable;
            this.addPreference = addPreference;
        }

        @Override
        public void buttonClick(@SuppressWarnings("unused") final com.vaadin.ui.Button.ClickEvent event) {
            addPreference.setEnabled(false);
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

            final Button addButton = new Button();
            addButton.setIcon(new ThemeResource("images/assets/plus.png"));
            addButton.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(final com.vaadin.ui.Button.ClickEvent event) {
                    if (isNotValid(key, value)) {
                        showMessage();
                    }
                    else {
                        try {
                            ur.createPreference(userProxy, new Preference(key.getValue().toString(), value
                                .getValue().toString()));
                            router.getMainWindow().showNotification("Preference added successfully ",
                                Window.Notification.TYPE_TRAY_NOTIFICATION);
                            hl.removeAllComponents();
                            addPreference.setEnabled(true);
                            userPrefTable.createItem(userPrefTable.getTableContainer(), key.getValue().toString(), key
                                .getValue().toString(), value.getValue().toString());
                        }
                        catch (final EscidocClientException e) {
                            router.getMainWindow().showNotification(
                                ViewConstants.ERROR_CREATING_USER_PREFERENCE + e.getLocalizedMessage(),
                                Window.Notification.TYPE_ERROR_MESSAGE);
                        }
                    }
                }

            });
            hl.addComponent(key);
            hl.addComponent(value);
            hl.addComponent(addButton);
            hl.setComponentAlignment(addButton, Alignment.BOTTOM_RIGHT);
            preferencePanel.addComponent(hl);
        }
    }

    private Router router;

    private UserProxy userProxy;

    private UserAccountRepository ur;

    private UserAccountController uac;

    private Panel attributePanel;

    private Button addPreferenceButton;

    public UserAccountView(Router router, UserProxy userProxy, UserAccountRepository ur, UserAccountController uac) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(userProxy, "userProxy is null: %s", userProxy);
        Preconditions.checkNotNull(ur, "ur is null: %s", ur);
        Preconditions.checkNotNull(uac, "uac is null: %s", uac);

        this.router = router;
        this.userProxy = userProxy;
        this.ur = ur;
        this.uac = uac;
        init();
    }

    public void init() {
        this.setImmediate(false);
        this.setWidth("100.0%");
        this.setHeight("100.0%");
        this.setStyleName(Runo.PANEL_LIGHT);
        this.setContent(buildContentPanel());
    }

    private Panel buildContentPanel() {
        // common part: create layout
        Panel contentPanel = new Panel();
        contentPanel.setImmediate(false);
        contentPanel.setWidth("100.0%");
        contentPanel.setHeight("100.0%");

        // vlContentPanel
        contentPanel.setContent(buildVlContentPanel());

        return contentPanel;
    }

    private VerticalLayout buildVlContentPanel() {
        // common part: create layout
        VerticalLayout vlContentPanel = new VerticalLayout();
        vlContentPanel.setImmediate(false);
        vlContentPanel.setWidth("100.0%");
        vlContentPanel.setHeight("100.0%");
        vlContentPanel.setMargin(false);

        // pnlCreateContext
        Accordion pnlCreateContext = buildPnlCreateContext();
        vlContentPanel.addComponent(pnlCreateContext);
        vlContentPanel.setExpandRatio(pnlCreateContext, 1f);

        return vlContentPanel;
    }

    private Accordion buildPnlCreateContext() {
        // common part: create layout
        Accordion accCreateContext = new Accordion();
        accCreateContext.setImmediate(false);
        accCreateContext.setWidth("100.0%");
        accCreateContext.setHeight("100.0%");

        // vlPnlCreateContext
        VerticalLayout vlAccCreateContext = new VerticalLayout();
        vlAccCreateContext.setImmediate(false);
        vlAccCreateContext.setWidth("100.0%");
        vlAccCreateContext.setHeight("100.0%");
        vlAccCreateContext.setMargin(false);
        vlAccCreateContext.setSpacing(false);

        try {
            buildEditUserForm(vlAccCreateContext);
        }
        catch (EscidocClientException e) {
            router.getMainWindow().showNotification(ViewConstants.ERROR_CREATING_RESOURCE + e.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }
        catch (URISyntaxException e) {
            router.getMainWindow().showNotification(ViewConstants.ERROR_CREATING_RESOURCE + e.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }
        accCreateContext.addTab(vlAccCreateContext, " ");

        return accCreateContext;
    }

    private void buildEditUserForm(VerticalLayout vlAccCreateContext) throws EscidocClientException, URISyntaxException {
        final Form form = new Form();
        form.setImmediate(true);

        buildLoginNameField(form);
        final TextField realNameField = buildRealNameField(form);
        final PasswordField passwordField = buildPasswordField(form);
        final PasswordField verifyPasswordField = buildVerifyPasswordField(form);

        Button submitButton = new Button("Submit", new Button.ClickListener() {

            @Override
            public void buttonClick(@SuppressWarnings("unused") com.vaadin.ui.Button.ClickEvent event) {
                try {
                    form.commit();
                    if (!passwordField.getValue().equals(verifyPasswordField.getValue())) {
                        router
                            .getMainWindow()
                            .showNotification(
                                "Password verification failed, please try again and make sure you are typing the same password twice ",
                                Window.Notification.TYPE_TRAY_NOTIFICATION);
                        return;
                    }
                    if (passwordField.getValue().toString() != "") {
                        ur.updatePassword(userProxy, passwordField.getValue().toString());
                    }
                    else {
                        ur.updateName(userProxy, realNameField.getValue().toString());
                    }
                    router.getMainWindow().showNotification("User updateds successfully ",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
                }
                catch (EmptyValueException e) {
                    router.getMainWindow().showNotification("Please fill in all the required elements in the form",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
                }
                catch (EscidocClientException e) {
                    router.getMainWindow().showNotification(
                        ViewConstants.ERROR_UPDATING_USER + e.getLocalizedMessage(),
                        Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });

        submitButton.setWidth("-1px");
        submitButton.setHeight("-1px");
        form.getLayout().addComponent(submitButton);

        form.getField("txtNameContext").setRequired(true);
        form.getField("txtNameContext").setRequiredError("Name is missing");

        vlAccCreateContext.addComponent(form);
        vlAccCreateContext.addComponent(buildPreferencesView());
        vlAccCreateContext.addComponent(buildAttributesView());

        setEnability(realNameField, passwordField, verifyPasswordField);
    }

    private void setEnability(
        final TextField realNameField, final PasswordField passwordField, final PasswordField verifyPasswordField)
        throws EscidocClientException, URISyntaxException {

        boolean allowedToUpdate = isAllowedToUpdate();
        realNameField.setEnabled(allowedToUpdate);
        passwordField.setEnabled(allowedToUpdate);
        verifyPasswordField.setEnabled(allowedToUpdate);
        addPreferenceButton.setEnabled(allowedToUpdate);
    }

    private static PasswordField buildVerifyPasswordField(final Form form) {
        final PasswordField verifyPasswordField = new PasswordField("Verify Password");
        verifyPasswordField.setImmediate(false);
        verifyPasswordField.setWidth("-1px");
        verifyPasswordField.setHeight("-1px");
        form.addField("txtPassword2", verifyPasswordField);
        return verifyPasswordField;
    }

    private static PasswordField buildPasswordField(final Form form) {
        final PasswordField passwordField = new PasswordField("Password");
        passwordField.setImmediate(false);
        passwordField.setNullSettingAllowed(false);
        passwordField.setWidth("-1px");
        passwordField.setHeight("-1px");
        form.addField("txtPassword", passwordField);
        return passwordField;
    }

    private TextField buildRealNameField(final Form form) {
        final TextField realNameField = new TextField();
        realNameField.setCaption("Real Name");
        realNameField.setValue(userProxy.getName());
        realNameField.setImmediate(false);
        realNameField.setWidth("-1px");
        realNameField.setHeight("-1px");
        realNameField.setInvalidAllowed(false);
        realNameField.setRequired(true);
        form.addField("txtNameContext", realNameField);
        return realNameField;
    }

    private void buildLoginNameField(final Form form) {
        final TextField loginNameField = new TextField();
        loginNameField.setCaption("Login Name");
        loginNameField.setValue(userProxy.getLoginName());
        loginNameField.setEnabled(false);
        loginNameField.setImmediate(false);
        loginNameField.setWidth("-1px");
        loginNameField.setHeight("-1px");
        loginNameField.setInvalidAllowed(false);
        loginNameField.setRequired(true);
        form.addField("txtLoginName", loginNameField);
    }

    private boolean isAllowedToUpdate() throws EscidocClientException, URISyntaxException {
        return router
            .getRepositories().pdp().isAction(ActionIdConstants.UPDATE_USER_ACCOUNT).forCurrentUser()
            .forResource(userProxy.getId()).permitted();
    }

    private Component buildAttributesView() throws EscidocClientException {
        attributePanel = new Panel("Attributes");
        final UserAccountAttributes attributeTable =
            new UserAccountAttributes(userProxy, ur.getAttributes(userProxy), ur, uac);
        attributePanel.addComponent(attributeTable);

        final Button addAttributeButton = new Button();
        addAttributeButton.setDescription("Add new Attribute");
        addAttributeButton.setIcon(new ThemeResource("images/assets/plus.png"));
        addAttributeButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(@SuppressWarnings("unused") com.vaadin.ui.Button.ClickEvent event) {
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
                    public void buttonClick(@SuppressWarnings("unused") com.vaadin.ui.Button.ClickEvent event) {
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
                                attributeTable.createItem(attributeTable.getTableContainer(),
                                    key.getValue().toString(), key.getValue().toString(), value.getValue().toString());
                            }
                            catch (EscidocClientException e) {
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

        });

        attributePanel.addComponent(addAttributeButton);
        return attributePanel;
    }

    private static boolean isNotValid(final TextField key, final TextField value) {
        return lessThanTwoChars(key) || lessThanTwoChars(value);
    }

    private static boolean lessThanTwoChars(final TextField key) {
        return key.getValue().toString().length() < 2;
    }

    private void showMessage() {
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
        return userPrefTable;
    }

    private Button buildAddPreferenceButton(final Panel preferencePanel, final UserAccountPreferences userPrefTable) {
        final Button addPreferenceButton = new Button();
        addPreferenceButton.setDescription("Add new Preference");
        addPreferenceButton.setIcon(new ThemeResource("images/assets/plus.png"));
        addPreferenceButton.addListener(new OnAddPreference(preferencePanel, userPrefTable, addPreferenceButton));
        return addPreferenceButton;
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

    public void hideAttributeView() {
        attributePanel.setVisible(false);
    }
}