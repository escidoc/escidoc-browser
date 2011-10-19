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
package org.escidoc.browser.elabsmodul.controller;

import java.util.Collections;
import java.util.List;

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.elabsmodul.interfaces.IBeanModel;
import org.escidoc.browser.elabsmodul.interfaces.ISaveAction;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.helper.ResourceHierarchy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class InvestigationSeriesController extends Controller implements ISaveAction {
    private static final Logger LOG = LoggerFactory.getLogger(InvestigationSeriesController.class);

    private EscidocServiceLocation serviceLocation;

    private Repositories repositories;

    private Router router;

    private ResourceProxy resourceProxy;

    private Window mainWindow;

    private CurrentUser currentUser;

    private InvestigationSeriesBean isb;

    @Override
    public void saveAction(IBeanModel dataBean) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void init(
        EscidocServiceLocation serviceLocation, Repositories repositories, Router router, ResourceProxy resourceProxy,
        Window mainWindow, CurrentUser currentUser) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(router, "mainSite is null: %s", router);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);
        Preconditions.checkArgument(resourceProxy instanceof ContainerProxy, "resourceProxy is not container proxy");

        this.serviceLocation = serviceLocation;
        this.repositories = repositories;
        this.router = router;
        this.resourceProxy = resourceProxy;
        this.mainWindow = mainWindow;
        this.currentUser = currentUser;

        // FIXME a little bit weird
        getResourceName(resourceProxy.getName());

        isb = resourceToBean();
        view = createView();
    }

    private InvestigationSeriesBean resourceToBean() {
        InvestigationSeriesBean isb = new InvestigationSeriesBean();
        return isb;
    }

    private Component createView() {
        return new InvestigationSeriesView((ContainerProxy) resourceProxy, isb, createBreadCrumbModel());
    }

    private List<ResourceModel> createBreadCrumbModel() {
        final ResourceHierarchy rs = new ResourceHierarchy(serviceLocation, repositories);
        try {
            List<ResourceModel> hierarchy = rs.getHierarchy(resourceProxy);
            Collections.reverse(hierarchy);
            hierarchy.add(resourceProxy);
            return hierarchy;
        }
        catch (EscidocClientException e) {
            LOG.error("Fatal error, could not load BreadCrumb " + e.getLocalizedMessage());
        }
        return Collections.emptyList();
    }
}