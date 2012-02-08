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
package org.escidoc.browser.ui.maincontent;

import java.net.URISyntaxException;

import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.EditMetaDataFileContainerBehaviour;
import org.escidoc.browser.ui.listeners.OnAddContainerMetadata;
import org.escidoc.browser.ui.listeners.VersionHistoryClickListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;

public class ContainerMetadataRecordsView {

    private static final Logger LOG = LoggerFactory.getLogger(ContainerMetadataRecordsView.class);

    private final Panel panel = new Panel();

    private final ContainerProxy resourceProxy;

    private final Window mainWindow;

    private final EscidocServiceLocation escidocServiceLocation;

    private final Repositories repositories;

    private final Router router;

    private VerticalLayout btnaddContainer = new VerticalLayout();

    private Accordion metadataRecs;

    private Panel pnlmdRec;

    private ContainerView containerView;

    public ContainerMetadataRecordsView(final ResourceProxy resourceProxy, final Repositories repositories,
        final Router router, ContainerView containerView) {

        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(router, "mainSite is null: %s", router);
        Preconditions.checkNotNull(containerView, "containerView is null: %s", containerView);

        this.router = router;
        this.resourceProxy = (ContainerProxy) resourceProxy;
        this.mainWindow = router.getMainWindow();
        this.escidocServiceLocation = router.getServiceLocation();
        this.repositories = repositories;
        this.containerView = containerView;

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

    private Panel lblAddtionalResources() {
        final Panel pnl = new Panel();
        pnl.setSizeFull();
        VerticalLayout hl = new VerticalLayout();
        hl.setSizeFull();
        final Button btnVersionHistoryContainer =
            new Button("Container Version History", new VersionHistoryClickListener(resourceProxy, mainWindow,
                escidocServiceLocation, repositories));
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
                    public void buttonClick(ClickEvent event) {
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
        // final Button btnContentRelation =
        // new Button("Container Content Relations", new RelationsClickListener(resourceProxy, mainWindow,
        // escidocServiceLocation, repositories, router));
        // btnContentRelation.setStyleName(BaseTheme.BUTTON_LINK);
        // btnContentRelation.setDescription("Show Version history in a Pop-up");

        hl.addComponent(btnVersionHistoryContainer);
        pnl.setContent(hl);
        // pnl.addComponent(btnContentRelation);
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

    private Panel lblMetadaRecs() {
        panel.setSizeFull();
        VerticalLayout hl = new VerticalLayout();
        hl.setSizeFull();
        final CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight("20px");
        buildPanelHeader(cssLayout, ViewConstants.METADATA);
        ThemeResource ICON = new ThemeResource("images/assets/plus.png");

        if (hasAccess()) {
            final Button btnAddNew = new Button();
            btnAddNew.addListener(new OnAddContainerMetadata(mainWindow, repositories, resourceProxy, this));
            btnAddNew.setStyleName(BaseTheme.BUTTON_LINK);
            btnAddNew.addStyleName("floatright paddingtop3");
            btnAddNew.setWidth("20px");
            btnAddNew.setIcon(ICON);
            cssLayout.addComponent(btnAddNew);
        }
        hl.addComponent(cssLayout);
        final MetadataRecords mdRecs = resourceProxy.getMedataRecords();
        for (final MetadataRecord metadataRecord : mdRecs) {
            buildMDButtons(btnaddContainer, metadataRecord);
        }
        hl.addComponent(new Label("&nbsp;", Label.CONTENT_RAW));
        hl.addComponent(btnaddContainer);
        hl.setComponentAlignment(btnaddContainer, Alignment.TOP_LEFT);
        hl.setExpandRatio(btnaddContainer, 0.9f);
        panel.setContent(hl);

        return panel;
    }

    /**
     * Create the buttons to be shown on the MetaDataRecords Accordion
     * 
     * @param panel
     * @param metadataRecord
     */
    public void buildMDButtons(final VerticalLayout btnaddContainer, final MetadataRecord metadataRecord) {
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

        if (hasAccess()) {
            final Button btnEditActualMetaData =
                new Button("edit", new EditMetaDataFileContainerBehaviour(metadataRecord, router, repositories,
                    resourceProxy, containerView));
            btnEditActualMetaData.setStyleName(BaseTheme.BUTTON_LINK);
            btnEditActualMetaData.setDescription("Replace the metadata with a new content file");
            // btnEditActualMetaData.setIcon(new ThemeResource("../myTheme/runo/icons/16/reload.png"));
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

    private boolean hasAccess() {
        try {
            return repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_CONTAINER).forResource(resourceProxy.getId())
                .permitted();
        }
        catch (final EscidocClientException e) {
            LOG.debug(e.getLocalizedMessage());
            return false;
        }
        catch (final URISyntaxException e) {
            LOG.debug(e.getLocalizedMessage());
            return false;
        }
    }
}