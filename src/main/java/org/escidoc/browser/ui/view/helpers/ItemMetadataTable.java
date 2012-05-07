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
package org.escidoc.browser.ui.view.helpers;

import org.escidoc.browser.controller.ItemController;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.AddMetaDataFileItemBehaviour;
import org.escidoc.browser.ui.listeners.OnEditItemMetadata;
import org.escidoc.browser.ui.view.helpers.OrgUnitMetadataTable.Metadata;

import com.google.common.base.Preconditions;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.resources.common.MetadataRecord;

public class ItemMetadataTable extends TableContainerVH {

    protected ItemController controller;

    private HierarchicalContainer tableContainer;

    private Action ACTION_DELETE = new Action("Delete Metadata");

    private Action ACTION_ADD = new Action("Add Metadata");

    private Action ACTION_EDIT = new Action("Edit Metadata");

    private Action[] ACTIONS_LIST = new Action[] { ACTION_ADD, ACTION_EDIT, ACTION_DELETE };

    private Router router;

    private final Repositories repositories;

    private final ItemProxy resourceProxy;

    private HierarchicalContainer dataSource;

    public ItemMetadataTable(ItemController itemController, Router router, ItemProxy resourceProxy,
        Repositories repositories) {
        Preconditions.checkNotNull(itemController, "itemController is null: %s", itemController);
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);

        this.controller = itemController;
        this.router = router;
        this.resourceProxy = resourceProxy;
        this.repositories = repositories;
    }

    public void buildTable() {
        table.setContainerDataSource(populateContainerTable());
        if (hasRightstoContextMenu()) {
            this.addActionLists();
        }
    }

    @SuppressWarnings("serial")
    @Override
    protected void addActionLists() {
        table.addActionHandler(new Action.Handler() {
            @Override
            public Action[] getActions(Object target, Object sender) {
                return ACTIONS_LIST;
            }

            @Override
            public void handleAction(Action action, Object sender, Object target) {
                if ((ACTION_DELETE == action) && (target != null)) {
                    confirmActionWindow(target);
                }
                else if (ACTION_ADD == action) {
                    new AddMetaDataFileItemBehaviour(router.getMainWindow(), repositories, resourceProxy)
                        .showAddWindow();
                    controller.refreshView();
                }
                else if ((ACTION_EDIT == action) && (target != null)) {
                    new OnEditItemMetadata(router, controller, (Metadata) target).showEditWindow();
                    controller.refreshView();

                }

            }
        });
    }

    @Override
    protected boolean hasRightstoContextMenu() {
        return controller.hasAccess();
    }

    @Override
    protected void removeAction(Object target) {
        controller.removeMetadata(target.toString());
        tableContainer.removeItem(target);

    }

    @Override
    protected HierarchicalContainer populateContainerTable() {
        dataSource = new HierarchicalContainer();

        dataSource.addContainerProperty(ViewConstants.PROPERTY_NAME, String.class, null);
        dataSource.addContainerProperty(ViewConstants.PROPERTY_LINK, Link.class, null);

        for (final MetadataRecord metadataRecord : resourceProxy.getMetadataRecords()) {
            Metadata md = Metadata.newInstance(metadataRecord);
            Item item = dataSource.addItem(md);
            if (item != null) {
                item.getItemProperty(ViewConstants.PROPERTY_NAME).setValue(metadataRecord.getName());
                item.getItemProperty(ViewConstants.PROPERTY_LINK).setValue(buildLink(metadataRecord));
            }
        }

        return dataSource;
    }

    private Link buildLink(final MetadataRecord metadataRecord) {
        Link mdLink = new Link("View", new ExternalResource(buildUri(metadataRecord)));
        mdLink.setTargetName("_blank");
        mdLink.setStyleName(BaseTheme.BUTTON_LINK);
        mdLink.setDescription("Show metadata information in a separate window");
        return mdLink;
    }

    private String buildUri(final MetadataRecord metadataRecord) {
        StringBuilder builder = new StringBuilder();
        builder.append(router.getServiceLocation().getEscidocUri());
        builder.append(metadataRecord.getXLinkHref());
        return builder.toString();
    }

    @Override
    protected void initializeTable() {
        table.setWidth("100%");
        table.setSelectable(true);
        table.setImmediate(true);
        table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
        table.setColumnWidth(ViewConstants.PROPERTY_LINK, 40);
    }

}
