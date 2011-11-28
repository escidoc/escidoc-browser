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
package org.escidoc.browser.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.VersionableResource;
import de.escidoc.core.resources.cmm.ContentModel;
import de.escidoc.core.resources.om.container.Container;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.item.Item;

public final class ModelConverter {
    private static final Logger LOG = LoggerFactory.getLogger(ModelConverter.class);

    private ModelConverter() {
        // Utility class;
    }

    public final static List<ResourceModel> contextListToModel(final Collection<Context> contextList) {
        final List<ResourceModel> models = new ArrayList<ResourceModel>(contextList.size());
        for (final Resource context : contextList) {
            models.add(new ContextModel(context));
        }
        return models;
    }

    public static List<ResourceModel> contextListToModelWithChildInfo(final List<Context> contextList) {
        final List<ResourceModel> modelList = new ArrayList<ResourceModel>(contextList.size());
        for (final Resource context : contextList) {
            final ContextModel contextModel = new ContextModel(context);
            // final boolean hasChildren = repo.hasChildren(context);
            final boolean hasChildren = true;
            contextModel.hasChildren(hasChildren);
            modelList.add(contextModel);
        }
        return modelList;
    }

    public final static List<ResourceModel> containerListToModel(final Collection<Container> containerList) {
        final List<ResourceModel> models = new ArrayList<ResourceModel>(containerList.size());
        for (final Resource context : containerList) {
            models.add(new ContainerModel(context));
        }
        return models;
    }

    public final static List<ResourceModel> itemListToModel(final Collection<Item> itemList) {
        final List<ResourceModel> models = new ArrayList<ResourceModel>(itemList.size());
        for (final Resource item : itemList) {
            models.add(new ItemModel(item));
        }
        return models;
    }

    public final static List<ResourceModel> genericResourcetoModel(final Collection<VersionableResource> resources) {

        final List<ResourceModel> models = new ArrayList<ResourceModel>(resources.size());

        for (final Resource containerOrItem : resources) {
            createModelBasedOnType(models, containerOrItem);
        }

        return models;
    }

    private static void createModelBasedOnType(final List<ResourceModel> models, final Resource containerOrItem) {
        models.add(new ResourceModel() {

            @Override
            public org.escidoc.browser.model.ResourceType getType() {
                switch (containerOrItem.getResourceType()) {
                    case ITEM:
                        return org.escidoc.browser.model.ResourceType.ITEM;
                    case CONTAINER:
                        return org.escidoc.browser.model.ResourceType.CONTAINER;
                    case CONTEXT:
                        return org.escidoc.browser.model.ResourceType.CONTAINER;
                    default:
                        throw new IllegalArgumentException("Unknown type: " + containerOrItem.getResourceType());
                }
            }

            @Override
            public String getName() {
                return containerOrItem.getXLinkTitle();
            }

            @Override
            public String getId() {
                return containerOrItem.getObjid();
            }
        });
    }

    public static List<ResourceModel> contentModelListToModel(List<ContentModel> list) {
        final List<ResourceModel> models = new ArrayList<ResourceModel>(list.size());
        for (final Resource resource : list) {
            models.add(new ContentModelProxyImpl(resource));
        }
        return models;
    }

}
