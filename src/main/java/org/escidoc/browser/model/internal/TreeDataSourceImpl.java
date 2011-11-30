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
package org.escidoc.browser.model.internal;

import java.util.Collection;
import java.util.List;

import org.escidoc.browser.model.ContextModel;
import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.TreeDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;

public class TreeDataSourceImpl implements TreeDataSource {

    private static final Logger LOG = LoggerFactory.getLogger(TreeDataSourceImpl.class);

    private final HierarchicalContainer dataSource = new HierarchicalContainer();

    private final Collection<? extends ResourceModel> topLevelResources;

    public TreeDataSourceImpl(final Collection<? extends ResourceModel> topLevelResources) {
        Preconditions.checkNotNull(topLevelResources, "topLevelResources is null: %s", topLevelResources);
        this.topLevelResources = topLevelResources;
    }

    @Override
    public void init() {
        addProperties();
        addTopLevel();
        sortByTypeAndNameAscending();
    }

    private void addProperties() {
        dataSource.addContainerProperty(PropertyId.OBJECT_ID, String.class, "NO ID");
        dataSource.addContainerProperty(PropertyId.NAME, String.class, "NO NAME");
        dataSource.addContainerProperty(PropertyId.ICON, Resource.class, null);
        dataSource.addContainerProperty(PropertyId.TYPE, ResourceType.class, null);
    }

    private void sortByTypeAndNameAscending() {
        dataSource.sort(new String[] { PropertyId.TYPE, PropertyId.NAME }, new boolean[] { true, true });
    }

    private void addTopLevel() {
        for (final ResourceModel topLevel : topLevelResources) {
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

    private boolean isChildless(final ContextModel topLevel) {
        return !topLevel.hasChildren();
    }

    private Item add(final ResourceModel resource) {
        Preconditions.checkNotNull(resource, "resource is null: %s", resource);
        return dataSource.addItem(resource);
    }

    private void bind(final Item item, final ResourceModel resource) {
        Preconditions.checkNotNull(item, "item is null: %s", item);
        Preconditions.checkNotNull(resource, "resource is null: %s", resource);

        item.getItemProperty(PropertyId.OBJECT_ID).setValue(resource.getId());
        item.getItemProperty(PropertyId.NAME).setValue(resource.getName());
        item.getItemProperty(PropertyId.ICON).setValue(
            new ThemeResource("images/resources/" + resource.getType().toString().toLowerCase() + ".png"));
        item.getItemProperty(PropertyId.TYPE).setValue(resource.getType());
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
        LOG.debug("All Chilren added");
    }

    @Override
    public void addChild(final ResourceModel parent, final ResourceModel child) {
        final Item addedItem = add(child);
        if (isAlreadyAdded(addedItem)) {
            LOG.debug("Resource: " + child + " already added to the tree.");
            return;
        }
        bind(addedItem, child);
        assignParent(parent, child);

        dataSource.setChildrenAllowed(child, isNotItem(child));
        sortByTypeAndNameAscending();
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
    public void remove(final ResourceModel resourceModel) {
        Preconditions.checkNotNull(resourceModel, "resourceModel is null: %s", resourceModel);
        boolean isSuccessful;
        switch (resourceModel.getType()) {
            case ITEM:
                isSuccessful = dataSource.removeItem(resourceModel);
                Preconditions.checkArgument(isSuccessful, "Can not remove " + resourceModel + " from data source.");
                break;
            case CONTAINER:
                isSuccessful = dataSource.removeItem(resourceModel);
                Preconditions.checkArgument(isSuccessful, "Can not remove " + resourceModel + " from data source.");
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }
}