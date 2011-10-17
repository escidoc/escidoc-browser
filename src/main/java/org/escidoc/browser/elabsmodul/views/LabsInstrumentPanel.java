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
import java.util.List;

import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.interfaces.ILabsAction;
import org.escidoc.browser.elabsmodul.interfaces.ILabsPanel;
import org.escidoc.browser.elabsmodul.interfaces.ISaveAction;
import org.escidoc.browser.elabsmodul.model.InstrumentBean;
import org.escidoc.browser.elabsmodul.views.helper.LabsLayoutHelper;
import org.escidoc.browser.elabsmodul.views.listeners.LabsClientViewEventHandler;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.BreadCrumbMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.POJOItem;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
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
public class LabsInstrumentPanel extends Panel implements ILabsPanel, ILabsAction {

    private static final long serialVersionUID = -7601252311598579746L;

    private static Logger LOG = LoggerFactory.getLogger(LabsInstrumentPanel.class);

    private final String[] PROPERTIES = ELabsViewContants.INSTRUMENT_PROPERTIES;

    final String VIEWCAPTION = "Instument View";

    final String LAST_MODIFIED_BY = "Last modification by ";

    final String FLOAT_LEFT = "floatleft";

    final String FLOAT_RIGHT = "floatright";

    private final int COMPONENT_COUNT = 9;

    private POJOItem<InstrumentBean> pojoItem = null;

    private InstrumentBean instrumentBean = null, lastStateBean = null;

    private VerticalLayout mainLayout = null, dynamicLayout = null;

    private LayoutClickListener clientViewEventHandler = null;

    private ClickListener mouseClickListener = null;

    private List<HorizontalLayout> registeredComponents = null;

    private HorizontalLayout modifiedComponent = null;

    private HorizontalLayout buttonLayout = null;

    private final ISaveAction saveComponent;

    private List<ResourceModel> breadCrumbModel;

    public LabsInstrumentPanel(InstrumentBean sourceBean, ISaveAction saveComponent, List<ResourceModel> breadCrumbModel) {

        this.instrumentBean = (sourceBean != null) ? sourceBean : new InstrumentBean();
        this.lastStateBean = instrumentBean;
        this.saveComponent = saveComponent;
        this.breadCrumbModel = breadCrumbModel;

        initialisePanelComponents();
        buildStaticGUI();
        buildDynamicGUI();
        createPanelListener();
        createClickListener();
    }

    private void initialisePanelComponents() {
        this.mainLayout = new VerticalLayout();
        this.mainLayout.setSpacing(true);
        this.mainLayout.setMargin(true);
        this.dynamicLayout = new VerticalLayout();
        this.dynamicLayout.setSpacing(true);
        this.dynamicLayout.setMargin(true);

        this.pojoItem = new POJOItem<InstrumentBean>(instrumentBean, PROPERTIES);
        this.registeredComponents = new ArrayList<HorizontalLayout>(COMPONENT_COUNT);

        this.setContent(this.mainLayout);
        this.setScrollable(true);
    }

    /**
     * Build the read-only layout of the eLabsElement
     */
    private void buildStaticGUI() {
        // Item title
        final Label titleLabel = new Label(ViewConstants.RESOURCE_NAME + instrumentBean.getName());
        titleLabel.setDescription("header");
        titleLabel.setStyleName("h2 fullwidth");

        // HR Ruler
        final Label descRuler = new Label("<hr/>", Label.CONTENT_RAW);
        descRuler.setStyleName("hr");

        // ItemProperties View
        final HorizontalLayout propertiesView = new HorizontalLayout();
        final Label descMetadata1 = new Label("ID: " + instrumentBean.getObjectId());
        final Label descMetadata2 =
            new Label(
                LAST_MODIFIED_BY + " " + instrumentBean.getModifiedBy() + " on " + instrumentBean.getModifiedOn(),
                Label.CONTENT_XHTML);

        final Panel pnlPropertiesLeft = new Panel();
        pnlPropertiesLeft.setWidth("40%");
        pnlPropertiesLeft.setHeight("60px");
        pnlPropertiesLeft.setStyleName(FLOAT_LEFT);
        pnlPropertiesLeft.addStyleName(Runo.PANEL_LIGHT);
        pnlPropertiesLeft.addComponent(descMetadata1);

        final Panel pnlPropertiesRight = new Panel();
        pnlPropertiesRight.setWidth("60%");
        pnlPropertiesRight.setHeight("60px");
        pnlPropertiesRight.setStyleName(FLOAT_RIGHT);
        pnlPropertiesRight.addStyleName(Runo.PANEL_LIGHT);
        pnlPropertiesRight.addComponent(descMetadata2);
        propertiesView.addComponent(pnlPropertiesLeft);
        propertiesView.addComponent(pnlPropertiesRight);

        Panel viewHandler = new Panel();
        viewHandler.setStyleName(Panel.STYLE_LIGHT);

        /* Add subelements on to RootComponent */
        new BreadCrumbMenu(viewHandler, breadCrumbModel);
        viewHandler.addComponent(titleLabel);
        viewHandler.addComponent(descRuler);
        viewHandler.addComponent(propertiesView);

        this.mainLayout.addComponent(viewHandler);
    }

    /**
     * Build the specific editable layout of the eLabsElement.
     */
    private void buildDynamicGUI() {
        this.dynamicLayout.setStyleName(ELabsViewContants.STYLE_ELABS_FORM);

        this.buttonLayout = LabsLayoutHelper.createButtonLayout();
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

        this.mainLayout.addComponent(this.dynamicLayout);
        this.mainLayout.attach();
        this.mainLayout.requestRepaintAll();
    }

    private void createPanelListener() {
        this.clientViewEventHandler = new LabsClientViewEventHandler(registeredComponents, dynamicLayout, this, this);
        this.dynamicLayout.addListener(this.clientViewEventHandler);
    }

    private void createClickListener() {
        this.mouseClickListener = new Button.ClickListener() {
            private static final long serialVersionUID = -8330004043242560612L;

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                if (event.getButton().getCaption().equals("Save")) {
                    LabsInstrumentPanel.this.saveComponent.saveAction(LabsInstrumentPanel.this.instrumentBean);
                    LOG.info("SAVE Action is triggered on the view panel");
                    LabsInstrumentPanel.this.resetLayout();
                    LabsInstrumentPanel.this.storeBackupBean();

                }
                else if (event.getButton().getCaption().equals("Cancel")) {
                    LabsInstrumentPanel.this.resetLayout();
                    LabsInstrumentPanel.this.resetBeanModel();
                    // TODO reset function
                }
                LabsInstrumentPanel.this.hideButtonLayout();
            }
        };

        try {
            ((Button) this.buttonLayout.getComponent(1)).addListener(this.mouseClickListener);
            ((Button) this.buttonLayout.getComponent(2)).addListener(this.mouseClickListener);
        }
        catch (ClassCastException e) {
            LOG.error(e.getMessage());
        }
    }

    protected void storeBackupBean() {
        this.lastStateBean = this.instrumentBean;
    }

    protected void resetBeanModel() {
        this.instrumentBean = this.lastStateBean;

    }

    protected void resetLayout() {

    }

    @Override
    public void hideButtonLayout() {
        if (this.dynamicLayout != null && this.dynamicLayout.getComponent(9) != null) {
            try {
                ((HorizontalLayout) this.dynamicLayout.getComponent(9)).removeAllComponents();
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
                horizontalLayout = (HorizontalLayout) this.dynamicLayout.getComponent(9);
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
        result = prime * result + ((instrumentBean == null) ? 0 : instrumentBean.hashCode());
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
        final LabsInstrumentPanel other = (LabsInstrumentPanel) obj;
        if (instrumentBean == null) {
            if (other.instrumentBean != null) {
                return false;
            }
        }
        else if (!instrumentBean.equals(other.instrumentBean)) {
            return false;
        }
        return true;
    }

}
