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
package org.escidoc.browser.ui.navigation.menubar;

import java.net.MalformedURLException;

import org.escidoc.browser.model.ItemModel;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.ItemBuilder;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.ViewConstants;
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
import de.escidoc.core.resources.om.item.Item;

public class ItemAddView {

    private static final Logger LOG = LoggerFactory.getLogger(ItemAddView.class);

    private final FormLayout addForm = new FormLayout();

    private final TextField nameField = new TextField(ViewConstants.ITEM_NAME);

    private final NativeSelect contentModelSelect = new NativeSelect(ViewConstants.PLEASE_SELECT_CONTENT_MODEL);

    private final Window subwindow = new Window(ViewConstants.CREATE_ITEM);

    private final Repositories repositories;

    private final Window mainWindow;

    private final ResourceModel parent;

    private final TreeDataSource treeDataSource;

    private final String contextId;

    private Button addButton;

    public ItemAddView(final Repositories repositories, final Window mainWindow, final ResourceModel parent,
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

    public void buildForm() throws MalformedURLException, EscidocClientException {
        addForm.setImmediate(true);
        addNameField();
        addContentModelSelect();
        addButton();
    }

    private void addNameField() {
        nameField.setRequired(true);
        nameField.setRequiredError(ViewConstants.PLEASE_ENTER_AN_ITEM_NAME);
        nameField
            .addValidator(new StringLengthValidator(ViewConstants.ITEM_NAME_MUST_BE_3_25_CHARACTERS, 3, 25, false));
        nameField.setImmediate(true);
        addForm.addComponent(nameField);
    }

    private void addContentModelSelect() throws MalformedURLException, EscidocException, InternalClientException,
        TransportException {
        Preconditions.checkNotNull(repositories.contentModel(), "ContentModelRepository is null: %s",
            repositories.contentModel());
        contentModelSelect.setRequired(true);
        for (final Resource resource : repositories.contentModel().findPublicOrReleasedResources()) {
            contentModelSelect.addItem(resource.getObjid());
        }
        addForm.addComponent(contentModelSelect);
    }

    private void addButton() {
        addButton = new Button(ViewConstants.ADD, new AddItemListener(this));
        addForm.addComponent(addButton);
    }

    private void buildSubWindowUsingForm() {
        subwindow.setWidth("600px");
        subwindow.setModal(true);
        subwindow.addComponent(addForm);
    }

    public void openSubWindow() throws MalformedURLException, EscidocClientException {
        buildForm();
        buildSubWindowUsingForm();
        mainWindow.addWindow(subwindow);
    }

    public boolean allValid() {
        return nameField.isValid() && contentModelSelect.isValid();
    }

    public String getContentModelId() {
        return (String) contentModelSelect.getValue();
    }

    public String getName() {
        return nameField.getValue().toString();
    }

    public void showRequiredMessage() {
        mainWindow.showNotification("Please fill in all the required elements", 1);
    }

    public void create() {
        createNewResource();
    }

    private void createNewResource() {
        try {
            final Item createdItem = repositories.item().createWithParent(buildItem(), parent);
            treeDataSource.addChild(parent, new ItemModel(createdItem));
            subwindow.getParent().removeWindow(subwindow);
        }
        catch (final EscidocClientException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private Item buildItem() {
        return new ItemBuilder(new ContextRef(getContextId()), new ContentModelRef(getContentModelId()))
            .build(getName());
    }

    private String getContextId() {
        return contextId;
    }
}
