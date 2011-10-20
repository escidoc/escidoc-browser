package org.escidoc.browser.elabsmodul.views.helpers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.escidoc.browser.elabsmodul.model.InstrumentBean;
import org.escidoc.browser.elabsmodul.model.RigBean;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect.MultiSelectMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.VerticalLayout;

public class LabsTableHelper {

    private final String rigProperty1 = "title", rigProperty2 = "id";

    final Label selectedLabel = new Label("No selection");

    final HashSet<Object> markedContactRows = new HashSet<Object>();

    private Table table = null;

    private Button deleteButton = null, addButton = null;

    private final String ADD_BUTTON = "Add element";

    private final String DELETE_BUTTON = "Delete element";

    private final String DELETES_BUTTON = "Delete all elements";

    private static LabsTableHelper singleton = null;

    private static Object syncObject = new Object();

    private LabsTableHelper() {
    }

    public static LabsTableHelper singleton() {
        if (singleton == null) {
            synchronized (syncObject) {
                if (singleton == null) {
                    singleton = new LabsTableHelper();
                }
            }
        }
        return singleton;
    }

    public synchronized VerticalLayout createTableLayoutForRig(final RigBean rigBean) {
        VerticalLayout layout = new VerticalLayout();
        table = new Table("Related instruments");

        final Action ACTION_MARK = new Action("Mark");
        final Action ACTION_UNMARK = new Action("Unmark");
        final Action ACTION_LOG = new Action("Save");
        final Action[] ACTIONS_UNMARKED = new Action[] { ACTION_MARK, ACTION_LOG };
        final Action[] ACTIONS_MARKED = new Action[] { ACTION_UNMARK, ACTION_LOG };

        table.setWidth("90%");
        table.setHeight("200px");

        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setImmediate(true);
        table.setEditable(false);
        table.setMultiSelectMode(MultiSelectMode.DEFAULT);

        // contactsTable.setStyleName();

        table.setContainerDataSource(fillRigTableData(rigBean.getContentList()));

        table.setColumnReorderingAllowed(false);
        table.setColumnCollapsingAllowed(false);

        // table.setColumnIcon(propertyId, icon
        table.setVisibleColumns(new Object[] { rigProperty1, rigProperty2 });
        table.setColumnHeaders(new String[] { "Instrument's name", "Instrument's ID" });

        table.setColumnIcon(rigProperty1, new ThemeResource("runo/icons/16/email.png"));
        table.setColumnIcon(rigProperty2, new ThemeResource("runo/icons/16/email-reply.png"));

        table.setColumnAlignment(rigProperty1, Table.ALIGN_LEFT);
        table.setColumnAlignment(rigProperty2, Table.ALIGN_LEFT);

        table.setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY);
        table.setWriteThrough(false);

        table.setCellStyleGenerator(new CellStyleGenerator() {
            private static final long serialVersionUID = 6844387492599807143L;

            @Override
            public String getStyle(Object itemId, Object propertyId) {
                if (propertyId == null) {
                    // no propertyId, styling row
                    return (markedContactRows.contains(itemId) ? "marked" : null);
                }
                else if (rigProperty1.equals(propertyId) || rigProperty2.equals(propertyId)) {
                    return "bold";
                }
                else {
                    return null;
                }
            }
        });

        table.addListener(new Table.ValueChangeListener() {
            private static final long serialVersionUID = 2000562132182698589L;

            @Override
            public void valueChange(final ValueChangeEvent event) {
                int selSize = 0;
                Set<?> value = (Set<?>) event.getProperty().getValue();
                if (value == null || value.size() == 0) {
                    selectedLabel.setValue("No selection");
                }
                else {
                    selSize = value.size();
                    if (selSize == 1) {
                        selectedLabel.setValue("Selected: 1 element");
                    }
                    else {
                        selectedLabel.setValue("Selected: " + selSize + " elements");
                    }
                }

                if (selSize == 0) {
                    deleteButton.setEnabled(false);
                }
                else if (selSize == 1) {
                    deleteButton.setEnabled(true);
                    deleteButton.setCaption(DELETE_BUTTON);
                }
                else {
                    deleteButton.setEnabled(true);
                    deleteButton.setCaption(DELETES_BUTTON);
                }
            }
        });
        layout.addComponent(table);
        layout.addComponent(selectedLabel);
        addRigButtonToLayout(layout);

        return layout;
    }

    private void addRigButtonToLayout(final VerticalLayout layout) {

        addButton = new Button(ADD_BUTTON);
        addButton.setEnabled(true);
        addButton.setVisible(true);
        addButton.setIcon(new ThemeResource("runo/icons/16/ok.png"));

        deleteButton = new Button(DELETE_BUTTON);
        deleteButton.setEnabled(false);
        deleteButton.setVisible(true);
        deleteButton.setIcon(new ThemeResource("runo/icons/16/cancel.png"));

        deleteButton.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1586321256611542129L;

            @Override
            public void buttonClick(final ClickEvent event) {
                if (event.getButton().getCaption().equals(DELETE_BUTTON)
                    || event.getButton().getCaption().equals(DELETES_BUTTON)) {

                }
                else if (event.getButton().getCaption().equals(ADD_BUTTON)) {
                    // TODO
                }
            }
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);

        horizontalLayout.addComponent(deleteButton);
        horizontalLayout.addComponent(addButton);
        layout.addComponent(horizontalLayout);
    }

    private IndexedContainer fillRigTableData(List<InstrumentBean> instrumentBeans) {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(rigProperty1, String.class, null);
        container.addContainerProperty(rigProperty2, String.class, null);

        for (Iterator<InstrumentBean> iterator = instrumentBeans.iterator(); iterator.hasNext();) {
            InstrumentBean instrumentBean = iterator.next();

            String id = instrumentBean.getObjectId();
            String title = instrumentBean.getName();
            String desc = instrumentBean.getDescription();
            Item item = container.addItem(id);
            item.getItemProperty(rigProperty1).setValue(title);
            item.getItemProperty(rigProperty2).setValue(id);
        }
        container.sort(new Object[] { rigProperty1 }, new boolean[] { true });

        return container;
    }
}
