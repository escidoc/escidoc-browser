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
package org.escidoc.browser.repository;

import com.google.common.base.Preconditions;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.repository.internal.ContainerRepository;
import org.escidoc.browser.repository.internal.ContentModelRepository;
import org.escidoc.browser.repository.internal.ContextRepository;
import org.escidoc.browser.repository.internal.ItemRepository;
import org.escidoc.browser.repository.internal.OrganizationUnitRepository;
import org.escidoc.browser.repository.internal.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class BulkRepository {

    private static final Logger LOG = LoggerFactory.getLogger(BulkRepository.class);

    public class DeleteResult {

        private final List<ResourceModel> success = new ArrayList<ResourceModel>();

        private final Map<ResourceModel, String> fail = new HashMap<ResourceModel, String>();

        public void addSuccess(final ResourceModel rm) {
            success.add(rm);
        }

        public void addFail(final ResourceModel rm, final String msg) {
            fail.put(rm, msg);
        }

        public List<ResourceModel> getSuccess() {
            return new ArrayList<ResourceModel>(success);
        }

        public Map<ResourceModel, String> getFail() {
            return new HashMap<ResourceModel, String>(fail);
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("DeleteResult [");
            if (success != null) {
                builder.append("success=").append(success).append(", ");
            }
            if (fail != null) {
                builder.append("fail=").append(fail);
            }
            builder.append("]");
            return builder.toString();
        }

    }

    private final ItemRepository itemRepository;

    private final ContainerRepository containerRepository;

    private final ContextRepository contextRepository;

    private final Repository contentModelRepository;

    private final OrganizationUnitRepository orgUnitRepot;

    private final UserAccountRepository userAccountRepository;

    public BulkRepository(final ContextRepository contextRepository, final ContainerRepository containerRepository,
        final ItemRepository itemRepository, final ContentModelRepository contentModelRepository,
        OrganizationUnitRepository orgUnitRepo, UserAccountRepository userAccountRepository) {
        Preconditions.checkNotNull(contextRepository, "contextRepository is null: %s", contextRepository);
        Preconditions.checkNotNull(containerRepository, "containerRepository is null: %s", containerRepository);
        Preconditions.checkNotNull(itemRepository, "itemRepository is null: %s", itemRepository);
        Preconditions
            .checkNotNull(contentModelRepository, "contentModelRepository is null: %s", contentModelRepository);
        Preconditions.checkNotNull(orgUnitRepo, "Org Unit Repository is null: %s", orgUnitRepo);

        this.contextRepository = contextRepository;
        this.containerRepository = containerRepository;
        this.itemRepository = itemRepository;
        this.contentModelRepository = contentModelRepository;
        this.orgUnitRepot = orgUnitRepo;
        this.userAccountRepository = userAccountRepository;
    }

    public DeleteResult delete(final Set<ResourceModel> selectedResources) {
        Preconditions.checkNotNull(selectedResources, "selectedResources is null: %s", selectedResources);

        final DeleteResult result = new DeleteResult();

        for (final ResourceModel rm : selectedResources) {
            try {
                getRepoByType(rm.getType()).delete(rm.getId());
                result.addSuccess(rm);
            }
            catch (final EscidocClientException e) {
                LOG.warn("Can not delete " + rm + ". Message: " + e.getMessage(), e);
                result.addFail(rm, e.getMessage());
            }
        }
        return result;
    }

    private Repository getRepoByType(final ResourceType type) {
        switch (type) {
            case CONTEXT:
                return contextRepository;
            case CONTAINER:
                return containerRepository;
            case ITEM:
                return itemRepository;
            case CONTENT_MODEL:
                return contentModelRepository;
            case ORG_UNIT:
                return orgUnitRepot;
            case USER_ACCOUNT:
                return userAccountRepository;
            default:
                throw new UnsupportedOperationException("Not yet implemented " + type);
        }
    }
}
