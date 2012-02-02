package org.escidoc.browser.ui.maincontent;

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

import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.OrgUnitProxy;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.MetadataFileReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;

@SuppressWarnings("serial")
public class OnEditOrgUnitMetadata implements ClickListener {

    private final static Logger LOG = LoggerFactory.getLogger(OnEditOrgUnitMetadata.class);

    private final HorizontalLayout progressLayout = new HorizontalLayout();

    private final ProgressIndicator pi = new ProgressIndicator();

    private final MetadataRecord metadataRecord;

    private final Repositories repositories;

    private final OrgUnitProxy resourceProxy;

    private MetadataFileReceiver receiver;

    private Upload upload;

    private Label status;

    private Window mainWindow;

    private HorizontalLayout hl;

    private Element metadataContent;

    private OrgUnitView view;

    public OnEditOrgUnitMetadata(MetadataRecord metadataRecord, Router router, Repositories repositories,
        OrgUnitProxy ou, OrgUnitView view) {
        Preconditions.checkNotNull(metadataRecord, "metadataRecord is null: %s", metadataRecord);
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(ou, "ou is null: %s", ou);
        Preconditions.checkNotNull(view, "view is null: %s", view);
        this.metadataRecord = metadataRecord;
        this.mainWindow = router.getMainWindow();
        this.repositories = repositories;
        this.resourceProxy = ou;
        this.view = view;
    }

    @Override
    public void buttonClick(ClickEvent event) {
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
                if (XmlUtil.isWellFormed(receiver.getFileContent())) {
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

        final Button saveBtn = new Button("Save", new Button.ClickListener() {

            @Override
            public void buttonClick(final ClickEvent event) {
                try {
                    metadataRecord.setContent(metadataContent);
                    repositories.organization().updateMetaData(resourceProxy, metadataRecord);
                    // TODO is it needed?
                    view.refreshView();
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

        final Button cancelBtn = new Button("Cancel", new Button.ClickListener() {

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

}
