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
import org.escidoc.browser.elabsmodul.model.StudyBean;
import org.escidoc.browser.elabsmodul.views.helpers.LabsLayoutHelper;
import org.escidoc.browser.elabsmodul.views.helpers.LabsStudyTableHelper;
import org.escidoc.browser.elabsmodul.views.helpers.ResourcePropertiesViewHelper;
import org.escidoc.browser.elabsmodul.views.listeners.LabsClientViewEventHandler;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.view.helpers.DirectMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.util.POJOItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class StudyView extends Panel implements ILabsPanel, ILabsAction {

    private static final long serialVersionUID = -6071621079544448578L;

    private static Logger LOG = LoggerFactory.getLogger(StudyView.class);

    private static final String RIGHT_PANEL = "";

    private static final String[] PROPERTIES = ELabsViewContants.STUDY_PROPERTIES;

    private final StudyBean studyBean;

    private final ISaveAction controller;

    private final List<ResourceModel> breadCrumbModel;

    private final ContainerProxy containerProxy;

    private final int COMPONENT_COUNT = 4;

    private final HorizontalLayout directMemberExperimentContainer = new HorizontalLayout();

    private final Router router;

    private POJOItem<StudyBean> pojoItem;

    private VerticalLayout mainLayout, dynamicLayout;

    private HorizontalLayout buttonLayout;

    private List<HorizontalLayout> registeredComponents;

    private LabsClientViewEventHandler clientViewEventHandler;

    private ClickListener mouseClickListener;

    private HorizontalLayout modifiedComponent;

    private LabsStudyTableHelper studyTableHelper = null;

    public StudyView(final StudyBean sourceBean, final ISaveAction controller,
        final List<ResourceModel> breadCrumbModel, final ResourceProxy resourceProxy, final Router router) {
        Preconditions.checkNotNull(sourceBean, "sourceBean is null: %s", sourceBean);
        Preconditions.checkNotNull(controller, "saveComponent is null: %s", controller);
        Preconditions.checkNotNull(breadCrumbModel, "breadCrumbModel is null: %s", breadCrumbModel);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(router, "router is null: %s", router);

        studyBean = (sourceBean != null) ? sourceBean : new StudyBean();
        this.controller = controller;
        this.breadCrumbModel = breadCrumbModel;
        containerProxy = (ContainerProxy) resourceProxy;
        this.router = router;
        this.studyTableHelper = new LabsStudyTableHelper(studyBean, this, this.controller.hasUpdateAccess());

        initialisePanelComponents();
        buildContainerGUI();
        buildPropertiesGUI();
        buildPanelGUI();
        if (controller.hasUpdateAccess()) {
            createPanelListener();
            createClickListener();
        }
    }

    @SuppressWarnings("serial")
    private void createClickListener() {
        mouseClickListener = new Button.ClickListener() {
            @Override
            public void buttonClick(final com.vaadin.ui.Button.ClickEvent event) {
                if (event.getButton().getCaption().equals("Save")) {
                    controller.saveAction(studyBean);
                    StudyView.this.resetLayout();
                    dynamicLayout.requestRepaintAll();
                }
            }
        };

        try {
            ((Button) buttonLayout.getComponent(1)).addListener(mouseClickListener);
        }
        catch (final ClassCastException e) {
            // TODO report error to user?
            LOG.error(e.getMessage());
        }
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
            if (LabsLayoutHelper.switchToLabelFromEditedField(tempParentLayout)) {
                setModifiedComponent(null);
            }
        }

    }

    private void initialisePanelComponents() {
        mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.setSizeFull();
        dynamicLayout = new VerticalLayout();
        dynamicLayout.setSpacing(true);
        dynamicLayout.setMargin(true);
        dynamicLayout.setSizeFull();

        pojoItem = new POJOItem<StudyBean>(studyBean, PROPERTIES);
        registeredComponents = new ArrayList<HorizontalLayout>(COMPONENT_COUNT);

        setContent(mainLayout);

        this.setStyleName(Runo.PANEL_LIGHT);
        setScrollable(true);
    }

    private void buildPanelGUI() {
        Preconditions.checkNotNull(studyTableHelper, "StudyHelper is null");
        dynamicLayout.setStyleName(ELabsViewContants.STYLE_ELABS_FORM);

        buttonLayout = LabsLayoutHelper.createButtonLayout();
        final HorizontalLayout h1 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_STUDY_TITLE,
                pojoItem.getItemProperty(ELabsViewContants.P_STUDY_TITLE));
        final HorizontalLayout h2 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_STUDY_DESC,
                getPojoItem().getItemProperty(ELabsViewContants.P_STUDY_DESC));
        final HorizontalLayout h3 =
            LabsLayoutHelper.createHorizontalLayoutWithPublicationDataForStudy(ELabsViewContants.L_STUDY_MOT_PUB,
                getPojoItem().getItemProperty(ELabsViewContants.P_STUDY_MOT_PUB), true, studyTableHelper);
        final HorizontalLayout h4 =
            LabsLayoutHelper.createHorizontalLayoutWithPublicationDataForStudy(ELabsViewContants.L_STUDY_RES_PUB,
                getPojoItem().getItemProperty(ELabsViewContants.P_STUDY_RES_PUB), false, studyTableHelper);

        registeredComponents.add(h1);
        registeredComponents.add(h2);
        registeredComponents.add(h3);
        registeredComponents.add(h4);

        dynamicLayout.addComponent(h1, 0);
        dynamicLayout.addComponent(h2, 1);
        dynamicLayout.addComponent(h3, 2);
        dynamicLayout.addComponent(h4, 3);

        dynamicLayout.addComponent(new HorizontalLayout(), 4);

        rightCell(dynamicLayout);
        mainLayout.addComponent(directMemberExperimentContainer);
        mainLayout.attach();
        mainLayout.requestRepaintAll();
    }

    private void createPanelListener() {
        clientViewEventHandler = new LabsClientViewEventHandler(registeredComponents, dynamicLayout, this, this);
        dynamicLayout.addListener(clientViewEventHandler);

    }

    public POJOItem<StudyBean> getPojoItem() {
        return pojoItem;
    }

    /**
     * Builds a Container for the DM and the ElabPanel
     */
    private void buildContainerGUI() {
        directMemberExperimentContainer.setWidth("100%");
        directMemberExperimentContainer.setHeight("100%");
        try {
            leftCell();
        }
        catch (final EscidocClientException e) {
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
    private void rightCell(final Component comptoBind) {
        final Panel rightpnl = new Panel();
        rightpnl.setDescription(RIGHT_PANEL);
        rightpnl.setStyleName("floatright");
        rightpnl.addStyleName(Runo.PANEL_LIGHT);
        rightpnl.setSizeFull();
        VerticalLayout vlRightPnl = new VerticalLayout();
        vlRightPnl.setSizeFull();
        vlRightPnl.setMargin(false);
        vlRightPnl.addComponent(comptoBind);
        rightpnl.setContent(vlRightPnl);
        directMemberExperimentContainer.addComponent(rightpnl);
        directMemberExperimentContainer.setExpandRatio(rightpnl, 7.0f);
    }

    private void leftCell() throws EscidocClientException {
        final Panel leftPanel = new Panel();
        leftPanel.setStyleName("directmembers floatleft");
        leftPanel.setScrollable(false);
        leftPanel.setSizeFull();
        VerticalLayout vlLeftPanel = new VerticalLayout();
        vlLeftPanel.setSizeFull();
        vlLeftPanel.setMargin(false);
        leftPanel.setContent(vlLeftPanel);
        new DirectMember(router.getServiceLocation(), router, containerProxy.getId(), router.getMainWindow(),
            router.getRepositories(), leftPanel, ResourceType.CONTAINER.toString()).containerAsTree();
        directMemberExperimentContainer.addComponent(leftPanel);
        directMemberExperimentContainer.setExpandRatio(leftPanel, 3.0f);
    }

    /**
     * Build the read-only layout of the eLabsElement
     */
    private void buildPropertiesGUI() {
        addComponent(new ResourcePropertiesViewHelper(containerProxy, breadCrumbModel, "Study",
            router.getServiceLocation()).generatePropertiesView());
    }

    @Override
    public void showButtonLayout() {
        HorizontalLayout horizontalLayout = null;
        if (dynamicLayout != null && buttonLayout != null) {
            try {
                horizontalLayout = (HorizontalLayout) dynamicLayout.getComponent(COMPONENT_COUNT);
            }
            catch (final ClassCastException e) {
                // TODO log exception and tell the user something going wrong
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
    public void setModifiedComponent(final Component modifiedComponent) {
        try {
            this.modifiedComponent = (HorizontalLayout) modifiedComponent;
        }
        catch (final ClassCastException e) {
            // TODO tell the user something going wrong
            LOG.error(e.getMessage());
        }
    }

    @Override
    public Panel getReference() {
        return this;
    }

    @Override
    public void hideButtonLayout() {
        if (dynamicLayout != null && dynamicLayout.getComponent(COMPONENT_COUNT) != null) {
            try {
                ((HorizontalLayout) dynamicLayout.getComponent(COMPONENT_COUNT)).removeAllComponents();
            }
            catch (final ClassCastException e) {
                // TODO tell the user something going wrong
                LOG.error(e.getMessage());
            }
        }
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
        final StudyView other = (StudyView) obj;
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
}