package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.listeners.TreeClickListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class ContextView extends VerticalLayout {
    private static final Logger LOG = LoggerFactory.getLogger(TreeClickListener.class);

    private static final String DESCRIPTION = "Description: ";

    private static final String CREATED_BY = "Created by";

    private static final String NAME = "Name: ";

    private static final String FULLWIDHT_STYLE_NAME = "fullwidth";

    private static final String LAST_MODIFIED_BY = "last modification by";

    private static final String DIRECT_MEMBERS = "Direct Members";

    private static final String RESOURCE_NAME = "Context: ";

    private static final String STATUS = "Status ";

    private final CssLayout cssLayout = new CssLayout();

    private final MainSite mainSite;

    private final ResourceProxy resourceProxy;

    private int appHeight;

    private int accordionHeight;

    private final EscidocServiceLocation serviceLocation;

    private final Window mainWindow;

    private final CurrentUser currentUser;

    public ContextView(final EscidocServiceLocation serviceLocation,
        final MainSite mainSite, final ResourceProxy resourceProxy,
        final Window mainWindow, final CurrentUser currentUser)
        throws EscidocClientException {
        Preconditions.checkNotNull(serviceLocation,
            "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s",
            resourceProxy);
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s",
            currentUser);
        this.serviceLocation = serviceLocation;
        this.mainSite = mainSite;
        appHeight = mainSite.getApplicationHeight();
        this.resourceProxy = resourceProxy;
        this.mainWindow = mainWindow;
        this.currentUser = currentUser;
        init();
    }

    private void init() throws EscidocClientException {
        configureLayout();
        createBreadCrumb();
        bindNameToHeader();
        bindDescription();
        addHorizontalRuler();
        bindProperties();
        addDirectMembersView();
        addContextDetailsView();
        addComponent(cssLayout);
    }

    private void addContextDetailsView() {
        final ContextAddInfo cnxAddinfo = new ContextAddInfo(resourceProxy, accordionHeight, mainWindow);
        rightCell(cnxAddinfo.addPanels());
    }

    private void addDirectMembersView() throws EscidocClientException {
        final DirectMember directMembers =
            new DirectMember(serviceLocation, mainSite, resourceProxy.getId(),
                mainWindow, currentUser);
        leftCell(DIRECT_MEMBERS, directMembers.contextAsTree());
    }

    /**
     * This is the inner Right Cell within a Context By default a set of Organizational Unit / Admin Description /
     * RelatedItem / Resources are bound
     * 
     * @param comptoBind
     */
    private void rightCell(final Component comptoBind) {
        final Panel leftpnl = new Panel();
        leftpnl.setStyleName("floatright");
        leftpnl.setWidth("70%");
        leftpnl.setHeight("100%");
        leftpnl.addComponent(comptoBind);
        cssLayout.addComponent(leftpnl);
    }

    /**
     * This is the inner Left Cell within a Context By default the Direct Members are bound here
     * 
     * @param directMembers
     * 
     * @param comptoBind
     */
    private void leftCell(final String directMembers, final Component comptoBind) {
        final Panel leftpnl = new Panel();

        leftpnl.setStyleName("directmembers floatleft paddingtop10");
        leftpnl.setScrollable(false);
        leftpnl.setWidth("30%");
        leftpnl.setHeight("86%");

        final Label nameofPanel = new Label("<strong>" + DIRECT_MEMBERS + "</string>", Label.CONTENT_RAW);
        leftpnl.addComponent(nameofPanel);

        leftpnl.addComponent(comptoBind);
        cssLayout.addComponent(leftpnl);
    }

    /**
     * Bindind Context Properties 2 sets of labels in 2 rows
     */
    private void bindProperties() {
        // LEFT SIde
        final Label descMetadata1 =
        // new Label(NAME + resourceProxy.getName() + " <br /> " + DESCRIPTION
        // + resourceProxy.getDescription() + "<br />" + "ID: "
        // + resourceProxy.getId() + " is " + resourceProxy.getStatus(),
        // Label.CONTENT_RAW);
            new Label("ID: " + resourceProxy.getId() + "<br />" + STATUS + "is " + resourceProxy.getStatus(),
                Label.CONTENT_RAW);
        descMetadata1.setStyleName("floatleft columnheight50");
        descMetadata1.setWidth("35%");
        cssLayout.addComponent(descMetadata1);

        // RIGHT SIDE
        final Label descMetadata2 =
            new Label(CREATED_BY + " <a href='/ESCD/Frankie'>" + resourceProxy.getCreator() + "</a> "
                + resourceProxy.getCreatedOn() + "<br>" + LAST_MODIFIED_BY + " <a href='#user/"
                + resourceProxy.getModifier() + "'>" + resourceProxy.getModifier() + "</a> "
                + resourceProxy.getModifiedOn() + " <br>", Label.CONTENT_RAW);
        descMetadata2.setStyleName("floatright columnheight50");
        descMetadata2.setWidth("65%");
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
        final BreadCrumbMenu bm = new BreadCrumbMenu(cssLayout, "context");
    }

    private void bindNameToHeader() {
        final Label headerContext = new Label(RESOURCE_NAME + resourceProxy.getName());
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
        accordionHeight = appHeight - 420;
    }

}
