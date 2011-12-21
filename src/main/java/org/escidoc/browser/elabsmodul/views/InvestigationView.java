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
import org.escidoc.browser.elabsmodul.interfaces.IInvestigationAction;
import org.escidoc.browser.elabsmodul.interfaces.ILabsAction;
import org.escidoc.browser.elabsmodul.interfaces.ILabsInvestigationAction;
import org.escidoc.browser.elabsmodul.interfaces.ILabsPanel;
import org.escidoc.browser.elabsmodul.model.DurationBean;
import org.escidoc.browser.elabsmodul.model.InvestigationBean;
import org.escidoc.browser.elabsmodul.model.RigBean;
import org.escidoc.browser.elabsmodul.model.UserBean;
import org.escidoc.browser.elabsmodul.views.helpers.LabsLayoutHelper;
import org.escidoc.browser.elabsmodul.views.helpers.ResourcePropertiesViewHelper;
import org.escidoc.browser.elabsmodul.views.helpers.StartInvestigationViewHelper;
import org.escidoc.browser.elabsmodul.views.listeners.DepositEndpointSelectionLayoutListener;
import org.escidoc.browser.elabsmodul.views.listeners.DepositorSelectionLayoutListener;
import org.escidoc.browser.elabsmodul.views.listeners.DurationSelectionLayoutListener;
import org.escidoc.browser.elabsmodul.views.listeners.LabsClientViewEventHandler;
import org.escidoc.browser.elabsmodul.views.listeners.RigSelectionLayoutListener;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.maincontent.View;
import org.escidoc.browser.ui.view.helpers.DirectMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
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

import de.escidoc.core.client.exceptions.EscidocClientException;

public class InvestigationView extends View implements ILabsPanel, ILabsAction, ILabsInvestigationAction {

    private static final long serialVersionUID = -5284506653803233585L;

    private static final Logger LOG = LoggerFactory.getLogger(InvestigationView.class);

    private final InvestigationBean investigationBean;

    private final IInvestigationAction controller;

    private final List<ResourceModel> breadCrumbModel;

    private final ContainerProxy containerProxy;

    private VerticalLayout mainLayout, dynamicLayout;

    private POJOItem<InvestigationBean> pojoItem;

    private List<HorizontalLayout> registeredComponents;

    private final int COMPONENT_COUNT = 6;

    private LayoutClickListener clientViewEventHandler;

    private ClickListener mouseClickListener;

    private HorizontalLayout buttonLayout;

    private HorizontalLayout modifiedComponent;

    private final HorizontalLayout directMemberInvestigationContainer = new HorizontalLayout();

    private final Router router;

    public InvestigationView(final InvestigationBean sourceBean, final IInvestigationAction controller,
        final List<ResourceModel> breadCrumbModel, final ContainerProxy containerProxy, final Router router) {
        Preconditions.checkNotNull(sourceBean, "sourceBean is null: %s", sourceBean);
        Preconditions.checkNotNull(controller, "saveComponent is null: %s", controller);
        Preconditions.checkNotNull(breadCrumbModel, "breadCrumbModel is null: %s", breadCrumbModel);
        Preconditions.checkNotNull(containerProxy, "resourceProxy is null: %s", containerProxy);
        Preconditions.checkNotNull(router, "router is null: %s", router);
        this.investigationBean = (sourceBean != null) ? sourceBean : new InvestigationBean();
        this.controller = controller;
        this.breadCrumbModel = breadCrumbModel;
        this.containerProxy = containerProxy;
        this.setViewName(containerProxy.getName());
        this.router = router;
        initialisePanelComponents();
        buildPropertiesGUI();
        buildContainerGUI();
        buildPanelGUI();
        if (controller.hasUpdateAccess()) {
            createPanelListener();
            createClickListener();
        }
    }

    private void buildContainerGUI() {
        this.directMemberInvestigationContainer.setWidth("100%");
        this.directMemberInvestigationContainer.setHeight("100%");
        try {
            leftCell();
        }
        catch (final EscidocClientException e) {
            this.router.getMainWindow().showNotification(
                "Could not load the Direct Members Helper in the View" + e.getLocalizedMessage());
        }
    }

    private void leftCell() throws EscidocClientException {
        final Panel leftPanel = new Panel();
        leftPanel.setStyleName("directmembers floatleft");
        leftPanel.setScrollable(false);
        leftPanel.setSizeFull();

        VerticalLayout vl = new VerticalLayout();
        vl.setMargin(false);
        vl.setSizeFull();
        leftPanel.setContent(vl);
        new DirectMember(this.router.getServiceLocation(), this.router, containerProxy.getId(),
            this.router.getMainWindow(), this.router.getRepositories(), leftPanel, ResourceType.CONTAINER.toString())
            .containerAsTree();
        directMemberInvestigationContainer.addComponent(leftPanel);
        directMemberInvestigationContainer.setExpandRatio(leftPanel, 3.0f);
    }

    /**
     * This is the inner Right Cell within a Context By default a set of Organizational Unit / Admin Description /
     * RelatedItem / Resources are bound This Cell is binding the Panel and the Start/Stop button in the Investigation,
     * through StartInvestigationViewHelper
     * 
     * @param comptoBind
     */
    @SuppressWarnings("deprecation")
    private void rightCell(final Component comptoBind) {
        final Panel rightpnl = new Panel();
        rightpnl.setStyleName("floatright");
        rightpnl.addStyleName(Runo.PANEL_LIGHT);
        rightpnl.setSizeFull();
        rightpnl.getLayout().setMargin(false);
        rightpnl.addComponent(comptoBind);
        new StartInvestigationViewHelper(this, this.controller).createStartButton(rightpnl);
        directMemberInvestigationContainer.addComponent(rightpnl);
        directMemberInvestigationContainer.setExpandRatio(rightpnl, 7.0f);
    }

    private void initialisePanelComponents() {
        this.mainLayout = new VerticalLayout();
        this.mainLayout.setSpacing(true);
        this.mainLayout.setMargin(true);
        mainLayout.setSizeFull();
        this.dynamicLayout = new VerticalLayout();
        this.dynamicLayout.setSpacing(true);
        this.pojoItem =
            new POJOItem<InvestigationBean>(this.investigationBean, ELabsViewContants.INVESTIGATION_PROPERTIES);
        this.registeredComponents = new ArrayList<HorizontalLayout>(COMPONENT_COUNT);

        setSizeFull();
        setStyleName(Runo.PANEL_LIGHT);
        setContent(this.mainLayout);
        setScrollable(true);
    }

    /**
     * Build the read-only layout of the eLabsElement
     */
    private void buildPropertiesGUI() {
        addComponent(new ResourcePropertiesViewHelper(this.containerProxy, this.breadCrumbModel, "Investigation",
            this.router.getServiceLocation()).generatePropertiesView());
    }

    private void createPanelListener() {
        this.clientViewEventHandler =
            new LabsClientViewEventHandler(this.registeredComponents, this.dynamicLayout, this, this);
        this.dynamicLayout.addListener(this.clientViewEventHandler);
    }

    private void createClickListener() {
        this.mouseClickListener = new Button.ClickListener() {

            private static final long serialVersionUID = 3427496817637644626L;

            @Override
            public void buttonClick(final com.vaadin.ui.Button.ClickEvent event) {
                if (event.getButton().getCaption().equals("Save")) {
                    InvestigationView.this.resetLayout();
                    dynamicLayout.requestRepaintAll();
                    controller.saveAction(investigationBean);
                }
            }
        };

        try {
            ((Button) this.buttonLayout.getComponent(1)).addListener(this.mouseClickListener);
        }
        catch (final ClassCastException e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public void resetLayout() {
        Preconditions.checkNotNull(this.dynamicLayout, "View's dynamiclayout is null.");

        HorizontalLayout tempParentLayout = null;
        for (final Iterator<Component> iterator = this.dynamicLayout.getComponentIterator(); iterator.hasNext();) {
            final Component component = iterator.next();
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
                if (dynamicLayout.getComponentIndex(tempParentLayout) == 2) {
                    investigationBean
                        .setDepositEndpoint((String) ((Label) tempParentLayout.getComponent(1)).getValue());
                }
            }
        }
    }

    /**
     * Build the specific editable layout of the eLabsElement.
     */
    private void buildPanelGUI() {
        final String investigatorId = investigationBean.getInvestigator();
        String investigatorText = null;
        if (investigatorId != null) {
            for (Iterator<UserBean> iterator = ELabsCache.getUsers().iterator(); iterator.hasNext();) {
                UserBean user = iterator.next();
                if (user.getId().equals(investigatorId)) {
                    investigatorText = user.getComplexId();
                }
            }
        }
        this.dynamicLayout.setStyleName(ELabsViewContants.STYLE_ELABS_FORM);
        this.buttonLayout = LabsLayoutHelper.createButtonLayout();
        final HorizontalLayout h1 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_TITLE,
                this.pojoItem.getItemProperty(ELabsViewContants.P_INVESTIGATION_TITLE), true);
        final HorizontalLayout h2 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_DESCRIPTION,
                this.pojoItem.getItemProperty(ELabsViewContants.P_INVESTIGATION_DESC), true);
        final HorizontalLayout h3 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndStaticComboData(
                ELabsViewContants.L_INVESTIGATION_DEPOSIT_SERVICE, investigationBean.getDepositEndpoint(), true);
        final HorizontalLayout h4 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelComplexData(
                ELabsViewContants.L_INVESTIGATION_INVESTIGATOR, investigatorText, false);
        final HorizontalLayout h5 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabsViewContants.L_INVESTIGATION_DURATION,
                this.pojoItem.getItemProperty(ELabsViewContants.P_INVESTIGATION_DURATION), true);
        final HorizontalLayout h6 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_INVESTIGATION_RIG,
                this.pojoItem.getItemProperty(ELabsViewContants.P_INVESTIGATION_RIG), true);

        h3.addListener(new DepositEndpointSelectionLayoutListener(this, this));
        h4.addListener(new DepositorSelectionLayoutListener(this));
        h5.addListener(new DurationSelectionLayoutListener(this, this));
        h6.addListener(new RigSelectionLayoutListener(this.controller, this));

        this.registeredComponents.add(h1);
        this.registeredComponents.add(h2);
        this.registeredComponents.add(h3);
        this.registeredComponents.add(h4);
        this.registeredComponents.add(h5);
        this.registeredComponents.add(h6);

        this.dynamicLayout.addComponent(h1, 0);
        this.dynamicLayout.addComponent(h2, 1);
        this.dynamicLayout.addComponent(h3, 2);
        this.dynamicLayout.addComponent(h4, 3);
        this.dynamicLayout.addComponent(h5, 4);
        this.dynamicLayout.addComponent(h6, 5);
        this.dynamicLayout.addComponent(new HorizontalLayout(), 6);

        rightCell(this.dynamicLayout);
        this.mainLayout.addComponent(this.directMemberInvestigationContainer);
        this.mainLayout.setExpandRatio(this.directMemberInvestigationContainer, 1.0f);
        this.mainLayout.attach();
        this.mainLayout.requestRepaintAll();
    }

    @Override
    public void showButtonLayout() {
        HorizontalLayout horizontalLayout = null;
        if (this.dynamicLayout != null && this.buttonLayout != null) {
            try {
                horizontalLayout = (HorizontalLayout) this.dynamicLayout.getComponent(COMPONENT_COUNT);
            }
            catch (final ClassCastException e) {
                LOG.error(e.getMessage());
            }
            if (horizontalLayout != null) {
                horizontalLayout.removeAllComponents();
                horizontalLayout.addComponent(this.buttonLayout);
            }
        }
    }

    @Override
    public void hideButtonLayout() {
        if (this.dynamicLayout != null && this.dynamicLayout.getComponent(COMPONENT_COUNT) != null) {
            try {
                ((HorizontalLayout) this.dynamicLayout.getComponent(COMPONENT_COUNT)).removeAllComponents();
            }
            catch (final ClassCastException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    @Override
    public Component getModifiedComponent() {
        return this.modifiedComponent;
    }

    @Override
    public void setModifiedComponent(final Component modifiedComponent) {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.containerProxy == null) ? 0 : this.containerProxy.hashCode());
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
        final InvestigationView other = (InvestigationView) obj;
        if (this.containerProxy == null) {
            if (other.containerProxy != null) {
                return false;
            }
        }
        else if (!this.containerProxy.equals(other.containerProxy)) {
            return false;
        }
        return true;
    }

    public List<RigBean> getAvailableRigs() {
        return this.controller.getAvailableRigs();
    }

    @Override
    public synchronized void setRigBean(final RigBean rigBean) {
        Preconditions.checkNotNull(rigBean, "input arg is null");
        this.investigationBean.setRigBean(rigBean);
    }

    @Override
    public void setInvestigator(final String investigatorId) {
        Preconditions.checkNotNull(investigatorId, "input arg is null");
        this.investigationBean.setInvestigator(investigatorId);
    }

    @Override
    public void setDuration(DurationBean durationBean) {
        Preconditions.checkNotNull(durationBean, "Duration is null");
        StringBuilder sb = new StringBuilder();
        if (durationBean.getDays() != 0) {
            sb.append(durationBean.getDays());
            sb.append((durationBean.getDays() == 1) ? " day " : " days ");
        }
        if (durationBean.getHours() != 0) {
            sb.append(durationBean.getHours());
            sb.append((durationBean.getHours() == 1) ? " hour " : " hours ");
        }
        sb.append(durationBean.getMinutes());
        sb.append((durationBean.getMinutes() == 0 || durationBean.getMinutes() == 1) ? " minute" : " minutes");
        this.investigationBean.setMaxRuntimeInMin(durationBean.getDays() * 1440 + durationBean.getHours() * 60
            + durationBean.getMinutes());

        ((Label) ((HorizontalLayout) this.dynamicLayout.getComponent(4)).getComponent(1)).setValue(sb.toString());
    }
}
