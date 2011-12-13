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
package org.escidoc.browser.ui.tools;

import java.util.HashSet;
import java.util.Set;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.adm.AdminStatus;
import de.escidoc.core.resources.adm.MessagesStatus;

@SuppressWarnings("serial")
final class PurgeButtonListener implements ClickListener {

    private static final Logger LOG = LoggerFactory.getLogger(PurgeButtonListener.class);

    private final FilterButtonListener filterButtonListener;

    private final Window mainWindow;

    private final BeanItemContainer<ResourceModel> dataSource;

    PurgeButtonListener(final FilterButtonListener filterButtonListener, final Window mainWindow,
        final BeanItemContainer<ResourceModel> dataSource) {
        Preconditions.checkNotNull(filterButtonListener, "filterButtonListener is null: %s", filterButtonListener);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(dataSource, "dataSource is null: %s", dataSource);

        this.filterButtonListener = filterButtonListener;
        this.mainWindow = mainWindow;
        this.dataSource = dataSource;
    }

    @Override
    public void buttonClick(final ClickEvent event) {
        askForUserConfirmation();
    }

    private void askForUserConfirmation() {
        mainWindow.addWindow(buildModalDialog());
    }

    private Window buildModalDialog() {
        final Window subWindow = new Window(ViewConstants.WARNING);
        subWindow.setWidth("600px");
        subWindow.setModal(true);

        subWindow.addComponent(new Label(ViewConstants.PURGE_WARNING_MESSAGE, Label.CONTENT_XHTML));
        final HorizontalLayout buttonLayout = new HorizontalLayout();
        subWindow.addComponent(buttonLayout);
        buttonLayout.setSpacing(true);
        buttonLayout.addComponent(new Button(ViewConstants.YES, new ClickListener() {

            @Override
            public void buttonClick(final ClickEvent event) {
                closeDialog(subWindow);
                tryPurge(getSelectedResourceIds(filterButtonListener.getSelectedResources()));
            }
        }));

        buttonLayout.addComponent(new Button(ViewConstants.NO, new ClickListener() {

            @Override
            public void buttonClick(final ClickEvent event) {
                closeDialog(subWindow);
            }

        }));
        return subWindow;
    }

    private void closeDialog(final Window subWindow) {
        mainWindow.removeWindow(subWindow);
    }

    private Set<String> getSelectedResourceIds(final Set<ResourceModel> selectedResources) {
        final Set<String> objectIds = new HashSet<String>(selectedResources.size());
        for (final ResourceModel resource : selectedResources) {
            objectIds.add(resource.getId());
        }
        return objectIds;
    }

    private void tryPurge(final Set<String> objectIds) {
        try {
            showPurgeStatus(startPurging(objectIds));
        }
        catch (final EscidocClientException e) {
            LOG.error("Internal Server Error while purging resources. " + e);
            this.filterButtonListener.bulkTasksView.showErrorMessage(e);
        }
    }

    private void showPurgeStatus(final MessagesStatus status) throws EscidocClientException {
        if (status.getStatusCode() == AdminStatus.STATUS_INVALID_RESULT) {
            this.filterButtonListener.bulkTasksView.showErrorMessage(status);
        }
        if (status.getStatusCode() == AdminStatus.STATUS_FINISHED) {
            removeFromDataSource();
            showSucess(status);
        }
        else if (status.getStatusCode() == AdminStatus.STATUS_IN_PROGRESS) {
            showPurgeStatus(this.filterButtonListener.bulkTasksView.repositories
                .admin().retrievePurgeStatus());
        }
        else {
            this.filterButtonListener.bulkTasksView.showErrorMessage(status);
        }
    }

    private void removeFromDataSource() {
        for (final ResourceModel rm : this.filterButtonListener.getSelectedResources()) {
            dataSource.removeItem(rm);
        }

    }

    private void showSucess(final MessagesStatus status) {
        this.filterButtonListener.bulkTasksView.router.getMainWindow().showNotification(
            ViewConstants.INFO, status.getStatusMessage(), Notification.TYPE_TRAY_NOTIFICATION);
    }

    private MessagesStatus startPurging(final Set<String> objectIds) throws EscidocClientException {
        return this.filterButtonListener.bulkTasksView.repositories.admin().purge(objectIds);
    }
}