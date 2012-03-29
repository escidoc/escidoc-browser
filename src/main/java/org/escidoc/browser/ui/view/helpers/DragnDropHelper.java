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

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.And;
import com.vaadin.event.dd.acceptcriteria.ClientSideCriterion;
import com.vaadin.event.dd.acceptcriteria.SourceIs;
import com.vaadin.ui.AbstractSelect.AcceptItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.ui.Router;

import de.escidoc.core.client.exceptions.EscidocClientException;

/**
 * This class can be used for any Drag n Drop functionality
 * 
 * @author ajb
 * 
 */
public abstract class DragnDropHelper extends VerticalLayout {

    protected static Object PROPERTY_HREF = "link";

    protected static String PROPERTY_NAME = "name";

    private Table table;

    private Table tableDelete;

    private TextField tf;

    protected Tree tree;

    protected Controller controller;

    protected ResourceProxy resourceProxy;

    protected Router router;

    public DragnDropHelper() {
        super();
    }

    /**
     * This is the container for the Recycle Bin
     * 
     * @param acceptCriterion
     * @throws EscidocClientException
     */
    private void initializeDeleteTable(final ClientSideCriterion acceptCriterion) throws EscidocClientException {
        tableDelete.setWidth("170px");
        tableDelete.setHeight("200px");
        tableDelete.setStyleName(Reindeer.TABLE_BORDERLESS);
        tableDelete.addStyleName("deleteTable");
        tableDelete.addContainerProperty("Drag here to remove", String.class, null);
        tableDelete.setDropHandler(new DropHandler() {
            @Override
            public void drop(DragAndDropEvent dropEvent) {
                DataBoundTransferable t = (DataBoundTransferable) dropEvent.getTransferable();
                if (!(t.getSourceContainer() instanceof Container.Hierarchical)) {
                    return;
                }
                Container.Hierarchical source = (Container.Hierarchical) t.getSourceContainer();
                source.removeItem(t.getItemId());
                Object sourceItemId = t.getItemId();
                removeElementController(sourceItemId);
            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return new And(acceptCriterion, AcceptItem.ALL);
            }
        });

    }

    /**
     * This is the table where the elements are added by drag'n drop
     * 
     * @param acceptCriterion
     * @throws EscidocClientException
     */
    private void initializeTable(final ClientSideCriterion acceptCriterion) throws EscidocClientException {
        final HierarchicalContainer tableContainer = populateContainerTable();
        table.setContainerDataSource(tableContainer);
        table.setItemCaptionPropertyId(PROPERTY_NAME);
        table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);

        table.setVisibleColumns(new Object[] { PROPERTY_NAME, PROPERTY_HREF });

        // Handle drop in table: move hardware item or subtree to the table
        table.setDragMode(TableDragMode.ROW);
        table.setDropHandler(new DropHandler() {
            @Override
            public void drop(DragAndDropEvent dropEvent) {
                // criteria verify that this is safe
                DataBoundTransferable t = (DataBoundTransferable) dropEvent.getTransferable();
                if (!(t.getSourceContainer() instanceof Container.Hierarchical)) {
                    return;
                }
                Container.Hierarchical source = (Container.Hierarchical) t.getSourceContainer();
                Object sourceItemId = t.getItemId();
                if (tableContainer.getItem(sourceItemId) == null) {
                    createItem(tableContainer, sourceItemId.toString(), getTreeNodeName(source, sourceItemId),
                        getTreeNodeHref(source, sourceItemId));
                    addOnController(sourceItemId);
                }
            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return new And(acceptCriterion, AcceptItem.ALL);
            }
        });
    }

    protected void initializeTree(final ClientSideCriterion acceptCriterion) throws EscidocClientException {
        treeFilter();
        tree.setContainerDataSource(populateContainerTree());
        tree.setItemCaptionPropertyId(PROPERTY_NAME);
        // Expand all nodes
        // for (Object name : tree.rootItemIds()) {
        // tree.expandItemsRecursively(name);
        // }
        tree.setDragMode(TreeDragMode.NODE);

        addActionListener();
    }

    @SuppressWarnings("serial")
    private void treeFilter() {
        tf = new TextField();
        tf.addListener(new TextChangeListener() {
            SimpleStringFilter filter = null;

            @Override
            public void textChange(TextChangeEvent event) {
                Filterable f = (Filterable) tree.getContainerDataSource();

                // Remove old filter
                if (filter != null) {
                    f.removeContainerFilter(filter);
                }
                // Set new filter for the "caption" property
                filter = new SimpleStringFilter(PROPERTY_NAME, event.getText(), true, false);
                f.addContainerFilter(filter);
            }
        });
    }

    /**
     * Create the item in the tree
     * 
     * @param tableContainer
     * @param itemId
     * @param itemName
     * @param itemHref
     * @return Item
     */
    private static Item createItem(HierarchicalContainer tableContainer, String itemId, String itemName, String itemHref) {
        Item item = tableContainer.addItem(itemId);
        item.getItemProperty(PROPERTY_NAME).setValue(itemName);
        item.getItemProperty(PROPERTY_HREF).setValue(itemHref);
        return item;
    }

    protected void createDragComponents() throws EscidocClientException {
        tree = new Tree();
        table = new Table("Drag from table to tree");
        table.setWidth("100%");
        tableDelete = new Table();

        initializeTree(new SourceIs(table));
        initializeTable(new SourceIs(tree));

        Panel pn = new Panel();
        pn.setHeight("100%");
        VerticalLayout vlTree = new VerticalLayout();
        vlTree.addComponent(new Label("Drag from Tree to Table to add new OU"));

        vlTree.addComponent(tf);
        vlTree.addComponent(tree);

        HorizontalLayout hl = new HorizontalLayout();
        pn.setContent(vlTree);
        hl.addComponent(pn);

        VerticalLayout vl = new VerticalLayout();
        vl.setWidth("100%");
        hl.addComponent(vl);
        vl.addComponent(table);

        if (canRemoveOperation()) {
            initializeDeleteTable(new SourceIs(table));
            vl.addComponent(tableDelete);
            vl.setComponentAlignment(tableDelete, Alignment.TOP_RIGHT);
        }
        addComponent(hl);

    }

    protected void addActionListener() throws EscidocClientException {

    }

    /**
     * Check if the remove operation can be handled<br />
     * Normally should forward the request to the controller that can provide that information
     * 
     * @return boolean
     */
    protected abstract boolean canRemoveOperation();

    /**
     * Populate the table with elements<br />
     * Example: Add already existing OUs to the Table
     * 
     * @return HierarchicalContainer
     * @throws EscidocClientException
     */
    protected abstract HierarchicalContainer populateContainerTable() throws EscidocClientException;

    /**
     * Populate the tree with elements<br />
     * Normally should load all the specific elements that exist in the repository<br />
     * Example: Populate the tree with all the OUs
     * 
     * @return HierarchicalContainer
     * @throws EscidocClientException
     */
    protected abstract HierarchicalContainer populateContainerTree() throws EscidocClientException;

    /**
     * Once an element is deleted from the Table, the same should be done in the infrastructure. This method should be
     * implemented so that the Controller can remove the element from the infrastructure
     * 
     * @param sourceItemId
     */
    protected abstract void removeElementController(Object sourceItemId);

    /**
     * Once an element is added in the Table, the same should be done in the infrastructure.
     * 
     * @param sourceItemId
     */
    protected abstract void addOnController(Object sourceItemId);

    private static String getTreeNodeHref(Container.Hierarchical source, Object sourceId) {
        return (String) source.getItem(sourceId).getItemProperty(PROPERTY_HREF).getValue();
    }

    private static String getTreeNodeName(Container.Hierarchical source, Object sourceId) {
        return (String) source.getItem(sourceId).getItemProperty(PROPERTY_NAME).getValue();
    }

}