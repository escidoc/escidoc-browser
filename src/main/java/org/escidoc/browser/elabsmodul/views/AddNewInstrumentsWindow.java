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

import java.util.List;
import java.util.Set;

import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
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

    private final String property1 = "id", property2 = "title", property3 = "description";

    private final String ADD_BUTTON_TEXT = "Add element", ADD_ALL_BUTTON_TEXT = "Add all elements",
        CANCEL_BUTTON_TEXT = "Cancel";

    public AddNewInstrumentsWindow(RigBean rigBean, Callback callback) {
        super("Available Instruments");

        Preconditions.checkNotNull(rigBean, "rigModel is null");
        Preconditions.checkNotNull(callback, "callback is null");

        this.callback = callback;
        this.rigBean = rigBean;

        setModal(true);
        setWidth("50%");
        setHeight("500px");
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

        // contactsTable.setStyleName();
        addOnTable.setContainerDataSource(fillAddOnTableData(rigBean.getContentList()));
        addOnTable.setVisibleColumns(new Object[] { property1, property2, property3 });
        addOnTable
            .setColumnHeaders(new String[] { "Instrument's ID", "Instrument's name", "Instrument's description" });

        addOnTable.setColumnIcon(property1, ELabsViewContants.ICON_16_GLOBE);
        addOnTable.setColumnIcon(property2, ELabsViewContants.ICON_16_USERS);
        addOnTable.setColumnIcon(property3, ELabsViewContants.ICON_16_NOTE);

        addOnTable.setColumnAlignment(property1, Table.ALIGN_CENTER);
        addOnTable.setColumnAlignment(property2, Table.ALIGN_LEFT);
        addOnTable.setColumnAlignment(property3, Table.ALIGN_LEFT);

        addOnTable.setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY);
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
                    okButton.setCaption(ADD_BUTTON_TEXT);
                }
            }
        });
        layout.addComponent(addOnTable);
        layout.addComponent(selectedLabel);

        return layout;
    }

    private Container fillAddOnTableData(List<InstrumentBean> contentList) {

        Preconditions.checkNotNull(contentList, "InstumentList is null");

        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(property1, String.class, null);
        container.addContainerProperty(property2, String.class, null);
        container.addContainerProperty(property3, String.class, null);

        // TODO fill from proper dataSource
        Item item = container.addItem("escidoc;110011");
        item.getItemProperty(property1).setValue("escidoc;110011");
        item.getItemProperty(property2).setValue("New Instrument 01");
        item.getItemProperty(property3).setValue("BWeLabs not used instrument");
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

    protected void synchronizeWithRigModel(Set<String> selectedIdSet) {
        // TODO Add newly selected items to the rigmodel
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
            callback.onAcceptRigAction(selectedIdSet);
        }
    }

    public interface Callback {
        void onAcceptRigAction(Set<String> instrumentIdentifiers);
    }
}