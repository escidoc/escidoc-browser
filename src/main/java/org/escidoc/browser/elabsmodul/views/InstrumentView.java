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

import org.escidoc.browser.elabsmodul.cache.ELabsCache;
import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.interfaces.ILabsAction;
import org.escidoc.browser.elabsmodul.interfaces.ILabsInstrumentAction;
import org.escidoc.browser.elabsmodul.interfaces.ILabsPanel;
import org.escidoc.browser.elabsmodul.interfaces.ISaveAction;
import org.escidoc.browser.elabsmodul.model.InstrumentBean;
import org.escidoc.browser.elabsmodul.model.OrgUnitBean;
import org.escidoc.browser.elabsmodul.model.UserBean;
import org.escidoc.browser.elabsmodul.views.helpers.LabsLayoutHelper;
import org.escidoc.browser.elabsmodul.views.helpers.ResourcePropertiesViewHelper;
import org.escidoc.browser.elabsmodul.views.listeners.DeviceSupervisorSelectionLayoutListener;
import org.escidoc.browser.elabsmodul.views.listeners.ESyncDaemonEndpointSelectionLayoutListener;
import org.escidoc.browser.elabsmodul.views.listeners.FileFormatSelectionLayoutListener;
import org.escidoc.browser.elabsmodul.views.listeners.InstituteSelectionLayoutListener;
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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

/**
 * Specific BWeLabsView for Instrument item-element.
 */
public class InstrumentView extends Panel implements ILabsPanel, ILabsAction, ILabsInstrumentAction {

    private static final long serialVersionUID = -7601252311598579746L;

    private static Logger LOG = LoggerFactory.getLogger(InstrumentView.class);

    private final String[] PROPERTIES = ELabsViewContants.INSTRUMENT_PROPERTIES;

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

    private final ItemProxy itemProxy;

    private EscidocServiceLocation serviceLocation;

    public InstrumentView(InstrumentBean sourceBean, ISaveAction controller, List<ResourceModel> breadCrumbModel,
        ResourceProxy resourceProxy, EscidocServiceLocation serviceLocation) {
        this.instrumentBean = (sourceBean != null) ? sourceBean : new InstrumentBean();
        this.controller = controller;
        this.serviceLocation = serviceLocation;
        this.breadCrumbModel = breadCrumbModel;
        if (resourceProxy instanceof ItemProxy) {
            this.itemProxy = (ItemProxy) resourceProxy;
        }
        else {
            LOG.error("ResourceProxy is not ItemProxy");
            this.itemProxy = null;
            return;
        }
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
        // dynamicLayout.setMargin(true);

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

        final String supervisorId = instrumentBean.getDeviceSupervisor();
        String supervisorText = null;
        if (supervisorId != null) {
            for (Iterator<UserBean> iterator = ELabsCache.getUsers().iterator(); iterator.hasNext();) {
                UserBean user = iterator.next();
                if (user.getId().equals(supervisorId)) {
                    supervisorText = user.getComplexId();
                    break;
                }
            }
        }

        final String instituteId = instrumentBean.getInstitute();
        String instituteText = null;
        if (instituteId != null) {
            for (Iterator<OrgUnitBean> iterator = ELabsCache.getOrgUnits().iterator(); iterator.hasNext();) {
                OrgUnitBean unit = iterator.next();
                if (unit.getId().equals(instituteId)) {
                    instituteText = unit.getComplexId();
                    break;
                }
            }
        }

        dynamicLayout.setStyleName(ELabsViewContants.STYLE_ELABS_FORM);

        buttonLayout = LabsLayoutHelper.createButtonLayout();
        HorizontalLayout h1 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_TITLE,
                pojoItem.getItemProperty(ELabsViewContants.P_INSTRUMENT_TITLE), true);
        HorizontalLayout h2 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_DESCRIPTION,
                getPojoItem().getItemProperty(ELabsViewContants.P_INSTRUMENT_DESC), true);
        HorizontalLayout h3 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndCheckBoxData(
                ELabsViewContants.L_INSTRUMENT_CONFIGURATION_KEY, ELabsViewContants.L_INSTRUMENT_CONFIGURATION_VALUE,
                getPojoItem().getItemProperty(ELabsViewContants.P_INSTRUMENT_CONFIGURATION), false);
        HorizontalLayout h4 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndCheckBoxData(
                ELabsViewContants.L_INSTRUMENT_CALIBRATION_KEY, ELabsViewContants.L_INSTRUMENT_CALIBRATION_VALUE,
                getPojoItem().getItemProperty(ELabsViewContants.P_INSTRUMENT_CALIBRATION), false);
        HorizontalLayout h5 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndStaticComboData(
                ELabsViewContants.L_INSTRUMENT_ESYNC_DAEMON, instrumentBean.getESyncDaemon(), true);
        HorizontalLayout h6 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_INSTRUMENT_FOLDER,
                getPojoItem().getItemProperty(ELabsViewContants.P_INSTRUMENT_FOLDER), true);
        HorizontalLayout h7 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabsViewContants.L_INSTRUMENT_FILE_FORMAT,
                getPojoItem().getItemProperty(ELabsViewContants.P_INSTRUMENT_FILEFORMAT), false);
        HorizontalLayout h8 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelComplexData(
                ELabsViewContants.L_INSTRUMENT_DEVICE_SUPERVISOR, supervisorText, false);
        HorizontalLayout h9 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelComplexData(
                ELabsViewContants.L_INSTRUMENT_INSTITUTE, instituteText, false);

        // set up specific listeners
        h5.addListener(new ESyncDaemonEndpointSelectionLayoutListener(this, this));
        if (!ELabsCache.getFileFormats().isEmpty()) {
            h7.addListener(new FileFormatSelectionLayoutListener(this));
        }
        h8.addListener(new DeviceSupervisorSelectionLayoutListener(this));
        h9.addListener(new InstituteSelectionLayoutListener(this));

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

        dynamicLayout.setComponentAlignment(h1, Alignment.MIDDLE_LEFT);
        dynamicLayout.setComponentAlignment(h2, Alignment.MIDDLE_LEFT);
        dynamicLayout.setComponentAlignment(h3, Alignment.MIDDLE_LEFT);
        dynamicLayout.setComponentAlignment(h4, Alignment.MIDDLE_LEFT);
        dynamicLayout.setComponentAlignment(h5, Alignment.MIDDLE_LEFT);
        dynamicLayout.setComponentAlignment(h6, Alignment.MIDDLE_LEFT);
        dynamicLayout.setComponentAlignment(h7, Alignment.MIDDLE_LEFT);
        dynamicLayout.setComponentAlignment(h8, Alignment.MIDDLE_LEFT);
        dynamicLayout.setComponentAlignment(h9, Alignment.MIDDLE_LEFT);

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
                    InstrumentView.this.resetLayout();
                    dynamicLayout.requestRepaintAll();
                    controller.saveAction(instrumentBean);

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

            if (tempParentLayout.getComponentCount() != 2) {
                continue;
            }

            if (LabsLayoutHelper.switchToLabelFromEditedField(tempParentLayout)) {
                setModifiedComponent(null);

                // get esynch label for instant save
                if (dynamicLayout.getComponentIndex(tempParentLayout) == 4) {
                    instrumentBean.setESyncDaemon((String) ((Label) tempParentLayout.getComponent(1)).getValue());
                }
            }
            else {
                LOG.error("Label change error, mod .component is not set to null");
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
        if (modifiedComponent == null) {
            this.modifiedComponent = null;
            return;
        }

        if (modifiedComponent instanceof HorizontalLayout) {
            this.modifiedComponent = (HorizontalLayout) modifiedComponent;
        }
        else {
            LOG.error("Wrong class type!");
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

    @Override
    public void setDeviceSupervisor(final String deviceSupervisorId) {
        Preconditions.checkNotNull(deviceSupervisorId, "input arg is null");
        instrumentBean.setDeviceSupervisor(deviceSupervisorId);
    }

    @Override
    public void setInstitute(final String instituteId) {
        Preconditions.checkNotNull(instituteId, "input arg is null");
        instrumentBean.setInstitute(instituteId);
    }

    @Override
    public void setFileFormat(String fileFormat) {
        Preconditions.checkNotNull(fileFormat, "input arg is null");
        instrumentBean.setFileFormat(fileFormat);
    }
}
