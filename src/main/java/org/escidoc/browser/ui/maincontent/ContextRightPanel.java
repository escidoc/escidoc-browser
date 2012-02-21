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
import org.escidoc.browser.ui.listeners.AdminDescriptorFormListener;
import org.escidoc.browser.ui.view.helpers.AdminDescriptorsTableVH;
import org.escidoc.browser.ui.view.helpers.OrganizationalUnitsTableVH;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.om.context.AdminDescriptors;

class ContextRightPanel {

    private final ContextProxyImpl resourceProxy;

    private Router router;

    private ContextController contextController;

    ContextRightPanel(final ResourceProxy resourceProxy, Router router, ContextController contextController) {
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(router, "resource is null.");

        this.resourceProxy = (ContextProxyImpl) resourceProxy;
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

    private static void buildPanelHeader(CssLayout cssLayout, String name) {
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

    @SuppressWarnings("serial")
    private Panel buildAdminDescription() {
        final Panel admDescriptors = new Panel();
        admDescriptors.setWidth("100%");
        admDescriptors.setHeight("100%");
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        final CssLayout cssLayout = new CssLayout();
        buildPanelHeader(cssLayout, ViewConstants.ADMIN_DESCRIPTION);
        ThemeResource ICON = new ThemeResource("images/assets/plus.png");
        if (contextController.canUpdateContext()) {
            final Button addResourceButton = new Button();
            addResourceButton.setStyleName(BaseTheme.BUTTON_LINK);
            addResourceButton.addStyleName("floatright paddingtop3");
            addResourceButton.setWidth("20px");
            addResourceButton.setIcon(ICON);
            addResourceButton.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    new AdminDescriptorFormListener(router, contextController).adminDescriptorForm();
                }
            });
            cssLayout.addComponent(addResourceButton);
        }
        vl.addComponent(cssLayout);

        VerticalLayout vl2 = new VerticalLayout();
        final AdminDescriptors admDesc = resourceProxy.getAdminDescription();
        final AdminDescriptorsTableVH adminDescriptorTable =
            new AdminDescriptorsTableVH(contextController, admDesc, router);
        vl2.addComponent(adminDescriptorTable);
        vl.addComponent(vl2);
        vl.setExpandRatio(vl2, 9);
        admDescriptors.setContent(vl);
        return admDescriptors;
    }

    @SuppressWarnings("serial")
    private Panel buildOrganizationUnit() {
        final Panel pnlOrgUnit = new Panel();
        pnlOrgUnit.setSizeFull();
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();

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
                public void buttonClick(@SuppressWarnings("unused")
                final ClickEvent event) {
                    final Window subwindow = new Window("A modal subwindow");
                    subwindow.setModal(true);
                    subwindow.setWidth("650px");
                    VerticalLayout layout = (VerticalLayout) subwindow.getContent();
                    layout.setMargin(true);
                    layout.setSpacing(true);

                    try {
                        subwindow.addComponent(new AddOrgUnitstoContext(router, resourceProxy, contextController,
                            resourceProxy.getOrganizationalUnit()));
                    }
                    catch (EscidocClientException e) {
                        contextController.showError(e);
                    }
                    Button close = new Button(ViewConstants.CLOSE, new Button.ClickListener() {
                        @Override
                        public void buttonClick(@SuppressWarnings("unused")
                        ClickEvent event) {
                            subwindow.getParent().removeWindow(subwindow);
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

        OrganizationalUnitsTableVH orgUnitTable =
            new OrganizationalUnitsTableVH(contextController, resourceProxy.getOrganizationalUnit(), router,
                resourceProxy);
        vl.addComponent(orgUnitTable);
        vl.setComponentAlignment(orgUnitTable, Alignment.TOP_LEFT);
        vl.setExpandRatio(orgUnitTable, 9f);

        pnlOrgUnit.setContent(vl);
        return pnlOrgUnit;
    }
}