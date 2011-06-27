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

import org.escidoc.browser.model.ContentModelService;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemModel;
import org.escidoc.browser.model.ResourceContainer;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.internal.ItemBuilder;
import org.escidoc.browser.repository.internal.ItemRepository;

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
import de.escidoc.core.resources.om.item.Item;

public class TreeCreateItem {

    public Object target;

    public String contextId;

    public EscidocServiceLocation serviceLocation;

    public ItemRepository itemRepository;

    public ResourceContainer resourceContainer;

    private TextField txtItemName;

    private Button btnAdd;

    private NativeSelect slcContentModl;

    private Window subwindow;

    private final Window mainWindow;

    public TreeCreateItem(Object target, String contextId, EscidocServiceLocation serviceLocation, Window window,
        ItemRepository containerRepo, ResourceContainer container) {
        this.target = target;
        this.contextId = contextId;
        this.serviceLocation = serviceLocation;
        this.mainWindow = window;
        this.itemRepository = containerRepo;
        this.resourceContainer = container;
    }

    // TODO heavy refactoring here
    public void createItem() {
        try {
            addItemForm();
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

    public void addItemForm() throws EscidocException, MalformedURLException, InternalClientException,
        TransportException {
        FormLayout frmAddCont = new FormLayout();
        frmAddCont.setImmediate(true);

        // textfield

        txtItemName = new TextField("Item name");

        txtItemName.setRequired(true);
        txtItemName.setRequiredError("Please enter an Item Name");
        txtItemName.addValidator(new StringLengthValidator("Item Name must be 3-25 characters", 3, 25, false));
        txtItemName.setImmediate(true);

        slcContentModl = new NativeSelect("Please select Content Model");
        slcContentModl.setRequired(true);
        ContentModelService cMS = new ContentModelService(serviceLocation);

        Collection<? extends Resource> contentModels = cMS.filterUsingInput("");
        for (Resource resource : contentModels) {
            slcContentModl.addItem(resource.getObjid());
        }

        // apply-button
        btnAdd = new Button("Add", this, "clickButtonaddContainer");

        frmAddCont.addComponent(txtItemName);
        frmAddCont.addComponent(slcContentModl);
        frmAddCont.addComponent(btnAdd);

        subwindow = new Window("Create Item");
        subwindow.setWidth("600px");
        subwindow.setModal(true);
        subwindow.addComponent(frmAddCont);
        mainWindow.addWindow(subwindow);
    }

    public void clickButtonaddContainer(ClickEvent event) {
        if (txtItemName.isValid() && slcContentModl.isValid()) {
            String containerName = txtItemName.getValue().toString();
            String contentModelId = (String) slcContentModl.getValue();
            // We really create the container here
            try {
                createNewItem(containerName, contentModelId, contextId);
            }
            catch (ParserConfigurationException e) {
                mainWindow.showNotification("Not able to create a new Item for you. Please contact the developers", 1);
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
     * @param itemContainer
     * @throws ParserConfigurationException
     * 
     */
    private void createNewItem(String itemName, String contentModelId, String contextId)
        throws ParserConfigurationException {

        ItemBuilder itmBuild = new ItemBuilder(new ContextRef(contextId), new ContentModelRef(contentModelId));
        Item newItem = itmBuild.build(itemName);

        try {
            Item create = itemRepository.create(newItem);
            resourceContainer.addChild((ResourceModel) target, new ItemModel(create));
            subwindow.getParent().removeWindow(subwindow);
        }
        catch (EscidocClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}