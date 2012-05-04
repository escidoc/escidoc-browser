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
package org.escidoc.browser.ui.maincontent;

import java.net.URISyntaxException;

import org.escidoc.browser.controller.OrgUnitController;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.repository.internal.OrgUnitProxy;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.view.helpers.OrgUnitMetadataTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class OrgUnitMetadataRecordsView {

    private final static Logger LOG = LoggerFactory.getLogger(OrgUnitMetadataRecordsView.class);

    private OrgUnitProxy orgUnit;

    private Router router;

    private OrgUnitController controller;

    public OrgUnitMetadataRecordsView(OrgUnitProxy orgUnit, Router router, OrgUnitController controller) {
        Preconditions.checkNotNull(orgUnit, "resourceProxy is null: %s", orgUnit);
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(controller, "controller is null: %s", controller);

        this.orgUnit = orgUnit;
        this.router = router;
        this.controller = controller;
    }

    public Panel asPanel() {
        final Panel panel = new Panel();
        panel.setSizeFull();
        panel.setStyleName(Runo.PANEL_LIGHT);
        VerticalLayout vl = new VerticalLayout();
        vl.setImmediate(false);
        vl.setWidth("100.0%");
        vl.setHeight("100.0%");
        vl.setMargin(false);
        vl.addComponent(buildMetaDataTab());

        panel.setContent(vl);
        return panel;
    }

    @SuppressWarnings("serial")
    private Component buildMetaDataTab() {
        Panel innerPanel = new Panel();
        innerPanel.setSizeFull();
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();

        final CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight("20px");
        buildPanelHeader(cssLayout, ViewConstants.METADATA);
        ThemeResource ICON = new ThemeResource("images/assets/plus.png");

        if (canAddMetadata()) {
            final Button addNewOrgUnitBtn = new Button();
            addNewOrgUnitBtn.addListener(new Button.ClickListener() {

                @Override
                public void buttonClick(@SuppressWarnings("unused")
                ClickEvent event) {
                    OnAddOrgUnitMetadata view = new OnAddOrgUnitMetadata(controller, router.getMainWindow());
                    view.showAddWindow();
                }
            });
            addNewOrgUnitBtn.setStyleName(BaseTheme.BUTTON_LINK);
            addNewOrgUnitBtn.addStyleName("floatright paddingtop3");
            addNewOrgUnitBtn.setWidth("20px");
            addNewOrgUnitBtn.setIcon(ICON);
            cssLayout.addComponent(addNewOrgUnitBtn);
        }
        vl.addComponent(cssLayout);
        OrgUnitMetadataTable metadataTable = new OrgUnitMetadataTable(orgUnit.getMetadataRecords(), controller, router);
        metadataTable.buildTable();
        vl.addComponent(metadataTable);
        vl.setExpandRatio(metadataTable, 9);
        innerPanel.setContent(vl);
        return innerPanel;
    }

    private static void buildPanelHeader(CssLayout cssLayout, String name) {
        cssLayout.addStyleName("v-accordion-item-caption v-caption v-captiontext");
        cssLayout.setWidth("100%");
        cssLayout.setMargin(false);

        final Label nameofPanel = new Label(name, Label.CONTENT_RAW);
        nameofPanel.setStyleName("accordion v-captiontext");
        nameofPanel.setWidth("70%");
        cssLayout.addComponent(nameofPanel);
    }

    private boolean canAddMetadata() {
        try {
            return router
                .getRepositories().pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_ORG_UNIT)
                .forResource(orgUnit.getId()).permitted();
        }
        catch (final EscidocClientException e) {
            LOG.debug("Infrastructure Exception " + e.getLocalizedMessage());
            return false;
        }
        catch (final URISyntaxException e) {
            LOG.debug("URI Exception " + e.getLocalizedMessage());
            return false;
        }
    }
}