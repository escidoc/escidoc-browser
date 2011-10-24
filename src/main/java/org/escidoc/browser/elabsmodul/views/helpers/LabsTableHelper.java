package org.escidoc.browser.elabsmodul.views.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.model.InstrumentBean;
import org.escidoc.browser.elabsmodul.model.RigBean;
import org.escidoc.browser.elabsmodul.views.AddNewInstrumentsWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.AbstractSelect.MultiSelectMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class LabsTableHelper {

    // properties for the tables def
    private final String rigProperty1 = "title", rigProperty2 = "id";

    // buttons related to the tables
    private Button rigDeleteButton = null, rigAddButton = null;

    // table references
    private Table rigTable = null; // table on the RigView

    private Table studyTable = null; // table on the StudyView and so on...

    // model references
    private RigBean rigBean = null;

    // common texts
    private final String ADD_BUTTON = "Add element";

    private final String DELETE_BUTTON_TEXT = "Delete element";

    private final String DELETES_BUTTON_TEXT = "Delete all elements";

    private static LabsTableHelper singleton = null;

    private static Object syncObject = new Object();

    private static final Logger LOG = LoggerFactory.getLogger(LabsTableHelper.class);

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
        Preconditions.checkNotNull(rigBean, "rigModel is null");
        this.rigBean = rigBean;

        final int RIGTABLESIZE = 5;
        final Label selectedLabel = new Label("No selection");
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();

        rigTable = new Table();
        rigTable.setSelectable(true);
        rigTable.setMultiSelect(true);
        rigTable.setMultiSelectMode(MultiSelectMode.DEFAULT);
        rigTable.setImmediate(true);
        rigTable.setPageLength(RIGTABLESIZE);

        rigTable.setColumnReorderingAllowed(false);
        rigTable.setColumnCollapsingAllowed(false);
        rigTable.setRowHeaderMode(Table.ROW_HEADER_MODE_HIDDEN);

        rigTable.setContainerDataSource(fillRigTableData(rigBean.getContentList()));
        rigTable.setColumnHeaders(new String[] { "Name", "Id" }); // put these into ELabsViewContants

        rigTable.addListener(new Table.ValueChangeListener() {
            private static final long serialVersionUID = 2000562132182698589L;

            @Override
            public void valueChange(final ValueChangeEvent event) {
                int selSize = 0;
                Set<?> values = null;
                try {
                    values = (Set<?>) event.getProperty().getValue();
                }
                catch (ClassCastException e) {
                    LOG.warn("Table should be multiselectable!", e.getMessage());
                }
                if (values == null || values.size() == 0) {
                    selectedLabel.setValue("No selection");
                }
                else {
                    selSize = values.size();
                    selectedLabel.setValue("Selected: " + selSize + " element" + ((selSize > 1) ? "s" : ""));
                }

                if (selSize == 0) {
                    rigDeleteButton.setEnabled(false);
                }
                else if (selSize == 1) {
                    rigDeleteButton.setEnabled(true);
                    rigDeleteButton.setCaption(DELETE_BUTTON_TEXT);
                }
                else {
                    rigDeleteButton.setEnabled(true);
                    rigDeleteButton.setCaption(DELETES_BUTTON_TEXT);
                }
            }
        });
        layout.addComponent(rigTable);
        layout.addComponent(selectedLabel);
        addRigButtonToLayout(layout);
        return layout;
    }

    private void addRigButtonToLayout(final VerticalLayout layout) {

        rigAddButton = new Button(ADD_BUTTON);
        rigAddButton.setEnabled(true);
        rigAddButton.setVisible(true);
        rigAddButton.setIcon(ELabsViewContants.ICON_16_OK);

        rigDeleteButton = new Button(DELETE_BUTTON_TEXT);
        rigDeleteButton.setEnabled(false);
        rigDeleteButton.setVisible(true);
        rigDeleteButton.setIcon(ELabsViewContants.ICON_16_CANCEL);

        Button.ClickListener rigButtonsListener = new Button.ClickListener() {
            private static final long serialVersionUID = 1586321256611542129L;

            @Override
            public void buttonClick(final ClickEvent event) {
                if (event.getButton().getCaption().equals(DELETE_BUTTON_TEXT)
                    || event.getButton().getCaption().equals(DELETES_BUTTON_TEXT)) {
                    @SuppressWarnings("unchecked")
                    Set<String> selectedIdSet = (Set<String>) rigTable.getValue();

                    // delete relations from the model
                    LabsTableHelper.this.synchronizeRigModel(selectedIdSet);

                    // instant delete from table containerdatasource
                    for (Iterator<String> iterator = selectedIdSet.iterator(); iterator.hasNext();) {
                        String idToDelete = iterator.next();
                        rigTable.getContainerDataSource().removeItem(idToDelete);
                    }
                    rigTable.requestRepaint();
                }
                else if (event.getButton().getCaption().equals(ADD_BUTTON)) {
                    rigTable
                        .getApplication().getMainWindow()
                        .addWindow(new AddNewInstrumentsWindow(rigBean, new AddNewInstrumentsWindow.Callback() {

                            @Override
                            public void onAcceptRigAction(Set<String> instrumentIdentifiers) {

                            }
                        }));
                }
            }
        };

        rigDeleteButton.addListener(rigButtonsListener);
        rigAddButton.addListener(rigButtonsListener);
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);

        horizontalLayout.addComponent(rigDeleteButton);
        horizontalLayout.addComponent(rigAddButton);
        layout.addComponent(horizontalLayout);
    }

    private void synchronizeRigModel(final Set<String> selectedElements) {
        List<InstrumentBean> beanToDelete = new ArrayList<InstrumentBean>();
        for (Iterator<InstrumentBean> iterator = this.rigBean.getContentList().iterator(); iterator.hasNext();) {
            InstrumentBean bean = iterator.next();
            if (selectedElements.contains(bean.getObjectId())) {
                beanToDelete.add(bean);
            }
        }
        this.rigBean.getContentList().removeAll(beanToDelete);
    }

    private IndexedContainer fillRigTableData(List<InstrumentBean> instrumentBeans) {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(rigProperty1, String.class, null);
        container.addContainerProperty(rigProperty2, String.class, null);

        for (Iterator<InstrumentBean> iterator = instrumentBeans.iterator(); iterator.hasNext();) {
            InstrumentBean instrumentBean = iterator.next();
            String id = instrumentBean.getObjectId();
            String title = instrumentBean.getName();
            Item item = container.addItem(id);
            if (id != null) {
                item.getItemProperty(rigProperty1).setValue(title);
                item.getItemProperty(rigProperty2).setValue(id);
            }
        }
        container.sort(new Object[] { rigProperty1 }, new boolean[] { true });
        return container;
    }
}
