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
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Runo;

import org.escidoc.browser.controller.ContextController;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.view.helpers.BreadCrumbMenu;
import org.escidoc.browser.ui.view.helpers.CreatePermanentLinkVH;
import org.escidoc.browser.ui.view.helpers.DirectMember;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.properties.PublicStatus;

@SuppressWarnings("serial")
public class ContextView extends View {

    private static final String CREATED = "created";

    private final Router router;

    private final ContextProxyImpl resourceProxy;

    private final EscidocServiceLocation serviceLocation;

    private final Window mainWindow;

    private final Repositories repositories;

    private VerticalLayout vlResourceProperties;

    private ContextController contextController;

    private Label lblStatus;

    private Label lblType;

    private Component oldComponent;

    private Component swapComponent;

    private VerticalLayout vlLeft;

    private VerticalLayout headerLayout;

    private VerticalLayout vlContentPanel;

    private Panel breadCrump;

    public ContextView(final Router router, final ResourceProxy resourceProxy, final Repositories repositories,
        ContextController contextController) throws EscidocClientException {

        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(contextController, "contextController is null: %s", contextController);

        this.serviceLocation = router.getServiceLocation();
        this.router = router;
        this.resourceProxy = (ContextProxyImpl) resourceProxy;
        this.setViewName(resourceProxy.getName());
        this.mainWindow = router.getMainWindow();
        this.repositories = repositories;
        this.contextController = contextController;
        buildContentPanel();
        handleLayoutListeners();
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
    @SuppressWarnings("unused")
    private VerticalLayout buildVlContentPanel() throws EscidocClientException {
        // common part: create layout
        vlContentPanel = new VerticalLayout();
        vlContentPanel.setImmediate(false);
        vlContentPanel.setWidth("100.0%");
        vlContentPanel.setHeight("100.0%");
        vlContentPanel.setMargin(true, true, false, true);

        // Permanent Link
        new CreatePermanentLinkVH(mainWindow.getURL().toString(), resourceProxy.getId(), resourceProxy
            .getType().toString(), vlContentPanel, serviceLocation);

        // breadCrumpPanel
        breadCrump = buildBreadCrumpPanel();
        vlContentPanel.addComponent(breadCrump);

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

    private static void addHorizontalRuler(AbstractComponentContainer contentLayout) {
        final Label descRuler = new Label("<hr />", Label.CONTENT_RAW);
        descRuler.setStyleName("hr");
        contentLayout.addComponent(descRuler);
    }

    private VerticalLayout bindNameToHeader() {
        headerLayout = new VerticalLayout();
        headerLayout.setMargin(false);
        headerLayout.setWidth("100%");
        final Label headerContext = new Label(ViewConstants.RESOURCE_NAME_CONTEXT + resourceProxy.getName());
        headerContext.setStyleName("h1 fullwidth");
        headerContext.setDescription(ViewConstants.RESOURCE_NAME_CONTEXT);
        headerLayout.addComponent(headerContext);
        return headerLayout;
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
        vlRightPanel.addComponent(buildMetaDataRecsPnl());

        return vlRightPanel;
    }

    private Panel buildMetaDataRecsPnl() {
        Panel metaDataRecsPnl = new ContextRightPanel(resourceProxy, router, contextController).asPanel();
        return metaDataRecsPnl;
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
        directMembersPanel.setImmediate(false);
        directMembersPanel.setWidth("100.0%");
        directMembersPanel.setHeight("100.0%");
        directMembersPanel.setStyleName(Runo.PANEL_LIGHT);

        // vlDirectMember
        VerticalLayout vlDirectMember = new VerticalLayout();
        vlDirectMember.setImmediate(false);
        vlDirectMember.setWidth("100.0%");
        vlDirectMember.setHeight("100.0%");
        vlDirectMember.setMargin(false);
        directMembersPanel.setContent(vlDirectMember);
        new DirectMember(serviceLocation, router, resourceProxy.getId(), mainWindow, repositories, directMembersPanel,
            ResourceType.CONTEXT.toString()).contextAsTree();

        return directMembersPanel;
    }

    private Panel buildResourcePropertiesPanel() {
        // common part: create layout
        Panel resourcePropertiesPanel = new Panel();
        resourcePropertiesPanel.setImmediate(false);
        resourcePropertiesPanel.setWidth("100.0%");
        resourcePropertiesPanel.setHeight("100%");
        resourcePropertiesPanel.setStyleName(Runo.PANEL_LIGHT);

        // vlResourceProperties
        vlResourceProperties = buildVlResourceProperties();
        resourcePropertiesPanel.setContent(vlResourceProperties);

        return resourcePropertiesPanel;
    }

    private VerticalLayout buildVlResourceProperties() {
        // common part: create layout
        vlResourceProperties = new VerticalLayout();
        vlResourceProperties.setImmediate(false);
        vlResourceProperties.setWidth("100.0%");
        vlResourceProperties.setHeight("100.0%");
        vlResourceProperties.setMargin(false);

        // creating the properties / without the breadcrump
        createProperties(vlResourceProperties);
        return vlResourceProperties;
    }

    private void createProperties(VerticalLayout vlResourceProperties) {
        // Create Property fields. Probably not the best place for them to be
        vlResourceProperties.addComponent(bindNameToHeader());
        addHorizontalRuler(vlResourceProperties);
        vlResourceProperties.addComponent(bindProperties());
    }

    @SuppressWarnings("unused")
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
        lblType = new Label(ViewConstants.CONTEXT_TYPE + resourceProxy.getPropertiesType(), Label.CONTENT_RAW);
        lblType.setDescription(ViewConstants.CONTEXT_TYPE);
        lblStatus =
            new Label(resourceProxy.getType().getLabel() + " is " + resourceProxy.getStatus(), Label.CONTENT_RAW);
        lblStatus.setDescription(ViewConstants.DESC_STATUS);

        vlLeft.addComponent(descMetadata1);
        vlLeft.addComponent(lblType);
        vlLeft.addComponent(lblStatus);

        // RIGHT SIDE
        final Label descMetadata2 =
            new Label(ViewConstants.CREATED_BY + " " + resourceProxy.getCreator() + " on "
                + resourceProxy.getCreatedOn() + "<br/>" + ViewConstants.LAST_MODIFIED_BY + " "
                + resourceProxy.getModifier() + " on " + resourceProxy.getModifiedOn(), Label.CONTENT_XHTML);

        descMetadata2.setStyleName("floatright columnheight50");
        descMetadata2.setWidth("65%");
        vlRight.addComponent(descMetadata2);

        hlProperties.addComponent(vlLeft);
        hlProperties.setExpandRatio(vlLeft, 0.4f);
        hlProperties.addComponent(vlRight);
        hlProperties.setExpandRatio(vlRight, 0.6f);
        return hlProperties;
    }

    private void handleLayoutListeners() {
        if (contextController.canUpdateContext()) {
            headerLayout.addListener(new LayoutClickListener() {

                @Override
                public void layoutClick(final LayoutClickEvent event) {
                    if (event.getChildComponent() != null) {
                        // Is Label?
                        if (event.getChildComponent().getClass().getCanonicalName() == "com.vaadin.ui.Label") {
                            final Label child = (Label) event.getChildComponent();
                            if ((child.getDescription() == ViewConstants.RESOURCE_NAME_CONTEXT)) {
                                reSwapComponents();
                                oldComponent = event.getClickedComponent();
                                swapComponent =
                                    editName(
                                        child.getValue().toString().replace(ViewConstants.RESOURCE_NAME_CONTEXT, ""),
                                        ViewConstants.RESOURCE_NAME_CONTEXT);
                                headerLayout.replaceComponent(oldComponent, swapComponent);
                            }
                        }
                    }
                    else {
                        reSwapComponents();
                    }
                }

                private Component editName(String name, String description) {
                    final TextField txtType = new TextField();
                    txtType.setInvalidAllowed(false);
                    txtType.setValue(name);
                    txtType.setDescription(description);

                    return txtType;
                }
            });

            vlLeft.addListener(new LayoutClickListener() {
                private static final long serialVersionUID = 1L;

                @Override
                public void layoutClick(final LayoutClickEvent event) {
                    if (event.getChildComponent() != null) {
                        // Is Label?
                        if (event.getChildComponent().getClass().getCanonicalName() == "com.vaadin.ui.Label") {
                            final Label child = (Label) event.getChildComponent();

                            if ((child.getDescription() == ViewConstants.CONTEXT_TYPE)) {
                                reSwapComponents();
                                oldComponent = event.getClickedComponent();
                                swapComponent =
                                    editType(child.getValue().toString().replace(ViewConstants.CONTEXT_TYPE, ""),
                                        ViewConstants.CONTEXT_TYPE);
                                vlLeft.replaceComponent(oldComponent, swapComponent);
                            }
                            else if ((child.getDescription() == ViewConstants.DESC_STATUS)
                                && (!child.getValue().equals(resourceProxy.getType().getLabel() + " is closed"))) {
                                reSwapComponents();
                                oldComponent = event.getClickedComponent();
                                swapComponent =
                                    editStatus(child
                                        .getValue().toString().replace(resourceProxy.getType().getLabel() + " is ", ""));
                                vlLeft.replaceComponent(oldComponent, swapComponent);
                            }
                        }
                    }
                    else {
                        reSwapComponents();
                    }
                }

                private ComboBox cmbStatus;

                private Component editStatus(final String lockStatus) {
                    cmbStatus = new ComboBox();
                    cmbStatus.setNullSelectionAllowed(false);
                    if (lockStatus.contains(CREATED)) {
                        cmbStatus.addItem(PublicStatus.OPENED.toString().toLowerCase());
                    }
                    else {
                        cmbStatus.addItem(PublicStatus.CLOSED.toString().toLowerCase());
                    }
                    cmbStatus.select(Integer.valueOf(1));
                    return cmbStatus;
                }

            });
        }

    }

    /**
     * Switch the component back to the original component (Label) after inline editing<br />
     * Used to swap the header and the properties
     * 
     * @throws EscidocClientException
     */
    private void reSwapComponents() {

        if (swapComponent != null) {
            if ((swapComponent instanceof TextField)
                && (((TextField) swapComponent).getDescription().equals(ViewConstants.CONTEXT_TYPE))) {
                ((Label) oldComponent).setValue(ViewConstants.CONTEXT_TYPE + ((TextField) swapComponent).getValue());
                try {
                    contextController.updateContextType(((TextField) swapComponent).getValue().toString(),
                        resourceProxy.getId());
                    mainWindow.showNotification("Context Type updated successfully",
                        Notification.TYPE_TRAY_NOTIFICATION);
                }
                catch (EscidocClientException e) {
                    mainWindow.showNotification(
                        "Could not update Context Type, an error occurred" + e.getLocalizedMessage(),
                        Notification.TYPE_ERROR_MESSAGE);
                }
                vlLeft.replaceComponent(swapComponent, oldComponent);
            }
            else if ((swapComponent instanceof ComboBox) && ((ComboBox) swapComponent).getValue() != null) {
                ((Label) oldComponent).setValue(resourceProxy.getType().getLabel() + " is "
                    + ((ComboBox) swapComponent).getValue());
                addCommentWindow(((ComboBox) swapComponent).getValue().toString());
                vlLeft.replaceComponent(swapComponent, oldComponent);
            }
            else if ((swapComponent instanceof TextField)
                && (((TextField) swapComponent).getDescription().equals(ViewConstants.RESOURCE_NAME_CONTEXT))) {
                ((Label) oldComponent).setValue(ViewConstants.RESOURCE_NAME_CONTEXT
                    + ((TextField) swapComponent).getValue());
                try {
                    contextController.updateContextName(((TextField) swapComponent).getValue().toString(),
                        resourceProxy.getId());
                    mainWindow.showNotification("Context Name updated successfully",
                        Notification.TYPE_TRAY_NOTIFICATION);
                    contextController.refreshView();
                }
                catch (EscidocClientException e) {
                    mainWindow.showNotification(
                        "Could not update Context Name, an error occurred" + e.getLocalizedMessage(),
                        Notification.TYPE_ERROR_MESSAGE);
                }
                headerLayout.replaceComponent(swapComponent, oldComponent);
            }

            swapComponent = null;
        }
    }

    /**
     * Called in the reSwapComponents()
     * 
     * @return String
     */
    public void addCommentWindow(final String status) {
        final Window subwindow = new Window(ViewConstants.SUBWINDOW_EDIT);
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

            @Override
            public void buttonClick(@SuppressWarnings("unused") com.vaadin.ui.Button.ClickEvent event) {
                // close the window by removing it from the parent window
                String comment = editor.getValue().toString();
                try {
                    contextController.updatePublicStatus(status, resourceProxy.getId(), comment);
                    mainWindow.showNotification("Context Status updated successfully",
                        Notification.TYPE_TRAY_NOTIFICATION);
                }
                catch (EscidocClientException e) {
                    mainWindow.showNotification(
                        "Could not update Context Type, an error occurred" + e.getLocalizedMessage(),
                        Notification.TYPE_ERROR_MESSAGE);
                }
                (subwindow.getParent()).removeWindow(subwindow);

            }
        });
        Button cancel = new Button("Cancel", new Button.ClickListener() {
            @Override
            public void buttonClick(@SuppressWarnings("unused") com.vaadin.ui.Button.ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);

            }
        });

        hl.addComponent(close);
        hl.addComponent(cancel);

        subwindow.addComponent(editor);
        subwindow.addComponent(hl);
        mainWindow.addWindow(subwindow);
    }

    private static TextField editType(final String contextType, String description) {
        final TextField txtType = new TextField();
        txtType.setInvalidAllowed(false);
        txtType.setValue(contextType);
        txtType.setDescription(description);

        return txtType;
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
        final ContextView other = (ContextView) obj;
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
