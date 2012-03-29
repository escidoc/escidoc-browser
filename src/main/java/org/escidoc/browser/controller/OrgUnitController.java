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
package org.escidoc.browser.controller;

import com.vaadin.ui.Window;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.OrgUnitProxy;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.OrgUnitView;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;

public class OrgUnitController extends Controller {

    public OrgUnitController(final Repositories repositories, final Router router, final ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        createView();
    }

    @Override
    public void createView() {
        view = new OrgUnitView(getRouter(), getResourceProxy(), this);
        ((OrgUnitView) view).buildContentPanel();
    }

    public void removeParent(String parentId) {
        try {
            getRepositories().organization().removeParent(resourceProxy, parentId);
            showTrayMessage("Updated!", "Parent was removed successfully");
        }
        catch (EscidocClientException e) {
            showError("Unable to remove. An error occurred " + e.getMessage());
        }
    }

    public void addParent(ResourceProxy resourceProxy, String parentId) {
        try {
            getRepositories().organization().addParent(resourceProxy, parentId);
            showTrayMessage("Updated!", "Organizational Unit was added Successfully");
        }
        catch (EscidocClientException e) {
            showError("Unable to add the OrganizationalUnit. An error occurred " + e.getLocalizedMessage());
        }
    }

    public void updateMetadata(MetadataRecord metadataRecord) throws EscidocClientException {
        repositories.organization().updateMetaData((OrgUnitProxy) resourceProxy, metadataRecord);
        // TODO Success message here

    }

    public MetadataRecord getMetadata(String metadataRecordName) {
        try {
            return repositories.organization().getMetadataRecord(resourceProxy.getId(), metadataRecordName);
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
        return null;

    }

    public void removeMetadata(String metadataRecordName) {
        try {
            repositories.organization().removeMD(resourceProxy.getId(), metadataRecordName);
            showTrayMessage(ViewConstants.MD_REMOVE, ViewConstants.ADMINDESCRIPTION_REMOVED);
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    public void addMetaData(MetadataRecord metadataRecord) {
        try {
            repositories.organization().addMetaData(resourceProxy, metadataRecord);
            showTrayMessage(ViewConstants.ADDED_SUCCESSFULLY, ViewConstants.ADDED_SUCCESSFULLY);
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }
}
