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

import java.io.File;
import java.net.URISyntaxException;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.dnd.DragAndDropFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.om.item.component.Component;

@SuppressWarnings("serial")
public class ItemContent extends Panel {

    private static final String SRC_MAIN_WEBAPP_VAADIN_THEMES_MY_THEME_IMAGES_FILETYPES = "src/main/webapp/VAADIN/themes/myTheme/images/filetypes/";

    private static final Logger LOG = LoggerFactory.getLogger(ItemContent.class);

    private final EscidocServiceLocation serviceLocation;

    private final Repositories repositories;

    private final CurrentUser currentUser;

    private final Window mainWindow;

    private ItemProxyImpl itemProxy;

    private Table table;

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

    private final VerticalLayout verticalLayout = new VerticalLayout();

    private void initView() {
        getLayout().setMargin(false);
        setScrollable(false);
        this.setHeight("100%");
        wrap(verticalLayout);
        if (hasComponents()) {
            verticalLayout.addComponent(buildTable());
        }
    }

    private void wrap(final VerticalLayout verticalLayout) {
        try {
            if (userIsPermittedToUpdate()) {
                verticalLayout.setHeight("750px");
                verticalLayout.setWidth("100%");
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
            .pdp().isAction(ActionIdConstants.UPDATE_ITEM).forUser(currentUser.getUserId())
            .forResource(itemProxy.getId()).permitted();
    }

    private void showError(final Exception e) {
        mainWindow.showNotification(new Window.Notification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE));
    }

    private boolean hasComponents() {
        return itemProxy.hasComponents().booleanValue();
    }

    private Button createDownloadLink(final Component comp) {
        final Button link = new Button();
        link.setStyleName(BaseTheme.BUTTON_LINK);
        link.setIcon(new ThemeResource("images/download.png"));
        link.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(final ClickEvent event) {
                mainWindow.open(new ExternalResource(
                    serviceLocation.getEscidocUri() + comp.getContent().getXLinkHref(), comp
                        .getProperties().getMimeType()));
            }
        });
        return link;
    }

    private Label createLabelForMetadata(final Component comp) {
        final Label labelMetadata =
            new Label(comp.getContent().getXLinkTitle() + "<br />" + ViewConstants.CREATED_ON
                + comp.getProperties().getCreationDate().toString("d.M.y, H:mm") + "<br /> by "
                + comp.getProperties().getCreatedBy().getXLinkTitle() + "<br /> Mime Type: "
                + comp.getProperties().getMimeType() + "<br />", Label.CONTENT_RAW);
        labelMetadata.setStyleName("smallfont");
        return labelMetadata;
    }

    private Embedded createEmbeddedImage(final Component comp) {
        String currentDir = new File(".").getAbsolutePath();
        File file =
            new File(currentDir.substring(0, currentDir.length() - 1)
                + SRC_MAIN_WEBAPP_VAADIN_THEMES_MY_THEME_IMAGES_FILETYPES + getFileType(comp) + ".png");
        boolean exists = file.exists();
        if (exists) {
            return new Embedded("", new ThemeResource("images/filetypes/" + getFileType(comp) + ".png"));
        }
        return new Embedded("", new ThemeResource("images/filetypes/article.png"));
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

    private Table buildTable() {
        table = new Table();
        table.setPageLength(0);
        table.setWidth("100%");
        table.addContainerProperty("Type", Embedded.class, null);
        table.addContainerProperty("Meta", Label.class, null);
        table.addContainerProperty("Link", Button.class, null);
        int rowIndex = 0;
        for (final Component comp : itemProxy.getElements()) {
            table.addItem(new Object[] { createEmbeddedImage(comp), createLabelForMetadata(comp),
                createDownloadLink(comp) }, Integer.valueOf(rowIndex++));
        }
        table.setColumnWidth("Type", 20);
        table.setColumnWidth("Link", 20);
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