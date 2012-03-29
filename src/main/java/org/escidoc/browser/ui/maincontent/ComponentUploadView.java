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
package org.escidoc.browser.ui.maincontent;

import com.google.common.base.Preconditions;

import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.escidoc.browser.AppConstants;
import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.internal.ComponentBuilder;
import org.escidoc.browser.model.internal.ItemProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.ViewConstants;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.StorageType;
import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.om.item.component.Components;

@SuppressWarnings("serial")
public class ComponentUploadView extends VerticalLayout {

    private final OnUploadReceive uploadReceiver = new OnUploadReceive();

    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private Upload upload = new Upload(AppConstants.EMPTY_STRING, uploadReceiver);

    private final class onUploadSucceed implements SucceededListener {

        private Components componentList = new Components();

        private final Repositories repositories;

        private final OnUploadReceive uploadReceiver;

        private final Controller controller;

        private ResourceModel itemProxy;

        private ItemContent componentListView;

        private Window mainWindow;

        private onUploadSucceed(Repositories repositories, OnUploadReceive uploadReceiver, Controller controller,
            ResourceModel itemProxy, ItemContent vlDirectMember, Window mainWindow) {

            this.repositories = repositories;
            this.uploadReceiver = uploadReceiver;
            this.controller = controller;
            this.itemProxy = itemProxy;
            this.componentListView = vlDirectMember;
            this.mainWindow = mainWindow;
        }

        @Override
        public void uploadSucceeded(@SuppressWarnings("unused") SucceededEvent event) {
            try {
                URL contentUrl = putInStagingServer();
                if (contentUrl == null) {
                    return;
                }

                addToComponentList(contentUrl);
                componentListView.updateView(new ItemProxyImpl(updateItem(addFiles(findItem()))));
                mainWindow.showNotification(new Notification("Item is updated",
                    Window.Notification.TYPE_TRAY_NOTIFICATION));
            }
            catch (EscidocClientException e) {
                controller.showError(e);
            }
            catch (ParserConfigurationException e) {
                controller.showError(e);
            }
        }

        private URL putInStagingServer() throws EscidocClientException {
            return repositories.staging().putFileInStagingServer(new ByteArrayInputStream(outputStream.toByteArray()));
        }

        private void addToComponentList(final URL contentUrl) throws ParserConfigurationException {
            componentList.add(buildNewComponent(contentUrl));
        }

        private Component buildNewComponent(final URL contentUrl) throws ParserConfigurationException {
            return new ComponentBuilder(uploadReceiver.getFileName(), StorageType.INTERNAL_MANAGED)
                .withMimeType(uploadReceiver.getMimeType()).withContentUrl(contentUrl).build();
        }

        private Item updateItem(final Item toBeUpdate) throws EscidocClientException {
            return repositories.item().update(itemProxy.getId(), toBeUpdate);
        }

        private Item findItem() throws EscidocClientException {
            return repositories.item().findItemById(itemProxy.getId());
        }

        private Item addFiles(final Item toBeUpdate) {
            final Components components = toBeUpdate.getComponents();
            components.addAll(componentList);
            return toBeUpdate;
        }
    }

    private final class OnUploadReceive implements Receiver {

        private String fileName;

        private String mimeType;

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            Preconditions.checkNotNull(filename, "filename is null: %s", filename);
            Preconditions.checkNotNull(mimeType, "mimeType is null: %s", mimeType);
            this.fileName = filename;
            this.mimeType = mimeType;

            return outputStream;
        }

        public String getFileName() {
            return fileName;
        }

        public String getMimeType() {
            return mimeType;
        }
    }

    public ComponentUploadView(final Repositories repositories, final Controller controller, ResourceModel itemProxy,
        ItemContent vlDirectMember, Window mainWindow) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(controller, "controller is null: %s", controller);
        Preconditions.checkNotNull(itemProxy, "itemProxy is null: %s", itemProxy);
        Preconditions.checkNotNull(vlDirectMember, "componentListView is null: %s", vlDirectMember);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);

        upload.setButtonCaption(ViewConstants.SAVE);
        upload.setStyleName(Reindeer.BUTTON_SMALL);
        addListener(repositories, controller, itemProxy, vlDirectMember, mainWindow);
        setMargin(true);
        addComponent(upload);
    }

    private void addListener(
        final Repositories repositories, final Controller controller, ResourceModel itemProxy,
        ItemContent vlDirectMember, Window mainWindow) {
        upload.addListener(new onUploadSucceed(repositories, uploadReceiver, controller, itemProxy, vlDirectMember,
            mainWindow));
    }
}