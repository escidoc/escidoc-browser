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
import org.escidoc.browser.model.ItemModel;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.ItemProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.view.helpers.ItemPropertiesVH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.om.item.Item;

@SuppressWarnings("serial")
public final class ItemView extends View {

    private static final Logger LOG = LoggerFactory.getLogger(ItemView.class);

    private final Router router;

    private final ItemProxyImpl resourceProxy;

    private final Window mainWindow;

    private final EscidocServiceLocation serviceLocation;

    private final Repositories repositories;

    private ItemPropertiesVH itemPropertiesView;

    private Panel panelView;

    public ItemView(final Repositories repositories, final Router router, final ResourceProxy resourceProxy)
        throws EscidocClientException {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(router, "mainSite is null.");
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null.");

        this.resourceProxy = (ItemProxyImpl) resourceProxy;
        this.repositories = repositories;
        this.setViewName(resourceProxy.getName());
        this.mainWindow = router.getMainWindow();
        this.router = router;
        this.serviceLocation = router.getServiceLocation();
        panelView = buildContentPanel();
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
        Accordion metaDataRecsAcc = new MetadataRecsItem(resourceProxy, repositories, router, this).asAccord();
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
        vlDirectMember = new ItemContent(repositories, resourceProxy, serviceLocation, mainWindow);
        directMembersPanel.setContent(vlDirectMember);

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

        // creating the properties / without the breadcrump
        itemPropertiesView = new ItemPropertiesVH(resourceProxy, repositories, mainWindow, serviceLocation);
        vlResourceProperties.addComponent(itemPropertiesView.getContentLayout());
        return vlResourceProperties;
    }

    public ItemPropertiesVH getItemPropertiesVH() {
        return itemPropertiesView;
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
        final ItemView other = (ItemView) obj;
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

    public void reloadView() {
        try {
            router.show(resourceProxy, true);
            // refresh tree
            TreeDataSource treeDS = router.getLayout().getTreeDataSource();
            ItemModel im = new ItemModel(resourceProxy.getResource());
            ResourceModel parentModel = treeDS.getParent(im);

            boolean isSuccesful = treeDS.remove(im);
            if (isSuccesful) {
                // Need to reload again the item
                Item itemP = repositories.item().findItemById(resourceProxy.getId());
                treeDS.addChild(parentModel, new ItemModel(itemP));

                // Reload Parent
                // reloadParent(parentModel);
            }

        }
        catch (EscidocClientException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    // private void reloadParent(ResourceModel parentModel) throws EscidocClientException {
    // TabSheet ts = (TabSheet) router.getLayout().getViewContainer();
    // for (int i = ts.getComponentCount() - 1; i >= 0; i--) {
    // String tabDescription =
    // ts.getTab(i).getDescription().substring(ts.getTab(i).getDescription().lastIndexOf('#') + 1).toString();
    // LOG.debug("############################ " + tabDescription);
    // // Remove the tab from the TabSheet
    // if (tabDescription.equals(parentModel.getId().toString())) {
    // router.show(parentModel, true);
    // }
    // }
    // }
}
