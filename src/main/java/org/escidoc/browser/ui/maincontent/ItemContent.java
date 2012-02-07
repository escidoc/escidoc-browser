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

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.controller.ItemController;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.internal.ItemProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.dnd.DragAndDropFileUpload;
import org.escidoc.browser.ui.view.helpers.ItemComponentsView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.om.item.component.Component;

@SuppressWarnings("serial")
public class ItemContent extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(ItemContent.class);

    private final VerticalLayout verticalLayout = new VerticalLayout();

    private final EscidocServiceLocation serviceLocation;

    private final Repositories repositories;

    private final Window mainWindow;

    private ItemProxy itemProxy;

    private ItemComponentsView table;

    private ItemController controller;

    public ItemContent(final Repositories repositories, final ItemProxyImpl itemProxy,
        final EscidocServiceLocation serviceLocation, final Window mainWindow, ItemController controller) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(itemProxy, "resourceProxy is null.");
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null.");
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(controller, "controller is null: %s", controller);

        this.repositories = repositories;
        this.itemProxy = itemProxy;
        this.serviceLocation = serviceLocation;
        this.mainWindow = mainWindow;
        this.controller = controller;
        initView();

    }

    private void initView() {
        verticalLayout.addStyleName("drophere");
        wrap(verticalLayout);
        if (hasComponents()) {
            verticalLayout.addComponent(buildTable());
        }
        else {
            final Label lblNoComponents =
                new Label(
                    "No components in this Item. You can drag n'drop some file from your computer to this box to add new components!");
            lblNoComponents.setWidth("90%");
            lblNoComponents.setStyleName("skybluetext");
            verticalLayout.addComponent(lblNoComponents);
        }
    }

    private void wrap(final VerticalLayout verticalLayout) {
        try {
            if (userIsPermittedToUpdate()) {
                verticalLayout.setHeight(mainWindow.getHeight() * 65 / 100 + "px");
                verticalLayout.setWidth("99%");

                final DragAndDropFileUpload dragAndDropFileUpload =
                    new DragAndDropFileUpload(repositories, itemProxy, this, verticalLayout);
                dragAndDropFileUpload.setSizeFull();
                addComponent(dragAndDropFileUpload);
            }
        }
        catch (final EscidocClientException e) {
            LOG.error(e.getMessage());
            showError(e);
        }
        catch (final URISyntaxException e) {
            LOG.error(e.getMessage());
            showError(e);
        }
    }

    private boolean userIsPermittedToUpdate() throws EscidocClientException, URISyntaxException {
        return repositories
            .pdp().isAction(ActionIdConstants.UPDATE_ITEM).forCurrentUser().forResource(itemProxy.getId()).permitted();
    }

    private void showError(final Exception e) {
        mainWindow.showNotification(new Window.Notification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE));
    }

    private boolean hasComponents() {
        return itemProxy.hasComponents().booleanValue();
    }

    private String getFileType(final Component itemProperties) {
        final String mimeType = itemProperties.getProperties().getMimeType();
        if (mimeType == null) {
            return AppConstants.EMPTY_STRING;
        }
        final String[] last = mimeType.split("/");
        final String lastOne = last[last.length - 1];
        return lastOne;
    }

    private ItemComponentsView buildTable() {
        table = new ItemComponentsView(itemProxy.getElements(), controller, serviceLocation, mainWindow);
        return table;
    }

    public void updateView(final ItemProxyImpl itemProxy) {
        this.itemProxy = itemProxy;
        if (hasComponents()) {
            rebuildFilesTable();
        }
    }

    private void rebuildFilesTable() {
        verticalLayout.removeAllComponents();
        verticalLayout.addComponent(buildTable());
    }
}