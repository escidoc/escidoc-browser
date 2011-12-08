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
package org.escidoc.browser.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.repository.internal.ContainerRepository;
import org.escidoc.browser.repository.internal.ContentModelRepository;
import org.escidoc.browser.repository.internal.ContextRepository;
import org.escidoc.browser.repository.internal.ItemRepository;
import org.jfree.util.Log;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class BulkRepository {

    public class DeleteResult {

        private List<ResourceModel> success = new ArrayList<ResourceModel>();

        private Map<ResourceModel, String> fail = new HashMap<ResourceModel, String>();

        public void addSuccess(ResourceModel rm) {
            success.add(rm);
        }

        public void addFail(ResourceModel rm, String msg) {
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
            StringBuilder builder = new StringBuilder();
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

    private ItemRepository itemRepository;

    private ContainerRepository containerRepository;

    private ContextRepository contextRepository;

    private Repository contentModelRepository;

    public BulkRepository(ContextRepository contextRepository, ContainerRepository containerRepository,
        ItemRepository itemRepository, ContentModelRepository contentModelRepository) {
        Preconditions.checkNotNull(contextRepository, "contextRepository is null: %s", contextRepository);
        Preconditions.checkNotNull(containerRepository, "containerRepository is null: %s", containerRepository);
        Preconditions.checkNotNull(itemRepository, "itemRepository is null: %s", itemRepository);
        Preconditions
            .checkNotNull(contentModelRepository, "contentModelRepository is null: %s", contentModelRepository);

        this.contextRepository = contextRepository;
        this.containerRepository = containerRepository;
        this.itemRepository = itemRepository;
        this.contentModelRepository = contentModelRepository;
    }

    public DeleteResult delete(Set<ResourceModel> selectedResources) {
        Preconditions.checkNotNull(selectedResources, "selectedResources is null: %s", selectedResources);

        DeleteResult result = new DeleteResult();

        for (ResourceModel rm : selectedResources) {
            Repository repo = getRepoByType(rm.getType());
            try {
                repo.delete(rm.getId());
                result.addSuccess(rm);
            }
            catch (EscidocClientException e) {
                Log.warn("Can not delete " + rm + ". Message: " + e.getMessage(), e);
                result.addFail(rm, e.getMessage());
            }
        }
        return result;
    }

    private Repository getRepoByType(ResourceType type) {
        switch (type) {
            case CONTEXT:
                return contextRepository;
            case CONTAINER:
                return containerRepository;
            case ITEM:
                return itemRepository;
            case CONTENT_MODEL:
                return contentModelRepository;
            default:
                throw new UnsupportedOperationException("Not yet implemented " + type);
        }
    }
}
