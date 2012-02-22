package org.escidoc.browser.ui.view.helpers;

import org.escidoc.browser.controller.OrgUnitController;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.OnAddOrgUnitMetadata;
import org.escidoc.browser.ui.maincontent.OnEditOrgUnitMetadata;

import com.google.common.base.Preconditions;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.BaseTheme;

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
                new Link("Link", new ExternalResource(router.getServiceLocation().getEscidocUri()
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

}
