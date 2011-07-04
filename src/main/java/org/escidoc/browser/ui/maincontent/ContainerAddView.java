package org.escidoc.browser.ui.maincontent;

import java.net.MalformedURLException;

import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.ContainerBuilder;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.AddContainerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
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

    public void create() {
        createNewContainer(getContainerName(), getContentModelId(), getContextId());
    }

    private void createNewContainer(final String containerName, final String contentModelId, final String contextId) {
        final ContainerBuilder cntBuild =
            new ContainerBuilder(new ContextRef(contextId), new ContentModelRef(contentModelId));
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