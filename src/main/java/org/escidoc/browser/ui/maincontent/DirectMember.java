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
package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.navigation.NavigationTreeBuilder;
import org.escidoc.browser.ui.navigation.NavigationTreeView;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class DirectMember {

    private final String parentId;

    private final MainSite mainSite;

    private final Window mainWindow;

    private final NavigationTreeBuilder navigationTreeBuilder;

    private final Repositories repositories;

    // private ContainerRepository containerRepository;

    // private ItemRepository itemRepository;

    public DirectMember(final EscidocServiceLocation serviceLocation, final MainSite mainSite, final String parentId,
        final Window mainWindow, final CurrentUser currentUser, final Repositories repositories) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);
        Preconditions.checkNotNull(parentId, "parentID is null: %s", parentId);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);

        this.parentId = parentId;
        this.mainSite = mainSite;
        this.mainWindow = mainWindow;
        this.repositories = repositories;
        navigationTreeBuilder = new NavigationTreeBuilder(serviceLocation, currentUser, repositories);
    }

    public NavigationTreeView contextAsTree() throws EscidocClientException {
        final NavigationTreeView tree = createContextDirectMembers();
        tree.setSizeFull();
        return tree;
    }

    private NavigationTreeView createContextDirectMembers() throws EscidocClientException {
        return navigationTreeBuilder.buildContextDirectMemberTree(mainSite, parentId, mainWindow);
    }

    public NavigationTreeView containerAsTree() throws EscidocClientException {
        final NavigationTreeView tree = createContainerDirectMembers();
        tree.setSizeFull();
        return tree;
    }

    private NavigationTreeView createContainerDirectMembers() throws EscidocClientException {
        return navigationTreeBuilder.buildContainerDirectMemberTree(mainSite, parentId, mainWindow);

    }
}