package org.escidoc.browser.ui.listeners;

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

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.container.Container;

public class EditMetaDataFileContainerBehaviour implements ClickListener {
    static final Logger LOG = LoggerFactory.getLogger(BrowserApplication.class);

    private final MetadataFileReceiver receiver = new MetadataFileReceiver();

    private final HorizontalLayout progressLayout = new HorizontalLayout();

    private final Upload upload = new Upload("", receiver);

    private final Label status = new Label("Upload a wellformed XML file to create metadata!");

    private final ProgressIndicator pi = new ProgressIndicator();

    private final MetadataRecord metadataRecord;

    private final Window mainWindow;

    private final EscidocServiceLocation escidocServiceLocation;

    private final Repositories repositories;

    private HorizontalLayout hl;

    private final ResourceProxy resourceProxy;

    private Element metadataContent;

    public EditMetaDataFileContainerBehaviour(final MetadataRecord metadataRecord, final Window mainWindow,
        final EscidocServiceLocation escidocServiceLocation, final Repositories repositories,
        final ResourceProxy resourceProxy) {
        this.metadataRecord = metadataRecord;
        this.mainWindow = mainWindow;
        this.escidocServiceLocation = escidocServiceLocation;
        this.repositories = repositories;
        this.resourceProxy = resourceProxy;

    }

    @Override
    public void buttonClick(final ClickEvent event) {
        showWindow();

    }

    private void showWindow() {
        final Window subwindow = new Window(ViewConstants.EDIT_METADATA);
        subwindow.setWidth("600px");
        subwindow.setModal(true);

        // Make uploading start immediately when file is selected
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
                pi.setValue(0f);
                pi.setPollingInterval(500);
                status.setValue("Uploading file \"" + event.getFilename() + "\"");
            }
        });

        upload.addListener(new Upload.SucceededListener() {
            @Override
            public void uploadSucceeded(final SucceededEvent event) {
                // This method gets called when the upload finished successfully
                status.setValue("Uploading file \"" + event.getFilename() + "\" succeeded");
                if (isValidXml(receiver.getFileContent())) {
                    status.setValue("XML Looks correct");
                    hl.setVisible(true);
                    upload.setEnabled(false);
                }
                else {
                    status.setValue("Not valid");
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

        final Button btnAdd = new Button("Add New Metadata", new Button.ClickListener() {
            Container container;

            @Override
            public void buttonClick(final ClickEvent event) {
                try {
                    container = repositories.container().findContainerById(resourceProxy.getId());
                    metadataRecord.setContent(metadataContent);
                    repositories.container().updateMetaData(metadataRecord, container);

                }
                catch (final EscidocClientException e) {
                    LOG.debug(e.getLocalizedMessage());
                }
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });

        final Button cnclAdd = new Button("Cancel", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        hl = new HorizontalLayout();
        hl.setVisible(false);
        hl.addComponent(btnAdd);
        hl.addComponent(cnclAdd);

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
