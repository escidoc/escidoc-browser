package org.escidoc.browser.model;

import de.escidoc.core.resources.Resource;

public class ItemModel extends AbstractResourceModel {

    public ItemModel(final Resource resource) {
        super(resource);
    }

    @Override
    public ResourceType getType() {
        return ResourceType.ITEM;
    }

    @Override
    public String toString() {
        return "ItemModel [getType()=" + getType() + ", getId()=" + getId() + ", getName()=" + getName() + "]";
    }

    public static boolean isItem(final ResourceModel resource) {
        return resource.getType().equals(ResourceType.ITEM);
    }

}
