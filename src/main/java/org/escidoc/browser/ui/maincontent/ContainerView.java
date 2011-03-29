package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.ui.MainSite;

import com.google.common.base.Preconditions;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
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

        // BREADCRUMB
        BreadCrumbMenu bm = new BreadCrumbMenu(cssLayout, "container");
        // cssLayout.addComponent(bm);

        // HEADER
        Label headerContext = new Label("SOMMER 2010");
        headerContext.setStyleName("h1 fullwidth");
        cssLayout.addComponent(headerContext);

        // TODO move these labels somewhere
        // +++++++++++++++++++++++++++++++++++//
        // ContainerView Desc 1
        Label descContext1 =
            new Label(
                accordionHeight
                    + "+ Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. ");
        descContext1.setStyleName("fullwidth");
        cssLayout.addComponent(descContext1);

        // ContainerView Horizontal Ruler
        Label descRuler =
            new Label(
                "____________________________________________________________________________________________________");
        descRuler.setStyleName("hr");
        cssLayout.addComponent(descRuler);
        // TODO Fix this ruler! I cannot believe I did that line as a ruler

        // ContainerView DescMetadata1
        Label descMetadata1 =
            new Label("Name: Sommer 2010 <br /> "
                + "Description: Meine Bilder von Sommer 2010<br />"
                + "ID: escidoc:30294 is pending", Label.CONTENT_RAW);
        descMetadata1.setStyleName("floatleft columnheight50");
        descMetadata1.setWidth("30%");
        cssLayout.addComponent(descMetadata1);

        // ContainerView DescMetadata2

        Label descMetadata2 =
            new Label(
                "Created by: <a href='/ESCD/Frankie'>Frank Schwichtenberg</a> 26.01.2011, 09:33 <br>"
                    + "last modification by <a href='/ESCD/Frankie'>Frank Schwichtenberg</a> 26.01.2011, 09:33 <br>"
                    + "is not released, has no previous versions",
                Label.CONTENT_RAW);
        descMetadata2.setStyleName("floatright columnheight50");
        descMetadata2.setWidth("70%");
        cssLayout.addComponent(descMetadata2);

        // Direct Members!//TODO KUJDES
        DirectMember directMembers =
            new DirectMember(mainSite, resourceProxy.getId());

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
        leftpnl.addComponent(directMembers.containerasTree());
        absL.addComponent(horizontal, "left: 0px; top: 380px;");
        leftpnl.addComponent(absL);

        // right most panel
        MetadataRecs metadataRecs = new MetadataRecs(accordionHeight);
        Accordion acc = metadataRecs.asAccord();

        Panel rightpnl = new Panel();
        rightpnl.setStyleName("floatright");
        rightpnl.setWidth("70%");
        rightpnl.setHeight("100%");
        rightpnl.addComponent(acc);

        cssLayout.addComponent(leftpnl);
        cssLayout.addComponent(rightpnl);

        // cssLayout.addComponent();

        addComponent(cssLayout);
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
        int accordionHeight = innerelementsHeight - 40;
    }
}
