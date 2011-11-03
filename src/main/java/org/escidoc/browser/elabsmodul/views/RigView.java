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
import org.escidoc.browser.elabsmodul.interfaces.IRigAction;
import org.escidoc.browser.elabsmodul.model.RigBean;
import org.escidoc.browser.elabsmodul.views.helpers.LabsLayoutHelper;
import org.escidoc.browser.elabsmodul.views.helpers.LabsRigTableHelper;
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

/**
 * Specific BWeLabsView for Rig item-element.
 */
public class RigView extends Panel implements ILabsPanel, ILabsAction {

    private static final long serialVersionUID = -6095498070313755653L;

    private static Logger LOG = LoggerFactory.getLogger(RigView.class);

    private final String[] PROPERTIES = ELabsViewContants.RIG_PROPERTIES;

    final String LAST_MODIFIED_BY = "Last modification by ";

    final String FLOAT_LEFT = "floatleft";

    final String FLOAT_RIGHT = "floatright";

    private final int COMPONENT_COUNT = 3;

    private POJOItem<RigBean> pojoItem = null;

    private RigBean rigBean = null;

    private VerticalLayout mainLayout = null, dynamicLayout = null;

    private LayoutClickListener clientViewEventHandler = null;

    private ClickListener mouseClickListener = null;

    private List<HorizontalLayout> registeredComponents = null;

    private HorizontalLayout modifiedComponent = null;

    private HorizontalLayout buttonLayout = null;

    private final IRigAction controller;

    private List<ResourceModel> breadCrumbModel;

    private ItemProxy itemProxy;

    private EscidocServiceLocation serviceLocation;

    private LabsRigTableHelper rigTableHelper;

    public RigView(RigBean sourceBean, final IRigAction controller, List<ResourceModel> breadCrumbModel,
        final ResourceProxy resourceProxy, EscidocServiceLocation serviceLocation) {
        this.rigBean = (sourceBean != null) ? sourceBean : new RigBean();
        this.controller = controller;
        this.breadCrumbModel = breadCrumbModel;
        this.itemProxy = (ItemProxy) resourceProxy;
        this.serviceLocation = serviceLocation;
        this.rigTableHelper = new LabsRigTableHelper(this);

        initialisePanelComponents();
        buildPropertiesGUI();
        buildPanelGUI();
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
        this.pojoItem = new POJOItem<RigBean>(this.rigBean, PROPERTIES);
        this.registeredComponents = new ArrayList<HorizontalLayout>(COMPONENT_COUNT);
        this.setContent(mainLayout);
        this.setScrollable(true);
    }

    /**
     * Build the read-only layout of the eLabsElement
     */
    private void buildPropertiesGUI() {
        this.addComponent(new ResourcePropertiesViewHelper(itemProxy, breadCrumbModel, "Rig", serviceLocation)
            .generatePropertiesView());
    }

    /**
     * Build the specific editable layout of the eLabsElement.
     */
    private void buildPanelGUI() {
        Preconditions.checkNotNull(rigTableHelper, "Helper is not null");
        this.dynamicLayout.setStyleName(ELabsViewContants.STYLE_ELABS_FORM);

        this.buttonLayout = LabsLayoutHelper.createButtonLayout();
        HorizontalLayout h1 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_RIG_TITLE,
                pojoItem.getItemProperty(ELabsViewContants.P_RIG_TITLE));
        HorizontalLayout h2 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_RIG_DESC,
                getPojoItem().getItemProperty(ELabsViewContants.P_RIG_DESC));
        HorizontalLayout h3 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndRelatedDataForRig(ELabsViewContants.L_RIG_CONTENT,
                getPojoItem().getItemProperty(ELabsViewContants.P_RIG_CONTENT), this.rigBean, this.controller,
                this.rigTableHelper);

        registeredComponents.add(h1);
        registeredComponents.add(h2);
        registeredComponents.add(h3);

        this.dynamicLayout.addComponent(h1, 0);
        this.dynamicLayout.addComponent(h2, 1);
        this.dynamicLayout.addComponent(h3, 2);
        this.dynamicLayout.addComponent(new HorizontalLayout(), 3);

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
                    RigView.this.controller.saveAction(RigView.this.rigBean);
                    RigView.this.resetLayout();
                    RigView.this.dynamicLayout.requestRepaintAll();
                }
            }
        };

        try {
            ((Button) this.buttonLayout.getComponent(1)).addListener(this.mouseClickListener);
        }
        catch (ClassCastException e) {
            LOG.error(e.getMessage());
        }
    }

    protected void resetLayout() {
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
            if (LabsLayoutHelper.switchToLabelFromEditedField(tempParentLayout)) {
                this.setModifiedComponent(null);
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

    public POJOItem<RigBean> getPojoItem() {
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
        final RigView other = (RigView) obj;
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
