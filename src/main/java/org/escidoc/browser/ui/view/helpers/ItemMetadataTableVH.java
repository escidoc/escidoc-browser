package org.escidoc.browser.ui.view.helpers;

import org.escidoc.browser.controller.ItemController;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.AddMetaDataFileItemBehaviour;
import org.escidoc.browser.ui.listeners.EditMetaDataFileItemBehaviour;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.ui.Label;

import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;

public class ItemMetadataTableVH extends TableContainerVH {

    protected ItemController controller;

    private HierarchicalContainer tableContainer;

    private Action ACTION_DELETE = new Action("Delete Metadata");

    private Action ACTION_ADD = new Action("Add Metadata");

    private Action ACTION_EDIT = new Action("Edit Metadata");

    private Action[] ACTIONS_LIST = new Action[] { ACTION_ADD, ACTION_EDIT, ACTION_DELETE };

    private Router router;

    private MetadataRecords mdRecords;

    private final Repositories repositories;

    private final ItemProxy resourceProxy;

    public ItemMetadataTableVH(ItemController itemController, Router router, ItemProxy resourceProxy,
        Repositories repositories) {
        this.controller = itemController;
        this.router = router;
        this.resourceProxy = resourceProxy;
        this.repositories = repositories;
        this.mdRecords = resourceProxy.getMedataRecords();
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
                    new AddMetaDataFileItemBehaviour(router.getMainWindow(), repositories, resourceProxy)
                        .showAddWindow();
                    controller.refreshView();
                }
                else if (ACTION_EDIT == action) {
                    MetadataRecord md = resourceProxy.getMedataRecords().get((String) target);
                    new EditMetaDataFileItemBehaviour(md, router.getMainWindow(), repositories, resourceProxy)
                        .showWindow();
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
        controller.removeMetadata(target.toString());
        tableContainer.removeItem(target);

    }

    @Override
    protected HierarchicalContainer populateContainerTable() {
        tableContainer = new HierarchicalContainer();
        // Create container property for name
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_NAME, String.class, null);
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_LINK, Label.class, null);

        for (final MetadataRecord metadataRecord : mdRecords) {
            Item item = tableContainer.addItem(metadataRecord.getXLinkTitle());
            if (item != null) {
                item.getItemProperty(ViewConstants.PROPERTY_NAME).setValue(metadataRecord.getXLinkTitle());
                item.getItemProperty(ViewConstants.PROPERTY_LINK).setValue(
                    new Label("<a href=\"" + router.getServiceLocation().getEscidocUri()
                        + metadataRecord.getXLinkHref() + "\" target=\"_blank\">Link</a>", Label.CONTENT_RAW));
            }
        }
        table.setColumnWidth(ViewConstants.PROPERTY_LINK, 40);
        return tableContainer;
    }

}
