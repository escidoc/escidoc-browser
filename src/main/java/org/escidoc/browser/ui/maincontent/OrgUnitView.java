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

import org.escidoc.browser.controller.OrgUnitController;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.internal.OrgUnitProxy;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.view.helpers.BreadCrumbMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.resources.common.properties.PublicStatus;

@SuppressWarnings("serial")
public class OrgUnitView extends View {

    private final ResourceProxy resourceProxy;

    private final Router router;

    private OrgUnitController orgUnitController;

    private Panel breadCrump;

    private VerticalLayout vlLeft;

    private Label lblStatus;

    private String status;

    private Component oldComponent;

    private Component swapComponent;

    private static final Logger LOG = LoggerFactory.getLogger(OrgUnitView.class);

    public OrgUnitView(final Router router, final ResourceProxy resourceProxy, OrgUnitController orgUnitController) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(orgUnitController, "orgUnitController is null: %s", orgUnitController);
        this.router = router;
        this.resourceProxy = resourceProxy;
        this.orgUnitController = orgUnitController;
    }

    public void buildContentPanel() {
        this.setImmediate(false);
        this.setWidth("100.0%");
        this.setHeight("100.0%");
        this.setStyleName(Runo.PANEL_LIGHT);
        this.setContent(buildVlContentPanel());
        handleLayoutListeners();
    }

    private VerticalLayout buildVlContentPanel() {
        VerticalLayout contentPanel = new VerticalLayout();
        contentPanel.setImmediate(false);
        contentPanel.setWidth("100.0%");

        contentPanel.setMargin(false, true, false, true);
        // breadCrumpPanel
        breadCrump = buildBreadCrumpPanel();
        contentPanel.addComponent(breadCrump);
        // resourcePropertiesPanel
        Panel resourcePropertiesPanel = buildResourcePropertiesPanel();
        contentPanel.addComponent(resourcePropertiesPanel);
        contentPanel.setComponentAlignment(resourcePropertiesPanel, Alignment.TOP_LEFT);

        // metaViewsPanel contains Panel for the DirectMembers & for the Metas
        Panel metaViewsPanel = buildMetaViewsPanel();
        contentPanel.addComponent(metaViewsPanel);
        contentPanel.setComponentAlignment(metaViewsPanel, Alignment.TOP_LEFT);

        return contentPanel;
    }

    private Panel buildMetaViewsPanel() {
        Panel mdView = new Panel();
        mdView.setImmediate(false);
        mdView.setWidth("100.0%");
        // mdView.setHeight("300px");
        mdView.setStyleName(Runo.PANEL_LIGHT);

        mdView.setContent(buildHlMetaViews());

        return mdView;
    }

    private HorizontalLayout buildHlMetaViews() {
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

    private Panel buildLeftPanel() {
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

    private VerticalLayout buildVlLeftPanel() {
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

    private Panel buildDirectMembersPanel() {
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

        return directMembersPanel;
    }

    private Panel buildRightPanel() {
        // common part: create layout
        Panel rightPanel = new Panel();
        rightPanel.setImmediate(false);
        rightPanel.setWidth("100.0%");
        rightPanel.setHeight("100.0%");

        // vlRightPanel
        rightPanel.setContent(buildVlRightPanel());

        return rightPanel;
    }

    private VerticalLayout buildVlRightPanel() {
        // common part: create layout
        VerticalLayout vlRightPanel = new VerticalLayout();
        vlRightPanel.setImmediate(false);
        vlRightPanel.setWidth("100.0%");
        vlRightPanel.setHeight("100.0%");
        vlRightPanel.setMargin(false);

        vlRightPanel.addComponent(buildMetaDataView());
        vlRightPanel.addComponent(buildParentsView());

        return vlRightPanel;
    }

    private Component buildParentsView() {
        return new ParentsView(resourceProxy, router.getMainWindow(), router, orgUnitController).asPanel();
    }

    private Panel buildMetaDataView() {
        return new OrgUnitMetadataRecordsView((OrgUnitProxy) resourceProxy, router, orgUnitController).asPanel();
    }

    private Panel buildResourcePropertiesPanel() {
        // common part: create layout
        Panel resourcePropertiesPanel = new Panel();
        resourcePropertiesPanel.setImmediate(false);
        resourcePropertiesPanel.setWidth("100.0%");
        resourcePropertiesPanel.setHeight("100%");
        resourcePropertiesPanel.setStyleName(Runo.PANEL_LIGHT);

        // vlResourceProperties
        resourcePropertiesPanel.setContent(buildVlResourceProperties());

        return resourcePropertiesPanel;
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
        new BreadCrumbMenu(breadCrumpPanel, resourceProxy.getName().toString());

        return breadCrumpPanel;
    }

    private VerticalLayout buildVlResourceProperties() {
        // common part: create layout
        VerticalLayout vlResourceProperties = new VerticalLayout();
        vlResourceProperties.setImmediate(false);
        vlResourceProperties.setWidth("100.0%");
        vlResourceProperties.setHeight("100px");
        vlResourceProperties.setMargin(false);

        // creating the properties / without the breadcrump
        createProperties(vlResourceProperties);
        return vlResourceProperties;
    }

    private void createProperties(VerticalLayout vlResourceProperties) {
        // Create Property fields. Probably not the best place for them to be
        buildBreadCrumpPanel();
        vlResourceProperties.addComponent(bindNameToHeader());
        vlResourceProperties.addComponent(bindDescription());
        addHorizontalRuler(vlResourceProperties);
        vlResourceProperties.addComponent(bindProperties());
    }

    private VerticalLayout bindNameToHeader() {
        VerticalLayout headerLayout = new VerticalLayout();
        headerLayout.setMargin(false);
        headerLayout.setWidth("100%");
        final Label headerContext = new Label("Organizational Unit: " + resourceProxy.getName());
        headerContext.setStyleName("h1 fullwidth");
        headerContext.setDescription("Organizational Unit");
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

    private HorizontalLayout bindProperties() {
        HorizontalLayout hlProperties = new HorizontalLayout();
        hlProperties.setWidth("100%");
        vlLeft = new VerticalLayout();
        VerticalLayout vlRight = new VerticalLayout();
        final Label descMetadata1 = new Label("ID: " + resourceProxy.getId());
        status = resourceProxy.getType().getLabel() + " is ";
        lblStatus = new Label(status + resourceProxy.getStatus(), Label.CONTENT_RAW);

        if (orgUnitController.hasAccess()) {
            lblStatus.setDescription(ViewConstants.DESC_STATUS);
            lblStatus.setStyleName("inset");
        }

        lblStatus.setDescription(ViewConstants.DESC_STATUS);

        vlLeft.addComponent(descMetadata1);
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
        if (orgUnitController.hasAccess()) {

            vlLeft.addListener(new LayoutClickListener() {
                private static final long serialVersionUID = 1L;

                @Override
                public void layoutClick(final LayoutClickEvent event) {
                    // Get the child component which was clicked
                    if (event.getChildComponent() != null) {
                        // Is Label?
                        if (event.getChildComponent().getClass().getCanonicalName() == "com.vaadin.ui.Label") {
                            final Label child = (Label) event.getChildComponent();
                            if ((child.getDescription() == ViewConstants.DESC_STATUS)) {
                                reSwapComponents();
                                oldComponent = event.getClickedComponent();
                                swapComponent = editStatus(child.getValue().toString().replace(status, ""));
                                vlLeft.replaceComponent(oldComponent, swapComponent);
                            }
                        }
                        // else {
                        // getWindow().showNotification(
                        // "The click was over a " + event.getChildComponent().getClass().getCanonicalName()
                        // + event.getChildComponent().getStyleName());
                        // }
                    }
                    else {
                        reSwapComponents();
                    }
                }

                private Component editStatus(final String publicStatus) {
                    final ComboBox cmbStatus = new ComboBox();
                    cmbStatus.setInvalidAllowed(false);
                    cmbStatus.setNullSelectionAllowed(false);
                    final String pubStatus = publicStatus.toUpperCase();
                    if (publicStatus.equals(PublicStatus.CREATED.toString())) {
                        cmbStatus.setNullSelectionItemId(PublicStatus.CREATED.toString().toUpperCase());
                        cmbStatus.addItem(PublicStatus.OPENED.toString().toUpperCase());
                    }
                    else if (publicStatus.equals(PublicStatus.OPENED.toString())) {
                        cmbStatus.setNullSelectionItemId(PublicStatus.OPENED.toString().toUpperCase());
                        cmbStatus.addItem(PublicStatus.CLOSED.toString().toUpperCase());
                    }
                    else if (publicStatus.equals(PublicStatus.CLOSED.toString())) {
                        cmbStatus.setNullSelectionItemId(PublicStatus.WITHDRAWN.toString().toUpperCase());
                    }
                    else {
                        cmbStatus.addItem(PublicStatus.valueOf(pubStatus));
                    }
                    cmbStatus.select(1);

                    return cmbStatus;
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
                            ((Label) oldComponent).setValue(status
                                + ((ComboBox) swapComponent).getValue().toString().toUpperCase());
                            if (((ComboBox) swapComponent)
                                .getValue().toString().toUpperCase().equals(PublicStatus.OPENED.toString())) {
                                orgUnitController.openOU();
                            }
                            if (((ComboBox) swapComponent)
                                .getValue().toString().toUpperCase().equals(PublicStatus.WITHDRAWN.toString())) {
                                orgUnitController.withdrawOU();
                            }
                            else if (((ComboBox) swapComponent)
                                .getValue().toString().toUpperCase().equals(PublicStatus.CLOSED.toString())) {
                                orgUnitController.closeOU();
                            }
                        }
                        vlLeft.replaceComponent(swapComponent, oldComponent);
                        swapComponent = null;
                    }
                }

            });
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resourceProxy == null) ? 0 : resourceProxy.hashCode());
        result = prime * result + ((router == null) ? 0 : router.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
        OrgUnitView other = (OrgUnitView) obj;
        if (resourceProxy == null) {
            if (other.resourceProxy != null) {
                return false;
            }
        }
        else if (!resourceProxy.equals(other.resourceProxy)) {
            return false;
        }
        if (router == null) {
            if (other.router != null) {
                return false;
            }
        }
        else if (!router.equals(other.router)) {
            return false;
        }
        return true;
    }

    public void refreshView() {
        orgUnitController.refreshView();
    }
}