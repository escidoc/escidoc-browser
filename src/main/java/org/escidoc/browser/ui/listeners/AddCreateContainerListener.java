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

import javax.xml.parsers.ParserConfigurationException;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public final class AddCreateContainerListener implements Button.ClickListener {

    private final TreeCreateContainer treeCreateContainer;

    AddCreateContainerListener(final TreeCreateContainer treeCreateContainer) {
        this.treeCreateContainer = treeCreateContainer;
    }

    @Override
    public void buttonClick(final ClickEvent event) {
        if (allValid()) {
            createContainerInRepository(getContainerName(), getContentModelId());
        }
        else {
            treeCreateContainer.mainWindow.showNotification("Please fill in all the required elements", 1);
        }

    }

    private String getContentModelId() {
        return (String) treeCreateContainer.contentModelSelect.getValue();
    }

    private String getContainerName() {
        return treeCreateContainer.nameField.getValue().toString();
    }

    private void createContainerInRepository(final String containerName, final String contentModelId) {
        try {
            treeCreateContainer.createNewContainer(containerName, contentModelId, treeCreateContainer.contextId);
        }
        catch (final ParserConfigurationException e) {
            treeCreateContainer.mainWindow.showNotification(
                "Not able to create a new Container for you. Please contact the developers", 1);
        }
    }

    private boolean allValid() {
        return treeCreateContainer.nameField.isValid() && treeCreateContainer.contentModelSelect.isValid();
    }
}