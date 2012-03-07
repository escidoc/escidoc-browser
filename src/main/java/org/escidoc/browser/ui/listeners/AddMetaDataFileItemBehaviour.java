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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.vaadin.terminal.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.TextField;
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
public class AddMetaDataFileItemBehaviour implements ClickListener {
    private static final Logger LOG = LoggerFactory.getLogger(AddMetaDataFileItemBehaviour.class);

    private final ResourceProxy resourceProxy;

    private final Window mainWindow;

    private MetadataFileReceiver receiver;

    private final HorizontalLayout progressLayout = new HorizontalLayout();

    private Upload upload;

    private final Label status = new Label("Upload a wellformed XML file to create metadata!");

    private final ProgressIndicator pi = new ProgressIndicator();

    private HorizontalLayout hl;

    private final Repositories repositories;

    private TextField mdName;

    public AddMetaDataFileItemBehaviour(final Window mainWindow, final Repositories repositories,
        final ResourceProxy resourceProxy) {
        this.mainWindow = mainWindow;
        this.repositories = repositories;
        this.resourceProxy = resourceProxy;
    }

    @Override
    public void buttonClick(final ClickEvent event) {
        showAddWindow();
    }

    public void showAddWindow() {
        final Window subwindow = new Window(ViewConstants.ADD_ITEM_S_METADATA);
        subwindow.setWidth("600px");
        subwindow.setModal(true);
        receiver = new MetadataFileReceiver();
        // Make uploading start immediately when file is selected
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
                final String fileContent = receiver.getFileContent();
                final boolean isWellFormed = XmlUtil.isWellFormed(fileContent);
                receiver.setWellFormed(isWellFormed);
                if (isWellFormed) {
                    status.setValue(ViewConstants.XML_IS_WELL_FORMED);
                    hl.setVisible(true);
                    upload.setEnabled(false);
                }
                else {
                    status.setValue(ViewConstants.XML_IS_NOT_WELL_FORMED);
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
        mdName = new TextField("Metadata name");
        mdName.setValue("");
        mdName.setImmediate(true);
        mdName.setValidationVisible(false);

        hl = new HorizontalLayout();
        hl.setMargin(true);
        final Button btnAdd = new Button("Save", new Button.ClickListener() {
            Item item;

            private boolean containSpace(final String text) {
                final Pattern pattern = Pattern.compile("\\s");
                final Matcher matcher = pattern.matcher(text);
                return matcher.find();
            }

            @Override
            public void buttonClick(final ClickEvent event) {
                if (mdName.getValue().equals("")) {
                    mdName.setComponentError(new UserError("You have to add a name for your MetaData"));
                }
                else if (containSpace(((String) mdName.getValue()))) {
                    mdName.setComponentError(new UserError("The name of MetaData can not contain space"));
                }
                else {
                    mdName.setComponentError(null);

                    if (receiver.getFileContent().isEmpty()) {
                        upload.setComponentError(new UserError("Please select a well formed XML file as metadata."));
                    }
                    else if (!receiver.isWellFormed()) {
                        upload.setComponentError(new UserError(ViewConstants.XML_IS_NOT_WELL_FORMED));
                    }
                    else {
                        final MetadataRecord metadataRecord = new MetadataRecord(mdName.getValue().toString());
                        try {
                            item = repositories.item().findItemById(resourceProxy.getId());
                            metadataRecord.setContent(getMetadataContent());
                            repositories.item().addMetaData(metadataRecord, item);
                            upload.setEnabled(true);
                            subwindow.getParent().removeWindow(subwindow);
                        }
                        catch (final EscidocClientException e) {
                            LOG.error(e.getLocalizedMessage());
                            mdName.setComponentError(new UserError("Failed to add the new Metadata record"
                                + e.getLocalizedMessage()));
                        }
                        catch (final SAXException e) {
                            LOG.error(e.getLocalizedMessage());
                            mdName.setComponentError(new UserError("Failed to add the new Metadata record"
                                + e.getLocalizedMessage()));
                        }
                        catch (final IOException e) {
                            LOG.error(e.getLocalizedMessage());
                            mdName.setComponentError(new UserError("Failed to add the new Metadata record"
                                + e.getLocalizedMessage()));
                        }
                        catch (final ParserConfigurationException e) {
                            LOG.error(e.getLocalizedMessage());
                            mdName.setComponentError(new UserError("Failed to add the new Metadata record"
                                + e.getLocalizedMessage()));
                        }
                    }
                }
            }

        });

        final Button cnclAdd = new Button("Cancel", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });

        hl.addComponent(btnAdd);
        hl.addComponent(cnclAdd);
        subwindow.addComponent(mdName);
        subwindow.addComponent(status);
        subwindow.addComponent(upload);
        subwindow.addComponent(progressLayout);
        subwindow.addComponent(hl);
        mainWindow.addWindow(subwindow);

    }

    private Element getMetadataContent() throws SAXException, IOException, ParserConfigurationException {
        return XmlUtil.string2Dom(receiver.getFileContent()).getDocumentElement();
    }

}
