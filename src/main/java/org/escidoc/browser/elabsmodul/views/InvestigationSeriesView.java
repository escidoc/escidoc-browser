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
import java.util.Arrays;
import java.util.List;

import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.interfaces.ILabsAction;
import org.escidoc.browser.elabsmodul.interfaces.ILabsPanel;
import org.escidoc.browser.elabsmodul.model.InvestigationSeriesBean;
import org.escidoc.browser.elabsmodul.views.helpers.ResourcePropertiesViewHelper;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;

import com.google.common.base.Preconditions;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class InvestigationSeriesView extends Panel implements ILabsPanel, ILabsAction {

    private static final int COMPONENT_COUNT = 0;

    private final String[] PROPERTIES = ELabsViewContants.INVESTIGATION_PROPERTIES;

    private VerticalLayout mainLayout;

    private VerticalLayout dynamicLayout;

    private BeanItem<InvestigationSeriesBean> beanItem;

    private List<HorizontalLayout> registeredComponents;

    private InvestigationSeriesBean investigationSeriesBean;

    private ResourceProxy containerProxy;

    private List<ResourceModel> breadCrumb;

    public InvestigationSeriesView(ContainerProxy containerProxy, InvestigationSeriesBean investigationSeriesBean,
        List<ResourceModel> breadCrumb) {

        Preconditions.checkNotNull(containerProxy, "containerProxy is null: %s", containerProxy);
        Preconditions.checkNotNull(investigationSeriesBean, "investigationSeriesBean is null: %s",
            investigationSeriesBean);
        Preconditions.checkNotNull(breadCrumb, "breadCrumb is null: %s", breadCrumb);

        this.containerProxy = containerProxy;
        this.investigationSeriesBean = investigationSeriesBean;
        this.breadCrumb = breadCrumb;

        initPanelComponents();
        buildPropertiesView();
    }

    private void buildPropertiesView() {
        addComponent(new ResourcePropertiesViewHelper(containerProxy, breadCrumb).generatePropertiesView());
    }

    private void initPanelComponents() {
        mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);

        dynamicLayout = new VerticalLayout();
        dynamicLayout.setSpacing(true);
        dynamicLayout.setMargin(true);

        beanItem = new BeanItem<InvestigationSeriesBean>(investigationSeriesBean, Arrays.asList(PROPERTIES));
        registeredComponents = new ArrayList<HorizontalLayout>(COMPONENT_COUNT);

        setContent(mainLayout);
        setScrollable(true);
    }

    @Override
    public void showButtonLayout() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void hideButtonLayout() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Component getModifiedComponent() {
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public void setModifiedComponent(Component modifiedComponent) {
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public Panel getReference() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}