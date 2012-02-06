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

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import org.escidoc.browser.controller.ContextController;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.AddOrgUnitstoContext;
import org.escidoc.browser.ui.listeners.ContextAdminDescriptorsClickListener;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.om.context.AdminDescriptor;
import de.escidoc.core.resources.om.context.AdminDescriptors;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;

class MetadataRecsContext {

    private final ContextProxyImpl resourceProxy;

    private final Window mainWindow;

    private Router router;

    private ContextController contextController;

    MetadataRecsContext(final ResourceProxy resourceProxy, Router router, ContextController contextController) {
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(router, "resource is null.");

        this.resourceProxy = (ContextProxyImpl) resourceProxy;
        this.mainWindow = router.getMainWindow();
        this.router = router;
        this.contextController = contextController;
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
    }

    private Panel buildAdminDescription() {
        final Panel admDescriptors = new Panel();
        admDescriptors.setWidth("100%");
        admDescriptors.setHeight("100%");
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();

        final AdminDescriptors admDesc = resourceProxy.getAdminDescription();
        for (final AdminDescriptor adminDescriptor : admDesc) {

            Link admDescBtn =
                new Link(adminDescriptor.getXLinkTitle(), new ExternalResource(router
                    .getServiceLocation().getEscidocUri() + adminDescriptor.getXLinkHref()));
            admDescBtn.setTargetName("_blank");
            admDescBtn.setStyleName(BaseTheme.BUTTON_LINK);
            admDescBtn.setDescription("Show metadata information in a separate window");
            hl.addComponent(admDescBtn);
        }
        admDescriptors.setContent(hl);
        return admDescriptors;
    }

    private Panel buildOrganizationUnit() {
        final Panel pnlOrgUnit = new Panel();
        pnlOrgUnit.setWidth("100%");
        pnlOrgUnit.setHeight("100%");

        final OrganizationalUnitRefs orgUnits = resourceProxy.getOrganizationalUnit();
        for (final OrganizationalUnitRef organizationalUnitRef : orgUnits) {
            final Button button = new Button(organizationalUnitRef.getXLinkTitle());
            button.setStyleName(BaseTheme.BUTTON_LINK);
            button.addListener(new ContextAdminDescriptorsClickListener(organizationalUnitRef, mainWindow));
            pnlOrgUnit.addComponent(button);
        }

        if (contextController.canAddOUs()) {
            Button btnAdd = new Button("+/-");
            btnAdd.setStyleName(BaseTheme.BUTTON_LINK);
            btnAdd.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    final Window subwindow = new Window("A modal subwindow");
                    subwindow.setModal(true);
                    subwindow.setWidth("650px");
                    VerticalLayout layout = (VerticalLayout) subwindow.getContent();
                    layout.setMargin(true);
                    layout.setSpacing(true);

                    try {
                        subwindow.addComponent(new AddOrgUnitstoContext(router, resourceProxy, contextController,
                            orgUnits));
                    }
                    catch (EscidocClientException e) {

                        e.printStackTrace();
                    }
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
            pnlOrgUnit.addComponent(btnAdd);
        }
        return pnlOrgUnit;
    }

}