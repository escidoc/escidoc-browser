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

import org.escidoc.browser.controller.ItemController;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.internal.ItemProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.dnd.DragAndDropFileUpload;
import org.escidoc.browser.ui.view.helpers.ItemComponentsView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.client.exceptions.EscidocClientException;

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
        final CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight("20px");
        buildPanelHeader(cssLayout, ViewConstants.COMPONENTS);
        ThemeResource ICON = new ThemeResource("images/assets/plus.png");

        if (true) {
            final Button btnAddNew = new Button();
            btnAddNew.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                    Window modalWindow = new Window("Select a file to add.");
                    modalWindow.setWidth("25%");
                    modalWindow.setHeight("20%");
                    modalWindow.setModal(true);
                    modalWindow.addComponent(new ComponentUploadView(repositories, controller, itemProxy,
                        ItemContent.this, mainWindow));
                    mainWindow.addWindow(modalWindow);
                }
            });
            btnAddNew.setStyleName(BaseTheme.BUTTON_LINK);
            btnAddNew.addStyleName("floatright paddingtop3");
            btnAddNew.setWidth("20px");
            btnAddNew.setIcon(ICON);
            cssLayout.addComponent(btnAddNew);
        }

        verticalLayout.addComponent(cssLayout);
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

    private void buildPanelHeader(CssLayout cssLayout, String name) {
        cssLayout.addStyleName("v-accordion-item-caption v-caption v-captiontext");
        cssLayout.setWidth("100%");
        cssLayout.setMargin(false);

        final Label nameofPanel = new Label(name, Label.CONTENT_RAW);
        nameofPanel.setStyleName("accordion v-captiontext");
        nameofPanel.setWidth("70%");
        cssLayout.addComponent(nameofPanel);

    }

    private void wrap(final VerticalLayout verticalLayout) {
        try {
            if (userIsPermittedToUpdate()) {

                verticalLayout.setHeight("99%");
                // verticalLayout.setWidth("90%");

                final DragAndDropFileUpload dragAndDropFileUpload =
                    new DragAndDropFileUpload(repositories, itemProxy, this, verticalLayout);
                dragAndDropFileUpload.setSizeFull();

                addComponent(dragAndDropFileUpload);
                verticalLayout.getParent().setWidth("99%");
                verticalLayout.getParent().setHeight("99%");
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