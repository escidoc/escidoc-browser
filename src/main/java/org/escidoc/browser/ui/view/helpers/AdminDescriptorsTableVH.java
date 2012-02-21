package org.escidoc.browser.ui.view.helpers;

import org.escidoc.browser.controller.ContextController;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.AdminDescriptorFormListener;

import com.google.common.base.Preconditions;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;

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
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_VALUE, String.class, null);

        for (AdminDescriptor adminDescriptor : adminDescriptors) {
            Item item = tableContainer.addItem(adminDescriptor.getName());
            if (item != null) {
                item.getItemProperty(ViewConstants.PROPERTY_NAME).setValue(adminDescriptor.getName());
                item.getItemProperty(ViewConstants.PROPERTY_VALUE).setValue(adminDescriptor.getXLinkHref());
            }
        }
        return tableContainer;
    }

    @Override
    protected boolean hasRightstoContextMenu() {
        return true;
    }

}
