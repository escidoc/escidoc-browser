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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.ContainerBuilder;
import org.escidoc.browser.model.internal.ResourceDisplay;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.AddContainerListener;
import org.escidoc.browser.ui.listeners.MetadataFileReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.util.BeanItemContainer;
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

    private final Label status = new Label("Upload a wellformed XML file to create metadata!");

    private final ProgressIndicator progressIndicator = new ProgressIndicator();

    private final MetadataFileReceiver receiver = new MetadataFileReceiver();

    private final HorizontalLayout progressLayout = new HorizontalLayout();

    private final Upload upload = new Upload("", receiver);

    private final Repositories repositories;

    private final Window mainWindow;

    private final ResourceModel parent;

    private final TreeDataSource treeDataSource;

    private final String contextId;

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

    private void buildContainerForm() throws EscidocClientException {
        addContainerForm.setImmediate(true);
        addNameField();
        addContentModelSelect();
        addMetaData();
        addButton();
    }

    private void addNameField() {
        nameField.setRequired(true);
        nameField.setRequiredError(ViewConstants.PLEASE_ENTER_A_CONTAINER_NAME);
        nameField.addValidator(new StringLengthValidator(ViewConstants.CONTAINER_LENGTH, 3, 50, false));
        nameField.setImmediate(true);
        addContainerForm.addComponent(nameField);
    }

    private void addContentModelSelect() throws EscidocException, InternalClientException, TransportException {
        Preconditions.checkNotNull(repositories.contentModel(), "ContentModelRepository is null: %s",
            repositories.contentModel());
        contentModelSelect.setRequired(true);
        bindData();
        addContainerForm.addComponent(contentModelSelect);
    }

    private void bindData() throws EscidocException, InternalClientException, TransportException {
        final Collection<? extends Resource> contentModelList =
            repositories.contentModel().findPublicOrReleasedResources();
        final List<ResourceDisplay> resourceDisplayList = new ArrayList<ResourceDisplay>(contentModelList.size());
        for (final Resource resource : contentModelList) {
            resourceDisplayList.add(new ResourceDisplay(resource.getObjid(), resource.getXLinkTitle() + " ("
                + resource.getObjid() + ")"));
        }
        final BeanItemContainer<ResourceDisplay> resourceDisplayContainer =
            new BeanItemContainer<ResourceDisplay>(ResourceDisplay.class, resourceDisplayList);
        resourceDisplayContainer.addNestedContainerProperty("objectId");
        resourceDisplayContainer.addNestedContainerProperty("title");
        contentModelSelect.setContainerDataSource(resourceDisplayContainer);
        contentModelSelect.setItemCaptionPropertyId("title");
    }

    @SuppressWarnings("serial")
    private void addMetaData() {
        addContainerForm.addComponent(status);
        addContainerForm.addComponent(upload);
        addContainerForm.addComponent(progressLayout);

        // Make uploading start immediately when file is selected
        upload.setImmediate(true);
        upload.setButtonCaption(ViewConstants.SELECT_FILE);

        progressLayout.setSpacing(true);
        progressLayout.setVisible(false);
        progressLayout.addComponent(progressIndicator);
        progressLayout.setComponentAlignment(progressIndicator, Alignment.MIDDLE_LEFT);

        final Button cancelProcessing = new Button("Cancel");
        cancelProcessing.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
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
            public void uploadStarted(final StartedEvent event) {
                // This method gets called immediately after upload is started
                upload.setVisible(false);
                progressLayout.setVisible(true);
                progressIndicator.setValue(Float.valueOf(0f));
                progressIndicator.setPollingInterval(500);
                status.setValue("Uploading file \"" + event.getFilename() + "\"");
            }
        });

        upload.addListener(new Upload.ProgressListener() {
            @Override
            public void updateProgress(final long readBytes, final long contentLength) {
                // This method gets called several times during the update
                progressIndicator.setValue(new Float(readBytes / (float) contentLength));
            }

        });

        upload.addListener(new Upload.SucceededListener() {
            @Override
            public void uploadSucceeded(final SucceededEvent event) {
                // This method gets called when the upload finished successfully
                status.setValue("Uploading file \"" + event.getFilename() + "\" succeeded");
                final boolean isWellFormed = XmlUtil.isWellFormed(receiver.getFileContent());
                receiver.setWellFormed(isWellFormed);
                if (isWellFormed) {
                    status.setValue(ViewConstants.XML_IS_WELL_FORMED);
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
    }

    private void addButton() {
        addContainerForm.addComponent(new Button(ViewConstants.ADD, new AddContainerListener(this)));
    }

    private void buildSubWindowUsingContainerForm() {
        subwindow.setWidth("600px");
        subwindow.setModal(true);
        subwindow.addComponent(addContainerForm);
    }

    public void openSubWindow() throws EscidocClientException {
        buildContainerForm();
        buildSubWindowUsingContainerForm();
        mainWindow.addWindow(subwindow);
    }

    public boolean allValid() {
        return nameField.isValid() && contentModelSelect.isValid();
    }

    public String getContentModelId() {
        final Object value = contentModelSelect.getValue();
        if (value instanceof ResourceDisplay) {
            return ((ResourceDisplay) value).getObjectId();
        }
        return AppConstants.EMPTY_STRING;
    }

    public String getContainerName() {
        return nameField.getValue().toString();
    }

    public void showRequiredMessage() {
        mainWindow.showNotification("Please fill in all the required elements", 1);
    }

    private String getMetadata() {
        if (receiver.isWellFormed()) {
            return receiver.getFileContent();
        }
        return AppConstants.EMPTY_STRING;
    }

    public void create() {
        createNewContainer(getContainerName(), getContentModelId(), getContextId());
    }

    private void createNewContainer(final String containerName, final String contentModelId, final String contextId) {
        try {
            updateDataSource(createContainerInRepository(buildContainer(containerName, contentModelId, contextId)));
            closeSubWindow();
        }
        catch (final EscidocClientException e) {
            LOG.error(e.getMessage());
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private Container buildContainer(final String containerName, final String contentModelId, final String contextId) {
        final Container tobeCreated =
            new ContainerBuilder(new ContextRef(contextId), new ContentModelRef(contentModelId), getMetadata())
                .build(containerName);
        return tobeCreated;
    }

    private Container createContainerInRepository(final Container newContainer) throws EscidocClientException {
        return repositories.container().createWithParent(newContainer, parent);
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