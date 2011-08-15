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
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.listeners.AddMetaDataFileItemBehaviour;
import org.escidoc.browser.ui.listeners.EditMetaDataFileItemBehaviour;
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

public class MetadataRecsItem {

    private static final Logger LOG = LoggerFactory.getLogger(BrowserApplication.class);

    private final int height;

    private final ItemProxy resourceProxy;

    private final Window mainWindow;

    private final EscidocServiceLocation escidocServiceLocation;

    private final Repositories repositories;

    private Accordion metadataRecs;

    private Panel pnlmdRec;

    final Panel pnl = new Panel();

    VerticalLayout btnaddContainer = new VerticalLayout();

    private final CurrentUser currentUser;

    private final MainSite mainSite;

    MetadataRecsItem(final ItemProxy resourceProxy, final int innerelementsHeight, final Window mainWindow,
        final EscidocServiceLocation escidocServiceLocation, final Repositories repositories,
        final CurrentUser currentUser, final MainSite mainSite) {
        Preconditions.checkNotNull(mainWindow, "resource is null.");
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions
            .checkNotNull(escidocServiceLocation, "escidocServiceLocation is null: %s", escidocServiceLocation);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);

        height = innerelementsHeight;
        this.resourceProxy = resourceProxy;
        this.mainWindow = mainWindow;
        this.escidocServiceLocation = escidocServiceLocation;
        this.repositories = repositories;
        this.currentUser = currentUser;
        this.mainSite = mainSite;
    }

    protected Accordion asAccord() {
        final Accordion metadataRecs = new Accordion();
        metadataRecs.setSizeFull();

        // TODO use meaningful variable, class, method name
        final Panel pnlMetadataRecs = lblMetadaRecs();
        final Panel pnlAdditionalResources = lblAddtionalResources();

        // Add the components as tabs in the Accordion.
        metadataRecs.addTab(pnlMetadataRecs, "eSciDoc Metadata", null);
        // metadataRecs.addTab(l2, "Relations", null);
        metadataRecs.addTab(pnlAdditionalResources, "Additional Resources", null);
        return metadataRecs;
    }

    private Panel lblAddtionalResources() {

        final Button btnVersionHistory =
            new Button("Item Version History", new VersionHistoryClickListener(resourceProxy, mainWindow,
                escidocServiceLocation, repositories));
        btnVersionHistory.setStyleName(BaseTheme.BUTTON_LINK);
        btnVersionHistory.setDescription("Show Version history in a Pop-up");

        final Button btnContentRelation =
            new Button("Item Content Relations", new RelationsClickListener(resourceProxy, mainWindow,
                escidocServiceLocation, repositories, mainSite, currentUser));
        btnContentRelation.setStyleName(BaseTheme.BUTTON_LINK);
        btnContentRelation.setDescription("Show Version history in a Pop-up");

        final Panel pnl = new Panel();
        pnl.setHeight(height + "px");
        pnl.addComponent(btnVersionHistory);
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
            final Button btnAddNew =
                new Button("Add New MetaData", new AddMetaDataFileItemBehaviour(mainWindow, repositories,
                    resourceProxy, this));
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
    private void buildMDButtons(final VerticalLayout btnaddContainer, final MetadataRecord metadataRecord) {
        final HorizontalLayout hl = new HorizontalLayout();
        if (hasAccess()) {
            final Button btnEditActualMetaData =
                new Button("", new EditMetaDataFileItemBehaviour(metadataRecord, mainWindow, repositories,
                    resourceProxy));
            btnEditActualMetaData.setStyleName(BaseTheme.BUTTON_LINK);
            btnEditActualMetaData.setDescription("Replace the metadata with a new content file");
            btnEditActualMetaData.setIcon(new ThemeResource("../myTheme/runo/icons/16/reload.png"));
            hl.addComponent(btnEditActualMetaData);
        }

        final Button btnmdRec =
            new Button(metadataRecord.getName(), new MetadataRecBehavour(metadataRecord, mainWindow));
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
    public void addButtons(final MetadataRecord metadataRecord) {
        buildMDButtons(btnaddContainer, metadataRecord);
    }

    private boolean hasAccess() {
        try {
            return repositories
                .pdp().forUser(currentUser.getUserId()).isAction(ActionIdConstants.UPDATE_ITEM)
                .forResource(resourceProxy.getId()).permitted();
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
