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

import com.google.common.base.Preconditions;

import com.vaadin.ui.Window;

import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.TreeDataSourceImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.listeners.TreeClickListener;
import org.escidoc.browser.ui.listeners.TreeExpandListener;
import org.escidoc.browser.ui.orgunit.OrgUnitTreeView;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class NavigationTreeBuilder {

    private final Repositories repositories;

    private final Window mainWindow;

    private final Router router;

    public NavigationTreeBuilder(final Window mainWindow, final Router router, final Repositories repositories) {
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);

        this.mainWindow = mainWindow;
        this.router = router;
        this.repositories = repositories;
    }

    public NavigationTreeView buildNavigationTree(final TreeDataSource treeDataSource) {
        Preconditions.checkNotNull(treeDataSource, "treeDataSource is null: %s", treeDataSource);
        return createNavigationTreeView(treeDataSource);
    }

    private NavigationTreeView createNavigationTreeView(final TreeDataSource treeDataSource) {
        final NavigationTreeView navigationTreeView = new ResourceTreeView();
        navigationTreeView.setDataSource(treeDataSource);
        navigationTreeView.addClickListener(new TreeClickListener(mainWindow, router));
        navigationTreeView.addExpandListener(new TreeExpandListener(repositories, treeDataSource));
        navigationTreeView.addActionHandler(new ActionHandlerImpl(mainWindow, repositories, treeDataSource, router));
        return navigationTreeView;
    }

    public NavigationTreeView buildContainerDirectMemberTree(final String parentId) throws EscidocClientException {
        Preconditions.checkNotNull(parentId, "parentID is null: %s", parentId);
        return createNavigationTreeView(withDataSource(repositories.container(), parentId));
    }

    private static TreeDataSource withDataSource(final Repository repository, final String parentId)
        throws EscidocClientException {
        final TreeDataSource treeDataSource = new TreeDataSourceImpl(repository.findTopLevelMembersById(parentId));
        treeDataSource.init();
        return treeDataSource;
    }

    public NavigationTreeView buildContextDirectMemberTree(final String parentId) throws EscidocClientException {
        Preconditions.checkNotNull(parentId, "parentId is null: %s", parentId);
        return createNavigationTreeView(withDataSource(repositories.context(), parentId));
    }

    public OrgUnitTreeView buildOrgUnitTree() {
        final OrgUnitTreeView tree = new OrgUnitTreeView();
        tree.setDataSource(getDataSource());
        addClickListener(tree);
        addExpandListener(tree);
        addActionListener(tree);
        return tree;
    }

    private void addActionListener(final OrgUnitTreeView tree) {
        tree.addActionHandler(new ActionHandlerImpl(mainWindow, repositories, getDataSource(), router));
    }

    private void addExpandListener(final OrgUnitTreeView tree) {
        tree.addExpandListener(new OrgUnitTreeExpandListener(repositories.organization(), mainWindow, getDataSource()));
    }

    private void addClickListener(final OrgUnitTreeView tree) {
        tree.addClickListener(new TreeClickListener(mainWindow, router));
    }

    private TreeDataSource getDataSource() {
        final OrgUnitDataSource treeDataSource = new OrgUnitDataSource(repositories.organization());
        treeDataSource.init();
        return treeDataSource;

    }
}