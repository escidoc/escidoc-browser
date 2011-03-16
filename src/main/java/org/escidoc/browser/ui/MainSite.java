package org.escidoc.browser.ui;

import org.escidoc.browser.ui.maincontent.Context;
import org.escidoc.browser.ui.mainpage.Footer;
import org.escidoc.browser.ui.mainpage.HeaderContainer;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class MainSite extends VerticalLayout {

    private final CssLayout mainLayout;

    private final Tree mainnavtree;

    private final int appHeight;

    /**
     * The mainWindow should be revised wether we need it or not the appHeight
     * is the Height of the Application and I need it for calculations in the
     * inner elements
     * 
     * @param mainWindow
     * @param appHeight
     */
    public MainSite(final Window mainWindow, final int appHeight) {
        // General Height for the application
        this.appHeight = appHeight;
        this.setMargin(true);
        setSizeFull();
        this.setWidth("86%");

        // common part: create layout
        mainLayout = new CssLayout();
        mainLayout.setStyleName("maincontainer");
        mainLayout.setSizeFull();

        final HeaderContainer header = new HeaderContainer();

        // HERE COMES THE MAIN NAVIGATION (LEFT SIDE)
        final TreeMenu treemenu = new TreeMenu();
        mainnavtree = treemenu.sampleTree();

        final Panel mainnav = new Panel();
        mainnav.setScrollable(true);
        mainnav.setStyleName("floatleft paddingtop20");
        mainnav.setWidth("30%");
        mainnav.setHeight("86%");
        mainnav.addComponent(mainnavtree);

        // Right section TABS
        final TabSheet maincontent = new TabSheet();
        maincontent.setStyleName("floatright paddingtop20");
        maincontent.setWidth("70%");
        maincontent.setHeight("86%");

        // adding tab elements
        final Context context = new Context(appHeight);
        maincontent.addComponent(context);
        maincontent.addTab(context);
        maincontent.getTab(context).setCaption("Sommer 2010");
        maincontent.getTab(context).setClosable(true);

        final Footer futer = new Footer();

        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.addComponent(header);
        mainLayout.addComponent(mainnav);
        mainLayout.addComponent(maincontent);
        mainLayout.addComponent(futer);
        addComponent(mainLayout);

    }

}
