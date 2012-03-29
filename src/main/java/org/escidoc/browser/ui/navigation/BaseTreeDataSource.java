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
package org.escidoc.browser.ui.navigation;

import com.google.common.base.Preconditions;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;

import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.Resource;

public class BaseTreeDataSource implements TreeDataSource {

    private final static Logger LOG = LoggerFactory.getLogger(UserAccountDataSource.class);

    private final HierarchicalContainer dataSource = new HierarchicalContainer();

    private Repository r;

    public BaseTreeDataSource(Repository r) {
        Preconditions.checkNotNull(r, "r is null: %s", r);
        this.r = r;
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

    private void addTopLevel() throws EscidocClientException {
        for (final ResourceModel topLevel : r.findAll()) {
            addTopLevelResource(topLevel);
        }
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

    @Override
    public int size() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public Container getContainer() {
        return dataSource;
    }

    @Override
    public void addChildren(ResourceModel parent, List<ResourceModel> children) {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public void addChild(ResourceModel parent, ResourceModel child) {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public void addTopLevelResource(ResourceModel topLevel) {
        final Item addedItem = add(topLevel);
        if (isAlreadyAdded(addedItem)) {
            return;
        }
        bind(addedItem, topLevel);

        dataSource.setChildrenAllowed(topLevel, false);
    }

    private static boolean isAlreadyAdded(final Item addedItem) {
        return addedItem == null;
    }

    private Item add(final ResourceModel resource) {
        Preconditions.checkNotNull(resource, "resource is null: %s", resource);
        return dataSource.addItem(resource);
    }

    private static void bind(final Item item, final ResourceModel resource) {
        Preconditions.checkNotNull(item, "item is null: %s", item);
        Preconditions.checkNotNull(resource, "resource is null: %s", resource);

        item.getItemProperty(PropertyId.OBJECT_ID).setValue(resource.getId());
        item.getItemProperty(PropertyId.NAME).setValue(resource.getName());
        item.getItemProperty(PropertyId.TYPE).setValue(resource.getType());
    }

    @Override
    public boolean remove(ResourceModel resourceModel) {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public ResourceModel getParent(ResourceModel child) {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public void reload() throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

}