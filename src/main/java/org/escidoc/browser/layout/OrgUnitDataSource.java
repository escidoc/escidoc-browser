///**
// * CDDL HEADER START
// *
// * The contents of this file are subject to the terms of the
// * Common Development and Distribution License, Version 1.0 only
// * (the "License").  You may not use this file except in compliance
// * with the License.
// *
// * You can obtain a copy of the license at license/ESCIDOC.LICENSE
// * or https://www.escidoc.org/license/ESCIDOC.LICENSE .
// * See the License for the specific language governing permissions
// * and limitations under the License.
// *
// * When distributing Covered Code, include this CDDL HEADER in each
// * file and include the License file at license/ESCIDOC.LICENSE.
// * If applicable, add the following below this CDDL HEADER, with the
// * fields enclosed by brackets "[]" replaced with your own identifying
// * information: Portions Copyright [yyyy] [name of copyright owner]
// *
// * CDDL HEADER END
// *
// *
// *
// * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft
// * fuer wissenschaftlich-technische Information mbH and Max-Planck-
// * Gesellschaft zur Foerderung der Wissenschaft e.V.
// * All rights reserved.  Use is subject to license terms.
// */
//package org.escidoc.browser.layout;
//
//import java.util.List;
//
//import org.escidoc.browser.model.PropertyId;
//import org.escidoc.browser.model.ResourceModel;
//import org.escidoc.browser.model.ResourceType;
//import org.escidoc.browser.model.TreeDataSource;
//import org.escidoc.browser.repository.internal.OrganizationUnitRepository;
//import org.jfree.util.Log;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.google.common.base.Preconditions;
//import com.vaadin.data.Container;
//import com.vaadin.data.Item;
//import com.vaadin.data.util.HierarchicalContainer;
//import com.vaadin.terminal.Resource;
//
//import de.escidoc.core.client.exceptions.EscidocClientException;
//
//public class OrgUnitDataSource implements TreeDataSource {
//
//    private static final Logger LOG = LoggerFactory.getLogger(OrgUnitDataSource.class);
//
//    private final HierarchicalContainer dataSource = new HierarchicalContainer();
//
//    private final OrganizationUnitRepository repository;
//
//    public OrgUnitDataSource(final OrganizationUnitRepository repository) {
//        Preconditions.checkNotNull(repository, "repository is null: %s", repository);
//        this.repository = repository;
//    }
//
//    @Override
//    public void init() {
//        addProperties();
//        addTopLevel();
//        sortByTypeAndNameAscending();
//    }
//
//    @Override
//    public int size() {
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    @Override
//    public Container getContainer() {
//        return dataSource;
//    }
//
//    @Override
//    public void addChildren(final ResourceModel parent, final List<ResourceModel> children) {
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    @Override
//    public void addChild(final ResourceModel parent, final ResourceModel child) {
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    @Override
//    public boolean remove(final ResourceModel resourceModel) {
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    private void sortByTypeAndNameAscending() {
//        dataSource.sort(new String[] { PropertyId.TYPE, PropertyId.NAME }, new boolean[] { true, true });
//    }
//
//    private void addTopLevel() {
//        try {
//            final List<ResourceModel> topLevelOrgUnitList = repository.findAll();
//            for (final ResourceModel rM : topLevelOrgUnitList) {
//                LOG.debug("" + rM);
//                final Item addedItem = add(rM);
//
//                if (isAlreadyAdded(addedItem)) {
//                    return;
//                }
//                bind(addedItem, rM);
//
//                dataSource.setChildrenAllowed(rM, true);
//            }
//        }
//        catch (final EscidocClientException e) {
//            Log.error("Error while fetching top level organizational unit.", e);
//            // FIXME show error to user.
//        }
//    }
//
//    private Item add(final ResourceModel resource) {
//        Preconditions.checkNotNull(resource, "resource is null: %s", resource);
//        return dataSource.addItem(resource);
//    }
//
//    private void bind(final Item item, final ResourceModel resource) {
//        Preconditions.checkNotNull(item, "item is null: %s", item);
//        Preconditions.checkNotNull(resource, "resource is null: %s", resource);
//
//        item.getItemProperty("id").setValue(resource.getId());
//        item.getItemProperty(PropertyId.NAME).setValue(resource.getName());
//        // item.getItemProperty(PropertyId.ICON).setValue(
//        // new ThemeResource("images/resources/" + resource.getType().toString().toLowerCase() + ".png"));
//        item.getItemProperty(PropertyId.TYPE).setValue(resource.getType());
//    }
//
//    private static boolean isAlreadyAdded(final Item addedItem) {
//        return addedItem == null;
//    }
//
//    private void addProperties() {
//        dataSource.addContainerProperty("id", String.class, "NO ID");
//        dataSource.addContainerProperty(PropertyId.NAME, String.class, "NO NAME");
//        dataSource.addContainerProperty(PropertyId.ICON, Resource.class, null);
//        dataSource.addContainerProperty(PropertyId.TYPE, ResourceType.class, null);
//    }
//
//    @Override
//    public ResourceModel getParent(final ResourceModel child) {
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
// }