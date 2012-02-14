package org.escidoc.browser.ui.view.helpers;

import org.escidoc.browser.ui.ViewConstants;

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

/**
 * This is a table container. <br >
 * It lists a set of elements and provides a remove operation on each element.
 * 
 * @author ajb
 * 
 */
@SuppressWarnings("serial")
public abstract class TableContainerVH extends VerticalLayout {

    protected Table table = new Table();

    protected static final String PROPERTY_NAME = "name";

    protected static final String PROPERTY_VALUE = "value";

    private static final Action ACTION_DELETE = new Action("Delete");

    private static final Action[] ACTIONS_LIST = new Action[] { ACTION_DELETE };

    public TableContainerVH() {
        addComponent(table);
        initializeTable();

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

        // style generator
        table.setCellStyleGenerator(new CellStyleGenerator() {
            @Override
            public String getStyle(Object itemId, Object propertyId) {
                if (PROPERTY_NAME.equals(propertyId)) {
                    return "bold";
                }
                return null;
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
        item.getItemProperty(PROPERTY_NAME).setValue(itemName);
        item.getItemProperty(PROPERTY_VALUE).setValue(itemHref);
        return item;
    }

    /**
     * Just an initialization of the table. Should be overridden
     */
    private void initializeTable() {
        // size
        table.setWidth("100%");
        table.setHeight("170px");
        // selectable
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setImmediate(true); // react at once when something is selected
        // turn on column reordering and collapsing
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
