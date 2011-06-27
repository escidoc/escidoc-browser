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

import java.util.List;

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceContainer;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.internal.ResourceContainerImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.repository.StagingRepository;
import org.escidoc.browser.repository.internal.ContextRepository;
import org.escidoc.browser.service.PdpService;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.listeners.TreeClickListener;
import org.escidoc.browser.ui.listeners.TreeExpandListener;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class NavigationTreeBuilder {

    private final CurrentUser currentUser;

    private final EscidocServiceLocation serviceLocation;

    private final Repositories repositories;

    private TreeClickListener clickListener;

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
        final Repository contextRepository, final Repository containerRepository, final Repository itemRepository,
        final PdpService pdpService, final MainSite mainSite, final Window mainWindow,
        final StagingRepository stagingRepository) throws EscidocClientException {

        final NavigationTreeView navigationTreeView =
            new NavigationTreeViewImpl(repositories, serviceLocation, currentUser);

        final ResourceContainer resourceContainer =
            new ResourceContainerImpl(((ContextRepository) contextRepository).findAllWithChildrenInfo());
        resourceContainer.init();

        clickListener = new TreeClickListener(serviceLocation, repositories, mainWindow, mainSite, currentUser);

        navigationTreeView.setDataSource(resourceContainer, mainSite);
        navigationTreeView.addExpandListener(new TreeExpandListener(repositories, resourceContainer));
        navigationTreeView.addClickListener(clickListener);

        return navigationTreeView;
    }

    public NavigationTreeView buildContextDirectMemberTree(
        final Repository containerRepository, final Repository itemRepository, final MainSite mainSite,
        final String parentID, final Window mainWindow, final StagingRepository stagingRepository)
        throws EscidocClientException {

        final NavigationTreeView navigationTreeView =
            new NavigationTreeViewImpl(repositories, serviceLocation, currentUser);

        final List<ResourceModel> contexts = repositories.context().findTopLevelMembersById(parentID);

        final ResourceContainerImpl resourceContainer = new ResourceContainerImpl(contexts);
        resourceContainer.init();

        clickListener = new TreeClickListener(serviceLocation, repositories, mainWindow, mainSite, currentUser);

        navigationTreeView.setDataSource(resourceContainer, mainSite);
        navigationTreeView.addClickListener(clickListener);

        return navigationTreeView;
    }

    public NavigationTreeView buildContainerDirectMemberTree(
        final MainSite mainSite, final String parentID, final Window mainWindow) throws EscidocClientException {

        final NavigationTreeView navigationTreeView =
            new NavigationTreeViewImpl(repositories, serviceLocation, currentUser);

        final List<ResourceModel> container = repositories.container().findTopLevelMembersById(parentID);

        final ResourceContainerImpl resourceContainer = new ResourceContainerImpl(container);
        resourceContainer.init();

        navigationTreeView.setDataSource(resourceContainer, mainSite);

        clickListener = new TreeClickListener(serviceLocation, repositories, mainWindow, mainSite, currentUser);
        navigationTreeView.addClickListener(clickListener);
        return navigationTreeView;
    }

    public NavigationTreeView buildNavigationTree(final MainSite mainSite, final Window mainWindow)
        throws EscidocClientException {

        final NavigationTreeView navigationTreeView =
            new NavigationTreeViewImpl(repositories, serviceLocation, currentUser);

        final List<ResourceModel> contextWithChildren = repositories.context().findAllWithChildrenInfo();
        final ResourceContainer resourceContainer = new ResourceContainerImpl(contextWithChildren);
        resourceContainer.init();

        clickListener = new TreeClickListener(serviceLocation, repositories, mainWindow, mainSite, currentUser);

        navigationTreeView.setDataSource(resourceContainer, mainSite);
        navigationTreeView.addExpandListener(new TreeExpandListener(repositories, resourceContainer));
        navigationTreeView.addClickListener(clickListener);

        return navigationTreeView;
    }

    public NavigationTreeView buildContextDirectMemberTree(
        final MainSite mainSite, final String parentId, final Window mainWindow) throws EscidocClientException {

        final NavigationTreeView navigationTreeView =
            new NavigationTreeViewImpl(repositories, serviceLocation, currentUser);

        final List<ResourceModel> container = repositories.context().findTopLevelMembersById(parentId);

        final ResourceContainerImpl resourceContainer = new ResourceContainerImpl(container);
        resourceContainer.init();

        navigationTreeView.setDataSource(resourceContainer, mainSite);

        clickListener = new TreeClickListener(serviceLocation, repositories, mainWindow, mainSite, currentUser);
        navigationTreeView.addClickListener(clickListener);
        return navigationTreeView;
    }
}
