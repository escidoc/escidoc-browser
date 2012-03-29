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

import com.google.common.base.Preconditions;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Runo;

import org.escidoc.browser.controller.OrgUnitController;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.internal.OrgUnitProxy;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.view.helpers.OUParentTableVH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ParentsView {

    private final static Logger LOG = LoggerFactory.getLogger(ParentsView.class);

    private final OrgUnitProxy orgUnitProxy;

    private Window mainWindow;

    private Router router;

    private OrgUnitController orgUnitController;

    public ParentsView(ResourceProxy resourceProxy, Window mainWindow, Router router,
        OrgUnitController orgUnitController) {
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(orgUnitController, "orgUnitController is null: %s", orgUnitController);
        this.orgUnitProxy = (OrgUnitProxy) resourceProxy;
        this.mainWindow = mainWindow;
        this.router = router;
        this.orgUnitController = orgUnitController;
    }

    public Component asAccord() {
        final Accordion accordion = new Accordion();
        accordion.setSizeFull();
        accordion.addTab(buildParentsList(), ViewConstants.PARENTS, null);
        return accordion;
    }

    public Panel asPanel() {
        final Panel pnlmetadataRecs = new Panel();
        pnlmetadataRecs.setSizeFull();
        pnlmetadataRecs.setStyleName(Runo.PANEL_LIGHT);
        VerticalLayout vl = new VerticalLayout();
        vl.setImmediate(false);
        vl.setWidth("100.0%");
        vl.setHeight("100.0%");
        vl.setMargin(false);
        vl.addComponent(buildParentsList());

        pnlmetadataRecs.setContent(vl);
        return pnlmetadataRecs;
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

    @SuppressWarnings("serial")
    private Component buildParentsList() {
        // ViewConstants.PARENTS
        final Panel panel = new Panel();
        panel.setSizeFull();
        panel.setStyleName(Runo.PANEL_LIGHT);

        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();

        final CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight("20px");
        buildPanelHeader(cssLayout, ViewConstants.PARENTS);
        ThemeResource ICON = new ThemeResource("images/assets/plus.png");

        Button btnAdd = new Button();
        btnAdd.setStyleName(BaseTheme.BUTTON_LINK);
        btnAdd.addStyleName("floatright paddingtop3");
        btnAdd.setWidth("20px");
        btnAdd.setIcon(ICON);
        btnAdd.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {

                final Window subwindow = new Window("Manage Organizational Unit Parents");
                subwindow.setModal(true);
                subwindow.setWidth("650px");
                VerticalLayout layout = (VerticalLayout) subwindow.getContent();
                layout.setMargin(true);
                layout.setSpacing(true);

                try {
                    subwindow.addComponent(new OrgUnitParentEditView(orgUnitProxy, orgUnitProxy.getParentList(),
                        router, orgUnitController));
                }
                catch (EscidocClientException e) {
                    orgUnitController.showError(e);
                }
                Button close = new Button("Close", new Button.ClickListener() {

                    @Override
                    public void buttonClick(@SuppressWarnings("unused") com.vaadin.ui.Button.ClickEvent event) {
                        (subwindow.getParent()).removeWindow(subwindow);
                    }
                });
                layout.addComponent(close);
                layout.setComponentAlignment(close, Alignment.TOP_RIGHT);

                mainWindow.addWindow(subwindow);
            }
        });
        cssLayout.addComponent(btnAdd);
        vl.addComponent(cssLayout);
        List<ResourceModel> l = orgUnitProxy.getParentList();
        OUParentTableVH parentTable = new OUParentTableVH(orgUnitProxy, router, orgUnitController);
        vl.addComponent(parentTable);
        vl.setExpandRatio(parentTable, 9f);
        // TODO here comes table
        panel.setContent(vl);
        return panel;
    }
}