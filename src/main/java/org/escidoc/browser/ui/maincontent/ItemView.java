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

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.VersionHistoryClickListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.properties.LockStatus;
import de.escidoc.core.resources.common.properties.PublicStatus;
import de.escidoc.core.resources.om.item.Item;

@SuppressWarnings("serial")
public class ItemView extends VerticalLayout {

    private static final String DESC_LOCKSTATUS = "lockstatus";

    private static final String DESC_STATUS2 = "status";

    private static final String DESC_HEADER = "header";

    private static final String SUBWINDOW_EDIT = "Add Comment to the Edit operation";

    protected static final String STATUS = "Item is ";

    private String status;

    private static final Logger LOG = LoggerFactory.getLogger(ItemView.class);

    private final CssLayout cssLayout = new CssLayout();

    private final int appHeight;

    private final ItemProxyImpl resourceProxy;

    private final Window mainWindow;

    private final EscidocServiceLocation serviceLocation;

    private final Repositories repositories;

    private final CurrentUser currentUser;

    private int accordionHeight;

    private int innerelementsHeight;

    private Label lblLockstatus;

    private Component oldComponent = null;

    private Component swapComponent = null;

    private Label publicLblStatus;

    private Window subwindow;

    public ItemView(final EscidocServiceLocation serviceLocation, final Repositories repositories,
        final MainSite mainSite, final ResourceProxy resourceProxy, final Window mainWindow,
        final CurrentUser currentUser) {

        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null.");
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(mainSite, "mainSite is null.");
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null.");
        Preconditions.checkNotNull(mainWindow, "mainWindow is null.");

        this.resourceProxy = (ItemProxyImpl) resourceProxy;
        this.repositories = repositories;
        this.mainWindow = mainWindow;
        this.serviceLocation = serviceLocation;
        appHeight = mainSite.getApplicationHeight();
        this.currentUser = currentUser;

        init();
    }

    private void init() {
        buildLayout();
        handleLayoutListeners();
        createBreadcrumbp();
        bindNametoHeader();
        bindHrRuler();
        bindProperties();

        buildLeftCell(new ItemContent(repositories, resourceProxy, serviceLocation, mainWindow, currentUser));
        buildRightCell(new MetadataRecsItem(resourceProxy, accordionHeight, mainWindow, serviceLocation, repositories,
            currentUser).asAccord());

        addComponent(cssLayout);
    }

    /**
     * @param metadataRecs
     */
    private void buildRightCell(final Component metadataRecs) {
        final Panel rightPanel = new Panel();
        rightPanel.setStyleName("floatright");
        rightPanel.setWidth("70%");
        rightPanel.setHeight("82%");
        rightPanel.addComponent(metadataRecs);
        cssLayout.addComponent(rightPanel);
        rightPanel.getLayout().setMargin(false);
    }

    private void buildLeftCell(final Component itCnt) {
        final Panel leftPanel = new Panel();
        leftPanel.getLayout().setMargin(false);
        leftPanel.setStyleName("floatleft");
        leftPanel.setScrollable(false);
        leftPanel.setWidth("30%");
        leftPanel.setHeight("82%");
        leftPanel.addComponent(itCnt);
        cssLayout.addComponent(leftPanel);
    }

    private void bindProperties() {
        final Label descMetadata1 = new Label("ID: " + resourceProxy.getId());

        status = resourceProxy.getType().asLabel() + " is ";
        publicLblStatus = new Label(status + resourceProxy.getStatus(), Label.CONTENT_RAW);
        lblLockstatus = new Label(status + resourceProxy.getLockStatus(), Label.CONTENT_RAW);

        descMetadata1.setStyleName("floatleft");
        descMetadata1.setWidth("35%");

        buildPublicStatus();
        buildLockStatus();

        // RIGHT SIDE
        final Label descMetadata2 =
            new Label(ViewConstants.CREATED_BY + " " + resourceProxy.getCreator() + " on "
                + resourceProxy.getCreatedOn() + "<br/>" + ViewConstants.LAST_MODIFIED_BY + " "
                + resourceProxy.getModifier() + " on " + resourceProxy.getModifiedOn(), Label.CONTENT_XHTML);
        descMetadata2.setStyleName("floatright");
        descMetadata2.setWidth("65%");

        final Component versionHistory = getHistory();
        versionHistory.setStyleName("floatleft");
        versionHistory.setWidth("65%");

        final Label test = new Label("History should come here");
        test.setStyleName("floatright");
        test.setWidth("65%");

        cssLayout.addComponent(descMetadata1);
        cssLayout.addComponent(descMetadata2);
        cssLayout.addComponent(publicLblStatus);
        cssLayout.addComponent(lblLockstatus);
        cssLayout.addComponent(test);

    }

    private void buildPublicStatus() {
        publicLblStatus.setStyleName("floatleft");
        publicLblStatus.setDescription("status");
        publicLblStatus.setWidth("35%");
    }

    private void buildLockStatus() {
        lblLockstatus.setStyleName("floatleft");
        lblLockstatus.setDescription("lockstatus");
        lblLockstatus.setWidth("35%");
    }

    private void bindHrRuler() {
        final Label descRuler = new Label("<hr/>", Label.CONTENT_RAW);
        descRuler.setStyleName("hr");
        cssLayout.addComponent(descRuler);
    }

    private void bindNametoHeader() {
        final Label headerContext = new Label(ViewConstants.RESOURCE_NAME + resourceProxy.getName());
        headerContext.setDescription("header");
        headerContext.setStyleName("h2 fullwidth");
        cssLayout.addComponent(headerContext);
    }

    @SuppressWarnings("unused")
    private void createBreadcrumbp() {
        new BreadCrumbMenu(cssLayout, resourceProxy, mainWindow, serviceLocation, repositories);
    }

    private void buildLayout() {
        setMargin(true);
        setHeight("100%");
        cssLayout.setWidth("100%");
        cssLayout.setHeight("100%");
        innerelementsHeight = appHeight - 420;
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
                                if ((child).getDescription() == DESC_HEADER) {
                                    // We are not editing header anymore
                                    // oldComponent = event.getClickedComponent();
                                    // swapComponent = editHeader(child.getValue().toString());
                                    // cssLayout.replaceComponent(oldComponent, swapComponent);
                                    // btnEdit.setVisible(true);
                                }
                                else if ((child.getDescription() == DESC_STATUS2)
                                    && (!publicLblStatus.getValue().equals(status + "withdrawn"))) {
                                    reSwapComponents();
                                    oldComponent = event.getClickedComponent();
                                    swapComponent = editStatus(child.getValue().toString().replace(status, ""));
                                    cssLayout.replaceComponent(oldComponent, swapComponent);
                                }
                                else if (child.getDescription() == DESC_LOCKSTATUS) {
                                    reSwapComponents();
                                    oldComponent = event.getClickedComponent();
                                    swapComponent = editLockStatus(child.getValue().toString().replace(status, ""));
                                    cssLayout.replaceComponent(oldComponent, swapComponent);
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
                                ((Label) oldComponent).setValue(((TextArea) swapComponent).getValue());
                            }
                            else if ((swapComponent instanceof ComboBox)
                                && ((ComboBox) swapComponent).getValue() != null) {
                                ((Label) oldComponent).setValue(status + ((ComboBox) swapComponent).getValue());
                                addCommentWindow();
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
                        cmbLockStatus.select(Integer.valueOf(1));
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
                            publicLblStatus.setValue("withdrawn");
                        }
                        else {
                            cmbStatus.addItem(PublicStatus.valueOf(pubStatus));
                        }
                        cmbStatus.select(Integer.valueOf(1));

                        return cmbStatus;
                    }

                    public void addCommentWindow() {
                        subwindow = new Window(SUBWINDOW_EDIT);
                        subwindow.setModal(true);
                        // Configure the windws layout; by default a VerticalLayout
                        final VerticalLayout layout = (VerticalLayout) subwindow.getContent();
                        layout.setMargin(true);
                        layout.setSpacing(true);
                        layout.setSizeUndefined();

                        final TextArea editor = new TextArea("Your Comment");
                        editor.setRequired(true);
                        editor.setRequiredError("The Field may not be empty.");

                        final HorizontalLayout hl = new HorizontalLayout();

                        final Button close = new Button("Update", new Button.ClickListener() {
                            // inline click-listener
                            @Override
                            public void buttonClick(final ClickEvent event) {
                                // close the window by removing it from the parent window
                                updateItem(editor.getValue().toString());
                                (subwindow.getParent()).removeWindow(subwindow);
                            }
                        });
                        final Button cancel = new Button("Cancel", new Button.ClickListener() {
                            @Override
                            public void buttonClick(final ClickEvent event) {
                                (subwindow.getParent()).removeWindow(subwindow);
                            }
                        });

                        hl.addComponent(close);
                        hl.addComponent(cancel);

                        subwindow.addComponent(editor);
                        subwindow.addComponent(hl);
                        mainWindow.addWindow(subwindow);
                    }

                    private void updatePublicStatus(final Item item, final String comment)
                        throws EscidocClientException {
                        Preconditions.checkNotNull(item, "Item is null");
                        Preconditions.checkNotNull(comment, "Comment is null");
                        // Update PublicStatus if there is a change
                        if (!resourceProxy
                            .getStatus().equals(publicLblStatus.getValue().toString().replace(status, ""))) {
                            repositories.item().changePublicStatus(item,
                                publicLblStatus.getValue().toString().replace(STATUS, "").toUpperCase(), comment);
                        }
                    }

                    private void updateLockStatus(final Item item, final String comment) throws EscidocClientException {

                        // Update LockStatus if there is a change
                        if (!resourceProxy.getLockStatus().equals(
                            lblLockstatus.getValue().toString().replace(status, ""))) {
                            repositories.item().changeLockStatus(item,
                                lblLockstatus.getValue().toString().replace(STATUS, "").toUpperCase(), comment);
                        }
                    }

                    private void updateItem(final String comment) {
                        Item item;
                        try {
                            item = repositories.item().findItemById(resourceProxy.getId());
                            if (resourceProxy.getLockStatus().equals("unlocked")) {
                                updatePublicStatus(item, comment);
                                // retrive the container to get the last modifiaction date.
                                item = repositories.item().findItemById(resourceProxy.getId());
                                updateLockStatus(item, comment);
                            }
                            else {
                                updateLockStatus(item, comment);
                                updatePublicStatus(item, comment);
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
                new Button(" Has previous versions", new VersionHistoryClickListener(resourceProxy, mainWindow,
                    serviceLocation, repositories));
            versionHistory.setStyleName(BaseTheme.BUTTON_LINK);
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
        final ItemView other = (ItemView) obj;
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