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
import org.escidoc.browser.ui.view.helpers.FolderChildrenVH;
import org.escidoc.browser.ui.view.helpers.ResourcePropertiesFolderView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
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

    private VerticalLayout vlResourceProperties;

    protected Component swapComponent;

    protected Component oldComponent;

    private ResourcePropertiesFolderView containerPropertiesView;

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

        Panel resourcePropertiesPanel = buildResourcePropertiesPanel();
        vlContentPanel.addComponent(resourcePropertiesPanel);
        vlContentPanel.setComponentAlignment(resourcePropertiesPanel, Alignment.TOP_CENTER);

        //
        // // metaViewsPanel contains Panel for the DirectMembers & for the Metas
        Panel membersPanel = buildMembersPanel();
        vlContentPanel.addComponent(membersPanel);
        vlContentPanel.setComponentAlignment(membersPanel, Alignment.TOP_CENTER);

        return vlContentPanel;
    }

    private Panel buildMembersPanel() {
        // common part: create layout
        Panel metaViewsPanel = new Panel();
        metaViewsPanel.setImmediate(false);
        metaViewsPanel.setWidth("100.0%");
        metaViewsPanel.setHeight("500px");
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
        resourcePropertiesPanel.setHeight("130px");
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
        vlResourceProperties.setHeight("100.0%");
        vlResourceProperties.setMargin(false);

        // creating the properties / without the breadcrump

        containerPropertiesView = new ResourcePropertiesFolderView(resourceProxy, router, folderController);
        vlResourceProperties.addComponent(containerPropertiesView.getContentLayout());

        return vlResourceProperties;
    }

}
