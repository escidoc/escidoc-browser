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
 * Copyright 2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui.listeners;

import com.google.common.base.Preconditions;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class TreeClickListener implements ItemClickListener {

    private static final boolean SHOULD_RELOAD_VIEW = true;

    private static final Logger LOG = LoggerFactory.getLogger(TreeClickListener.class);

    private final Router router;

    private final Window mainWindow;

    public TreeClickListener(final Window mainWindow, final Router router) {
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(router, "router is null: %s", router);

        this.mainWindow = mainWindow;
        this.router = router;
    }

    @Override
    public void itemClick(final ItemClickEvent event) {
        if (event.getButton() == ClickEvent.BUTTON_RIGHT || !(event.getItemId() instanceof ResourceModel)) {
            return;
        }
        openInNewTab((ResourceModel) event.getItemId());
    }

    private void openInNewTab(final ResourceModel clickedResource) {
        try {
            router.show(clickedResource, SHOULD_RELOAD_VIEW);
        }
        catch (final EscidocClientException e) {
            LOG.error(e.getMessage(), e);
            showErrorMessageToUser(e);
        }
    }

    private void showErrorMessageToUser(final EscidocClientException e) {
        mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
            Notification.TYPE_ERROR_MESSAGE));
    }
}