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

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.ContextView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ContextController extends Controller {

    public ContextController(final Repositories repositories, final Router router, final ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        Preconditions.checkNotNull(resourceProxy, "ResourceProxy is NULL");
        Preconditions.checkNotNull(repositories, "repositories is NULL");
        Preconditions.checkNotNull(router, "Router is NULL");

        this.repositories = repositories;
        this.router = router;
        try {
            this.view = createView(resourceProxy);
        }
        catch (final EscidocClientException e) {
            router.getMainWindow().showNotification(
                ViewConstants.VIEW_ERROR_CANNOT_LOAD_VIEW + e.getLocalizedMessage(), Notification.TYPE_ERROR_MESSAGE);
            LOG.error("Failed at: ", e.getStackTrace());
        }
        this.setResourceName(resourceProxy.getName() + "#" + resourceProxy.getId());
    }

    private final Repositories repositories;

    private final Router router;

    private static Logger LOG = LoggerFactory.getLogger(ContextController.class);

    private Component createView(final ResourceProxy resourceProxy) throws EscidocClientException {
        Preconditions.checkNotNull(resourceProxy, "ResourceProxy is NULL");
        return new ContextView(router, resourceProxy, repositories);
    }

}
