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
package org.escidoc.browser.elabsmodul.views.helpers;

import java.util.List;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.ui.maincontent.BreadCrumbMenu;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.Runo;

public class ResourcePropertiesViewHelper {

    final String LAST_MODIFIED_BY = "Last modification by ";

    final String FLOAT_LEFT = "floatleft";

    final String FLOAT_RIGHT = "floatright";

    private ResourceProxy resourceProxy;

    private List<ResourceModel> breadCrumbModel;

    private String type;

    public ResourcePropertiesViewHelper(ResourceProxy resourceProxy, List<ResourceModel> breadCrumbModel) {
        this.resourceProxy = resourceProxy;
        this.breadCrumbModel = breadCrumbModel;
    }

    public ResourcePropertiesViewHelper(ResourceProxy resourceProxy, List<ResourceModel> breadCrumbModel, String type) {
        this.resourceProxy = resourceProxy;
        this.breadCrumbModel = breadCrumbModel;
        this.type = type;
    }

    public Panel generatePropertiesView() {
        // Item title
        String resourceType = resourceProxy.getType().toString();
        if (this.type != null && this.type.length() > 0) {
            resourceType = this.type;
        }
        final Label titleLabel =
            new Label(resourceType.substring(0, 1).toUpperCase() + resourceType.substring(1).toLowerCase() + ": "
                + resourceProxy.getName());
        titleLabel.setDescription("header");
        titleLabel.setStyleName("h2 fullwidth");

        // HR Ruler
        final Label descRuler = new Label("<hr/>", Label.CONTENT_RAW);
        descRuler.setStyleName("hr");

        // ItemProperties View
        final CssLayout propertiesView = new CssLayout();
        propertiesView.setWidth("100%");
        propertiesView.setHeight("100%");

        final Label descMetadata1 = new Label("ID: " + resourceProxy.getId());
        final Label descMetadata2 =
            new Label(LAST_MODIFIED_BY + " " + resourceProxy.getModifier() + " on " + resourceProxy.getModifiedOn(),
                Label.CONTENT_XHTML);

        final Panel pnlPropertiesLeft = buildLeftPanel();
        pnlPropertiesLeft.setWidth("40%");
        pnlPropertiesLeft.setHeight("20px");
        pnlPropertiesLeft.setStyleName(FLOAT_LEFT);
        pnlPropertiesLeft.addStyleName(Runo.PANEL_LIGHT);
        pnlPropertiesLeft.getLayout().setMargin(false);
        pnlPropertiesLeft.addComponent(descMetadata1);

        final Panel pnlPropertiesRight = buildRightPanel();
        pnlPropertiesRight.setWidth("60%");
        pnlPropertiesRight.setHeight("20px");
        pnlPropertiesRight.setStyleName(FLOAT_RIGHT);
        pnlPropertiesRight.addStyleName(Runo.PANEL_LIGHT);
        pnlPropertiesRight.getLayout().setMargin(false);
        pnlPropertiesRight.addComponent(descMetadata2);

        propertiesView.addComponent(pnlPropertiesLeft);
        propertiesView.addComponent(pnlPropertiesRight);

        Panel viewHandler = buildmainView();

        new BreadCrumbMenu(viewHandler, breadCrumbModel);

        viewHandler.addComponent(titleLabel);
        viewHandler.addComponent(descRuler);
        viewHandler.addComponent(propertiesView);

        return viewHandler;
    }

    private Panel buildmainView() {
        Panel viewHandler = new Panel();
        viewHandler.getLayout().setMargin(false);
        viewHandler.setStyleName(Runo.PANEL_LIGHT);
        return viewHandler;
    }

    private Panel buildRightPanel() {
        final Panel pnlPropertiesRight = new Panel();
        pnlPropertiesRight.setWidth("60%");
        pnlPropertiesRight.setHeight("60px");
        pnlPropertiesRight.setStyleName(FLOAT_RIGHT);
        pnlPropertiesRight.addStyleName(Runo.PANEL_LIGHT);
        return pnlPropertiesRight;
    }

    private Panel buildLeftPanel() {
        final Panel pnlPropertiesLeft = new Panel();
        pnlPropertiesLeft.setWidth("40%");
        pnlPropertiesLeft.setHeight("70px");
        pnlPropertiesLeft.setStyleName(FLOAT_LEFT);
        pnlPropertiesLeft.addStyleName(Runo.PANEL_LIGHT);
        return pnlPropertiesLeft;
    }
}
