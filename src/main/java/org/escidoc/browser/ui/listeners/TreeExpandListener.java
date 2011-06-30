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
package org.escidoc.browser.ui.listeners;

import java.util.List;

import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.ContextModel;
import org.escidoc.browser.model.ItemModel;
import org.escidoc.browser.model.ResourceContainer;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public final class TreeExpandListener implements Tree.ExpandListener {

    private static final Logger LOG = LoggerFactory.getLogger(TreeExpandListener.class);

    private final Repository contextRepository;

    private final Repository containerRepository;

    private final ResourceContainer resourceContainer;

    public TreeExpandListener(final Repositories repositories, final ResourceContainer resourceContainer) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(resourceContainer, "resourceContainer is null: %s", resourceContainer);

        contextRepository = repositories.context();
        containerRepository = repositories.container();
        this.resourceContainer = resourceContainer;
    }

    @Override
    public void nodeExpand(final ExpandEvent event) {
        final ResourceModel resource = (ResourceModel) event.getItemId();

        if (ContextModel.isContext(resource)) {
            addContextChildren(resource);
        }
        else if (ContainerModel.isContainer(resource)) {
            addContainerChildren(resource);
        }
        else if (ItemModel.isItem(resource)) {
            LOG.debug("do nothing, an item does not have any members.");
        }
        else {
            throw new UnsupportedOperationException("Unknown Type: " + resource);
        }

    }

    private void addContainerChildren(final ResourceModel resource) {
        try {
            final List<ResourceModel> children = containerRepository.findTopLevelMembersById(resource.getId());
            resourceContainer.addChildren(resource, children);
        }
        catch (final EscidocClientException e) {
            showErrorMessageToUser(resource, e);
        }
    }

    private void addContextChildren(final ResourceModel resource) {
        try {
            final List<ResourceModel> children = contextRepository.findTopLevelMembersById(resource.getId());
            resourceContainer.addChildren(resource, children);
        }
        catch (final EscidocClientException e) {
            showErrorMessageToUser(resource, e);
        }
    }

    // TODO: show notification to user, not just log.
    private void showErrorMessageToUser(final ResourceModel hasChildrenResource, final EscidocClientException e) {
        LOG.error("Can not find member of: " + hasChildrenResource.getId(), e);
    }
}