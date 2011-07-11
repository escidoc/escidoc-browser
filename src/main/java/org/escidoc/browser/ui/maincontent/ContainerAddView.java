package org.escidoc.browser.ui.maincontent;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.ContainerBuilder;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.AddContainerListener;
import org.escidoc.browser.ui.listeners.MyReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.common.reference.ContentModelRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.om.container.Container;

public class ContainerAddView {

    private final static Logger LOG = LoggerFactory.getLogger(ContainerAddView.class);

    private final FormLayout addContainerForm = new FormLayout();

    private final TextField nameField = new TextField(ViewConstants.CONTAINER_NAME);

    private final NativeSelect contentModelSelect = new NativeSelect(ViewConstants.PLEASE_SELECT_CONTENT_MODEL);

    private final Window subwindow = new Window(ViewConstants.CREATE_CONTAINER);

    private final Repositories repositories;

    private final Window mainWindow;

    private final ResourceModel parent;

    private final TreeDataSource treeDataSource;

    private Button addButton;

    private final String contextId;

    private final Label status = new Label("Please select a file to upload");

    private final ProgressIndicator pi = new ProgressIndicator();

    private final MyReceiver receiver = new MyReceiver();

    private final HorizontalLayout progressLayout = new HorizontalLayout();

    private final Upload upload = new Upload("", receiver);

    public ContainerAddView(final Repositories repositories, final Window mainWindow, final ResourceModel parent,
        final TreeDataSource treeDataSource, final String contextId) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(parent, "parent is null: %s", parent);
        Preconditions.checkNotNull(treeDataSource, "treeDataSource is null: %s", treeDataSource);
        Preconditions.checkNotNull(contextId, "contextId is null: %s", contextId);
        this.repositories = repositories;
        this.mainWindow = mainWindow;
        this.parent = parent;
        this.treeDataSource = treeDataSource;
        this.contextId = contextId;
    }

    public void buildContainerForm() throws MalformedURLException, EscidocClientException {
        addContainerForm.setImmediate(true);
        addNameField();
        addContentModelSelect();
        addMetaData();
        addButton();
    }

    private void addNameField() {
        nameField.setRequired(true);
        nameField.setRequiredError(ViewConstants.PLEASE_ENTER_A_CONTAINER_NAME);
        nameField.addValidator(new StringLengthValidator(ViewConstants.CONTAINER_NAME_MUST_BE_3_25_CHARACTERS, 3, 25,
            false));
        nameField.setImmediate(true);
        addContainerForm.addComponent(nameField);
    }

    private void addContentModelSelect() throws MalformedURLException, EscidocException, InternalClientException,
        TransportException {
        Preconditions.checkNotNull(repositories.contentModel(), "ContentModelRepository is null: %s",
            repositories.contentModel());
        contentModelSelect.setRequired(true);
        for (final Resource resource : repositories.contentModel().findPublicOrReleasedResources()) {
            contentModelSelect.addItem(resource.getObjid());
        }
        addContainerForm.addComponent(contentModelSelect);
    }

    private void addMetaData() {
        // Slow down the upload
        receiver.setSlow(true);

        addContainerForm.addComponent(status);
        addContainerForm.addComponent(upload);
        addContainerForm.addComponent(progressLayout);

        // Make uploading start immediately when file is selected
        upload.setImmediate(true);
        upload.setButtonCaption("Select file");

        progressLayout.setSpacing(true);
        progressLayout.setVisible(false);
        progressLayout.addComponent(pi);
        progressLayout.setComponentAlignment(pi, Alignment.MIDDLE_LEFT);

        final Button cancelProcessing = new Button("Cancel");
        cancelProcessing.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                upload.interruptUpload();
            }
        });
        cancelProcessing.setStyleName("small");
        progressLayout.addComponent(cancelProcessing);

        /**
         * =========== Add needed listener for the upload component: start, progress, finish, success, fail ===========
         */

        upload.addListener(new Upload.StartedListener() {
            @Override
            public void uploadStarted(StartedEvent event) {
                // This method gets called immediatedly after upload is started
                upload.setVisible(false);
                progressLayout.setVisible(true);
                pi.setValue(0f);
                pi.setPollingInterval(500);
                status.setValue("Uploading file \"" + event.getFilename() + "\"");
            }
        });

        upload.addListener(new Upload.ProgressListener() {
            @Override
            public void updateProgress(long readBytes, long contentLength) {
                // This method gets called several times during the update
                pi.setValue(new Float(readBytes / (float) contentLength));
            }

        });

        upload.addListener(new Upload.SucceededListener() {
            @Override
            public void uploadSucceeded(SucceededEvent event) {
                // This method gets called when the upload finished successfully
                status.setValue("Uploading file \"" + event.getFilename() + "\" succeeded");
                if (isValidXml(receiver.getFileContent())) {
                    status.setValue("XML Looks correct");
                    upload.setEnabled(false);
                }
                else {
                    status.setValue("Not valid");
                }
            }
        });

        upload.addListener(new Upload.FailedListener() {
            @Override
            public void uploadFailed(FailedEvent event) {
                // This method gets called when the upload failed
                status.setValue("Uploading interrupted");
            }
        });

        upload.addListener(new Upload.FinishedListener() {
            @Override
            public void uploadFinished(FinishedEvent event) {
                // This method gets called always when the upload finished,
                // either succeeding or failing
                progressLayout.setVisible(false);
                upload.setVisible(true);
                upload.setCaption("Select another file");
            }
        });

    }

    /**
     * checking if the uploaded file contains a valid XML string
     * 
     * @param xml
     * @return boolean
     */
    private boolean isValidXml(final String xml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document d;
            try {
                d = builder.parse(is);
                return true;
                // Element content = d.getDocumentElement();
            }
            catch (SAXException e) {
                return false;
            }
            catch (IOException e) {
                return false;
            }
        }
        catch (ParserConfigurationException e) {
            return false;
        }

    }

    private void addButton() {
        addButton = new Button(ViewConstants.ADD, new AddContainerListener(this));
        addContainerForm.addComponent(addButton);
    }

    private void buildSubWindowUsingContainerForm() {
        subwindow.setWidth("600px");
        subwindow.setModal(true);
        subwindow.addComponent(addContainerForm);
    }

    public void openSubWindow() throws MalformedURLException, EscidocClientException {
        buildContainerForm();
        buildSubWindowUsingContainerForm();
        mainWindow.addWindow(subwindow);
    }

    public boolean allValid() {
        return nameField.isValid() && contentModelSelect.isValid();
    }

    public String getContentModelId() {
        return (String) contentModelSelect.getValue();
    }

    public String getContainerName() {
        return nameField.getValue().toString();
    }

    public void showRequiredMessage() {
        mainWindow.showNotification("Please fill in all the required elements", 1);
    }

    private String getMetadata() {
        return receiver.getFileContent();
    }

    public void create() {
        createNewContainer(getContainerName(), getContentModelId(), getContextId(), getMetadata());
    }

    private void createNewContainer(
        final String containerName, final String contentModelId, final String contextId, String metaData) {
        final ContainerBuilder cntBuild =
            new ContainerBuilder(new ContextRef(contextId), new ContentModelRef(contentModelId), metaData);
        final Container newContainer = cntBuild.build(containerName);
        try {
            final Container createdContainer = createContainerInRepository(newContainer);
            updateDataSource(createdContainer);
            closeSubWindow();
        }
        catch (final EscidocClientException e) {
            LOG.error(e.getMessage());
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private Container createContainerInRepository(final Container newContainer) throws EscidocClientException {
        final Container createdContainer = repositories.container().createWithParent(newContainer, parent);
        return createdContainer;
    }

    private void updateDataSource(final Container createdContainer) {
        treeDataSource.addChild(parent, new ContainerModel(createdContainer));
    }

    private void closeSubWindow() {
        subwindow.getParent().removeWindow(subwindow);
    }

    private String getContextId() {
        return contextId;
    }
}