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
package org.escidoc.browser.ui.listeners;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import org.escidoc.browser.controller.ContextController;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.XmlUtil;

import org.escidoc.browser.controller.ContextController;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.XmlUtil;

import de.escidoc.core.resources.om.context.AdminDescriptor;

public class OnContextAdminDescriptor {
    private Router router;

    private ContextController controller;

    public OnContextAdminDescriptor(Router router, ContextController controller) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(controller, "contextController is null: %s", controller);
        this.router = router;
        this.controller = controller;
    }

    public void adminDescriptorForm() {
        final Window subwindow = new Window("A modal subwindow");
        subwindow.setModal(true);
        subwindow.setWidth("650px");
        VerticalLayout layout = (VerticalLayout) subwindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);

        final TextField txtName = new TextField("Name");
        txtName.setImmediate(true);
        txtName.setValidationVisible(true);
        final TextArea txtContent = new TextArea("Content");
        txtContent.setColumns(30);
        txtContent.setRows(40);

        Button addAdmDescButton = new Button("Add Description");
        addAdmDescButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {

                if (txtName.getValue().toString() == null) {
                    router.getMainWindow().showNotification(ViewConstants.PLEASE_ENTER_A_NAME,
                        Notification.TYPE_ERROR_MESSAGE);
                }
                else if (!XmlUtil.isWellFormed(txtContent.getValue().toString())) {
                    router.getMainWindow().showNotification(ViewConstants.XML_IS_NOT_WELL_FORMED,
                        Notification.TYPE_ERROR_MESSAGE);
                }
                else {
                    AdminDescriptor newAdmDesc =
                        controller.addAdminDescriptor(txtName.getValue().toString(), txtContent.getValue().toString());
                    (subwindow.getParent()).removeWindow(subwindow);
                    router.getMainWindow().showNotification("Addedd Successfully", Notification.TYPE_HUMANIZED_MESSAGE);
                }
            }
        });

        subwindow.addComponent(txtName);
        subwindow.addComponent(txtContent);
        subwindow.addComponent(addAdmDescButton);

        Button close = new Button(ViewConstants.CLOSE, new Button.ClickListener() {
            @Override
            public void buttonClick(@SuppressWarnings("unused") ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        layout.addComponent(close);
        layout.setComponentAlignment(close, Alignment.TOP_RIGHT);

        router.getMainWindow().addWindow(subwindow);

    }

    /**
     * Editing since it has a parameter
     * 
     * @param adminDescriptor
     */
    public void adminDescriptorForm(AdminDescriptor adminDescriptor) {

        // Editing
        final Window subwindow = new Window("A modal subwindow");
        subwindow.setModal(true);
        subwindow.setWidth("650px");

        VerticalLayout layout = (VerticalLayout) subwindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);

        final TextField txtName = new TextField("Name");
        txtName.setValue(adminDescriptor.getName());
        txtName.setImmediate(true);
        txtName.setValidationVisible(true);
        final TextArea txtContent = new TextArea("Content");
        txtContent.setColumns(30);
        txtContent.setRows(40);
        try {
            txtContent.setValue(adminDescriptor.getContentAsString());
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        txtContent.setColumns(30);

        Button addAdmDescButton = new Button("Add Description");
        addAdmDescButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {

                if (txtName.getValue().toString() == null) {
                    router.getMainWindow().showNotification(ViewConstants.PLEASE_ENTER_A_NAME,
                        Notification.TYPE_ERROR_MESSAGE);

                }
                else if (!XmlUtil.isWellFormed(txtContent.getValue().toString())) {
                    router.getMainWindow().showNotification(ViewConstants.XML_IS_NOT_WELL_FORMED,
                        Notification.TYPE_ERROR_MESSAGE);
                }
                else {
                    controller.addAdminDescriptor(txtName.getValue().toString(), txtContent.getValue().toString());
                    (subwindow.getParent()).removeWindow(subwindow);
                    router.getMainWindow().showNotification("Addedd Successfully", Notification.TYPE_HUMANIZED_MESSAGE);
                }
            }
        });

        subwindow.addComponent(txtName);
        subwindow.addComponent(txtContent);
        subwindow.addComponent(addAdmDescButton);

        Button close = new Button(ViewConstants.CLOSE, new Button.ClickListener() {
            @Override
            public void buttonClick(@SuppressWarnings("unused") ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        layout.addComponent(close);
        layout.setComponentAlignment(close, Alignment.TOP_RIGHT);

        router.getMainWindow().addWindow(subwindow);

    }
}
