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

import java.util.Iterator;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.listeners.MetadataRecBehavour;
import org.escidoc.browser.ui.listeners.RelationsClickListener;
import org.escidoc.browser.ui.listeners.VersionHistoryClickListener;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;

public class MetadataRecsItem {
    private int height;

    private final ItemProxy resourceProxy;

    private final Window mainWindow;

    private final EscidocServiceLocation escidocServiceLocation;

    private final Repositories repositories;

    public MetadataRecsItem(final ItemProxy resourceProxy, final int innerelementsHeight, final Window mainWindow,
        final EscidocServiceLocation escidocServiceLocation, final Repositories repositories) {
        Preconditions.checkNotNull(mainWindow, "resource is null.");
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions
            .checkNotNull(escidocServiceLocation, "escidocServiceLocation is null: %s", escidocServiceLocation);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);

        // TODO: what the value of height?
        // TODO: User Camel Case for variable, method and class name
        height = innerelementsHeight;

        if (height < 1) {
            height = 400;
        }

        this.resourceProxy = resourceProxy;
        this.mainWindow = mainWindow;
        this.escidocServiceLocation = escidocServiceLocation;
        this.repositories = repositories;
    }

    public Accordion asAccord() {
        final Accordion metadataRecs = new Accordion();
        metadataRecs.setSizeFull();

        // TODO use meaningful variable, class, method name
        final Panel l1 = lblMetadaRecs();
        final Panel pnl = lblAddtionalResources();

        // Add the components as tabs in the Accordion.
        metadataRecs.addTab(l1, "Metadata Records", null);
        // metadataRecs.addTab(l2, "Relations", null);
        metadataRecs.addTab(pnl, "Additional Resources", null);
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
                escidocServiceLocation, repositories));
        btnContentRelation.setStyleName(BaseTheme.BUTTON_LINK);
        btnContentRelation.setDescription("Show Version history in a Pop-up");

        final Panel pnl = new Panel();
        pnl.setHeight(height + "px");
        pnl.addComponent(btnVersionHistory);
        pnl.addComponent(btnContentRelation);
        return pnl;
    }

    private Label lblRelations() {
        final Iterator itr = resourceProxy.getRelations().iterator();
        String relRecords = "";
        final StringBuffer buf = new StringBuffer();
        while (itr.hasNext()) {
            buf.append("<a href='#'>" + itr.next() + "</a><br />");
        }
        relRecords = buf.toString();
        final Label l2 = new Label(relRecords, Label.CONTENT_RAW);
        l2.setHeight(height + "px");
        return l2;
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
