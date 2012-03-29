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

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import org.escidoc.browser.controller.ItemController;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.AddMetaDataFileItemBehaviour;
import org.escidoc.browser.ui.listeners.EditMetaDataFileItemBehaviour;
import org.escidoc.browser.ui.listeners.RelationsClickListener;
import org.escidoc.browser.ui.listeners.VersionHistoryClickListener;
import org.escidoc.browser.ui.view.helpers.ItemMetadataTableVH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.escidoc.core.resources.common.MetadataRecord;

public class MetadataRecsItem {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataRecsItem.class);

    private final ItemProxy resourceProxy;

    private final Window mainWindow;

    private final EscidocServiceLocation escidocServiceLocation;

    private final Repositories repositories;

    private final Panel pnl = new Panel();

    private VerticalLayout btnaddContainer = new VerticalLayout();

    private final Router router;

    private ItemController itemController;

    MetadataRecsItem(final ItemProxy resourceProxy, final Repositories repositories, final Router router,
        ItemController controller) {
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null.");
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        this.router = router;
        this.resourceProxy = resourceProxy;
        this.mainWindow = router.getMainWindow();
        this.repositories = repositories;
        this.itemController = controller;
        this.escidocServiceLocation = router.getServiceLocation();

    }

    protected Accordion asAccord() {
        final Accordion metadataRecs = new Accordion();
        metadataRecs.setSizeFull();

        final Panel pnlMetadataRecs = lblMetadaRecs();
        final Panel pnlAdditionalResources = lblAddtionalResources();

        // Add the components as tabs in the Accordion.
        metadataRecs.addTab(pnlMetadataRecs, "Metadata", null);
        // metadataRecs.addTab(l2, "Relations", null);
        metadataRecs.addTab(pnlAdditionalResources, "Additional Resources", null);
        return metadataRecs;
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

    private Panel lblAddtionalResources() {

        final Button btnVersionHistory =
            new Button("Item Version History", new VersionHistoryClickListener(resourceProxy, mainWindow, repositories));
        btnVersionHistory.setStyleName(BaseTheme.BUTTON_LINK);
        btnVersionHistory.setDescription("Show Version history in a Pop-up");

        final Button btnContentRelation =
            new Button("Item Content Relations", new RelationsClickListener(resourceProxy, repositories, router));
        btnContentRelation.setStyleName(BaseTheme.BUTTON_LINK);
        btnContentRelation.setDescription("Show Version history in a Pop-up");

        final Panel pnl = new Panel();
        pnl.setHeight("100%");
        pnl.addComponent(btnVersionHistory);
        pnl.addComponent(btnContentRelation);
        return pnl;
    }

    private Panel lblMetadaRecs() {
        pnl.setSizeFull();
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        final CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight("20px");
        buildPanelHeader(cssLayout, ViewConstants.METADATA);
        ThemeResource ICON = new ThemeResource("images/assets/plus.png");

        ItemMetadataTableVH metadataItem = new ItemMetadataTableVH(itemController, router, resourceProxy, repositories);
        if (itemController.hasAccess()) {
            final Button btnAddNew = new Button();
            btnAddNew.addListener(new AddMetaDataFileItemBehaviour(mainWindow, repositories, resourceProxy));
            btnAddNew.setStyleName(BaseTheme.BUTTON_LINK);
            btnAddNew.addStyleName("floatright paddingtop3");
            btnAddNew.setWidth("20px");
            btnAddNew.setIcon(ICON);
            cssLayout.addComponent(btnAddNew);
        }
        vl.addComponent(cssLayout);
        vl.addComponent(metadataItem);
        vl.setExpandRatio(metadataItem, 9f);
        pnl.setContent(vl);
        return pnl;
    }

    private void buildPanelHeader(CssLayout cssLayout, String name) {
        cssLayout.addStyleName("v-accordion-item-caption v-caption v-captiontext");
        cssLayout.setWidth("100%");
        cssLayout.setMargin(false);

        final Label nameofPanel = new Label(name, Label.CONTENT_RAW);
        nameofPanel.setStyleName("accordion v-captiontext");
        nameofPanel.setWidth("70%");
        cssLayout.addComponent(nameofPanel);
    }

    /**
     * Create the buttons to be shown on the MetaDataRecords Accordion
     * 
     * @param pnl
     * @param metadataRecord
     */
    private void buildMDButtons(final VerticalLayout btnaddContainer, final MetadataRecord metadataRecord) {
        final HorizontalLayout hl = new HorizontalLayout();
        hl.setStyleName("metadata");

        Link btnmdRec =
            new Link(metadataRecord.getName(), new ExternalResource(escidocServiceLocation.getEscidocUri()
                + metadataRecord.getXLinkHref()));
        btnmdRec.setTargetName("_blank");

        btnmdRec.setStyleName(BaseTheme.BUTTON_LINK);
        btnmdRec.setDescription("Show metadata information in a separate window");
        hl.addComponent(btnmdRec);
        hl.addComponent(new Label("&nbsp; | &nbsp;", Label.CONTENT_RAW));
        if (itemController.hasAccess()) {
            final Button btnEditActualMetaData =
                new Button("edit", new EditMetaDataFileItemBehaviour(metadataRecord, mainWindow, repositories,
                    resourceProxy));
            btnEditActualMetaData.setStyleName(BaseTheme.BUTTON_LINK);
            btnEditActualMetaData.setDescription("Replace the metadata with a new content file");
            // btnEditActualMetaData.setIcon(new
            // ThemeResource("../myTheme/runo/icons/16/reload.png"));
            hl.addComponent(btnEditActualMetaData);
        }

        btnaddContainer.addComponent(hl);
    }

    /**
     * Used to bind new buttons on the view Usually when adding a new record
     * 
     * @param metadataRecord
     */
    public void addButtons(final MetadataRecord metadataRecord) {
        buildMDButtons(btnaddContainer, metadataRecord);
    }

}
