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
package org.escidoc.browser.elabsmodul.view.subcontent;

import java.util.ArrayList;
import java.util.List;

import org.escidoc.browser.elabsmodul.constants.ELabViewContants;
import org.escidoc.browser.elabsmodul.controller.InstrumentController;
import org.escidoc.browser.elabsmodul.model.InstrumentBean;
import org.escidoc.browser.elabsmodul.view.helper.LabsLayoutHelper;
import org.escidoc.browser.elabsmodul.view.listeners.LabsClientViewEventHandler;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.internal.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.POJOItem;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.item.Item;

public class LabsInstrumentPanel extends Panel
    implements ILabsPanel, ILabsAction {

    private static final long serialVersionUID = -7601252311598579746L;

    private static Logger LOG = LoggerFactory
        .getLogger(LabsInstrumentPanel.class);

    private final String[] PROPERTIES = ELabViewContants.INSTRUMENT_PROPERTIES;

    private final int COMPONENT_COUNT = 9;

    private POJOItem<InstrumentBean> pojoItem = null;

    private InstrumentBean instrumentBean = null;

    private VerticalLayout mainLayout = null;

    private LayoutClickListener clientViewEventHandler = null;

    private ClickListener mouseClickListener = null;

    private List<HorizontalLayout> registeredComponents = null;

    private HorizontalLayout modifiedComponent = null;

    private Window mainWindow = null;

    private HorizontalLayout buttonLayout = null;

    private ResourceProxy resourceProxy;

    private ItemRepository itemRepositories;

    public LabsInstrumentPanel(Window mainWindow, InstrumentBean sourceBean,
        ResourceProxy resourceProxy, ItemRepository itemRepositories) {

        LOG.info("Constructor created.");
        this.mainWindow = mainWindow;
        this.instrumentBean =
            (sourceBean != null) ? sourceBean : new InstrumentBean();
        this.resourceProxy = resourceProxy;
        this.itemRepositories = itemRepositories;

        initialisePanelComponents();
        buildPanelGUI();
        createPanelListener();
        createClickListener();
    }

    private void initialisePanelComponents() {
        this.mainLayout = new VerticalLayout();
        this.pojoItem =
            new POJOItem<InstrumentBean>(instrumentBean, PROPERTIES);
        this.registeredComponents =
            new ArrayList<HorizontalLayout>(COMPONENT_COUNT);

        this.setContent(mainLayout);
        this.setScrollable(true);
    }

    private void buildPanelGUI() {
        this.mainLayout.setStyleName(ELabViewContants.STYLE_ELABS_FORM);
        this.mainLayout.setSpacing(true);
        this.mainLayout.setMargin(true);

        buttonLayout = LabsLayoutHelper.createButtonLayout();
        HorizontalLayout h1 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabViewContants.L_INSTRUMENT_TITLE,
                pojoItem.getItemProperty(ELabViewContants.P_INSTRUMENT_TITLE));
        HorizontalLayout h2 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabViewContants.L_INSTRUMENT_DESC, getPojoItem()
                    .getItemProperty(ELabViewContants.P_INSTRUMENT_DESC));
        HorizontalLayout h3 =
            LabsLayoutHelper
                .createHorizontalLayoutWithELabsLabelAndCheckBoxData(
                    ELabViewContants.L_INSTRUMENT_CONFIGURATION_KEY,
                    ELabViewContants.L_INSTRUMENT_CONFIGURATION_VALUE,
                    getPojoItem().getItemProperty(
                        ELabViewContants.P_INSTRUMENT_CONFIGURATION));
        HorizontalLayout h4 =
            LabsLayoutHelper
                .createHorizontalLayoutWithELabsLabelAndCheckBoxData(
                    ELabViewContants.L_INSTRUMENT_CALIBRATION_KEY,
                    ELabViewContants.L_INSTRUMENT_CALIBRATION_VALUE,
                    getPojoItem().getItemProperty(
                        ELabViewContants.P_INSTRUMENT_CALIBRATION));
        HorizontalLayout h5 =
            LabsLayoutHelper
                .createHorizontalLayoutWithELabsLabelAndLabelData(
                    ELabViewContants.L_INSTRUMENT_ESYNC_DAEMON,
                    getPojoItem().getItemProperty(
                        ELabViewContants.P_INSTRUMENT_ESYNCDAEMON));
        HorizontalLayout h6 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabViewContants.L_INSTRUMENT_FOLDER, getPojoItem()
                    .getItemProperty(ELabViewContants.P_INSTRUMENT_FOLDER));
        HorizontalLayout h7 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabViewContants.L_INSTRUMENT_FILE_FORMAT, getPojoItem()
                    .getItemProperty(ELabViewContants.P_INSTRUMENT_FILEFORMAT));
        HorizontalLayout h8 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabViewContants.L_INSTRUMENT_DEVICE_SUPERVISOR,
                getPojoItem().getItemProperty(
                    ELabViewContants.P_INSTRUMENT_DEVICESUPERVISOR));
        HorizontalLayout h9 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabViewContants.L_INSTRUMENT_INSTITUTE, getPojoItem()
                    .getItemProperty(ELabViewContants.P_INSTRUMENT_INSTITUTE));

        registeredComponents.add(h1);
        registeredComponents.add(h2);
        registeredComponents.add(h3);
        registeredComponents.add(h4);
        registeredComponents.add(h5);
        registeredComponents.add(h6);
        registeredComponents.add(h7);
        registeredComponents.add(h8);
        registeredComponents.add(h9);

        this.mainLayout.addComponent(new VerticalLayout(), 0);
        this.mainLayout.addComponent(h1, 1);
        this.mainLayout.addComponent(h2, 2);
        this.mainLayout.addComponent(h3, 3);
        this.mainLayout.addComponent(h4, 4);
        this.mainLayout.addComponent(h5, 5);
        this.mainLayout.addComponent(h6, 6);
        this.mainLayout.addComponent(h7, 7);
        this.mainLayout.addComponent(h8, 8);
        this.mainLayout.addComponent(h9, 9);

        this.mainLayout.attach();
        this.mainLayout.requestRepaintAll();
    }

    private void createPanelListener() {
        this.clientViewEventHandler =
            new LabsClientViewEventHandler(registeredComponents, mainLayout,
                this, this);
        this.mainLayout.addListener(this.clientViewEventHandler);
    }

    private void createClickListener() {
        this.mouseClickListener = new Button.ClickListener() {

            private static final long serialVersionUID = -8330004043242560612L;

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                if (event.getButton().getCaption().equals("Save")) {
                    LabsInstrumentPanel.this.saveAction();
                }
                else if (event.getButton().getCaption().equals("Cancel")) {
                    // TODO reset function
                }

                LabsInstrumentPanel.this.hideButtonLayout();
            }
        };

        ((Button) this.buttonLayout.getComponent(0))
            .addListener(this.mouseClickListener);
        ((Button) this.buttonLayout.getComponent(1))
            .addListener(this.mouseClickListener);
    }

    @Override
    public void hideButtonLayout() {
        if (mainLayout != null && this.mainLayout.getComponent(0) != null) {
            ((VerticalLayout) this.mainLayout.getComponent(0))
                .removeAllComponents();
        }
    }

    @Override
    public void showButtonLayout() {
        VerticalLayout verticalLayout = null;
        if (mainLayout != null && buttonLayout != null) {
            verticalLayout = (VerticalLayout) this.mainLayout.getComponent(0);
            if (verticalLayout != null) {
                verticalLayout.removeAllComponents();
            }
            verticalLayout.addComponent(buttonLayout, 0);
        }
    }

    @Override
    public Component getModifiedComponent() {
        return this.modifiedComponent;
    }

    @Override
    public void setModifiedComponent(Component modifiedComponent) {
        this.modifiedComponent = (HorizontalLayout) modifiedComponent;
    }

    @Override
    public Window getMainWindow() {
        return this.mainWindow;
    }

    @Override
    public Panel getReference() {
        return this;
    }

    public POJOItem<InstrumentBean> getPojoItem() {
        return pojoItem;
    }

    @Override
    public void saveAction() {
        // TODO
        Item item;
        try {
            item = itemRepositories.findItemById(resourceProxy.getId());
            MetadataRecord metadataRecord =
                item.getMetadataRecords().get("escidoc");
            metadataRecord.setContent(InstrumentController
                .createDOMElementByBeanModel(instrumentBean));
            itemRepositories.update(resourceProxy.getId(), item);

            this.mainWindow.showNotification("Save", "View is saved",
                Notification.TYPE_HUMANIZED_MESSAGE);
        }
        catch (EscidocClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}