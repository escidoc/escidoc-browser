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

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

import javax.xml.transform.TransformerException;

import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.om.context.AdminDescriptor;

public class ContextAdminDescriptorsClickListener implements ClickListener {

    private AdminDescriptor adminDescriptor = null;

    private String content;

    private String wndname;

    private final Window mainWindow;

    private OrganizationalUnitRef organizationalUnitRef = null;

    public ContextAdminDescriptorsClickListener(AdminDescriptor adminDescriptor, Window mainWindow) {
        this.adminDescriptor = adminDescriptor;
        this.mainWindow = mainWindow;
        try {
            this.wndname = adminDescriptor.getName();
            this.content = adminDescriptor.getContentAsString();
        }
        catch (TransformerException e) {
            this.content = "No value is set";
        }
    }

    public ContextAdminDescriptorsClickListener(OrganizationalUnitRef organizationalUnitRef, Window mainWindow) {
        this.mainWindow = mainWindow;
        this.organizationalUnitRef = organizationalUnitRef;

        this.wndname = organizationalUnitRef.getXLinkTitle();
        this.content = organizationalUnitRef.getObjid();
    }

    @Override
    public void buttonClick(ClickEvent event) {
        Window subwindow = new Window(wndname);
        subwindow.setWidth("600px");
        subwindow.setModal(true);

        Label msgWindow = new Label(content, Label.CONTENT_RAW);

        subwindow.addComponent(msgWindow);
        if (subwindow.getParent() != null) {
            mainWindow.showNotification("Window is already open");
        }
        else {
            mainWindow.addWindow(subwindow);
        }
    }

}
