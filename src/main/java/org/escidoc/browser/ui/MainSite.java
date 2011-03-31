package org.escidoc.browser.ui;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.EscidocServiceLocationImpl;
import org.escidoc.browser.repository.ContainerRepository;
import org.escidoc.browser.repository.ContextRepository;
import org.escidoc.browser.repository.ItemRepository;
import org.escidoc.browser.ui.listeners.TreeClickListener;
import org.escidoc.browser.ui.maincontent.SearchSimple;
import org.escidoc.browser.ui.mainpage.Footer;
import org.escidoc.browser.ui.mainpage.HeaderContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class MainSite extends VerticalLayout {
    final EscidocServiceLocationImpl serviceLocation =
        new EscidocServiceLocationImpl(AppConstants.HARDCODED_ESCIDOC_URI);

    private final CssLayout mainLayout;

    private NavigationTreeView mainnavtree;

    private final TabSheet maincontent = new TabSheet();

    private final int appHeight;

    private final BrowserApplication app;

    private final Window mainWindow;

    private static final Logger LOG = LoggerFactory
        .getLogger(TreeClickListener.class);

    /**
     * The mainWindow should be revised whether we need it or not the appHeight
     * is the Height of the Application and I need it for calculations in the
     * inner elements
     * 
     * @param mainWindow
     * @param appHeight
     * @throws EscidocClientException
     */
    public MainSite(final Window mainWindow, final int appHeight,
        final BrowserApplication app) throws EscidocClientException {
        // General Height for the application
        this.appHeight = appHeight;
        this.app = app;
        this.mainWindow = mainWindow;
        this.setMargin(true);
        setSizeFull();
        this.setWidth("86%");

        // common part: create layout
        mainLayout = new CssLayout();
        mainLayout.setStyleName("maincontainer");
        mainLayout.setSizeFull();

        final HeaderContainer header =
            new HeaderContainer(this, appHeight, app);

        final Footer futer = new Footer();

        mainLayout.addComponent(header);
        // Creating the mainNav Panel
        mainLayout.addComponent(buildNavigationPanel());
        // Go Main Tab Content
        mainLayout.addComponent(buildTabContainer());

        mainLayout.addComponent(futer);

        addComponent(mainLayout);
    }

    /**
     * This is the main container It is a Tab Sheet with TABS within it
     * 
     * @return TabSheet
     */
    private TabSheet buildTabContainer() {
        // Right section TABS

        maincontent.setStyleName("floatright paddingtop20");
        maincontent.setWidth("70%");
        maincontent.setHeight("86%");

        // adding tab elements
        // final ContextView contextView = new ContextView(this);
        // maincontent.addComponent(contextView);
        // maincontent.addTab(contextView);
        // maincontent.getTab(contextView).setCaption("Sommer 2010");
        // maincontent.getTab(contextView).setClosable(true);

        return maincontent;

    }

    /**
     * MainNavigation Panel This is the left-most (human side) panel on the page
     * It contains a Main Navigation Tree
     * 
     * @return Panel
     * @throws EscidocClientException
     */
    private Panel buildNavigationPanel() throws EscidocClientException {
        // HERE COMES THE MAIN NAVIGATION (LEFT SIDE)
        final Panel mainnav = new Panel();
        mainnav.setScrollable(true);
        mainnav.setStyleName("floatleft paddingtop20");
        mainnav.setWidth("30%");
        mainnav.setHeight("86%");

        // Search icon
        final Button srchButton =
            new Button("Search", this, "OnClickSrchButton");
        srchButton.setStyleName(BaseTheme.BUTTON_LINK);
        srchButton.setIcon(new ThemeResource("../myTheme/images/search.png"));
        srchButton.setDescription("Search the Infrastructure");
        mainnav.addComponent(srchButton);

        // Navication tree

        final NavigationTreeView treemenu =
            new UiBuilder().buildNavigationTree(new ContextRepository(
                serviceLocation), new ContainerRepository(serviceLocation),
                new ItemRepository(serviceLocation), this);
        mainnavtree = treemenu;
        mainnav.addComponent(mainnavtree);

        return mainnav;
    }

    public void openTab(final Component cmp, final String tabname) {
        // New Tab

        maincontent.addComponent(cmp);
        maincontent.addTab(cmp);
        maincontent.getTab(cmp).setCaption(tabname);
        maincontent.setSelectedTab(cmp);
        maincontent.getTab(cmp).setClosable(true);
    }

    /**
     * Getter mainContent
     * 
     * @return TabSheet
     */
    public TabSheet getMaincontent() {
        return maincontent;
    }

    /**
     * Handle the event from the srchButton Link at the buildNavigationPanel
     * 
     * @param event
     */
    public void OnClickSrchButton(final Button.ClickEvent event) {
        final SearchSimple smpSearch = new SearchSimple(this);
        openTab(smpSearch, "Search Results");
    }

    public int getApplicationHeight() {
        return appHeight;
    }
}
