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
/**
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
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.TreeDataSourceImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.SimpleSearch;
import org.escidoc.browser.ui.mainpage.Footer;
import org.escidoc.browser.ui.mainpage.HeaderContainer;
import org.escidoc.browser.ui.navigation.NavigationTreeBuilder;
import org.escidoc.browser.ui.navigation.NavigationTreeView;
import org.escidoc.browser.ui.navigation.RootNode;
import org.escidoc.browser.ui.tools.ToolsTreeView;
import org.escidoc.browser.ui.view.helpers.CloseTabsViewHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class SimpleLayout extends VerticalLayout implements LayoutDesign {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleLayout.class);

    private final TabSheet mainContentTabs = new TabSheet();

    private final CssLayout mainLayout = new CssLayout();

    private final Panel mainNavigation = new Panel();

    private EscidocServiceLocation serviceLocation;

    private BrowserApplication app;

    private Window mainWindow;

    private CurrentUser currentUser;

    private Repositories repositories;

    private Router router;

    private NavigationTreeView mainNavigationTree;

    private TreeDataSource treeDataSource;

    @Override
    public void init(
        final Window mainWindow, final EscidocServiceLocation serviceLocation, final BrowserApplication app,
        final CurrentUser currentUser, final Repositories repositories, final Router router)
        throws EscidocClientException {
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(app, "app is null: %s", app);
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(router, "router is null: %s", router);

        this.serviceLocation = serviceLocation;
        this.app = app;
        this.mainWindow = mainWindow;
        this.serviceLocation = serviceLocation;
        this.currentUser = currentUser;
        this.repositories = repositories;
        this.router = router;
        buildViews();
    }

    private void buildViews() throws EscidocClientException {
        initTreeDataSource();
        configureLayout();
        addHeader();
        addNavigationPanel();
        addMainContentTabs();
        // FIXME: bad design, create a static class instead.
        new CloseTabsViewHelper(mainLayout, mainContentTabs);
        addFooter();
        addComponent(mainLayout);
    }

    private void configureLayout() {
        setMargin(false, true, false, true);
        mainLayout.setStyleName("maincontainer");
        mainLayout.setSizeFull();
    }

    private void addHeader() {
        final HeaderContainer header =
            new HeaderContainer(router, this, app, serviceLocation, currentUser, repositories);
        header.init();

        mainLayout.addComponent(header);
    }

    private void addFooter() {
        mainLayout.addComponent(new Footer(serviceLocation));
    }

    private void addMainContentTabs() {
        mainLayout.addComponent(buildTabContainer());
    }

    private void addNavigationPanel() throws EscidocClientException {
        try {
            mainLayout.addComponent(buildNavigationPanel());
        }
        catch (UnsupportedOperationException e) {
            LOG.error(e.getMessage());
            mainWindow.showNotification(ViewConstants.ERROR, e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
        catch (URISyntaxException e) {
            LOG.error(e.getMessage());
            mainWindow.showNotification(ViewConstants.ERROR, e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    /**
     * This is the main container It is a Tab Sheet with TABS within it
     * 
     * @return TabSheet
     */
    private TabSheet buildTabContainer() {
        mainContentTabs.setStyleName("floatright paddingtop10 tab");
        mainContentTabs.setWidth("70%");
        mainContentTabs.setHeight("88%");
        return mainContentTabs;
    }

    /**
     * MainNavigation Panel This is the left-most (human side) panel on the page It contains a Main Navigation Tree
     * 
     * @return Panel
     * @throws EscidocClientException
     * @throws URISyntaxException
     * @throws UnsupportedOperationException
     */
    private Panel buildNavigationPanel() throws EscidocClientException, UnsupportedOperationException,
        URISyntaxException {
        mainNavigation.setScrollable(true);
        mainNavigation.setStyleName("floatleft paddingtop10");
        mainNavigation.setWidth("30%");
        mainNavigation.setHeight("88%");

        addRootNode();
        addAccordion();
        return mainNavigation;
    }

    private void addAccordion() throws UnsupportedOperationException, EscidocClientException, URISyntaxException {
        final Accordion accordion = new Accordion();

        mainNavigationTree =
            new NavigationTreeBuilder(serviceLocation, currentUser, repositories).buildNavigationTree(router,
                mainWindow, treeDataSource);

        accordion.addTab(mainNavigationTree, ViewConstants.RESOURCES, null);

        ToolsTreeView toolsTreeView = new ToolsTreeView(router, repositories);
        toolsTreeView.init();

        accordion.addTab(toolsTreeView, ViewConstants.TOOLS, null);

        mainNavigation.addComponent(accordion);
        ((Layout) mainNavigation.getContent()).setMargin(false);
    }

    private void initTreeDataSource() throws EscidocClientException {
        treeDataSource = new TreeDataSourceImpl(repositories.context().findAllWithChildrenInfo());
        treeDataSource.init();
    }

    private void addRootNode() {
        mainNavigation.addComponent(new RootNode(serviceLocation));
    }

    public boolean isUserAllowedToCreateContext() {
        try {
            return repositories
                .pdp().isAction(ActionIdConstants.CREATE_CONTEXT).forResource("").forUser(currentUser.getUserId())
                .permitted();
        }
        catch (final EscidocClientException e) {
            showError(e.getMessage());
        }
        catch (final URISyntaxException e) {
            showError(e.getMessage());
        }
        return false;
    }

    /**
     * This method handles the open of a new tab on the right section of the mainWindow This is the perfect place to
     * inject Views that represent objects
     * 
     * @param cmp
     * @param tabname
     */
    @Override
    public void openView(final Component cmp, String tabname) {
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

    /**
     * Getter mainContent
     * 
     * @return TabSheet
     */
    public TabSheet getMaincontent() {
        return mainContentTabs;
    }

    /**
     * Handle the event from the srchButton Link at the buildNavigationPanel
     * 
     * @param event
     */
    public void onClickSrchButton(final Button.ClickEvent event) {
        final SimpleSearch simpleSearch = new SimpleSearch(router, this, serviceLocation, repositories, currentUser);
        openView(simpleSearch, ViewConstants.SEARCH_RESULTS);
    }

    public int getApplicationHeight() {
        return app.getApplicationHeight();
    }

    public CurrentUser getCurrentUser() {
        return currentUser;
    }

    private void showError(final String msg) {
        getWindow().showNotification(msg, Notification.TYPE_ERROR_MESSAGE);
    }
}