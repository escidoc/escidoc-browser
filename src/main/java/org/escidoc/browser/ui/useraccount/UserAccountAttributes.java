package org.escidoc.browser.ui.useraccount;

import com.google.common.base.Preconditions;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;

import org.escidoc.browser.controller.UserAccountController;
import org.escidoc.browser.model.internal.UserProxy;
import org.escidoc.browser.repository.internal.UserAccountRepository;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.view.helpers.TableContainerVH;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.aa.useraccount.Attribute;
import de.escidoc.core.resources.aa.useraccount.Attributes;

@SuppressWarnings("serial")
public class UserAccountAttributes extends TableContainerVH {

    private UserAccountRepository repository;

    private HierarchicalContainer tableContainer;

    private UserProxy userProxy;

    private UserAccountController uac;

    private Attributes attributes;

    public UserAccountAttributes(UserProxy up, Attributes attributes, UserAccountRepository ur,
        UserAccountController uac) {

        Preconditions.checkNotNull(up, "up is null: %s", up);
        Preconditions.checkNotNull(attributes, "attributes is null: %s", attributes);
        Preconditions.checkNotNull(uac, "uac is null: %s", uac);
        Preconditions.checkNotNull(uac, "uac is null: %s", uac);

        this.userProxy = up;
        this.attributes = attributes;
        this.repository = ur;
        this.uac = uac;

        table.setContainerDataSource(populateContainerTable());
    }

    @Override
    protected void removeAction(Object preferenceName) {
        try {
            repository.removePreference(userProxy, (String) preferenceName);
            uac.showTrayMessage(ViewConstants.PREFERENCE_REMOVE, ViewConstants.THE_PREFERENCE_REMOVED);
            tableContainer.removeItem(preferenceName);
        }
        catch (EscidocClientException e) {
            uac.showError(e.getLocalizedMessage());
        }
    }

    @Override
    protected HierarchicalContainer populateContainerTable() {
        // Create new container
        tableContainer = new HierarchicalContainer();
        // Create container property for name
        tableContainer.addContainerProperty(PROPERTY_NAME, String.class, null);
        tableContainer.addContainerProperty(PROPERTY_VALUE, String.class, null);

        for (Attribute a : attributes) {
            if (a.isInternal()) {
                Item item = tableContainer.addItem(a.getName());
                if (item != null) {
                    item.getItemProperty(PROPERTY_NAME).setValue(a.getName());
                    item.getItemProperty(PROPERTY_VALUE).setValue(a.getValue());
                }
            }
        }
        return tableContainer;
    }

    public HierarchicalContainer getTableContainer() {
        return tableContainer;
    }

}