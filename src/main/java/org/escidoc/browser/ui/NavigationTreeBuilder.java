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
 * Copyright ${year} Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui;

import java.util.List;

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceContainer;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.internal.ResourceContainerImpl;
import org.escidoc.browser.repository.ContextRepository;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.listeners.TreeClickListener;
import org.escidoc.browser.ui.listeners.TreeExpandListener;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class NavigationTreeBuilder {

    private final EscidocServiceLocation serviceLocation;

    private final CurrentUser currentUser;

    private TreeClickListener clickListener;

    public NavigationTreeBuilder(final EscidocServiceLocation serviceLocation, final CurrentUser currentUser) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);
        this.serviceLocation = serviceLocation;
        this.currentUser = currentUser;

    }

    public NavigationTreeView buildNavigationTree(
        final Repository contextRepository, final Repository containerRepository, final Repository itemRepository,
        final MainSite mainSite, final Window mainWindow) throws EscidocClientException {

        final NavigationTreeView navigationTreeView = new NavigationTreeViewImpl(containerRepository, serviceLocation);

        final ResourceContainer resourceContainer =
            new ResourceContainerImpl(((ContextRepository) contextRepository).findAllWithChildrenInfo());
        resourceContainer.init();

        clickListener =
            new TreeClickListener(serviceLocation, contextRepository, containerRepository, itemRepository, mainWindow,
                mainSite, currentUser);

        navigationTreeView.setDataSource(resourceContainer, mainSite);
        navigationTreeView.addExpandListener(new TreeExpandListener(contextRepository, containerRepository,
            resourceContainer));
        navigationTreeView.addClickListener(clickListener);

        return navigationTreeView;
    }

    public NavigationTreeView buildContextDirectMemberTree(
        final Repository contextRepository, final Repository containerRepository, final Repository itemRepository,
        final MainSite mainSite, final String parentID, final Window mainWindow) throws EscidocClientException {

        final NavigationTreeView navigationTreeView = new NavigationTreeViewImpl(containerRepository, serviceLocation);

        final List<ResourceModel> contexts = contextRepository.findTopLevelMembersById(parentID);

        final ResourceContainerImpl resourceContainer = new ResourceContainerImpl(contexts);
        resourceContainer.init();

        clickListener =
            new TreeClickListener(serviceLocation, contextRepository, containerRepository, itemRepository, mainWindow,
                mainSite, currentUser);

        navigationTreeView.setDataSource(resourceContainer, mainSite);
        navigationTreeView.addClickListener(clickListener);

        return navigationTreeView;
    }

    public NavigationTreeView buildContainerDirectMemberTree(
        final Repository contextRepository, final Repository containerRepository, final Repository itemRepository,
        final MainSite mainSite, final String parentID, final Window mainWindow) throws EscidocClientException {

        final NavigationTreeView navigationTreeView = new NavigationTreeViewImpl(containerRepository, serviceLocation);

        final List<ResourceModel> container = containerRepository.findTopLevelMembersById(parentID);

        final ResourceContainerImpl resourceContainer = new ResourceContainerImpl(container);
        resourceContainer.init();

        navigationTreeView.setDataSource(resourceContainer, mainSite);

        clickListener =
            new TreeClickListener(serviceLocation, contextRepository, containerRepository, itemRepository, mainWindow,
                mainSite, currentUser);
        navigationTreeView.addClickListener(clickListener);
        return navigationTreeView;
    }

}
