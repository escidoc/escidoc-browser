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
package org.escidoc.browser.elabsmodul.views;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Specific BWeLabsView for Instrument item-element.
 */
public class InstrumentView extends Panel implements ILabsPanel, ILabsAction, ILabsInstrumentAction {

    private static final long serialVersionUID = -7601252311598579746L;

    private static final Logger LOG = LoggerFactory.getLogger(InstrumentView.class);

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

    private final List<ResourceModel> breadCrumbModel;

    private final ItemProxy itemProxy;

    private final EscidocServiceLocation serviceLocation;

    public InstrumentView(InstrumentBean sourceBean, ISaveAction controller, List<ResourceModel> breadCrumbModel,
        ResourceProxy resourceProxy, EscidocServiceLocation serviceLocation) {
        Preconditions.checkNotNull(sourceBean, "sourceBean is null: %s", sourceBean);
        Preconditions.checkNotNull(controller, "saveComponent is null: %s", controller);
        Preconditions.checkNotNull(breadCrumbModel, "breadCrumbModel is null: %s", breadCrumbModel);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkArgument(resourceProxy instanceof ItemProxy, "resourceProxy is not an ItemProxy");
        this.instrumentBean = (sourceBean != null) ? sourceBean : new InstrumentBean();
        this.controller = controller;
        this.serviceLocation = serviceLocation;
        this.breadCrumbModel = breadCrumbModel;
        this.itemProxy = (ItemProxy) resourceProxy;
        initialisePanelComponents();
        buildPropertiesGUI();
        buildPanelGUI();
        if (controller.hasUpdateAccess()) {
            createPanelListener();
            createClickListener();
        }
    }

    private void initialisePanelComponents() {
        this.mainLayout = new VerticalLayout();
        this.mainLayout.setSizeFull();
        this.mainLayout.setSpacing(true);
        this.mainLayout.setMargin(true);
        this.dynamicLayout = new VerticalLayout();
        this.dynamicLayout.setSpacing(true);
        this.pojoItem = new POJOItem<InstrumentBean>(this.instrumentBean, PROPERTIES);
        this.registeredComponents = new ArrayList<HorizontalLayout>(COMPONENT_COUNT);
        this.setSizeFull();
        this.setStyleName(Runo.PANEL_LIGHT);
        setContent(this.mainLayout);
        setScrollable(true);
    }

    /**
     * Build the read-only layout of the eLabsElement
     */
    private void buildPropertiesGUI() {
        addComponent(new ResourcePropertiesViewHelper(this.itemProxy, this.breadCrumbModel, "Instrument",
            this.serviceLocation).generatePropertiesView());
    }

    /**
     * Build the specific editable layout of the eLabsElement.
     */
    private void buildPanelGUI() {

        final String supervisorId = this.instrumentBean.getDeviceSupervisor();
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

        final String instituteId = this.instrumentBean.getInstitute();
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

        this.dynamicLayout.setStyleName(ELabsViewContants.STYLE_ELABS_FORM);

        this.buttonLayout = LabsLayoutHelper.createButtonLayout();
        HorizontalLayout h1 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_TITLE, getPojoItem()
                .getItemProperty(ELabsViewContants.P_INSTRUMENT_TITLE), true);
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

        h5.addListener(new ESyncDaemonEndpointSelectionLayoutListener(this, this));
        if (!ELabsCache.getFileFormats().isEmpty()) {
            h7.addListener(new FileFormatSelectionLayoutListener(this));
        }
        h8.addListener(new DeviceSupervisorSelectionLayoutListener(this));
        h9.addListener(new InstituteSelectionLayoutListener(this));

        this.registeredComponents.add(h1);
        this.registeredComponents.add(h2);
        this.registeredComponents.add(h3);
        this.registeredComponents.add(h4);
        this.registeredComponents.add(h5);
        this.registeredComponents.add(h6);
        this.registeredComponents.add(h7);
        this.registeredComponents.add(h8);
        this.registeredComponents.add(h9);

        this.dynamicLayout.addComponent(h1, 0);
        this.dynamicLayout.addComponent(h2, 1);
        this.dynamicLayout.addComponent(h3, 2);
        this.dynamicLayout.addComponent(h4, 3);
        this.dynamicLayout.addComponent(h5, 4);
        this.dynamicLayout.addComponent(h6, 5);
        this.dynamicLayout.addComponent(h7, 6);
        this.dynamicLayout.addComponent(h8, 7);
        this.dynamicLayout.addComponent(h9, 8);
        this.dynamicLayout.addComponent(new HorizontalLayout(), 9);

        this.dynamicLayout.setComponentAlignment(h1, Alignment.MIDDLE_LEFT);
        this.dynamicLayout.setComponentAlignment(h2, Alignment.MIDDLE_LEFT);
        this.dynamicLayout.setComponentAlignment(h3, Alignment.MIDDLE_LEFT);
        this.dynamicLayout.setComponentAlignment(h4, Alignment.MIDDLE_LEFT);
        this.dynamicLayout.setComponentAlignment(h5, Alignment.MIDDLE_LEFT);
        this.dynamicLayout.setComponentAlignment(h6, Alignment.MIDDLE_LEFT);
        this.dynamicLayout.setComponentAlignment(h7, Alignment.MIDDLE_LEFT);
        this.dynamicLayout.setComponentAlignment(h8, Alignment.MIDDLE_LEFT);
        this.dynamicLayout.setComponentAlignment(h9, Alignment.MIDDLE_LEFT);

        this.mainLayout.addComponent(this.dynamicLayout);
        this.mainLayout.setExpandRatio(this.dynamicLayout, 9.0f);
        this.mainLayout.attach();
        this.mainLayout.requestRepaintAll();
    }

    private void createPanelListener() {
        this.clientViewEventHandler =
            new LabsClientViewEventHandler(this.registeredComponents, this.dynamicLayout, this, this);
        this.dynamicLayout.addListener(this.clientViewEventHandler);
    }

    private void createClickListener() {
        this.mouseClickListener = new Button.ClickListener() {
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

    @Override
    public void resetLayout() {
        Preconditions.checkNotNull(this.dynamicLayout, "View's dynamiclayout is null.");
        HorizontalLayout tempParentLayout = null;
        for (Iterator<Component> iterator = this.dynamicLayout.getComponentIterator(); iterator.hasNext();) {
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
                this.setModifiedComponent(null);
                if (this.dynamicLayout.getComponentIndex(tempParentLayout) == 4) {
                    this.instrumentBean.setESyncDaemon((String) ((Label) tempParentLayout.getComponent(1)).getValue());
                }
            }
            else {
                LOG.error("Label change error, mod .component is not set to null");
            }
        }
    }

    @Override
    public void hideButtonLayout() {
        if (this.dynamicLayout != null && this.dynamicLayout.getComponent(COMPONENT_COUNT) != null) {
            try {
                ((HorizontalLayout) this.dynamicLayout.getComponent(COMPONENT_COUNT)).removeAllComponents();
            }
            catch (ClassCastException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    @Override
    public void showButtonLayout() {
        HorizontalLayout horizontalLayout = null;
        if (this.dynamicLayout != null && this.buttonLayout != null) {
            try {
                horizontalLayout = (HorizontalLayout) this.dynamicLayout.getComponent(COMPONENT_COUNT);
            }
            catch (ClassCastException e) {
                LOG.error(e.getMessage());
            }
            if (horizontalLayout != null) {
                horizontalLayout.removeAllComponents();
                horizontalLayout.addComponent(this.buttonLayout);
            }
        }
    }

    @Override
    public Component getModifiedComponent() {
        return this.modifiedComponent;
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
        return this.pojoItem;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.itemProxy == null) ? 0 : this.itemProxy.hashCode());
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
        if (this.itemProxy == null) {
            if (other.itemProxy != null) {
                return false;
            }
        }
        else if (!this.itemProxy.equals(other.itemProxy)) {
            return false;
        }
        return true;
    }

    @Override
    public void setDeviceSupervisor(final String deviceSupervisorId) {
        Preconditions.checkNotNull(deviceSupervisorId, "input arg is null");
        this.instrumentBean.setDeviceSupervisor(deviceSupervisorId);
    }

    @Override
    public void setInstitute(final String instituteId) {
        Preconditions.checkNotNull(instituteId, "input arg is null");
        this.instrumentBean.setInstitute(instituteId);
    }

    @Override
    public void setFileFormat(String fileFormat) {
        Preconditions.checkNotNull(fileFormat, "input arg is null");
        this.instrumentBean.setFileFormat(fileFormat);
    }
}
