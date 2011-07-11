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

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.listeners.TreeClickListener;
import org.escidoc.browser.ui.navigation.menubar.NavigationMenuBar;

import com.google.common.base.Preconditions;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandListener;

@SuppressWarnings("serial")
public class NavigationTreeViewImpl extends CustomComponent implements NavigationTreeView {

    private final Tree tree = new Tree();

    private ItemClickListener itemClickListener;

    public NavigationTreeViewImpl(final Repositories repositories, final CurrentUser currentUser) {
        setCompositionRoot(tree);
        tree.setImmediate(true);
    }

    @Override
    public void addClickListener(final ItemClickListener clickListener) {
        itemClickListener = clickListener;
        tree.addListener(clickListener);
    }

    @Override
    public void addExpandListener(final ExpandListener expandListener) {
        tree.addListener(expandListener);
    }

    @Override
    public ResourceModel getSelected() {
        return (ResourceModel) tree.getValue();
    }

    @Override
    public void setDataSource(final TreeDataSource dataSource, final MainSite mainSite) {
        tree.setContainerDataSource(dataSource.getContainer());
        tree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        tree.setItemCaptionPropertyId(PropertyId.NAME);
        tree.setItemIconPropertyId(PropertyId.ICON);
        tree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
    }

    @Override
    public void withNavigationMenuBar(final NavigationMenuBar navigationMenuBar) {
        Preconditions.checkNotNull(navigationMenuBar, "navigationMenuBar is null: %s", navigationMenuBar);
        ((TreeClickListener) itemClickListener).withNavigationMenuBar(navigationMenuBar);
    }

    @Override
    public void addActionHandler(final Handler handler) {
        Preconditions.checkNotNull(handler, "handler is null: %s", handler);
        tree.addActionHandler(handler);
    }
}