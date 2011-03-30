package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
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
import com.vaadin.ui.Tree;

import de.escidoc.core.client.exceptions.EscidocClientException;

/**
 * @author ARB
 * 
 */
public class ContainerView extends VerticalLayout {

    private int appHeight;

    private MainSite mainSite;

    private final ResourceProxy resourceProxy;

    private final CssLayout cssLayout = new CssLayout();

    private static final String DESCRIPTION = "Description: ";

    private static final String CREATED_BY = "Created by";

    private static final String NAME = "Name: ";

    private static final String FULLWIDHT_STYLE_NAME = "fullwidth";

    private static final String LAST_MODIFIED_BY = "Last modification by";

    private int accordionHeight;

    private int innerelementsHeight;

    public ContainerView(final MainSite mainSite,
        final ResourceProxy resourceProxy) throws EscidocClientException {

        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s",
            resourceProxy);
        this.mainSite = mainSite;
        this.appHeight = mainSite.getApplicationHeight();
        this.resourceProxy = resourceProxy;
        init();
    }

    void init() throws EscidocClientException {

        configureLayout();
        createBreadCrumb();
        bindNameToHeader();
        bindDescription();
        addHorizontalRuler();
        bindProperties();

        // Direct Members!
        DirectMember directMembers =
            new DirectMember(mainSite, resourceProxy.getId());
        leftCell(directMembers.containerasTree());

        // right most panel
        MetadataRecs metaData = new MetadataRecs(accordionHeight);
        rightCell(metaData.asAccord());

        // cssLayout.addComponent();

        addComponent(cssLayout);
    }

    /**
     * This is the inner Right Cell within a Context By default a set of
     * Organizational Unit / Admin Description / RelatedItem / Resources are
     * bound
     * 
     * @param comptoBind
     */
    private void rightCell(Component comptoBind) {
        final Panel rightpnl = new Panel();
        rightpnl.setStyleName("floatright");
        rightpnl.setWidth("70%");
        rightpnl.setHeight("100%");
        rightpnl.addComponent(comptoBind);
        cssLayout.addComponent(rightpnl);
    }

    private void leftCell(Component comptoBind) {
        final Panel leftpnl = new Panel();
        leftpnl.setStyleName("floatleft paddingtop10");
        leftpnl.setScrollable(false);
        leftpnl.setWidth("30%");
        leftpnl.setHeight("86%");
        leftpnl.addComponent(comptoBind);

        // Adding some buttons
        AbsoluteLayout absL = new AbsoluteLayout();
        absL.setWidth("100%");
        absL.setHeight(innerelementsHeight + "px");
        HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.addComponent(new Button("Add"));
        horizontal.addComponent(new Button("Delete"));
        horizontal.addComponent(new Button("Edit"));
        leftpnl.addComponent(horizontal);

        absL.addComponent(horizontal, "left: 0px; top: 380px;");
        cssLayout.addComponent(leftpnl);
    }

    /**
     * Bindind Context Properties 2 sets of labels in 2 rows
     */
    private void bindProperties() {
        // LEFT SIde
        final Label descMetadata1 =
            new Label(NAME + resourceProxy.getName() + " <br /> " + DESCRIPTION
                + resourceProxy.getDescription() + "<br />" + "ID: "
                + resourceProxy.getId() + " is " + resourceProxy.getStatus(),
                Label.CONTENT_RAW);
        descMetadata1.setStyleName("floatleft columnheight50");
        descMetadata1.setWidth("30%");
        cssLayout.addComponent(descMetadata1);

        // RIGHT SIDE
        // TODO HAS NO PREVIOUS VERSION
        final Label descMetadata2 =
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

    // TODO Fix this ruler! I cannot believe I did that line as a ruler
    private void addHorizontalRuler() {
        final Label descRuler =
            new Label(
                "____________________________________________________________________________________________________");
        descRuler.setStyleName("hr");
        cssLayout.addComponent(descRuler);
    }

    private void bindDescription() {
        final Label description = new Label(resourceProxy.getDescription());
        description.setStyleName(FULLWIDHT_STYLE_NAME);
        cssLayout.addComponent(description);
    }

    private void createBreadCrumb() {
        final BreadCrumbMenu bm = new BreadCrumbMenu(cssLayout, "container");
    }

    private void bindNameToHeader() {
        final Label headerContext = new Label(resourceProxy.getName());
        headerContext.setStyleName("h1 fullwidth");
        cssLayout.addComponent(headerContext);
    }

    private void configureLayout() {
        setMargin(true);
        this.setHeight("100%");

        cssLayout.setWidth("100%");
        cssLayout.setHeight("100%");

        // this is an assumption of the height that should be left for the
        // accordion or elements of the DirectMember in the same level
        // I remove 420px that are taken by elements on the de.escidoc.esdc.page
        // and 40px for the accordion elements?
        int innerelementsHeight = appHeight - 420;
        accordionHeight = innerelementsHeight - 40;
    }
}
