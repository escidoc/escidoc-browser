package org.escidoc.browser.layout;

import java.net.URISyntaxException;

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.TreeDataSourceImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.navigation.BaseNavigationTreeView;
import org.escidoc.browser.ui.navigation.BaseTreeDataSource;
import org.escidoc.browser.ui.navigation.NavigationTreeBuilder;
import org.escidoc.browser.ui.navigation.NavigationTreeView;
import org.escidoc.browser.ui.tools.ToolsTreeView;

import com.vaadin.data.Container.Filterable;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class NavigationSimpleLayout {
    private Panel navigationPanel;

    private VerticalLayout vlNavigationPanel;

    private BrowserApplication app;

    private Repositories repositories;

    private NavigationTreeBuilder treeBuilder;

    private TreeDataSource treeDataSource;

    private Resource NO_ICON;

    private NavigationTreeView mainNavigationTree;

    private Router router;

    public NavigationSimpleLayout(SimpleLayout layout, Panel navigationPanel, VerticalLayout vlNavigationPanel) {
        super();
        this.navigationPanel = navigationPanel;
        this.vlNavigationPanel = vlNavigationPanel;
        this.app = layout.getApp();
        this.repositories = layout.getRouter().getRepositories();
        this.treeBuilder = layout.getTreeBuilder();
        this.treeDataSource = layout.getTreeDataSource();
        this.mainNavigationTree = layout.getMainNavigationTree();
        this.router = layout.getRouter();
    }

    public Panel buildNavigationPanel() throws EscidocClientException, UnsupportedOperationException,
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

        // Binding the tree to the NavigationPanel
        NavigationTreeView mainNavigationTree = addNavigationTree();
        addAccordion();
        navigationPanel.setContent(vlNavigationPanel);
        return navigationPanel;
    }

    private void addAccordion() throws EscidocClientException, URISyntaxException {
        final Accordion accordion = buildAccordion();
        vlNavigationPanel.addComponent(accordion);
        vlNavigationPanel.setExpandRatio(accordion, 1.0f);
    }

    private Accordion buildAccordion() throws EscidocClientException, URISyntaxException {
        final Accordion accordion = new Accordion();
        accordion.setSizeFull();
        accordion.addListener(new OnNavigationTabChange());

        addResourcesTab(accordion);
        addOrgUnitTab(accordion);
        addUserAccountsTab(accordion);
        addGroupsTab(accordion);
        addContentModelsTab(accordion);
        addToolsTab(accordion);

        return accordion;
    }

    private void addGroupsTab(Accordion accordion) throws EscidocClientException {
        if (app.getCurrentUser().isGuest()) {
            return;
        }
        BaseTreeDataSource groupDataSource = new BaseTreeDataSource(repositories.group());
        groupDataSource.init();
        accordion.addTab(buildGroupListWithFilter(treeBuilder.buildGroupTree(groupDataSource), groupDataSource),
            ViewConstants.User_Groups, NO_ICON);
    }

    private void addUserAccountsTab(Accordion accordion) {
        if (app.getCurrentUser().isGuest()) {
            return;
        }
        accordion.addTab(buildListWithFilter(treeBuilder.buildUserAccountTree()), ViewConstants.USER_ACCOUNTS, NO_ICON);
    }

    private void addContentModelsTab(Accordion accordion) {
        accordion.addTab(buildListWithFilter(treeBuilder.buildContentModelTree()), ViewConstants.CONTENT_MODELS,
            NO_ICON);
    }

    private void addOrgUnitTab(final Accordion accordion) {
        accordion.addTab(treeBuilder.buildOrgUnitTree(), ViewConstants.ORG_UNITS, NO_ICON);
    }

    private void addResourcesTab(final Accordion accordion) {
        accordion.addTab(mainNavigationTree, ViewConstants.RESOURCES, NO_ICON);
    }

    private void addToolsTab(final Accordion accordion) throws EscidocClientException, URISyntaxException {
        final ToolsTreeView toolsTreeView = new ToolsTreeView(router, repositories);
        toolsTreeView.init();
        Tab tab = accordion.addTab(toolsTreeView, ViewConstants.TOOLS, NO_ICON);
        tab.setClosable(true);
    }

    private NavigationTreeView addNavigationTree() throws EscidocClientException {
        treeDataSource = new TreeDataSourceImpl(repositories.context().findAllWithChildrenInfo());
        treeDataSource.init();
        setTreeDataSource(treeDataSource);
        mainNavigationTree = treeBuilder.buildNavigationTree(treeDataSource);
        return mainNavigationTree;
    }

    void setTreeDataSource(final TreeDataSource treeDataSource) {
        this.treeDataSource = treeDataSource;
    }

    private VerticalLayout buildListWithFilter(final BaseNavigationTreeView list) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true, false, false, true);

        TextField filterField = new TextField();
        filterField.setInputPrompt("Type something to filter the list");
        filterField.setWidth("250px");
        filterField.addListener(new TextChangeListener() {

            private SimpleStringFilter filter;

            @Override
            public void textChange(TextChangeEvent event) {
                // TODO refactor this, the list should not return the data
                // source
                Filterable ds = (Filterable) list.getDataSource();
                ds.removeAllContainerFilters();
                filter = new SimpleStringFilter(PropertyId.NAME, event.getText(), true, false);
                ds.addContainerFilter(filter);
            }
        });

        layout.addComponent(filterField);
        layout.addComponent(list);
        return layout;
    }

    private Component buildGroupListWithFilter(final BaseNavigationTreeView list, final TreeDataSource ds) {
        // TODO this is a temporary create button, it is to redesign.

        VerticalLayout vl = new VerticalLayout();

        CssLayout headerButton = newHeaderButton(ds);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true, false, false, true);
        layout.setSpacing(true);

        TextField filterField = new TextField();
        filterField.setInputPrompt("Type something to filter the list");
        filterField.setWidth("250px");
        filterField.addListener(new TextChangeListener() {

            private SimpleStringFilter filter;

            @Override
            public void textChange(TextChangeEvent event) {
                // TODO refactor this, the list should not return the data
                // source
                Filterable ds = (Filterable) list.getDataSource();
                ds.removeAllContainerFilters();
                filter = new SimpleStringFilter(PropertyId.NAME, event.getText(), true, false);
                ds.addContainerFilter(filter);
            }
        });

        layout.addComponent(filterField);
        layout.addComponent(list);

        vl.addComponent(headerButton);
        vl.addComponent(layout);
        return vl;
    }

    private CssLayout newHeaderButton(final TreeDataSource ds) {
        CssLayout cssLayout = new CssLayout();
        cssLayout.setWidth("97%");
        cssLayout.setMargin(false);

        ThemeResource plusIcon = new ThemeResource("images/assets/plus.png");

        final Button createGroupButton = new Button();
        createGroupButton.setStyleName(BaseTheme.BUTTON_LINK);
        createGroupButton.addStyleName("floatright paddingtop3");
        createGroupButton.setWidth("20px");
        createGroupButton.setIcon(plusIcon);
        createGroupButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(@SuppressWarnings("unused")
            final ClickEvent event) {
                showCreateGroupView();
            }

            private void showCreateGroupView() {
                router.getMainWindow().addWindow(
                    new CreateGroupView(repositories.group(), router.getMainWindow(), ds).modalWindow());
            }

        });
        cssLayout.addComponent(createGroupButton);
        return cssLayout;
    }
}
