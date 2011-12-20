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

import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.LABEL_WIDTH;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.interfaces.ILabsAction;
import org.escidoc.browser.elabsmodul.interfaces.ILabsPanel;
import org.escidoc.browser.elabsmodul.interfaces.ISaveAction;
import org.escidoc.browser.elabsmodul.model.InvestigationSeriesBean;
import org.escidoc.browser.elabsmodul.views.helpers.LabsLayoutHelper;
import org.escidoc.browser.elabsmodul.views.helpers.ResourcePropertiesViewHelper;
import org.escidoc.browser.elabsmodul.views.listeners.LabsClientViewEventHandler;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.maincontent.View;
import org.escidoc.browser.ui.view.helpers.DirectMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class InvestigationSeriesView extends View implements ILabsPanel, ILabsAction {

    private static final long serialVersionUID = 8357388998587122127L;

    private static final int COMPONENT_COUNT = 3;

    private static final String[] PROPERTIES = ELabsViewContants.INVESTIGATION_SERIES_PROPERTIES;

    private VerticalLayout mainLayout;

    private VerticalLayout dynamicLayout;

    private BeanItem<InvestigationSeriesBean> beanItem;

    private List<HorizontalLayout> registeredComponents;

    private InvestigationSeriesBean investigationSeriesBean;

    private ResourceProxy containerProxy;

    private List<ResourceModel> breadCrumb;

    private Component modifiedComponent;

    private ISaveAction controller;

    private Component buttonLayout;

    private HorizontalLayout hl = new HorizontalLayout();

    private Button saveButton = new Button("Save");

    private HorizontalLayout directMemberInvSeriesContainer = new HorizontalLayout();

    private Router router;

    private static Logger LOG = LoggerFactory.getLogger(InvestigationSeriesView.class);

    public InvestigationSeriesView(ContainerProxy containerProxy, InvestigationSeriesBean investigationSeriesBean,
        List<ResourceModel> breadCrumb, ISaveAction saveAction, Router router) {

        Preconditions.checkNotNull(containerProxy, "containerProxy is null: %s", containerProxy);
        Preconditions.checkNotNull(investigationSeriesBean, "investigationSeriesBean is null: %s",
            investigationSeriesBean);
        Preconditions.checkNotNull(breadCrumb, "breadCrumb is null: %s", breadCrumb);
        Preconditions.checkNotNull(saveAction, "saveAction is null: %s", saveAction);

        this.containerProxy = containerProxy;
        this.setViewName(containerProxy.getName());
        this.investigationSeriesBean = investigationSeriesBean;
        this.breadCrumb = breadCrumb;
        this.controller = saveAction;
        this.router = router;
        setSizeFull();
        initPanelComponents();
        buildPropertiesView();
        buildContainerGUI();
        buildPanelView();
        createPanelListener();
        createClickListener();
    }

    private void createClickListener() {
        saveButton.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 6314520686584942778L;

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                if (event.getButton().equals(saveButton)) {
                    InvestigationSeriesView.this.resetLayout();
                    dynamicLayout.requestRepaintAll();
                    controller.saveAction(investigationSeriesBean);
                }
            }
        });
    }

    private void createPanelListener() {
        dynamicLayout.addListener(new LabsClientViewEventHandler(registeredComponents, dynamicLayout, this, this));

    }

    private void buildPanelView() {
        dynamicLayout.setStyleName(ELabsViewContants.STYLE_ELABS_FORM);

        buttonLayout = createButtonLayout();

        HorizontalLayout name =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_TITLE,
                beanItem.getItemProperty("name"), true);
        HorizontalLayout description =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_DESCRIPTION,
                beanItem.getItemProperty("description"), true);

        registeredComponents.add(name);
        registeredComponents.add(description);

        dynamicLayout.addComponent(name, 0);
        dynamicLayout.addComponent(description, 1);
        dynamicLayout.addComponent(hl, 2);

        rightCell(dynamicLayout);
        mainLayout.addComponent(directMemberInvSeriesContainer);
        mainLayout.setExpandRatio(directMemberInvSeriesContainer, 1.0f);
        mainLayout.attach();
        mainLayout.requestRepaintAll();

    }

    private Component createButtonLayout() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        Label blank = new Label("");
        blank.setWidth(LABEL_WIDTH);
        horizontalLayout.addComponent(blank, 0);
        horizontalLayout.addComponent(saveButton, 1);
        return horizontalLayout;
    }

    private void buildPropertiesView() {
        addComponent(new ResourcePropertiesViewHelper(containerProxy, breadCrumb, "Investigationseries",
            router.getServiceLocation()).generatePropertiesView());
    }

    private void initPanelComponents() {
        mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.setSizeFull();
        dynamicLayout = new VerticalLayout();
        dynamicLayout.setSpacing(true);
        // dynamicLayout.setMargin(true);

        beanItem = new BeanItem<InvestigationSeriesBean>(investigationSeriesBean, Arrays.asList(PROPERTIES));
        registeredComponents = new ArrayList<HorizontalLayout>(COMPONENT_COUNT);

        setStyleName(Runo.PANEL_LIGHT);
        setContent(mainLayout);
        setScrollable(true);
    }

    private void buildContainerGUI() {
        directMemberInvSeriesContainer.setWidth("100%");
        directMemberInvSeriesContainer.setHeight("100%");
        try {
            leftCell();
        }
        catch (EscidocClientException e) {
            router.getMainWindow().showNotification(
                "Could not load the Direct Members Helper in the View" + e.getLocalizedMessage());
        }
    }

    /**
     * This is the inner Right Cell within a Context By default a set of Organizational Unit / Admin Description /
     * RelatedItem / Resources are bound
     * 
     * @param comptoBind
     */
    // TODO why deprecated?
    @SuppressWarnings("deprecation")
    private void rightCell(Component comptoBind) {
        Panel rightpnl = new Panel();
        rightpnl.setStyleName("floatright");
        rightpnl.addStyleName(Runo.PANEL_LIGHT);
        rightpnl.setSizeFull();
        rightpnl.getLayout().setMargin(false);
        rightpnl.addComponent(comptoBind);
        directMemberInvSeriesContainer.addComponent(rightpnl);
        directMemberInvSeriesContainer.setExpandRatio(rightpnl, 7.0f);

    }

    private void leftCell() throws EscidocClientException {
        Panel leftPanel = new Panel();
        leftPanel.setStyleName("directmembers floatleft");
        leftPanel.setScrollable(false);
        leftPanel.setSizeFull();

        VerticalLayout vl = new VerticalLayout();
        vl.setMargin(false);
        vl.setSizeFull();
        leftPanel.setContent(vl);
        new DirectMember(router.getServiceLocation(), router, containerProxy.getId(), router.getMainWindow(),
            router.getRepositories(), leftPanel, ResourceType.CONTAINER.toString()).containerAsTree();
        directMemberInvSeriesContainer.addComponent(leftPanel);
        directMemberInvSeriesContainer.setExpandRatio(leftPanel, 3.0f);
    }

    @Override
    public void showButtonLayout() {
        hl.removeAllComponents();
        hl.addComponent(buttonLayout);
    }

    @Override
    public void hideButtonLayout() {
        hl.removeAllComponents();
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
            this.modifiedComponent = modifiedComponent;
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
        result = prime * result + ((containerProxy == null) ? 0 : containerProxy.hashCode());
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
        final InvestigationSeriesView other = (InvestigationSeriesView) obj;
        if (containerProxy == null) {
            if (other.containerProxy != null) {
                return false;
            }
        }
        else if (!containerProxy.equals(other.containerProxy)) {
            return false;
        }
        return true;
    }

    protected void resetLayout() {
        Preconditions.checkNotNull(dynamicLayout, "View's dynamiclayout is null.");

        HorizontalLayout tempParentLayout = null;
        for (final Iterator<Component> iterator = dynamicLayout.getComponentIterator(); iterator.hasNext();) {
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
                setModifiedComponent(null);
            }
        }
    }
}
