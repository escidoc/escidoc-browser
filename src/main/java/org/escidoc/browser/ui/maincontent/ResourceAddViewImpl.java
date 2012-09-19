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
package org.escidoc.browser.ui.maincontent;

import com.google.common.base.Preconditions;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.ContainerBuilder;
import org.escidoc.browser.model.internal.ContainerModel;
import org.escidoc.browser.model.internal.ContextModel;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.model.internal.ItemBuilder;
import org.escidoc.browser.model.internal.ItemModel;
import org.escidoc.browser.model.internal.ResourceDisplay;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.AddResourceListener;
import org.escidoc.browser.ui.listeners.MetadataFileReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.VersionableResource;
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

    private TreeDataSource treeDataSource;

    private final String contextId;

    private final Router router;

    private String metaData = AppConstants.EMPTY_STRING;

    public ResourceAddViewImpl(final Repositories repositories, final ResourceModel parent,
        final TreeDataSource treeDataSource, final String contextId, final Router router) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(parent, "parent is null: %s", parent);
        Preconditions.checkNotNull(treeDataSource, "treeDataSource is null: %s", treeDataSource);
        Preconditions.checkNotNull(contextId, "contextId is null: %s", contextId);
        Preconditions.checkNotNull(router, "router is null: %s", router);
        this.repositories = repositories;
        this.router = router;
        this.mainWindow = router.getMainWindow();
        this.parent = parent;
        this.treeDataSource = treeDataSource;

        this.contextId = contextId;
    }

    /*
     * This is the case where the button is invoked from a button and not from a tree
     */
    public ResourceAddViewImpl(final ResourceProxy resourceProxy, final String contextId, final Router router) {
        Preconditions.checkNotNull(resourceProxy, "parent is null: %s", resourceProxy);
        Preconditions.checkNotNull(contextId, "contextId is null: %s", contextId);
        Preconditions.checkNotNull(router, "router is null: %s", router);

        this.router = router;
        this.repositories = router.getRepositories();
        this.mainWindow = router.getMainWindow();
        parent = createParentModel(resourceProxy);
        this.contextId = contextId;
    }

    private static ResourceModel createParentModel(final ResourceProxy resourceProxy) {
        ResourceModel contModel;
        if (resourceProxy.getType() == ResourceType.CONTEXT) {
            final ContextProxyImpl contextProxy = (ContextProxyImpl) resourceProxy;
            final Resource context = contextProxy.getContext();
            contModel = new ContextModel(context);
        }
        else {
            final ContainerProxy containerProxy = (ContainerProxy) resourceProxy;
            final Container container = containerProxy.getContainer();
            contModel = new ContainerModel(container);
        }
        return contModel;
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

    private void addContentModelSelect() throws EscidocClientException {
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

    // TODO refactor this method, make it shorter and the intent clearer.
    private void bindData() throws EscidocClientException {

        final ResourceProxy parentProxy = findParent();
        Map<String, Object> map = new HashMap<String, Object>();
        if (isContainer(parentProxy)) {
            map = jsonToMap(getContentModelDescription(parentProxy));
        }

        List<ResourceDisplay> displayList;
        if (map.isEmpty()) {
            displayList = addAllContentModels();
        }
        else {
            displayList = filterContentModel(map);
        }

        bind(displayList);

    }

    private List<ResourceDisplay> filterContentModel(final Map<String, Object> map) throws EscidocClientException {
        List<ResourceDisplay> displayList;
        final Object val = map.get("children");
        if (!(val instanceof List<?>)) {
            return Collections.emptyList();
        }
        final List<?> children = (List<?>) val;
        displayList = new ArrayList<ResourceDisplay>(children.size());
        for (final Object childObject : children) {
            if (childObject instanceof Map) {
                final Map<?, ?> childCm = getChildContentModel((Map<?, ?>) childObject);
                LOG.debug("using content model with id " + childCm.get("id"));

                if (childCm.get("id") instanceof String) {
                    final ResourceProxy childContentModel =
                        repositories.findByType(ResourceType.CONTENT_MODEL).findById((String) childCm.get("id"));
                    displayList.add(new ResourceDisplay(childContentModel.getId(), childContentModel.getName()));
                }
            }
        }
        return displayList;
    }

    private List<ResourceDisplay> addAllContentModels() throws EscidocException, InternalClientException,
        TransportException {
        List<ResourceDisplay> resourceDisplayList;
        final Collection<? extends Resource> contentModelList =
            repositories.contentModel().findPublicOrReleasedResources();
        resourceDisplayList = new ArrayList<ResourceDisplay>(contentModelList.size());
        for (final Resource resource : contentModelList) {
            resourceDisplayList.add(new ResourceDisplay(resource.getObjid(), resource.getXLinkTitle() + " ("
                + resource.getObjid() + ")"));
        }
        return resourceDisplayList;
    }

    private ResourceProxy findParent() throws EscidocClientException {
        return repositories.findByType(parent.getType()).findById(parent.getId());
    }

    private void bind(final List<ResourceDisplay> resourceDisplayList) {
        final BeanItemContainer<ResourceDisplay> resourceDisplayContainer =
            new BeanItemContainer<ResourceDisplay>(ResourceDisplay.class, resourceDisplayList);
        resourceDisplayContainer.addNestedContainerProperty("objectId");
        resourceDisplayContainer.addNestedContainerProperty("title");
        contentModelSelect.setContainerDataSource(resourceDisplayContainer);
        contentModelSelect.setItemCaptionPropertyId("title");
    }

    private String getContentModelDescription(final ResourceProxy parentProxy) throws EscidocClientException {
        return findContentModel(parentProxy).getDescription();
    }

    private ResourceProxy findContentModel(final ResourceProxy parentProxy) throws EscidocClientException {
        final ResourceProxy parentContentModel =
            repositories.findByType(ResourceType.CONTENT_MODEL).findById(parentProxy.getContentModel().getObjid());
        return parentContentModel;
    }

    private static boolean isContainer(final ResourceProxy parentProxy) {
        return parentProxy.getType() == ResourceType.CONTAINER;
    }

    // TODO move me to an util class
    private static Map<?, ?> getChildContentModel(final Map<?, ?> child) {
        final Object childCmObject = child.get("content-model");
        if (!(childCmObject instanceof Map<?, ?>)) {
            Collections.emptyMap();
        }
        return (Map<?, ?>) childCmObject;
    }

    // TODO move me to an util class
    private static Map<String, Object> jsonToMap(final String contentModelDescription) {
        try {
            return new ObjectMapper(new JsonFactory()).readValue(contentModelDescription,
                new TypeReference<HashMap<String, Object>>() {
                    // empty
                });
        }
        catch (final JsonParseException e) {
            LOG.debug("Content Model description is not a valid JSON, " + e.getMessage());
        }
        catch (final JsonMappingException e) {
            LOG.error("Jackson exception: " + e.getMessage());
        }
        catch (final IOException e) {
            LOG.error("Jackson exception: " + e.getMessage());
        }
        return Collections.emptyMap();
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
            public void buttonClick(@SuppressWarnings("unused") final ClickEvent event) {
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
                    setMetaData(receiver.getFileContent());
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
        addResourceForm.addComponent(new Button(ViewConstants.CREATE, new AddResourceListener(this)));
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

    @Override
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

    public String getContentModelType(final String cmDescription) {
        if ((cmDescription != null) && (cmDescription.contains("http://www.w3.org/1999/02/22-rdf-syntax-ns#type="))) {
            final Pattern controllerIdPattern =
                Pattern.compile("http://www.w3.org/1999/02/22-rdf-syntax-ns#type=org.escidoc.resources.([^;]*);");
            final Matcher controllerIdMatcher = controllerIdPattern.matcher(cmDescription);
            if (controllerIdMatcher.find()) {
                return controllerIdMatcher.group(1);
            }

        }
        return null;
    }

    public String getResourceName() {
        return nameField.getValue().toString();
    }

    @Override
    public void showRequiredMessage() {
        mainWindow.showNotification("Please fill in all the required elements", 1);
    }

    private void setMetaData(final String mdValue) {
        metaData = mdValue;
    }

    private String getMetadata() {
        return metaData;
    }

    /**
     * Create a resource
     * 
     * @throws EscidocClientException
     */
    @Override
    public void createResource() {
        try {
            final String contentModelDescription = getContentModelDescription(getContentModelId());
            final Map<String, Object> map = jsonToMap(contentModelDescription);
            if (map.isEmpty()) {
                final String resourceType = getContentModelType(contentModelDescription);
                if (resourceType == null) {
                    handleNoResourceType();
                }
                else if (resourceType.toUpperCase().equals(ResourceType.CONTAINER.toString())) {
                    createNewResource(ResourceType.CONTAINER.toString());
                }
                else if (resourceType.toUpperCase().equals(ResourceType.ITEM.toString())) {
                    createNewResource(ResourceType.ITEM.toString());
                }
                else {
                    mainWindow.showNotification(ViewConstants.ERROR_NO_RESOURCETYPE_IN_CONTENTMODEL
                        + contentModelSelect.getValue() + "\"", Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
            else {
                final String resourceType = (String) map.get("type");

                VersionableResource parent = null;

                if (resourceType.toUpperCase().equals(ResourceType.CONTAINER.toString())) {
                    parent = createNewResource(ResourceType.CONTAINER.toString());
                }
                else if (resourceType.toUpperCase().equals(ResourceType.ITEM.toString())) {
                    parent = createNewResource(ResourceType.ITEM.toString());
                }

                final Object val = map.get("children");
                if (!(val instanceof List<?>)) {
                    return;
                }

                createWithChildren(parent, (List<?>) val);
            }
        }
        catch (final EscidocClientException e) {
            mainWindow.showNotification(e.getLocalizedMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private void createWithChildren(final VersionableResource parent, final List<?> children)
        throws EscidocClientException {
        for (final Object childObject : children) {
            if (childObject instanceof Map) {
                final Map<?, ?> childContentModelMap = getChildContentModel((Map<?, ?>) childObject);
                final Object idObject = childContentModelMap.get("id");
                LOG.debug("using content model with id " + idObject);

                if (idObject instanceof String) {
                    final String childName = (String) ((Map<?, ?>) childObject).get("name");
                    final String childType = (String) ((Map<?, ?>) childObject).get("type");
                    final Boolean isRequired = (Boolean) ((Map<?, ?>) childObject).get("required");

                    if (isRequired.booleanValue()) {
                        createChild(parent, idObject, childName, childType);
                    }
                }
            }
        }
    }

    private void createChild(
        final VersionableResource parent, final Object idObject, final String childName, final String childType)
        throws EscidocClientException {

        final ResourceProxy childContentModel =
            repositories.findByType(ResourceType.CONTENT_MODEL).findById((String) idObject);
        final ResourceProxy parentProxy = repositories.container().findById(parent.getObjid());
        if (childType.equals("container")) {

            final Container container =
                new ContainerBuilder(new ContextRef(getContextId()), new ContentModelRef(childContentModel.getId()),
                    AppConstants.EMPTY_STRING).build(childName);
            final Container grantParent = repositories.container().createWithParent(container, parentProxy);

            final Map<String, Object> map = jsonToMap(childContentModel.getDescription());
            if (map.isEmpty()) {
                return;
            }

            final Object val = map.get("children");
            if (!(val instanceof List<?>)) {
                return;
            }

            createWithChildren(grantParent, (List<?>) val);
        }
        else if (childType.equals("item")) {
            final Item item =
                new ItemBuilder(new ContextRef(getContextId()), new ContentModelRef(childContentModel.getId()),
                    AppConstants.EMPTY_STRING).build(childName);
            repositories.item().createWithParent(item, parentProxy);
        }
    }

    private void handleNoResourceType() throws EscidocClientException {
        if (resourceTypeSelect.getValue() == null) {
            mainWindow.showNotification(
                ViewConstants.ERROR_NO_RESOURCETYPE_IN_CONTENTMODEL + contentModelSelect.getValue() + "\"",
                Window.Notification.TYPE_HUMANIZED_MESSAGE);
            addResouceTypeSelect();
        }
        else if (resourceTypeSelect.getValue() == "Container") {
            createNewResource(ResourceType.CONTAINER.toString());
        }
        else {
            createNewResource(ResourceType.ITEM.toString());
        }
    }

    private String getContentModelDescription(final String id) throws EscidocClientException {
        return repositories.contentModel().findById(id).getDescription();
    }

    private VersionableResource createNewResource(final String resourceType) throws EscidocClientException {
        final VersionableResource createdResource = createResourceBasedOnType(resourceType);

        if (treeDataSource != null) {
            updateDataSource(createdResource, resourceType);
            router.show(parent, Boolean.TRUE);
        }

        if (router != null) {
            router.show(parent, Boolean.TRUE);
            treeDataSource = router.getLayout().getTreeDataSource();
            updateDataSource(createdResource, resourceType);
        }

        closeSubWindow();
        return createdResource;
    }

    private VersionableResource createResourceBasedOnType(final String resourceType) throws EscidocClientException {

        if (resourceType.equals(ResourceType.CONTAINER.toString())) {
            return createContainerInRepository(buildContainer());
        }
        else if (resourceType.equals(ResourceType.ITEM.toString())) {
            return repositories.item().createWithParent(buildItem(), parent);
        }
        return null;
    }

    private Item buildItem() {
        return new ItemBuilder(new ContextRef(getContextId()), new ContentModelRef(getContentModelId()), getMetadata())
            .build(getResourceName());
    }

    private Container buildContainer() {
        return new ContainerBuilder(new ContextRef(getContextId()), new ContentModelRef(getContentModelId()),
            getMetadata()).build(getResourceName());
    }

    private Container createContainerInRepository(final Container newContainer) throws EscidocClientException {
        return repositories.container().createWithParent(newContainer, parent);
    }

    private void updateDataSource(final VersionableResource createdResource, final String resourceType) {
        Preconditions.checkNotNull(createdResource, "createdResource is null");
        Preconditions.checkNotNull(resourceType, "resourceType is null");
        // Preconditions.checkNotNull(treeDataSource, "treeDataSource is null");
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
