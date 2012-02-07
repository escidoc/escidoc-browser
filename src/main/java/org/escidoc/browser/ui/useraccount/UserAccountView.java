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
import org.escidoc.browser.repository.internal.UserAccountRepository;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.View;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.aa.useraccount.Attribute;
import de.escidoc.core.resources.aa.useraccount.Attributes;
import de.escidoc.core.resources.aa.useraccount.Preference;
import de.escidoc.core.resources.aa.useraccount.Preferences;

@SuppressWarnings("serial")
public class UserAccountView extends View {

    private Router router;

    private UserProxy userProxy;

    private UserAccountRepository ur;

    private UserAccountController uac;

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
        VerticalLayout vlContentPanel = buildVlContentPanel();
        contentPanel.setContent(vlContentPanel);

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
            router.getMainWindow().showNotification(ViewConstants.ERROR_CREATING_RESOURCE + e.getLocalizedMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }
        accCreateContext.addTab(vlAccCreateContext, " ");

        return accCreateContext;
    }

    private void buildEditUserForm(VerticalLayout vlAccCreateContext) throws EscidocClientException {
        final Form form = new Form();
        form.setImmediate(true);

        // Name
        final TextField txtLoginName = new TextField();
        txtLoginName.setCaption("Login Name");
        txtLoginName.setValue(userProxy.getLoginName());
        txtLoginName.setEnabled(false);
        txtLoginName.setImmediate(false);
        txtLoginName.setWidth("-1px");
        txtLoginName.setHeight("-1px");
        txtLoginName.setInvalidAllowed(false);
        txtLoginName.setRequired(true);
        form.addField("txtLoginName", txtLoginName);

        // Name
        final TextField txtNameContext = new TextField();
        txtNameContext.setCaption("Real Name");
        txtNameContext.setValue(userProxy.getName());
        txtNameContext.setImmediate(false);
        txtNameContext.setWidth("-1px");
        txtNameContext.setHeight("-1px");
        txtNameContext.setInvalidAllowed(false);
        txtNameContext.setRequired(true);
        form.addField("txtNameContext", txtNameContext);

        // Password
        final PasswordField txtPassword = new PasswordField("Password");
        txtPassword.setImmediate(false);
        txtPassword.setNullSettingAllowed(false);
        txtPassword.setWidth("-1px");
        txtPassword.setHeight("-1px");
        form.addField("txtPassword", txtPassword);

        // Description
        final PasswordField txtPassword2 = new PasswordField("Verify Password");
        txtPassword2.setImmediate(false);
        txtPassword2.setWidth("-1px");
        txtPassword2.setHeight("-1px");
        form.addField("txtPassword2", txtPassword2);

        Button submitButton = new Button("Submit", new Button.ClickListener() {

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                try {
                    form.commit();
                    if (!txtPassword.getValue().equals(txtPassword2.getValue())) {
                        router
                            .getMainWindow()
                            .showNotification(
                                "Password verification failed, please try again and make sure you are typing the same password twice ",
                                Window.Notification.TYPE_TRAY_NOTIFICATION);
                        return;
                    }
                    if (txtPassword.getValue().toString() != "") {
                        ur.updatePassword(userProxy, txtPassword.getValue().toString());
                    }
                    else {
                        ur.updateName(userProxy, txtNameContext.getValue().toString());
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
    }

    private Component buildAttributesView() throws EscidocClientException {
        final Panel panel = new Panel("Attributes");
        final UserAccountAttributes attributeTable = new UserAccountAttributes(userProxy, ur.getAttributes(userProxy), ur, uac);
        panel.addComponent(attributeTable);

        final Button addAttributeButton = new Button();
        addAttributeButton.setDescription("Add new Attribute");
        addAttributeButton.setIcon(new ThemeResource("images/assets/plus.png"));
        addAttributeButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
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
                    public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
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
                        catch (EscidocClientException e) {
                            router.getMainWindow().showNotification(
                                ViewConstants.ERROR_CREATING_USER_PREFERENCE + e.getLocalizedMessage(),
                                Window.Notification.TYPE_ERROR_MESSAGE);
                        }
                    }
                });
                hl.addComponent(key);
                hl.addComponent(value);
                hl.addComponent(btnadd);
                hl.setComponentAlignment(btnadd, Alignment.BOTTOM_RIGHT);
                panel.addComponent(hl);
            }

        });

        panel.addComponent(addAttributeButton);
        return panel;
    }

    private Panel buildPreferencesView() throws EscidocClientException {
        final Panel pnl = new Panel("Preferences");
        Preferences preferences = ur.getPreferences(userProxy);
        final UserAccountPreferences userPrefTable = new UserAccountPreferences(userProxy, preferences, ur, uac);
        pnl.addComponent(userPrefTable);

        final Button addPreference = new Button();
        addPreference.setDescription("Add new Preference");
        addPreference.setIcon(new ThemeResource("images/assets/plus.png"));
        addPreference.addListener(new ClickListener() {
            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
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

                final Button btnadd = new Button();
                btnadd.setIcon(new ThemeResource("images/assets/plus.png"));
                btnadd.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {

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
                        catch (EscidocClientException e) {
                            router.getMainWindow().showNotification(
                                ViewConstants.ERROR_CREATING_USER_PREFERENCE + e.getLocalizedMessage(),
                                Window.Notification.TYPE_ERROR_MESSAGE);
                        }
                    }
                });
                hl.addComponent(key);
                hl.addComponent(value);
                hl.addComponent(btnadd);
                hl.setComponentAlignment(btnadd, Alignment.BOTTOM_RIGHT);
                pnl.addComponent(hl);
            }

        });

        pnl.addComponent(addPreference);
        return pnl;
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
