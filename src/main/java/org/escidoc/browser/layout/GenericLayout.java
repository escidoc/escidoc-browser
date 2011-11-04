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

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.TreeDataSourceImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.maincontent.GenericView;
import org.escidoc.browser.ui.mainpage.HeaderContainer;
import org.escidoc.browser.ui.navigation.NavigationTreeBuilder;
import org.escidoc.browser.ui.navigation.NavigationTreeView;
import org.escidoc.browser.ui.navigation.RootNode;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class GenericLayout extends LayoutDesign {

    private AbsoluteLayout mainLayout;

    private HorizontalLayout container;

    private TabSheet mainContentTabs;

    private Panel contentPanel;

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

    @Override
    public void init(
        Window mainWindow, EscidocServiceLocation serviceLocation, BrowserApplication app, Repositories repositories,
        Router router) throws EscidocClientException {
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
        final Tab tb = mainContentTabs.addTab(cmp);
        if (tabname.length() > 50) {
            tb.setDescription(tabname);
            tabname = tabname.substring(0, 50) + "...";
        }
        tb.setCaption(tabname);
        tb.setDescription(tabname);
        mainContentTabs.setSelectedTab(cmp);
        tb.setClosable(true);
    }

    @Override
    public int getApplicationHeight() {
        // Not really needed for this Layout
        return 0;
    }

    private AbsoluteLayout buildMainLayout() throws EscidocClientException {
        // common part: create layout
        mainLayout = new AbsoluteLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.setMargin(false, true, false, true);

        // top-level component properties
        setWidth("100.0%");
        setHeight("100.0%");

        // headerContainer
        buildHeader();
        mainLayout.addComponent(headerContainer, "top:0.0px;right:0.0px;left:0.0px;");

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

    private HorizontalLayout buildContainer() throws EscidocClientException {
        // common part: create layout
        container = new HorizontalLayout();
        container.setImmediate(false);
        container.setWidth("100.0%");
        container.setHeight("100.0%");
        container.setMargin(false);

        // navigationPanel
        navigationPanel = buildNavigationPanel();
        container.addComponent(navigationPanel);
        container.setExpandRatio(navigationPanel, 3.0f);

        // TabContainer
        mainContentTabs = buildTabContainer();
        container.addComponent(mainContentTabs);
        container.setExpandRatio(mainContentTabs, 7.0f);

        return container;
    }

    private Panel buildNavigationPanel() throws EscidocClientException {
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
        vlNavigationPanel.addComponent(mainNavigationTree);
        vlNavigationPanel.setExpandRatio(mainNavigationTree, 1.0f);

        navigationPanel.setContent(vlNavigationPanel);

        return navigationPanel;
    }

    private NavigationTreeView addNavigationTree() throws EscidocClientException {

        treeDataSource = new TreeDataSourceImpl(repositories.context().findAllWithChildrenInfo());
        treeDataSource.init();

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

        // contentPanel
        contentPanel = buildContentPanel();
        mainContentTabs.addTab(contentPanel, "Tab", null);

        return mainContentTabs;
    }

    private Panel buildContentPanel() {
        return new GenericView(serviceLocation, repositories, router, this, null, mainWindow).buildContentPanel();
    }

}
