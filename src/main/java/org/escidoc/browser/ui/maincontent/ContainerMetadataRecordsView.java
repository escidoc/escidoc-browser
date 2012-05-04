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

import org.escidoc.browser.controller.ContainerController;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.OnAddContainerMetadata;
import org.escidoc.browser.ui.listeners.VersionHistoryClickListener;
import org.escidoc.browser.ui.view.helpers.ContainerMetadataTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class ContainerMetadataRecordsView {

    private static final Logger LOG = LoggerFactory.getLogger(ContainerMetadataRecordsView.class);

    private final Panel panel = new Panel();

    private final ContainerProxy resourceProxy;

    private final Window mainWindow;

    private final Repositories repositories;

    private final Router router;

    private Accordion metadataRecs;

    private Panel pnlmdRec;

    private final ContainerController containerController;

    public ContainerMetadataRecordsView(final ResourceProxy resourceProxy, final Repositories repositories,
        final Router router, ContainerController containerController) {

        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(router, "mainSite is null: %s", router);
        Preconditions.checkNotNull(containerController, "containerController is null: %s", containerController);

        this.resourceProxy = (ContainerProxy) resourceProxy;
        this.repositories = repositories;
        this.router = router;
        this.containerController = containerController;

        this.mainWindow = router.getMainWindow();
    }

    @Deprecated
    /**
     * Using asPanel instead
     * @return
     */
    public Accordion asAccord() {
        metadataRecs = new Accordion();
        metadataRecs.setSizeFull();

        pnlmdRec = lblMetadaRecs();
        addComponentsAsTabs(lblAddtionalResources());
        return metadataRecs;
    }

    private void addComponentsAsTabs(final Panel additionalResourcesPanel) {
        metadataRecs.addTab(pnlmdRec, "Metadata", null);
        metadataRecs.addTab(additionalResourcesPanel, "Additional Resources", null);
    }

    public Panel asPanel() {
        final Panel pnlmetadataRecs = new Panel();
        pnlmetadataRecs.setSizeFull();
        VerticalLayout vl = new VerticalLayout();
        vl.setImmediate(false);
        vl.setWidth("100.0%");
        vl.setHeight("100.0%");
        vl.setMargin(false);
        vl.addComponent(lblMetadaRecs());

        pnlmetadataRecs.setContent(vl);
        return pnlmetadataRecs;
    }

    @SuppressWarnings("serial")
    private Panel lblAddtionalResources() {
        final Panel pnl = new Panel();
        pnl.setSizeFull();
        VerticalLayout hl = new VerticalLayout();
        hl.setSizeFull();
        final Button btnVersionHistoryContainer =
            new Button("Container Version History", new VersionHistoryClickListener(resourceProxy, mainWindow,
                repositories));
        btnVersionHistoryContainer.setStyleName(BaseTheme.BUTTON_LINK);
        btnVersionHistoryContainer.setDescription("Show Version history in a Pop-up");

        final CssLayout cssLayout = new CssLayout();
        buildPanelHeader(cssLayout, "Additional Resources");
        ThemeResource ICON = new ThemeResource("images/assets/plus.png");

        final Button addResourceButton = new Button();
        addResourceButton.setStyleName(BaseTheme.BUTTON_LINK);
        addResourceButton.addStyleName("floatright paddingtop3");
        addResourceButton.setWidth("20px");
        addResourceButton.setIcon(ICON);
        addResourceButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(final ClickEvent event) {
                final Window subwindow = new Window("A modal subwindow");
                subwindow.setModal(true);
                subwindow.setWidth("650px");
                VerticalLayout layout = (VerticalLayout) subwindow.getContent();
                layout.setMargin(true);
                layout.setSpacing(true);

                subwindow.addComponent(new Label("Not yet implemented"));
                Button close = new Button("Close", new Button.ClickListener() {
                    @Override
                    public void buttonClick(@SuppressWarnings("unused")
                    ClickEvent event) {
                        (subwindow.getParent()).removeWindow(subwindow);
                    }
                });
                layout.addComponent(close);
                layout.setComponentAlignment(close, Alignment.TOP_RIGHT);

                router.getMainWindow().addWindow(subwindow);

            }

        });
        cssLayout.addComponent(addResourceButton);
        hl.addComponent(cssLayout);

        hl.addComponent(btnVersionHistoryContainer);
        pnl.setContent(hl);
        return pnl;
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

    private Panel lblMetadaRecs() {
        panel.setSizeFull();
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        final CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight("20px");
        buildPanelHeader(cssLayout, ViewConstants.METADATA);
        ThemeResource ICON = new ThemeResource("images/assets/plus.png");

        if (containerController.hasAccess()) {
            final Button btnAddNew = new Button();
            btnAddNew.addListener(new OnAddContainerMetadata(mainWindow, repositories, resourceProxy));
            btnAddNew.setStyleName(BaseTheme.BUTTON_LINK);
            btnAddNew.addStyleName("floatright paddingtop3");
            btnAddNew.setWidth("20px");
            btnAddNew.setIcon(ICON);
            cssLayout.addComponent(btnAddNew);
        }
        vl.addComponent(cssLayout);
        ContainerMetadataTable metadataTable =
            new ContainerMetadataTable(resourceProxy.getMetadataRecords(), containerController, router, resourceProxy,
                repositories);
        metadataTable.buildTable();
        vl.addComponent(metadataTable);
        vl.setComponentAlignment(metadataTable, Alignment.TOP_LEFT);
        vl.setExpandRatio(metadataTable, 0.9f);
        panel.setContent(vl);

        return panel;
    }
}