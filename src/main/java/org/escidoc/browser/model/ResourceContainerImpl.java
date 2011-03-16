package org.escidoc.browser.model;

import java.util.Collection;

import com.google.common.base.Preconditions;
import com.vaadin.data.Container;
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
        addGeneralProperties();
    }

    private void addGeneralProperties() {
        container.addContainerProperty(PropertyId.OBJECT_ID, String.class, "");
        container.addContainerProperty(PropertyId.NAME, String.class, "");
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
