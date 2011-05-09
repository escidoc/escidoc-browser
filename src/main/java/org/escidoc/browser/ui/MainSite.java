package org.escidoc.browser.ui;

import java.util.Map;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModelFactory;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.repository.ContainerProxy;
import org.escidoc.browser.repository.ContainerRepository;
import org.escidoc.browser.repository.ContextRepository;
import org.escidoc.browser.repository.ItemProxy;
import org.escidoc.browser.repository.ItemRepository;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.helper.Util;
import org.escidoc.browser.ui.maincontent.ContainerView;
import org.escidoc.browser.ui.maincontent.ContextView;
import org.escidoc.browser.ui.maincontent.ItemView;
import org.escidoc.browser.ui.maincontent.SearchSimple;
import org.escidoc.browser.ui.mainpage.Footer;
import org.escidoc.browser.ui.mainpage.HeaderContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class MainSite extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(MainSite.class);

    private final CssLayout mainLayout;

    private NavigationTreeView mainnavtree;

    private final TabSheet maincontenttab = new TabSheet();

    private final BrowserApplication app;

    private final Window mainWindow;

    private EscidocServiceLocation serviceLocation;

    private final CurrentUser currentUser;

    /**
     * The mainWindow should be revised whether we need it or not the appHeight is the Height of the Application and I
     * need it for calculations in the inner elements
     * 
     * @param mainWindow
     * @throws EscidocClientException
     */
    public MainSite(final Window mainWindow, final EscidocServiceLocation serviceLocation,
        final BrowserApplication app, final CurrentUser user) throws EscidocClientException {
        this.serviceLocation = serviceLocation;
        // General Height for the application
        this.app = app;
        this.mainWindow = mainWindow;
        this.serviceLocation = serviceLocation;
        this.currentUser = user;
        this.setMargin(true);
        setSizeFull();
        this.setWidth("86%");

        // common part: create layout
        mainLayout = new CssLayout();
        mainLayout.setStyleName("maincontainer");
        mainLayout.setSizeFull();
        init();

        final HeaderContainer header = new HeaderContainer(this, getApplicationHeight(), app, serviceLocation, user);
        header.init();
        final Footer futer = new Footer();

        mainLayout.addComponent(header);
        // Creating the mainNav Panel
        mainLayout.addComponent(buildNavigationPanel());
        // Go Main Tab Content
        mainLayout.addComponent(buildTabContainer());
        permanentURLelement();
        mainLayout.addComponent(futer);
        addComponent(mainLayout);
    }

    /**
     * This is the Permanent Link entry point Check if there is a tab variable set in the GET Method of the page and
     * open a tab containing the object with that ID URL Example
     * http://localhost:8084/browser/mainWindow?tab=escidoc:16037
     * &type=Item&escidocurl=http://escidev4.fiz-karlsruhe.de:8080
     */
    private void permanentURLelement() {

        Map<String, String[]> parameters = this.app.getParameters();

        if (Util.hasTabArg(parameters) && Util.hasObjectType(parameters)) {

            String escidocID = parameters.get(AppConstants.ARG_TAB)[0];
            final ContainerRepository containerRepository;
            final ContextRepository contextRepository;
            final Repository itemRepository;
            final ResourceModelFactory resourceFactory;
            containerRepository = new ContainerRepository(serviceLocation);
            contextRepository = new ContextRepository(serviceLocation);
            itemRepository = new ItemRepository(serviceLocation);

            resourceFactory = new ResourceModelFactory(itemRepository, containerRepository, contextRepository);
            if (parameters.get(AppConstants.ARG_TYPE)[0].equals("Context")) {
                try {
                    ContextProxyImpl context = (ContextProxyImpl) resourceFactory.find(escidocID, ResourceType.CONTEXT);
                    openTab(new ContextView(serviceLocation, this, context, mainWindow, currentUser), context.getName());
                }
                catch (EscidocClientException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if (parameters.get(AppConstants.ARG_TYPE)[0].equals("Container")) {
                try {
                    ContainerProxy container = (ContainerProxy) resourceFactory.find(escidocID, ResourceType.CONTAINER);
                    openTab(new ContainerView(serviceLocation, this, container, mainWindow, currentUser),
                        container.getName());
                }
                catch (EscidocClientException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if (parameters.get(AppConstants.ARG_TYPE)[0].equals("Item")) {
                try {
                    ItemProxy item = (ItemProxy) resourceFactory.find(escidocID, ResourceType.ITEM);
                    openTab(new ItemView(serviceLocation, this, item, mainWindow), item.getName());
                }
                catch (EscidocClientException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else {
                throw new UnsupportedOperationException("Not yet implemented");
            }

        }
    }

    private void init() {
        // addWindowDimensionDetection();
    }

    /**
     * This is the main container It is a Tab Sheet with TABS within it
     * 
     * @return TabSheet
     */
    private TabSheet buildTabContainer() {
        maincontenttab.setStyleName("floatright paddingtop20");
        maincontenttab.setWidth("70%");
        maincontenttab.setHeight("86%");
        return maincontenttab;

    }

    /**
     * MainNavigation Panel This is the left-most (human side) panel on the page It contains a Main Navigation Tree
     * 
     * @return Panel
     * @throws EscidocClientException
     */
    private Panel buildNavigationPanel() throws EscidocClientException {

        final Panel mainnav = new Panel();
        mainnav.setScrollable(true);
        mainnav.setStyleName("floatleft paddingtop20");
        LOG.debug("Window width is: " + app.getApplicationWidth());
        mainnav.setWidth("30%");
        mainnav.setHeight("85%");

        // final Button srchButton = new Button("Search", this, "onClickSrchButton");
        // srchButton.setStyleName(BaseTheme.BUTTON_LINK);
        // srchButton.setIcon(new ThemeResource("../myTheme/images/search.png"));
        // srchButton.setDescription("Search the Infrastructure");

        final ContainerRepository containerRepository = new ContainerRepository(serviceLocation);
        containerRepository.loginWith(((CurrentUser) app.getUser()).getToken());

        final ContextRepository contextRepository = new ContextRepository(serviceLocation);
        contextRepository.loginWith(((CurrentUser) app.getUser()).getToken());

        final ItemRepository itemRepository = new ItemRepository(serviceLocation);
        itemRepository.loginWith(((CurrentUser) app.getUser()).getToken());

        final NavigationTreeView treemenu =
            new UiBuilder(serviceLocation, (CurrentUser) app.getUser()).buildNavigationTree(contextRepository,
                containerRepository, itemRepository, this, mainWindow);
        mainnavtree = treemenu;

        mainnav.addComponent(mainnavtree);

        return mainnav;
    }

    public void openTab(final Component cmp, String tabname) {
        Tab tb = maincontenttab.addTab(cmp);
        final String tabnameshort = null;
        if (tabname.length() > 50) {
            tb.setDescription(tabname);
            tabname = tabname.substring(0, 50) + "...";
        }
        tb.setCaption(tabname);

        maincontenttab.setSelectedTab(cmp);
        tb.setClosable(true);
    }

    /**
     * Getter mainContent
     * 
     * @return TabSheet
     */
    public TabSheet getMaincontent() {
        return maincontenttab;
    }

    /**
     * Handle the event from the srchButton Link at the buildNavigationPanel
     * 
     * @param event
     */
    public void onClickSrchButton(final Button.ClickEvent event) {
        final SearchSimple smpSearch = new SearchSimple(this);
        openTab(smpSearch, "Search Results");
    }

    public int getApplicationHeight() {
        return this.app.getApplicationHeight();
    }

}
