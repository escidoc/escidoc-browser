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
package org.escidoc.browser.ui.navigation.menubar;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.model.internal.TreeDataSourceImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.maincontent.ContainerAddView;

import com.google.common.base.Preconditions;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
final class ShowContainerAddView implements Command {

    private final NavigationMenuBar navigationMenuBar;

    private Repositories repositories;

    private Window mainWindow;

    private ResourceModel parent;

    public ShowContainerAddView(final NavigationMenuBar navigationMenuBar, final Repositories repositories,
        final Window mainWindow) {
        Preconditions.checkNotNull(navigationMenuBar, "navigationMenuBar is null: %s", navigationMenuBar);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        this.navigationMenuBar = navigationMenuBar;
        this.repositories = repositories;
        this.mainWindow = mainWindow;
    }

    ShowContainerAddView(final NavigationMenuBar navigationMenuBar) {
        Preconditions.checkNotNull(navigationMenuBar, "navigation Menu Bar is null: %s", navigationMenuBar);
        this.navigationMenuBar = navigationMenuBar;
    }

    public void withParent(final ResourceModel parent) {
        this.parent = parent;
    }

    @Override
    public void menuSelected(final MenuItem selectedItem) {
        if (selectedItem.getText().equals("Container")) {
            navigationMenuBar.getWindow().showNotification("Show Container Add View");
            Preconditions.checkNotNull(parent, "parent is null: %s", parent);
            TreeDataSource treeDataSource;
            try {
                treeDataSource = new TreeDataSourceImpl(repositories.context().findAllWithChildrenInfo());
                treeDataSource.init();
                new ContainerAddView(repositories, mainWindow, parent, treeDataSource);
            }
            catch (final EscidocClientException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else {
            navigationMenuBar.getWindow().showNotification("Action " + selectedItem.getText());
        }
    }
}