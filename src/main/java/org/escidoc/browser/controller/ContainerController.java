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
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.maincontent.ContainerView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;

public class ContainerController extends Controller {
    private static final Logger LOG = LoggerFactory.getLogger(ContainerController.class);

    public ContainerController(final Repositories repositories, final Router router, final ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        createView();
    }

    @Override
    public void createView() {
        try {
            view = new ContainerView(getRouter(), getResourceProxy(), getRepositories(), this);
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification("Error cannot create view: ", e.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    public void removeMetadata(String mdId) {
        try {
            repositories.container().removeMetadata(resourceProxy.getId(), mdId);
            showTrayMessage("Removed!", "Metadata" + mdId + " was removed successfully!");
        }
        catch (EscidocClientException e) {
            showError("Unable to remove metadata " + e.getLocalizedMessage());
        }

    }

    public boolean hasAccess() {
        try {
            return repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_CONTAINER).forResource(resourceProxy.getId())
                .permitted();
        }
        catch (final EscidocClientException e) {
            LOG.debug("No Access" + e.getLocalizedMessage());
            return false;
        }
        catch (final URISyntaxException e) {
            LOG.debug("Wrong URI " + e.getLocalizedMessage());
            return false;
        }
    }

    public MetadataRecord getMetadata(String name) {
        Preconditions.checkNotNull(name, "name is null: %s", name);
        try {
            return repositories.container().getMetadataRecord(resourceProxy.getId(), name);
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
        return null;

    }

    public void updateMetadata(MetadataRecord metadataRecord) throws EscidocClientException {
        Preconditions.checkNotNull(metadataRecord, "metadataRecord is null: %s", metadataRecord);
        repositories.container().updateMetaData(resourceProxy, metadataRecord);
    }
}