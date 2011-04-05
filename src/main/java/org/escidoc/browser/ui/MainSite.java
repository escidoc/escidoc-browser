package org.escidoc.browser.ui;

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.ContainerRepository;
import org.escidoc.browser.repository.ContextRepository;
import org.escidoc.browser.repository.ItemRepository;
import org.escidoc.browser.ui.maincontent.SearchSimple;
import org.escidoc.browser.ui.mainpage.Footer;
import org.escidoc.browser.ui.mainpage.HeaderContainer;

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

@SuppressWarnings("serial")
public class MainSite extends VerticalLayout {

    private final CssLayout mainLayout;

    private NavigationTreeView mainnavtree;

    private final TabSheet maincontent = new TabSheet();

    private final int appHeight;

    private final BrowserApplication app;

    private final Window mainWindow;

    private EscidocServiceLocation serviceLocation;

    /**
     * The mainWindow should be revised whether we need it or not the appHeight
     * is the Height of the Application and I need it for calculations in the
     * inner elements
     * 
     * @param mainWindow
     * @param appHeight
     * @throws EscidocClientException
     */
    public MainSite(final Window mainWindow,
        final EscidocServiceLocation serviceLocation, final int appHeight,
        final BrowserApplication app) throws EscidocClientException {
        this.serviceLocation = serviceLocation;
        // General Height for the application
        this.appHeight = appHeight;
        this.app = app;
        this.mainWindow = mainWindow;
        this.serviceLocation = serviceLocation;
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
        maincontent.setStyleName("floatright paddingtop20");
        maincontent.setWidth("70%");
        maincontent.setHeight("86%");
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
        final Panel mainnav = new Panel();
        mainnav.setScrollable(true);
        mainnav.setStyleName("floatleft paddingtop20");
        mainnav.setWidth("30%");
        mainnav.setHeight("86%");

        final Button srchButton =
            new Button("Search", this, "OnClickSrchButton");
        srchButton.setStyleName(BaseTheme.BUTTON_LINK);
        srchButton.setIcon(new ThemeResource("../myTheme/images/search.png"));
        srchButton.setDescription("Search the Infrastructure");
        mainnav.addComponent(srchButton);

        final NavigationTreeView treemenu =
            new UiBuilder(serviceLocation).buildNavigationTree(
                new ContextRepository(serviceLocation),
                new ContainerRepository(serviceLocation), new ItemRepository(
                    serviceLocation), this, mainWindow);
        mainnavtree = treemenu;
        mainnav.addComponent(mainnavtree);

        return mainnav;
    }

    public void openTab(final Component cmp, String tabname) {
        maincontent.addComponent(cmp);
        maincontent.addTab(cmp);
        if (tabname.length()>50)
                tabname=tabname.substring(0,50)+"...";
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
