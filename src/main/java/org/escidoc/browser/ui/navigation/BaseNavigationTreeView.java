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

import com.google.common.base.Preconditions;

import com.vaadin.data.Container;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandListener;
import com.vaadin.ui.VerticalLayout;

import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.ui.orgunit.Reloadable;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class BaseNavigationTreeView extends VerticalLayout implements NavigationTreeView, Reloadable {

    private Tree tree = new Tree();

    private TreeDataSource dataSource;

    public BaseNavigationTreeView() {
        setSizeFull();
        tree.setSizeFull();
        tree.setSelectable(true);
        tree.setImmediate(true);
        tree.setNullSelectionAllowed(false);
        addComponent(tree);
    }

    @Override
    public void setDataSource(TreeDataSource dataSource) {
        this.dataSource = dataSource;
        tree.setContainerDataSource(dataSource.getContainer());
        tree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        tree.setItemCaptionPropertyId(PropertyId.NAME);
        tree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
    }

    @Override
    public void reload() throws EscidocClientException {
        final boolean isSucessful = tree.removeAllItems();
        if (isSucessful) {
            dataSource.reload();
        }
    }

    @Override
    public ResourceModel getSelected() {
        return (ResourceModel) tree.getValue();
    }

    @Override
    public void addClickListener(ItemClickListener clickListener) {
        tree.addListener(clickListener);
    }

    @Override
    public void addExpandListener(ExpandListener clickListener) {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public void addActionHandler(Handler handler) {
        Preconditions.checkNotNull(handler, "handler is null: %s", handler);
        tree.addActionHandler(handler);
    }

    @Override
    public Container getDataSource() {
        return dataSource.getContainer();
    }

    @Override
    public void setClickListener(ItemClickListener clickListener) {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }
}