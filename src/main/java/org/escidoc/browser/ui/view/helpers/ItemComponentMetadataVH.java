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

public class ItemComponentMetadataVH extends TableContainerVH {

    private MetadataRecords md;

    private Router router;

    protected ItemController controller;

    private HierarchicalContainer tableContainer;

    private Action ACTION_DELETE = new Action("Delete Metadata");

    private Action ACTION_ADD = new Action("Add Metadata");

    private Action ACTION_EDIT = new Action("Edit Metadata");

    private Action[] ACTIONS_LIST = new Action[] { ACTION_ADD, ACTION_EDIT, ACTION_DELETE };

    private final ItemProxyImpl itemProxy;

    private final Component component;

    public ItemComponentMetadataVH(MetadataRecords md, ItemController controller, Router router,
        ItemProxyImpl itemProxy, Component component) {

        this.component = component;
        Preconditions.checkNotNull(md, "MetadataRecords is null: %s", md);
        Preconditions.checkNotNull(router, "router is null.");
        Preconditions.checkNotNull(controller, "ItemController is null.");
        this.md = md;
        this.controller = controller;
        this.router = router;
        this.itemProxy = itemProxy;
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
                    new AddMetaDataFileItemComponentBehaviour(router.getMainWindow(), component, controller, itemProxy,
                        ItemComponentMetadataVH.this).showAddWindow();
                    controller.refreshView();
                }
                else if (ACTION_EDIT == action) {
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
        return true;
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

        for (final MetadataRecord metadataRecord : md) {
            Item item = tableContainer.addItem(metadataRecord.getName());
            if (item != null) {
                item.getItemProperty(ViewConstants.PROPERTY_NAME).setValue(metadataRecord.getName());
                item.getItemProperty(ViewConstants.PROPERTY_LINK).setValue(
                    new Label("<a href=\"" + router.getServiceLocation().getEscidocUri()
                        + metadataRecord.getXLinkHref() + "\" target=\"_blank\">View</a>", Label.CONTENT_RAW));
            }
        }
        table.setColumnWidth(ViewConstants.PROPERTY_LINK, 40);
        return tableContainer;
    }

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
