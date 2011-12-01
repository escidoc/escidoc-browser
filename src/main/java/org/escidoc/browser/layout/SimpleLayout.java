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
package org.escidoc.browser.layout;

import java.net.URISyntaxException;

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.TreeDataSourceImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.View;
import org.escidoc.browser.ui.mainpage.Footer;
import org.escidoc.browser.ui.mainpage.HeaderContainer;
import org.escidoc.browser.ui.navigation.NavigationTreeBuilder;
import org.escidoc.browser.ui.navigation.NavigationTreeView;
import org.escidoc.browser.ui.navigation.RootNode;
import org.escidoc.browser.ui.tools.ToolsTreeView;
import org.escidoc.browser.ui.view.helpers.CloseTabsViewHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class SimpleLayout extends LayoutDesign {

    private AbsoluteLayout mainLayout;

    private HorizontalSplitPanel container;

    private TabSheet mainContentTabs;

    private Panel navigationPanel;

    private VerticalLayout vlNavigationPanel;

    private HorizontalLayout headerContainer;

    private EscidocServiceLocation serviceLocation;

    private BrowserApplication app;

    private Window mainWindow;

    private Repositories repositories;

    private Router router;

    private NavigationTreeView mainNavigationTree;

    private TreeDataSource treeDataSource;

    private CssLayout cssContent;

    private HorizontalLayout footer;

    private static final Logger LOG = LoggerFactory.getLogger(SimpleLayout.class);

    @Override
    public void init(
        Window mainWindow, EscidocServiceLocation serviceLocation, BrowserApplication app, Repositories repositories,
        Router router) throws EscidocClientException, UnsupportedOperationException, URISyntaxException {
        this.serviceLocation = serviceLocation;
        this.app = app;
        this.mainWindow = mainWindow;
        this.serviceLocation = serviceLocation;
        this.repositories = repositories;
        this.router = router;
        buildMainLayout();
        addComponent(mainLayout);
    }

    @Override
    public void openView(Component cmp, String tabname) {
        String description = tabname;
        int p = tabname.lastIndexOf('#');
        if (p > 0) {
            tabname = tabname.substring(0, p);
        }

        if (tabname.length() > 50) {
            tabname = tabname.substring(0, 50) + "...";
        }

        final Tab tb = mainContentTabs.addTab(cmp);
        tb.setDescription(description);
        tb.setCaption(tabname);
        mainContentTabs.setSelectedTab(cmp);
        tb.setClosable(true);
    }

    @Override
    public void openViewByReloading(Component cmp, String tabname) {
        String description = tabname;

        // use as tabname the name without the ID
        int p = tabname.lastIndexOf('#');
        if (p > 0) {
            tabname = tabname.substring(0, p);
        }
        if (tabname.length() > 50) {
            tabname = tabname.substring(0, 50) + "...";
        }
        int position = -1;
        if (mainContentTabs.getTab(cmp) != null) {
            Tab tmpTab = mainContentTabs.getTab(cmp);
            position = mainContentTabs.getTabPosition(tmpTab);
            mainContentTabs.removeTab(tmpTab);
        }
        final Tab tb = mainContentTabs.addTab(cmp);
        tb.setCaption(tabname);
        tb.setDescription(description);
        if (position != -1) {
            mainContentTabs.setTabPosition(tb, position);
        }

        mainContentTabs.setSelectedTab(cmp);
        tb.setClosable(true);
    }

    /**
     * There are cases where we need to close views programatically. <br />
     * Example close resource-views which are deleted.
     * 
     * @param cmp
     */
    public void closeView(ResourceModel model, ResourceModel parent) {
        // 1. Remove the tab for the resource to be deleted
        // 2. Reload the parent Tab
        // 3. Remove the element from the tree
        for (int i = mainContentTabs.getComponentCount() - 1; i >= 0; i--) {
            String tabDescription =
                mainContentTabs
                    .getTab(i).getDescription()
                    .substring(mainContentTabs.getTab(i).getDescription().lastIndexOf('#') + 1).toString();
            LOG.debug("############################ " + tabDescription);
            // Remove the tab from the TabSheet
            if (tabDescription.equals(model.getId().toString())) {
                mainContentTabs.removeTab(mainContentTabs.getTab(i));
            }

            // Deleting from the tree, I know the parent and I reload it
            if (parent != null) {
                LOG.debug("############################ Parent is not null" + parent.getId());
                if (tabDescription.equals(parent.getId().toString())) {
                    try {
                        router.show(parent, true);
                    }
                    catch (EscidocClientException e) {
                        mainWindow.showNotification(ViewConstants.VIEW_ERROR_CANNOT_LOAD_VIEW,
                            Window.Notification.TYPE_ERROR_MESSAGE);

                    }
                    // View tabView = (View) mainContentTabs.getTab(i).getComponent();
                    // openViewByReloading(tabView, tabView.getViewName());
                }
            }
            else {
                // this is the case when Deleting from DirectMember
                LOG.debug("############################ Parent is null are you deleting from the DM?");
                View tab = (View) mainContentTabs.getSelectedTab();
                openViewByReloading(mainContentTabs.getSelectedTab(), tab.getViewName());
            }
        }
        // remove it from the tree
        treeDataSource.remove(model);
    }

    @Override
    @Deprecated
    public int getApplicationHeight() {
        // Not really needed for this Layout
        return 0;
    }

    private AbsoluteLayout buildMainLayout() throws EscidocClientException, UnsupportedOperationException,
        URISyntaxException {
        // common part: create layout
        mainLayout = new AbsoluteLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.setMargin(false, true, false, true);

        // top-level component properties
        setWidth("100.0%");
        setHeight("100.0%");

        buildHeader();
        mainLayout.addComponent(headerContainer, "top:0.0px;right:0.0px;left:0.0px;");
        buildFooter();
        mainLayout.addComponent(footer, "right:0.0px;bottom:0.0px;left:0.0px;");

        // container
        container = buildContainer();
        mainLayout.addComponent(container, "top:75.0px;right:0.0px;bottom:20.0px;left:0.0px;");

        return mainLayout;
    }

    private void buildHeader() {
        headerContainer = new HorizontalLayout();
        headerContainer.setImmediate(false);
        headerContainer.setWidth("100.0%");
        headerContainer.setHeight("70px");
        headerContainer.setMargin(false);
        final HeaderContainer header = new HeaderContainer(router, this, app, serviceLocation, repositories);
        header.init();
        headerContainer.addComponent(header);
    }

    private void buildFooter() {
        footer = new HorizontalLayout();
        footer.setImmediate(false);
        footer.setWidth("100.0%");
        footer.setHeight("20px");
        footer.setMargin(false);
        final Footer footerHtml = new Footer(serviceLocation);
        footer.addComponent(footerHtml);

    }

    private HorizontalSplitPanel buildContainer() throws EscidocClientException, UnsupportedOperationException,
        URISyntaxException {
        // common part: create layout
        container = new HorizontalSplitPanel();
        container.setStyleName(Runo.SPLITPANEL_SMALL);
        container.setImmediate(false);

        container.setMargin(false);

        container.setSplitPosition(30, Sizeable.UNITS_PERCENTAGE);
        container.setSizeFull();
        container.setLocked(false);

        // navigationPanel
        navigationPanel = buildNavigationPanel();
        container.addComponent(navigationPanel);

        // TabContainer
        cssContent = new CssLayout();
        cssContent.setSizeFull();
        cssContent.setMargin(false);
        mainContentTabs = buildTabContainer();
        cssContent.addComponent(mainContentTabs);
        container.addComponent(cssContent);

        return container;
    }

    private Panel buildNavigationPanel() throws EscidocClientException, UnsupportedOperationException,
        URISyntaxException {
        // common part: create layout
        navigationPanel = new Panel();
        navigationPanel.setImmediate(false);
        navigationPanel.setWidth("100.0%");
        navigationPanel.setHeight("100.0%");

        // vlNavigationPanel
        vlNavigationPanel = new VerticalLayout();
        vlNavigationPanel.setImmediate(false);
        vlNavigationPanel.setWidth("100.0%");
        vlNavigationPanel.setHeight("100.0%");
        vlNavigationPanel.setMargin(false);

        // Adding a root element in the Navigation Tree
        vlNavigationPanel.addComponent(new RootNode(serviceLocation));

        // Binding the tree to the NavigationPanel
        mainNavigationTree = this.addNavigationTree();

        final Accordion accordion = new Accordion();
        accordion.setSizeFull();
        accordion.addTab(mainNavigationTree, ViewConstants.RESOURCES, null);

        ToolsTreeView toolsTreeView = new ToolsTreeView(router, repositories);
        toolsTreeView.init();
        accordion.addTab(toolsTreeView, ViewConstants.TOOLS, null);

        vlNavigationPanel.addComponent(accordion);
        vlNavigationPanel.setExpandRatio(accordion, 1.0f);

        navigationPanel.setContent(vlNavigationPanel);

        return navigationPanel;
    }

    private NavigationTreeView addNavigationTree() throws EscidocClientException {
        treeDataSource = new TreeDataSourceImpl(repositories.context().findAllWithChildrenInfo());
        treeDataSource.init();
        setTreeDataSource(treeDataSource);
        mainNavigationTree =
            new NavigationTreeBuilder(serviceLocation, repositories).buildNavigationTree(router, mainWindow,
                treeDataSource);
        return mainNavigationTree;
    }

    private TabSheet buildTabContainer() {
        // common part: create layout
        mainContentTabs = new TabSheet();
        mainContentTabs.setImmediate(true);
        mainContentTabs.setWidth("100.0%");
        mainContentTabs.setHeight("100.0%");
        new CloseTabsViewHelper(cssContent, mainContentTabs); // headerContainer
        return mainContentTabs;
    }

    public TreeDataSource getTreeDataSource() {
        return treeDataSource;
    }

    void setTreeDataSource(TreeDataSource treeDataSource) {
        this.treeDataSource = treeDataSource;
    }
}
