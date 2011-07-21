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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
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

    protected final class ItemLayoutClickListener implements LayoutClickListener {

        private Component lockComboBox;

        private Component publicStatusComboBox;

        @Override
        public void layoutClick(final LayoutClickEvent event) {
            if (event.getChildComponent() != null) {
                if (event.getChildComponent().equals(publicStatusLabel)) {
                    clickedPublicStatusLabel = (Label) event.getClickedComponent();
                    publicStatusComboBox =
                        editPublicStatus((clickedPublicStatusLabel).getValue().toString().replace(resourceIs, ""));
                    cssLayout.replaceComponent(clickedPublicStatusLabel, publicStatusComboBox);
                }
                else if (event.getChildComponent().equals(lockStatusLabel)) {
                    clickedLockLabel = (Label) event.getClickedComponent();
                    lockComboBox = editLockStatus((clickedLockLabel).getValue().toString().replace(resourceIs, ""));
                    cssLayout.replaceComponent(clickedLockLabel, lockComboBox);
                }
            }
        }

        private Component editLockStatus(final String lockStatus) {
            final ComboBox lockStatusComboBox = new ComboBox();
            lockStatusComboBox.setImmediate(true);
            lockStatusComboBox.setNullSelectionAllowed(false);
            lockStatusComboBox.setNullSelectionItemId(lockStatus);
            lockStatusComboBox.addListener(new ValueChangeListener() {

                @Override
                public void valueChange(final ValueChangeEvent event) {
                    (clickedLockLabel).setValue(resourceIs + (lockStatusComboBox).getValue());
                    cssLayout.replaceComponent(lockStatusComboBox, clickedLockLabel);
                    btnEdit.setVisible(true);

                }
            });
            if (lockStatus.equals("unlocked")) {
                lockStatusComboBox.addItem(LockStatus.LOCKED.toString().toLowerCase());
            }
            else {
                lockStatusComboBox.addItem(LockStatus.UNLOCKED.toString().toLowerCase());
            }
            return lockStatusComboBox;
        }

        private Component editPublicStatus(final String publicStatus) {
            final ComboBox publicStatusComboBox = new ComboBox();
            publicStatusComboBox.setImmediate(true);
            publicStatusComboBox.setNullSelectionAllowed(false);
            publicStatusComboBox.setNullSelectionItemId(publicStatus);
            publicStatusComboBox.addListener(new ValueChangeListener() {

                @Override
                public void valueChange(final ValueChangeEvent event) {
                    (clickedPublicStatusLabel).setValue(resourceIs + (publicStatusComboBox).getValue());
                    cssLayout.replaceComponent(publicStatusComboBox, clickedPublicStatusLabel);
                    btnEdit.setVisible(true);
                }
            });

            if (publicStatus.equals("pending")) {
                publicStatusComboBox.addItem(PublicStatus.PENDING.toString().toLowerCase());
                publicStatusComboBox.addItem(PublicStatus.SUBMITTED.toString().toLowerCase());
            }
            else if (publicStatus.equals("submitted")) {
                publicStatusComboBox.addItem(PublicStatus.SUBMITTED.toString().toLowerCase());
                publicStatusComboBox.addItem(PublicStatus.IN_REVISION.toString().toLowerCase());
                publicStatusComboBox.addItem(PublicStatus.RELEASED.toString().toLowerCase());
            }
            else if (publicStatus.equals("in_revision")) {
                publicStatusComboBox.addItem(PublicStatus.IN_REVISION.toString().toLowerCase());
                publicStatusComboBox.addItem(PublicStatus.SUBMITTED.toString().toLowerCase());
            }
            else if (publicStatus.equals("released")) {
                publicStatusComboBox.addItem(PublicStatus.RELEASED.toString().toLowerCase());
                publicStatusComboBox.addItem(PublicStatus.WITHDRAWN.toString().toLowerCase());
            }
            else if (publicStatus.equals("withdrawn")) {
                publicStatusComboBox.addItem(PublicStatus.WITHDRAWN.toString().toLowerCase());
                publicStatusComboBox.addItem(PublicStatus.SUBMITTED.toString().toLowerCase());
            }
            return publicStatusComboBox;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(ItemView.class);

    private final CssLayout cssLayout = new CssLayout();

    private String resourceIs;

    private final int appHeight;

    private final ItemProxyImpl resourceProxy;

    private final Window mainWindow;

    private final EscidocServiceLocation serviceLocation;

    private final Repositories repositories;

    private final CurrentUser currentUser;

    private int accordionHeight;

    private int innerelementsHeight;

    private Button btnEdit;

    private Label clickedLockLabel;

    private Label clickedPublicStatusLabel;

    private Label publicStatusLabel;

    private Label lockStatusLabel;

    protected Window subwindow;

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
        createEditBtn();
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
        resourceIs = resourceProxy.getType().asLabel() + " is ";

        publicStatusLabel = new Label(resourceIs + resourceProxy.getStatus(), Label.CONTENT_RAW);
        lockStatusLabel = new Label(resourceIs + resourceProxy.getLockStatus(), Label.CONTENT_RAW);

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
        cssLayout.addComponent(publicStatusLabel);
        cssLayout.addComponent(lockStatusLabel);
        cssLayout.addComponent(test);

    }

    private void buildPublicStatus() {
        publicStatusLabel.setStyleName("floatleft");
        publicStatusLabel.setDescription("status");
        publicStatusLabel.setWidth("35%");
    }

    private void buildLockStatus() {
        lockStatusLabel.setStyleName("floatleft");
        lockStatusLabel.setDescription("lockstatus");
        lockStatusLabel.setWidth("35%");
    }

    private void bindHrRuler() {
        final Label descRuler = new Label("<hr/>", Label.CONTENT_RAW);
        descRuler.setStyleName("hr");
        cssLayout.addComponent(descRuler);
    }

    private void bindNametoHeader() {
        final Label headerContext = new Label(ViewConstants.RESOURCE_NAME + resourceProxy.getName());
        headerContext.setDescription("header");
        headerContext.setStyleName("h1 fullwidth");
        cssLayout.addComponent(headerContext);
    }

    private void createEditBtn() {
        btnEdit = new Button("Save Changes", new Button.ClickListener() {

            @Override
            public void buttonClick(final ClickEvent event) {
                btnEdit.setVisible(false);
                this.addCommentWindow();
            }

            private void updateItem(String comment) {
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

                    repositories.item().changePublicStatus(item,
                        publicStatusLabel.getValue().toString().replace(resourceIs, "").toUpperCase(), comment);
                    btnEdit.detach();
                }
                catch (final EscidocClientException e) {
                    LOG.debug(e.getLocalizedMessage());
                }
            }

            private void addCommentWindow() {
                subwindow = new Window("Add Comment to the Edit Operation");
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
                        updateItem(editor.getValue().toString());
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

            private void updatePublicStatus(final Item item, final String comment) throws EscidocClientException {
                // Update PublicStatus if there is a change
                if (!resourceProxy.getStatus().equals(publicStatusLabel.getValue().toString().replace(resourceIs, ""))) {
                    repositories.item().changePublicStatus(item,
                        publicStatusLabel.getValue().toString().replace(resourceIs, "").toUpperCase(), comment);
                }
            }

            private void updateLockStatus(final Item item, final String comment) throws EscidocClientException {
                // Update LockStatus if there is a change
                if (!resourceProxy
                    .getLockStatus().equals(lockStatusLabel.getValue().toString().replace(resourceIs, ""))) {
                    repositories.item().changeLockStatus(item,
                        lockStatusLabel.getValue().toString().replace(resourceIs, "").toUpperCase(), comment);
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

                cssLayout.addListener(new ItemLayoutClickListener());
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