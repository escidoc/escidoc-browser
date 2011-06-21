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
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.ContentModelService;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceContainer;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.internal.ContainerBuilder;
import org.escidoc.browser.repository.ContainerRepository;

import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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

    private final EscidocServiceLocation serviceLocation;

    private final Window mainWindow;

    private TextField txtContName;

    private Button btnAdd;

    private final Object target;

    private final ContainerRepository containerRepo;

    private NativeSelect slcContentModl;

    private Window subwindow;

    private final ResourceContainer resourceContainer;

    private final String contextId;

    /**
     * @param target
     * @param contextId
     * @param serviceLocation
     * @param window
     * @param tree
     * @param containerRepo
     * @param resourceContainer
     */
    public TreeCreateContainer(Object target, String contextId, EscidocServiceLocation serviceLocation, Window window,
        ContainerRepository containerRepo, ResourceContainer resourceContainer) {

        this.target = target;
        this.serviceLocation = serviceLocation;
        this.mainWindow = window;
        this.containerRepo = containerRepo;
        this.resourceContainer = resourceContainer;
        this.contextId = contextId;
    }

    // TODO heavy refactoring here
    public void createContainer() {
        try {
            addContainerForm();
        }
        catch (EscidocException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InternalClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (TransportException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addContainerForm() throws EscidocException, MalformedURLException, InternalClientException,
        TransportException {
        FormLayout frmAddCont = new FormLayout();
        frmAddCont.setImmediate(true);

        // textfield
        txtContName = new TextField("Container name");

        txtContName.setRequired(true);
        txtContName.setRequiredError("Please enter a Container Name");
        txtContName.addValidator(new StringLengthValidator("Container Name must be 3-25 characters", 3, 25, false));
        txtContName.setImmediate(true);

        slcContentModl = new NativeSelect("Please select Content Model");
        slcContentModl.setRequired(true);
        ContentModelService cMS = new ContentModelService(serviceLocation);

        Collection<? extends Resource> contentModels = cMS.filterUsingInput("");
        for (Resource resource : contentModels) {
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

    public void clickButtonaddContainer(ClickEvent event) {
        if (txtContName.isValid() && slcContentModl.isValid()) {
            String containerName = txtContName.getValue().toString();
            String contentModelId = (String) slcContentModl.getValue();

            // We really create the container here
            try {
                createNewContainer(containerName, contentModelId, contextId);
            }
            catch (ParserConfigurationException e) {
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
    private void createNewContainer(String containerName, String contentModelId, String contextId)
        throws ParserConfigurationException {

        ContainerBuilder cntBuild =
            new ContainerBuilder(new ContextRef(contextId), new ContentModelRef(contentModelId));
        Container newContainer = cntBuild.build(containerName);

        try {
            Container create = containerRepo.create(newContainer);
            resourceContainer.addChild((ResourceModel) target, new ContainerModel(create));
            subwindow.getParent().removeWindow(subwindow);
        }
        catch (EscidocClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
