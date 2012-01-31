package org.escidoc.browser.ui.view.helpers;

import org.escidoc.browser.controller.UserProfileController;
import org.escidoc.browser.ui.ViewConstants;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.aa.useraccount.Preference;
import de.escidoc.core.resources.aa.useraccount.Preferences;

@SuppressWarnings("serial")
public class UserPreferencesTable extends TableContainerVH {

    private Preferences preferences;

    private UserProfileController controller;

    private HierarchicalContainer tableContainer;

    public UserPreferencesTable(Preferences preferences, UserProfileController controller) {
        this.preferences = preferences;
        this.controller = controller;
        table.setContainerDataSource(populateContainerTable());

    }

    @Override
    protected HierarchicalContainer populateContainerTable() {
        // Create new container
        tableContainer = new HierarchicalContainer();
        // Create container property for name
        tableContainer.addContainerProperty(PROPERTY_NAME, String.class, null);
        tableContainer.addContainerProperty(PROPERTY_VALUE, String.class, null);

        for (Preference preference : preferences) {
            Item item = tableContainer.addItem(preference.getName());
            if (item != null) {
                item.getItemProperty(PROPERTY_NAME).setValue(preference.getName());
                item.getItemProperty(PROPERTY_VALUE).setValue(preference.getValue());
            }
        }
        boolean[] ascending = { true, false };
        Object[] propertyId = { PROPERTY_NAME, PROPERTY_VALUE };
        tableContainer.sort(propertyId, ascending);
        return tableContainer;
    }

    @Override
    protected void removeAction(Object preferenceName) {
        try {
            controller.removePreference((String) preferenceName);
            controller.showTrayMessage(ViewConstants.PREFERENCE_REMOVE, ViewConstants.THE_PREFERENCE_REMOVED);
            tableContainer.removeItem(preferenceName);

        }
        catch (EscidocClientException e) {
            controller.showError(e.getLocalizedMessage());
        }
    }

    public HierarchicalContainer getTableContainer() {
        return tableContainer;
    }
}