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

import com.google.common.base.Preconditions;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.common.reference.ContentModelRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.om.container.Container;

/**
 * @author ajb
 * 
 */
public class TreeCreateContainer {

    private final EscidocServiceLocation serviceLocation;

    private final Window mainWindow;

    private final ResourceContainer resourceContainer;

    private final String contextId;

    private final Object target;

    private final ContainerRepository containerRepository;

    private NativeSelect slcContentModl;

    private Window subwindow;

    private Button btnAdd;

    private TextField txtContName;

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
        final FormLayout frmAddCont = new FormLayout();
        frmAddCont.setImmediate(true);

        txtContName = new TextField("Container name");
        txtContName.setRequired(true);
        txtContName.setRequiredError("Please enter a Container Name");
        txtContName.addValidator(new StringLengthValidator("Container Name must be 3-25 characters", 3, 25, false));
        txtContName.setImmediate(true);

        slcContentModl = new NativeSelect("Please select Content Model");
        slcContentModl.setRequired(true);

        final ContentModelRepository cMS = new ContentModelRepository(serviceLocation);

        for (final Resource resource : cMS.findPublicOrReleasedResources()) {
            slcContentModl.addItem(resource.getObjid());
        }

        // apply-button
        btnAdd = new Button("Add", this, "clickButtonaddContainer");

        frmAddCont.addComponent(txtContName);
        frmAddCont.addComponent(slcContentModl);
        frmAddCont.addComponent(btnAdd);

        subwindow = new Window("Create Container");
        subwindow.setWidth("600px");
        subwindow.setModal(true);
        subwindow.addComponent(frmAddCont);
        mainWindow.addWindow(subwindow);
    }

    public void clickButtonaddContainer(final ClickEvent event) {
        if (txtContName.isValid() && slcContentModl.isValid()) {
            final String containerName = txtContName.getValue().toString();
            final String contentModelId = (String) slcContentModl.getValue();

            // We really create the container here
            try {
                createNewContainer(containerName, contentModelId, contextId);
            }
            catch (final ParserConfigurationException e) {
                mainWindow.showNotification(
                    "Not able to create a new Container for you. Please contact the developers", 1);
            }
        }
        else {
            mainWindow.showNotification("Please fill in all the required elements", 1);
        }

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
    private void createNewContainer(final String containerName, final String contentModelId, final String contextId)
        throws ParserConfigurationException {

        final ContainerBuilder cntBuild =
            new ContainerBuilder(new ContextRef(contextId), new ContentModelRef(contentModelId));
        final Container newContainer = cntBuild.build(containerName);

        try {
            final Container create = containerRepository.create(newContainer);
            resourceContainer.addChild((ResourceModel) target, new ContainerModel(create));
            subwindow.getParent().removeWindow(subwindow);
        }
        catch (final EscidocClientException e) {
            e.printStackTrace();
        }
    }

}
