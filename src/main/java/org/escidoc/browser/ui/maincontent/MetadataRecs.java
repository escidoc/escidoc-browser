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

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.listeners.AddMetaDataFileContainerBehaviour;
import org.escidoc.browser.ui.listeners.EditMetaDataFileContainerBehaviour;
import org.escidoc.browser.ui.listeners.MetadataRecBehavour;
import org.escidoc.browser.ui.listeners.RelationsClickListener;
import org.escidoc.browser.ui.listeners.VersionHistoryClickListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;

public class MetadataRecs {

    static final Logger LOG = LoggerFactory.getLogger(BrowserApplication.class);

    private final int height;

    private final ContainerProxy resourceProxy;

    private final Window mainWindow;

    private final EscidocServiceLocation escidocServiceLocation;

    private Repositories repositories;

    private Accordion metadataRecs;

    private Panel pnlmdRec;

    final Panel pnl = new Panel();

    VerticalLayout btnaddContainer = new VerticalLayout();

    private final CurrentUser currentUser;

    private MainSite mainSite;

    public MetadataRecs(final ResourceProxy resourceProxy, final int innerelementsHeight, final Window mainWindow,
        final EscidocServiceLocation escidocServiceLocation, final Repositories repositories, CurrentUser currentUser,
        MainSite mainSite) {

        Preconditions.checkNotNull(mainWindow, "resource is null.");
        Preconditions.checkNotNull(escidocServiceLocation, "escidocServiceLocation is null.");
        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null.");

        height = innerelementsHeight;
        this.resourceProxy = (ContainerProxy) resourceProxy;
        this.mainWindow = mainWindow;
        this.escidocServiceLocation = escidocServiceLocation;
        this.repositories = repositories;
        this.currentUser = currentUser;
        this.repositories = repositories;
        this.mainSite = mainSite;
    }

    private boolean hasAccess() {
        try {
            return repositories
                .pdp().forUser(currentUser.getUserId()).isAction(ActionIdConstants.UPDATE_CONTAINER)
                .forResource(resourceProxy.getId()).permitted();
        }
        catch (EscidocClientException e) {
            LOG.debug(e.getLocalizedMessage());
            return false;
        }
        catch (URISyntaxException e) {
            LOG.debug(e.getLocalizedMessage());
            return false;
        }
    }

    public Accordion asAccord() {
        metadataRecs = new Accordion();
        metadataRecs.setSizeFull();

        pnlmdRec = lblMetadaRecs();
        final Panel additionalResourcesPanel = lblAddtionalResources();

        // Add the components as tabs in the Accordion.
        metadataRecs.addTab(pnlmdRec, "eSciDoc Metadata", null);
        // metadataRecs.addTab(l2, "Relations", null);
        metadataRecs.addTab(additionalResourcesPanel, "Additional Resources", null);
        return metadataRecs;
    }

    private Panel lblAddtionalResources() {

        final Button btnVersionHistoryContainer =
            new Button("Container Version History", new VersionHistoryClickListener(resourceProxy, mainWindow,
                escidocServiceLocation, repositories));
        btnVersionHistoryContainer.setStyleName(BaseTheme.BUTTON_LINK);
        btnVersionHistoryContainer.setDescription("Show Version history in a Pop-up");

        final Button btnContentRelation =
            new Button("Container Content Relations", new RelationsClickListener(resourceProxy, mainWindow,
                escidocServiceLocation, repositories, currentUser, mainSite));
        btnContentRelation.setStyleName(BaseTheme.BUTTON_LINK);
        btnContentRelation.setDescription("Show Version history in a Pop-up");

        final Panel pnl = new Panel();
        pnl.setHeight(height + "px");
        pnl.addComponent(btnVersionHistoryContainer);
        pnl.addComponent(btnContentRelation);
        return pnl;
    }

    private Panel lblMetadaRecs() {
        pnl.setHeight(height + "px");

        final MetadataRecords mdRecs = resourceProxy.getMedataRecords();
        for (final MetadataRecord metadataRecord : mdRecs) {
            buildMDButtons(btnaddContainer, metadataRecord);
        }

        pnl.addComponent(btnaddContainer);
        if (hasAccess()) {
            Button btnAddNew =
                new Button("Add New MetaData", new AddMetaDataFileContainerBehaviour(mainWindow,
                    escidocServiceLocation, repositories, resourceProxy, this));
            btnAddNew.setStyleName(BaseTheme.BUTTON_LINK);
            btnAddNew.setIcon(new ThemeResource("../myTheme/runo/icons/16/note.png"));
            pnl.addComponent(btnAddNew);
        }
        return pnl;
    }

    /**
     * Create the buttons to be shown on the MetaDataRecords Accordion
     * 
     * @param pnl
     * @param metadataRecord
     */
    public void buildMDButtons(final VerticalLayout btnaddContainer, final MetadataRecord metadataRecord) {
        HorizontalLayout hl = new HorizontalLayout();
        if (hasAccess()) {
            Button btnEditActualMetaData =
                new Button("", new EditMetaDataFileContainerBehaviour(metadataRecord, mainWindow,
                    escidocServiceLocation, repositories, resourceProxy));
            btnEditActualMetaData.setStyleName(BaseTheme.BUTTON_LINK);
            btnEditActualMetaData.setDescription("Replace the metadata with a new content file");
            btnEditActualMetaData.setIcon(new ThemeResource("../myTheme/runo/icons/16/reload.png"));
            hl.addComponent(btnEditActualMetaData);
        }

        final Button btnmdRec =
            new Button(metadataRecord.getName(), new MetadataRecBehavour(metadataRecord, mainWindow,
                escidocServiceLocation));
        btnmdRec.setStyleName(BaseTheme.BUTTON_LINK);
        btnmdRec.setDescription("Show metadata information in a separate window");
        hl.addComponent(btnmdRec);

        btnaddContainer.addComponent(hl);
    }

    /**
     * Used to bind new buttons on the view Usually when adding a new record
     * 
     * @param metadataRecord
     */
    public void addButtons(MetadataRecord metadataRecord) {
        buildMDButtons(btnaddContainer, metadataRecord);
    }
}