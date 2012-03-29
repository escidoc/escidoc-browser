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
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.BaseTheme;

import org.escidoc.browser.controller.ContextController;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.OnContextAdminDescriptor;
import org.escidoc.browser.ui.view.helpers.OrgUnitMetadataTable.Metadata;

import de.escidoc.core.resources.om.context.AdminDescriptor;
import de.escidoc.core.resources.om.context.AdminDescriptors;

@SuppressWarnings("serial")
public class AdminDescriptorsTable extends TableContainerVH {

    protected ContextController controller;

    private AdminDescriptors adList;

    private HierarchicalContainer dataSource;

    private Action ACTION_DELETE = new Action("Delete");

    private Action ACTION_ADD = new Action("Add");

    private Action ACTION_EDIT = new Action("Edit");

    private Action[] ACTIONS_LIST = new Action[] { ACTION_ADD, ACTION_EDIT, ACTION_DELETE };

    private Router router;

    public AdminDescriptorsTable(ContextController controller, AdminDescriptors adList, Router router) {
        Preconditions.checkNotNull(controller, "contextController is null: %s", controller);
        Preconditions.checkNotNull(adList, "adminDescriptors is null: %s", adList);
        Preconditions.checkNotNull(router, "router is null: %s", router);

        this.controller = controller;
        this.adList = adList;
        this.router = router;

        table.setContainerDataSource(populateContainerTable());
    }

    @Override
    protected void addActionLists() {
        table.addActionHandler(new Action.Handler() {
            @Override
            public Action[] getActions(
                @SuppressWarnings("unused") final Object target, @SuppressWarnings("unused") final Object sender) {
                return ACTIONS_LIST;
            }

            @Override
            public void handleAction(
                final Action action, @SuppressWarnings("unused") final Object sender, final Object target) {
                Preconditions.checkNotNull(action, "action is null: %s", action);
                Preconditions.checkNotNull(target, "target is null: %s", target);

                if (ACTION_DELETE == action) {
                    confirmActionWindow(target);
                }
                else if (ACTION_ADD == action) {
                    new OnContextAdminDescriptor(router, controller).adminDescriptorForm();
                    controller.refreshView();
                }
                else if (ACTION_EDIT == action) {
                    if (target != null) {
                        Metadata md = (Metadata) target;
                        new OnEditContextMetadata(router, controller, router.getMainWindow(), md).showEditWindow();
                        // TODO what is this method doing?
                        controller.refreshView();
                    }
                }
            }
        });
    }

    @Override
    protected void removeAction(Object target) {
        controller.removeAdminDescriptor(target.toString());
        dataSource.removeItem(target);
    }

    @Override
    protected HierarchicalContainer populateContainerTable() {
        dataSource = new HierarchicalContainer();
        dataSource.addContainerProperty(ViewConstants.PROPERTY_NAME, String.class, null);
        dataSource.addContainerProperty(ViewConstants.PROPERTY_LINK, Label.class, null);

        for (final AdminDescriptor adminDescriptor : adList) {
            Metadata md = Metadata.newInstance(adminDescriptor);
            Item item = dataSource.addItem(md);
            if (item != null) {
                item.getItemProperty(ViewConstants.PROPERTY_NAME).setValue(md.name);
                item.getItemProperty(ViewConstants.PROPERTY_LINK).setValue(buildLink(adminDescriptor));
            }
        }
        return dataSource;
    }

    private Link buildLink(AdminDescriptor ad) {
        Link mdLink = new Link("View", new ExternalResource(buildUri(ad)));
        mdLink.setTargetName("_blank");
        mdLink.setStyleName(BaseTheme.BUTTON_LINK);
        mdLink.setDescription("Show Admin Descriptor information in a separate window");
        return mdLink;
    }

    private String buildUri(AdminDescriptor ad) {
        StringBuilder builder = new StringBuilder();
        builder.append(router.getServiceLocation().getEscidocUri());
        builder.append(ad.getXLinkHref());
        return builder.toString();
    }

    @Override
    protected boolean hasRightstoContextMenu() {
        return true;
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
