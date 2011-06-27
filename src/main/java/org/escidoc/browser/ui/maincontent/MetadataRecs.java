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

import com.google.common.base.Preconditions;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.ui.listeners.CmDefBehaviourClickListener;
import org.escidoc.browser.ui.listeners.MetadataRecBehavour;
import org.escidoc.browser.ui.listeners.RelationsClickListener;
import org.escidoc.browser.ui.listeners.VersionHistoryClickListener;

import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;

public class MetadataRecs {
    private final int height;

    private final ContainerProxy resourceProxy;

    private final Window mainWindow;

    private final EscidocServiceLocation escidocServiceLocation;

    public MetadataRecs(final ResourceProxy resourceProxy, final int innerelementsHeight, final Window mainWindow,
        final EscidocServiceLocation escidocServiceLocation) {
        Preconditions.checkNotNull(mainWindow, "resource is null.");
        height = innerelementsHeight;
        this.resourceProxy = (ContainerProxy) resourceProxy;
        this.mainWindow = mainWindow;
        this.escidocServiceLocation = escidocServiceLocation;
    }

    public Accordion asAccord() {
        final Accordion metadataRecs = new Accordion();
        metadataRecs.setSizeFull();

        final Panel pnlmdRec = lblMetadaRecs();
        final Panel additionalResourcesPanel = lblAddtionalResources();

        // Add the components as tabs in the Accordion.
        metadataRecs.addTab(pnlmdRec, "Metadata Records", null);
        // metadataRecs.addTab(l2, "Relations", null);
        metadataRecs.addTab(additionalResourcesPanel, "Additional Resources", null);
        return metadataRecs;
    }

    private Panel lblAddtionalResources() {

        final Button btnVersionHistoryContainer =
            new Button("Container Version History", new VersionHistoryClickListener(resourceProxy, mainWindow,
                escidocServiceLocation));
        btnVersionHistoryContainer.setStyleName(BaseTheme.BUTTON_LINK);
        btnVersionHistoryContainer.setDescription("Show Version history in a Pop-up");

        final Button btnContentRelation =
            new Button("Container Content Relations", new RelationsClickListener(resourceProxy, mainWindow,
                escidocServiceLocation));
        btnContentRelation.setStyleName(BaseTheme.BUTTON_LINK);
        btnContentRelation.setDescription("Show Version history in a Pop-up");

        final Panel pnl = new Panel();
        pnl.setHeight(height + "px");
        pnl.addComponent(btnVersionHistoryContainer);
        pnl.addComponent(btnContentRelation);

        return pnl;
    }

    private Panel lblMetadaRecs() {
        final Panel pnl = new Panel();
        pnl.setHeight(height + "px");

        final MetadataRecords mdRecs = resourceProxy.getMedataRecords();
        for (final MetadataRecord metadataRecord : mdRecs) {
            final Button btnmdRec =
                new Button(metadataRecord.getName(), new MetadataRecBehavour(metadataRecord, mainWindow,
                    escidocServiceLocation));
            btnmdRec.setStyleName(BaseTheme.BUTTON_LINK);
            btnmdRec.setDescription("Show metadata information in a separate window");
            pnl.addComponent(btnmdRec);
        }

        return pnl;
    }
}