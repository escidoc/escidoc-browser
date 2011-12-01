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

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.view.helpers.BreadCrumbMenu;
import org.escidoc.browser.ui.view.helpers.CreatePermanentLinkVH;
import org.escidoc.browser.ui.view.helpers.DirectMember;

import com.google.common.base.Preconditions;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class ContextView extends View {

    private static final String CREATED_BY = "Created by";

    private static final String LAST_MODIFIED_BY = "Last modification by";

    private static final String RESOURCE_NAME = "Workspace: ";

    private final Router router;

    private final ResourceProxy resourceProxy;

    private final EscidocServiceLocation serviceLocation;

    private final Window mainWindow;

    private final Repositories repositories;

    private VerticalLayout vlResourceProperties;

    public ContextView(final Router router, final ResourceProxy resourceProxy, final Repositories repositories)
        throws EscidocClientException {

        Preconditions.checkNotNull(router, "mainSite is null: %s", router);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);

        this.serviceLocation = router.getServiceLocation();
        this.router = router;
        this.resourceProxy = resourceProxy;
        this.setViewName(resourceProxy.getName());
        this.mainWindow = router.getMainWindow();
        this.repositories = repositories;
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

        // Permanent Link
        new CreatePermanentLinkVH(mainWindow.getURL().toString(), resourceProxy.getId(), resourceProxy
            .getType().toString(), vlContentPanel, serviceLocation);

        // breadCrumpPanel
        vlContentPanel.addComponent(buildBreadCrumpPanel());

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

    /**
     * Binding Context Properties 2 sets of labels in 2 rows
     */
    private void bindProperties(AbstractComponentContainer componentContainer) {
        final Label descMetadata1 =
            new Label("ID: " + resourceProxy.getId() + " <br /> " + resourceProxy.getType().getLabel() + " is "
                + resourceProxy.getStatus(), Label.CONTENT_RAW);
        descMetadata1.setWidth("35%");
        descMetadata1.setStyleName("floatleft columnheight50");
        componentContainer.addComponent(descMetadata1);

        // RIGHT SIDE
        final Label descMetadata2 =
            new Label(CREATED_BY + " " + resourceProxy.getCreator() + " on " + resourceProxy.getCreatedOn() + "<br/>"
                + LAST_MODIFIED_BY + " " + resourceProxy.getModifier() + " on " + resourceProxy.getModifiedOn(),
                Label.CONTENT_XHTML);

        descMetadata2.setStyleName("floatright columnheight50");
        descMetadata2.setWidth("65%");
        componentContainer.addComponent(descMetadata2);
    }

    private void addHorizontalRuler(AbstractComponentContainer contentLayout) {
        final Label descRuler = new Label("<hr />", Label.CONTENT_RAW);
        descRuler.setStyleName("hr");
        contentLayout.addComponent(descRuler);
    }

    private void bindNameToHeader(AbstractComponentContainer contentLayout) {
        final Label headerContext = new Label(RESOURCE_NAME + resourceProxy.getName());
        headerContext.setStyleName("h1 fullwidth");
        contentLayout.addComponent(headerContext);
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
        Accordion metaDataRecsAcc = buildMetaDataRecsAcc();
        vlRightPanel.addComponent(metaDataRecsAcc);

        return vlRightPanel;
    }

    private Accordion buildMetaDataRecsAcc() {
        // common part: create layout
        Accordion metaDataRecsAcc = new MetadataRecsContext(resourceProxy, mainWindow, router).asAccord();
        return metaDataRecsAcc;
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

    private void createProperties(CssLayout contentContainer) {
        // Create Property fields. Probably not the best place for them to be
        bindNameToHeader(contentContainer);
        addHorizontalRuler(contentContainer);
        bindProperties(contentContainer);
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
