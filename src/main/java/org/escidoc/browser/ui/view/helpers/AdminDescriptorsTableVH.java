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
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

import org.escidoc.browser.controller.ContextController;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.AdminDescriptorFormListener;

import de.escidoc.core.resources.om.context.AdminDescriptor;
import de.escidoc.core.resources.om.context.AdminDescriptors;

public class AdminDescriptorsTableVH extends TableContainerVH {

    protected ContextController controller;

    private AdminDescriptors adminDescriptors;

    private HierarchicalContainer tableContainer;

    private Action ACTION_DELETE = new Action("Delete AdminDescriptor");

    private Action ACTION_ADD = new Action("Add AdminDescriptor");

    private Action ACTION_EDIT = new Action("Edit AdminDescriptor");

    private Action[] ACTIONS_LIST = new Action[] { ACTION_ADD, ACTION_EDIT, ACTION_DELETE };

    private Router router;

    public AdminDescriptorsTableVH(ContextController contextController, AdminDescriptors adminDescriptors, Router router) {

        Preconditions.checkNotNull(contextController, "contextController is null: %s", contextController);
        Preconditions.checkNotNull(adminDescriptors, "preferences is null: %s", adminDescriptors);

        this.controller = contextController;
        this.adminDescriptors = adminDescriptors;
        this.router = router;
        table.setContainerDataSource(populateContainerTable());
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
                if (ACTION_DELETE == action) {
                    confirmActionWindow(target);
                }
                else if (ACTION_ADD == action) {
                    new AdminDescriptorFormListener(router, controller).adminDescriptorForm();
                    controller.refreshView();
                }
                else if (ACTION_EDIT == action) {
                    new AdminDescriptorFormListener(router, controller).adminDescriptorForm(adminDescriptors.get(target
                        .toString()));
                    controller.refreshView();

                }

            }
        });
        // }
    }

    @Override
    protected void removeAction(Object target) {
        controller.removeAdminDescriptor(target.toString());
        tableContainer.removeItem(target);
    }

    @Override
    protected HierarchicalContainer populateContainerTable() {
        // Create new container
        tableContainer = new HierarchicalContainer();
        // Create container property for name
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_NAME, String.class, null);
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_LINK, Label.class, null);

        for (final AdminDescriptor adminDescriptor : adminDescriptors) {
            Item item = tableContainer.addItem(adminDescriptor.getName());
            if (item != null) {
                item.getItemProperty(ViewConstants.PROPERTY_NAME).setValue(adminDescriptor.getName());
                item.getItemProperty(ViewConstants.PROPERTY_LINK).setValue(
                    new Label("<a href=\"" + router.getServiceLocation().getEscidocUri()
                        + adminDescriptor.getXLinkHref() + "\" target=\"_blank\">View</a>", Label.CONTENT_RAW));
            }
        }
        table.setColumnWidth(ViewConstants.PROPERTY_LINK, 40);
        return tableContainer;
    }

    @Override
    protected boolean hasRightstoContextMenu() {
        return true;
    }

    protected void initializeTable() {
        table.setWidth("100%");
        table.setSelectable(true);
        table.setImmediate(true); // react at once when something is selected
        table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
    }

}
