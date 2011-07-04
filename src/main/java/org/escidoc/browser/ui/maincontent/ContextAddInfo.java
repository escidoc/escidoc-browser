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

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.ui.listeners.ContextAdminDescriptorsClickListener;

import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.om.context.AdminDescriptor;
import de.escidoc.core.resources.om.context.AdminDescriptors;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;

/**
 * Deprecated the MetadataRecsContext is used instead
 * 
 * @author ajb
 * 
 */
public class ContextAddInfo {
    private int height;

    private final ContextProxyImpl resourceProxy;

    private final Window mainWindow;

    public ContextAddInfo(ResourceProxy resourceProxy, int innerelementsHeight, Window mainWindow) {
        this.height = innerelementsHeight;
        this.resourceProxy = (ContextProxyImpl) resourceProxy;
        this.mainWindow = mainWindow;
    }

    public Panel addPanels() {

        final Panel mainpnl = new Panel();
        mainpnl.setHeight("100%");
        mainpnl.setWidth("100%");

        final int elementHeight = this.height / 4;

        mainpnl.addComponent(buildOrganizationUnit(elementHeight));
        mainpnl.addComponent(buildAdminDescription(elementHeight));
        mainpnl.addComponent(buildRelations(elementHeight));
        mainpnl.addComponent(buildResources(elementHeight));

        return mainpnl;

    }

    /**
     * @param elementHeight
     * @return
     */
    private static Panel buildResources(int elementHeight) {
        final Panel resources = new Panel("Resources");
        resources.setWidth("100%");
        resources.setHeight(elementHeight + "px");
        final Label lblresources =
            new Label("<a href='/ESCD/#Resources/id'>Members Filtered</a><br />", Label.CONTENT_RAW);
        resources.addComponent(lblresources);
        return resources;
    }

    /**
     * @param elementHeight
     * @return
     */
    private static Panel buildRelations(int elementHeight) {
        final Panel relations = new Panel("Relations");
        relations.setWidth("100%");
        relations.setHeight(elementHeight + "px");
        final Label lblrelations =
            new Label("isRelatedTo <a href='/ESCD/#Context/123'>Other Context</a><br />", Label.CONTENT_RAW);
        relations.addComponent(lblrelations);
        return relations;
    }

    /**
     * @param elementHeight
     * @return
     */
    private Panel buildAdminDescription(int elementHeight) {
        final Panel admDescriptors = new Panel("Admin Descriptors");
        admDescriptors.setWidth("100%");
        admDescriptors.setHeight(elementHeight + "px");

        final AdminDescriptors admDesc = resourceProxy.getAdminDescription();
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
    private Panel buildOrganizationUnit(int elementHeight) {
        final Panel orgUnit = new Panel("Organizational Unit");
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

    public void setHeight(int height) {
        this.height = height;
    }

}
