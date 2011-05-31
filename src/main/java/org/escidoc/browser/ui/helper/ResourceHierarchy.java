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
package org.escidoc.browser.ui.helper;

import java.util.ArrayList;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.internal.HasNoNameResourceImpl;
import org.escidoc.browser.repository.UtilRepository;
import org.escidoc.browser.repository.internal.UtilRepositoryImpl;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ResourceHierarchy {

    private final UtilRepository repository;

    private final ArrayList<ResourceModel> containerHierarchy = new ArrayList<ResourceModel>();

    public ResourceHierarchy(EscidocServiceLocation serviceLocation) {
        repository = new UtilRepositoryImpl(serviceLocation);
    }

    public ResourceModel getReturnParentOfItem(String id) throws Exception {
        final ResourceModel parent = repository.findParent(new HasNoNameResourceImpl(id, ResourceType.ITEM));
        return parent;
    }

    public ResourceModel getParentOfContainer(String id) throws EscidocClientException {
        final ResourceModel parent = repository.findParent(new HasNoNameResourceImpl(id, ResourceType.CONTAINER));
        return parent;
    }

    private void createContainerHierarchy(String id) {
        try {
            if (getParentOfContainer(id) != null) {
                containerHierarchy.add(getParentOfContainer(id));
                createContainerHierarchy(getParentOfContainer(id).getId());
            }
        }
        catch (EscidocClientException e) {
            System.out.print("q" + id);
        }
    }

    public ArrayList<ResourceModel> getHierarchy(String id) throws EscidocClientException {
        createContainerHierarchy(id);

        return containerHierarchy;
    }

}
