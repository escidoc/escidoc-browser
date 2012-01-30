package org.escidoc.browser.ui.useraccount;

import com.google.common.base.Preconditions;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;

import org.escidoc.browser.repository.internal.UserAccountRepository;
import org.escidoc.browser.ui.view.helpers.TableContainerVH;

import de.escidoc.core.resources.aa.useraccount.Preference;
import de.escidoc.core.resources.aa.useraccount.Preferences;

@SuppressWarnings("serial")
public class UserAccountPreferences extends TableContainerVH {

    private Preferences preferences;

    private UserAccountRepository ur;

    private HierarchicalContainer tableContainer;

    public UserAccountPreferences(Preferences preferences, UserAccountRepository ur) {
        Preconditions.checkNotNull(preferences, "preferences is null: %s", preferences);
        Preconditions.checkNotNull(ur, "ur is null: %s", ur);
        this.preferences = preferences;
        this.ur = ur;
        table.setContainerDataSource(populateContainerTable());
    }

    @Override
    protected void removeAction(Object target) {
        throw new UnsupportedOperationException("not-yet-implemented.");
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
        return tableContainer;
    }

    public HierarchicalContainer getTableContainer() {
        return tableContainer;
    }

}
