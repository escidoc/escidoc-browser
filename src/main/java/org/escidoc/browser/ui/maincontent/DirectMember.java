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
package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.ContainerRepository;
import org.escidoc.browser.repository.ContextRepository;
import org.escidoc.browser.repository.ItemRepository;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.NavigationTreeBuilder;
import org.escidoc.browser.ui.NavigationTreeView;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;

public class DirectMember {
    private final EscidocServiceLocation serviceLocation;

    private final String parentId;

    private final MainSite mainSite;

    private final Window mainWindow;

    private ContextRepository contextRepository;

    private ContainerRepository containerRepository;

    private ItemRepository itemRepository;

    private final NavigationTreeBuilder navigationTreeBuilder;

    private final CurrentUser currentUser;

    public DirectMember(final EscidocServiceLocation serviceLocation, final MainSite mainSite, final String parentId,
        final Window mainWindow, final CurrentUser currentUser) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);
        Preconditions.checkNotNull(parentId, "parentID is null: %s", parentId);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);
        this.serviceLocation = serviceLocation;
        this.parentId = parentId;
        this.mainSite = mainSite;
        this.mainWindow = mainWindow;
        this.currentUser = currentUser;

        initRepositories(serviceLocation);
        navigationTreeBuilder = new NavigationTreeBuilder(serviceLocation, currentUser, mainSite.getPdpService());
    }

    private void initRepositories(final EscidocServiceLocation serviceLocation) {
        contextRepository = new ContextRepository(serviceLocation);
        containerRepository = new ContainerRepository(serviceLocation);
        itemRepository = new ItemRepository(serviceLocation);
        tryLogin();
        checkPostConditions();
    }

    private void tryLogin() {
        try {
            contextRepository.loginWith(currentUser.getToken());
            containerRepository.loginWith(currentUser.getToken());
            itemRepository.loginWith(currentUser.getToken());
        }
        catch (final InternalClientException e) {
            mainWindow.showNotification("Can not Login. " + e.getMessage());
        }
    }

    private void checkPostConditions() {
        Preconditions.checkNotNull(contextRepository, "contextRepository is null: %s", contextRepository);
        Preconditions.checkNotNull(containerRepository, "containerRepository is null: %s", containerRepository);
        Preconditions.checkNotNull(itemRepository, "itemRepository is null: %s", itemRepository);
    }

    public NavigationTreeView contextAsTree() throws EscidocClientException {
        final NavigationTreeView tree =
            createContextDirectMembers(contextRepository, containerRepository, itemRepository);
        tree.setSizeFull();
        return tree;
    }

    private NavigationTreeView createContextDirectMembers(
        final ContextRepository contextRepository, final ContainerRepository containerRepository,
        final ItemRepository itemRepository) throws EscidocClientException {
        return navigationTreeBuilder.buildContextDirectMemberTree(contextRepository, containerRepository,
            itemRepository, mainSite, parentId, mainWindow);
    }

    public NavigationTreeView containerAsTree() throws EscidocClientException {
        final NavigationTreeView tree =
            createContainerDirectMembers(contextRepository, containerRepository, itemRepository);
        tree.setSizeFull();
        return tree;
    }

    private NavigationTreeView createContainerDirectMembers(
        final ContextRepository contextRepository, final ContainerRepository containerRepository,
        final ItemRepository itemRepository) throws EscidocClientException {

        return navigationTreeBuilder.buildContainerDirectMemberTree(contextRepository, containerRepository,
            itemRepository, mainSite, parentId, mainWindow);
    }

}
