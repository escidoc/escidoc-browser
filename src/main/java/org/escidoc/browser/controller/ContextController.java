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
package org.escidoc.browser.controller;

import com.google.common.base.Preconditions;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.maincontent.ContextView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ContextController extends Controller {
    private Repositories repositories;

    private Router router;

    private final ResourceProxy resourceProxy;

    private static final Logger LOG = LoggerFactory.getLogger(ContextController.class);

    public ContextController(final Repositories repositories, final Router router, final ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        this.resourceProxy = resourceProxy;
    }

    @Override
    protected Component createView(final ResourceProxy resourceProxy) throws EscidocClientException {
        Preconditions.checkNotNull(resourceProxy, "ResourceProxy is NULL");
        return new ContextView(router, resourceProxy, repositories, this);
    }

    public void addOrgUnitToContext(final ContextProxyImpl resourceProxy, final String orgUnitid) {
        try {
            repositories.context().addOrganizationalUnit(resourceProxy.getId(), orgUnitid);
            showTrayMessage("Updated!", "Organizational Unit was added Successfully");
        }
        catch (final EscidocClientException e) {
            showError("Unable to add the OrganizationalUnit. An error occurred " + e.getLocalizedMessage());
        }
    }

    public void removeOrgUnitFromContext(final ContextProxyImpl resourceProxy, final String orgUnitid) {
        try {
            repositories.context().delOrganizationalUnit(resourceProxy.getId(), orgUnitid);
            showTrayMessage("Updated!", "Organizational Unit was removed Successfully");
        }
        catch (final EscidocClientException e) {
            showError("Unable to add the OrganizationalUnit. An error occurred " + e.getLocalizedMessage());
        }
    }

    public boolean canRemoveOUs() {
        try {
            return repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.DELETE_ORGANIZATIONAL_UNIT_ACTION)
                .forResource(resourceProxy.getId()).permitted();
        }
        catch (final EscidocClientException e) {
            router.getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
        catch (final URISyntaxException e) {
            router.getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
    }

    public boolean canAddOUs() {
        try {
            return repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.CREATE_ORG_UNIT).forResource(resourceProxy.getId())
                .permitted();
        }
        catch (final EscidocClientException e) {
            router.getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
        catch (final URISyntaxException e) {
            router.getMainWindow().showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
    }

}
