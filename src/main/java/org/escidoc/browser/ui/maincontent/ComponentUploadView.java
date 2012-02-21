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
            ResourceModel itemProxy, ItemContent componentListView, Window mainWindow) {

            this.repositories = repositories;
            this.uploadReceiver = uploadReceiver;
            this.controller = controller;
            this.itemProxy = itemProxy;
            this.componentListView = componentListView;
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
        ItemContent componentListView, Window mainWindow) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(controller, "controller is null: %s", controller);
        Preconditions.checkNotNull(itemProxy, "itemProxy is null: %s", itemProxy);
        Preconditions.checkNotNull(componentListView, "componentListView is null: %s", componentListView);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);

        upload.setButtonCaption(ViewConstants.SAVE);
        upload.setStyleName(Reindeer.BUTTON_SMALL);
        addListener(repositories, controller, itemProxy, componentListView, mainWindow);
        setMargin(true);
        addComponent(upload);
    }

    private void addListener(
        final Repositories repositories, final Controller controller, ResourceModel itemProxy,
        ItemContent componentListView, Window mainWindow) {
        upload.addListener(new onUploadSucceed(repositories, uploadReceiver, controller, itemProxy, componentListView,
            mainWindow));
    }
}