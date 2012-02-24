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
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui.useraccount;

import org.escidoc.browser.controller.UserAccountController;
import org.escidoc.browser.model.internal.UserProxy;
import org.escidoc.browser.repository.internal.UserAccountRepository;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.view.helpers.TableContainerVH;

import com.google.common.base.Preconditions;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.aa.useraccount.Preference;
import de.escidoc.core.resources.aa.useraccount.Preferences;

@SuppressWarnings("serial")
public class UserAccountPreferences extends TableContainerVH {

    private UserProxy userProxy;

    private Preferences preferences;

    private UserAccountRepository repository;

    private HierarchicalContainer tableContainer;

    private UserAccountController uac;

    public UserAccountPreferences(UserProxy userProxy, Preferences preferences, UserAccountRepository repository,
        UserAccountController uac) {
        Preconditions.checkNotNull(userProxy, "userProxy is null: %s", userProxy);
        Preconditions.checkNotNull(preferences, "preferences is null: %s", preferences);
        Preconditions.checkNotNull(repository, "repository is null: %s", repository);
        Preconditions.checkNotNull(uac, "uac is null: %s", uac);

        this.userProxy = userProxy;
        this.preferences = preferences;
        this.repository = repository;
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
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_NAME, String.class, null);
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_VALUE, String.class, null);

        for (Preference preference : preferences) {
            Item item = tableContainer.addItem(preference.getName());
            if (item != null) {
                item.getItemProperty(ViewConstants.PROPERTY_NAME).setValue(preference.getName());
                item.getItemProperty(ViewConstants.PROPERTY_VALUE).setValue(preference.getValue());
            }
        }
        return tableContainer;
    }

    public HierarchicalContainer getTableContainer() {
        return tableContainer;
    }

    @Override
    protected boolean hasRightstoContextMenu() {
        return true;
    }

}
