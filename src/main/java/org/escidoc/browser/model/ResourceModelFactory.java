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
package org.escidoc.browser.model;

import com.google.common.base.Preconditions;

import org.escidoc.browser.repository.Repositories;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ResourceModelFactory {

    private final Repositories repositories;

    public ResourceModelFactory(final Repositories repositories) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        this.repositories = repositories;
    }

    public ResourceModel find(final String id, final ResourceType type) throws EscidocClientException {
        Preconditions.checkNotNull(id, "id is null: %s", id);
        Preconditions.checkNotNull(type, "type is null: %s", type);
        switch (type) {
            case ITEM: {
                return repositories.item().findById(id);
            }
            case CONTAINER: {
                return repositories.container().findById(id);
            }
            case CONTEXT: {
                return repositories.context().findById(id);
            }
            default:
                throw new UnsupportedOperationException("Not supported type: " + type);
        }
    }
}
