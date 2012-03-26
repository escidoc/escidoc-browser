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
package org.escidoc.browser.ui.helper;

import com.google.common.base.Preconditions;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.internal.HasNoNameResourceImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.UtilRepository;
import org.escidoc.browser.repository.internal.UtilRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ResourceHierarchy {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceHierarchy.class);

    private final UtilRepository utilRepository;

    private final List<ResourceModel> containerHierarchy = new ArrayList<ResourceModel>();

    public ResourceHierarchy(final EscidocServiceLocation serviceLocation, final Repositories repositories) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        utilRepository = new UtilRepositoryImpl(serviceLocation, repositories);
    }

    public List<ResourceModel> getHierarchy(final ResourceModel model) throws EscidocClientException {
        switch (model.getType()) {
            case CONTAINER:
                createContainerHierarchy(model.getId());
                return containerHierarchy;
            case ITEM:
                final ResourceModel parent = getParent(model);
                if (parent.getType().equals(ResourceType.CONTAINER)) {
                    createContainerHierarchy(parent.getId());
                }
                else {
                    containerHierarchy.add(parent);
                }
                return containerHierarchy;
            default:
                return Collections.emptyList();
        }
    }

    private ResourceModel getParent(final ResourceModel model) throws EscidocClientException {
        return utilRepository.findParent(new HasNoNameResourceImpl(model.getId(), model.getType()));
    }

    private void createContainerHierarchy(final String id) throws EscidocClientException {
        final ResourceModel parentOfContainer = getParentOfContainer(id);
        if (parentOfContainer != null) {
            containerHierarchy.add(parentOfContainer);
            if (parentOfContainer.getType().equals(ResourceType.CONTAINER)) {
                createContainerHierarchy(getParentOfContainer(id).getId());
            }
        }
    }

    private ResourceModel getParentOfContainer(final String id) throws EscidocClientException {
        return utilRepository.findParent(new HasNoNameResourceImpl(id, ResourceType.CONTAINER));
    }
}