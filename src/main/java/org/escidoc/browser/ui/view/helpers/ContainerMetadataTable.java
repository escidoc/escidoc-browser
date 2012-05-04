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

import org.escidoc.browser.controller.ContainerController;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.OnAddContainerMetadata;
import org.escidoc.browser.ui.listeners.OnEditContainerMetadata;
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
import de.escidoc.core.resources.common.MetadataRecords;

@SuppressWarnings("serial")
public class ContainerMetadataTable extends TableContainerVH {
    // FIXME a lot of duplicate code with other ${Resource}MetadataTable

    protected final ContainerController controller;

    private final Action ACTION_DELETE = new Action("Delete Metadata");

    private final Action ACTION_ADD = new Action("Add Metadata");

    private final Action ACTION_EDIT = new Action("Edit Metadata");

    private final Action[] ACTIONS_LIST = new Action[] { ACTION_ADD, ACTION_EDIT, ACTION_DELETE };

    private final Router router;

    private final MetadataRecords mdList;

    private final Repositories repositories;

    private final ContainerProxy resourceProxy;

    private HierarchicalContainer dataSource;

    private HierarchicalContainer tableContainer;

    public ContainerMetadataTable(MetadataRecords metadataRecords, ContainerController controller, Router router,
        ContainerProxy resourceProxy, Repositories repositories) {
        Preconditions.checkNotNull(metadataRecords, "metadataRecords is null: %s", metadataRecords);
        Preconditions.checkNotNull(controller, "containerController is null: %s", controller);
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);

        this.mdList = metadataRecords;
        this.controller = controller;
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

    @Override
    protected void addActionLists() {
        table.addActionHandler(new Action.Handler() {
            @Override
            public Action[] getActions(@SuppressWarnings("unused")
            Object target, @SuppressWarnings("unused")
            Object sender) {
                return ACTIONS_LIST;
            }

            @Override
            public void handleAction(Action action, @SuppressWarnings("unused")
            Object sender, Object target) {
                if (ACTION_DELETE == action) {
                    confirmActionWindow(target);
                }
                else if (ACTION_ADD == action) {
                    new OnAddContainerMetadata(router.getMainWindow(), repositories, resourceProxy).showAddWindow();
                    controller.refreshView();
                }
                else if (ACTION_EDIT == action) {
                    new OnEditContainerMetadata(router, controller, (Metadata) target).showEditWindow();
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

        for (final MetadataRecord metadataRecord : mdList) {
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
        table.setImmediate(true); // react at once when something is selected
        table.setColumnReorderingAllowed(true);
        table.setColumnCollapsingAllowed(true);
        table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
        table.setColumnWidth(ViewConstants.PROPERTY_LINK, 40);
    }
}