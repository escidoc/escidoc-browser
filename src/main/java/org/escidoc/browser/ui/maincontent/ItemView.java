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

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.listeners.VersionHistoryClickListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.properties.LockStatus;
import de.escidoc.core.resources.common.properties.PublicStatus;
import de.escidoc.core.resources.om.item.Item;

@SuppressWarnings("serial")
public class ItemView extends VerticalLayout {
    static final Logger LOG = LoggerFactory.getLogger(BrowserApplication.class);

    private static final String CREATED_BY = "Created by";

    private static final String FULLWIDHT_STYLE_NAME = "fullwidth";

    private static final String LAST_MODIFIED_BY = "Last modification by";

    private static final String RESOURCE_NAME = "Item: ";

    private static final String STATUS = "Status is ";

    private final CssLayout cssLayout = new CssLayout();

    private final int appHeight;

    private final ItemProxyImpl resourceProxy;

    private final Window mainWindow;

    private final EscidocServiceLocation serviceLocation;

    private final Repositories repositories;

    private final CurrentUser currentUser;

    private int accordionHeight;

    private int innerelementsHeight;

    private Button btnEdit = null;

    private Component oldComponent = null;

    private Component swapComponent = null;

    private Label lblStatus;

    private Label lblLockstatus;

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

    void init() {
        buildLayout();
        handleLayoutListeners();
        createBreadcrumbp();
        createEditBtn();
        bindNametoHeader();
        bindHrRuler();
        bindProperties();
        buildLeftCell(new ItemContent(repositories, resourceProxy, serviceLocation, mainWindow, currentUser));
        buildRightCell(new MetadataRecsItem(resourceProxy, accordionHeight, mainWindow, serviceLocation, repositories)
            .asAccord());
        addComponent(cssLayout);
    }

    /**
     * @param metadataRecs
     */
    private void buildRightCell(final Component metadataRecs) {

        final Panel rightpnl = new Panel();
        rightpnl.setStyleName("floatright");
        rightpnl.setWidth("70%");
        rightpnl.setHeight("82%");
        rightpnl.addComponent(metadataRecs);
        cssLayout.addComponent(rightpnl);
    }

    private void buildLeftCell(final Component itCnt) {
        // Adding some buttons
        final AbsoluteLayout absL = new AbsoluteLayout();
        absL.setWidth("100%");
        absL.setHeight(innerelementsHeight + "px");

        final Panel leftpnl = new Panel();
        leftpnl.getLayout().setMargin(false);
        leftpnl.setStyleName("floatleft");
        leftpnl.setScrollable(false);
        leftpnl.setWidth("30%");
        leftpnl.setHeight("82%");
        leftpnl.addComponent(itCnt);
        cssLayout.addComponent(leftpnl);
    }

    private void bindProperties() {
        final Label descMetadata1 = new Label("ID: " + resourceProxy.getId());
        lblStatus = new Label(STATUS + resourceProxy.getStatus(), Label.CONTENT_RAW);
        lblLockstatus = new Label(STATUS + resourceProxy.getLockStatus(), Label.CONTENT_RAW);
        descMetadata1.setStyleName("floatleft");
        descMetadata1.setWidth("35%");

        lblStatus.setStyleName("floatleft");
        lblStatus.setDescription("status");
        lblStatus.setWidth("35%");
        lblLockstatus.setStyleName("floatleft");
        lblLockstatus.setDescription("lockstatus");
        lblLockstatus.setWidth("35%");

        // RIGHT SIDE
        final Label descMetadata2 =
            new Label(CREATED_BY + resourceProxy.getCreator() + resourceProxy.getCreatedOn() + "<br/>"
                + LAST_MODIFIED_BY + resourceProxy.getModifier() + " on " + resourceProxy.getModifiedOn(),
                Label.CONTENT_RAW);
        descMetadata2.setStyleName("floatright");
        descMetadata2.setWidth("65%");

        final Component versionHistory = getHistory();
        versionHistory.setStyleName("floatleft");
        versionHistory.setWidth("65%");

        Label test = new Label("History should come here");
        test.setStyleName("floatright");
        test.setWidth("65%");

        cssLayout.addComponent(descMetadata1);
        cssLayout.addComponent(descMetadata2);
        cssLayout.addComponent(lblStatus);
        cssLayout.addComponent(lblLockstatus);
        cssLayout.addComponent(test);
        // cssLayout.addComponent(getHistory());

    }

    private void bindHrRuler() {
        // ContainerView Horizontal Ruler
        final Label descRuler = new Label("<hr/>", Label.CONTENT_RAW);
        descRuler.setStyleName("hr");
        cssLayout.addComponent(descRuler);
    }

    private void bindNametoHeader() {
        final Label headerContext = new Label(RESOURCE_NAME + resourceProxy.getName());
        headerContext.setDescription("header");
        headerContext.setStyleName("h1 fullwidth");
        cssLayout.addComponent(headerContext);
    }

    private void createEditBtn() {
        btnEdit = new Button("Save Changes", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                btnEdit.setCaption("Save Changes");
                Item item;
                try {
                    item = repositories.item().findItemById(resourceProxy.getId());
                    if (resourceProxy.getLockStatus().equals("unlocked")) {
                        updatePublicStatus(item);
                        // retrive the container to get the last modifiaction date.
                        item = repositories.item().findItemById(resourceProxy.getId());
                        updateLockStatus(item);
                    }
                    else {
                        updateLockStatus(item);
                        updatePublicStatus(item);
                    }

                    repositories.item().changePublicStatus(item,
                        lblStatus.getValue().toString().replace(STATUS, "").toUpperCase());
                    btnEdit.detach();
                }
                catch (final EscidocClientException e) {
                    LOG.debug(e.getLocalizedMessage());
                }
            }

            private void updatePublicStatus(Item item) throws EscidocClientException {
                // Update PublicStatus if there is a change
                if (!resourceProxy.getStatus().equals(lblStatus.getValue().toString().replace(STATUS, ""))) {
                    repositories.item().changePublicStatus(item,
                        lblStatus.getValue().toString().replace(STATUS, "").toUpperCase());
                }
            }

            private void updateLockStatus(Item item) throws EscidocClientException {
                // Update LockStatus if there is a change
                if (!resourceProxy.getLockStatus().equals(lblLockstatus.getValue().toString().replace(STATUS, ""))) {
                    repositories.item().changeLockStatus(item,
                        lblLockstatus.getValue().toString().replace(STATUS, "").toUpperCase());
                }
            }
        });
        btnEdit.setStyleName("floatright");
        btnEdit.setVisible(false);
        cssLayout.addComponent(btnEdit);

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
                .pdp().forUser(currentUser.getUserId()).isAction(ActionIdConstants.UPDATE_ITEM)
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
                                    oldComponent = event.getClickedComponent();
                                    swapComponent = editStatus(child.getValue().toString().replace(STATUS, ""));
                                    cssLayout.replaceComponent(oldComponent, swapComponent);
                                }
                                else if (child.getDescription() == "lockstatus") {
                                    oldComponent = event.getClickedComponent();
                                    swapComponent = editLockStatus(child.getValue().toString().replace(STATUS, ""));
                                    cssLayout.replaceComponent(oldComponent, swapComponent);
                                }
                            }
                            else {
                                getWindow().showNotification(
                                    "The click was over a " + event.getChildComponent().getClass().getCanonicalName());
                            }
                        }
                        else {
                            if (swapComponent != null) {
                                if (swapComponent instanceof Label) {
                                    ((Label) oldComponent).setValue(((TextField) swapComponent).getValue());
                                }
                                else if ((swapComponent instanceof ComboBox)) {
                                    ((Label) oldComponent).setValue(STATUS + ((ComboBox) swapComponent).getValue());
                                    btnEdit.setVisible(true);
                                }
                                cssLayout.replaceComponent(swapComponent, oldComponent);
                                swapComponent = null;
                            }
                        }
                    }

                    private Component editLockStatus(String lockStatus) {
                        final ComboBox cmbLockStatus = new ComboBox();
                        cmbLockStatus.setNullSelectionAllowed(false);
                        if (lockStatus.equals("unlocked")) {
                            cmbLockStatus.addItem(LockStatus.LOCKED.toString().toLowerCase());
                        }
                        else {
                            cmbLockStatus.addItem(LockStatus.UNLOCKED.toString().toLowerCase());
                        }
                        return cmbLockStatus;
                    }

                    private Component editStatus(final String publicStatus) {
                        final ComboBox cmbStatus = new ComboBox();
                        if (publicStatus.equals("pending")) {
                            cmbStatus.addItem(PublicStatus.PENDING.toString().toLowerCase());
                            cmbStatus.addItem(PublicStatus.SUBMITTED.toString().toLowerCase());
                        }
                        else if (publicStatus.equals("submitted")) {
                            cmbStatus.addItem(PublicStatus.SUBMITTED.toString().toLowerCase());
                            cmbStatus.addItem(PublicStatus.IN_REVISION.toString().toLowerCase());
                            cmbStatus.addItem(PublicStatus.RELEASED.toString().toLowerCase());
                        }
                        else if (publicStatus.equals("in_revision")) {
                            cmbStatus.addItem(PublicStatus.IN_REVISION.toString().toLowerCase());
                            cmbStatus.addItem(PublicStatus.SUBMITTED.toString().toLowerCase());
                        }
                        else if (publicStatus.equals("released")) {
                            cmbStatus.addItem(PublicStatus.RELEASED.toString().toLowerCase());
                            cmbStatus.addItem(PublicStatus.WITHDRAWN.toString().toLowerCase());
                        }
                        else if (publicStatus.equals("withdrawn")) {
                            cmbStatus.addItem(PublicStatus.WITHDRAWN.toString().toLowerCase());
                            cmbStatus.addItem(PublicStatus.SUBMITTED.toString().toLowerCase());
                        }
                        return cmbStatus;
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