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

import org.escidoc.browser.controller.ContextController;
import org.escidoc.browser.model.internal.ContextProxyImpl;
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
import de.escidoc.core.resources.common.properties.PublicStatus;

public class ResourcePropertiesContextView extends ResourceProperties {

    private static final Logger LOG = LoggerFactory.getLogger(ResourcePropertiesContextView.class);

    private static final String CREATED = "created";

    private String status;

    private Label lblLockstatus;

    private Component swapComponent;

    private Label lblStatus;

    private Window subwindow;

    private ContextProxyImpl resourceProxy;

    private CssLayout cssLayout;

    private Window mainWindow;

    private final VerticalLayout vlPropertiesLeft = new VerticalLayout();

    private Label descTitle;

    private Label descLabel;

    private ContextController controller;

    private Router router;

    private TextField txtFieldTitle;

    private NativeSelect cmbStatus;

    private HorizontalLayout hlPublicStatus;

    private TextField txtFieldDescription;

    private TextField txtType;

    public ResourcePropertiesContextView(ContextProxyImpl resourceProxy, Router router, ContextController controller) {
        this.resourceProxy = (ContextProxyImpl) resourceProxy;
        this.mainWindow = router.getMainWindow();
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
        this.cssLayout.setHeight("100%");
        this.cssLayout.setMargin(false);
    }

    protected void createPermanentLink() {
        new CreateResourceLinksVH(mainWindow.getURL().toString(), resourceProxy, this, cssLayout, router);
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
        new BreadCrumbMenu(cssLayout, resourceProxy);
    }

    public void saveActionWindow() {
        if (controller.canUpdateContext()) {
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
    }

    protected void bindProperties() {

        final Panel pnlPropertiesLeft = buildLeftPropertiesPnl();
        final Panel pnlPropertiesRight = buildRightPnlProperties();

        final Label descMetadata1 = new Label("ID: " + resourceProxy.getId());

        status = resourceProxy.getType().getLabel() + " is ";

        lblStatus = new Label(status + resourceProxy.getStatus(), Label.CONTENT_RAW);
        lblStatus.setDescription(ViewConstants.DESC_STATUS2);

        lblLockstatus = new Label(status + resourceProxy.getType(), Label.CONTENT_RAW);
        lblLockstatus.setDescription(ViewConstants.DESC_LOCKSTATUS);
        if (controller.canUpdateContext()) {
            lblLockstatus.setStyleName("inset");
        }
        final Label descMetadata2 =
            new Label(ViewConstants.CREATED_BY + " " + resourceProxy.getCreator() + " on "
                + resourceProxy.getCreatedOn() + "<br/>" + ViewConstants.LAST_MODIFIED_BY + " "
                + resourceProxy.getModifier() + " on " + resourceProxy.getModifiedOn(), Label.CONTENT_XHTML);

        vlPropertiesLeft.addComponent(descMetadata1);
        vlPropertiesLeft.addComponent(lblStatus);
        vlPropertiesLeft.addComponent(lblLockstatus);
        pnlPropertiesLeft.addComponent(vlPropertiesLeft);
        cssLayout.addComponent(pnlPropertiesLeft);

        pnlPropertiesRight.addComponent(descMetadata2);
        cssLayout.addComponent(pnlPropertiesRight);
    }

    private Component editStatus(final String publicStatus) {
        cmbStatus = new NativeSelect();
        cmbStatus.setInvalidAllowed(false);
        cmbStatus.setNullSelectionAllowed(false);
        cmbStatus.addStyleName(Reindeer.LABEL_SMALL);
        LOG.debug("PublicStatus is " + publicStatus);
        if (publicStatus.contains(CREATED.toLowerCase())) {
            cmbStatus.addItem(PublicStatus.CREATED.toString().toLowerCase());
            cmbStatus.addItem(PublicStatus.OPENED.toString().toLowerCase());
        }
        else if (publicStatus.contains(PublicStatus.OPENED.toString().toLowerCase())) {
            cmbStatus.addItem(PublicStatus.OPENED.toString().toLowerCase());
            cmbStatus.addItem(PublicStatus.CLOSED.toString().toLowerCase());
        }
        else {
            cmbStatus.setNullSelectionItemId(PublicStatus.CLOSED.toString().toLowerCase());
            cmbStatus.addItem(PublicStatus.CLOSED.toString().toLowerCase());
        }

        hlPublicStatus = new HorizontalLayout();
        hlPublicStatus.addComponent(new Label("Public Status is: "));
        hlPublicStatus.addComponent(cmbStatus);
        this.setHlPublicStatus(hlPublicStatus);
        this.setCmbStatus(cmbStatus);
        return hlPublicStatus;
    }

    public void showEditableFields() {
        if (controller.canUpdateContext()) {
            // Showing editable title
            txtFieldTitle = new TextField();
            txtFieldTitle.setValue(resourceProxy.getName());
            txtFieldTitle.addStyleName("inlineblock");
            cssLayout.replaceComponent(descTitle, txtFieldTitle);

            txtFieldDescription = new TextField();
            txtFieldDescription.setValue(resourceProxy.getDescription());
            cssLayout.replaceComponent(descLabel, txtFieldDescription);

            swapComponent =
                editStatus(lblStatus.getValue().toString().replace(resourceProxy.getType().getLabel() + " is ", ""));
            vlPropertiesLeft.replaceComponent(lblStatus, swapComponent);

            txtType = new TextField();
            txtType.setValue(resourceProxy.getType());
            vlPropertiesLeft.replaceComponent(lblLockstatus, txtType);
        }
        else {
            router.getMainWindow().showNotification(
                "Unfortunately you have no access to run this operation on this resource",
                Notification.TYPE_TRAY_NOTIFICATION);
        }
    }

    public void saveEditableFields(String comment) {
        // Showing editable title
        descTitle.setValue(ViewConstants.RESOURCE_NAME_CONTEXT + txtFieldTitle.getValue());
        descLabel.setValue(ViewConstants.DESCRIPTION_LBL + txtFieldDescription.getValue());
        lblLockstatus.setValue(status + txtType.getValue());
        cssLayout.replaceComponent(txtFieldTitle, descTitle);
        cssLayout.replaceComponent(txtFieldDescription, descLabel);
        vlPropertiesLeft.replaceComponent(txtType, lblLockstatus);

        Boolean isChangedTitle = false;
        Boolean isChangedDescription = false;
        Boolean isChangedPublicStatus = false;
        Boolean isChangedType = false;
        // Store operation logic here!
        cmbStatus = this.getCmbStatus();
        if (cmbStatus.getValue() != null) {
            lblStatus.setValue(resourceProxy.getType().getLabel() + " is " + cmbStatus.getValue().toString());
            isChangedPublicStatus = areEqual(resourceProxy.getStatus(), cmbStatus.getValue().toString());
        }
        else {
            lblStatus.setValue(resourceProxy.getType().getLabel() + " is " + resourceProxy.getStatus().toString());
            cmbStatus.setValue(resourceProxy.getStatus().toString());
        }
        vlPropertiesLeft.replaceComponent(this.getHlPublicStatus(), lblStatus);

        isChangedTitle = areEqual(resourceProxy.getName(), txtFieldTitle.getValue().toString());
        isChangedDescription = areEqual(resourceProxy.getDescription(), txtFieldDescription.getValue().toString());
        isChangedType = areEqual(resourceProxy.getType().toString(), txtType.getValue().toString());

        try {
            controller.updateContext(isChangedTitle, isChangedDescription, isChangedPublicStatus, isChangedType,
                txtFieldTitle.getValue().toString(), cmbStatus.getValue().toString(), txtType.getValue().toString(),
                comment);
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
}
