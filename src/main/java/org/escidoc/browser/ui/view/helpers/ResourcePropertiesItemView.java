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

import org.escidoc.browser.controller.ItemController;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.internal.ItemProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.ResourceDeleteConfirmation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.properties.LockStatus;
import de.escidoc.core.resources.common.properties.PublicStatus;
import de.escidoc.core.resources.om.item.Item;

public class ResourcePropertiesItemView extends ResourceProperties {

    private static final Logger LOG = LoggerFactory.getLogger(ResourcePropertiesItemView.class);

    private String status;

    private Label lblLockstatus;

    private Component swapComponent;

    private Label lblStatus;

    private Window subwindow;

    private String lockStatus;

    private Label lblCurrentVersionStatus;

    private ItemProxyImpl resourceProxy;

    private Repositories repositories;

    private CssLayout cssLayout;

    private Window mainWindow;

    private final VerticalLayout vlPropertiesLeft = new VerticalLayout();

    private EscidocServiceLocation serviceLocation;

    private Label descTitle;

    private Label descLabel;

    private ItemController controller;

    private Router router;

    private TextField txtFieldTitle;

    private NativeSelect cmbStatus;

    private NativeSelect cmbLockStatus;

    private HorizontalLayout hlPublicStatus;

    private HorizontalLayout hlLockStatus;

    public ResourcePropertiesItemView(ItemProxyImpl resourceProxy, Router router, ItemController controller) {
        this.resourceProxy = resourceProxy;
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
        descTitle = new Label(ViewConstants.ITEM_LABEL + resourceProxy.getName());
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
        this.cssLayout.setHeight("100%");
        this.cssLayout.setMargin(false);
    }

    protected void createPermanentLink() {
        new CreateResourceLinksVH(mainWindow.getURL().toString(), resourceProxy, this, cssLayout, router);
    }

    protected void bindProperties() {

        final Panel pnlPropertiesLeft = buildLeftPropertiesPnl();
        final Panel pnlPropertiesRight = buildRightPnlProperties();

        final Label descMetadata1 = new Label("ID: " + resourceProxy.getId());

        status = resourceProxy.getType().getLabel() + " is ";
        lockStatus = status;
        lblStatus = new Label(status + resourceProxy.getStatus(), Label.CONTENT_RAW);
        lblStatus.setDescription(ViewConstants.DESC_STATUS2);

        lblLockstatus = new Label(status + resourceProxy.getLockStatus(), Label.CONTENT_RAW);
        lblLockstatus.setDescription(ViewConstants.DESC_LOCKSTATUS);
        if (controller.canUpdateItem()) {
            lblLockstatus.setStyleName("inset");
        }
        final Label descMetadata2 =
            new Label(ViewConstants.CREATED_BY + " " + resourceProxy.getCreator() + " on "
                + resourceProxy.getCreatedOn() + "<br/>" + ViewConstants.LAST_MODIFIED_BY + " "
                + resourceProxy.getModifier() + " on " + resourceProxy.getModifiedOn() + "<br/>" + "Released by "
                + resourceProxy.getReleasedBy() + " on " + resourceProxy.getLatestVersionModifiedOn(),
                Label.CONTENT_XHTML);

        vlPropertiesLeft.addComponent(descMetadata1);
        if (controller.canUpdateItem()) {
            status = "Latest status is ";
            lblCurrentVersionStatus = new Label(status + resourceProxy.getVersionStatus());
            lblCurrentVersionStatus.setDescription(ViewConstants.DESC_STATUS2);
            lblCurrentVersionStatus.setStyleName("inset");
            vlPropertiesLeft.addComponent(lblCurrentVersionStatus);

        }
        else {
            vlPropertiesLeft.addComponent(lblStatus);
        }

        vlPropertiesLeft.addComponent(lblLockstatus);
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

    public void saveActionWindow() {
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

            // inline click-listener
            @Override
            public void buttonClick(final ClickEvent event) {
                // close the window by removing it from the
                // parent window
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

    private Component editLockStatus(final String lockStatus) {
        cmbLockStatus = new NativeSelect();
        cmbLockStatus.setNullSelectionAllowed(false);
        LOG.debug(lockStatus);
        cmbLockStatus.addItem(LockStatus.UNLOCKED.toString().toLowerCase());
        cmbLockStatus.addItem(LockStatus.LOCKED.toString().toLowerCase());

        if (lockStatus.contains("unlocked")) {
            cmbLockStatus.select(LockStatus.UNLOCKED.toString().toLowerCase());
        }
        else {
            cmbLockStatus.select(LockStatus.LOCKED.toString().toLowerCase());
        }
        cmbLockStatus.select(Integer.valueOf(1));

        hlLockStatus = new HorizontalLayout();
        hlLockStatus.addComponent(new Label("Lock status is: "));
        hlLockStatus.addComponent(cmbLockStatus);
        this.setCmbLockStatus(cmbLockStatus);
        setHlLockStatus(hlLockStatus);
        return hlLockStatus;

    }

    public HorizontalLayout getHlLockStatus() {
        return hlLockStatus;
    }

    public void setHlLockStatus(HorizontalLayout hlLockStatus) {
        this.hlLockStatus = hlLockStatus;
    }

    private Component editStatus(final String publicStatus) {
        cmbStatus = new NativeSelect();
        cmbStatus.setInvalidAllowed(false);
        cmbStatus.setNullSelectionAllowed(false);
        cmbStatus.addStyleName(Reindeer.LABEL_SMALL);
        final String pubStatus = publicStatus.toUpperCase();
        if (publicStatus.equals("pending")) {
            cmbStatus.addItem(PublicStatus.PENDING.toString().toLowerCase());
            cmbStatus.addItem(PublicStatus.SUBMITTED.toString().toLowerCase());
            // cmbStatus.setNullSelectionItemId(PublicStatus.PENDING.toString().toLowerCase());
            cmbStatus.select(publicStatus);
            if (controller.hasAccessDelResource()) {
                cmbStatus.addItem("delete");
            }
        }
        else if (publicStatus.equals("submitted")) {
            cmbStatus.setNullSelectionItemId(PublicStatus.SUBMITTED.toString().toLowerCase());
            cmbStatus.addItem(PublicStatus.SUBMITTED.toString().toLowerCase());
            cmbStatus.addItem(PublicStatus.IN_REVISION.toString().toLowerCase());
            cmbStatus.addItem(PublicStatus.RELEASED.toString().toLowerCase());
        }
        else if (publicStatus.equals("in_revision")) {
            cmbStatus.setNullSelectionItemId(PublicStatus.IN_REVISION.toString().toLowerCase());
            cmbStatus.addItem(PublicStatus.IN_REVISION.toString().toLowerCase());
            cmbStatus.addItem(PublicStatus.SUBMITTED.toString().toLowerCase());
        }
        else if (publicStatus.equals("released")) {
            cmbStatus.setNullSelectionItemId(PublicStatus.RELEASED.toString().toLowerCase());
            cmbStatus.addItem(PublicStatus.RELEASED.toString().toLowerCase());
            cmbStatus.addItem(PublicStatus.WITHDRAWN.toString().toLowerCase());
        }
        else if (publicStatus.equals("withdrawn")) {
            lblStatus.setValue("withdrawn");
        }
        else {
            cmbStatus.addItem(PublicStatus.valueOf(pubStatus));
        }
        // cmbStatus.select(Integer.valueOf(1));

        hlPublicStatus = new HorizontalLayout();
        hlPublicStatus.addComponent(new Label("Public Status is: "));
        hlPublicStatus.addComponent(cmbStatus);
        this.setHlPublicStatus(hlPublicStatus);
        this.setCmbStatus(cmbStatus);
        return hlPublicStatus;
    }

    public HorizontalLayout getHlPublicStatus() {
        return hlPublicStatus;
    }

    public void setHlPublicStatus(HorizontalLayout hlPublicStatus) {
        this.hlPublicStatus = hlPublicStatus;
    }

    public NativeSelect getCmbStatus() {
        return cmbStatus;
    }

    public void setCmbStatus(NativeSelect cmbStatus) {
        this.cmbStatus = cmbStatus;
    }

    public NativeSelect getCmbLockStatus() {
        return cmbLockStatus;
    }

    public void setCmbLockStatus(NativeSelect cmbLockStatus) {
        this.cmbLockStatus = cmbLockStatus;
    }

    private void updatePublicStatus(final Item item, final String comment) {
        Preconditions.checkNotNull(item, "Item is null");
        Preconditions.checkNotNull(comment, "Comment is null");
        // Update PublicStatus if there is a change
        if (!resourceProxy.getVersionStatus().equals(lblCurrentVersionStatus.getValue().toString().replace(status, ""))) {
            String publicStatusTxt = lblCurrentVersionStatus.getValue().toString().replace(status, "").toUpperCase();
            if (publicStatusTxt.equals("DELETE")) {
                new ResourceDeleteConfirmation(item, repositories.item(), mainWindow);
            }
            try {
                repositories.item().changePublicStatus(item,
                    lblCurrentVersionStatus.getValue().toString().replace(status, "").toUpperCase(), comment);
                if (publicStatusTxt.equals("SUBMITTED")) {
                    mainWindow.showNotification(new Window.Notification(ViewConstants.SUBMITTED,
                        Notification.TYPE_TRAY_NOTIFICATION));
                }
                else if (publicStatusTxt.equals("IN_REVISION")) {
                    mainWindow.showNotification(new Window.Notification(ViewConstants.IN_REVISION,
                        Notification.TYPE_TRAY_NOTIFICATION));
                }
                else if (publicStatusTxt.equals("RELEASED")) {
                    mainWindow.showNotification(new Window.Notification(ViewConstants.RELEASED,
                        Notification.TYPE_TRAY_NOTIFICATION));
                }
                else if (publicStatusTxt.equals("WITHDRAWN")) {
                    mainWindow.showNotification(new Window.Notification(ViewConstants.WITHDRAWN,
                        Notification.TYPE_TRAY_NOTIFICATION));
                }
            }
            catch (EscidocClientException e) {
                mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE));
            }
        }
    }

    private void updateLockStatus(final Item item, final String comment) {
        if (!resourceProxy.getLockStatus().equals(lblLockstatus.getValue().toString().replace(lockStatus, ""))) {
            String lockStatusTxt = lblLockstatus.getValue().toString().replace(lockStatus, "").toUpperCase();
            try {
                if (lockStatusTxt.contains("LOCKED")) {
                    repositories.item().lockResource(item, comment);
                    mainWindow.showNotification(new Window.Notification(ViewConstants.LOCKED,
                        Notification.TYPE_TRAY_NOTIFICATION));
                }
                else {
                    repositories.item().unlockResource(item, comment);
                    mainWindow.showNotification(new Window.Notification(ViewConstants.UNLOCKED,
                        Notification.TYPE_TRAY_NOTIFICATION));
                }

            }
            catch (EscidocClientException e) {
                mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE));
            }
        }
    }

    public void showEditableFields() {
        // Showing editable title
        txtFieldTitle = new TextField();
        txtFieldTitle.setValue(resourceProxy.getName());
        cssLayout.replaceComponent(descTitle, txtFieldTitle);
        // lblStatus.getValue().equals(status + "withdrawn")
        // Showing editable public status

        swapComponent =
            editStatus(lblStatus.getValue().toString().replace(resourceProxy.getType().getLabel() + " is ", ""));
        vlPropertiesLeft.replaceComponent(lblCurrentVersionStatus, swapComponent);

        // Showing editable LockStatus
        swapComponent = editLockStatus(lblLockstatus.getValue().toString().replace(status, ""));
        vlPropertiesLeft.replaceComponent(lblLockstatus, swapComponent);

    }

    public void saveEditableFields(String comment) {
        // Showing editable title
        descTitle.setValue(ViewConstants.ITEM_LABEL + txtFieldTitle.getValue());
        cssLayout.replaceComponent(txtFieldTitle, descTitle);
        Boolean isChangedTitle = false;
        Boolean isChangedPublicStatus = false;
        Boolean isChangedLockStatus = false;

        // Store operation logic here!
        cmbStatus = this.getCmbStatus();
        lblCurrentVersionStatus.setValue(resourceProxy.getType().getLabel() + " is " + cmbStatus.getValue().toString());
        vlPropertiesLeft.replaceComponent(this.getHlPublicStatus(), lblCurrentVersionStatus);
        // Store operation?
        cmbLockStatus = this.getCmbLockStatus();
        lblLockstatus.setValue(status + cmbLockStatus.getValue().toString());
        vlPropertiesLeft.replaceComponent(this.getHlLockStatus(), lblLockstatus);

        isChangedTitle = areEqual(resourceProxy.getName(), txtFieldTitle.getValue().toString());
        isChangedPublicStatus = areEqual(resourceProxy.getStatus(), cmbStatus.getValue().toString());
        isChangedLockStatus = areEqual(resourceProxy.getLockStatus(), cmbLockStatus.getValue().toString());
        try {
            controller.updateItem(isChangedTitle, isChangedPublicStatus, isChangedLockStatus, txtFieldTitle
                .getValue().toString(), cmbStatus.getValue().toString(), cmbLockStatus.getValue().toString(), comment);
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
        if (oldValue.equals(newValue)) {
            return false;
        }
        return true;
    }
}
