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
package org.escidoc.browser.ui.view.helpers;

import org.escidoc.browser.controller.ContainerController;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.internal.ContainerProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ResourcePropertiesFolderView extends ResourceProperties {

    private static final Logger LOG = LoggerFactory.getLogger(ResourcePropertiesFolderView.class);

    private String status;

    private Component oldComponent;

    private Component swapComponent;

    private Label lblStatus;

    private Window subwindow;

    private ContainerProxyImpl resourceProxy;

    private Repositories repositories;

    private CssLayout cssLayout;

    private Window mainWindow;

    private final VerticalLayout vlPropertiesLeft = new VerticalLayout();

    private EscidocServiceLocation serviceLocation;

    private Label descTitle;

    private Label descLabel;

    private ContainerController controller;

    private Router router;

    private TextField txtFieldTitle;

    private TextField txtFieldDescription;

    private boolean isChangedTitle;

    private boolean isChangedDescription;

    private boolean isChangedPublicStatus;

    private boolean isChangedLockStatus;

    public ResourcePropertiesFolderView(ContainerProxy resourceProxy, Router router, ContainerController controller) {
        this.resourceProxy = (ContainerProxyImpl) resourceProxy;
        this.repositories = router.getRepositories();
        this.mainWindow = router.getMainWindow();
        this.serviceLocation = router.getServiceLocation();
        this.controller = controller;
        this.router = router;
        buildViews();
    }

    // protected void buildViews() {
    // createLayout();
    // // handleLayoutListeners();
    // createBreadcrump();
    // createPermanentLink();
    // bindNametoHeader();
    // bindDescription();
    // bindHrRuler();
    // bindProperties();
    // }

    protected void bindDescription() {
        descLabel = new Label(ViewConstants.DESCRIPTION_LBL + resourceProxy.getDescription());
        descLabel.setDescription("header");
        cssLayout.addComponent(descLabel);
    }

    protected void bindNametoHeader() {
        descTitle = new Label(ViewConstants.RESOURCE_NAME_CONTEXT + resourceProxy.getName());
        descTitle.setDescription("header");
        descTitle.setStyleName("h1 fullwidth");
        cssLayout.addComponent(descTitle);
    }

    public CssLayout getContentLayout() {
        return cssLayout;
    }

    public void createLayout() {
        this.cssLayout = new CssLayout();
        this.cssLayout.setWidth("100%");
        this.cssLayout.setHeight("460px");
        this.cssLayout.setMargin(false);
    }

    protected void createPermanentLink() {
        new CreateResourceLinksVH(mainWindow.getURL().toString(), resourceProxy, this, cssLayout, router);
    }

    protected void bindProperties() {

        final Panel pnlPropertiesLeft = buildLeftPropertiesPnl();
        final Panel pnlPropertiesRight = buildRightPnlProperties();

        final Label descMetadata1 = new Label("ID: " + resourceProxy.getId());

        status = "Folder " + " is ";

        lblStatus = new Label(status + resourceProxy.getStatus(), Label.CONTENT_RAW);
        lblStatus.setDescription(ViewConstants.DESC_STATUS2);

        final Label descMetadata2 =
            new Label(ViewConstants.CREATED_BY + " " + resourceProxy.getCreator() + " on "
                + resourceProxy.getCreatedOn() + "<br/>" + ViewConstants.LAST_MODIFIED_BY + " "
                + resourceProxy.getModifier() + " on " + resourceProxy.getModifiedOn(), Label.CONTENT_XHTML);

        vlPropertiesLeft.addComponent(descMetadata1);
        vlPropertiesLeft.addComponent(lblStatus);
        pnlPropertiesLeft.addComponent(vlPropertiesLeft);
        cssLayout.addComponent(pnlPropertiesLeft);

        pnlPropertiesRight.addComponent(descMetadata2);
        cssLayout.addComponent(pnlPropertiesRight);

    }

    protected Panel buildLeftPropertiesPnl() {
        final Panel pnlPropertiesLeft = new Panel();
        pnlPropertiesLeft.setWidth("40%");
        pnlPropertiesLeft.setHeight("60px");
        pnlPropertiesLeft.setStyleName(ViewConstants.FLOAT_LEFT);
        pnlPropertiesLeft.addStyleName(Runo.PANEL_LIGHT);
        pnlPropertiesLeft.getLayout().setMargin(false);
        return pnlPropertiesLeft;
    }

    protected Panel buildRightPnlProperties() {
        final Panel pnlPropertiesRight = new Panel();
        pnlPropertiesRight.setWidth("60%");
        pnlPropertiesRight.setHeight("60px");
        pnlPropertiesRight.setStyleName(ViewConstants.FLOAT_RIGHT);
        pnlPropertiesRight.addStyleName(Runo.PANEL_LIGHT);
        pnlPropertiesRight.getLayout().setMargin(false);
        return pnlPropertiesRight;
    }

    protected void bindHrRuler() {
        final Label descRuler = new Label("<hr/>", Label.CONTENT_RAW);
        descRuler.setStyleName("hr");
        cssLayout.addComponent(descRuler);
    }

    protected void createBreadcrump() {
        new BreadCrumbMenu(cssLayout, resourceProxy, mainWindow, serviceLocation, repositories);
    }

    public void handleSaveAction() {
        isChangedTitle = areEqual(resourceProxy.getName(), txtFieldTitle.getValue().toString());
        isChangedDescription = areEqual(resourceProxy.getDescription(), txtFieldDescription.getValue().toString());
        if (txtFieldDescription.getValue().toString() == "") {
            isChangedDescription = false;
        }
        if (isChangedTitle == false && isChangedDescription == false && isChangedPublicStatus == false
            && isChangedLockStatus == false) {
            closeEditablefields();
        }
        else {
            saveActionModalWindow();
        }
    }

    protected void saveActionModalWindow() {
        subwindow = new Window(ViewConstants.SUBWINDOW_EDIT);
        subwindow.setModal(true);
        // Configure the windws layout; by default a VerticalLayout
        final VerticalLayout layout = (VerticalLayout) subwindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeUndefined();

        final TextArea editor = new TextArea("Your Comment");
        editor.setRequired(true);
        editor.setRequiredError("The Field may not be empty.");

        final HorizontalLayout hl = new HorizontalLayout();

        final Button close = new Button("Update", new Button.ClickListener() {

            private static final long serialVersionUID = 1424933077274899865L;

            @Override
            public void buttonClick(final ClickEvent event) {
                saveEditableFields(editor.getValue().toString());
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        final Button cancel = new Button("Cancel", new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });

        hl.addComponent(close);
        hl.addComponent(cancel);

        subwindow.addComponent(editor);
        subwindow.addComponent(hl);
        mainWindow.addWindow(subwindow);
    }

    private void closeEditablefields() {
        descTitle.setValue(ViewConstants.RESOURCE_NAME_CONTEXT + txtFieldTitle.getValue());
        descLabel.setValue(ViewConstants.DESCRIPTION_LBL + txtFieldDescription.getValue());
        cssLayout.replaceComponent(txtFieldTitle, descTitle);
        cssLayout.replaceComponent(txtFieldDescription, descLabel);

    }

    public void showEditableFields() {
        // Showing editable title
        txtFieldTitle = new TextField();
        txtFieldTitle.setValue(resourceProxy.getName());
        txtFieldTitle.addStyleName("inlineblock");
        cssLayout.replaceComponent(descTitle, txtFieldTitle);

        txtFieldDescription = new TextField();
        if (resourceProxy.getDescription() == null) {
            txtFieldDescription.setValue("");
        }
        else {
            txtFieldDescription.setValue(resourceProxy.getDescription());
        }
        cssLayout.replaceComponent(descLabel, txtFieldDescription);
        isChangedTitle = false;
        isChangedDescription = false;
        isChangedPublicStatus = false;
        isChangedLockStatus = false;

    }

    public void saveEditableFields(String comment) {
        // Showing editable title
        descTitle.setValue(ViewConstants.RESOURCE_NAME_CONTAINER + txtFieldTitle.getValue());
        descLabel.setValue(ViewConstants.DESCRIPTION_LBL + txtFieldDescription.getValue());
        cssLayout.replaceComponent(txtFieldTitle, descTitle);
        cssLayout.replaceComponent(txtFieldDescription, descLabel);

        try {
            controller.updateContainer(isChangedTitle, isChangedDescription, isChangedPublicStatus,
                isChangedLockStatus, txtFieldTitle.getValue().toString(), txtFieldDescription.getValue().toString(),
                "", "", comment);
            router.getMainWindow().showNotification(
                new Window.Notification("Resource Updated successfully", Notification.TYPE_TRAY_NOTIFICATION));
        }
        catch (EscidocClientException e) {
            router
                .getMainWindow().showNotification(
                    new Window.Notification("Update failed " + e.getLocalizedMessage(),
                        Notification.TYPE_TRAY_NOTIFICATION));
            e.printStackTrace();
        }

    }

    private boolean areEqual(String oldValue, String newValue) {
        if ((oldValue != null) && oldValue.equals(newValue)) {
            return false;
        }
        return true;
    }
}
