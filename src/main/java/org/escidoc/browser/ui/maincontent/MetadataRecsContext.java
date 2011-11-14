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

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.ContextAdminDescriptorsClickListener;

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

class MetadataRecsContext {

    private final ContextProxyImpl resourceProxy;

    private final Window mainWindow;

    MetadataRecsContext(final ResourceProxy resourceProxy, final Window mainWindow) {
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(mainWindow, "resource is null.");

        this.resourceProxy = (ContextProxyImpl) resourceProxy;
        this.mainWindow = mainWindow;
    }

    public Accordion asAccord() {
        final Accordion metadataRecs = new Accordion();
        metadataRecs.setSizeFull();
        addComponentAsTabs(metadataRecs);
        return metadataRecs;
    }

    private void addComponentAsTabs(final Accordion metadataRecs) {
        metadataRecs.addTab(buildOrganizationUnit(), ViewConstants.ORGANIZATIONAL_UNIT, null);
        metadataRecs.addTab(buildAdminDescription(), ViewConstants.ADMIN_DESCRIPTION, null);
        metadataRecs.addTab(buildRelations(), ViewConstants.RELATIONS, null);
        metadataRecs.addTab(buildResources(), ViewConstants.ADDITIONAL_RESOURCES, null);
    }

    private Panel buildResources() {
        final Panel resources = new Panel();
        resources.setWidth("100%");
        resources.setHeight("100%");
        final Label lblresources = new Label("<a href='#'>Members Filtered</a><br />", Label.CONTENT_RAW);
        resources.addComponent(lblresources);
        return resources;
    }

    private Panel buildRelations() {
        final Panel relations = new Panel();
        relations.setWidth("100%");
        relations.setHeight("100%");
        final Label lblrelations = new Label("isRelatedTo <a href='#'>Other Context</a><br />", Label.CONTENT_RAW);
        relations.addComponent(lblrelations);
        return relations;
    }

    private Panel buildAdminDescription() {
        final Panel admDescriptors = new Panel();
        admDescriptors.setWidth("100%");
        admDescriptors.setHeight("100%");

        final AdminDescriptors admDesc = resourceProxy.getAdminDescription();
        for (final AdminDescriptor adminDescriptor : admDesc) {
            final Button button = new Button(adminDescriptor.getName());
            button.setStyleName(BaseTheme.BUTTON_LINK);
            button.addListener(new ContextAdminDescriptorsClickListener(adminDescriptor, mainWindow));
            admDescriptors.addComponent(button);
        }
        return admDescriptors;
    }

    private Panel buildOrganizationUnit() {
        final Panel orgUnit = new Panel();
        orgUnit.setWidth("100%");
        orgUnit.setHeight("100%");

        final OrganizationalUnitRefs orgUnits = resourceProxy.getOrganizationalUnit();
        for (final OrganizationalUnitRef organizationalUnitRef : orgUnits) {
            final Button button = new Button(organizationalUnitRef.getXLinkTitle());
            button.setStyleName(BaseTheme.BUTTON_LINK);
            button.addListener(new ContextAdminDescriptorsClickListener(organizationalUnitRef, mainWindow));
            orgUnit.addComponent(button);
        }
        return orgUnit;
    }

}