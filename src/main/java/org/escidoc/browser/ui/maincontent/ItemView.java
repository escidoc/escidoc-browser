package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.ContainerProxy;
import org.escidoc.browser.repository.ItemProxyImpl;
import org.escidoc.browser.ui.MainSite;

import com.google.common.base.Preconditions;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Accordion;
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

    private int appHeight;

    private MainSite mainSite;

    private final CssLayout cssLayout = new CssLayout();

    private int accordionHeight;

    private int innerelementsHeight;

    private ItemProxyImpl resourceProxy;

    public ItemView(EscidocServiceLocation serviceLocation, MainSite mainSite, ResourceProxy resourceProxy, Window mainWindow) {
        Preconditions.checkNotNull(mainWindow, "resource is null.");
        this.resourceProxy = (ItemProxyImpl) resourceProxy;
        this.mainSite = mainSite;
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
        ItemContent itCnt = new ItemContent(accordionHeight - 30,resourceProxy);
        buildLeftCell(itCnt);

        // right most panel
        // TODO SOME PROBLEMS WITH THE RESOURCEPROXY
        MetadataRecsItem metadataRecs =
            new MetadataRecsItem(resourceProxy, innerelementsHeight, this.getApplication());
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
        Label descMetadata1 =
            new Label(NAME + resourceProxy.getName() + " <br /> " + DESCRIPTION
                + resourceProxy.getDescription() + "<br />" + "ID: "
                + resourceProxy.getId() + " is " + resourceProxy.getStatus(),
                Label.CONTENT_RAW);
        descMetadata1.setStyleName("floatleft columnheight50");
        descMetadata1.setWidth("30%");
        cssLayout.addComponent(descMetadata1);

        // ContainerView DescMetadata2

        Label descMetadata2 =
            new Label(CREATED_BY + "<a href='/ESCD/Frankie'> "
                + resourceProxy.getCreator() + "</a>"
                + resourceProxy.getCreatedOn() + "<br>" + LAST_MODIFIED_BY
                + " <a href='#user/" + resourceProxy.getModifier() + "'>"
                + resourceProxy.getModifier() + "</a>"
                + resourceProxy.getModifiedOn() + " <br>"
                + resourceProxy.getStatus(), Label.CONTENT_RAW);
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
        Label descContext1 =
            new Label(resourceProxy.getDescription());
        descContext1.setStyleName(FULLWIDHT_STYLE_NAME);
        cssLayout.addComponent(descContext1);
    }

    private void bindNametoHeader() {
        // HEADER
        Label headerContext = new Label(resourceProxy.getName());
        headerContext.setStyleName("h1 fullwidth");
        cssLayout.addComponent(headerContext);
    }

    private void createBreadcrumbp() {
        // BREADCRUMB
        BreadCrumbMenu bm = new BreadCrumbMenu(cssLayout, "item");
    }

    private void buildLayout() {
        setMargin(true);
        this.setHeight("100%");
        cssLayout.setWidth("100%");
        cssLayout.setHeight("100%");

        // this is an assumption of the height that should be left for the
        // accordion or elements of the DirectMember in the same level
        // I remove 420px that are taken by elements on the de.escidoc.esdc.page
        // and 40px for the accordion elements?
        innerelementsHeight = appHeight - 420;
        accordionHeight = innerelementsHeight - 40;
    }
}
