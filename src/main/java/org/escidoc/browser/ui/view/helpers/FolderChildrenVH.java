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

import java.util.List;
import java.util.Set;

import org.escidoc.browser.controller.ContainerController;
import org.escidoc.browser.controller.FolderController;
import org.escidoc.browser.controller.ItemController;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceModelFactory;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.internal.ContainerProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ContainerRepository;
import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.om.item.component.Component;

@SuppressWarnings("serial")
public class FolderChildrenVH extends TableContainerVH {

    protected FolderController folderController;

    private HierarchicalContainer tableContainer;

    // private Action ACTION_DELETE = new Action("Add Resou Metadata");
    //
    // private Action ACTION_ADD = new Action("Add Metadata");
    //
    // private Action ACTION_EDIT = new Action("Edit Metadata");
    //
    // private Action[] ACTIONS_LIST = new Action[] { ACTION_ADD, ACTION_EDIT, ACTION_DELETE };

    private final ContainerProxyImpl containerProxy;

    private Component component;

    private ContainerRepository containerRepository;

    private ResourceModelFactory resourceModelFactory;

    private Repositories repository;

    private static final Logger LOG = LoggerFactory.getLogger(FolderChildrenVH.class);

    public FolderChildrenVH(FolderController folderController, ContainerProxyImpl resourceProxy, Repositories repository) {
        this.containerProxy = resourceProxy;
        this.folderController = folderController;
        this.containerRepository = repository.container();
        this.repository = repository;
        this.setHeight("100%");
        table.setContainerDataSource(populateContainerTable());

    }

    @Override
    protected void addActionLists() {
        // if (contextController.canUpdateContext()) {
        // table.addActionHandler(new Action.Handler() {
        // @Override
        // public Action[] getActions(Object target, Object sender) {
        // return ACTIONS_LIST;
        // }
        //
        // @Override
        // public void handleAction(Action action, Object sender, Object target) {
        // if (ACTION_DELETE == action) {
        // confirmActionWindow(target);
        // }
        // else if (ACTION_ADD == action) {
        // // new AddMetaDataFileItemComponentBehaviour(router.getMainWindow(), component, controller,
        // // itemProxy,
        // // FolderChildrenVH.this).showAddWindow();
        // controller.refreshView();
        // }
        // else if (ACTION_EDIT == action) {
        // MetadataRecord md = component.getMetadataRecords().get((String) target);
        // // new EditMetaDataFileItemComponentBehaviour(md, router.getMainWindow(), component, controller,
        // // itemProxy, FolderChildrenVH.this).showWindow();
        // // controller.refreshView();
        //
        // }
        //
        // }
        // });
        // }
        table.addListener(new Table.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                Set<?> value = (Set<?>) event.getProperty().getValue();
                if (null == value || value.size() == 0) {
                    LOG.debug("No Value retrieved from Table Click");
                }
                else {
                    String[] splitResult = table.getValue().toString().split("#");
                    String resourceType = splitResult[1].substring(0, splitResult[1].length() - 1);
                    System.out.println(resourceType + "=" + ResourceType.CONTAINER);
                    String resourceId = splitResult[0].substring(1, splitResult[0].length());
                    LOG.debug(splitResult[0] + resourceId);

                    if (resourceType.equals(ResourceType.CONTAINER.toString())) {
                        try {
                            ContainerController newCont =
                                new ContainerController(repository, folderController.getRouter(), repository
                                    .container().findById(resourceId));
                            folderController.getRouter().openControllerView(newCont, true);
                        }
                        catch (EscidocClientException e) {
                            LOG.debug(ViewConstants.ERROR + e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }
                    else {
                        try {
                            ItemController newCont =
                                new ItemController(repository, folderController.getRouter(), repository
                                    .item().findById(resourceId));
                            folderController.getRouter().openControllerView(newCont, true);
                        }
                        catch (EscidocClientException e) {
                            LOG.debug(ViewConstants.ERROR + e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected boolean hasRightstoContextMenu() {
        return true;
    }

    @Override
    protected void removeAction(Object target) {
        // TODO IMPLEMENT THIS ONE
        tableContainer.removeItem(target);
    }

    @Override
    protected HierarchicalContainer populateContainerTable() {
        tableContainer = new HierarchicalContainer();
        // Create container property for name
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_ICON, Button.class, null);
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_NAME, String.class, null);
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_EDIT, Button.class, null);
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_DELETE, Button.class, null);
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_DOWNLOAD, Button.class, null);
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_SHARE, Button.class, null);

        List<ResourceModel> childrenList;
        try {
            childrenList = containerRepository.findDirectMembers(containerProxy.getId());
            if (containerRepository.findDirectMembers(containerProxy.getId()) != null) {
                for (final ResourceModel child : childrenList) {
                    createItem(child);
                }
            }

        }
        catch (EscidocClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        table.setColumnWidth(ViewConstants.PROPERTY_ICON, 20);
        table.setColumnWidth(ViewConstants.PROPERTY_EDIT, 20);
        table.setColumnWidth(ViewConstants.PROPERTY_DELETE, 20);
        table.setColumnWidth(ViewConstants.PROPERTY_DOWNLOAD, 20);
        table.setColumnWidth(ViewConstants.PROPERTY_SHARE, 20);
        return tableContainer;
    }

    private void createItem(final ResourceModel child) {
        Item item = tableContainer.addItem(child.getId() + "#" + child.getType());
        if (item != null) {
            item.getItemProperty(ViewConstants.PROPERTY_NAME).setValue(child.getName());

            Button icon = showIcon(child);
            item.getItemProperty(ViewConstants.PROPERTY_ICON).setValue(icon);

            Button edit = showEdit(child);
            item.getItemProperty(ViewConstants.PROPERTY_EDIT).setValue(edit);
            Button delete = showDelete(child);
            item.getItemProperty(ViewConstants.PROPERTY_DELETE).setValue(delete);
            Button share = showShare(child);
            item.getItemProperty(ViewConstants.PROPERTY_SHARE).setValue(share);
            Button download = downloadShow(child);
            item.getItemProperty(ViewConstants.PROPERTY_DOWNLOAD).setValue(download);
        }

    }

    private Button showIcon(final ResourceModel child) {
        Button icon = new Button();
        icon.setStyleName(BaseTheme.BUTTON_LINK);
        LOG.debug(child.getType().toString());
        if (child.getType().toString().equals(ResourceType.CONTAINER.toString())) {
            icon.setDescription("Container");
            icon.setIcon(new ThemeResource("images/Container.png"));
        }
        else {
            icon.setDescription("Item");
            icon.setIcon(new ThemeResource("images/Item.png"));
        }

        return icon;
    }

    private Button showEdit(final ResourceModel child) {
        Button edit = new Button();
        edit.setStyleName(BaseTheme.BUTTON_LINK);
        edit.setDescription("Edit");
        edit.setIcon(new ThemeResource("images/wpzoom/pencil.png"));
        edit.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                folderController
                    .getRouter().getMainWindow()
                    .showNotification("Not yet Implemented " + child.getId(), Notification.TYPE_HUMANIZED_MESSAGE);

            }
        });
        return edit;
    }

    private Button showDelete(final ResourceModel child) {
        Button edit = new Button();
        edit.setStyleName(BaseTheme.BUTTON_LINK);
        edit.setDescription(ViewConstants.PROPERTY_DELETE);
        edit.setIcon(new ThemeResource("images/wpzoom/trash.png"));
        edit.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                folderController
                    .getRouter().getMainWindow()
                    .showNotification("Not yet Implemented " + child.getId(), Notification.TYPE_HUMANIZED_MESSAGE);

            }
        });
        return edit;
    }

    private Button showShare(final ResourceModel child) {
        Button edit = new Button();
        edit.setStyleName(BaseTheme.BUTTON_LINK);
        edit.setDescription(ViewConstants.PROPERTY_SHARE);
        edit.setIcon(new ThemeResource("images/wpzoom/share.png"));
        edit.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                folderController
                    .getRouter().getMainWindow()
                    .showNotification("Not yet Implemented " + child.getId(), Notification.TYPE_HUMANIZED_MESSAGE);

            }
        });
        return edit;
    }

    private Button downloadShow(final ResourceModel child) {
        Button edit = new Button();
        edit.setStyleName(BaseTheme.BUTTON_LINK);
        edit.setDescription(ViewConstants.PROPERTY_DOWNLOAD);
        edit.setIcon(new ThemeResource("images/wpzoom/eye.png"));
        edit.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                folderController
                    .getRouter().getMainWindow()
                    .showNotification("Not yet Implemented " + child.getId(), Notification.TYPE_HUMANIZED_MESSAGE);

            }
        });
        return edit;
    }

    @Override
    protected void initializeTable() {
        // size
        table.setWidth("100%");
        table.setHeight("100%");
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
