package org.escidoc.browser.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.ResourceType;
import de.escidoc.core.resources.om.GenericVersionableResource;
import de.escidoc.core.resources.om.context.Context;

public final class ModelConverter {
    private static final Logger LOG = LoggerFactory
        .getLogger(ModelConverter.class);

    private ModelConverter() {
        // Utility class;
    }

    public final static List<ResourceModel> toModel(
        final Collection<Context> contextsAsList) {
        final List<ResourceModel> models =
            new ArrayList<ResourceModel>(contextsAsList.size());
        for (final Resource context : contextsAsList) {
            models.add(new ContextModel(context));
        }
        return models;
    }

    public final static List<ResourceModel> genericResourcetoModel(
        final Collection<GenericVersionableResource> resources) {

        final List<ResourceModel> models =
            new ArrayList<ResourceModel>(resources.size());

        for (final Resource containerOrItem : resources) {
            createModelBasedOnType(models, containerOrItem);
        }

        return models;
    }

    private static void createModelBasedOnType(
        final List<ResourceModel> models, final Resource containerOrItem) {
        if (isContainer(containerOrItem)) {
            models.add(new ContainerModel(containerOrItem));
        }
        else if (isItem(containerOrItem)) {
            models.add(new ItemModel(containerOrItem));
        }
        else {
            LOG
                .error("Not yet implemented, if members of context other than Item or Container");
        }
    }

    private static boolean isContainer(final Resource containerOrItem) {
        return containerOrItem.getResourceType().equals(ResourceType.Container);
    }

    private static boolean isItem(final Resource containerOrItem) {
        return containerOrItem.getResourceType().equals(ResourceType.Item);
    }
}
