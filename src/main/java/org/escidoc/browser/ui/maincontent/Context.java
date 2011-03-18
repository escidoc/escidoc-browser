package org.escidoc.browser.ui.maincontent;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

public class Context extends VerticalLayout {

    private final int appHeight;

    public Context(final int appHeight) {

        this.appHeight = appHeight;
        setMargin(true);
        this.setHeight("100%");
        final CssLayout cssLayout = new CssLayout();
        cssLayout.setWidth("100%");
        cssLayout.setHeight("100%");

        // this is an assumtion of the height that should be left for the
        // accordion or elements of the DirectMember in the same level
        final int accordionHeight = appHeight - 420;

        // BREADCRUMB
        final BreadCMenu bm = new BreadCMenu(cssLayout,"context");
        // cssLayout.addComponent(bm);

        // HEADER
        final Label headerContext = new Label("SOMMER 2010");
        headerContext.setStyleName("h1 fullwidth");
        cssLayout.addComponent(headerContext);

        // TODO move these labels somewhere
        // +++++++++++++++++++++++++++++++++++//
        // Context Desc 1
        final Label descContext1 =
            new Label(
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. ");
        descContext1.setStyleName("fullwidth");
        cssLayout.addComponent(descContext1);

        // Context Horizontal Ruler
        final Label descRuler =
            new Label(
                "____________________________________________________________________________________________________");
        descRuler.setStyleName("hr");
        cssLayout.addComponent(descRuler);
        // TODO Fix this ruler! I cannot believe I did that line as a ruler

        // Context DescMetadata1
        final Label descMetadata1 =
            new Label("Name: Sommer 2010 <br /> "
                + "Description: Meine Bilder von Sommer 2010<br />"
                + "ID: escidoc:30294 is pending", Label.CONTENT_RAW);
        descMetadata1.setStyleName("floatleft columnheight50");
        descMetadata1.setWidth("30%");
        cssLayout.addComponent(descMetadata1);

        // Context DescMetadata2

        final Label descMetadata2 =
            new Label(
                "Created by: <a href='/ESCD/Frankie'>Frank Schwichtenberg</a> 26.01.2011, 09:33 <br>"
                    + "last modification by <a href='/ESCD/Frankie'>Frank Schwichtenberg</a> 26.01.2011, 09:33 <br>"
                    + "is not released, has no previous versions",
                Label.CONTENT_RAW);
        descMetadata2.setStyleName("floatright columnheight50");
        descMetadata2.setWidth("70%");
        cssLayout.addComponent(descMetadata2);

        // Direct Members
        final DirectMember directMembers = new DirectMember();
        Tree treedirectmembers = new Tree();
        treedirectmembers = directMembers.asTree();

        final Panel rightpnl = new Panel();
        rightpnl.setStyleName("floatleft paddingtop10");
        rightpnl.setScrollable(false);
        rightpnl.setWidth("30%");
        rightpnl.setHeight("86%");
        rightpnl.addComponent(treedirectmembers);

        // Metadata Records
        final Accordion metadataRecs = new Accordion();
        metadataRecs.setSizeFull();
        final Label l1 = new Label("escidoc");
        l1.setHeight("400px");
        final Label l2 = new Label("Relations");
        l2.setHeight("400px");
        final Label l3 = new Label("Additional Resources");
        l3.setHeight("400px");
        // Add the components as tabs in the Accordion.
        metadataRecs.addTab(l1, "Metadata Records", null);
        metadataRecs.addTab(l2, "Relations", null);
        metadataRecs.addTab(l3, "Additional Resources", null);

        final Panel leftpnl = new Panel();
        leftpnl.setStyleName("floatright");
        leftpnl.setWidth("70%");
        leftpnl.setHeight("100%");
        leftpnl.addComponent(metadataRecs);

        cssLayout.addComponent(rightpnl);
        cssLayout.addComponent(leftpnl);
        // cssLayout.addComponent();

        addComponent(cssLayout);
    }

}
