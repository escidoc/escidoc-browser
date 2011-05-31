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
 * Copyright ${year} Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextProxyImpl;
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

public class MetadataRecsContext {
    private int height;

    private final ContextProxyImpl resourceProxy;

    private final Window mainWindow;

    private final EscidocServiceLocation escidocServiceLocation;

    public MetadataRecsContext(ResourceProxy resourceProxy, int innerelementsHeight, Window mainWindow,
        EscidocServiceLocation escidocServiceLocation) {
        Preconditions.checkNotNull(mainWindow, "resource is null.");
        this.height = innerelementsHeight;
        if (this.height < 1)
            this.height = 400;
        this.height = innerelementsHeight;
        this.resourceProxy = (ContextProxyImpl) resourceProxy;
        this.mainWindow = mainWindow;
        this.escidocServiceLocation = escidocServiceLocation;
    }

    public Accordion asAccord() {
        Accordion metadataRecs = new Accordion();
        metadataRecs.setSizeFull();
        int elementHeight = this.height / 4;
        System.out.println(elementHeight);
        elementHeight = 410;
        // Add the components as tabs in the Accordion.
        metadataRecs.addTab(buildOrganizationUnit(elementHeight), "Organizational Unit", null);
        metadataRecs.addTab(buildAdminDescription(elementHeight), "Admin Description", null);
        metadataRecs.addTab(buildRelations(elementHeight), "Relations", null);
        metadataRecs.addTab(buildResources(elementHeight), "Resources", null);
        return metadataRecs;
    }

    /**
     * @param elementHeight
     * @return
     */
    private Panel buildResources(int elementHeight) {
        Panel resources = new Panel();
        resources.setWidth("100%");
        resources.setHeight(elementHeight + "px");
        Label lblresources = new Label("<a href='#'>Members Filtered</a><br />", Label.CONTENT_RAW);
        resources.addComponent(lblresources);
        return resources;
    }

    /**
     * @param elementHeight
     * @return
     */
    private Panel buildRelations(int elementHeight) {
        Panel relations = new Panel();
        relations.setWidth("100%");
        relations.setHeight(elementHeight + "px");
        Label lblrelations = new Label("isRelatedTo <a href='#'>Other Context</a><br />", Label.CONTENT_RAW);
        relations.addComponent(lblrelations);
        return relations;
    }

    /**
     * @param elementHeight
     * @return
     */
    private Panel buildAdminDescription(int elementHeight) {
        Panel admDescriptors = new Panel();
        admDescriptors.setWidth("100%");
        admDescriptors.setHeight(elementHeight + "px");

        AdminDescriptors admDesc = resourceProxy.getAdminDescription();
        String txt = "";
        for (AdminDescriptor adminDescriptor : admDesc) {
            Button fooBtn = new Button(adminDescriptor.getName());
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
    private Panel buildOrganizationUnit(int elementHeight) {
        Panel orgUnit = new Panel();
        orgUnit.setWidth("100%");
        orgUnit.setHeight(elementHeight + "px");

        OrganizationalUnitRefs orgUnits = resourceProxy.getOrganizationalUnit();
        for (OrganizationalUnitRef organizationalUnitRef : orgUnits) {

            Button fooBtn = new Button(organizationalUnitRef.getXLinkTitle());
            fooBtn.setStyleName(BaseTheme.BUTTON_LINK);
            fooBtn.addListener(new ContextAdminDescriptorsClickListener(organizationalUnitRef, mainWindow));
            orgUnit.addComponent(fooBtn);
        }
        return orgUnit;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
