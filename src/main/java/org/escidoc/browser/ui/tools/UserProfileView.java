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
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class UserProfileView extends View {
    private static final Logger LOG = LoggerFactory.getLogger(UserProfileView.class);

    private Router router;

    private Repositories repositories;

    private UserProfileController controller;

    private CurrentUser currentUser;

    public UserProfileView(Router router, Repositories repositories, UserProfileController userProfileController,
        CurrentUser currentUser) {
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
    }
}
