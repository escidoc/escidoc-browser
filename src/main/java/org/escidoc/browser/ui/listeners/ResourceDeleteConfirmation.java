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
package org.escidoc.browser.ui.listeners;

import org.escidoc.browser.repository.internal.ContainerRepository;
import org.escidoc.browser.repository.internal.ItemRepository;
import org.escidoc.browser.ui.ViewConstants;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.om.container.Container;
import de.escidoc.core.resources.om.item.Item;

public class ResourceDeleteConfirmation {

    public ResourceDeleteConfirmation(Item item, ItemRepository itemRepository, Window mainWindow) {
        try {
            delete(item, itemRepository, mainWindow);
            mainWindow.showNotification(new Window.Notification(ViewConstants.DELETED,
                Notification.TYPE_TRAY_NOTIFICATION));
        }
        catch (EscidocClientException e) {
            if (e.getMessage().toString().contains("An error occured removing member entries for container")) {
                mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR,
                    ViewConstants.ERR_BELONGS_TO_NONDELETABLE_PARENT, Notification.TYPE_ERROR_MESSAGE));
            }
            else {
                mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE));
            }
        }
    }

    public ResourceDeleteConfirmation(Container container, ContainerRepository containerRepository, Window mainWindow) {
        delete(container, containerRepository, mainWindow);
    }

    private void delete(
        final Container container, final ContainerRepository containerRepository, final Window mainWindow) {
        final Window subwindow = new Window(ViewConstants.DELETE_RESOURCE_WND_NAME);
        subwindow.setModal(true);
        final Label message = new Label(ViewConstants.QUESTION_DELETE_RESOURCE);
        subwindow.addComponent(message);

        @SuppressWarnings("serial")
        final Button okConfirmed = new Button("Yes", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
                try {
                    containerRepository.finalDelete(container);
                    mainWindow.showNotification(new Window.Notification(ViewConstants.DELETED,
                        Notification.TYPE_TRAY_NOTIFICATION));
                }
                catch (final EscidocClientException e) {
                    mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                        Notification.TYPE_ERROR_MESSAGE));
                }
            }

        });
        @SuppressWarnings("serial")
        final Button cancel = new Button("Cancel", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        final HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(okConfirmed);
        hl.addComponent(cancel);
        subwindow.addComponent(hl);
        mainWindow.addWindow(subwindow);
    }

    private void delete(final Item item, final ItemRepository itemRepository, final Window mainWindow)
        throws EscidocClientException {
        final Window subwindow = new Window(ViewConstants.DELETE_RESOURCE_WND_NAME);
        subwindow.setModal(true);
        Label message = new Label(ViewConstants.QUESTION_DELETE_RESOURCE);
        subwindow.addComponent(message);

        @SuppressWarnings("serial")
        Button okConfirmed = new Button("Yes", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
                try {
                    itemRepository.finalDelete(item);
                    mainWindow.showNotification(new Window.Notification(ViewConstants.DELETED,
                        Notification.TYPE_TRAY_NOTIFICATION));
                }
                catch (EscidocClientException e) {
                    mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                        Notification.TYPE_ERROR_MESSAGE));
                }
            }

        });
        @SuppressWarnings("serial")
        Button cancel = new Button("Cancel", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(okConfirmed);
        hl.addComponent(cancel);
        subwindow.addComponent(hl);
        mainWindow.addWindow(subwindow);
    }
}

// catch (EscidocClientException e) {

// }
