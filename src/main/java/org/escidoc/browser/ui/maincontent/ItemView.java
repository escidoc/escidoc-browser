package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.ui.MainSite;

import com.google.common.base.Preconditions;
import com.vaadin.Application;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ItemView extends VerticalLayout {

    private static final String DESCRIPTION = "Description: ";

    private static final String CREATED_BY = "Created by";

    private static final String NAME = "Name: ";

    private static final String FULLWIDHT_STYLE_NAME = "fullwidth";

    private static final String LAST_MODIFIED_BY = "Last modification by";

    private static final String DIRECT_MEMBERS = "Direct Members";

    private static final String RESOURCE_NAME = "Item: ";

    private static final String STATUS = "Status ";

    private int appHeight;

    private final MainSite mainSite;

    private final CssLayout cssLayout = new CssLayout();

    private int accordionHeight;

    private int innerelementsHeight;

    private final ItemProxyImpl resourceProxy;

    private Application app;

    private final Window mainWindow;

    private final EscidocServiceLocation serviceLocation;

    public ItemView(EscidocServiceLocation serviceLocation, MainSite mainSite, ResourceProxy resourceProxy,
        Window mainWindow) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null.");
        Preconditions.checkNotNull(mainSite, "mainSite is null.");
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null.");
        Preconditions.checkNotNull(mainWindow, "mainWindow is null.");
        this.resourceProxy = (ItemProxyImpl) resourceProxy;
        this.mainSite = mainSite;
        this.mainWindow = mainWindow;
        this.serviceLocation = serviceLocation;
        appHeight = mainSite.getApplicationHeight();
        init();
    }

    void init() {
        buildLayout();
        createBreadcrumbp();
        bindNametoHeader();
        bindDescription();
        bindHrRuler();
        bindProperties();

        // Direct Members
        ItemContent itCnt = new ItemContent(accordionHeight - 30, resourceProxy, serviceLocation);
        buildLeftCell(itCnt);

        // right most panelY
        MetadataRecsItem metadataRecs =
            new MetadataRecsItem(resourceProxy, accordionHeight, mainWindow, serviceLocation);
        buildRightCell(metadataRecs.asAccord());

        addComponent(cssLayout);
    }

    /**
     * @param metadataRecs
     */
    private void buildRightCell(Component metadataRecs) {

        Panel rightpnl = new Panel();
        rightpnl.setStyleName("floatright");
        rightpnl.setWidth("70%");
        rightpnl.setHeight("86%");
        rightpnl.addComponent(metadataRecs);
        cssLayout.addComponent(rightpnl);
    }

    /**
     * @param itCnt
     * @return
     */
    private void buildLeftCell(Component itCnt) {
        // Adding some buttons
        AbsoluteLayout absL = new AbsoluteLayout();
        absL.setWidth("100%");
        absL.setHeight(innerelementsHeight + "px");
        HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.addComponent(new Button("Add"));
        horizontal.addComponent(new Button("Delete"));
        horizontal.addComponent(new Button("Edit"));

        final Panel leftpnl = new Panel();
        leftpnl.setStyleName("floatleft paddingtop10");
        leftpnl.setScrollable(false);
        leftpnl.setWidth("30%");
        leftpnl.setHeight("86%");
        leftpnl.addComponent(itCnt);
        absL.addComponent(horizontal, "left: 0px; top: 280px;");
        cssLayout.addComponent(leftpnl);
    }

    private void bindProperties() {
        // ContainerView DescMetadata1
        // Label descMetadata1 =
        // new Label(NAME + resourceProxy.getName() + " <br /> " + DESCRIPTION
        // + resourceProxy.getDescription() + "<br />" + "ID: "
        // + resourceProxy.getId() + " is " + resourceProxy.getStatus(),
        // Label.CONTENT_RAW);
        Label descMetadata1 =
            new Label("ID: " + resourceProxy.getId() + "<br />" + STATUS + " is " + resourceProxy.getStatus(),
        descMetadata1.setStyleName("floatleft columnheight50");
        descMetadata1.setWidth("30%");
        cssLayout.addComponent(descMetadata1);

        // ContainerView DescMetadata2

        Label descMetadata2 =
            new Label(CREATED_BY + "<a href='/ESCD/Frankie'> " + resourceProxy.getCreator() + "</a> "
                + resourceProxy.getCreatedOn() + " <br>" + LAST_MODIFIED_BY + " <a href='#user/"
                + resourceProxy.getModifier() + "'>" + resourceProxy.getModifier() + "</a> "
                + resourceProxy.getModifiedOn() + " " + getHistory(), Label.CONTENT_RAW);
        descMetadata2.setStyleName("floatright columnheight50");
        descMetadata2.setWidth("70%");
        cssLayout.addComponent(descMetadata2);
    }

    private void bindHrRuler() {
        // ContainerView Horizontal Ruler
        Label descRuler =
            new Label(
                "____________________________________________________________________________________________________");
        descRuler.setStyleName("hr");
        cssLayout.addComponent(descRuler);
    }

    private void bindDescription() {
        Label descContext1 = new Label(resourceProxy.getDescription());
        descContext1.setStyleName(FULLWIDHT_STYLE_NAME);
        cssLayout.addComponent(descContext1);
    }

    private void bindNametoHeader() {
        // HEADER
        Label headerContext = new Label(RESOURCE_NAME + resourceProxy.getName());
        headerContext.setStyleName("h1 fullwidth");
        cssLayout.addComponent(headerContext);
    }

    private void createBreadcrumbp() {
        // BREADCRUMB
        BreadCrumbMenu bm = new BreadCrumbMenu(cssLayout, resourceProxy);
    }

    private void buildLayout() {
        setMargin(true);
        this.setHeight("100%");
        cssLayout.setWidth("100%");
        cssLayout.setHeight("100%");
        final int innerelementsHeight = appHeight - 420;
        accordionHeight = innerelementsHeight - 20;
    }

    @Override
    public void attach() {
        this.app = getApplication();
    }

    /**
     * Checks if a resource has previous history and returns a string TODO in the future it should be a Link (Button
     * Link) that holds a reference to the history of the resource
     * 
     * @return String
     */
    private String getHistory() {
        String strHistory;
        if (resourceProxy.getPreviousVersion() == null) {
            strHistory = " has no previous history";
        }
        else {
            strHistory = " previous version";
        }
        return strHistory;
    }

}
