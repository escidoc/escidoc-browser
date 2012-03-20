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
package org.escidoc.browser.elabsmodul.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.interfaces.IRigAction;
import org.escidoc.browser.elabsmodul.model.InstrumentBean;
import org.escidoc.browser.elabsmodul.model.RigBean;

import com.google.common.base.Preconditions;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.AbstractSelect.MultiSelectMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AddNewInstrumentsWindow extends Window implements Button.ClickListener {

    private static final long serialVersionUID = 2444046110793999051L;

    private Callback callback;

    private Button okButton = null, cancelButton = null;

    private Table addOnTable = null;

    private RigBean rigBean = null;

    private final IRigAction rigController;

    private List<InstrumentBean> assignableInstruments = null;

    private static final String property1 = "title", property2 = "description", property3 = "id";

    private static final String ADD_BUTTON_TEXT = "Add element", ADD_ALL_BUTTON_TEXT = "Add selected elements",
        CANCEL_BUTTON_TEXT = "Cancel";

    public AddNewInstrumentsWindow(RigBean rigBean, final IRigAction controller, final Callback callback) {
        Preconditions.checkNotNull(rigBean, "rigModel is null");
        Preconditions.checkNotNull(controller, "rigController is null");
        Preconditions.checkNotNull(callback, "callback is null");

        this.callback = callback;
        this.rigBean = rigBean;
        this.rigController = controller;

        setModal(true);
        setWidth("650px");
        setHeight("450px");
        setClosable(true);
        setResizable(true);
        setScrollable(false);
        addComponent(buildGUI());
        center();
    }

    private Component buildGUI() {
        final VerticalLayout rootLayout = new VerticalLayout();
        rootLayout.addComponent(createAvailableInsturmentsTableLayout());
        rootLayout.addComponent(createButtonLayout());
        return rootLayout;
    }

    private VerticalLayout createAvailableInsturmentsTableLayout() {
        VerticalLayout layout = new VerticalLayout();
        addOnTable = new Table("Available instuments to choose");
        addOnTable.setWidth("90%");
        addOnTable.setHeight("300px");
        addOnTable.setSelectable(true);
        addOnTable.setMultiSelect(true);
        addOnTable.setImmediate(true);
        addOnTable.setEditable(false);
        addOnTable.setMultiSelectMode(MultiSelectMode.DEFAULT);
        addOnTable.setColumnReorderingAllowed(true);
        addOnTable.setColumnCollapsingAllowed(false);
        addOnTable.setContainerDataSource(fillAddOnTableData(this.rigController, rigBean.getContentList()));
        addOnTable.setVisibleColumns(new Object[] { property1, property2, property3 });
        addOnTable.setColumnHeaders(new String[] { "Name", "Description", "Id" });
        addOnTable.setColumnAlignment(property1, Table.ALIGN_LEFT);
        addOnTable.setColumnAlignment(property2, Table.ALIGN_LEFT);
        addOnTable.setColumnAlignment(property3, Table.ALIGN_CENTER);
        addOnTable.setColumnCollapsingAllowed(true);
        addOnTable.setColumnCollapsed(property1, false);
        addOnTable.setColumnCollapsed(property2, false);
        addOnTable.setColumnCollapsed(property3, false);
        addOnTable.setRowHeaderMode(Table.ROW_HEADER_MODE_HIDDEN);
        addOnTable.setWriteThrough(false);

        final Label selectedLabel = new Label("No selection");
        addOnTable.addListener(new Table.ValueChangeListener() {
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
                    selectedLabel.setValue("Selected: " + selSize + " element" + ((selSize > 1) ? "s" : ""));
                }

                if (selSize == 0) {
                    okButton.setEnabled(false);
                }
                else if (selSize == 1) {
                    okButton.setEnabled(true);
                    okButton.setCaption(ADD_BUTTON_TEXT);
                }
                else {
                    okButton.setEnabled(true);
                    okButton.setCaption(ADD_ALL_BUTTON_TEXT);
                }
            }
        });
        layout.addComponent(addOnTable);
        layout.addComponent(selectedLabel);

        return layout;
    }

    private Container fillAddOnTableData(final IRigAction rigController, final List<InstrumentBean> contentList) {
        Preconditions.checkNotNull(rigController, "RigController is null");
        Preconditions.checkNotNull(contentList, "InstumentList is null");

        final List<String> storedInstrumentIDs = new ArrayList<String>();
        for (Iterator<InstrumentBean> iterator = contentList.iterator(); iterator.hasNext();) {
            InstrumentBean instrumentBean = iterator.next();
            storedInstrumentIDs.add(instrumentBean.getObjectId());
        }
        assignableInstruments = rigController.getNewAvailableInstruments(storedInstrumentIDs);

        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(property1, String.class, null);
        container.addContainerProperty(property2, String.class, null);
        container.addContainerProperty(property3, String.class, null);

        for (Iterator<InstrumentBean> iterator = assignableInstruments.iterator(); iterator.hasNext();) {
            InstrumentBean instrumentBean = iterator.next();
            final String id = instrumentBean.getObjectId(), title = instrumentBean.getName(), description =
                (instrumentBean.getDescription() == null || instrumentBean.getDescription().equals("")) ? ELabsViewContants.RIG_NO_DESCRIPTION_BY_INSTR : instrumentBean
                    .getDescription();
            Item item = container.addItem(id);
            item.getItemProperty(property1).setValue(title);
            item.getItemProperty(property2).setValue(description);
            item.getItemProperty(property3).setValue(id);
        }
        container.sort(new Object[] { property1 }, new boolean[] { true });
        return container;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        okButton = new Button(ADD_BUTTON_TEXT, this);
        okButton.setIcon(ELabsViewContants.ICON_16_OK);
        okButton.setEnabled(false);
        cancelButton = new Button(CANCEL_BUTTON_TEXT, this);
        cancelButton.setIcon(ELabsViewContants.ICON_16_CANCEL);
        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(okButton);

        return buttonLayout;
    }

    @Override
    public void buttonClick(final ClickEvent event) {
        if (getParent() != null) {
            getParent().removeWindow(this);
        }

        if (event.getButton().getCaption().equals(ADD_BUTTON_TEXT)
            || event.getButton().getCaption().equals(ADD_ALL_BUTTON_TEXT)) {
            @SuppressWarnings("unchecked")
            final Set<String> selectedIdSet = (Set<String>) addOnTable.getValue();
            callback.onAcceptRigAction(assignableInstruments, selectedIdSet);
        }
    }

    public interface Callback {
        void onAcceptRigAction(final List<InstrumentBean> assignableInstruments, final Set<String> instrumentIdentifiers);
    }
}
