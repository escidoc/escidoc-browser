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
 * Copyright 2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui.listeners;

import com.google.common.base.Preconditions;

import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.ContainerModel;
import org.escidoc.browser.model.internal.ContextModel;
import org.escidoc.browser.model.internal.ItemModel;
import org.escidoc.browser.repository.Repositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import de.escidoc.core.client.exceptions.EscidocClientException;

public final class TreeExpandListener implements Tree.ExpandListener {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(TreeExpandListener.class);

    private final Repositories repositories;

    private final TreeDataSource treeDataSource;

    public TreeExpandListener(final Repositories repositories, final TreeDataSource treeDataSource) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(treeDataSource, "treeDataSource is null: %s", treeDataSource);

        this.repositories = repositories;
        this.treeDataSource = treeDataSource;
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
            final List<ResourceModel> children = repositories.container().findTopLevelMembersById(resource.getId());
            treeDataSource.addChildren(resource, children);
        }
        catch (final EscidocClientException e) {
            handleError(resource, e);
        }
    }

    private void addContextChildren(final ResourceModel resource) {
        try {
            treeDataSource.addChildren(resource, repositories.context().findTopLevelMembersById(resource.getId()));
        }
        catch (final EscidocClientException e) {
            handleError(resource, e);
        }
    }

    // TODO: show notification to user, not just log.
    private static void handleError(final ResourceModel hasChildrenResource, final EscidocClientException e) {
        LOG.error("Can not find member of: " + hasChildrenResource.getId(), e);
    }
}