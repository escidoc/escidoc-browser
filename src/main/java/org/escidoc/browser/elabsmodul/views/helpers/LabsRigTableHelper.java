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
package org.escidoc.browser.elabsmodul.views.helpers;

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

import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.interfaces.ILabsAction;
import org.escidoc.browser.elabsmodul.interfaces.IRigAction;
import org.escidoc.browser.elabsmodul.model.InstrumentBean;
import org.escidoc.browser.elabsmodul.model.RigBean;
import org.escidoc.browser.elabsmodul.views.AddNewInstrumentsWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Helper class to build data tables related to Rig Element.
 */
public final class LabsRigTableHelper {

    private Button rigDeleteButton = null, rigAddButton = null;

    private Table rigTable = null;

    private RigBean rigBean = null;

    private IndexedContainer rigContainer = null;

    private final static String ADD_BUTTON = "Add element";

    private final static String DELETE_BUTTON_TEXT = "Delete element";

    private final static String DELETES_BUTTON_TEXT = "Delete selected elements";

    private static final String rigProperty1 = "title", rigProperty2 = "id";

    private ILabsAction labsAction = null;

    private static final Logger LOG = LoggerFactory.getLogger(LabsRigTableHelper.class);

    public LabsRigTableHelper(ILabsAction action) {
        Preconditions.checkNotNull(action, "Action is null");
        this.labsAction = action;
    }

    public VerticalLayout createTableLayoutForRig(final RigBean rigBean, final IRigAction controller) {
        Preconditions.checkNotNull(rigBean, "rigModel is null");
        final int RIG_TABLE_SIZE = 5;
        this.rigBean = rigBean;
        final Label selectedLabel = new Label("No selection");
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();
        this.rigTable = new Table();
        this.rigTable.setSelectable(true);
        this.rigTable.setMultiSelect(true);
        this.rigTable.setMultiSelectMode(MultiSelectMode.DEFAULT);
        this.rigTable.setImmediate(true);
        this.rigTable.setPageLength(RIG_TABLE_SIZE);
        this.rigTable.setWidth("350px");
        this.rigTable.setColumnReorderingAllowed(false);
        this.rigTable.setColumnCollapsingAllowed(false);
        this.rigTable.setRowHeaderMode(Table.ROW_HEADER_MODE_HIDDEN);
        this.rigTable.setContainerDataSource(fillRigTableData(rigBean.getContentList()));
        this.rigTable.setColumnHeaders(new String[] { "Name", "Id" }); // put
                                                                       // these
                                                                       // into
                                                                       // ELabsViewContants
        this.rigTable.addListener(new Table.ValueChangeListener() {
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
        layout.addComponent(this.rigTable);
        layout.addComponent(selectedLabel);
        addRigButtonToLayout(layout, controller);
        return layout;
    }

    private void addRigButtonToLayout(final VerticalLayout layout, final IRigAction controller) {
        final Button.ClickListener rigButtonsListener = new Button.ClickListener() {
            private static final long serialVersionUID = 1586321256611542129L;

            @Override
            public void buttonClick(final ClickEvent event) {
                LabsRigTableHelper.this.labsAction.showButtonLayout();
                if (event.getButton().getCaption().equals(DELETE_BUTTON_TEXT)
                    || event.getButton().getCaption().equals(DELETES_BUTTON_TEXT)) {
                    @SuppressWarnings("unchecked")
                    Set<String> selectedIdSet = (Set<String>) rigTable.getValue();
                    LabsRigTableHelper.this.synchronizeRigModel(selectedIdSet);
                    for (Iterator<String> iterator = selectedIdSet.iterator(); iterator.hasNext();) {
                        String idToDelete = iterator.next();
                        rigTable.getContainerDataSource().removeItem(idToDelete);
                    }
                    rigTable.requestRepaint();
                }
                else if (event.getButton().getCaption().equals(ADD_BUTTON)) {
                    rigTable
                        .getApplication()
                        .getMainWindow()
                        .addWindow(
                            new AddNewInstrumentsWindow(rigBean, controller, new AddNewInstrumentsWindow.Callback() {
                                @Override
                                public void onAcceptRigAction(
                                    final List<InstrumentBean> assignableInstruments,
                                    final Set<String> instrumentIdentifiers) {
                                    if (assignableInstruments == null || assignableInstruments.isEmpty()) {
                                        return;
                                    }
                                    if (instrumentIdentifiers == null || instrumentIdentifiers.isEmpty()) {
                                        return;
                                    }
                                    for (Iterator<String> iterator = instrumentIdentifiers.iterator(); iterator
                                        .hasNext();) {
                                        final String id = iterator.next();
                                        for (Iterator<InstrumentBean> iterator2 = assignableInstruments.iterator(); iterator2
                                            .hasNext();) {
                                            InstrumentBean instrumentBean = iterator2.next();
                                            if (instrumentBean.getObjectId().equals(id)) {
                                                LabsRigTableHelper.this.rigBean.getContentList().add(instrumentBean);
                                                LabsRigTableHelper.this.addnewItemToRigTable(instrumentBean);
                                            }
                                        }
                                    }
                                }
                            }));
                }
            }
        };
        this.rigAddButton = new Button(ADD_BUTTON);
        this.rigDeleteButton = new Button(DELETE_BUTTON_TEXT);
        if (controller.hasUpdateAccess()) {
            this.rigAddButton.setEnabled(true);
            this.rigAddButton.setVisible(true);
            this.rigAddButton.setIcon(ELabsViewContants.ICON_16_OK);
            this.rigDeleteButton.setEnabled(false);
            this.rigDeleteButton.setVisible(true);
            this.rigDeleteButton.setIcon(ELabsViewContants.ICON_16_CANCEL);
            this.rigDeleteButton.addListener(rigButtonsListener);
            this.rigAddButton.addListener(rigButtonsListener);
        }
        else {
            this.rigAddButton.setVisible(false);
            this.rigDeleteButton.setVisible(false);
        }
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(this.rigAddButton);
        horizontalLayout.addComponent(this.rigDeleteButton);
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
        this.rigContainer = new IndexedContainer();
        this.rigContainer.addContainerProperty(rigProperty1, String.class, null);
        this.rigContainer.addContainerProperty(rigProperty2, String.class, null);

        for (Iterator<InstrumentBean> iterator = instrumentBeans.iterator(); iterator.hasNext();) {
            InstrumentBean instrumentBean = iterator.next();
            String id = instrumentBean.getObjectId();
            String title = instrumentBean.getName();
            Item item = this.rigContainer.addItem(id);
            if (item != null) {
                item.getItemProperty(rigProperty1).setValue(title);
                item.getItemProperty(rigProperty2).setValue(id);
            }
        }
        this.rigContainer.sort(new Object[] { rigProperty1 }, new boolean[] { true });
        return this.rigContainer;
    }

    private void addnewItemToRigTable(final InstrumentBean instrumentBean) {
        Preconditions.checkNotNull(this.rigContainer, "Rig Container is null");
        Preconditions.checkNotNull(instrumentBean, "Bean is null");
        Item newItem = this.rigContainer.addItem(instrumentBean.getObjectId());
        if (newItem != null) {
            newItem.getItemProperty(rigProperty1).setValue(instrumentBean.getName());
            newItem.getItemProperty(rigProperty2).setValue(instrumentBean.getObjectId());
        }
        this.rigTable.requestRepaint();
    }
}
