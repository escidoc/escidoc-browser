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
package org.escidoc.browser.ui.view.helpers;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.ui.ViewConstants;

/**
 * This is a table container. <br >
 * It lists a set of elements and provides a remove operation on each element.
 * 
 * @author arb
 * 
 */
@SuppressWarnings("serial")
public abstract class TableContainerVH extends VerticalLayout {

    protected Table table = new Table();

    private Action ACTION_DELETE = new Action("Delete");

    private Action[] ACTIONS_LIST = new Action[] { ACTION_DELETE };

    protected Controller controller;

    public TableContainerVH() {
        addComponent(table);
        initializeTable();
        if (hasRightstoContextMenu()) {
            addActionLists();
        }
        // style generator
        table.setCellStyleGenerator(new CellStyleGenerator() {
            @Override
            public String getStyle(Object itemId, Object propertyId) {
                if (ViewConstants.PROPERTY_NAME.equals(propertyId)) {
                    return "bold";
                }
                return null;
            }
        });
    }

    protected abstract boolean hasRightstoContextMenu();

    protected void addActionLists() {
        // Actions (a.k.a context menu)
        table.addActionHandler(new Action.Handler() {
            @Override
            public Action[] getActions(Object target, Object sender) {
                return ACTIONS_LIST;
            }

            @Override
            public void handleAction(Action action, Object sender, Object target) {
                if (ACTION_DELETE == action) {
                    confirmActionWindow(target);
                }
            }
        });
    }

    /**
     * This method should be implemented in classes that extend this class. It handles the operation of the delete in
     * communication with the core
     * 
     * @param target
     */
    protected abstract void removeAction(Object target);

    public Table getTable() {
        return table;
    }

    public Item createItem(HierarchicalContainer tableContainer, String itemId, String itemName, String itemHref) {
        Item item = tableContainer.addItem(itemId);
        item.getItemProperty(ViewConstants.PROPERTY_NAME).setValue(itemName);
        item.getItemProperty(ViewConstants.PROPERTY_VALUE).setValue(itemHref);
        return item;
    }

    /**
     * Just an initialization of the table. Should be overridden
     */
    protected void initializeTable() {
        table.setWidth("100%");
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setImmediate(true);
        table.setColumnReorderingAllowed(true);
        table.setColumnCollapsingAllowed(true);
    }

    /**
     * Populate the table with some values
     * 
     * @return
     */
    protected abstract HierarchicalContainer populateContainerTable();

    public void confirmActionWindow(final Object target) {
        final Window subwindow = new Window(ViewConstants.DELETE_RESOURCE_WINDOW_NAME);
        subwindow.setModal(true);
        subwindow.setWidth("500px");

        VerticalLayout layout = (VerticalLayout) subwindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        Label message = new Label(ViewConstants.QUESTION_DELETE_RESOURCE);
        subwindow.addComponent(message);
        Button okBtn = new Button("Yes Remove", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                removeAction(target);
                table.refreshRowCache();
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        Button cancelBtn = new Button("Cancel", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);

            }
        });
        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(okBtn);
        hl.addComponent(cancelBtn);
        layout.addComponent(hl);

        this.getApplication().getMainWindow().addWindow(subwindow);
    }

}
