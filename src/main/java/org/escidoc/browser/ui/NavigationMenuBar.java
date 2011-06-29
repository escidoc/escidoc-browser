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
package org.escidoc.browser.ui;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.ui.navigation.NavigationTreeView;

import com.google.common.base.Preconditions;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

@SuppressWarnings("serial")
public class NavigationMenuBar extends CustomComponent {

    private final MenuBar menuBar = new MenuBar();

    private NavigationTreeView mainNavigationTree;

    private MenuBar.MenuItem add;

    private MenuItem itemMenuItem;

    private MenuItem contextMenuItem;

    private MenuItem containerMenuItem;

    public NavigationMenuBar() {
        setCompositionRoot(menuBar);
        init();
    }

    private void init() {
        menuBar.setSizeFull();

        add = menuBar.addItem("Add", null);

        contextMenuItem = add.addItem(ResourceType.CONTEXT.name(), menuCommand);
        containerMenuItem = add.addItem(ResourceType.CONTAINER.name(), menuCommand);
        itemMenuItem = add.addItem(ResourceType.ITEM.name(), menuCommand);

        menuBar.addItem("Delete", new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
                getWindow().showNotification("Not Yet Implemented");
            }
        });
    }

    private final Command menuCommand = new Command() {
        public void menuSelected(final MenuItem selectedItem) {
            getWindow().showNotification("Action " + selectedItem.getText());
        }
    };

    public void withNavigationTree(final NavigationTreeView mainNavigationTree) {
        Preconditions.checkNotNull(mainNavigationTree, "mainNavigationTree is null: %s", mainNavigationTree);
        this.mainNavigationTree = mainNavigationTree;
    }

    public void update(final ResourceModel resourceModel) {
        if (mainNavigationTree.getSelected() == null) {
            return;
        }
        switch (mainNavigationTree.getSelected().getType()) {
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