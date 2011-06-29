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
package org.escidoc.browser.ui.navigation;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.ui.ViewConstants;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

@SuppressWarnings("serial")
public class NavigationMenuBar extends CustomComponent {

    private final class DeleteCommand implements Command {
        @Override
        public void menuSelected(final MenuItem selectedItem) {
            getWindow().showNotification("Not Yet Implemented");
        }
    }

    private final MenuBar menuBar = new MenuBar();

    private MenuBar.MenuItem add;

    private MenuItem itemMenuItem;

    private MenuItem contextMenuItem;

    private MenuItem containerMenuItem;

    private MenuItem deleteMenuItem;

    public NavigationMenuBar() {
        setCompositionRoot(menuBar);
        init();
    }

    private void init() {
        menuBar.setSizeFull();
        addCreateMenu();
        addDeleteMenu();
    }

    private void addCreateMenu() {
        add = menuBar.addItem(ViewConstants.ADD, null);

        contextMenuItem = add.addItem(ResourceType.CONTEXT.asLabel(), addItemCommand);
        containerMenuItem = add.addItem(ResourceType.CONTAINER.asLabel(), addItemCommand);
        itemMenuItem = add.addItem(ResourceType.ITEM.asLabel(), addItemCommand);
    }

    private void addDeleteMenu() {
        deleteMenuItem = menuBar.addItem(ViewConstants.DELETE, new DeleteCommand());
        deleteMenuItem.setEnabled(false);
    }

    private final Command addItemCommand = new Command() {
        public void menuSelected(final MenuItem selectedItem) {
            getWindow().showNotification("Action " + selectedItem.getText());
        }
    };

    public void update(final ResourceModel resourceModel) {
        switch (resourceModel.getType()) {
            case CONTEXT:
                showAddContainerAndItem();
                break;
            case CONTAINER:
                showAddContainerAndItem();
                break;
            case ITEM:
                menuBar.setEnabled(false);
        }
    }

    private void showAddContainerAndItem() {
        menuBar.setEnabled(true);
        contextMenuItem.setVisible(false);
        containerMenuItem.setVisible(true);
        itemMenuItem.setVisible(true);
    }
}