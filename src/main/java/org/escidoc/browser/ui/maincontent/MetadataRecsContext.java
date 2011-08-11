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

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.ui.listeners.ContextAdminDescriptorsClickListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.om.context.AdminDescriptor;
import de.escidoc.core.resources.om.context.AdminDescriptors;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;

public class MetadataRecsContext {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataRecsContext.class);

    private int height;

    private final ContextProxyImpl resourceProxy;

    private final Window mainWindow;

    private final EscidocServiceLocation escidocServiceLocation;

    public MetadataRecsContext(final ResourceProxy resourceProxy, final int innerelementsHeight,
        final Window mainWindow, final EscidocServiceLocation escidocServiceLocation) {
        Preconditions.checkNotNull(mainWindow, "resource is null.");
        height = innerelementsHeight;
        if (height < 1) {
            height = 400;
        }
        height = innerelementsHeight;
        this.resourceProxy = (ContextProxyImpl) resourceProxy;
        this.mainWindow = mainWindow;
        this.escidocServiceLocation = escidocServiceLocation;
    }

    public Accordion asAccord() {
        final Accordion metadataRecs = new Accordion();
        metadataRecs.setSizeFull();
        int elementHeight = height / 4;
        elementHeight = 410;
        addComponentAsTabs(metadataRecs, elementHeight);
        return metadataRecs;
    }

    private void addComponentAsTabs(final Accordion metadataRecs, final int elementHeight) {
        metadataRecs.addTab(buildOrganizationUnit(elementHeight), "Organizational Unit", null);
        metadataRecs.addTab(buildAdminDescription(elementHeight), "Admin Description", null);
        metadataRecs.addTab(buildRelations(elementHeight), "Relations", null);
        metadataRecs.addTab(buildResources(elementHeight), "Resources", null);
    }

    /**
     * @param elementHeight
     * @return
     */
    private Panel buildResources(final int elementHeight) {
        final Panel resources = new Panel();
        resources.setWidth("100%");
        resources.setHeight(elementHeight + "px");
        final Label lblresources = new Label("<a href='#'>Members Filtered</a><br />", Label.CONTENT_RAW);
        resources.addComponent(lblresources);
        return resources;
    }

    /**
     * @param elementHeight
     * @return
     */
    private Panel buildRelations(final int elementHeight) {
        final Panel relations = new Panel();
        relations.setWidth("100%");
        relations.setHeight(elementHeight + "px");
        final Label lblrelations = new Label("isRelatedTo <a href='#'>Other Context</a><br />", Label.CONTENT_RAW);
        relations.addComponent(lblrelations);
        return relations;
    }

    /**
     * @param elementHeight
     * @return
     */
    private Panel buildAdminDescription(final int elementHeight) {
        final Panel admDescriptors = new Panel();
        admDescriptors.setWidth("100%");
        admDescriptors.setHeight(elementHeight + "px");

        final AdminDescriptors admDesc = resourceProxy.getAdminDescription();
        final String txt = "";
        for (final AdminDescriptor adminDescriptor : admDesc) {
            final Button fooBtn = new Button(adminDescriptor.getName());
            fooBtn.setStyleName(BaseTheme.BUTTON_LINK);
            fooBtn.addListener(new ContextAdminDescriptorsClickListener(adminDescriptor, mainWindow));
            admDescriptors.addComponent(fooBtn);
        }

        // Label lblAdmDescriptor =
        // new Label(txt + "<a href='/NotImplementedYet'>"
        // + admDesc.getXLinkTitle() + "</a><br />", Label.CONTENT_RAW);
        // admDescriptors.addComponent(lblAdmDescriptor);
        return admDescriptors;
    }

    /**
     * @param elementHeight
     * @return
     */
    private Panel buildOrganizationUnit(final int elementHeight) {
        final Panel orgUnit = new Panel();
        orgUnit.setWidth("100%");
        orgUnit.setHeight(elementHeight + "px");

        final OrganizationalUnitRefs orgUnits = resourceProxy.getOrganizationalUnit();
        for (final OrganizationalUnitRef organizationalUnitRef : orgUnits) {

            final Button fooBtn = new Button(organizationalUnitRef.getXLinkTitle());
            fooBtn.setStyleName(BaseTheme.BUTTON_LINK);
            fooBtn.addListener(new ContextAdminDescriptorsClickListener(organizationalUnitRef, mainWindow));
            orgUnit.addComponent(fooBtn);
        }
        return orgUnit;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(final int height) {
        this.height = height;
    }
}
