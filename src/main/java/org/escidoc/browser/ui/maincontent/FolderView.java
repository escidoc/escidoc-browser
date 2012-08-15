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
package org.escidoc.browser.ui.maincontent;

import java.net.URISyntaxException;

import org.escidoc.browser.controller.FolderController;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContainerProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.helper.ViewHelper;
import org.escidoc.browser.ui.listeners.ResourceDeleteConfirmation;
import org.escidoc.browser.ui.view.helpers.BreadCrumbMenu;
import org.escidoc.browser.ui.view.helpers.CreateResourceLinksVH;
import org.escidoc.browser.ui.view.helpers.FolderChildrenVH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.properties.LockStatus;
import de.escidoc.core.resources.common.properties.PublicStatus;
import de.escidoc.core.resources.om.container.Container;

public class FolderView extends View {

    private EscidocServiceLocation serviceLocation;

    private Router router;

    private ContainerProxyImpl resourceProxy;

    private Repositories repositories;

    private FolderController folderController;

    private VerticalLayout vlContentPanel;

    private VerticalLayout vlResourceProperties;

    protected Component swapComponent;

    protected Component oldComponent;

    private Label lblStatus;

    private String status;

    private Label lblCurrentVersionStatus;

    private VerticalLayout vlLeft;

    private static final Logger LOG = LoggerFactory.getLogger(FolderView.class);

    public FolderView(Router router, ResourceProxy resourceProxy, Repositories repositories,
        FolderController folderController) throws EscidocClientException {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(folderController, "folderController is null: %s", folderController);

        this.serviceLocation = router.getServiceLocation();
        this.router = router;
        this.resourceProxy = (ContainerProxyImpl) resourceProxy;
        this.setViewName(resourceProxy.getName());
        this.repositories = repositories;
        this.folderController = folderController;

        buildContentPanel();
        handleLayoutListeners();
    }

    private Panel buildContentPanel() {
        this.setImmediate(false);
        this.setWidth("100.0%");
        this.setHeight("100.0%");
        this.setStyleName(Runo.PANEL_LIGHT);

        // vlContentPanel assign a layout to this panel
        this.setContent(buildVlContentPanel());
        return this;

    }

    private VerticalLayout buildVlContentPanel() {
        // common part: create layout
        vlContentPanel = new VerticalLayout();
        vlContentPanel.setImmediate(false);
        vlContentPanel.setWidth("100.0%");
        vlContentPanel.setHeight("100.0%");
        vlContentPanel.setMargin(false, true, false, true);
        vlContentPanel.addStyleName("red");

        // breadCrumpPanel
        Panel breadCrumb = buildBreadCrumpPanel();
        vlContentPanel.addComponent(breadCrumb);
        vlContentPanel.setComponentAlignment(breadCrumb, Alignment.TOP_RIGHT);
        // Permanent Link
        new CreateResourceLinksVH(router.getMainWindow().getURL().toString(), resourceProxy, vlContentPanel, router);

        // resourcePropertiesPanel
        Panel resourcePropertiesPanel = buildResourcePropertiesPanel();
        vlContentPanel.addComponent(resourcePropertiesPanel);
        vlContentPanel.setComponentAlignment(resourcePropertiesPanel, Alignment.TOP_RIGHT);
        //
        // // metaViewsPanel contains Panel for the DirectMembers & for the Metas
        Panel membersPanel = buildMembersPanel();
        vlContentPanel.addComponent(membersPanel);
        vlContentPanel.setComponentAlignment(membersPanel, Alignment.TOP_RIGHT);

        return vlContentPanel;
    }

    private Panel buildMembersPanel() {
        // common part: create layout
        Panel metaViewsPanel = new Panel();
        metaViewsPanel.setImmediate(false);
        metaViewsPanel.setWidth("100.0%");
        // metaViewsPanel.setHeight("100.0%");
        metaViewsPanel.setStyleName(Runo.PANEL_LIGHT);

        FolderChildrenVH folderChildrenView = new FolderChildrenVH(folderController, resourceProxy, repositories);
        folderChildrenView.buildTable();
        metaViewsPanel.setContent(folderChildrenView);

        return metaViewsPanel;
    }

    private Panel buildResourcePropertiesPanel() {
        // common part: create layout
        Panel resourcePropertiesPanel = new Panel();
        resourcePropertiesPanel.setImmediate(false);
        resourcePropertiesPanel.setWidth("100.0%");
        resourcePropertiesPanel.setHeight("120px");
        resourcePropertiesPanel.setStyleName(Runo.PANEL_LIGHT);

        // vlResourceProperties
        VerticalLayout vlResourceProperties = buildVlResourceProperties();
        resourcePropertiesPanel.setContent(vlResourceProperties);

        return resourcePropertiesPanel;
    }

    private VerticalLayout buildVlResourceProperties() {
        // common part: create layout
        vlResourceProperties = new VerticalLayout();
        vlResourceProperties.setImmediate(false);
        vlResourceProperties.setWidth("100.0%");
        vlResourceProperties.setHeight("100%");
        vlResourceProperties.setMargin(false);

        // creating the properties / without the breadcrump
        createProperties(vlResourceProperties);
        return vlResourceProperties;
    }

    private void createProperties(VerticalLayout vlResourceProperties) {
        // Create Property fields. Probably not the best place for them to be
        vlResourceProperties.addComponent(bindNameToHeader());
        vlResourceProperties.addComponent(bindDescription());
        addHorizontalRuler(vlResourceProperties);
        vlResourceProperties.addComponent(bindProperties());

    }

    private VerticalLayout bindNameToHeader() {
        VerticalLayout headerLayout = new VerticalLayout();
        headerLayout.setMargin(false);
        headerLayout.setWidth("100%");
        final Label headerContext = new Label(ViewConstants.RESOURCE_NAME_CONTEXT + resourceProxy.getName());
        headerContext.setStyleName("h1 fullwidth");
        headerContext.setDescription(ViewConstants.RESOURCE_NAME_CONTEXT);
        headerLayout.addComponent(headerContext);
        return headerLayout;
    }

    private VerticalLayout bindDescription() {
        VerticalLayout descLayout = new VerticalLayout();
        descLayout.setMargin(false);
        descLayout.setWidth("100%");
        final Label headerContext = new Label(ViewConstants.DESCRIPTION_LBL + resourceProxy.getDescription());
        headerContext.setStyleName("fullwidth");

        descLayout.addComponent(headerContext);
        return descLayout;
    }

    private static void addHorizontalRuler(AbstractComponentContainer contentLayout) {
        final Label descRuler = new Label("<hr />", Label.CONTENT_RAW);
        descRuler.setStyleName("hr");
        contentLayout.addComponent(descRuler);
    }

    /**
     * Binding Context Properties 2 sets of labels in 2 rows
     * 
     * @return
     */
    private HorizontalLayout bindProperties() {
        HorizontalLayout hlProperties = new HorizontalLayout();
        hlProperties.setWidth("100%");
        vlLeft = new VerticalLayout();
        VerticalLayout vlRight = new VerticalLayout();
        final Label descMetadata1 = new Label("ID: " + resourceProxy.getId());
        status = resourceProxy.getType().getLabel() + " is ";
        lblStatus = new Label(status + resourceProxy.getStatus(), Label.CONTENT_RAW);
        lblStatus.setDescription(ViewConstants.DESC_STATUS);
        vlLeft.addComponent(descMetadata1);
        if (folderController.hasAccess()) {
            status = "Latest status is ";
            lblCurrentVersionStatus = new Label(status + resourceProxy.getVersionStatus());
            lblCurrentVersionStatus.setDescription(ViewConstants.DESC_STATUS);
            lblCurrentVersionStatus.setStyleName("inset");
            vlLeft.addComponent(lblCurrentVersionStatus);

        }
        else {
            vlLeft.addComponent(lblStatus);
        }

        // RIGHT SIDE
        vlRight.addComponent(ViewHelper.buildCreateAndModifyLabel(resourceProxy));

        hlProperties.addComponent(vlLeft);
        hlProperties.setExpandRatio(vlLeft, 0.4f);
        hlProperties.addComponent(vlRight);
        hlProperties.setExpandRatio(vlRight, 0.6f);
        return hlProperties;
    }

    private void handleLayoutListeners() {
        if (folderController.hasAccess()) {
            vlLeft.addListener(new LayoutClickListener() {
                private static final long serialVersionUID = 1L;

                private Window subwindow;

                @Override
                public void layoutClick(final LayoutClickEvent event) {
                    // Get the child component which was clicked

                    if (event.getChildComponent() != null) {
                        // Is Label?
                        if (event.getChildComponent().getClass().getCanonicalName() == "com.vaadin.ui.Label") {
                            final Label child = (Label) event.getChildComponent();
                            if ((child.getDescription() == ViewConstants.DESC_STATUS)
                                && (!lblStatus.getValue().equals(status + "withdrawn"))) {
                                reSwapComponents();
                                oldComponent = event.getClickedComponent();
                                swapComponent = editStatus(child.getValue().toString().replace(status, ""));
                                vlLeft.replaceComponent(oldComponent, swapComponent);
                            }
                            else if (child.getDescription() == ViewConstants.DESC_LOCKSTATUS) {
                                reSwapComponents();
                                oldComponent = event.getClickedComponent();
                                swapComponent = editLockStatus(child.getValue().toString().replace(status, ""));
                                vlLeft.replaceComponent(oldComponent, swapComponent);
                            }
                        }
                        // else {
                        // // getWindow().showNotification(
                        // // "The click was over a " +
                        // event.getChildComponent().getClass().getCanonicalName()
                        // // + event.getChildComponent().getStyleName());
                        // }
                    }
                    else {
                        reSwapComponents();
                    }
                }

                /**
                 * Switch the component back to the original component (Label) after inline editing
                 */
                private void reSwapComponents() {

                    if (swapComponent != null) {
                        if (swapComponent instanceof Label) {
                            ((Label) oldComponent).setValue(((TextArea) swapComponent).getValue());
                        }
                        else if ((swapComponent instanceof ComboBox) && ((ComboBox) swapComponent).getValue() != null) {
                            ((Label) oldComponent).setValue(status + ((ComboBox) swapComponent).getValue());
                            // Because there should be no comment-window on
                            // Delete Operation
                            if (!(((ComboBox) swapComponent).getValue().equals("delete"))) {
                                addCommentWindow();
                            }
                            else {
                                updateContainer("");
                            }
                        }
                        vlLeft.replaceComponent(swapComponent, oldComponent);
                        swapComponent = null;
                    }
                }

                private Component editLockStatus(final String lockStatus) {
                    final ComboBox cmbLockStatus = new ComboBox();
                    cmbLockStatus.setNullSelectionAllowed(false);
                    if (lockStatus.contains("unlocked")) {
                        cmbLockStatus.addItem(LockStatus.LOCKED.toString().toLowerCase());
                    }
                    else {
                        cmbLockStatus.addItem(LockStatus.UNLOCKED.toString().toLowerCase());
                    }
                    cmbLockStatus.select(1);
                    return cmbLockStatus;

                }

                private Component editStatus(final String publicStatus) {
                    final ComboBox cmbStatus = new ComboBox();
                    cmbStatus.setInvalidAllowed(false);
                    cmbStatus.setNullSelectionAllowed(false);
                    final String pubStatus = publicStatus.toUpperCase();
                    if (publicStatus.equals("pending")) {
                        cmbStatus.addItem(PublicStatus.PENDING.toString().toLowerCase());
                        cmbStatus.addItem(PublicStatus.SUBMITTED.toString().toLowerCase());
                        cmbStatus.setNullSelectionItemId(PublicStatus.PENDING.toString().toLowerCase());
                        if (hasAccessDelResource()) {
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
                        // do nothing
                    }
                    else {
                        cmbStatus.addItem(PublicStatus.valueOf(pubStatus));
                    }
                    cmbStatus.select(1);

                    return cmbStatus;
                }

                private boolean hasAccessDelResource() {
                    try {
                        return repositories
                            .pdp().forCurrentUser().isAction(ActionIdConstants.DELETE_CONTAINER)
                            .forResource(resourceProxy.getId()).permitted();
                    }
                    catch (UnsupportedOperationException e) {
                        router.getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
                        e.printStackTrace();
                        return false;
                    }
                    catch (EscidocClientException e) {
                        router.getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
                        e.printStackTrace();
                        return false;
                    }
                    catch (URISyntaxException e) {
                        router.getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
                        e.printStackTrace();
                        return false;
                    }
                }

                public void addCommentWindow() {
                    subwindow = new Window(ViewConstants.SUBWINDOW_EDIT);
                    subwindow.setModal(true);
                    // Configure the windws layout; by default a VerticalLayout
                    VerticalLayout layout = (VerticalLayout) subwindow.getContent();
                    layout.setMargin(true);
                    layout.setSpacing(true);
                    layout.setSizeUndefined();

                    final TextArea editor = new TextArea("Your Comment");
                    editor.setRequired(true);
                    editor.setRequiredError("The Field may not be empty.");

                    HorizontalLayout hl = new HorizontalLayout();

                    Button close = new Button("Update", new Button.ClickListener() {
                        // inline click-listener
                        @Override
                        public void buttonClick(ClickEvent event) {
                            // close the window by removing it from the
                            // parent window
                            updateContainer(editor.getValue().toString());
                            (subwindow.getParent()).removeWindow(subwindow);
                        }
                    });
                    Button cancel = new Button("Cancel", new Button.ClickListener() {
                        @Override
                        public void buttonClick(ClickEvent event) {
                            (subwindow.getParent()).removeWindow(subwindow);
                        }
                    });

                    hl.addComponent(close);
                    hl.addComponent(cancel);

                    subwindow.addComponent(editor);
                    subwindow.addComponent(hl);
                    router.getMainWindow().addWindow(subwindow);
                }

                private void updatePublicStatus(Container container, String comment) {
                    // Update PublicStatus if there is a change
                    if (!resourceProxy.getVersionStatus().equals(
                        lblCurrentVersionStatus.getValue().toString().replace(status, ""))) {

                        String publicStatusTxt =
                            lblCurrentVersionStatus.getValue().toString().replace(status, "").toUpperCase();
                        if (publicStatusTxt.equals("DELETE")) {
                            new ResourceDeleteConfirmation(container, repositories.container(), router.getMainWindow());

                        }
                        try {
                            repositories.container().changePublicStatus(container,
                                lblCurrentVersionStatus.getValue().toString().replace(status, "").toUpperCase(),
                                comment);
                            if (publicStatusTxt.equals("SUBMITTED")) {
                                router.getMainWindow().showNotification(
                                    new Window.Notification(ViewConstants.SUBMITTED,
                                        Notification.TYPE_TRAY_NOTIFICATION));
                            }
                            else if (publicStatusTxt.equals("IN_REVISION")) {
                                router.getMainWindow().showNotification(
                                    new Window.Notification(ViewConstants.IN_REVISION,
                                        Notification.TYPE_TRAY_NOTIFICATION));
                            }
                            else if (publicStatusTxt.equals("RELEASED")) {
                                router
                                    .getMainWindow().showNotification(
                                        new Window.Notification(ViewConstants.RELEASED,
                                            Notification.TYPE_TRAY_NOTIFICATION));

                            }
                            else if (publicStatusTxt.equals("WITHDRAWN")) {
                                router.getMainWindow().showNotification(
                                    new Window.Notification(ViewConstants.WITHDRAWN,
                                        Notification.TYPE_TRAY_NOTIFICATION));
                            }
                        }
                        catch (EscidocClientException e) {
                            router.getMainWindow().showNotification(
                                new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                                    Notification.TYPE_ERROR_MESSAGE));
                        }
                    }
                }

                private void updateContainer(String comment) {
                    LOG.debug("Called the updateContainer");
                    Container container;
                    try {
                        container = repositories.container().findContainerById(resourceProxy.getId());
                        if (resourceProxy.getLockStatus().contains("unlocked")) {
                            container = repositories.container().findContainerById(resourceProxy.getId());
                            updatePublicStatus(container, comment);
                        }
                        else {
                            updatePublicStatus(container, comment);
                        }
                    }
                    catch (final EscidocClientException e) {
                        LOG.debug("Infrastructure Exception " + e.getLocalizedMessage());
                    }
                }

            });
        }

    }

    private Panel buildBreadCrumpPanel() {
        // common part: create layout
        Panel breadCrumpPanel = new Panel();
        breadCrumpPanel.setImmediate(false);
        breadCrumpPanel.setWidth("100.0%");
        breadCrumpPanel.setHeight("30px");
        breadCrumpPanel.setStyleName(Runo.PANEL_LIGHT);

        // vlBreadCrump
        VerticalLayout vlBreadCrump = new VerticalLayout();
        vlBreadCrump.setImmediate(false);
        vlBreadCrump.setWidth("100.0%");
        vlBreadCrump.setHeight("100.0%");
        vlBreadCrump.setMargin(false);
        breadCrumpPanel.setContent(vlBreadCrump);

        // BreadCreumb
        new BreadCrumbMenu(breadCrumpPanel, resourceProxy);

        return breadCrumpPanel;
    }

}
