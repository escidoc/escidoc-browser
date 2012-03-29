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
package org.escidoc.browser.layout;

import com.google.common.base.Preconditions;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import org.escidoc.browser.model.GroupModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.repository.GroupRepository;
import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class CreateGroupView {

    private static final String GROUP_NAME = "Group Name";

    private final static Logger LOG = LoggerFactory.getLogger(CreateGroupView.class);

    private static TextField nameField;

    private VerticalLayout modalWindowLayout;

    private GroupRepository groupRepo;

    private Window mainWindow;

    private TreeDataSource ds;

    private Window modalWindow;

    public CreateGroupView(GroupRepository groupRepo, Window mainWindow, TreeDataSource ds) {
        Preconditions.checkNotNull(groupRepo, "groupRepo is null: %s", groupRepo);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(ds, "ds is null: %s", ds);
        this.groupRepo = groupRepo;
        this.mainWindow = mainWindow;
        this.ds = ds;
    }

    public Window modalWindow() {
        final Window subwindow = buildModalWindow();
        addNameField(modalWindowLayout);
        addSaveButton(modalWindowLayout);
        return subwindow;
    }

    private Window buildModalWindow() {
        modalWindow = new Window(ViewConstants.CREATE_A_NEW_GROUP);
        modalWindow.setModal(true);

        modalWindowLayout = new VerticalLayout();
        modalWindow.setContent(modalWindowLayout);

        modalWindowLayout.setMargin(true);
        modalWindowLayout.setSpacing(true);

        modalWindowLayout.setWidth("400px");
        modalWindowLayout.setHeight("150px");
        return modalWindow;
    }

    private static void addNameField(VerticalLayout layout) {
        nameField = new TextField(ViewConstants.NAME);
        nameField.setWidth("300px");
        nameField.setRequired(true);
        nameField.setRequiredError("A group name is required.");
        layout.addComponent(nameField);
    }

    private void addSaveButton(VerticalLayout layout) {
        Button saveButton = new Button(ViewConstants.SAVE, new Button.ClickListener() {

            @Override
            public void buttonClick(@SuppressWarnings("unused") ClickEvent event) {
                try {
                    if (!nameField.isValid()) {
                        mainWindow.showNotification("A group name is required",
                            Window.Notification.TYPE_WARNING_MESSAGE);
                    }
                    GroupModel groupModel = new GroupModel(groupRepo.createGroup((String) nameField.getValue()));
                    ds.addTopLevelResource(groupModel);
                    mainWindow.removeWindow(modalWindow);
                }
                catch (EscidocClientException e) {
                    StringBuilder errorMessage = new StringBuilder();
                    errorMessage.append("Can not create a group. Reason: ");
                    errorMessage.append(e.getMessage());
                    LOG.warn(errorMessage.toString());
                    mainWindow.showNotification(ViewConstants.ERROR, errorMessage.toString(),
                        Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });
        layout.addComponent(saveButton);
    }
}