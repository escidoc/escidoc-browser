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
package org.escidoc.browser.model;

import org.escidoc.browser.repository.Repository;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ResourceModelFactory {

    private final Repository itemRepo;

    private final Repository containerRepo;

    private final Repository contextRepository;

    public ResourceModelFactory(final Repository itemRepo, final Repository containerRepo, Repository contextRepository) {
        Preconditions.checkNotNull(itemRepo, "itemRepo is null: %s", itemRepo);
        Preconditions.checkNotNull(containerRepo, "containerRepo is null: %s", containerRepo);
        Preconditions.checkNotNull(contextRepository, "contextRepository is null: %s", contextRepository);
        this.itemRepo = itemRepo;
        this.containerRepo = containerRepo;
        this.contextRepository = contextRepository;
    }

    public ResourceModel find(final String id, final ResourceType type) throws EscidocClientException {
        Preconditions.checkNotNull(id, "id is null: %s", id);
        Preconditions.checkNotNull(type, "type is null: %s", type);
        switch (type) {
            case ITEM: {
                return itemRepo.findById(id);
            }
            case CONTAINER: {
                return containerRepo.findById(id);
            }
            case CONTEXT: {
                return contextRepository.findById(id);
            }
            default:
                throw new UnsupportedOperationException("Not supported type: " + type);
        }
    }
}
