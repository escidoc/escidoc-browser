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
package org.escidoc.browser.ui.navigation;

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.TreeDataSourceImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.listeners.TreeClickListener;
import org.escidoc.browser.ui.listeners.TreeExpandListener;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class NavigationTreeBuilder {

    private final CurrentUser currentUser;

    private final EscidocServiceLocation serviceLocation;

    private final Repositories repositories;

    public NavigationTreeBuilder(final EscidocServiceLocation serviceLocation, final CurrentUser currentUser,
        final Repositories repositories) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        this.serviceLocation = serviceLocation;
        this.currentUser = currentUser;
        this.repositories = repositories;
    }

    public NavigationTreeView buildNavigationTree(
        final Router mainSite, final Window mainWindow, final TreeDataSource treeDataSource)
        throws EscidocClientException {

        final NavigationTreeView navigationTreeView = createNavigationTreeView(mainSite, mainWindow, treeDataSource);
        navigationTreeView.addExpandListener(new TreeExpandListener(repositories, treeDataSource));

        return navigationTreeView;
    }

    public NavigationTreeView buildContainerDirectMemberTree(
        final Router mainSite, final String parentID, final Window mainWindow) throws EscidocClientException {

        final TreeDataSource treeDataSource =
            new TreeDataSourceImpl(repositories.container().findTopLevelMembersById(parentID));
        treeDataSource.init();

        return createNavigationTreeView(mainSite, mainWindow, treeDataSource);
    }

    public NavigationTreeView buildContextDirectMemberTree(
        final Router mainSite, final String parentId, final Window mainWindow) throws EscidocClientException {

        final TreeDataSource treeDataSource =
            new TreeDataSourceImpl(repositories.context().findTopLevelMembersById(parentId));
        treeDataSource.init();

        return createNavigationTreeView(mainSite, mainWindow, treeDataSource);
    }

    private NavigationTreeView createNavigationTreeView(
        final Router router, final Window mainWindow, final TreeDataSource treeDataSource) {
        final NavigationTreeView navigationTreeView = new NavigationTreeViewImpl(repositories, currentUser);
        navigationTreeView.setDataSource(treeDataSource, router);
        navigationTreeView.addClickListener(new TreeClickListener(serviceLocation, repositories, mainWindow, router,
            currentUser));
        navigationTreeView.addActionHandler(new ActionHandlerImpl(mainWindow, repositories, currentUser,
            treeDataSource, router));
        return navigationTreeView;
    }
}
