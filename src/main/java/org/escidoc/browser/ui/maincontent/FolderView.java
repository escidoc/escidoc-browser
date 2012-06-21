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

import org.escidoc.browser.controller.FolderController;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContainerProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.helper.ViewHelper;
import org.escidoc.browser.ui.view.helpers.BreadCrumbMenu;
import org.escidoc.browser.ui.view.helpers.CreateResourceLinksVH;
import org.escidoc.browser.ui.view.helpers.FolderChildrenVH;

import com.google.common.base.Preconditions;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class FolderView extends View {

    private EscidocServiceLocation serviceLocation;

    private Router router;

    private ContainerProxyImpl resourceProxy;

    private Repositories repositories;

    private FolderController folderController;

    private VerticalLayout vlContentPanel;

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

        // breadCrumpPanel
        vlContentPanel.addComponent(buildBreadCrumpPanel());
        // Permanent Link
        new CreateResourceLinksVH(router.getMainWindow().getURL().toString(), resourceProxy, vlContentPanel, router);

        // resourcePropertiesPanel
        Panel resourcePropertiesPanel = buildResourcePropertiesPanel();
        vlContentPanel.addComponent(resourcePropertiesPanel);
        vlContentPanel.setExpandRatio(resourcePropertiesPanel, 1.5f);
        //
        // // metaViewsPanel contains Panel for the DirectMembers & for the Metas
        Panel membersPanel = buildMembersPanel();
        vlContentPanel.addComponent(membersPanel);
        vlContentPanel.setExpandRatio(membersPanel, 8.5f);

        return vlContentPanel;
    }

    private Panel buildMembersPanel() {
        // common part: create layout
        Panel metaViewsPanel = new Panel();
        metaViewsPanel.setImmediate(false);
        metaViewsPanel.setWidth("100.0%");
        metaViewsPanel.setHeight("100.0%");
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
        VerticalLayout vlLeft = new VerticalLayout();
        VerticalLayout vlRight = new VerticalLayout();
        final Label descMetadata1 = new Label("ID: " + resourceProxy.getId());
        Label lblStatus =
            new Label(resourceProxy.getType().getLabel() + " is " + resourceProxy.getStatus(), Label.CONTENT_RAW);
        lblStatus.setDescription(ViewConstants.DESC_STATUS);
        vlLeft.addComponent(descMetadata1);
        vlLeft.addComponent(lblStatus);

        // RIGHT SIDE
        vlRight.addComponent(ViewHelper.buildCreateAndModifyLabel(resourceProxy));

        hlProperties.addComponent(vlLeft);
        hlProperties.setExpandRatio(vlLeft, 0.4f);
        hlProperties.addComponent(vlRight);
        hlProperties.setExpandRatio(vlRight, 0.6f);
        return hlProperties;
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
