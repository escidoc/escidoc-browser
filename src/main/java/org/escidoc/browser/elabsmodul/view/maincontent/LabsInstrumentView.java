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
package org.escidoc.browser.elabsmodul.view.maincontent;

import org.escidoc.browser.elabsmodul.controller.InstrumentController;
import org.escidoc.browser.elabsmodul.exceptions.EscidocBrowserException;
import org.escidoc.browser.elabsmodul.model.InstrumentBean;
import org.escidoc.browser.elabsmodul.view.subcontent.LabsInstrumentPanel;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.BreadCrumbMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public final class LabsInstrumentView extends VerticalLayout {

    private static final String FLOAT_LEFT = "floatleft";

    private static final String FLOAT_RIGHT = "floatright";

    private static final String LAST_MODIFIED_BY = "Last modification by ";

    private static final Logger LOG = LoggerFactory.getLogger(LabsInstrumentView.class);

    private final CssLayout cssLayout = new CssLayout();

    private final CurrentUser currentUser;

    private final MainSite mainSite;

    private final int appHeight;

    private final ItemProxyImpl resourceProxy;

    private final Window mainWindow;

    private final EscidocServiceLocation serviceLocation;

    private final Repositories repositories;

    private LabsInstrumentPanel instrumentPanel = null;

    public LabsInstrumentView(final EscidocServiceLocation serviceLocation, final Repositories repositories,
        final MainSite mainSite, final ResourceProxy resourceProxy, final Window mainWindow,
        final CurrentUser currentUser) {

        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null.");
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(mainSite, "mainSite is null.");
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null.");
        Preconditions.checkNotNull(mainWindow, "mainWindow is null.");

        this.resourceProxy = (ItemProxyImpl) resourceProxy;
        this.repositories = repositories;
        this.mainWindow = mainWindow;
        this.mainSite = mainSite;
        this.serviceLocation = serviceLocation;
        appHeight = mainSite.getApplicationHeight();
        this.currentUser = currentUser;

        // test

        InstrumentBean bean = null;
        try {
            bean = InstrumentController.getInstance().loadBeanData(this.resourceProxy);
        }
        catch (EscidocBrowserException e) {
            LOG.error(e.getLocalizedMessage());
            bean = null;
        }
        this.instrumentPanel = new LabsInstrumentPanel(this.mainWindow, bean);

        init();
    }

    private final void init() {
        buildLayout();
        createBreadcrumbp();
        bindNametoHeader();
        bindProperties();
        bindHrRuler();
        buildContentCell(this.instrumentPanel);
        addComponent(cssLayout);
    }

    /**
     * @param metadataRecs
     */
    private void buildContentCell(final Component formLayout) {
        final Panel panel = new Panel();
        panel.setStyleName(FLOAT_RIGHT);
        panel.setWidth("100%");
        panel.setHeight("82%");
        panel.addComponent(formLayout);
        cssLayout.addComponent(panel);
    }

    private void bindNametoHeader() {
        final Label headerContext = new Label(ViewConstants.RESOURCE_NAME + resourceProxy.getName());
        headerContext.setDescription("header");
        headerContext.setStyleName("h2 fullwidth");
        cssLayout.addComponent(headerContext);
    }

    private void bindProperties() {

        final Panel pnlPropertiesLeft = buildLeftPropertiesPnl();
        final Panel pnlPropertiesRight = buildRightPnlProperties();

        final Label descMetadata1 = new Label("ID: " + resourceProxy.getId());
        final Label descMetadata2 =
            new Label(LAST_MODIFIED_BY + " " + resourceProxy.getModifier() + " on " + resourceProxy.getModifiedOn(),
                Label.CONTENT_XHTML);
        pnlPropertiesLeft.addComponent(descMetadata1);
        pnlPropertiesRight.addComponent(descMetadata2);
        cssLayout.addComponent(pnlPropertiesLeft);
        cssLayout.addComponent(pnlPropertiesRight);

    }

    private Panel buildLeftPropertiesPnl() {
        final Panel pnlPropertiesLeft = new Panel();
        pnlPropertiesLeft.setWidth("40%");
        pnlPropertiesLeft.setHeight("60px");
        pnlPropertiesLeft.setStyleName(FLOAT_LEFT);
        pnlPropertiesLeft.addStyleName(Runo.PANEL_LIGHT);
        pnlPropertiesLeft.getLayout().setMargin(false);
        return pnlPropertiesLeft;
    }

    private Panel buildRightPnlProperties() {
        final Panel pnlPropertiesRight = new Panel();
        pnlPropertiesRight.setWidth("60%");
        pnlPropertiesRight.setHeight("60px");
        pnlPropertiesRight.setStyleName(FLOAT_RIGHT);
        pnlPropertiesRight.addStyleName(Runo.PANEL_LIGHT);
        pnlPropertiesRight.getLayout().setMargin(false);
        return pnlPropertiesRight;
    }

    private void bindHrRuler() {
        final Label descRuler = new Label("<hr/>", Label.CONTENT_RAW);
        descRuler.setStyleName("hr");
        cssLayout.addComponent(descRuler);
    }

    @SuppressWarnings("unused")
    private void createBreadcrumbp() {
        new BreadCrumbMenu(cssLayout, resourceProxy, mainWindow, serviceLocation, repositories);
    }

    private void buildLayout() {
        setMargin(true);
        setHeight("100%");
        cssLayout.setWidth("100%");
        cssLayout.setHeight("100%");

        final int innerelementsHeight = appHeight - 420;
        int accordionHeight = innerelementsHeight - 20;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resourceProxy == null) ? 0 : resourceProxy.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LabsInstrumentView other = (LabsInstrumentView) obj;
        if (resourceProxy == null) {
            if (other.resourceProxy != null) {
                return false;
            }
        }
        else if (!resourceProxy.equals(other.resourceProxy)) {
            return false;
        }
        return true;
    }
}