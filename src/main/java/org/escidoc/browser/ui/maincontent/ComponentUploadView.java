package org.escidoc.browser.ui.maincontent;

import com.google.common.base.Preconditions;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.model.internal.ItemProxyImpl;
import org.escidoc.browser.repository.Repositories;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.om.item.Item;

@SuppressWarnings("serial")
public class ComponentUploadView extends CustomComponent {

    private final StringBuffer filecontent = new StringBuffer();

    private Upload upload;

    private final class onReceiveUpload implements Receiver {

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            Preconditions.checkNotNull(filename, "filename is null: %s", filename);
            Preconditions.checkNotNull(mimeType, "mimeType is null: %s", mimeType);

            return new OutputStream() {

                @Override
                public void write(final int b) throws IOException {
                    filecontent.append((char) b);
                }
            };

        }
    }

    public ComponentUploadView(final Repositories repositories, final Controller controller) {
        upload = new Upload("Caption", new onReceiveUpload());
        upload.addListener(new SucceededListener() {

            @Override
            public void uploadSucceeded(@SuppressWarnings("unused") SucceededEvent event) {
                try {
                    URL contentUrl = putInStagingServer();
                    if (contentUrl == null) {
                        return;
                    }

                    addToComponentList(contentUrl);
                    final Item toBeUpdate = findWholeItem();
                    final Item itemWithNewFiles = addFiles(toBeUpdate);
                    final Item updatedItem = updateItem(itemWithNewFiles);

                    mainWindow.showNotification(new Notification("Item is updated",
                        Window.Notification.TYPE_TRAY_NOTIFICATION));
                    componentListView.updateView(new ItemProxyImpl(updatedItem));
                }
                catch (EscidocClientException e) {
                    controller.showError(e);
                }
            }

            private URL putInStagingServer() throws EscidocClientException {
                return repositories.staging().putFileInStagingServer(
                    new ByteArrayInputStream(filecontent.toString().getBytes()));
            }
        });
        upload.setButtonCaption("FOO");
        setCompositionRoot(upload);
    }
}