/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
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
 * All rights reserved. Use is subject to license terms.
 */
package org.escidoc.browser.ui.useraccount;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import org.escidoc.browser.ui.ViewConstants;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.aa.useraccount.Preference;

@SuppressWarnings("serial")
final class OnAddPreference implements ClickListener {

    private final UserAccountView userAccountView;

    private final Panel preferencePanel;

    private final UserAccountPreferences userPrefTable;

    private final Button addPreference;

    OnAddPreference(UserAccountView userAccountView, Panel preferencePanel, UserAccountPreferences userPrefTable,
        Button addPreference) {
        this.userAccountView = userAccountView;
        this.preferencePanel = preferencePanel;
        this.userPrefTable = userPrefTable;
        this.addPreference = addPreference;
    }

    @Override
    public void buttonClick(@SuppressWarnings("unused") final com.vaadin.ui.Button.ClickEvent event) {
        addPreference.setEnabled(false);
        final HorizontalLayout hl = new HorizontalLayout();
        final TextField key = new TextField();
        key.setCaption("Name");
        key.setImmediate(false);
        key.setWidth("-1px");
        key.setHeight("-1px");
        key.setInvalidAllowed(false);
        key.setRequired(true);

        final TextField value = new TextField();
        value.setCaption("Value");
        value.setImmediate(false);
        value.setWidth("-1px");
        value.setHeight("-1px");
        value.setInvalidAllowed(false);
        value.setRequired(true);

        final Button addButton = new Button();
        addButton.setIcon(new ThemeResource("images/assets/plus.png"));
        addButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(@SuppressWarnings("unused") final com.vaadin.ui.Button.ClickEvent event) {
                if (UserAccountView.isNotValid(key, value)) {
                    OnAddPreference.this.userAccountView.showMessage();
                }
                else {
                    try {
                        OnAddPreference.this.userAccountView.ur.createPreference(
                            OnAddPreference.this.userAccountView.userProxy, new Preference(key.getValue().toString(),
                                value.getValue().toString()));
                        OnAddPreference.this.userAccountView.router.getMainWindow().showNotification(
                            "Preference added successfully ", Window.Notification.TYPE_TRAY_NOTIFICATION);
                        hl.removeAllComponents();
                        addPreference.setEnabled(true);
                        userPrefTable.createItem(userPrefTable.getTableContainer(), key.getValue().toString(), key
                            .getValue().toString(), value.getValue().toString());
                    }
                    catch (final EscidocClientException e) {
                        OnAddPreference.this.userAccountView.router.getMainWindow().showNotification(
                            ViewConstants.ERROR_CREATING_USER_PREFERENCE + e.getLocalizedMessage(),
                            Window.Notification.TYPE_ERROR_MESSAGE);
                    }
                }
            }

        });
        hl.addComponent(key);
        hl.addComponent(value);
        hl.addComponent(addButton);
        hl.setComponentAlignment(addButton, Alignment.BOTTOM_RIGHT);
        preferencePanel.addComponent(hl);
    }
}