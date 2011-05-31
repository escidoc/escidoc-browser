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
package org.escidoc.browser.repository.internal;

import java.util.ArrayList;
import java.util.List;

import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceModelFactory;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.internal.HasNoNameResource;
import org.escidoc.browser.model.internal.HasNoNameResourceImpl;
import org.escidoc.browser.repository.ContainerProxy;
import org.escidoc.browser.repository.ContainerRepository;
import org.escidoc.browser.repository.ContextRepository;
import org.escidoc.browser.repository.ItemRepository;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.repository.UtilRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.om.container.Container;

public class UtilRepositoryImpl implements UtilRepository {

    private static final Logger LOG = LoggerFactory.getLogger(UtilRepositoryImpl.class);

    private final ContainerRepository containerRepository;

    private final ContextRepository contextRepository;

    private final Repository itemRepository;

    public UtilRepositoryImpl(final EscidocServiceLocation escidocServiceLocation) {
        Preconditions
            .checkNotNull(escidocServiceLocation, "escidocServiceLocation is null: %s", escidocServiceLocation);

        containerRepository = new ContainerRepository(escidocServiceLocation);
        contextRepository = new ContextRepository(escidocServiceLocation);
        itemRepository = new ItemRepository(escidocServiceLocation);

        resourceFactory = new ResourceModelFactory(itemRepository, containerRepository, contextRepository);
    }

    private final List<ResourceModel> path = new ArrayList<ResourceModel>();

    private final ResourceModelFactory resourceFactory;

    @Override
    public ResourceModel[] findAncestors(final HasNoNameResource resource) throws EscidocClientException {

        path.add(resourceFactory.find(resource.getId(), resource.getType()));

        final List<Container> parents = containerRepository.findParents(resource);

        if (parents.size() > 1) {
            LOG.warn("Found more than one parent: " + parents);
            throw new UnsupportedOperationException("Found more than one parent: " + parents);
        }
        else if (isContainer(parents)) {
            path.add(new ContainerModel(parents.get(0)));
            findAncestors(new HasNoNameResourceImpl(parents.get(0).getObjid(), ResourceType.CONTAINER));
        }
        else if (isContext(parents)) {
            return null;
        }

        throw new UnsupportedOperationException("Not yet implemented for parent");
    }

    @Override
    public ResourceModel findParent(final HasNoNameResource resource) throws EscidocClientException {

        Preconditions.checkNotNull(resource, "resource is null: %s", resource);
        Preconditions.checkArgument(
            resource.getType().equals(ResourceType.ITEM) || resource.getType().equals(ResourceType.CONTAINER),
            "Only Item and Container is supported");

        final List<Container> parents = containerRepository.findParents(resource);

        if (parents.size() > 1) {
            LOG.warn("Found more than one parent: " + parents);
            throw new UnsupportedOperationException("Found more than one parent: " + parents);
        }
        else if (parentIsContainer(parents)) {
            return new ContainerModel(parents.get(0));
        }
        else if (parentIsContext(parents)) {
            return containerRepository.findContext(resource);
        }
        return null;
        // throw new UnsupportedOperationException("Unsupported");
    }

    private boolean parentIsContainer(final List<Container> parents) {
        return parents.size() == 1;
    }

    private boolean parentIsContext(final List<Container> parents) {
        return parents.size() == 0;
    }

    private boolean isContext(final List<Container> parents) {
        return parentIsContext(parents);
    }

    private boolean isContainer(final List<Container> parents) {
        return parentIsContainer(parents);
    }

    @Override
    public Resource getParentContext(String id) {
        try {
            return ((ContainerProxy) containerRepository.findById(id)).getContext();
        }
        catch (EscidocClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
