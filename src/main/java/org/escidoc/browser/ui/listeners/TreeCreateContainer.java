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

import java.net.MalformedURLException;

import javax.xml.parsers.ParserConfigurationException;

import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceContainer;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.internal.ContainerBuilder;
import org.escidoc.browser.repository.internal.ContainerRepository;
import org.escidoc.browser.repository.internal.ContentModelRepository;
import org.escidoc.browser.ui.ViewConstants;

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

/**
 * @author ajb
 * 
 */
public class TreeCreateContainer {

    private final FormLayout addContainerForm = new FormLayout();

    private final EscidocServiceLocation serviceLocation;

    final Window mainWindow;

    private final ResourceContainer resourceContainer;

    final String contextId;

    private final Object target;

    private final ContainerRepository containerRepository;

    NativeSelect contentModelSelect;

    private Window subwindow;

    private Button addButton;

    TextField nameField;

    public TreeCreateContainer(final Object target, final String contextId,
        final EscidocServiceLocation serviceLocation, final Window mainWindow,
        final ContainerRepository containerRepository, final ResourceContainer resourceContainer) {

        Preconditions.checkNotNull(target, "target is null: %s", target);
        Preconditions.checkNotNull(contextId, "contextId is null: %s", contextId);
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(containerRepository, "containerRepo is null: %s", containerRepository);
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);

        this.target = target;
        this.contextId = contextId;
        this.serviceLocation = serviceLocation;
        this.mainWindow = mainWindow;
        this.containerRepository = containerRepository;
        this.resourceContainer = resourceContainer;
    }

    public void createContainer() throws MalformedURLException, EscidocClientException {
        addContainerForm();
    }

    public void addContainerForm() throws MalformedURLException, EscidocClientException {
        addContainerForm.setImmediate(true);
        addNameField();
        addContentModelSelect();
        addButton();
        createSubWindow();
        openSubWindow();
    }

    private void openSubWindow() {
        mainWindow.addWindow(subwindow);
    }

    private void createSubWindow() {
        subwindow = new Window(ViewConstants.CREATE_CONTAINER);
        subwindow.setWidth("600px");
        subwindow.setModal(true);
        subwindow.addComponent(addContainerForm);
    }

    private void addButton() {
        addButton = new Button(ViewConstants.ADD, new AddCreateContainerListener(this));
        addContainerForm.addComponent(addButton);
    }

    private void addContentModelSelect() throws MalformedURLException, EscidocException, InternalClientException,
        TransportException {
        contentModelSelect = new NativeSelect(ViewConstants.PLEASE_SELECT_CONTENT_MODEL);
        contentModelSelect.setRequired(true);

        final ContentModelRepository cMS = new ContentModelRepository(serviceLocation);

        for (final Resource resource : cMS.findPublicOrReleasedResources()) {
            contentModelSelect.addItem(resource.getObjid());
        }

        addContainerForm.addComponent(contentModelSelect);
    }

    private void addNameField() {
        nameField = new TextField(ViewConstants.CONTAINER_NAME);
        nameField.setRequired(true);
        nameField.setRequiredError(ViewConstants.PLEASE_ENTER_A_CONTAINER_NAME);
        nameField.addValidator(new StringLengthValidator(ViewConstants.CONTAINER_NAME_MUST_BE_3_25_CHARACTERS, 3, 25,
            false));
        nameField.setImmediate(true);
        addContainerForm.addComponent(nameField);
    }

    /**
     * Creating a container Required: A Context Reference A Name A Content Model
     * 
     * @param contextId
     * @param contentModelId
     * @param containerName
     * @throws ParserConfigurationException
     * 
     */
    void createNewContainer(final String containerName, final String contentModelId, final String contextId)
        throws ParserConfigurationException {

        final ContainerBuilder cntBuild =
            new ContainerBuilder(new ContextRef(contextId), new ContentModelRef(contentModelId), resourceContainer);
        final Container newContainer = cntBuild.build(containerName);
        try {
            // final Container create = containerRepository.create(newContainer);
            final Container create =
                containerRepository.createWithParent(newContainer, ((ResourceModel) target).getId());
            resourceContainer.addChild((ResourceModel) target, new ContainerModel(create));
            subwindow.getParent().removeWindow(subwindow);
        }
        catch (final EscidocClientException e) {
            e.printStackTrace();
        }
    }
}