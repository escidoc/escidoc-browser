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

import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.interfaces.ILabsAction;
import org.escidoc.browser.elabsmodul.interfaces.ILabsPanel;
import org.escidoc.browser.elabsmodul.interfaces.ISaveAction;
import org.escidoc.browser.elabsmodul.model.InstrumentBean;
import org.escidoc.browser.elabsmodul.views.helpers.LabsLayoutHelper;
import org.escidoc.browser.elabsmodul.views.helpers.ResourcePropertiesViewHelper;
import org.escidoc.browser.elabsmodul.views.listeners.LabsClientViewEventHandler;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.util.POJOItem;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

/**
 * Specific BWeLabsView for Instrument item-element.
 */
public class InstrumentView extends Panel implements ILabsPanel, ILabsAction {

    private static final long serialVersionUID = -7601252311598579746L;

    private static Logger LOG = LoggerFactory.getLogger(InstrumentView.class);

    private final String[] PROPERTIES = ELabsViewContants.INSTRUMENT_PROPERTIES;

    final String VIEWCAPTION = "Instument View";

    final String LAST_MODIFIED_BY = "Last modification by ";

    final String FLOAT_LEFT = "floatleft";

    final String FLOAT_RIGHT = "floatright";

    private final int COMPONENT_COUNT = 9;

    private POJOItem<InstrumentBean> pojoItem = null;

    private InstrumentBean instrumentBean = null;

    private VerticalLayout mainLayout = null, dynamicLayout = null;

    private LayoutClickListener clientViewEventHandler = null;

    private ClickListener mouseClickListener = null;

    private List<HorizontalLayout> registeredComponents = null;

    private HorizontalLayout modifiedComponent = null;

    private HorizontalLayout buttonLayout = null;

    private final ISaveAction controller;

    private List<ResourceModel> breadCrumbModel;

    private ItemProxy itemProxy;

    private EscidocServiceLocation serviceLocation;

    public InstrumentView(InstrumentBean sourceBean, ISaveAction controller, List<ResourceModel> breadCrumbModel,
        ResourceProxy resourceProxy, List<String> eSyncDaemonUrls, EscidocServiceLocation serviceLocation) {

        instrumentBean = (sourceBean != null) ? sourceBean : new InstrumentBean();
        this.controller = controller;
        this.breadCrumbModel = breadCrumbModel;
        itemProxy = (ItemProxy) resourceProxy;
        this.serviceLocation = serviceLocation;

        initialisePanelComponents();
        buildPropertiesGUI();
        buildPanelGUI();
        if (controller.hasUpdateAccess()) {
            createPanelListener();
            createClickListener();
        }
    }

    private void initialisePanelComponents() {

        mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        dynamicLayout = new VerticalLayout();
        dynamicLayout.setSpacing(true);
        dynamicLayout.setMargin(true);

        pojoItem = new POJOItem<InstrumentBean>(instrumentBean, PROPERTIES);
        registeredComponents = new ArrayList<HorizontalLayout>(COMPONENT_COUNT);

        this.setSizeFull();
        this.setStyleName(Runo.PANEL_LIGHT);
        setContent(mainLayout);
        setScrollable(true);
    }

    /**
     * Build the read-only layout of the eLabsElement
     */
    private void buildPropertiesGUI() {
        addComponent(new ResourcePropertiesViewHelper(itemProxy, breadCrumbModel, "Instrument", serviceLocation)
            .generatePropertiesView());
    }

    /**
     * Build the specific editable layout of the eLabsElement.
     */
    private void buildPanelGUI() {
        dynamicLayout.setStyleName(ELabsViewContants.STYLE_ELABS_FORM);

        buttonLayout = LabsLayoutHelper.createButtonLayout();
        HorizontalLayout h1 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_INSTRUMENT_TITLE,
                pojoItem.getItemProperty(ELabsViewContants.P_INSTRUMENT_TITLE));
        HorizontalLayout h2 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_INSTRUMENT_DESC,
                getPojoItem().getItemProperty(ELabsViewContants.P_INSTRUMENT_DESC));
        HorizontalLayout h3 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndCheckBoxData(
                ELabsViewContants.L_INSTRUMENT_CONFIGURATION_KEY, ELabsViewContants.L_INSTRUMENT_CONFIGURATION_VALUE,
                getPojoItem().getItemProperty(ELabsViewContants.P_INSTRUMENT_CONFIGURATION));
        HorizontalLayout h4 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndCheckBoxData(
                ELabsViewContants.L_INSTRUMENT_CALIBRATION_KEY, ELabsViewContants.L_INSTRUMENT_CALIBRATION_VALUE,
                getPojoItem().getItemProperty(ELabsViewContants.P_INSTRUMENT_CALIBRATION));
        HorizontalLayout h5 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabsViewContants.L_INSTRUMENT_ESYNC_DAEMON,
                getPojoItem().getItemProperty(ELabsViewContants.P_INSTRUMENT_ESYNCDAEMON));
        HorizontalLayout h6 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_INSTRUMENT_FOLDER,
                getPojoItem().getItemProperty(ELabsViewContants.P_INSTRUMENT_FOLDER));
        HorizontalLayout h7 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabsViewContants.L_INSTRUMENT_FILE_FORMAT,
                getPojoItem().getItemProperty(ELabsViewContants.P_INSTRUMENT_FILEFORMAT));
        HorizontalLayout h8 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabsViewContants.L_INSTRUMENT_DEVICE_SUPERVISOR,
                getPojoItem().getItemProperty(ELabsViewContants.P_INSTRUMENT_DEVICESUPERVISOR));
        HorizontalLayout h9 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_INSTRUMENT_INSTITUTE,
                getPojoItem().getItemProperty(ELabsViewContants.P_INSTRUMENT_INSTITUTE));

        registeredComponents.add(h1);
        registeredComponents.add(h2);
        registeredComponents.add(h3);
        registeredComponents.add(h4);
        registeredComponents.add(h5);
        registeredComponents.add(h6);
        registeredComponents.add(h7);
        registeredComponents.add(h8);
        registeredComponents.add(h9);

        dynamicLayout.addComponent(h1, 0);
        dynamicLayout.addComponent(h2, 1);
        dynamicLayout.addComponent(h3, 2);
        dynamicLayout.addComponent(h4, 3);
        dynamicLayout.addComponent(h5, 4);
        dynamicLayout.addComponent(h6, 5);
        dynamicLayout.addComponent(h7, 6);
        dynamicLayout.addComponent(h8, 7);
        dynamicLayout.addComponent(h9, 8);
        dynamicLayout.addComponent(new HorizontalLayout(), 9);

        mainLayout.addComponent(dynamicLayout);
        mainLayout.setExpandRatio(dynamicLayout, 9.0f);
        mainLayout.attach();
        mainLayout.requestRepaintAll();
    }

    private void createPanelListener() {
        clientViewEventHandler = new LabsClientViewEventHandler(registeredComponents, dynamicLayout, this, this);
        dynamicLayout.addListener(clientViewEventHandler);
    }

    private void createClickListener() {
        mouseClickListener = new Button.ClickListener() {
            private static final long serialVersionUID = -8330004043242560612L;

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                if (event.getButton().getCaption().equals("Save")) {
                    controller.saveAction(instrumentBean);
                    // FIXME why do we these methods?
                    InstrumentView.this.resetLayout();
                    dynamicLayout.requestRepaintAll();

                }
            }
        };

        try {
            ((Button) buttonLayout.getComponent(1)).addListener(mouseClickListener);
        }
        catch (ClassCastException e) {
            LOG.error(e.getMessage());
        }
    }

    protected void resetLayout() {
        Preconditions.checkNotNull(dynamicLayout, "View's dynamiclayout is null.");

        HorizontalLayout tempParentLayout = null;
        for (Iterator<Component> iterator = dynamicLayout.getComponentIterator(); iterator.hasNext();) {
            Component component = iterator.next();
            if (component instanceof HorizontalLayout) {
                tempParentLayout = (HorizontalLayout) component;
            }
            else {
                LOG.error("DynamicLayout can contain only HorizontalLayouts as direct child element.");
                break;
            }
            if (LabsLayoutHelper.switchToLabelFromEditedField(tempParentLayout)) {
                setModifiedComponent(null);
            }
        }
    }

    @Override
    public void hideButtonLayout() {
        if (dynamicLayout != null && dynamicLayout.getComponent(COMPONENT_COUNT) != null) {
            try {
                ((HorizontalLayout) dynamicLayout.getComponent(COMPONENT_COUNT)).removeAllComponents();
            }
            catch (ClassCastException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    @Override
    public void showButtonLayout() {
        HorizontalLayout horizontalLayout = null;
        if (dynamicLayout != null && buttonLayout != null) {
            try {
                horizontalLayout = (HorizontalLayout) dynamicLayout.getComponent(COMPONENT_COUNT);
            }
            catch (ClassCastException e) {
                LOG.error(e.getMessage());
            }
            if (horizontalLayout != null) {
                horizontalLayout.removeAllComponents();
                horizontalLayout.addComponent(buttonLayout);
            }
        }
    }

    @Override
    public Component getModifiedComponent() {
        return modifiedComponent;
    }

    @Override
    public void setModifiedComponent(Component modifiedComponent) {
        try {
            this.modifiedComponent = (HorizontalLayout) modifiedComponent;
        }
        catch (ClassCastException e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public Panel getReference() {
        return this;
    }

    public POJOItem<InstrumentBean> getPojoItem() {
        return pojoItem;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((itemProxy == null) ? 0 : itemProxy.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InstrumentView other = (InstrumentView) obj;
        if (itemProxy == null) {
            if (other.itemProxy != null) {
                return false;
            }
        }
        else if (!itemProxy.equals(other.itemProxy)) {
            return false;
        }
        return true;
    }
}
