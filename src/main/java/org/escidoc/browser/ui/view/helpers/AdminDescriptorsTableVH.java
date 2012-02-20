package org.escidoc.browser.ui.view.helpers;

import org.escidoc.browser.controller.ContextController;

import com.google.common.base.Preconditions;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;

import de.escidoc.core.resources.om.context.AdminDescriptor;
import de.escidoc.core.resources.om.context.AdminDescriptors;

public class AdminDescriptorsTableVH extends TableContainerVH {

    private ContextController contextController;

    private AdminDescriptors adminDescriptors;

    private HierarchicalContainer tableContainer;

    public AdminDescriptorsTableVH(ContextController contextController, AdminDescriptors adminDescriptors) {
        Preconditions.checkNotNull(contextController, "contextController is null: %s", contextController);
        Preconditions.checkNotNull(adminDescriptors, "preferences is null: %s", adminDescriptors);

        this.contextController = contextController;
        this.adminDescriptors = adminDescriptors;

        table.setContainerDataSource(populateContainerTable());
    }

    @Override
    protected void removeAction(Object target) {
        contextController.removeAdminDescriptor(target.toString());
        tableContainer.removeItem(target);
    }

    @Override
    protected HierarchicalContainer populateContainerTable() {
        // Create new container
        tableContainer = new HierarchicalContainer();
        // Create container property for name
        tableContainer.addContainerProperty(PROPERTY_NAME, String.class, null);
        tableContainer.addContainerProperty(PROPERTY_VALUE, String.class, null);

        for (AdminDescriptor adminDescriptor : adminDescriptors) {
            Item item = tableContainer.addItem(adminDescriptor.getName());
            if (item != null) {
                item.getItemProperty(PROPERTY_NAME).setValue(adminDescriptor.getName());
                item.getItemProperty(PROPERTY_VALUE).setValue(adminDescriptor.getXLinkHref());
            }
        }
        return tableContainer;
    }

}
