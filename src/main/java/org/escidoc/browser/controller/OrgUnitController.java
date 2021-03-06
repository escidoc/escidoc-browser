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

import java.net.URISyntaxException;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.repository.internal.OrgUnitProxy;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.OrgUnitView;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.oum.OrganizationalUnit;

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
        Preconditions.checkNotNull(metadataRecord, "metadataRecord is null: %s", metadataRecord);
        repositories.organization().updateMetaData((OrgUnitProxy) resourceProxy, metadataRecord);
    }

    public MetadataRecord getMetadata(String metadataRecordName) {
        Preconditions.checkNotNull(metadataRecordName, "metadataRecordName is null: %s", metadataRecordName);
        try {
            return repositories.organization().getMetadataRecord(resourceProxy.getId(), metadataRecordName);
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
        return null;

    }

    public void removeMetadata(String metadataRecordName) {
        Preconditions.checkNotNull(metadataRecordName, "metadataRecordName is null: %s", metadataRecordName);
        try {
            repositories.organization().removeMD(resourceProxy.getId(), metadataRecordName);
            showTrayMessage(ViewConstants.MD_REMOVE, ViewConstants.ADMINDESCRIPTION_REMOVED);
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    public void addMetaData(MetadataRecord metadataRecord) {
        Preconditions.checkNotNull(metadataRecord, "metadataRecord is null: %s", metadataRecord);
        try {
            repositories.organization().addMetaData(resourceProxy, metadataRecord);
            showTrayMessage(ViewConstants.ADDED_SUCCESSFULLY, ViewConstants.ADDED_SUCCESSFULLY);
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    public boolean hasAccess() {
        try {
            return getRepositories()
                .pdp().forCurrentUser().isAction(ActionIdConstants.OPEN_ORG_UNIT)
                .forResource(getResourceProxy().getId()).permitted();
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
        catch (URISyntaxException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
    }

    public OrganizationalUnit openOU() {
        OrganizationalUnit oU;
        try {
            oU = router.getRepositories().organization().findOU(resourceProxy.getId());
            router.getRepositories().organization().open(oU);
            showTrayMessage("Opened!", "Organizational Unit was opened successfully!");
            return oU;
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;

    }

    public OrganizationalUnit closeOU() {
        OrganizationalUnit oU;
        try {
            oU = router.getRepositories().organization().findOU(resourceProxy.getId());
            router.getRepositories().organization().close(oU);
            showTrayMessage("Closed!", "Organizational Unit was closed successfully!");
            return oU;
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;
    }

    public OrganizationalUnit withdrawOU() {
        OrganizationalUnit oU;
        try {
            oU = router.getRepositories().organization().findOU(resourceProxy.getId());
            router.getRepositories().organization().withdraw(oU);
            showTrayMessage("Closed!", "Organizational Unit was closed successfully!");
            return oU;
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;
    }

}
