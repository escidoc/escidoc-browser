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
package org.escidoc.browser.ui.tools;

import com.google.common.base.Preconditions;

import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

import org.escidoc.browser.controller.CreateResourcesController;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.OrgUnitService;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.View;

import java.util.Collection;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.oum.OrganizationalUnit;

@SuppressWarnings("serial")
public class CreateResourcesView extends View {

    private final Router router;

    private final Repositories repositories;

    private final CreateResourcesController controller;

    public CreateResourcesView(final Router router, final Repositories repositories,
        final CreateResourcesController controller) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        this.router = router;
        this.repositories = repositories;
        this.controller = controller;
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
        final Panel contentPanel = new Panel();
        contentPanel.setImmediate(false);
        contentPanel.setWidth("100.0%");
        contentPanel.setHeight("100.0%");

        // vlContentPanel
        final VerticalLayout vlContentPanel = buildVlContentPanel();
        contentPanel.setContent(vlContentPanel);

        return contentPanel;
    }

    private VerticalLayout buildVlContentPanel() {
        // common part: create layout
        final VerticalLayout vlContentPanel = new VerticalLayout();
        vlContentPanel.setImmediate(false);
        vlContentPanel.setWidth("100.0%");
        vlContentPanel.setHeight("100.0%");
        vlContentPanel.setMargin(false);

        // pnlCreateContext
        final Accordion pnlCreateContext = buildPnlCreateContext();
        vlContentPanel.addComponent(pnlCreateContext);
        vlContentPanel.setExpandRatio(pnlCreateContext, 0.4f);

        // pnlCreate
        final Accordion pnlCreate = buildCreateContentModel();
        vlContentPanel.addComponent(pnlCreate);
        vlContentPanel.setExpandRatio(pnlCreate, 0.3f);

        // pnlCreateOrgUnit
        final Accordion pnlCreateOrgUnit = buildPnlCreateOrgUnit();
        vlContentPanel.addComponent(pnlCreateOrgUnit);
        vlContentPanel.setExpandRatio(pnlCreateOrgUnit, 0.3f);

        // pnlCreateOrgUnit
        final Accordion createUserAccount = buildCreateUserAccount();
        vlContentPanel.addComponent(createUserAccount);
        vlContentPanel.setExpandRatio(createUserAccount, 0.3f);

        return vlContentPanel;
    }

    private Accordion buildCreateUserAccount() {
        // common part: create layout
        final Accordion add = new Accordion();
        add.setImmediate(false);
        add.setWidth("100.0%");
        add.setHeight("100.0%");

        // vlPnlCreateOrgUnit
        final VerticalLayout layout = new VerticalLayout();
        layout.setImmediate(false);
        layout.setWidth("100.0%");
        layout.setHeight("100.0%");
        layout.setMargin(false);
        formAddUser(layout);
        add.addTab(layout, "Create User Account");

        return add;
    }

    private void formAddUser(VerticalLayout layout) {
        layout.addComponent(buildCreateUserAccountForm());
    }

    private Form buildCreateUserAccountForm() {
        final Form form = new Form();
        form.setImmediate(true);

        // loginName
        final TextField loginNameField = new TextField("Login Name");
        loginNameField.setImmediate(false);
        loginNameField.setWidth("-1px");
        loginNameField.setHeight("-1px");
        form.addField("loginName", loginNameField);

        // Name
        final TextField realNameField = new TextField();
        realNameField.setCaption("Real Name");
        realNameField.setImmediate(false);
        realNameField.setWidth("-1px");
        realNameField.setHeight("-1px");
        realNameField.setInvalidAllowed(false);
        realNameField.setRequired(true);
        form.addField("realName", realNameField);

        // Password
        final PasswordField txtPassword = new PasswordField("Password");
        txtPassword.setImmediate(false);
        txtPassword.setNullSettingAllowed(false);
        txtPassword.setWidth("-1px");
        txtPassword.setHeight("-1px");
        form.addField("txtPassword", txtPassword);

        // Verify Password
        final PasswordField txtPassword2 = new PasswordField("Verify Password");
        txtPassword2.setImmediate(false);
        txtPassword2.setWidth("-1px");
        txtPassword2.setHeight("-1px");
        form.addField("txtPassword2", txtPassword2);

        // btnAddContext
        final Button addButton = new Button("Submit", new Button.ClickListener() {
            private static final long serialVersionUID = -1373866726572059290L;

            @Override
            public void buttonClick(final ClickEvent event) {
                try {
                    form.commit();
                    if (!txtPassword.getValue().equals(txtPassword2.getValue())) {
                        router
                            .getMainWindow()
                            .showNotification(
                                "Password verification failed, please try again and make sure you are typing the same password twice ",
                                Window.Notification.TYPE_WARNING_MESSAGE);
                        return;
                    }

                    controller.createResourceAddUserAccount(realNameField.getValue().toString(), loginNameField
                        .getValue().toString(), txtPassword.getValue().toString());

                    router.getMainWindow().showNotification(
                        "User Account" + realNameField.getValue().toString() + " created successfully ",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);

                    form.getField("loginName").setValue("");
                    form.getField("realName").setValue("");
                    form.getField("txtPassword").setValue("");
                    form.getField("txtPassword2").setValue("");
                }
                catch (final EmptyValueException e) {
                    router.getMainWindow().showNotification("Please fill in all the required elements in the form",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
                }
                catch (final Exception e) {
                    router.getMainWindow().showNotification(
                        ViewConstants.ERROR_CREATING_RESOURCE + e.getLocalizedMessage(),
                        Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });

        addButton.setWidth("-1px");
        addButton.setHeight("-1px");
        form.getLayout().addComponent(addButton);

        form.getField("loginName").setRequired(true);
        form.getField("loginName").setRequiredError("Login Name is missing");

        form.getField("realName").setRequired(true);
        form.getField("realName").setRequiredError("Real Name is missing");

        return form;
    }

    private Accordion buildPnlCreateContext() {
        // common part: create layout
        final Accordion accCreateContext = new Accordion();
        accCreateContext.setImmediate(false);
        accCreateContext.setWidth("100.0%");
        accCreateContext.setHeight("100.0%");

        // vlPnlCreateContext
        final VerticalLayout vlAccCreateContext = new VerticalLayout();
        vlAccCreateContext.setImmediate(false);
        vlAccCreateContext.setWidth("100.0%");
        vlAccCreateContext.setHeight("100.0%");
        vlAccCreateContext.setMargin(false);
        vlAccCreateContext.setSpacing(false);
        vlAccCreateContext.setSizeUndefined();

        // AddContext Form
        try {
            formAddContext(vlAccCreateContext);
        }
        catch (final EscidocClientException e) {
            router.getMainWindow().showNotification(ViewConstants.ERROR_CREATING_RESOURCE + e.getLocalizedMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }
        accCreateContext.addTab(vlAccCreateContext, "Create Context");

        return accCreateContext;
    }

    private Accordion buildCreateContentModel() {
        // common part: create layout
        final Accordion accCreateContentModel = new Accordion();
        accCreateContentModel.setImmediate(false);
        accCreateContentModel.setWidth("100.0%");
        accCreateContentModel.setHeight("100.0%");

        // vlPnlCreate
        final VerticalLayout vlAccCreate = new VerticalLayout();
        vlAccCreate.setImmediate(false);
        vlAccCreate.setWidth("100.0%");
        vlAccCreate.setHeight("100.0%");
        vlAccCreate.setMargin(false);
        formAddContentModel(vlAccCreate);
        accCreateContentModel.addTab(vlAccCreate, "Create Content Model");

        return accCreateContentModel;
    }

    private Accordion buildPnlCreateOrgUnit() {
        // common part: create layout
        final Accordion accCreateOrgUnit = new Accordion();
        accCreateOrgUnit.setImmediate(false);
        accCreateOrgUnit.setWidth("100.0%");
        accCreateOrgUnit.setHeight("100.0%");

        // vlPnlCreateOrgUnit
        final VerticalLayout vlAccCreateOrgUnit = new VerticalLayout();
        vlAccCreateOrgUnit.setImmediate(false);
        vlAccCreateOrgUnit.setWidth("100.0%");
        vlAccCreateOrgUnit.setHeight("100.0%");
        vlAccCreateOrgUnit.setMargin(false);
        formAddOrgUnit(vlAccCreateOrgUnit);
        accCreateOrgUnit.addTab(vlAccCreateOrgUnit, "Create Organizational Units");

        return accCreateOrgUnit;
    }

    private void formAddOrgUnit(final VerticalLayout vlAccCreateOrgUnit) {
        vlAccCreateOrgUnit.addComponent(buildCreateOrgUnitForm());
    }

    private Form buildCreateOrgUnitForm() {
        final Form form = new Form();
        form.setImmediate(true);

        // Name
        final TextField txtNameContext = new TextField();
        txtNameContext.setCaption("Name");
        txtNameContext.setImmediate(false);
        txtNameContext.setWidth("-1px");
        txtNameContext.setHeight("-1px");
        txtNameContext.setInvalidAllowed(false);
        txtNameContext.setRequired(true);
        form.addField("txtNameContext", txtNameContext);

        // Description
        final TextField txtDescContext = new TextField("Description");
        txtDescContext.setImmediate(false);
        txtDescContext.setWidth("-1px");
        txtDescContext.setHeight("-1px");
        form.addField("txtDescContext", txtDescContext);

        // btnAddContext
        final Button btnAddContext = new Button("Submit", new Button.ClickListener() {
            private static final long serialVersionUID = -1373866726572059290L;

            @Override
            public void buttonClick(final ClickEvent event) {
                try {
                    form.commit();
                    controller.createResourceAddOrgUnit(txtNameContext.getValue().toString(), txtDescContext
                        .getValue().toString(), router, router.getServiceLocation());
                    router.getMainWindow().showNotification(
                        "Organizational Unit " + txtNameContext.getValue().toString() + " created successfully ",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
                    form.getField("txtNameContext").setValue("");
                    form.getField("txtDescContext").setValue("");
                }
                catch (final EmptyValueException e) {
                    router.getMainWindow().showNotification("Please fill in all the required elements in the form",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
                }
                catch (final Exception e) {
                    router.getMainWindow().showNotification(
                        ViewConstants.ERROR_CREATING_RESOURCE + e.getLocalizedMessage(),
                        Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });

        btnAddContext.setWidth("-1px");
        btnAddContext.setHeight("-1px");
        form.getLayout().addComponent(btnAddContext);

        form.getField("txtNameContext").setRequired(true);
        form.getField("txtNameContext").setRequiredError("Name is missing");

        form.getField("txtDescContext").setRequired(true);
        form.getField("txtDescContext").setRequiredError("Description is missing");
        return form;
    }

    private void formAddContentModel(final VerticalLayout vlAccCreateContentModel) {
        final Form frm = new Form();
        frm.setImmediate(true);
        // Name
        final TextField txtNameContext = new TextField();
        txtNameContext.setCaption("Name");
        txtNameContext.setImmediate(false);
        txtNameContext.setWidth("-1px");
        txtNameContext.setHeight("-1px");
        txtNameContext.setInvalidAllowed(false);
        txtNameContext.setRequired(true);
        frm.addField("txtNameContext", txtNameContext);

        // Description
        final TextField txtDescContext = new TextField("Description");
        txtDescContext.setImmediate(false);
        txtDescContext.setWidth("-1px");
        txtDescContext.setHeight("-1px");
        frm.addField("txtDescContext", txtDescContext);

        // btnAddContext
        final Button btnAddContext = new Button("Submit", new Button.ClickListener() {
            private static final long serialVersionUID = -6461338505705399082L;

            @Override
            public void buttonClick(final ClickEvent event) {
                try {
                    frm.commit();
                    controller.createResourceAddContentModel(txtNameContext.getValue().toString(), txtDescContext
                        .getValue().toString(), router, router.getServiceLocation());
                    router.getMainWindow().showNotification(
                        "ContentModel " + txtNameContext.getValue().toString() + " created successfully ",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
                    frm.getField("txtNameContext").setValue("");
                    frm.getField("txtDescContext").setValue("");
                }
                catch (final EmptyValueException e) {
                    router.getMainWindow().showNotification("Please fill in all the required elements in the form",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
                }
                catch (final EscidocClientException e) {
                    router.getMainWindow().showNotification(
                        ViewConstants.ERROR_CREATING_RESOURCE + e.getLocalizedMessage(),
                        Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });

        btnAddContext.setWidth("-1px");
        btnAddContext.setHeight("-1px");
        frm.getLayout().addComponent(btnAddContext);

        frm.getField("txtNameContext").setRequired(true);
        frm.getField("txtNameContext").setRequiredError("Name is missing");

        frm.getField("txtDescContext").setRequired(true);
        frm.getField("txtDescContext").setRequiredError("Description is missing");

        vlAccCreateContentModel.addComponent(frm);
    }

    private void formAddContext(final VerticalLayout vlAccCreateContext) throws EscidocClientException {
        final Form frm = new Form();
        frm.setImmediate(true);
        // Name
        final TextField txtNameContext = new TextField();
        txtNameContext.setCaption("Name");
        txtNameContext.setImmediate(false);
        txtNameContext.setWidth("-1px");
        txtNameContext.setHeight("-1px");
        txtNameContext.setInvalidAllowed(false);
        txtNameContext.setRequired(true);
        frm.addField("txtNameContext", txtNameContext);

        // Description
        final TextField txtDescContext = new TextField("Description");
        txtDescContext.setImmediate(false);
        txtDescContext.setWidth("-1px");
        txtDescContext.setHeight("-1px");
        frm.addField("txtDescContext", txtDescContext);

        // Description
        final TextField txtType = new TextField("Type");
        txtType.setImmediate(false);
        txtType.setWidth("-1px");
        txtType.setHeight("-1px");
        frm.addField("txtType", txtType);

        // OrgUnit
        final NativeSelect slOrgUnit = new NativeSelect("Organizational Unit");
        slOrgUnit.setImmediate(true);
        slOrgUnit.setWidth("-1px");
        slOrgUnit.setHeight("-1px");
        slOrgUnit.setRequired(true);
        slOrgUnit.setNullSelectionAllowed(false);
        frm.addField("slOrgUnit", slOrgUnit);

        final OrgUnitService orgUnitService =
            new OrgUnitService(router.getServiceLocation().getEscidocUri(), router.getApp().getCurrentUser().getToken());
        final Collection<OrganizationalUnit> orgUnits = orgUnitService.findAll();
        for (final OrganizationalUnit organizationalUnit : orgUnits) {
            slOrgUnit.addItem(organizationalUnit.getObjid());
            slOrgUnit.setItemCaption(organizationalUnit.getObjid(), organizationalUnit.getXLinkTitle());
        }

        frm.getLayout().addComponent(slOrgUnit);
        final CheckBox checkStatusOpened = new CheckBox("Create Context in Status opened?", true);
        checkStatusOpened.setImmediate(true);
        frm.addField("checkStatusOpened", checkStatusOpened);

        // btnAddContext
        final Button btnAddContext = new Button("Submit", new Button.ClickListener() {
            private static final long serialVersionUID = -4696167135894721166L;

            @Override
            public void buttonClick(final ClickEvent event) {
                try {
                    frm.commit();
                    controller.createResourceAddContext(txtNameContext.getValue().toString(), txtDescContext
                        .getValue().toString(), txtType.getValue().toString(), slOrgUnit.getValue().toString(),
                        (Boolean) checkStatusOpened.getValue(), repositories, router.getServiceLocation());
                    router.getMainWindow().showNotification(
                        "Context " + txtNameContext.getValue().toString() + " created successfully ",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
                    frm.getField("txtNameContext").setValue("");
                    frm.getField("txtDescContext").setValue("");
                    frm.getField("txtType").setValue("");
                    frm.getField("slOrgUnit").setValue(null);
                    // Ideally here should be a sync method to sync the tree

                }
                catch (final EmptyValueException e) {
                    router.getMainWindow().showNotification("Please fill in all the required elements in the form",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
                }
                catch (final EscidocClientException e) {
                    router.getMainWindow().showNotification(
                        ViewConstants.ERROR_CREATING_RESOURCE + e.getLocalizedMessage(),
                        Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });

        btnAddContext.setWidth("-1px");
        btnAddContext.setHeight("-1px");
        frm.getLayout().addComponent(btnAddContext);

        frm.getField("txtNameContext").setRequired(true);
        frm.getField("txtNameContext").setRequiredError("Name is missing");

        frm.getField("txtDescContext").setRequired(true);
        frm.getField("txtDescContext").setRequiredError("Description is missing");

        frm.getField("txtType").setRequired(true);
        frm.getField("txtType").setRequiredError("Context Type is missing");
        //
        frm.getField("slOrgUnit").setRequired(true);
        frm.getField("slOrgUnit").setRequiredError("Organizazional Unit is required");

        vlAccCreateContext.addComponent(frm);
    }
}
