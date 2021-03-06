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

import java.io.File;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.controller.ItemController;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.internal.ItemProxyImpl;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.om.item.component.Components;

public class ItemComponentsView extends TableContainerVH {

    private static final Object COMPONENT_CATEGORY = "Category";

    private static final Object COMPONENT_MIMETYPE = "MimeType";

    private static final Object COMPONENT_CREATEDDATE = "Created Date";

    private static final Object COMPONENT_ICON = "";

    private static final Object COMPONENT_METADATA = "Metadata";

    private Action ACTION_DELETE = new Action("Delete Component");

    private Action ACTION_CHANGE_CATEGORY = new Action("Change Category Type");

    private Action[] ACTIONS_LIST = new Action[] { ACTION_DELETE, ACTION_CHANGE_CATEGORY };

    private ItemController controller;

    private Components componentsList;

    private HierarchicalContainer tableContainer;

    private EscidocServiceLocation serviceLocation;

    private Window mainWindow;

    private final Router router;

    private final ItemProxyImpl itemProxy;

    public ItemComponentsView(Components components, ItemController controller, Router router, ItemProxy itemProxy) {
        this.componentsList = components;
        this.controller = controller;
        this.router = router;
        this.itemProxy = (ItemProxyImpl) itemProxy;
        this.serviceLocation = router.getServiceLocation();
        this.mainWindow = router.getMainWindow();
        table.setWidth("100%");
        table.setHeight("100%");
        table.setStyleName(Reindeer.TABLE_BORDERLESS);
    }

    public void buildTable() {
        table.setContainerDataSource(populateContainerTable());
        if (hasRightstoContextMenu()) {
            this.addActionLists();
        }
    }

    @Override
    protected void removeAction(Object target) {
        controller.removeComponent(target, this);
    }

    public void removeItemFromTable(String id) {
        table.removeItem(id);
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
                else if ((ACTION_CHANGE_CATEGORY == action) && (target != null)) {
                    new ChangeComponentCategoryTypeHelper(router, tableContainer
                        .getItem(target.toString()).getItemProperty(COMPONENT_CATEGORY).getValue().toString(), target
                        .toString(), controller, itemProxy.getId()).showWindow();
                    controller.refreshView();
                }

            }
        });
        // }
    }

    @Override
    protected void initializeTable() {
        table.setWidth("100%");
        table.setSelectable(true);
        table.setImmediate(true); // react at once when something is selected
        table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
    }

    @Override
    protected HierarchicalContainer populateContainerTable() {
        // Create new container
        tableContainer = new HierarchicalContainer();
        // Create container property for name
        tableContainer.addContainerProperty(COMPONENT_ICON, Button.class, null);
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_NAME, String.class, null);
        tableContainer.addContainerProperty(COMPONENT_CATEGORY, String.class, null);
        tableContainer.addContainerProperty(COMPONENT_MIMETYPE, String.class, null);
        tableContainer.addContainerProperty(COMPONENT_CREATEDDATE, String.class, null);
        tableContainer.addContainerProperty(COMPONENT_METADATA, VerticalLayout.class, null);

        for (Component component : componentsList) {
            Item item = tableContainer.addItem(component.getObjid());
            if (item != null) {
                item.getItemProperty(COMPONENT_ICON).setValue(createDownloadLink(component));
                item.getItemProperty(ViewConstants.PROPERTY_NAME).setValue(component.getProperties().getFileName());
                item.getItemProperty(COMPONENT_CATEGORY).setValue(component.getProperties().getContentCategory());
                item.getItemProperty(COMPONENT_MIMETYPE).setValue(component.getProperties().getMimeType());
                item.getItemProperty(COMPONENT_CREATEDDATE).setValue(
                    component.getProperties().getCreationDate().toString("d.M.y, H:mm"));
                ItemComponentMetadataVH itemComponentMD =
                    new ItemComponentMetadataVH(component.getMetadataRecords(), controller, router, itemProxy,
                        component);
                itemComponentMD.buildTable();
                item.getItemProperty(COMPONENT_METADATA).setValue(itemComponentMD);
            }
        }
        boolean[] ascending = { true, false };
        Object[] propertyId = { ViewConstants.PROPERTY_NAME, ViewConstants.PROPERTY_VALUE };
        tableContainer.sort(propertyId, ascending);
        table.setColumnWidth(COMPONENT_ICON, 20);
        table.setColumnWidth(COMPONENT_MIMETYPE, 90);
        table.setColumnWidth(COMPONENT_CATEGORY, 120);
        table.setColumnWidth(COMPONENT_CREATEDDATE, 120);
        table.setColumnWidth(COMPONENT_METADATA, 180);
        return tableContainer;
    }

    private static ThemeResource createEmbeddedImage(final Component comp) {
        final String currentDir = new File(".").getAbsolutePath();
        final File file =
            new File(currentDir.substring(0, currentDir.length() - 1) + AppConstants.MIMETYPE_ICON_LOCATION
                + getFileType(comp) + ".png");
        final boolean exists = file.exists();
        if (exists) {
            return new ThemeResource("images/filetypes/" + getFileType(comp) + ".png");
        }
        return new ThemeResource("images/filetypes/article.png");
    }

    private static String getFileType(final Component itemProperties) {
        final String mimeType = itemProperties.getProperties().getMimeType();
        if (mimeType == null) {
            return AppConstants.EMPTY_STRING;
        }
        final String[] last = mimeType.split("/");
        final String lastOne = last[last.length - 1];
        return lastOne;
    }

    private Button createDownloadLink(final Component comp) {
        final Button link = new Button();
        link.setStyleName(BaseTheme.BUTTON_LINK);
        link.setIcon(createEmbeddedImage(comp));
        link.addListener(new Button.ClickListener() {

            private static final long serialVersionUID = 651483473875504715L;

            @Override
            public void buttonClick(@SuppressWarnings("unused")
            final ClickEvent event) {
                mainWindow.open(new ExternalResource(
                    serviceLocation.getEscidocUri() + comp.getContent().getXLinkHref(), comp
                        .getProperties().getMimeType()), "_new");
            }
        });
        return link;
    }

    @Override
    protected boolean hasRightstoContextMenu() {
        return controller.canUpdateItem();
    }
}
