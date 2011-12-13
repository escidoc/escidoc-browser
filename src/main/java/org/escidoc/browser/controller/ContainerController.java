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

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.ContainerView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ContainerController extends Controller {

    private EscidocServiceLocation serviceLocation;

    private ResourceProxy resourceProxy;

    private Repositories repositories;

    private Window mainWindow;

    private Router router;

    private static Logger LOG = LoggerFactory.getLogger(ContainerController.class);

    public ContainerController(Repositories repositories, Router router, ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        this.router = router;
        this.serviceLocation = router.getServiceLocation();
        this.resourceProxy = resourceProxy;
        this.repositories = repositories;
        this.mainWindow = router.getMainWindow();

        try {
            this.view = createView(resourceProxy);
        }
        catch (EscidocClientException e) {
            mainWindow.showNotification(ViewConstants.VIEW_ERROR_CANNOT_LOAD_VIEW + e.getLocalizedMessage(),
                Notification.TYPE_ERROR_MESSAGE);
            LOG.error("Failed at: ", e.getStackTrace());
        }
        this.setResourceName(resourceProxy.getName() + "#" + resourceProxy.getId());
    }

    private Component createView(ResourceProxy resourceProxy) throws EscidocClientException {
        Preconditions.checkNotNull(resourceProxy, "ResourceProxy is NULL");
        return new ContainerView(router, resourceProxy, repositories);
    }
}
