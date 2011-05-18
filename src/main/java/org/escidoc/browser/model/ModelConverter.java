package org.escidoc.browser.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.escidoc.browser.repository.ContextRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.ResourceType;
import de.escidoc.core.resources.om.GenericVersionableResource;
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

    public final static List<ResourceModel> contextListToModelWithChildInfo(
        final Collection<Context> contextList, final ContextRepository repo) throws EscidocClientException {
        final List<ResourceModel> modelList = new ArrayList<ResourceModel>(contextList.size());
        for (final Resource context : contextList) {
            final ContextModel contextModel = new ContextModel(context);
            final boolean hasChildren = repo.hasChildren(context);
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

    public final static List<ResourceModel> genericResourcetoModel(
        final Collection<GenericVersionableResource> resources) {

        final List<ResourceModel> models = new ArrayList<ResourceModel>(resources.size());

        for (final Resource containerOrItem : resources) {
            createModelBasedOnType(models, containerOrItem);
        }

        return models;
    }

    private static void createModelBasedOnType(final List<ResourceModel> models, final Resource containerOrItem) {
        if (isContainer(containerOrItem)) {
            models.add(new ContainerModel(containerOrItem));
        }
        else if (isItem(containerOrItem)) {
            models.add(new ItemModel(containerOrItem));
        }
        else {
            LOG.error("Not yet implemented, if members of context other than Item or Container");
        }
    }

    private static boolean isContainer(final Resource containerOrItem) {
        return containerOrItem.getResourceType().equals(ResourceType.Container);
    }

    private static boolean isItem(final Resource containerOrItem) {
        return containerOrItem.getResourceType().equals(ResourceType.Item);
    }
}
