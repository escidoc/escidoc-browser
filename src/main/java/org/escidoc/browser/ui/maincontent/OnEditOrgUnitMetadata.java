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

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.escidoc.browser.controller.OrgUnitController;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.MetadataFileReceiver;
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

@SuppressWarnings("serial")
public class OnEditOrgUnitMetadata {

    private final static Logger LOG = LoggerFactory.getLogger(OnEditOrgUnitMetadata.class);

    private final HorizontalLayout progressLayout = new HorizontalLayout();

    private final ProgressIndicator pi = new ProgressIndicator();

    private final String metadataRecordName;

    private final Repositories repositories;

    private MetadataFileReceiver receiver;

    private Upload upload;

    private Label status;

    private Window mainWindow;

    private HorizontalLayout hl;

    private Element metadataContent;

    private OrgUnitController controller;

    public OnEditOrgUnitMetadata(String name, Router router, Repositories repositories, OrgUnitController controller) {
        Preconditions.checkNotNull(name, "metadataRecord is null: %s", name);
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);

        this.metadataRecordName = name;
        this.mainWindow = router.getMainWindow();
        this.repositories = repositories;

        this.controller = controller;
        showSubWindow();
    }

    public void showSubWindow() {
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

        progressLayout.setSpacing(true);
        progressLayout.setVisible(false);
        progressLayout.addComponent(pi);
        progressLayout.setComponentAlignment(pi, Alignment.MIDDLE_LEFT);

        /**
         * =========== Add needed listener for the upload component: start, progress, finish, success, fail ===========
         */

        upload.addListener(new Upload.StartedListener() {
            @Override
            public void uploadStarted(final StartedEvent event) {
                upload.setVisible(false);
                progressLayout.setVisible(true);
                pi.setValue(Float.valueOf(0f));
                pi.setPollingInterval(500);
                status.setValue("Uploading file \"" + event.getFilename() + "\"");
            }
        });

        upload.addListener(new Upload.SucceededListener() {
            @Override
            public void uploadSucceeded(final SucceededEvent event) {
                // This method gets called when the upload finished successfully
                status.setValue("Uploading file \"" + event.getFilename() + "\" succeeded");
                if (isWellFormed(receiver.getFileContent())) {
                    status.setValue(ViewConstants.XML_IS_WELL_FORMED);
                    hl.setVisible(true);
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
                // This method gets called always when the upload finished,
                // either succeeding or failing
                progressLayout.setVisible(false);
                upload.setVisible(true);
                upload.setCaption("Select another file");
            }
        });

        final Button saveBtn = new Button(ViewConstants.SAVE, new Button.ClickListener() {

            private MetadataRecord metadata;

            @Override
            public void buttonClick(final ClickEvent event) {
                try {
                    metadata = controller.getMetadata(metadataRecordName);
                    metadata.setContent(metadataContent);

                    controller.updateMetadata(metadata);
                    controller.refreshView();
                    status.setValue("");
                    upload.setEnabled(true);
                }
                catch (final EscidocClientException e) {
                    LOG.error(e.getMessage());
                    mainWindow.showNotification(e.getMessage());
                }
                subwindow.getParent().removeWindow(subwindow);
            }
        });

        final Button cancelBtn = new Button(ViewConstants.CANCEL, new Button.ClickListener() {

            @Override
            public void buttonClick(final ClickEvent event) {
                subwindow.getParent().removeWindow(subwindow);
            }
        });

        hl = new HorizontalLayout();
        hl.setVisible(false);
        hl.addComponent(saveBtn);
        hl.addComponent(cancelBtn);

        subwindow.addComponent(status);
        subwindow.addComponent(upload);
        subwindow.addComponent(progressLayout);
        subwindow.addComponent(hl);
        mainWindow.addWindow(subwindow);
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