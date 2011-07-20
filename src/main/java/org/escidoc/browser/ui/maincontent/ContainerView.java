/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or https://www.escidoc.org/license/ESCIDOC.LICENSE .
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui.maincontent;

import java.net.URISyntaxException;
import java.util.Iterator;

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.listeners.VersionHistoryClickListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.properties.LockStatus;
import de.escidoc.core.resources.common.properties.PublicStatus;
import de.escidoc.core.resources.om.container.Container;

/**
 * @author ARB
 * 
 */
@SuppressWarnings("serial")
public class ContainerView extends VerticalLayout {
    private static final String RIGHT_PANEL = "Right Panel";

    static final Logger LOG = LoggerFactory.getLogger(BrowserApplication.class);

    private final int appHeight;

    private final MainSite mainSite;

    private final ContainerProxy resourceProxy;

    private final CssLayout cssLayout = new CssLayout();

    private static final String CREATED_BY = "Created by ";

    private static final String FULLWIDHT_STYLE_NAME = "fullwidth";

    private static final String LAST_MODIFIED_BY = "Last modification by ";

    private static final String DIRECT_MEMBERS = "Direct Members";

    private static final String RESOURCE_NAME = "Container: ";

    protected static final String STATUS = "Container is ";

    private String status;

    private int accordionHeight;

    private int innerelementsHeight;

    private final EscidocServiceLocation serviceLocation;

    private final Window mainWindow;

    private final CurrentUser currentUser;

    private final Repositories repositories;

    private Component oldComponent = null;

    private Component swapComponent = null;

    private Label lblStatus;

    private Label lblLockstatus;

    private Window subwindow;

    public ContainerView(final EscidocServiceLocation serviceLocation, final MainSite mainSite,
        final ResourceProxy resourceProxy, final Window mainWindow, final CurrentUser currentUser,
        final Repositories repositories) throws EscidocClientException {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkArgument(resourceProxy instanceof ContainerProxy, resourceProxy.getClass()
            + " is not an instance of ContainerProxy.class");
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkArgument(resourceProxy instanceof ContainerProxy, resourceProxy.getClass()
            + " is not an instance of ContainerProxy.class");
        this.serviceLocation = serviceLocation;
        this.mainSite = mainSite;
        appHeight = mainSite.getApplicationHeight();
        this.resourceProxy = (ContainerProxy) resourceProxy;
        this.mainWindow = mainWindow;
        this.currentUser = currentUser;
        this.repositories = repositories;
        init();
    }

    private void init() throws EscidocClientException {
        configureLayout();
        handleLayoutListeners();
        createBreadCrumb();
        bindNameToHeader();
        bindDescription();
        addHorizontalRuler();
        bindProperties();
        addDirectMembers();
        addMetadataRecords();
        addComponent(cssLayout);
    }

    private void addMetadataRecords() {
        final MetadataRecs metaData =
            new MetadataRecs(resourceProxy, accordionHeight, mainWindow, serviceLocation, repositories, currentUser);
        rightCell(metaData.asAccord());
    }

    private void addDirectMembers() throws EscidocClientException {
        final DirectMember directMembers =
            new DirectMember(serviceLocation, mainSite, resourceProxy.getId(), mainWindow, currentUser, repositories);
        leftCell(DIRECT_MEMBERS, directMembers.containerAsTree());
    }

    /**
     * This is the inner Right Cell within a Context By default a set of Organizational Unit / Admin Description /
     * RelatedItem / Resources are bound
     * 
     * @param comptoBind
     */
    @SuppressWarnings("deprecation")
    private void rightCell(final Component comptoBind) {
        final Panel rightpnl = new Panel();
        rightpnl.setDescription(RIGHT_PANEL);
        rightpnl.setStyleName("floatright");
        rightpnl.setWidth("70%");
        rightpnl.setHeight("82%");
        rightpnl.getLayout().setMargin(false);
        rightpnl.addComponent(comptoBind);
        cssLayout.addComponent(rightpnl);
    }

    @SuppressWarnings("deprecation")
    private void leftCell(final String string, final Component comptoBind) {
        final Panel leftpnl = new Panel();

        leftpnl.setStyleName("directmembers floatleft");
        leftpnl.setScrollable(false);
        leftpnl.getLayout().setMargin(false);

        leftpnl.setWidth("30%");
        leftpnl.setHeight("82%");

        final Label nameofPanel = new Label("<strong>" + DIRECT_MEMBERS + "</string>", Label.CONTENT_RAW);
        leftpnl.addComponent(nameofPanel);
        leftpnl.addComponent(comptoBind);

        // Adding some buttons
        final AbsoluteLayout absL = new AbsoluteLayout();
        absL.setWidth("100%");
        absL.setHeight(innerelementsHeight + "px");
        final HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.addComponent(new Button("Add"));
        horizontal.addComponent(new Button("Delete"));
        horizontal.addComponent(new Button("Edit"));
        leftpnl.addComponent(horizontal);

        absL.addComponent(horizontal, "left: 0px; top: 380px;");
        cssLayout.addComponent(leftpnl);
    }

    /**
     * Binding Context Properties 2 sets of labels in 2 rows
     */
    private void bindProperties() {
        // LEFT SIde
        final Label descMetadata1 = new Label("ID: " + resourceProxy.getId());
        status = resourceProxy.getType().asLabel() + " is ";
        lblStatus = new Label(status + resourceProxy.getStatus(), Label.CONTENT_RAW);
        lblLockstatus = new Label(status + resourceProxy.getLockStatus(), Label.CONTENT_RAW);
        descMetadata1.setStyleName("floatleft");
        descMetadata1.setWidth("35%");

        lblStatus.setStyleName("floatleft");
        lblStatus.setDescription("status");
        lblStatus.setWidth("35%");
        lblLockstatus.setStyleName("floatleft");
        lblLockstatus.setDescription("lockstatus");
        lblLockstatus.setWidth("35%");

        final Label descMetadata2 =
            new Label(CREATED_BY + " " + resourceProxy.getCreator() + " on " + resourceProxy.getCreatedOn() + "<br/>"
                + LAST_MODIFIED_BY + " " + resourceProxy.getModifier() + " on " + resourceProxy.getModifiedOn(),
                Label.CONTENT_XHTML);

        descMetadata2.setStyleName("floatright");
        descMetadata2.setWidth("65%");

        final Label padder = new Label("&nbsp;");
        padder.setStyleName("floatright");
        padder.setWidth("5%");

        final Component versionHistory = getHistory();
        versionHistory.setStyleName("floatright");
        versionHistory.setWidth("60%");

        final Label test = new Label("History should come here");
        test.setStyleName("floatright");
        test.setWidth("65%");

        cssLayout.addComponent(descMetadata1);
        cssLayout.addComponent(descMetadata2);
        cssLayout.addComponent(lblStatus);
        cssLayout.addComponent(lblLockstatus);
        cssLayout.addComponent(test);
    }

    private void addHorizontalRuler() {
        final Label descRuler = new Label("<hr />", Label.CONTENT_RAW);
        descRuler.setStyleName("hr");
        cssLayout.addComponent(descRuler);
    }

    private void bindDescription() {
        final Label description = new Label(resourceProxy.getDescription());
        description.setStyleName(FULLWIDHT_STYLE_NAME);
        cssLayout.addComponent(description);
    }

    private void createBreadCrumb() {
        new BreadCrumbMenu(cssLayout, resourceProxy, mainWindow, serviceLocation, repositories);
    }

    /**
     * Building the Header Element that shows the title of the Container
     */
    private void bindNameToHeader() {
        final Label headerContext = new Label(RESOURCE_NAME + resourceProxy.getName());
        headerContext.setStyleName("h1 fullwidth floatleft");
        headerContext.setWidth("80%");
        headerContext.setDescription("header");
        cssLayout.addComponent(headerContext);
    }

    private void configureLayout() {
        setMargin(true, true, false, true);
        this.setHeight("100%");
        cssLayout.setWidth("100%");
        cssLayout.setHeight("100%");
        // this is an assumption of the height that should be left for the
        // accordion or elements of the DirectMember in the same level
        // I remove 420px that are taken by elements on the de.escidoc.esdc.page
        // and 40px for the accordion elements?
        final int innerelementsHeight = appHeight - 420;
        accordionHeight = innerelementsHeight - 20;

    }

    private void handleLayoutListeners() {
        try {
            if (repositories
                .pdp().forUser(currentUser.getUserId()).isAction(ActionIdConstants.UPDATE_CONTAINER)
                .forResource(resourceProxy.getId()).permitted()) {

                cssLayout.addListener(new LayoutClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void layoutClick(final LayoutClickEvent event) {
                        // Get the child component which was clicked
                        if (event.getChildComponent() != null) {
                            // Is Label?
                            if (event.getChildComponent().getClass().getCanonicalName() == "com.vaadin.ui.Label") {
                                final Label child = (Label) event.getChildComponent();
                                if ((child).getDescription() == "header") {
                                    // We are not editing header anymore
                                    // oldComponent = event.getClickedComponent();
                                    // swapComponent = editHeader(child.getValue().toString());
                                    // cssLayout.replaceComponent(oldComponent, swapComponent);
                                    // btnEdit.setVisible(true);
                                }
                                else if (child.getDescription() == "status") {
                                    reSwapComponents();
                                    oldComponent = event.getClickedComponent();
                                    swapComponent = editStatus(child.getValue().toString().replace(status, ""));
                                    cssLayout.replaceComponent(oldComponent, swapComponent);
                                }
                                else if (child.getDescription() == "lockstatus") {
                                    reSwapComponents();
                                    oldComponent = event.getClickedComponent();
                                    swapComponent = editLockStatus(child.getValue().toString().replace(status, ""));
                                    cssLayout.replaceComponent(oldComponent, swapComponent);
                                }
                            }
                            else if (event.getChildComponent().getClass().getCanonicalName() == "com.vaadin.ui.Panel") {
                                final Panel child = (Panel) event.getChildComponent();
                                if (child.getDescription() == RIGHT_PANEL) {
                                    Iterator itr = child.getComponentIterator();
                                    while (itr.hasNext()) {
                                        final Accordion element = (Accordion) itr.next();

                                        System.out.print(element + " ");

                                    }
                                }
                            }

                            else {
                                getWindow().showNotification(
                                    "The click was over a " + event.getChildComponent().getClass().getCanonicalName()
                                        + event.getChildComponent().getStyleName());
                            }
                        }
                        else {
                            reSwapComponents();
                        }
                    }

                    /**
                     * Switch the component back to the original component (Label) after inline editing
                     */
                    private void reSwapComponents() {
                        if (swapComponent != null) {
                            if (swapComponent instanceof Label) {
                                ((Label) oldComponent).setValue(((TextField) swapComponent).getValue());
                            }
                            else if ((swapComponent instanceof ComboBox)) {
                                ((Label) oldComponent).setValue(status + ((ComboBox) swapComponent).getValue());
                                this.addCommentWindow();
                            }
                            cssLayout.replaceComponent(swapComponent, oldComponent);
                            swapComponent = null;
                        }
                    }

                    private Component editLockStatus(final String lockStatus) {
                        final ComboBox cmbLockStatus = new ComboBox();
                        cmbLockStatus.setNullSelectionAllowed(false);
                        if (lockStatus.equals("unlocked")) {
                            cmbLockStatus.addItem(LockStatus.LOCKED.toString().toLowerCase());
                        }
                        else {
                            cmbLockStatus.addItem(LockStatus.UNLOCKED.toString().toLowerCase());
                        }
                        cmbLockStatus.select(1);
                        return cmbLockStatus;

                    }

                    private Component editStatus(final String publicStatus) {
                        final ComboBox cmbStatus = new ComboBox();
                        cmbStatus.setInvalidAllowed(false);
                        cmbStatus.setNullSelectionAllowed(false);
                        final String pubStatus = publicStatus.toUpperCase();
                        if (publicStatus.equals("pending")) {
                            cmbStatus.addItem(PublicStatus.PENDING.toString().toLowerCase());
                            cmbStatus.addItem(PublicStatus.SUBMITTED.toString().toLowerCase());
                            cmbStatus.setNullSelectionItemId(PublicStatus.PENDING.toString().toLowerCase());
                        }
                        else if (publicStatus.equals("submitted")) {
                            cmbStatus.setNullSelectionItemId(PublicStatus.SUBMITTED.toString().toLowerCase());
                            cmbStatus.addItem(PublicStatus.SUBMITTED.toString().toLowerCase());
                            cmbStatus.addItem(PublicStatus.IN_REVISION.toString().toLowerCase());
                            cmbStatus.addItem(PublicStatus.RELEASED.toString().toLowerCase());
                        }
                        else if (publicStatus.equals("in_revision")) {
                            cmbStatus.setNullSelectionItemId(PublicStatus.IN_REVISION.toString().toLowerCase());
                            cmbStatus.addItem(PublicStatus.IN_REVISION.toString().toLowerCase());
                            cmbStatus.addItem(PublicStatus.SUBMITTED.toString().toLowerCase());
                        }
                        else if (publicStatus.equals("released")) {
                            cmbStatus.setNullSelectionItemId(PublicStatus.RELEASED.toString().toLowerCase());
                            cmbStatus.addItem(PublicStatus.RELEASED.toString().toLowerCase());
                            cmbStatus.addItem(PublicStatus.WITHDRAWN.toString().toLowerCase());
                        }
                        else if (publicStatus.equals("withdrawn")) {
                            cmbStatus.setNullSelectionItemId(PublicStatus.WITHDRAWN.toString().toLowerCase());
                            cmbStatus.addItem(PublicStatus.WITHDRAWN.toString().toLowerCase());
                            cmbStatus.addItem(PublicStatus.SUBMITTED.toString().toLowerCase());
                        }
                        else {
                            cmbStatus.addItem(PublicStatus.valueOf(pubStatus));
                        }
                        cmbStatus.select(1);

                        return cmbStatus;
                    }

                    public void addCommentWindow() {
                        subwindow = new Window("Add Comment to the Edit operation");
                        subwindow.setModal(true);
                        // Configure the windws layout; by default a VerticalLayout
                        VerticalLayout layout = (VerticalLayout) subwindow.getContent();
                        layout.setMargin(true);
                        layout.setSpacing(true);
                        layout.setSizeUndefined();

                        final TextField editor = new TextField("Your Comment");
                        editor.setRequired(true);
                        editor.setRequiredError("The Field may not be empty.");

                        HorizontalLayout hl = new HorizontalLayout();

                        Button close = new Button("Update", new Button.ClickListener() {
                            // inline click-listener
                            @Override
                            public void buttonClick(ClickEvent event) {
                                // close the window by removing it from the parent window
                                updateContainer(editor.getValue().toString());
                                (subwindow.getParent()).removeWindow(subwindow);
                            }
                        });
                        Button cancel = new Button("Cancel", new Button.ClickListener() {
                            @Override
                            public void buttonClick(ClickEvent event) {
                                (subwindow.getParent()).removeWindow(subwindow);
                            }
                        });

                        hl.addComponent(close);
                        hl.addComponent(cancel);

                        subwindow.addComponent(editor);
                        subwindow.addComponent(hl);
                        mainWindow.addWindow(subwindow);
                    }

                    private void updatePublicStatus(Container container, String comment) throws EscidocClientException {

                        // Update PublicStatus if there is a change
                        if (!resourceProxy.getStatus().equals(lblStatus.getValue().toString().replace(status, ""))) {
                            repositories.container().changePublicStatus(container,
                                lblStatus.getValue().toString().replace(STATUS, "").toUpperCase(), comment);
                        }
                    }

                    private void updateLockStatus(Container container, String comment) throws EscidocClientException {

                        // Update LockStatus if there is a change
                        if (!resourceProxy.getLockStatus().equals(
                            lblLockstatus.getValue().toString().replace(status, ""))) {
                            repositories.container().changeLockStatus(container,
                                lblLockstatus.getValue().toString().replace(STATUS, "").toUpperCase(), comment);
                        }
                    }

                    private void updateContainer(String comment) {
                        this.addCommentWindow();
                        Container container;
                        try {
                            container = repositories.container().findContainerById(resourceProxy.getId());
                            if (resourceProxy.getLockStatus().equals("unlocked")) {
                                updatePublicStatus(container, comment);
                                // retrive the container to get the last modifiaction date.
                                container = repositories.container().findContainerById(resourceProxy.getId());
                                updateLockStatus(container, comment);
                            }
                            else {
                                updateLockStatus(container, comment);
                                updatePublicStatus(container, comment);
                            }
                        }
                        catch (final EscidocClientException e) {
                            LOG.debug(e.getLocalizedMessage());
                        }
                    }

                });
            }
        }
        catch (final EscidocClientException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            e.printStackTrace();
        }
        catch (final URISyntaxException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Checks if a resource has previous history and returns a string TODO in the future it should be a Link (Button
     * Link) that holds a reference to the history of the resource
     * 
     * @return String
     */
    private Component getHistory() {
        if (resourceProxy.getPreviousVersion()) {
            final Button versionHistory =
                new Button(" Has previous version", new VersionHistoryClickListener(resourceProxy, mainWindow,
                    serviceLocation, repositories));
            // versionHistory.setStyleName(BaseTheme.BUTTON_LINK);
            return versionHistory;
        }
        else {
            final Label strHistory = new Label("Has no previous history");
            return strHistory;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resourceProxy == null) ? 0 : resourceProxy.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContainerView other = (ContainerView) obj;
        if (resourceProxy == null) {
            if (other.resourceProxy != null) {
                return false;
            }
        }
        else if (!resourceProxy.equals(other.resourceProxy)) {
            return false;
        }
        return true;
    }
}
