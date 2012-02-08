package org.escidoc.browser.ui.maincontent;

import com.google.common.base.Preconditions;

import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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

import org.escidoc.browser.controller.OrgUnitController;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.MetadataFileReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;

@SuppressWarnings("serial")
public final class OnAddOrgUnitMetadata implements Button.ClickListener {

    private final static Logger LOG = LoggerFactory.getLogger(OnAddOrgUnitMetadata.class);

    private final Label status = new Label(ViewConstants.UPLOAD_A_WELLFORMED_XML_FILE_TO_CREATE_METADATA);

    private MetadataFileReceiver receiver = new MetadataFileReceiver();

    private final AbstractOrderedLayout progressLayout = new HorizontalLayout();

    private HorizontalLayout hl;

    private TextField mdName;

    private Repositories repositories;

    private ResourceProxy ou;

    private Window mainWindow;

    private OrgUnitController controller;

    public OnAddOrgUnitMetadata(ResourceProxy ou, OrgUnitController controller, Repositories repositories,
        Window mainWindow) {
        Preconditions.checkNotNull(ou, "ou is null: %s", ou);
        Preconditions.checkNotNull(controller, "controller is null: %s", controller);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);

        this.ou = ou;
        this.controller = controller;
        this.repositories = repositories;
        this.mainWindow = mainWindow;
    }

    @Override
    public void buttonClick(final ClickEvent event) {
        showAddWindow();
    }

    private void showAddWindow() {
        final Window subwindow = new Window(ViewConstants.ADD_ORGANIZATIONAL_UNIT_S_METADATA);
        subwindow.setWidth("600px");
        subwindow.setModal(true);

        // Make uploading start immediately when file is selected
        final Upload upload = new Upload("", receiver);
        upload.setImmediate(true);
        upload.setButtonCaption("Select file");

        progressLayout.setSpacing(true);
        progressLayout.setVisible(false);

        final ProgressIndicator pi = new ProgressIndicator();
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
                    hl.setVisible(false);
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
                            metadataRecord.setContent(getMetadataContent());
                            repositories.organization().addMetaData(ou, metadataRecord);
                            controller.refreshView();
                            upload.setEnabled(true);
                            subwindow.getParent().removeWindow(subwindow);
                        }
                        catch (final EscidocClientException e) {
                            LOG.error(e.getMessage());
                            mdName.setComponentError(new UserError("Failed to add the new Metadata record"
                                + e.getLocalizedMessage()));
                        }
                        catch (final SAXException e) {
                            LOG.error(e.getMessage());
                            mdName.setComponentError(new UserError("Failed to add the new Metadata record"
                                + e.getLocalizedMessage()));
                        }
                        catch (final IOException e) {
                            LOG.error(e.getMessage());
                            mdName.setComponentError(new UserError("Failed to add the new Metadata record"
                                + e.getLocalizedMessage()));
                        }
                        catch (final ParserConfigurationException e) {
                            LOG.error(e.getMessage());
                            mdName.setComponentError(new UserError("Failed to add the new Metadata record"
                                + e.getLocalizedMessage()));
                        }
                    }
                }
            }

            private Element getMetadataContent() throws SAXException, IOException, ParserConfigurationException {
                final String fileContent = receiver.getFileContent();
                return XmlUtil.string2Dom(fileContent).getDocumentElement();
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
}