package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.NavigationTreeView;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class ContextView extends VerticalLayout {

    private static final String DESCRIPTION = "Description: ";

    private static final String CREATED_BY = "Created by";

    private static final String NAME = "Name: ";

    private static final String FULLWIDHT_STYLE_NAME = "fullwidth";

    private static final String LAST_MODIFIED_BY = "last modification by";

    private final CssLayout cssLayout = new CssLayout();

    private final MainSite mainSite;

    private final ResourceProxy resourceProxy;

    private int appHeight;

    public ContextView(final MainSite mainSite,
        final ResourceProxy resourceProxy) throws EscidocClientException {
        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s",
            resourceProxy);
        this.mainSite = mainSite;
        this.resourceProxy = resourceProxy;

        init();
    }

    private void init() throws EscidocClientException {
        configureLayout();
        createBreadCrumb();
        bindNameToHeader();
        bindDescription();
        addHorizontalRuler();
        bindProperties();

        // Left Inner Cell
        // Binding Direct Members in
        final DirectMember directMembers =
            new DirectMember(mainSite, resourceProxy.getId());
        leftCell(directMembers.contextasTree());

        // Right Inner Cell
        // Binding Additional Info into it
        ContextAddInfo cnxAddinfo =
            new ContextAddInfo(resourceProxy, appHeight);
        rightCell(cnxAddinfo.addPanels());

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
        final Panel leftpnl = new Panel();
        leftpnl.setStyleName("floatright");
        leftpnl.setWidth("70%");
        leftpnl.setHeight("100%");
        leftpnl.addComponent(comptoBind);
        cssLayout.addComponent(leftpnl);
    }

    /**
     * This is the inner Left Cell within a Context By default the Direct
     * Members are bound here
     * 
     * @param comptoBind
     */
    private void leftCell(Component comptoBind) {
        final Panel rightpnl = new Panel();
        rightpnl.setStyleName("floatleft paddingtop10");
        rightpnl.setScrollable(false);
        rightpnl.setWidth("30%");
        rightpnl.setHeight("86%");
        rightpnl.addComponent(comptoBind);
        cssLayout.addComponent(rightpnl);
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
        final Label descMetadata2 =
            new Label(CREATED_BY + "<a href='/ESCD/Frankie'>"
                + resourceProxy.getCreator() + "</a>"
                + resourceProxy.getCreatedOn() + "<br>" + LAST_MODIFIED_BY
                + " <a href='#user/" + resourceProxy.getModifier() + "'>"
                + resourceProxy.getModifier() + "</a>"
                + resourceProxy.getModifiedOn() + " <br>", Label.CONTENT_RAW);
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
