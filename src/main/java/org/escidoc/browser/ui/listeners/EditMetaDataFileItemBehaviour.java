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

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.item.Item;

@SuppressWarnings("serial")
public class EditMetaDataFileItemBehaviour implements ClickListener {

    private static final Logger LOG = LoggerFactory.getLogger(EditMetaDataFileItemBehaviour.class);

    private MetadataFileReceiver receiver;

    private final HorizontalLayout progressLayout = new HorizontalLayout();

    private Upload upload;

    private Label status;

    private final ProgressIndicator progressIndicator = new ProgressIndicator();

    private final MetadataRecord metadataRecord;

    private final Window mainWindow;

    private final Repositories repositories;

    private HorizontalLayout horizontalLayout;

    private final ResourceProxy resourceProxy;

    private Element metadataContent;

    public EditMetaDataFileItemBehaviour(final MetadataRecord metadataRecord, final Window mainWindow,
        final Repositories repositories, final ResourceProxy resourceProxy) {
        Preconditions.checkNotNull(metadataRecord, "metadataRecord is null: %s", metadataRecord);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        this.metadataRecord = metadataRecord;
        this.mainWindow = mainWindow;
        this.repositories = repositories;
        this.resourceProxy = resourceProxy;

    }

    @Override
    public void buttonClick(final ClickEvent event) {
        showWindow();
    }

    public void showWindow() {
        final Window subwindow = new Window(ViewConstants.EDIT_METADATA);
        subwindow.setWidth("600px");
        subwindow.setModal(true);
        status = new Label(ViewConstants.UPLOAD_A_WELLFORMED_XML_FILE_TO_REPLACE_METADATA);

        // Make uploading start immediately when file is selected
        receiver = new MetadataFileReceiver();
        receiver.clearBuffer();

        upload = new Upload("", receiver);
        upload.setImmediate(true);
        upload.setButtonCaption("Select file");
        upload.setEnabled(true);
        progressLayout.setSpacing(true);
        progressLayout.setVisible(false);
        progressLayout.addComponent(progressIndicator);
        progressLayout.setComponentAlignment(progressIndicator, Alignment.MIDDLE_LEFT);

        /**
         * =========== Add needed listener for the upload component: start, progress, finish, success, fail ===========
         */

        upload.addListener(new Upload.StartedListener() {
            @Override
            public void uploadStarted(final StartedEvent event) {
                upload.setVisible(false);
                progressLayout.setVisible(true);
                progressIndicator.setValue(Float.valueOf(0.5f));
                progressIndicator.setPollingInterval(500);
                status.setValue("Uploading file \"" + event.getFilename() + "\"");
            }
        });

        upload.addListener(new Upload.SucceededListener() {
            @Override
            public void uploadSucceeded(final SucceededEvent event) {
                // This method gets called when the upload finished successfully
                status.setValue("Uploading file \"" + event.getFilename() + "\" succeeded");
                if (isValidXml(receiver.getFileContent())) {
                    status.setValue(ViewConstants.XML_IS_WELL_FORMED);
                    horizontalLayout.setVisible(true);
                    upload.setEnabled(false);
                }
                else {
                    status.setValue(ViewConstants.XML_IS_NOT_WELL_FORMED);
                    receiver.clearBuffer();
                }
            }
        });

        upload.addListener(new Upload.FailedListener() {
            @Override
            public void uploadFailed(final FailedEvent event) {
                // This method gets called when the upload failed
                status.setValue("Uploading interrupted");
            }
        });

        upload.addListener(new Upload.FinishedListener() {
            @Override
            public void uploadFinished(final FinishedEvent event) {

                progressLayout.setVisible(false);
                upload.setVisible(true);
                upload.setCaption("Select another file");
            }
        });

        final Button btnAdd = new Button("Save", new Button.ClickListener() {
            Item item;

            @Override
            public void buttonClick(final ClickEvent event) {
                try {
                    item = repositories.item().findItemById(resourceProxy.getId());
                    metadataRecord.setContent(metadataContent);
                    repositories.item().updateMetaData(metadataRecord, item);
                    upload.setEnabled(true);
                }
                catch (final EscidocClientException e) {
                    LOG.debug(e.getLocalizedMessage());
                }
                subwindow.getParent().removeWindow(subwindow);
            }
        });

        final Button cnclAdd = new Button("Cancel", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        horizontalLayout = new HorizontalLayout();
        horizontalLayout.setVisible(false);
        horizontalLayout.addComponent(btnAdd);
        horizontalLayout.addComponent(cnclAdd);

        subwindow.addComponent(status);
        subwindow.addComponent(upload);
        subwindow.addComponent(progressLayout);
        subwindow.addComponent(horizontalLayout);
        mainWindow.addWindow(subwindow);
    }

    /**
     * checking if the uploaded file contains a valid XML string
     * 
     * @param xml
     * @return boolean
     */
    private boolean isValidXml(final String xml) {
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
                metadataContent = null;
                return false;
            }
            catch (final IOException e) {
                metadataContent = null;
                return false;
            }
        }
        catch (final ParserConfigurationException e) {
            metadataContent = null;
            return false;
        }
    }

}
