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

import org.escidoc.browser.controller.ContextController;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.AddOrgUnitstoContext;
import org.escidoc.browser.ui.listeners.ContextAdminDescriptorsClickListener;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

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

    public Panel asPanel() {
        final Panel pnlmetadataRecs = new Panel();
        pnlmetadataRecs.setSizeFull();
        VerticalLayout vl = new VerticalLayout();
        vl.setImmediate(false);
        vl.setWidth("100.0%");
        vl.setHeight("100.0%");
        vl.setMargin(false);
        vl.addComponent(buildOrganizationUnit());
        vl.addComponent(buildAdminDescription());

        pnlmetadataRecs.setContent(vl);
        return pnlmetadataRecs;
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

    private void addComponentAsTabs(final Accordion metadataRecs) {
        metadataRecs.addTab(buildOrganizationUnit(), ViewConstants.ORGANIZATIONAL_UNIT, null);
        metadataRecs.addTab(buildAdminDescription(), ViewConstants.ADMIN_DESCRIPTION, null);
    }

    private Panel buildAdminDescription() {
        final Panel admDescriptors = new Panel();
        admDescriptors.setWidth("100%");
        admDescriptors.setHeight("100%");
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        final CssLayout cssLayout = new CssLayout();
        buildPanelHeader(cssLayout, ViewConstants.ADMIN_DESCRIPTION);
        ThemeResource ICON = new ThemeResource("images/assets/plus.png");
        if (contextController.canAddOUs()) {
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
        }
        vl.addComponent(cssLayout);

        VerticalLayout vl2 = new VerticalLayout();
        final AdminDescriptors admDesc = resourceProxy.getAdminDescription();
        for (final AdminDescriptor adminDescriptor : admDesc) {

            Link admDescBtn =
                new Link(adminDescriptor.getXLinkTitle(), new ExternalResource(router
                    .getServiceLocation().getEscidocUri() + adminDescriptor.getXLinkHref()));
            admDescBtn.setTargetName("_blank");
            admDescBtn.setStyleName(BaseTheme.BUTTON_LINK);
            admDescBtn.setDescription("Show metadata information in a separate window");
            vl2.addComponent(admDescBtn);
        }
        vl.addComponent(vl2);
        vl.setExpandRatio(vl2, 9);
        admDescriptors.setContent(vl);
        return admDescriptors;
    }

    private Panel buildOrganizationUnit() {
        final Panel pnlOrgUnit = new Panel();
        pnlOrgUnit.setSizeFull();
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        final OrganizationalUnitRefs orgUnits = resourceProxy.getOrganizationalUnit();

        final CssLayout cssLayout = new CssLayout();
        buildPanelHeader(cssLayout, ViewConstants.ORGANIZATIONAL_UNIT);
        ThemeResource ICON = new ThemeResource("images/assets/plus.png");
        if (contextController.canAddOUs()) {
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
            cssLayout.addComponent(addResourceButton);
        }
        vl.addComponent(cssLayout);
        VerticalLayout vl2 = new VerticalLayout();
        for (final OrganizationalUnitRef organizationalUnitRef : orgUnits) {
            final Button button = new Button(organizationalUnitRef.getXLinkTitle());
            button.setStyleName(BaseTheme.BUTTON_LINK);
            button.addListener(new ContextAdminDescriptorsClickListener(organizationalUnitRef, mainWindow));
            vl2.addComponent(button);
            vl2.setComponentAlignment(button, Alignment.TOP_LEFT);
        }
        vl.addComponent(vl2);
        vl.setComponentAlignment(vl2, Alignment.TOP_LEFT);
        vl.setExpandRatio(vl2, 9);
        pnlOrgUnit.setContent(vl);
        return pnlOrgUnit;
    }
}