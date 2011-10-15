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

import org.escidoc.browser.elabsmodul.constants.ELabViewContants;
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

public class LabsInstrumentPanel extends Panel implements ILabsPanel, ILabsAction {

    private static final long serialVersionUID = -7601252311598579746L;

    private static Logger LOG = LoggerFactory.getLogger(LabsInstrumentPanel.class);

    private final String[] PROPERTIES = ELabViewContants.INSTRUMENT_PROPERTIES;

    final String VIEWCAPTION = "Instument View";

    final String LAST_MODIFIED_BY = "Last modification by ";

    final String FLOAT_LEFT = "floatleft";

    final String FLOAT_RIGHT = "floatright";

    private final int COMPONENT_COUNT = 9;

    private POJOItem<InstrumentBean> pojoItem = null;

    private InstrumentBean instrumentBean = null;

    private VerticalLayout mainLayout = null;

    private LayoutClickListener clientViewEventHandler = null;

    private ClickListener mouseClickListener = null;

    private List<HorizontalLayout> registeredComponents = null;

    private HorizontalLayout modifiedComponent = null;

    private HorizontalLayout buttonLayout = null;

    private final ISaveAction saveComponent;

    private List<ResourceModel> breadCrumbModel;

    public LabsInstrumentPanel(InstrumentBean sourceBean, ISaveAction saveComponent, List<ResourceModel> breadCrumbModel) {

        this.instrumentBean = (sourceBean != null) ? sourceBean : new InstrumentBean();
        this.saveComponent = saveComponent;
        this.breadCrumbModel=breadCrumbModel;

        initialisePanelComponents();
        buildPanelGUI();
        createPanelListener();
        createClickListener();
    }

    private void initialisePanelComponents() {
        this.mainLayout = new VerticalLayout();
        this.pojoItem = new POJOItem<InstrumentBean>(instrumentBean, PROPERTIES);
        this.registeredComponents = new ArrayList<HorizontalLayout>(COMPONENT_COUNT);
        

        this.setContent(mainLayout);
        this.setScrollable(true);
    }

    private void buildPanelGUI() {
        this.mainLayout.setStyleName(ELabViewContants.STYLE_ELABS_FORM);
        this.mainLayout.setSpacing(true);
        this.mainLayout.setMargin(true);
        
        

        buttonLayout = LabsLayoutHelper.createButtonLayout();
        HorizontalLayout h1 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabViewContants.L_INSTRUMENT_TITLE,
                pojoItem.getItemProperty(ELabViewContants.P_INSTRUMENT_TITLE));
        HorizontalLayout h2 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabViewContants.L_INSTRUMENT_DESC,
                getPojoItem().getItemProperty(ELabViewContants.P_INSTRUMENT_DESC));
        HorizontalLayout h3 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndCheckBoxData(
                ELabViewContants.L_INSTRUMENT_CONFIGURATION_KEY, ELabViewContants.L_INSTRUMENT_CONFIGURATION_VALUE,
                getPojoItem().getItemProperty(ELabViewContants.P_INSTRUMENT_CONFIGURATION));
        HorizontalLayout h4 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndCheckBoxData(
                ELabViewContants.L_INSTRUMENT_CALIBRATION_KEY, ELabViewContants.L_INSTRUMENT_CALIBRATION_VALUE,
                getPojoItem().getItemProperty(ELabViewContants.P_INSTRUMENT_CALIBRATION));
        HorizontalLayout h5 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabViewContants.L_INSTRUMENT_ESYNC_DAEMON,
                getPojoItem().getItemProperty(ELabViewContants.P_INSTRUMENT_ESYNCDAEMON));
        HorizontalLayout h6 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabViewContants.L_INSTRUMENT_FOLDER,
                getPojoItem().getItemProperty(ELabViewContants.P_INSTRUMENT_FOLDER));
        HorizontalLayout h7 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabViewContants.L_INSTRUMENT_FILE_FORMAT,
                getPojoItem().getItemProperty(ELabViewContants.P_INSTRUMENT_FILEFORMAT));
        HorizontalLayout h8 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabViewContants.L_INSTRUMENT_DEVICE_SUPERVISOR,
                getPojoItem().getItemProperty(ELabViewContants.P_INSTRUMENT_DEVICESUPERVISOR));
        HorizontalLayout h9 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabViewContants.L_INSTRUMENT_INSTITUTE,
                getPojoItem().getItemProperty(ELabViewContants.P_INSTRUMENT_INSTITUTE));

        registeredComponents.add(h1);
        registeredComponents.add(h2);
        registeredComponents.add(h3);
        registeredComponents.add(h4);
        registeredComponents.add(h5);
        registeredComponents.add(h6);
        registeredComponents.add(h7);
        registeredComponents.add(h8);
        registeredComponents.add(h9);

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

        this.mainLayout.addComponent(new VerticalLayout(), 0);
        Panel viewHandler = new Panel();
        viewHandler.setStyleName(Panel.STYLE_LIGHT);
        
        /* Add subelements on to RootComponent */
        new BreadCrumbMenu(viewHandler, breadCrumbModel);
        viewHandler.addComponent(titleLabel);
        viewHandler.addComponent(descRuler);
        viewHandler.addComponent(propertiesView);
        
        this.mainLayout.addComponent(viewHandler,1);
        this.mainLayout.addComponent(h1, 2);
        this.mainLayout.addComponent(h2, 3);
        this.mainLayout.addComponent(h3, 4);
        this.mainLayout.addComponent(h4, 5);
        this.mainLayout.addComponent(h5, 6);
        this.mainLayout.addComponent(h6, 7);
        this.mainLayout.addComponent(h7, 8);
        this.mainLayout.addComponent(h8, 9);
        this.mainLayout.addComponent(h9, 10);

        this.mainLayout.attach();
        this.mainLayout.requestRepaintAll();
    }

    private void createPanelListener() {
        this.clientViewEventHandler = new LabsClientViewEventHandler(registeredComponents, mainLayout, this, this);
        this.mainLayout.addListener(this.clientViewEventHandler);
    }

    private void createClickListener() {
        this.mouseClickListener = new Button.ClickListener() {

            private static final long serialVersionUID = -8330004043242560612L;

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                if (event.getButton().getCaption().equals("Save")) {
                    LabsInstrumentPanel.this.saveComponent.saveAction(LabsInstrumentPanel.this.instrumentBean);
                }
                else if (event.getButton().getCaption().equals("Cancel")) {
                    // TODO reset function
                }

                LabsInstrumentPanel.this.hideButtonLayout();
            }
        };

        ((Button) this.buttonLayout.getComponent(0)).addListener(this.mouseClickListener);
        ((Button) this.buttonLayout.getComponent(1)).addListener(this.mouseClickListener);
    }

    @Override
    public void hideButtonLayout() {
        if (mainLayout != null && this.mainLayout.getComponent(0) != null) {
            ((VerticalLayout) this.mainLayout.getComponent(0)).removeAllComponents();
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