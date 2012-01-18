package org.escidoc.browser.ui.tools;

import org.escidoc.browser.controller.UserProfileController;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.aa.useraccount.Preference;
import de.escidoc.core.resources.aa.useraccount.Preferences;

@SuppressWarnings("serial")
public class UserProfileView extends View {
    private static final Logger LOG = LoggerFactory.getLogger(UserProfileView.class);

    private Router router;

    private Repositories repositories;

    private UserProfileController controller;

    private CurrentUser currentUser;

    public UserProfileView(Router router, Repositories repositories, UserProfileController userProfileController,
        CurrentUser currentUser) throws EscidocClientException {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        this.router = router;
        this.repositories = repositories;
        this.controller = userProfileController;
        this.currentUser = currentUser;
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

        // AddContext Form
        try {
            frmEditUser(vlAccCreateContext);
        }
        catch (EscidocClientException e) {
            router.getMainWindow().showNotification(ViewConstants.ERROR_CREATING_RESOURCE + e.getLocalizedMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }
        accCreateContext.addTab(vlAccCreateContext, " ");

        return accCreateContext;
    }

    private void frmEditUser(VerticalLayout vlAccCreateContext) throws EscidocClientException {
        final Form frm = new Form();
        frm.setImmediate(true);

        // Name
        final TextField txtLoginName = new TextField();
        txtLoginName.setCaption("Login Name");
        txtLoginName.setValue(currentUser.getLoginName());
        txtLoginName.setEnabled(false);
        txtLoginName.setImmediate(false);
        txtLoginName.setWidth("-1px");
        txtLoginName.setHeight("-1px");
        txtLoginName.setInvalidAllowed(false);
        txtLoginName.setRequired(true);
        frm.addField("txtLoginName", txtLoginName);

        // Name
        final TextField txtNameContext = new TextField();
        txtNameContext.setCaption("Real Name");
        txtNameContext.setValue(currentUser.getRealName());
        txtNameContext.setImmediate(false);
        txtNameContext.setWidth("-1px");
        txtNameContext.setHeight("-1px");
        txtNameContext.setInvalidAllowed(false);
        txtNameContext.setRequired(true);
        frm.addField("txtNameContext", txtNameContext);

        // Password
        final PasswordField txtPassword = new PasswordField("Password");
        txtPassword.setImmediate(false);
        txtPassword.setNullSettingAllowed(false);
        txtPassword.setWidth("-1px");
        txtPassword.setHeight("-1px");
        frm.addField("txtPassword", txtPassword);

        // Description
        final PasswordField txtPassword2 = new PasswordField("Verify Password");
        txtPassword2.setImmediate(false);
        txtPassword2.setWidth("-1px");
        txtPassword2.setHeight("-1px");
        frm.addField("txtPassword2", txtPassword2);

        // btnAddContext
        Button btnAddContext = new Button("Submit", new Button.ClickListener() {
            private static final long serialVersionUID = -4696167135894721166L;

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    frm.commit();
                    if (!txtPassword.getValue().equals(txtPassword2.getValue())) {
                        router
                            .getMainWindow()
                            .showNotification(
                                "Password verification failed, please try again and make sure you are typing the same password twice ",
                                Window.Notification.TYPE_TRAY_NOTIFICATION);
                        return;
                    }
                    if (txtPassword.getValue().toString() != "") {
                        LOG.debug("Updating with password!");
                        controller.updateProfile(txtNameContext.getValue().toString(), txtPassword
                            .getValue().toString());
                    }
                    else {
                        controller.updateProfile(txtNameContext.getValue().toString());
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

        btnAddContext.setWidth("-1px");
        btnAddContext.setHeight("-1px");
        frm.getLayout().addComponent(btnAddContext);

        frm.getField("txtNameContext").setRequired(true);
        frm.getField("txtNameContext").setRequiredError("Name is missing");

        vlAccCreateContext.addComponent(frm);

        final Panel pnl = buildPreferences();

        vlAccCreateContext.addComponent(pnl);
    }

    private Panel buildPreferences() throws EscidocClientException {
        final Panel pnl = new Panel("Preferences");
        Preferences preferences = controller.getUserPreferences();

        for (Preference preference : preferences) {
            final Label preferenceName = new Label(preference.getName() + " : " + preference.getValue());
            pnl.addComponent(preferenceName);
        }

        Button addPreference = new Button();
        addPreference.setDescription("Add new Preference");
        addPreference.setIcon(new ThemeResource("images/assets/plus.png"));
        addPreference.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
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

                Button btnadd = new Button();
                btnadd.setIcon(new ThemeResource("images/assets/plus.png"));
                btnadd.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {

                        try {
                            controller.createPreference(key.getValue().toString(), value.getValue().toString());
                            router.getMainWindow().showNotification("Preference added successfully ",
                                Window.Notification.TYPE_TRAY_NOTIFICATION);
                            hl.removeAllComponents();
                            hl.addComponent(new Label(key.getValue().toString() + " : " + value.getValue().toString()));

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
        result = prime * result + ((currentUser == null) ? 0 : currentUser.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserProfileView other = (UserProfileView) obj;
        if (currentUser == null) {
            if (other.currentUser != null)
                return false;
        }
        else if (!currentUser.equals(other.currentUser))
            return false;
        return true;
    }

}
