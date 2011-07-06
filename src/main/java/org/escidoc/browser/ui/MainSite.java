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
package org.escidoc.browser.ui;

import java.net.URISyntaxException;
import java.util.Map;

import org.escidoc.browser.ActionIdConstants;
import org.escidoc.browser.AppConstants;
import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.ResourceModelFactory;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.model.internal.TreeDataSourceImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.helper.Util;
import org.escidoc.browser.ui.maincontent.ContainerView;
import org.escidoc.browser.ui.maincontent.ContextView;
import org.escidoc.browser.ui.maincontent.ItemView;
import org.escidoc.browser.ui.maincontent.SearchAdvancedView;
import org.escidoc.browser.ui.maincontent.SimpleSearch;
import org.escidoc.browser.ui.mainpage.Footer;
import org.escidoc.browser.ui.mainpage.HeaderContainer;
import org.escidoc.browser.ui.navigation.NavigationTreeBuilder;
import org.escidoc.browser.ui.navigation.NavigationTreeView;
import org.escidoc.browser.ui.navigation.RootNode;
import org.escidoc.browser.ui.navigation.menubar.NavigationMenuBar;

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
public class MainSite extends VerticalLayout {

    private final TabSheet mainContentTabs = new TabSheet();

    private final CssLayout mainLayout = new CssLayout();

    private final Panel mainNavigation = new Panel();

    private final BrowserApplication app;

    private final Window mainWindow;

    private final CurrentUser currentUser;

    private final Repositories repositories;

    private EscidocServiceLocation serviceLocation;

    private NavigationTreeView mainNavigationTree;

    private NavigationMenuBar navigationMenuBar;

    private TreeDataSource treeDataSource;

    /**
     * The mainWindow should be revised whether we need it or not the appHeight is the Height of the Application and I
     * need it for calculations in the inner elements
     * 
     * @param mainWindow
     * @param repositories
     * @throws EscidocClientException
     */
    public MainSite(final Window mainWindow, final EscidocServiceLocation serviceLocation,
        final BrowserApplication app, final CurrentUser currentUser, final Repositories repositories)
        throws EscidocClientException {

        this.serviceLocation = serviceLocation;
        this.app = app;
        this.mainWindow = mainWindow;
        this.serviceLocation = serviceLocation;
        this.currentUser = currentUser;
        this.repositories = repositories;

        init();
    }

    private void init() throws EscidocClientException {
        initTreeDataSource();
        configureLayout();
        addHeader();
        addMenuBar();
        addNavigationPanel();
        addMainContentTabs();
        permanentURLelement();
        addFooter();
        addComponent(mainLayout);
    }

    private void addMenuBar() {
        navigationMenuBar = new NavigationMenuBar(currentUser, repositories, mainWindow, treeDataSource);
        mainNavigation.addComponent(navigationMenuBar);
    }

    private void configureLayout() {
        setMargin(true);
        mainLayout.setStyleName("maincontainer");
        mainLayout.setSizeFull();
    }

    private void addHeader() {
        final HeaderContainer header = new HeaderContainer(this, app, serviceLocation, currentUser, repositories);
        header.init();

        mainLayout.addComponent(header);
    }

    private void addFooter() {
        mainLayout.addComponent(new Footer());
    }

    private void addMainContentTabs() {
        mainLayout.addComponent(buildTabContainer());
    }

    private void addNavigationPanel() throws EscidocClientException {
        mainLayout.addComponent(buildNavigationPanel());
    }

    /**
     * This is the Permanent Link entry point Check if there is a tab variable set in the GET Method of the page and
     * open a tab containing the object with that ID URL Example
     * http://localhost:8084/browser/mainWindow?tab=escidoc:16037
     * &type=Item&escidocurl=http://escidev4.fiz-karlsruhe.de:8080
     */
    private void permanentURLelement() {
        final Map<String, String[]> parameters = app.getParameters();
        if (Util.hasTabArg(parameters) && Util.hasObjectType(parameters)) {
            final String escidocID = parameters.get(AppConstants.ARG_TAB)[0];

            final ResourceModelFactory resourceFactory = new ResourceModelFactory(repositories);

            if (parameters.get(AppConstants.ARG_TYPE)[0].equals("CONTEXT")) {
                try {
                    final ContextProxyImpl context =
                        (ContextProxyImpl) resourceFactory.find(escidocID, ResourceType.CONTEXT);
                    openTab(new ContextView(serviceLocation, this, context, mainWindow, currentUser, repositories),
                        context.getName());
                }
                catch (final EscidocClientException e) {
                    showError("Cannot retrieve Context");
                }
            }
            else if (parameters.get(AppConstants.ARG_TYPE)[0].equals("CONTAINER")) {
                try {
                    final ContainerProxy container =
                        (ContainerProxy) resourceFactory.find(escidocID, ResourceType.CONTAINER);
                    openTab(new ContainerView(serviceLocation, this, container, mainWindow, currentUser, repositories),
                        container.getName());
                }
                catch (final EscidocClientException e) {
                    showError("Cannot retrieve Container");
                }
            }
            else if (parameters.get(AppConstants.ARG_TYPE)[0].equals("ITEM")) {
                try {
                    final ItemProxy item = (ItemProxy) resourceFactory.find(escidocID, ResourceType.ITEM);
                    openTab(new ItemView(serviceLocation, repositories, this, item, mainWindow, currentUser),
                        item.getName());
                }
                catch (final EscidocClientException e) {
                    showError("Cannot retrieve Item");
                }
            }
            else if (parameters.get(AppConstants.ARG_TYPE)[0].equals("sa")) {
                final SearchAdvancedView srch = new SearchAdvancedView(this, serviceLocation);
                openTab(srch, "Advanced Search");
            }
            else {
                throw new UnsupportedOperationException("Not yet implemented");
            }
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
     */
    private Panel buildNavigationPanel() throws EscidocClientException {
        mainNavigation.setScrollable(true);
        mainNavigation.setStyleName("floatleft paddingtop10");
        mainNavigation.setWidth("30%");
        mainNavigation.setHeight("88%");

        addRootNode();
        addNavigationTree();
        mainNavigationTree.withNavigationMenuBar(navigationMenuBar);
        return mainNavigation;
    }

    private void addNavigationTree() throws EscidocClientException {
        mainNavigationTree =
            new NavigationTreeBuilder(serviceLocation, currentUser, repositories).buildNavigationTree(this, mainWindow,
                treeDataSource);
        mainNavigation.addComponent(mainNavigationTree);
        ((Layout) mainNavigation.getContent()).setMargin(false);
    }

    private void initTreeDataSource() throws EscidocClientException {
        treeDataSource = new TreeDataSourceImpl(repositories.context().findAllWithChildrenInfo());
        treeDataSource.init();
    }

    private void addRootNode() {
        mainNavigation.addComponent(new RootNode(serviceLocation, this));
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
    public void openTab(final Component cmp, String tabname) {
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
        final SimpleSearch smpSearch = new SimpleSearch(this, serviceLocation, repositories, currentUser);
        openTab(smpSearch, "Search Results");
    }

    public int getApplicationHeight() {
        return app.getApplicationHeight();
    }

    public CurrentUser getCurrentUser() {
        return currentUser;
    }

    private void showError(final String msg) {
        getWindow().showNotification(msg, Notification.TYPE_HUMANIZED_MESSAGE);
    }
}
