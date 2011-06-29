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

import org.escidoc.browser.ActionIdConstants;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.ui.dnd.DragAndDropFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.om.item.component.Component;

@SuppressWarnings("serial")
public class ItemContent extends CustomLayout {

    private static final Logger LOG = LoggerFactory.getLogger(ItemContent.class);

    private static final String ITEM_TEMPLATE_NAME = "itemtemplate";

    private final Panel panelComponent = new Panel();

    private final EscidocServiceLocation serviceLocation;

    private final Repositories repositories;

    private final CurrentUser currentUser;

    private final Window mainWindow;

    private ItemProxyImpl itemProxy;

    public ItemContent(final Repositories repositories, final ItemProxyImpl itemProxy,
        final EscidocServiceLocation serviceLocation, final Window mainWindow, final CurrentUser currentUser) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(itemProxy, "resourceProxy is null.");
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null.");
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);

        this.repositories = repositories;
        this.itemProxy = itemProxy;
        this.serviceLocation = serviceLocation;
        this.currentUser = currentUser;
        this.mainWindow = mainWindow;
        initView();
    }

    private void initView() {
        setTemplateName(ITEM_TEMPLATE_NAME);
        buildComponentPanel();
        addDragAndDropFiles();
        if (hasComponents()) {
            buildComponents();
        }
        addComponent(panelComponent, "components");
    }

    private void buildComponentPanel() {
        panelComponent.addStyleName(Runo.PANEL_LIGHT);
    }

    private void addDragAndDropFiles() {
        try {
            if (userIsPermittedToUpdate()) {
                final DragAndDropFileUpload dragAndDropFileUpload =
                    new DragAndDropFileUpload(repositories, itemProxy, this);
                panelComponent.addComponent(dragAndDropFileUpload);
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
            .pdp().isAction(ActionIdConstants.UPDATE_ITEM).forUser(currentUser.getUserId())
            .forResource(itemProxy.getId()).permitted();
    }

    private void showError(final Exception e) {
        mainWindow.showNotification(new Window.Notification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE));
    }

    private boolean hasComponents() {
        return itemProxy.hasComponents().booleanValue();
    }

    private void buildComponents() {
        for (final Component component : itemProxy.getElements()) {
            buildComponentElement(component);
        }
    }

    private void buildComponentElement(final Component comp) {
        panelComponent.addComponent(createEmbeddedImage(comp));
        panelComponent.addComponent(createDownloadLink(comp));
        panelComponent.addComponent(createLabelForMetadata(comp));
    }

    private Link createDownloadLink(final Component comp) {
        final Link link =
            new Link("Download File", new ExternalResource(serviceLocation.getEscidocUri()
                + comp.getContent().getXLinkHref()));
        link.setIcon(new ThemeResource("images/download.png"));
        return link;
    }

    private Label createLabelForMetadata(final Component comp) {
        final Label labelMetadata =
            new Label(comp.getContent().getXLinkTitle() + "<br />" + comp.getProperties().getVisibility() + "<br />"
                + comp.getProperties().getChecksumAlgorithm() + " " + comp.getProperties().getChecksum(),
                Label.CONTENT_RAW);
        return labelMetadata;
    }

    private Embedded createEmbeddedImage(final Component comp) {
        return new Embedded("", new ThemeResource("images/filetypes/" + getFileType(comp) + ".png"));
    }

    private String getFileType(final Component itemProperties) {
        final String mimeType = itemProperties.getProperties().getMimeType();
        final String[] last = mimeType.split("/");
        final String lastOne = last[last.length - 1];
        return lastOne;
    }

    public void updateView(final ItemProxyImpl itemProxy) {
        this.itemProxy = itemProxy;
        panelComponent.removeAllComponents();
        addDragAndDropFiles();
        if (hasComponents()) {
            buildComponents();
        }
    }
}