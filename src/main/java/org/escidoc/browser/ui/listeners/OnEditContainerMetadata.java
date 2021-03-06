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

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import org.escidoc.browser.controller.ContainerController;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.view.helpers.OrgUnitMetadataTable.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;

@SuppressWarnings("serial")
public class OnEditContainerMetadata {

    private static final Logger LOG = LoggerFactory.getLogger(OnEditContainerMetadata.class);

    private MetadataFileReceiver receiver;

    private final HorizontalLayout progressLayout = new HorizontalLayout();

    private Upload upload;

    private final ProgressIndicator pi = new ProgressIndicator();

    private final Window mainWindow;

    private final Metadata md;

    private Element metadataContent;

    private ContainerController controller;

    private Label message;

    private HorizontalLayout buttonLayout;

    private Router router;

    public OnEditContainerMetadata(final Router router, ContainerController containerController, Metadata md) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(containerController, "containerController is null: %s", containerController);
        Preconditions.checkNotNull(md, "target is null: %s", md);

        this.router = router;
        this.controller = containerController;
        this.md = md;

        this.mainWindow = router.getMainWindow();
    }

    public void showEditWindow() {
        final Window modalWindow = buildModalWindow();
        message = new Label(ViewConstants.EDIT_METADATA_UPLOAD_MESSAGE);

        buildUploadComponent();
        buildSaveAndCancelButtons(modalWindow);

        modalWindow.addComponent(message);
        modalWindow.addComponent(upload);
        modalWindow.addComponent(new Label("OR"));
        modalWindow.addComponent(buildMdUpdateLink(modalWindow));
        modalWindow.addComponent(progressLayout);
        modalWindow.addComponent(buttonLayout);

        mainWindow.addWindow(modalWindow);
    }

    private Component buildMdUpdateLink(final Window modalWindow) {
        Button mdUpdateLink = new Button(ViewConstants.OPEN_METADATA_IN_EDITOR, new Button.ClickListener() {

            @Override
            public void buttonClick(@SuppressWarnings("unused") ClickEvent event) {
                closeModalWindow(modalWindow);
                openMetadataEditor();
            }

            private void openMetadataEditor() {
                mainWindow.open(new ExternalResource(buildMdUpdateUri(getAppBaseUri()), "_blank"));
            }

            private String getAppBaseUri() {
                StringBuilder builder = new StringBuilder();
                // @formatter:off
                builder
                  .append(getAppUrl().getProtocol())
                  .append("://")
                  .append(getAppUrl().getAuthority());
               // @formatter:on
                return builder.toString();
            }

            private URL getAppUrl() {
                return mainWindow.getApplication().getURL();
            }

            private void closeModalWindow(final Window modalWindow) {
                mainWindow.removeWindow(modalWindow);
            }
        });
        mdUpdateLink.setStyleName(BaseTheme.BUTTON_LINK);
        return mdUpdateLink;
    }

    private String buildMdUpdateUri(String baseUri) {
        StringBuilder builder = new StringBuilder();
        builder.append(baseUri).append("/rest/v0.9/").append("containers/");
        builder.append(Metadata.getId(md));
        builder.append("/metadata/");
        builder.append(md.name);
        builder.append("?escidocurl=");
        builder.append(router.getServiceLocation().getEscidocUri());
        String mdUpdateUri = builder.toString();
        return mdUpdateUri;
    }

    private void buildSaveAndCancelButtons(Window modalWindow) {
        final Button saveBtn = buildSaveButton(modalWindow);
        final Button cancelBtn = buildCancelButton(modalWindow);

        buttonLayout = new HorizontalLayout();
        buttonLayout.setVisible(false);

        buttonLayout.addComponent(saveBtn);
        buttonLayout.addComponent(cancelBtn);
    }

    private Button buildSaveButton(final Window modalWindow) {
        final Button saveBtn = new Button(ViewConstants.SAVE, new Button.ClickListener() {

            private MetadataRecord metadata;

            @Override
            public void buttonClick(@SuppressWarnings("unused") final ClickEvent event) {
                try {
                    metadata = controller.getMetadata(md.name);
                    metadata.setContent(metadataContent);
                    controller.updateMetadata(metadata);
                    controller.refreshView();
                    message.setValue("");
                    upload.setEnabled(true);
                }
                catch (final EscidocClientException e) {
                    LOG.error(e.getMessage());
                    mainWindow.showNotification(e.getMessage());
                }
                modalWindow.getParent().removeWindow(modalWindow);
            }
        });
        return saveBtn;
    }

    private static Button buildCancelButton(final Window modalWindow) {
        final Button cancelBtn = new Button(ViewConstants.CANCEL, new Button.ClickListener() {

            @Override
            public void buttonClick(@SuppressWarnings("unused") final ClickEvent event) {
                modalWindow.getParent().removeWindow(modalWindow);
            }
        });
        return cancelBtn;
    }

    private void buildUploadComponent() {
        buildMetadataReceiver();
        buildUpload();
        buildProgressLayout();
    }

    private void buildUpload() {
        upload = new Upload("", receiver);
        upload.setImmediate(true);
        upload.setButtonCaption(ViewConstants.SELECT_FILE);
        addListeners();
    }

    private void addListeners() {
        upload.addListener(new Upload.StartedListener() {
            @Override
            public void uploadStarted(final StartedEvent event) {
                upload.setVisible(false);
                progressLayout.setVisible(true);
                pi.setValue(Float.valueOf(0f));
                pi.setPollingInterval(500);
                message.setValue("Uploading file \"" + event.getFilename() + "\"");
            }
        });

        upload.addListener(new Upload.SucceededListener() {
            @Override
            public void uploadSucceeded(final SucceededEvent event) {
                message.setValue("Uploading file \"" + event.getFilename() + "\" succeeded");
                if (isWellFormed(receiver.getFileContent())) {
                    message.setValue(ViewConstants.XML_IS_WELL_FORMED);
                    buttonLayout.setVisible(true);
                    upload.setEnabled(false);
                }
                else {
                    message.setValue(ViewConstants.XML_IS_NOT_WELL_FORMED);
                    receiver.clearBuffer();
                }
            }
        });

        upload.addListener(new Upload.FailedListener() {
            @Override
            public void uploadFailed(@SuppressWarnings("unused") final FailedEvent event) {
                message.setValue("Uploading interrupted");
            }
        });

        upload.addListener(new Upload.FinishedListener() {
            @Override
            public void uploadFinished(@SuppressWarnings("unused") final FinishedEvent event) {
                progressLayout.setVisible(false);
                upload.setVisible(true);
                upload.setCaption("Select another file");
            }
        });
    }

    private void buildProgressLayout() {
        progressLayout.setSpacing(true);
        progressLayout.setVisible(false);
        progressLayout.addComponent(pi);
        progressLayout.setComponentAlignment(pi, Alignment.MIDDLE_LEFT);
    }

    private void buildMetadataReceiver() {
        receiver = new MetadataFileReceiver();
        receiver.clearBuffer();
    }

    private static Window buildModalWindow() {
        final Window subwindow = new Window(ViewConstants.EDIT_METADATA);
        subwindow.setWidth("600px");
        subwindow.setModal(true);
        return subwindow;
    }

    /**
     * checking if the uploaded file contains a valid XML string
     * 
     * @param xml
     * @return boolean
     */
    private boolean isWellFormed(final String xml) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
            final InputSource is = new InputSource(new StringReader(xml));
            Document d;
            try {
                d = builder.parse(is);
                metadataContent = d.getDocumentElement();
                return true;
            }
            catch (final SAXException e) {
                return false;
            }
            catch (final IOException e) {
                return false;
            }
        }
        catch (final ParserConfigurationException e) {
            return false;
        }
    }

}