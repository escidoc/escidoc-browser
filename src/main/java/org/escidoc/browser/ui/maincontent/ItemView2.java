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

import com.google.common.base.Preconditions;

import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;

import org.escidoc.browser.controller.ItemController;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ItemProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ItemView2 extends View {
    private ItemProxyImpl resourceProxy;

    private Repositories repositories;

    private Window mainWindow;

    private Router router;

    private EscidocServiceLocation serviceLocation;

    private Panel panelView;

    private ItemController controller;

    private String status;

    private String lockStatus;

    private Label lblStatus;

    private Label lblLockstatus;

    private Label lblCurrentVersionStatus;

    private Table table;

    public ItemView2(Router router, ResourceProxy resourceProxy, ItemController itemController)
        throws EscidocClientException {
        Preconditions.checkNotNull(itemController, "itemController is null: %s", itemController);
        Preconditions.checkNotNull(router, "router is null.");
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null.");

        this.resourceProxy = (ItemProxyImpl) resourceProxy;
        this.repositories = router.getRepositories();
        this.setViewName(resourceProxy.getName());
        this.mainWindow = router.getMainWindow();
        this.router = router;
        this.serviceLocation = router.getServiceLocation();
        this.controller = itemController;
        panelView = buildContentPanel();
    }

    private Panel buildContentPanel() throws EscidocClientException {
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
        vlContentPanel.setMargin(false);

        final HorizontalSplitPanel horiz = buildHorizontalSplit();
        vlContentPanel.addComponent(horiz);

        return vlContentPanel;
    }

    private HorizontalSplitPanel buildHorizontalSplit() {
        final HorizontalSplitPanel horiz = new HorizontalSplitPanel();
        horiz.setStyleName(Runo.SPLITPANEL_SMALL);
        horiz.setSplitPosition(80); // percent
        horiz.addComponent(new ItemContent(repositories, resourceProxy, router, controller));
        VerticalLayout sidebar = buildSidebar();
        horiz.addComponent(sidebar);

        return horiz;
    }

    private VerticalLayout buildSidebar() {
        VerticalLayout sidebar = new VerticalLayout();
        sidebar.setStyleName(Reindeer.LAYOUT_BLUE);
        Panel properties = new Panel("Properties");
        final Label descMetadata1 = new Label("ID: " + resourceProxy.getId());

        status = resourceProxy.getType().getLabel() + " is ";
        lockStatus = status;
        lblStatus = new Label(status + resourceProxy.getStatus(), Label.CONTENT_RAW);
        lblStatus.setDescription(ViewConstants.DESC_STATUS);

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

        properties.addComponent(descMetadata1);
        if (controller.canUpdateItem()) {
            status = "Latest status is ";
            lblCurrentVersionStatus = new Label(status + resourceProxy.getVersionStatus());
            lblCurrentVersionStatus.setDescription(ViewConstants.DESC_STATUS);
            lblCurrentVersionStatus.setStyleName("inset");
            properties.addComponent(lblCurrentVersionStatus);

        }
        else {
            properties.addComponent(lblStatus);
        }

        properties.addComponent(lblLockstatus);
        properties.addComponent(descMetadata2);

        sidebar.addComponent(properties);
        return sidebar;
    }
}
