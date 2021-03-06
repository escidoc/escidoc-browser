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
import org.escidoc.browser.model.internal.ItemProxyImpl;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.AddMetaDataFileItemComponentBehaviour;
import org.escidoc.browser.ui.listeners.EditMetaDataFileItemComponentBehaviour;

import com.google.common.base.Preconditions;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.om.item.component.Component;

@SuppressWarnings("serial")
public class ItemComponentMetadataVH extends TableContainerVH {

    private MetadataRecords mdList;

    private Router router;

    protected ItemController controller;

    private HierarchicalContainer tableContainer;

    private Action ACTION_DELETE = new Action("Delete Metadata");

    private Action ACTION_ADD = new Action("Add Metadata");

    private Action ACTION_EDIT = new Action("Edit Metadata");

    private Action[] ACTIONS_LIST = new Action[] { ACTION_ADD, ACTION_EDIT, ACTION_DELETE };

    private final ItemProxyImpl itemProxy;

    private final Component component;

    public ItemComponentMetadataVH(MetadataRecords mdList, ItemController controller, Router router,
        ItemProxyImpl itemProxy, Component component) {

        Preconditions.checkNotNull(router, "router is null.");
        Preconditions.checkNotNull(controller, "ItemController is null.");
        Preconditions.checkNotNull(itemProxy, "itemProxy is null: %s", itemProxy);
        Preconditions.checkNotNull(component, "component is null: %s", component);

        this.mdList = mdList;
        this.controller = controller;
        this.router = router;
        this.itemProxy = itemProxy;
        this.component = component;

    }

    public void buildTable() {
        table.setContainerDataSource(populateContainerTable());
        if (hasRightstoContextMenu()) {
            this.addActionLists();
        }
    }

    @Override
    protected void addActionLists() {
        // if (contextController.canUpdateContext()) {
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
                    new AddMetaDataFileItemComponentBehaviour(router.getMainWindow(), component, controller, itemProxy,
                        ItemComponentMetadataVH.this).showAddWindow();
                    controller.refreshView();
                }
                else if ((ACTION_EDIT == action) && (target != null)) {
                    MetadataRecord md = component.getMetadataRecords().get((String) target);
                    new EditMetaDataFileItemComponentBehaviour(md, router.getMainWindow(), component, controller,
                        itemProxy, ItemComponentMetadataVH.this).showWindow();
                    controller.refreshView();

                }

            }
        });
        // }
    }

    @Override
    protected boolean hasRightstoContextMenu() {
        return controller.canUpdateItem();
    }

    @Override
    protected void removeAction(Object target) {

        controller.removeComponentMetadata(target.toString(), itemProxy.getId(), component.getObjid());
        tableContainer.removeItem(target);
    }

    @Override
    protected HierarchicalContainer populateContainerTable() {
        tableContainer = new HierarchicalContainer();
        // Create container property for name
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_NAME, String.class, null);
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_LINK, Label.class, null);
        if (mdList != null) {
            for (final MetadataRecord metadataRecord : mdList) {
                Item item = tableContainer.addItem(metadataRecord.getName());
                if (item != null) {
                    item.getItemProperty(ViewConstants.PROPERTY_NAME).setValue(metadataRecord.getName());
                    item.getItemProperty(ViewConstants.PROPERTY_LINK).setValue(
                        new Label("<a href=\"" + router.getServiceLocation().getEscidocUri()
                            + metadataRecord.getXLinkHref() + "\" target=\"_blank\">View</a>", Label.CONTENT_RAW));
                }
            }
        }
        table.setColumnWidth(ViewConstants.PROPERTY_LINK, 40);
        return tableContainer;
    }

    @Override
    protected void initializeTable() {
        // size
        table.setWidth("100%");
        table.setHeight("100px");
        // selectable
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setImmediate(true); // react at once when something is selected
        // turn on column reordering and collapsing
        table.setColumnReorderingAllowed(true);
        table.setColumnCollapsingAllowed(true);
        table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
    }

    public void addNewItem(MetadataRecord metadataRecord) {
        // TODO this does not work!!!!
        createItem(tableContainer, metadataRecord.getName(), metadataRecord.getName(), metadataRecord.getXLinkHref());
        table.addItem(metadataRecord.getName());
        table.requestRepaint();
    }

}
