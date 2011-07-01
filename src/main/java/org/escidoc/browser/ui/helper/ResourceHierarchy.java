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

import java.util.ArrayList;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.internal.HasNoNameResourceImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.UtilRepository;
import org.escidoc.browser.repository.internal.UtilRepositoryImpl;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ResourceHierarchy {

    private final UtilRepository repository;

    private final ArrayList<ResourceModel> containerHierarchy = new ArrayList<ResourceModel>();

    public ResourceHierarchy(final EscidocServiceLocation serviceLocation, final Repositories repositories) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        repository = new UtilRepositoryImpl(serviceLocation, repositories);
    }

    public ResourceModel getReturnParentOfItem(final String id) throws Exception {
        return repository.findParent(new HasNoNameResourceImpl(id, ResourceType.ITEM));
    }

    public ResourceModel getParentOfContainer(final String id) throws EscidocClientException {
        return repository.findParent(new HasNoNameResourceImpl(id, ResourceType.CONTAINER));
    }

    private void createContainerHierarchy(final String id) throws EscidocClientException {
        if (getParentOfContainer(id) != null) {
            containerHierarchy.add(getParentOfContainer(id));
            createContainerHierarchy(getParentOfContainer(id).getId());
        }
    }

    public ArrayList<ResourceModel> getHierarchy(final String id) throws EscidocClientException {
        createContainerHierarchy(id);
        return containerHierarchy;
    }

}
