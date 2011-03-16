package org.escidoc.browser.model;

import java.util.Collection;

import com.google.common.base.Preconditions;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;

public class ResourceContainerImpl implements ResourceContainer {

    private final HierarchicalContainer container = new HierarchicalContainer();

    private final Collection<? extends ResourceModel> topLevelResources;

    public ResourceContainerImpl(
        final Collection<? extends ResourceModel> topLevelResources) {
        Preconditions.checkNotNull(topLevelResources,
            "topLevelResources is null: %s", topLevelResources);
        this.topLevelResources = topLevelResources;
    }

    public void init() {
        addProperties();
        addTopLevel();
    }

    private void addProperties() {
        container.addContainerProperty(PropertyId.OBJECT_ID, String.class, "");
        container.addContainerProperty(PropertyId.NAME, String.class, "");
    }

    private void addTopLevel() {
        assert (topLevelResources != null) : "top level resources is null";

        for (final ResourceModel topLevel : topLevelResources) {
            add(topLevel);
        }
    }

    private void add(final ResourceModel resource) {
        Preconditions.checkNotNull(resource, "resource is null: %s", resource);
        final Item item = container.addItem(resource);
        Preconditions.checkNotNull(item, "item is null: %s", item);
        bind(item, resource);
    }

    private void bind(final Item item, final ResourceModel resource) {
        Preconditions.checkNotNull(item, "item is null: %s", item);
        Preconditions.checkNotNull(resource, "resource is null: %s", resource);
        item.getItemProperty(PropertyId.OBJECT_ID).setValue(resource.getId());
        item.getItemProperty(PropertyId.NAME).setValue(resource.getName());
    }

    @Override
    public int size() {
        return container.size();
    }

    @Override
    public Container getContainer() {
        return container;
    }

}
