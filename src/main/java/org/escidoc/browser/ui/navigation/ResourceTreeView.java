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
 * Copyright 2012 Fachinformationszentrum Karlsruhe Gesellschaft
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
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandListener;

import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;

@SuppressWarnings("serial")
public class ResourceTreeView extends CustomComponent implements NavigationTreeView {

    private final Tree tree = new Tree();

    private ItemClickListener itemClickListener;

    public ResourceTreeView() {
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
    public void setDataSource(final TreeDataSource dataSource) {
        tree.setContainerDataSource(dataSource.getContainer());
        tree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        tree.setItemCaptionPropertyId(PropertyId.NAME);
        tree.setItemIconPropertyId(PropertyId.ICON);
        tree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
    }

    @Override
    public void addActionHandler(final Handler handler) {
        Preconditions.checkNotNull(handler, "handler is null: %s", handler);
        tree.addActionHandler(handler);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((itemClickListener == null) ? 0 : itemClickListener.hashCode());
        result = prime * result + ((tree == null) ? 0 : tree.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResourceTreeView other = (ResourceTreeView) obj;
        if (itemClickListener == null) {
            if (other.itemClickListener != null) {
                return false;
            }
        }
        else if (!itemClickListener.equals(other.itemClickListener)) {
            return false;
        }
        if (tree == null) {
            if (other.tree != null) {
                return false;
            }
        }
        else if (!tree.equals(other.tree)) {
            return false;
        }
        return true;
    }

    @Override
    public Container getDataSource() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public void setClickListener(@SuppressWarnings("unused") ItemClickListener clickListener) {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }
}