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
package org.escidoc.browser.ui.view.helpers;

import com.google.common.base.Preconditions;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.BaseTheme;

import org.escidoc.browser.controller.OrgUnitController;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.OnAddOrgUnitMetadata;
import org.escidoc.browser.ui.maincontent.OnEditOrgUnitMetadata;

import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;

public class MetadataOUTableVH extends TableContainerVH {

    private Router router;

    private Action ACTION_DELETE = new Action("Delete ");

    private Action ACTION_ADD = new Action("Add ");

    private Action ACTION_EDIT = new Action("Edit");

    private Action[] ACTIONS_LIST = new Action[] { ACTION_ADD, ACTION_EDIT, ACTION_DELETE };

    private HierarchicalContainer tableContainer;

    private MetadataRecords mdList;

    private OrgUnitController controller;

    public MetadataOUTableVH(MetadataRecords mdList, OrgUnitController controller, Router router) {
        Preconditions.checkNotNull(mdList, "mdList is null: %s", mdList);
        Preconditions.checkNotNull(controller, "controller is null: %s", controller);
        this.mdList = mdList;
        this.controller = controller;
        this.router = router;
        table.setContainerDataSource(populateContainerTable());
    }

    @Override
    protected boolean hasRightstoContextMenu() {
        return true;
    }

    @Override
    protected void removeAction(Object target) {
        controller.removeMetadata(target.toString());
        tableContainer.removeItem(target);
    }

    @Override
    protected void addActionLists() {
        table.addActionHandler(new Action.Handler() {
            @Override
            public Action[] getActions(Object target, Object sender) {
                return ACTIONS_LIST;
            }

            @Override
            public void handleAction(Action action, Object sender, Object target) {
                if (ACTION_DELETE == action) {
                    confirmActionWindow(target);
                }
                else if (ACTION_EDIT == action) {
                    new OnEditOrgUnitMetadata(target.toString(), router, router.getRepositories(), controller);
                }
                else if (ACTION_ADD == action) {
                    new OnAddOrgUnitMetadata(controller, router);
                }
            }
        });

    }

    @Override
    protected HierarchicalContainer populateContainerTable() {
        tableContainer = new HierarchicalContainer();
        // Create container property for name
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_NAME, String.class, null);
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_LINK, Link.class, null);

        for (final MetadataRecord metadataRecord : mdList) {
            Item item = tableContainer.addItem(metadataRecord.getName());

            Link mdLink =
                new Link("View", new ExternalResource(router.getServiceLocation().getEscidocUri()
                    + metadataRecord.getXLinkHref()));
            mdLink.setTargetName("_blank");
            mdLink.setStyleName(BaseTheme.BUTTON_LINK);
            mdLink.setDescription("Show metadata information in a separate window");
            if (item != null) {
                item.getItemProperty(ViewConstants.PROPERTY_NAME).setValue(metadataRecord.getName());
                item.getItemProperty(ViewConstants.PROPERTY_LINK).setValue(mdLink);
            }
        }
        table.setColumnWidth(ViewConstants.PROPERTY_LINK, 40);
        return tableContainer;
    }

    protected void initializeTable() {
        // size
        table.setWidth("100%");
        // selectable
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setImmediate(true); // react at once when something is selected
        // turn on column reordering and collapsing
        table.setColumnReorderingAllowed(true);
        table.setColumnCollapsingAllowed(true);
        table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);

    }

}
