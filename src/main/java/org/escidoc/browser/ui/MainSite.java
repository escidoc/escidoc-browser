package org.escidoc.browser.ui;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.EscidocServiceLocationImpl;
import org.escidoc.browser.repository.ContainerRepository;
import org.escidoc.browser.repository.ContextRepository;
import org.escidoc.browser.ui.maincontent.Context;
import org.escidoc.browser.ui.mainpage.Footer;
import org.escidoc.browser.ui.mainpage.HeaderContainer;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class MainSite extends VerticalLayout {
    final EscidocServiceLocationImpl serviceLocation =
        new EscidocServiceLocationImpl(AppConstants.HARDCODED_ESCIDOC_URI);

    private final CssLayout mainLayout;

    private NavigationTreeView mainnavtree;

    private final TabSheet maincontent = new TabSheet();

    private final int appHeight;

    /**
     * The mainWindow should be revised whether we need it or not the appHeight
     * is the Height of the Application and I need it for calculations in the
     * inner elements
     * 
     * @param mainWindow
     * @param appHeight
     * @throws EscidocClientException
     */
    public MainSite(final Window mainWindow, final int appHeight)
        throws EscidocClientException {
        // General Height for the application
        this.appHeight = appHeight;
        this.setMargin(true);
        setSizeFull();
        this.setWidth("86%");

        // common part: create layout
        mainLayout = new CssLayout();
        mainLayout.setStyleName("maincontainer");
        mainLayout.setSizeFull();

        final HeaderContainer header = new HeaderContainer(this, appHeight);

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
        final Context context = new Context(appHeight);
        maincontent.addComponent(context);
        maincontent.addTab(context);
        maincontent.getTab(context).setCaption("Sommer 2010");
        maincontent.getTab(context).setClosable(true);

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

        // Search icon

        // Navication

        final NavigationTreeView treemenu =
            new UiBuilder().buildNavigationTree(new ContextRepository(
                serviceLocation), new ContainerRepository(serviceLocation));
        mainnavtree = treemenu;

        final Panel mainnav = new Panel();
        mainnav.setScrollable(true);
        mainnav.setStyleName("floatleft paddingtop20");
        mainnav.setWidth("30%");
        mainnav.setHeight("86%");
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
}
