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

import org.escidoc.browser.layout.LayoutDesign;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.view.helpers.ItemPropertiesVH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public final class ItemView extends VerticalLayout {

    private static final String FLOAT_LEFT = "floatleft";

    private static final String FLOAT_RIGHT = "floatright";

    private static final Logger LOG = LoggerFactory.getLogger(ItemView.class);

    private final CssLayout cssLayout = new CssLayout();

    private final Router router;

    private final int appHeight;

    private final ItemProxyImpl resourceProxy;

    private final Window mainWindow;

    private final EscidocServiceLocation serviceLocation;

    private final Repositories repositories;

    private final CurrentUser currentUser;

    private int accordionHeight;

    private LayoutDesign layout;

    public ItemView(final EscidocServiceLocation serviceLocation, final Repositories repositories, final Router router,
        final LayoutDesign layout, final ResourceProxy resourceProxy, final Window mainWindow,
        final CurrentUser currentUser) {

        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null.");
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(router, "mainSite is null.");
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null.");
        Preconditions.checkNotNull(mainWindow, "mainWindow is null.");

        this.resourceProxy = (ItemProxyImpl) resourceProxy;
        this.repositories = repositories;
        this.mainWindow = mainWindow;
        this.router = router;
        this.layout = router.getLayout();
        this.serviceLocation = serviceLocation;
        appHeight = router.getApplicationHeight();
        this.currentUser = currentUser;

        init();
    }

    private final void init() {
        buildLayout();
        new ItemPropertiesVH(resourceProxy, repositories, currentUser, cssLayout, mainWindow, serviceLocation).init();
        buildLeftCell(new ItemContent(repositories, resourceProxy, serviceLocation, mainWindow, currentUser));
        buildRightCell(new MetadataRecsItem(resourceProxy, accordionHeight, mainWindow, serviceLocation, repositories,
            currentUser, router, layout).asAccord());

        addComponent(cssLayout);
    }

    /**
     * @param metadataRecs
     */
    private void buildRightCell(final Component metadataRecs) {
        final Panel rightPanel = new Panel();
        rightPanel.setStyleName(FLOAT_RIGHT);
        rightPanel.setWidth("70%");
        rightPanel.setHeight("82%");
        rightPanel.addComponent(metadataRecs);
        cssLayout.addComponent(rightPanel);
        rightPanel.getLayout().setMargin(false);
    }

    private void buildLeftCell(final Component itCnt) {
        final Panel leftPanel = new Panel();
        leftPanel.getLayout().setMargin(false);
        leftPanel.setStyleName("floatleft");
        leftPanel.setScrollable(false);
        leftPanel.setWidth("30%");
        leftPanel.setHeight("82%");
        leftPanel.addComponent(itCnt);

        VerticalLayout panelLayout = (VerticalLayout) leftPanel.getContent();
        panelLayout.setExpandRatio(itCnt, 1.0f);
        panelLayout.setHeight("100%");

        cssLayout.addComponent(leftPanel);
    }

    private void buildLayout() {
        setMargin(true);
        setHeight("100%");
        cssLayout.setWidth("100%");
        cssLayout.setHeight("100%");

        final int innerelementsHeight = appHeight - 420;
        accordionHeight = innerelementsHeight - 20;
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
        final ItemView other = (ItemView) obj;
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
