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
package org.escidoc.browser.ui.dnd;

import com.google.common.base.Preconditions;

import com.vaadin.terminal.StreamVariable;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.internal.ComponentBuilder;
import org.escidoc.browser.model.internal.ItemProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.maincontent.ItemContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.StorageType;
import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.om.item.component.Components;

@SuppressWarnings("serial")
class MultipleStreamVariable implements StreamVariable {

    private static final Logger LOG = LoggerFactory.getLogger(MultipleStreamVariable.class);

    private final AbstractComponent progressView;

    private final Window mainWindow;

    private final Components componentList;

    private final FilesDropBox itemDropBox;

    private final Html5File html5File;

    private ByteArrayOutputStream outputStream;

    private final Repositories repositories;

    private final ItemProxy itemProxy;

    private final ItemContent componentListView;

    MultipleStreamVariable(final ProgressIndicator progressView, final Window mainWindow, final Html5File html5File,
        final Components componentList, final FilesDropBox itemDropBox, final Repositories repositories,
        final ItemProxy itemProxy, final ItemContent componentListView) {

        Preconditions.checkNotNull(progressView, "progressView is null: %s", progressView);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(html5File, "html5File is null: %s", html5File);
        Preconditions.checkNotNull(componentList, "componentList is null: %s", componentList);
        Preconditions.checkNotNull(itemDropBox, "itemDropBox is null: %s", itemDropBox);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(componentListView, "componentListView is null: %s", componentListView);

        this.progressView = progressView;
        this.mainWindow = mainWindow;
        this.html5File = html5File;
        this.itemDropBox = itemDropBox;
        this.componentList = componentList;
        this.repositories = repositories;
        this.itemProxy = itemProxy;
        this.componentListView = componentListView;
    }

    @Override
    public OutputStream getOutputStream() {
        outputStream = new ByteArrayOutputStream();
        return outputStream;
    }

    @Override
    public boolean listenProgress() {
        return true;
    }

    @Override
    public void onProgress(final StreamingProgressEvent event) {
        progressView.setVisible(true);
        final double percentage = ((double) event.getBytesReceived() / event.getContentLength()) * 100;
        LOG.debug("onProgress Streaming, got: " + event.getBytesReceived() + "[" + percentage + "%]");
    }

    @Override
    public void streamingStarted(final StreamingStartEvent event) {
        LOG.debug("start streaming file with the name: " + event.getFileName());
    }

    @Override
    public void streamingFinished(final StreamingEndEvent event) {
        LOG.debug("finish streaming, got: " + event.getBytesReceived());
        try {
            onStreamFinished();
        }
        catch (final EscidocClientException e) {
            mainWindow.showNotification(new Notification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE));
        }
    }

    private void onStreamFinished() throws EscidocClientException {
        progressView.setVisible(false);

        final URL contentUrl = putInStagingServer();

        if (contentUrl == null) {
            return;
        }

        try {
            addToComponentList(contentUrl);
            decrementNumberOfProcessFile();
            if (areAllFilesStreamed()) {
                try {
                    final Item toBeUpdate = findWholeItem();
                    final Item itemWithNewFiles = addFiles(toBeUpdate);
                    final Item updatedItem = updateItem(itemWithNewFiles);
                    LOG.debug("updated: " + updatedItem.getObjid());

                    mainWindow.showNotification(new Notification("Item is updated",
                        Window.Notification.TYPE_TRAY_NOTIFICATION));
                    componentListView.updateView(new ItemProxyImpl(updatedItem));
                }
                catch (final EscidocClientException e) {
                    mainWindow
                        .showNotification(new Notification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE));
                }
            }
        }
        catch (final ParserConfigurationException e) {
            mainWindow.showNotification(new Notification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE));
        }

    }

    private void decrementNumberOfProcessFile() {
        itemDropBox.decrementNumberOfFiles();
    }

    private void addToComponentList(final URL contentUrl) throws ParserConfigurationException {
        componentList.add(buildNewComponent(contentUrl));
    }

    private Item updateItem(final Item toBeUpdate) throws EscidocClientException {
        return repositories.item().update(itemProxy.getId(), toBeUpdate);
    }

    private Item findWholeItem() throws EscidocClientException {
        return repositories.item().findItemById(itemProxy.getId());
    }

    private Item addFiles(final Item toBeUpdate) {
        final Components components = toBeUpdate.getComponents();
        components.addAll(componentList);
        return toBeUpdate;
    }

    private boolean areAllFilesStreamed() {
        return itemDropBox.getNumberOfFiles() == 0;
    }

    private Component buildNewComponent(final URL contentUrl) throws ParserConfigurationException {
        return new ComponentBuilder(html5File.getFileName(), StorageType.INTERNAL_MANAGED)
            .withMimeType(html5File.getType()).withContentUrl(contentUrl).build();
    }

    private URL putInStagingServer() throws EscidocClientException {
        return repositories.staging().putFileInStagingServer(new ByteArrayInputStream(outputStream.toByteArray()));
    }

    @Override
    public void streamingFailed(final StreamingErrorEvent event) {
        LOG.error("Failed Streaming: " + event.getException());
        progressView.setVisible(false);
        mainWindow.showNotification(new Notification(event.getException().getMessage(),
            Window.Notification.TYPE_ERROR_MESSAGE));
    }

    @Override
    public boolean isInterrupted() {
        return false;
    }
}