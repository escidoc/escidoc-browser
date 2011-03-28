package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.ui.MainSite;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ContextView extends VerticalLayout {

    private static final String DESCRIPTION = "Description: ";

    private static final String NAME = "Name: ";

    private static final String FULLWIDHT_STYLE_NAME = "fullwidth";

    private final CssLayout cssLayout = new CssLayout();

    private final MainSite mainSite;

    private final ResourceProxy resourceProxy;

    private int appHeight;

    public ContextView(final MainSite mainSite,
        final ResourceProxy resourceProxy) {
        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s",
            resourceProxy);
        this.mainSite = mainSite;
        this.resourceProxy = resourceProxy;

        init();
    }

    private void init() {
        configureLayout();
        createBreadCrumb();
        bindNameToHeader();
        bindDescription();
        addHorizontalRuler();

        // Context DescMetadata1
        final Label descMetadata1 =
            new Label(NAME + resourceProxy.getName() + " <br /> " + DESCRIPTION
                + resourceProxy.getDescription() + "<br />" + "ID: "
                + resourceProxy.getId() + " is " + resourceProxy.getStatus(),
                Label.CONTENT_RAW);
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

        addComponent(cssLayout);
    }

    // TODO Fix this ruler! I cannot believe I did that line as a ruler
    private void addHorizontalRuler() {
        final Label descRuler =
            new Label(
                "____________________________________________________________________________________________________");
        descRuler.setStyleName("hr");
        cssLayout.addComponent(descRuler);
    }

    // TODO move these labels somewhere
    private void bindDescription() {
        final Label description = new Label(resourceProxy.getDescription());
        description.setStyleName(FULLWIDHT_STYLE_NAME);
        cssLayout.addComponent(description);
    }

    private void createBreadCrumb() {
        final BreadCrumbMenu bm = new BreadCrumbMenu(cssLayout, "context");
    }

    private void bindNameToHeader() {
        final Label headerContext = new Label(resourceProxy.getName());
        headerContext.setStyleName("h1 fullwidth");
        cssLayout.addComponent(headerContext);
    }

    private void configureLayout() {
        appHeight = mainSite.getApplicationHeight();

        setMargin(true);
        setHeight(100, Sizeable.UNITS_PERCENTAGE);

        cssLayout.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        cssLayout.setHeight(100, Sizeable.UNITS_PERCENTAGE);

        // this is an assumtion of the height that should be left for the
        // accordion or elements of the DirectMember in the same level
        final int accordionHeight = appHeight - 420;
    }

}
