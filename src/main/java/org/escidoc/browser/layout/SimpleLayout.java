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

import com.google.common.base.Preconditions;

import com.vaadin.terminal.Resource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.TreeDataSourceImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.mainpage.Footer;
import org.escidoc.browser.ui.mainpage.HeaderContainer;
import org.escidoc.browser.ui.navigation.NavigationTreeBuilder;
import org.escidoc.browser.ui.navigation.NavigationTreeView;
import org.escidoc.browser.ui.navigation.RootNode;
import org.escidoc.browser.ui.orgunit.OrgUnitTreeView;
import org.escidoc.browser.ui.tools.ToolsTreeView;
import org.escidoc.browser.ui.view.helpers.CloseTabsViewHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class SimpleLayout extends LayoutDesign {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleLayout.class);

    private Resource NO_ICON;

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

    private NavigationTreeBuilder treeBuilder;

    @Override
    public void init(
        final Window mainWindow, final EscidocServiceLocation serviceLocation, final BrowserApplication app,
        final Repositories repositories, final Router router) throws EscidocClientException,
        UnsupportedOperationException, URISyntaxException {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(app, "app is null: %s", app);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(router, "router is null: %s", router);
        this.serviceLocation = serviceLocation;
        this.app = app;
        this.mainWindow = mainWindow;
        this.serviceLocation = serviceLocation;
        this.repositories = repositories;
        this.router = router;

        treeBuilder = new NavigationTreeBuilder(mainWindow, router, repositories);
        buildMainLayout();
        addComponent(mainLayout);
    }

    @Override
    public void openView(final Component cmp, String tabname) {
        final String description = tabname;
        final int p = tabname.lastIndexOf('#');
        // FIXME parameter should not be reassigned.
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
    public void openViewByReloading(final Component component, final String tabname) {
        Preconditions.checkNotNull(component, "component is null: %s", component);
        Preconditions.checkNotNull(tabname, "tabname is null: %s", tabname);

        final String description = tabname;

        // use as tabname the name without the ID
        final int p = tabname.lastIndexOf('#');

        // FIXME parameter should not be reassigned.
        String newTabName = tabname;
        if (p > 0) {
            newTabName = tabname.substring(0, p);
        }
        if (tabname.length() > 50) {
            newTabName = tabname.substring(0, 50) + "...";
        }

        int position = -1;
        if (mainContentTabs.getTab(component) != null) {
            final Tab tmpTab = mainContentTabs.getTab(component);
            position = mainContentTabs.getTabPosition(tmpTab);
            mainContentTabs.removeTab(tmpTab);
        }
        final Tab tab = mainContentTabs.addTab(component);
        tab.setCaption(newTabName);
        tab.setDescription(description);
        if (position != -1) {
            mainContentTabs.setTabPosition(tab, position);
        }

        mainContentTabs.setSelectedTab(component);
        tab.setClosable(true);
    }

    /**
     * There are cases where we need to close views programmatically. <br />
     * Example close resource-views which are deleted.
     * 
     * @param cmp
     */
    @Override
    public void closeView(final ResourceModel model, final ResourceModel parent, final Object sender) {
        // FIXME replace comment with methods.
        // 1. Remove the tab for the resource to be deleted
        // 2. Reload the parent Tab
        // 3. Remove the element from the tree
        for (int i = mainContentTabs.getComponentCount() - 1; i >= 0; i--) {
            final String tabDescription =
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
                    catch (final EscidocClientException e) {
                        mainWindow.showNotification(ViewConstants.VIEW_ERROR_CANNOT_LOAD_VIEW,
                            Window.Notification.TYPE_ERROR_MESSAGE);

                    }
                }
            }
            else {
                final Tree dmTree = (Tree) sender;
                dmTree.removeItem(model);
            }
        }
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

        container.setSplitPosition(20, Sizeable.UNITS_PERCENTAGE);
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
        mainNavigationTree = addNavigationTree();

        final Accordion accordion = buildAccordion();

        vlNavigationPanel.addComponent(accordion);
        vlNavigationPanel.setExpandRatio(accordion, 1.0f);

        navigationPanel.setContent(vlNavigationPanel);

        return navigationPanel;
    }

    private Accordion buildAccordion() throws EscidocClientException, URISyntaxException {
        final Accordion accordion = new Accordion();
        accordion.setSizeFull();
        accordion.addListener(new SelectedTabChangeListener() {

            @Override
            public void selectedTabChange(final SelectedTabChangeEvent event) {
                Preconditions.checkNotNull(event, "event is null: %s", event);

                final Object source = event.getSource();

                Preconditions.checkNotNull(source, "source is null: %s", source);
                if (!(source instanceof Accordion)) {
                    return;
                }

                if (isOrgUniTabSelected(source)) {
                    reloadOrgUnitTree(source);
                }
            }

            private void reloadOrgUnitTree(final Object source) {
                try {
                    reloadContent(source);
                }
                catch (final EscidocClientException e) {
                    LOG.error("Can not reload data source: " + e.getMessage(), e);
                }
            }

            private boolean isOrgUniTabSelected(final Object source) {
                return getSelectedTabCaption(source).equalsIgnoreCase(ViewConstants.ORG_UNITS)
                    && getTabContent(source) instanceof OrgUnitTreeView;
            }

            private void reloadContent(final Object source) throws EscidocClientException {
                ((OrgUnitTreeView) getTabContent(source)).reload();
            }

            private Component getTabContent(final Object source) {
                return ((Accordion) source).getSelectedTab();
            }

            private String getSelectedTabCaption(final Object source) {
                return ((Accordion) source).getTab(getTabContent(source)).getCaption();
            }
        });

        addResourcesTab(accordion);
        addOrgUnitTab(accordion);
        addToolsTab(accordion);
        return accordion;
    }

    private void addOrgUnitTab(final Accordion accordion) throws EscidocClientException {
        final OrgUnitTreeView tree = treeBuilder.buildOrgUnitTree();
        accordion.addTab(tree, ViewConstants.ORG_UNITS, NO_ICON);
    }

    private void addResourcesTab(final Accordion accordion) {
        accordion.addTab(mainNavigationTree, ViewConstants.RESOURCES, NO_ICON);
    }

    private void addToolsTab(final Accordion accordion) throws EscidocClientException, URISyntaxException {
        final ToolsTreeView toolsTreeView = new ToolsTreeView(router, repositories);
        toolsTreeView.init();
        accordion.addTab(toolsTreeView, ViewConstants.TOOLS, NO_ICON);
    }

    private NavigationTreeView addNavigationTree() throws EscidocClientException {
        treeDataSource = new TreeDataSourceImpl(repositories.context().findAllWithChildrenInfo());
        treeDataSource.init();
        setTreeDataSource(treeDataSource);
        mainNavigationTree = treeBuilder.buildNavigationTree(treeDataSource);
        return mainNavigationTree;
    }

    @SuppressWarnings("unused")
    private TabSheet buildTabContainer() {
        mainContentTabs = new TabSheet();
        mainContentTabs.setImmediate(true);
        mainContentTabs.setWidth("100.0%");
        mainContentTabs.setHeight("100.0%");
        new CloseTabsViewHelper(cssContent, mainContentTabs);
        return mainContentTabs;
    }

    @Override
    public TreeDataSource getTreeDataSource() {
        return treeDataSource;
    }

    void setTreeDataSource(final TreeDataSource treeDataSource) {
        this.treeDataSource = treeDataSource;
    }

    @Override
    public Component getViewContainer() {
        return mainContentTabs;
    }
}