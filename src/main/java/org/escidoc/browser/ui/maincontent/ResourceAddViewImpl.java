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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.ItemModel;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.ContainerBuilder;
import org.escidoc.browser.model.internal.ItemBuilder;
import org.escidoc.browser.model.internal.ResourceDisplay;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.AddResourceListener;
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
import de.escidoc.core.resources.VersionableResource;
import de.escidoc.core.resources.cmm.ContentModel;
import de.escidoc.core.resources.common.reference.ContentModelRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.om.container.Container;
import de.escidoc.core.resources.om.item.Item;

public class ResourceAddViewImpl implements ResourceAddView {

    private final static Logger LOG = LoggerFactory.getLogger(ResourceAddViewImpl.class);

    private final FormLayout addResourceForm = new FormLayout();

    private final TextField nameField = new TextField(ViewConstants.RESOURCE_NAME_GENERIC);

    private final NativeSelect contentModelSelect = new NativeSelect(ViewConstants.PLEASE_SELECT_CONTENT_MODEL);

    private final NativeSelect resourceTypeSelect = new NativeSelect(ViewConstants.PLEASE_SELECT_RESOURCE_TOCREATE);

    private final Window subwindow = new Window(ViewConstants.CREATE_RESOURCE);

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

    private Router router;

    public ResourceAddViewImpl(final Repositories repositories, final Window mainWindow, final ResourceModel parent,
        final TreeDataSource treeDataSource, final String contextId, Router router) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(parent, "parent is null: %s", parent);
        Preconditions.checkNotNull(treeDataSource, "treeDataSource is null: %s", treeDataSource);
        Preconditions.checkNotNull(contextId, "contextId is null: %s", contextId);
        Preconditions.checkNotNull(router, "router is null: %s", router);
        this.repositories = repositories;
        this.mainWindow = mainWindow;
        this.parent = parent;
        this.treeDataSource = treeDataSource;
        this.router = router;
        this.contextId = contextId;
    }

    /*
     * This is the case where the button is invoked from a button and not from a tree
     */
    public ResourceAddViewImpl(Repositories repositories, Window mainWindow, ResourceProxy containerProxy,
        String contextId, Router router) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(containerProxy, "parent is null: %s", containerProxy);
        Preconditions.checkNotNull(contextId, "contextId is null: %s", contextId);
        Preconditions.checkNotNull(router, "router is null: %s", router);

        this.repositories = repositories;
        this.mainWindow = mainWindow;
        parent = containerProxy;
        this.contextId = contextId;
        treeDataSource = null;
        this.router = router;
    }

    private void buildContainerForm() throws EscidocClientException {
        addResourceForm.setImmediate(true);
        addNameField();
        addContentModelSelect();
        addMetaData();
        addButton();
    }

    private void addNameField() {
        nameField.setRequired(true);
        nameField.setRequiredError(ViewConstants.PLEASE_ENTER_A_RESOURCE_NAME);
        nameField.addValidator(new StringLengthValidator(ViewConstants.RESOURCE_LENGTH, 3, 50, false));
        nameField.setImmediate(true);
        addResourceForm.addComponent(nameField);
    }

    private void addContentModelSelect() throws EscidocException, InternalClientException, TransportException {
        Preconditions.checkNotNull(repositories.contentModel(), "ContentModelRepository is null: %s",
            repositories.contentModel());
        contentModelSelect.setRequired(true);
        bindData();
        addResourceForm.addComponent(contentModelSelect);
    }

    /**
     * This dropbox is needed only if there is no resourceType description in the ContentModel
     */
    private void addResouceTypeSelect() {
        resourceTypeSelect.addItem("Item");
        resourceTypeSelect.addItem("Container");
        resourceTypeSelect.setRequired(true);
        addResourceForm.addComponent(resourceTypeSelect, 2);

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
        addResourceForm.addComponent(status);
        addResourceForm.addComponent(upload);
        addResourceForm.addComponent(progressLayout);

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
        addResourceForm.addComponent(new Button(ViewConstants.ADD, new AddResourceListener(this)));
    }

    private void buildSubWindowUsingContainerForm() {
        subwindow.setWidth("600px");
        subwindow.setModal(true);
        subwindow.addComponent(addResourceForm);
    }

    public void openSubWindow() throws EscidocClientException {
        buildContainerForm();
        buildSubWindowUsingContainerForm();
        mainWindow.addWindow(subwindow);
    }

    public boolean validateFields() {
        return nameField.isValid() && contentModelSelect.isValid();
    }

    public String getContentModelId() {
        final Object value = contentModelSelect.getValue();
        if (value instanceof ResourceDisplay) {
            return ((ResourceDisplay) value).getObjectId();
        }
        return AppConstants.EMPTY_STRING;
    }

    public String getContentModelType(String id) {
        try {
            ContentModel contentModel = repositories.contentModel().findById(id);
            String objectType = contentModel.getProperties().getDescription();
            if ((objectType != null) && (objectType.contains("http://www.w3.org/1999/02/22-rdf-syntax-ns#type="))) {
                final Pattern controllerIdPattern =
                    Pattern.compile("http://www.w3.org/1999/02/22-rdf-syntax-ns#type=org.escidoc.resources.([^;]*);");
                final Matcher controllerIdMatcher =
                    controllerIdPattern.matcher(contentModel.getProperties().getDescription());
                if (controllerIdMatcher.find()) {
                    objectType = controllerIdMatcher.group(1);
                }

                return objectType;
            }
            else {
                return null;
            }
        }
        catch (EscidocClientException e) {
            mainWindow.showNotification(ViewConstants.ERROR_RETRIEVING_CONTENTMODEL + e.getLocalizedMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * In case we have models which are different from the default ones, we get the name of the module here
     * 
     * @param contentModelid
     * @return
     */
    private String getModuleName(String contentModelid) {
        ContentModel contentModel;
        String controllerName = null;
        try {
            contentModel = repositories.contentModel().findById(contentModelid);
            String contentModelDesc = contentModel.getProperties().getDescription();
            final Pattern controllerIdPattern = Pattern.compile("org.escidoc.browser.Controller=([^;]*);");
            final Matcher controllerIdMatcher = controllerIdPattern.matcher(contentModelDesc);

            if (controllerIdMatcher.find()) {
                controllerName = controllerIdMatcher.group(1);
            }
            return controllerName;
        }
        catch (EscidocClientException e) {
            LOG.error("Could not find a name for this controller" + e.getLocalizedMessage());
            return null;

        }
    }

    public String getResourceName() {
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

    /**
     * Create a resource
     * 
     * @throws EscidocClientException
     */
    public void createResource() {
        String resourceType = getContentModelType(getContentModelId());
        // A HOOK is needed here to check if the resource belongs to something external!!!
        // LOG.debug(getModuleName(getContentModelId()));
        // if (getModuleName(getContentModelId()) != null) {
        //
        // String metadataResourceType =
        // getModuleName(getContentModelId())
        // .substring(getModuleName(getContentModelId()).lastIndexOf('.') + 1).toUpperCase();
        // String constantsClassName =
        // getModuleName(getContentModelId()).replace(metadataResourceType, "") + "MetaDataConstants";
        //
        // Class<?> controllerClass;
        // try {
        // controllerClass = Class.forName(constantsClassName);
        // Object someclass = controllerClass.newInstance();
        //
        //
        // }
        // catch (ClassNotFoundException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // catch (InstantiationException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // catch (IllegalAccessException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        //
        // LOG.debug("End it is not null" + metadataResourceType + " dhe emri i Classes eshte: " + constantsClassName);
        // }

        LOG.debug("Resource Type" + resourceTypeSelect.getValue());
        try {
            if (resourceType == null) {
                if (resourceTypeSelect.getValue() == null) {
                    mainWindow.showNotification(ViewConstants.ERROR_NO_RESOURCETYPE_IN_CONTENTMODEL
                        + contentModelSelect.getValue() + "\"", Window.Notification.TYPE_HUMANIZED_MESSAGE);
                    addResouceTypeSelect();
                }
                else if (resourceTypeSelect.getValue() == "Container") {
                    createNewResource(ResourceType.CONTAINER.toString());
                }
                else {
                    createNewResource(ResourceType.ITEM.toString());
                }
            }
            else if (resourceType.toUpperCase().equals(ResourceType.CONTAINER.toString())) {
                createNewResource(ResourceType.CONTAINER.toString());
            }
            else if (resourceType.toUpperCase().equals(ResourceType.ITEM.toString())) {
                createNewResource(ResourceType.ITEM.toString());
            }
            else {
                mainWindow.showNotification(
                    ViewConstants.ERROR_NO_RESOURCETYPE_IN_CONTENTMODEL + contentModelSelect.getValue() + "\"",
                    Window.Notification.TYPE_ERROR_MESSAGE);
            }
        }
        catch (EscidocClientException e) {
            mainWindow.showNotification(e.getLocalizedMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private void createNewResource(String resourceType) throws EscidocClientException {

        VersionableResource createdResource = null;
        if (resourceType.equals(ResourceType.CONTAINER.toString())) {
            createdResource =
                createContainerInRepository(buildContainer(getResourceName(), getContentModelId(), getContextId()));
        }
        else if (resourceType.equals(ResourceType.ITEM.toString())) {
            createdResource = repositories.item().createWithParent(buildItem(), parent);
        }
        else {
        }

        if (treeDataSource != null) {
            updateDataSource(createdResource, resourceType);
        }
        if (router != null) {
            router.show(parent);
        }
        closeSubWindow();
    }

    private Item buildItem() {
        return new ItemBuilder(new ContextRef(getContextId()), new ContentModelRef(getContentModelId()), getMetadata())
            .build(getResourceName());
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

    private void updateDataSource(final VersionableResource createdResource, String resourceType) {
        if (resourceType.equals(ResourceType.CONTAINER.toString())) {
            treeDataSource.addChild(parent, new ContainerModel(createdResource));
        }
        else {
            treeDataSource.addChild(parent, new ItemModel(createdResource));
        }
    }

    private void closeSubWindow() {
        subwindow.getParent().removeWindow(subwindow);
    }

    private String getContextId() {
        return contextId;
    }
}