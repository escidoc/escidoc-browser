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
package org.escidoc.browser.ui.maincontent;

import com.google.common.base.Preconditions;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Runo;

import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.ContainerModel;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.ResourceDeleteConfirmation;
import org.escidoc.browser.ui.view.helpers.BreadCrumbMenu;
import org.escidoc.browser.ui.view.helpers.CreatePermanentLinkVH;
import org.escidoc.browser.ui.view.helpers.DirectMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.properties.LockStatus;
import de.escidoc.core.resources.common.properties.PublicStatus;
import de.escidoc.core.resources.om.container.Container;

/**
 * @author ARB
 * 
 */
@SuppressWarnings("serial")
public class ContainerView extends View {
    private static final String DESC_LOCKSTATUS = "lockstatus";

    private static final String DESC_STATUS2 = "status";

    private static final String DESC_HEADER = "header";

    private static final String SUBWINDOW_EDIT = "Add Comment to the Edit operation";

    private static final String CREATED_BY = "Created by ";

    private static final String FULLWIDHT_STYLE_NAME = "fullwidth";

    private static final String LAST_MODIFIED_BY = "Last modification by ";

    private static final String RESOURCE_NAME = "Container: ";

    private final VerticalLayout vlPropertiesLeft = new VerticalLayout();

    private final Router router;

    private final ContainerProxy resourceProxy;

    private String status;

    private final EscidocServiceLocation serviceLocation;

    private final Window mainWindow;

    private final Repositories repositories;

    private Component oldComponent;

    private Component swapComponent;

    private Label lblStatus;

    private Label lblLockstatus;

    private Window subwindow;

    private Label lblCurrentVersionStatus;

    private String lockStatus;

    private static final Logger LOG = LoggerFactory.getLogger(ContainerView.class);

    public ContainerView(final Router router, final ResourceProxy resourceProxy, final Repositories repositories)
        throws EscidocClientException {
        Preconditions.checkNotNull(router, "Router is null: %s", router);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkArgument(resourceProxy instanceof ContainerProxy, resourceProxy.getClass()
            + " is not an instance of ContainerProxy.class");
        this.serviceLocation = router.getServiceLocation();

        this.router = router;
        this.resourceProxy = (ContainerProxy) resourceProxy;
        this.setViewName(resourceProxy.getName());
        this.mainWindow = router.getMainWindow();
        this.repositories = repositories;
        handleLayoutListeners();
        buildContentPanel();
    }

    public Panel buildContentPanel() throws EscidocClientException {
        this.setImmediate(false);
        this.setWidth("100.0%");
        this.setHeight("100.0%");
        this.setStyleName(Runo.PANEL_LIGHT);

        // vlContentPanel assign a layout to this panel
        this.setContent(buildVlContentPanel());
        return this;
    }

    // the main panel has a Layout.
    // Elements of the view are bound in this layout of the main Panel
    private VerticalLayout buildVlContentPanel() throws EscidocClientException {
        // common part: create layout
        VerticalLayout vlContentPanel = new VerticalLayout();
        vlContentPanel.setImmediate(false);
        vlContentPanel.setWidth("100.0%");
        vlContentPanel.setHeight("100.0%");
        vlContentPanel.setMargin(true, true, false, true);

        new CreatePermanentLinkVH(mainWindow.getURL().toString(), resourceProxy.getId(), resourceProxy
            .getType().toString(), vlContentPanel, serviceLocation);

        // resourcePropertiesPanel
        Panel resourcePropertiesPanel = buildResourcePropertiesPanel();
        vlContentPanel.addComponent(resourcePropertiesPanel);
        vlContentPanel.setExpandRatio(resourcePropertiesPanel, 1.5f);

        // metaViewsPanel contains Panel for the DirectMembers & for the Metas
        Panel metaViewsPanel = buildMetaViewsPanel();
        vlContentPanel.addComponent(metaViewsPanel);
        vlContentPanel.setExpandRatio(metaViewsPanel, 8.0f);

        return vlContentPanel;
    }

    private Panel buildMetaViewsPanel() throws EscidocClientException {
        // common part: create layout
        Panel metaViewsPanel = new Panel();
        metaViewsPanel.setImmediate(false);
        metaViewsPanel.setWidth("100.0%");
        metaViewsPanel.setHeight("100.0%");
        metaViewsPanel.setStyleName(Runo.PANEL_LIGHT);

        // hlMetaViews
        HorizontalLayout hlMetaViews = buildHlMetaViews();
        metaViewsPanel.setContent(hlMetaViews);

        return metaViewsPanel;
    }

    private HorizontalLayout buildHlMetaViews() throws EscidocClientException {
        // common part: create layout
        HorizontalLayout hlMetaViews = new HorizontalLayout();
        hlMetaViews.setImmediate(false);
        hlMetaViews.setWidth("100.0%");
        hlMetaViews.setHeight("100.0%");
        hlMetaViews.setMargin(false);

        // leftPanel
        Panel leftPanel = buildLeftPanel();
        hlMetaViews.addComponent(leftPanel);
        hlMetaViews.setExpandRatio(leftPanel, 4.5f);

        // rightPanel
        Panel rightPanel = buildRightPanel();
        hlMetaViews.addComponent(rightPanel);
        hlMetaViews.setExpandRatio(rightPanel, 5.5f);

        return hlMetaViews;
    }

    private Panel buildRightPanel() {
        // common part: create layout
        Panel rightPanel = new Panel();
        rightPanel.setImmediate(false);
        rightPanel.setWidth("100.0%");
        rightPanel.setHeight("100.0%");

        // vlRightPanel
        VerticalLayout vlRightPanel = buildVlRightPanel();
        rightPanel.setContent(vlRightPanel);

        return rightPanel;
    }

    private VerticalLayout buildVlRightPanel() {
        // common part: create layout
        VerticalLayout vlRightPanel = new VerticalLayout();
        vlRightPanel.setImmediate(false);
        vlRightPanel.setWidth("100.0%");
        vlRightPanel.setHeight("100.0%");
        vlRightPanel.setMargin(false);

        // metaDataRecsAcc
        Accordion metaDataRecsAcc = new MetadataRecs(resourceProxy, repositories, router, this).asAccord();
        vlRightPanel.addComponent(metaDataRecsAcc);
        return vlRightPanel;
    }

    private Panel buildLeftPanel() throws EscidocClientException {
        // common part: create layout
        Panel leftPanel = new Panel();
        leftPanel.setImmediate(false);
        leftPanel.setWidth("100.0%");
        leftPanel.setHeight("100.0%");

        // vlLeftPanel
        VerticalLayout vlLeftPanel = buildVlLeftPanel();
        leftPanel.setContent(vlLeftPanel);

        return leftPanel;
    }

    private VerticalLayout buildVlLeftPanel() throws EscidocClientException {
        // common part: create layout
        VerticalLayout vlLeftPanel = new VerticalLayout();
        vlLeftPanel.setImmediate(false);
        vlLeftPanel.setWidth("100.0%");
        vlLeftPanel.setHeight("100.0%");
        vlLeftPanel.setMargin(false);

        // directMembersPanel
        Panel directMembersPanel = buildDirectMembersPanel();
        directMembersPanel.setStyleName("directmembers");
        vlLeftPanel.addComponent(directMembersPanel);

        return vlLeftPanel;
    }

    private Panel buildDirectMembersPanel() throws EscidocClientException {
        // common part: create layout
        Panel directMembersPanel = new Panel();
        directMembersPanel.setImmediate(true);
        directMembersPanel.setSizeFull();
        directMembersPanel.setScrollable(true);
        directMembersPanel.setStyleName(Runo.PANEL_LIGHT);

        // vlDirectMember
        VerticalLayout vlDirectMember = new VerticalLayout();
        vlDirectMember.setImmediate(true);
        vlDirectMember.setWidth("100.0%");
        vlDirectMember.setHeight("100.0%");
        vlDirectMember.setMargin(false);
        directMembersPanel.setContent(vlDirectMember);
        new DirectMember(serviceLocation, router, resourceProxy.getId(), mainWindow, repositories, directMembersPanel,
            ResourceType.CONTAINER.toString()).containerAsTree();

        return directMembersPanel;
    }

    private Panel buildResourcePropertiesPanel() {
        // common part: create layout
        Panel resourcePropertiesPanel = new Panel();
        resourcePropertiesPanel.setImmediate(false);
        resourcePropertiesPanel.setWidth("100.0%");
        resourcePropertiesPanel.setHeight("100.0%");
        resourcePropertiesPanel.setStyleName(Runo.PANEL_LIGHT);

        // vlResourceProperties
        VerticalLayout vlResourceProperties = buildVlResourceProperties();
        resourcePropertiesPanel.setContent(vlResourceProperties);

        return resourcePropertiesPanel;
    }

    private VerticalLayout buildVlResourceProperties() {
        // common part: create layout
        VerticalLayout vlResourceProperties = new VerticalLayout();
        vlResourceProperties.setImmediate(false);
        vlResourceProperties.setWidth("100.0%");
        vlResourceProperties.setHeight("100.0%");
        vlResourceProperties.setMargin(false);

        CssLayout cssLayout = new CssLayout();
        cssLayout.setWidth("100%");
        cssLayout.setHeight("100%");

        // creating the properties / without the breadcrump
        createProperties(cssLayout);
        vlResourceProperties.addComponent(cssLayout);
        return vlResourceProperties;
    }

    private void createProperties(CssLayout cssLayout) {
        createBreadCrumb(cssLayout);

        bindNameToHeader(cssLayout);
        bindDescription(cssLayout);
        addHorizontalRuler(cssLayout);
        bindProperties(cssLayout);
    }

    /**
     * Binding Context Properties 2 sets of labels in 2 rows
     * 
     * @param cssLayout
     */
    private void bindProperties(CssLayout cssLayout) {
        Panel pnlPropertiesLeft = buildLeftPropertiesPnl();
        Panel pnlPropertiesRight = buildRightPnlProperties();

        final Label descMetadata1 = new Label("ID: " + resourceProxy.getId());

        status = resourceProxy.getType().getLabel() + " is ";
        lockStatus = status;
        lblStatus = new Label(status + resourceProxy.getStatus(), Label.CONTENT_RAW);
        lblStatus.setDescription(DESC_STATUS2);

        lblLockstatus = new Label(status + resourceProxy.getLockStatus(), Label.CONTENT_RAW);
        lblLockstatus.setDescription(DESC_LOCKSTATUS);
        if (hasAccess()) {
            lblLockstatus.setStyleName("inset");
        }
        final Label descMetadata2 =
            new Label(CREATED_BY + " " + resourceProxy.getCreator() + " on " + resourceProxy.getCreatedOn() + "<br/>"
                + LAST_MODIFIED_BY + " " + resourceProxy.getModifier() + " on " + resourceProxy.getModifiedOn()
                + "<br />" + "Released by " + resourceProxy.getReleasedBy() + " on "
                + resourceProxy.getLatestVersionModifiedOn(), Label.CONTENT_XHTML);

        vlPropertiesLeft.addComponent(descMetadata1);
        if (hasAccess()) {
            status = "Latest status is ";
            lblCurrentVersionStatus = new Label(status + resourceProxy.getVersionStatus());
            lblCurrentVersionStatus.setDescription(DESC_STATUS2);
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

    private static Panel buildLeftPropertiesPnl() {
        Panel pnlPropertiesLeft = new Panel();
        pnlPropertiesLeft.setWidth("40%");
        pnlPropertiesLeft.setHeight("60px");
        pnlPropertiesLeft.setStyleName("floatleft");
        pnlPropertiesLeft.addStyleName(Runo.PANEL_LIGHT);

        VerticalLayout vl = new VerticalLayout();
        vl.setMargin(false);
        pnlPropertiesLeft.setContent(vl);
        return pnlPropertiesLeft;
    }

    private static Panel buildRightPnlProperties() {
        Panel pnlPropertiesRight = new Panel();
        pnlPropertiesRight.setWidth("60%");
        pnlPropertiesRight.setHeight("60px");
        pnlPropertiesRight.setStyleName("floatright");
        pnlPropertiesRight.addStyleName(Runo.PANEL_LIGHT);
        VerticalLayout vl = new VerticalLayout();
        vl.setMargin(false);
        pnlPropertiesRight.setContent(vl);
        return pnlPropertiesRight;
    }

    private static void addHorizontalRuler(CssLayout cssLayout) {
        final Label descRuler = new Label("<hr />", Label.CONTENT_RAW);
        descRuler.setStyleName("hr");
        cssLayout.addComponent(descRuler);
    }

    private void bindDescription(CssLayout cssLayout) {
        final Label description = new Label(resourceProxy.getDescription());
        description.setStyleName(FULLWIDHT_STYLE_NAME);
        cssLayout.addComponent(description);
    }

    private void createBreadCrumb(CssLayout cssLayout) {
        new BreadCrumbMenu(cssLayout, resourceProxy, mainWindow, serviceLocation, repositories);
    }

    /**
     * Building the Header Element that shows the title of the Container
     */
    private void bindNameToHeader(CssLayout cssLayout) {
        final Label headerContext = new Label(RESOURCE_NAME + resourceProxy.getName());
        headerContext.setStyleName("h2 fullwidth floatleft");
        headerContext.setWidth("80%");
        headerContext.setDescription(DESC_HEADER);
        cssLayout.addComponent(headerContext);
    }

    private void handleLayoutListeners() {
        if (hasAccess()) {

            vlPropertiesLeft.addListener(new LayoutClickListener() {
                private static final long serialVersionUID = 1L;

                @Override
                public void layoutClick(final LayoutClickEvent event) {
                    // Get the child component which was clicked

                    if (event.getChildComponent() != null) {
                        // Is Label?
                        if (event.getChildComponent().getClass().getCanonicalName() == "com.vaadin.ui.Label") {
                            final Label child = (Label) event.getChildComponent();
                            if ((child.getDescription() == DESC_STATUS2)
                                && (!lblStatus.getValue().equals(status + "withdrawn"))) {
                                reSwapComponents();
                                oldComponent = event.getClickedComponent();
                                swapComponent = editStatus(child.getValue().toString().replace(status, ""));
                                vlPropertiesLeft.replaceComponent(oldComponent, swapComponent);
                            }
                            else if (child.getDescription() == DESC_LOCKSTATUS) {
                                reSwapComponents();
                                oldComponent = event.getClickedComponent();
                                swapComponent = editLockStatus(child.getValue().toString().replace(status, ""));
                                vlPropertiesLeft.replaceComponent(oldComponent, swapComponent);
                            }
                        }
                        // else {
                        // // getWindow().showNotification(
                        // // "The click was over a " + event.getChildComponent().getClass().getCanonicalName()
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
                            // Because there should be no comment-window on Delete Operation
                            if (!(((ComboBox) swapComponent).getValue().equals("delete"))) {
                                addCommentWindow();
                            }
                            else {
                                updateContainer("");
                            }
                        }
                        vlPropertiesLeft.replaceComponent(swapComponent, oldComponent);
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
                        mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
                        e.printStackTrace();
                        return false;
                    }
                    catch (EscidocClientException e) {
                        mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
                        e.printStackTrace();
                        return false;
                    }
                    catch (URISyntaxException e) {
                        mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
                        e.printStackTrace();
                        return false;
                    }
                }

                public void addCommentWindow() {
                    subwindow = new Window(SUBWINDOW_EDIT);
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
                            // close the window by removing it from the parent window
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
                    mainWindow.addWindow(subwindow);
                }

                private void updatePublicStatus(Container container, String comment) {
                    // Update PublicStatus if there is a change
                    if (!resourceProxy.getVersionStatus().equals(
                        lblCurrentVersionStatus.getValue().toString().replace(status, ""))) {

                        String publicStatusTxt =
                            lblCurrentVersionStatus.getValue().toString().replace(status, "").toUpperCase();
                        if (publicStatusTxt.equals("DELETE")) {
                            new ResourceDeleteConfirmation(container, repositories.container(), mainWindow);

                        }
                        try {
                            repositories.container().changePublicStatus(container,
                                lblCurrentVersionStatus.getValue().toString().replace(status, "").toUpperCase(),
                                comment);
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

                private void updateLockStatus(Container container, String comment) {
                    // Update LockStatus if there is a change
                    if (!resourceProxy.getLockStatus().equals(
                        lblLockstatus.getValue().toString().replace(lockStatus, ""))) {
                        String lockStatusTxt =
                            lblLockstatus.getValue().toString().replace(lockStatus, "").toUpperCase();
                        try {
                            if (lockStatusTxt.contains("LOCKED")) {
                                repositories.container().unlockResource(container, comment);
                                mainWindow.showNotification(new Window.Notification(ViewConstants.LOCKED,
                                    Notification.TYPE_TRAY_NOTIFICATION));
                            }
                            else {
                                repositories.container().lockResource(container, comment);
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

                private void updateContainer(String comment) {
                    LOG.debug("Called the updateContainer");
                    Container container;
                    try {
                        container = repositories.container().findContainerById(resourceProxy.getId());
                        if (resourceProxy.getLockStatus().contains("unlocked")) {
                            updatePublicStatus(container, comment);
                            // retrive the container to get the last modifiaction date.
                            container = repositories.container().findContainerById(resourceProxy.getId());
                            updateLockStatus(container, comment);
                        }
                        else {
                            updateLockStatus(container, comment);
                            updatePublicStatus(container, comment);
                        }
                    }
                    catch (final EscidocClientException e) {
                        LOG.debug(e.getLocalizedMessage());
                    }
                }

            });
        }

    }

    public void reloadView() {
        try {
            router.show(resourceProxy, true);
            // refresh tree
            TreeDataSource treeDS = router.getLayout().getTreeDataSource();
            ContainerModel cnt = new ContainerModel(resourceProxy.getResource());
            ResourceModel parentModel = treeDS.getParent(cnt);

            boolean isSuccesful = treeDS.remove(cnt);
            if (isSuccesful) {
                // Need to reload again the item
                Container containerT = repositories.container().findContainerById(resourceProxy.getId());
                treeDS.addChild(parentModel, new ContainerModel(containerT));
            }
        }
        catch (EscidocClientException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private boolean hasAccess() {
        try {
            return repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_CONTAINER).forResource(resourceProxy.getId())
                .permitted();
        }
        catch (EscidocClientException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
        catch (URISyntaxException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resourceProxy == null) ? 0 : resourceProxy.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContainerView other = (ContainerView) obj;
        if (resourceProxy == null) {
            if (other.resourceProxy != null) {
                return false;
            }
        }
        else if (!resourceProxy.equals(other.resourceProxy)) {
            return false;
        }
        return true;
    }
}
