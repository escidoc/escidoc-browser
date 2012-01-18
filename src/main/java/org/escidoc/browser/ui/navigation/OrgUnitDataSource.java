package org.escidoc.browser.ui.navigation;

import com.google.common.base.Preconditions;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;

import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.ContextModel;
import org.escidoc.browser.repository.internal.OrganizationUnitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class OrgUnitDataSource implements TreeDataSource {

    private final static Logger LOG = LoggerFactory.getLogger(OrgUnitDataSource.class);

    private final OrganizationUnitRepository repository;

    private final HierarchicalContainer dataSource = new HierarchicalContainer();

    public OrgUnitDataSource(final OrganizationUnitRepository repository) {
        this.repository = repository;
    }

    @Override
    public void init() {
        addProperties();
        try {
            addTopLevel();
            sortByTypeAndNameAscending();
        }
        catch (final EscidocClientException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void addProperties() {
        dataSource.addContainerProperty(PropertyId.OBJECT_ID, String.class, "NO ID");
        dataSource.addContainerProperty(PropertyId.NAME, String.class, "NO NAME");
        dataSource.addContainerProperty(PropertyId.ICON, Resource.class, null);
        dataSource.addContainerProperty(PropertyId.TYPE, ResourceType.class, null);
    }

    private void addTopLevel() throws EscidocClientException {
        for (final ResourceModel topLevel : repository.findTopLevel()) {
            addTopLevelResource(topLevel);
        }
    }

    private Item add(final ResourceModel resource) {
        Preconditions.checkNotNull(resource, "resource is null: %s", resource);
        return dataSource.addItem(resource);
    }

    private void sortByTypeAndNameAscending() {
        dataSource.sort(new String[] { PropertyId.TYPE, PropertyId.NAME }, new boolean[] { true, true });
    }

    private static void bind(final Item item, final ResourceModel resource) {
        Preconditions.checkNotNull(item, "item is null: %s", item);
        Preconditions.checkNotNull(resource, "resource is null: %s", resource);

        item.getItemProperty(PropertyId.OBJECT_ID).setValue(resource.getId());
        item.getItemProperty(PropertyId.NAME).setValue(resource.getName());

        // FIXME add org unit icon
        if (notOrgUnit(resource)) {
            item.getItemProperty(PropertyId.ICON).setValue(
                new ThemeResource("images/resources/" + resource.getType().toString().toLowerCase() + ".png"));
        }
        item.getItemProperty(PropertyId.TYPE).setValue(resource.getType());
    }

    private static boolean notOrgUnit(final ResourceModel resource) {
        return !resource.getType().equals(ResourceType.ORG_UNIT);
    }

    private static boolean isChildless(final ContextModel topLevel) {
        return !topLevel.hasChildren();
    }

    @Override
    public int size() {
        Preconditions.checkNotNull(dataSource, "container is null: %s", dataSource);
        return dataSource.size();
    }

    @Override
    public Container getContainer() {
        Preconditions.checkNotNull(dataSource, "container is null: %s", dataSource);
        return dataSource;
    }

    @Override
    public void addChildren(final ResourceModel parent, final List<ResourceModel> children) {
        Preconditions.checkNotNull(parent, "parent is null: %s", parent);
        Preconditions.checkNotNull(children, "children is null: %s", children);

        if (children.isEmpty()) {
            dataSource.setChildrenAllowed(parent, false);
        }
        for (final ResourceModel child : children) {
            addChild(parent, child);
        }

        sortByTypeAndNameAscending();
    }

    @Override
    public void addChild(final ResourceModel parent, final ResourceModel child) {
        Preconditions.checkNotNull(parent, "parent is null: %s", parent);
        Preconditions.checkNotNull(child, "child is null: %s", child);
        final Item addedItem = add(child);
        if (isAlreadyAdded(addedItem)) {
            LOG.debug("Resource: " + child + " already added to the tree.");
            return;
        }
        bind(addedItem, child);
        assignParent(parent, child);

        final boolean hasMember = true;
        dataSource.setChildrenAllowed(child, hasMember);
        dataSource.setChildrenAllowed(child, isNotItem(child));
    }

    private static boolean isAlreadyAdded(final Item addedItem) {
        return addedItem == null;
    }

    private static boolean isNotItem(final ResourceModel child) {
        return !child.getType().equals(ResourceType.ITEM);
    }

    private void assignParent(final ResourceModel parent, final ResourceModel child) {
        dataSource.setChildrenAllowed(parent, true);
        final boolean isSuccesful = dataSource.setParent(child, parent);
        Preconditions.checkArgument(isSuccesful, "Setting parent of " + child + " to " + parent + " is not succesful.");
    }

    @Override
    public boolean remove(final ResourceModel resourceModel) {
        Preconditions.checkNotNull(resourceModel, "resourceModel is null: %s", resourceModel);
        switch (resourceModel.getType()) {
            case ITEM:
                return dataSource.removeItem(resourceModel);
            case CONTAINER:
                return dataSource.removeItem(resourceModel);
            default:
                throw new UnsupportedOperationException("Cannot remove resource with the type: "
                    + resourceModel.getType());
        }
    }

    /**
     * If no parent was found, return null
     */
    @Override
    public ResourceModel getParent(final ResourceModel child) {
        Preconditions.checkNotNull(child, "Child must not be null.");
        final Object parent = dataSource.getParent(child);
        if (parent != null) {
            return (ResourceModel) parent;
        }
        return null;
    }

    @Override
    public void reload() throws EscidocClientException {
        init();
    }

    @Override
    public void addTopLevelResource(ResourceModel topLevel) {
        final Item addedItem = add(topLevel);
        if (isAlreadyAdded(addedItem)) {
            return;
        }
        bind(addedItem, topLevel);

        if (topLevel.getType() == ResourceType.CONTEXT && isChildless((ContextModel) topLevel)) {
            dataSource.setChildrenAllowed(topLevel, false);
        }
    }

}
