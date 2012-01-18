package org.escidoc.browser.ui.view.helpers;

import java.util.Set;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.VerticalLayout;

/**
 * This is a table container. <br >
 * It lists a set of elements and provides a remove operation on each element.
 * 
 * @author ajb
 * 
 */
public abstract class TableContainer extends VerticalLayout {
    Table table = new Table();

    static final Action ACTION_DELETE = new Action("Delete");

    static final Action[] ACTIONS_LIST = new Action[] { ACTION_DELETE };

    protected static final String PROPERTY_NAME = "name";

    protected static final String PROPERTY_VALUE = "value";

    public TableContainer() {
        addComponent(table);
        final Label selected = new Label("No selection");
        addComponent(selected);
        initializeTable();

        // Actions (a.k.a context menu)
        table.addActionHandler(new Action.Handler() {
            public Action[] getActions(Object target, Object sender) {
                return ACTIONS_LIST;
            }

            public void handleAction(Action action, Object sender, Object target) {
                if (ACTION_DELETE == action) {
                    removeAction(target);
                    table.refreshRowCache();
                }
            }
        });

        // style generator
        table.setCellStyleGenerator(new CellStyleGenerator() {
            public String getStyle(Object itemId, Object propertyId) {
                if (PROPERTY_NAME.equals(propertyId)) {
                    return "bold";
                }
                else {
                    // no style
                    return null;
                }

            }
        });

        // listen for valueChange, a.k.a 'select' and update the label
        table.addListener(new Table.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                // in multiselect mode, a Set of itemIds is returned,
                // in singleselect mode the itemId is returned directly
                Set<?> value = (Set<?>) event.getProperty().getValue();
                if (null == value || value.size() == 0) {
                    selected.setValue("No selection");
                }
                else {
                    selected.setValue("Selected: " + table.getValue());
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
}
