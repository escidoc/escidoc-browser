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

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.ui.MainSite;

import com.google.common.base.Preconditions;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class ItemView extends VerticalLayout {

    private static final String CREATED_BY = "Created by";

    private static final String FULLWIDHT_STYLE_NAME = "fullwidth";

    private static final String LAST_MODIFIED_BY = "Last modification by";

    private static final String RESOURCE_NAME = "Item: ";

    private static final String STATUS = "Status is ";

    private final CssLayout cssLayout = new CssLayout();

    private final int appHeight;

    private final ItemProxyImpl resourceProxy;

    private final Window mainWindow;

    private final EscidocServiceLocation serviceLocation;

    private final Repositories repositories;

    private final CurrentUser currentUser;

    private int accordionHeight;

    private int innerelementsHeight;

    public ItemView(final EscidocServiceLocation serviceLocation, final Repositories repositories,
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
        this.serviceLocation = serviceLocation;
        appHeight = mainSite.getApplicationHeight();
        this.currentUser = currentUser;

        init();
    }

    void init() {
        buildLayout();
        createBreadcrumbp();
        bindNametoHeader();
        bindDescription();
        bindHrRuler();
        bindProperties();
        buildLeftCell(new ItemContent(repositories, resourceProxy, serviceLocation, mainWindow, currentUser));
        buildRightCell(new MetadataRecsItem(resourceProxy, accordionHeight, mainWindow, serviceLocation, repositories)
            .asAccord());
        addComponent(cssLayout);
    }

    /**
     * @param metadataRecs
     */
    private void buildRightCell(final Component metadataRecs) {

        final Panel rightpnl = new Panel();
        rightpnl.setStyleName("floatright");
        rightpnl.setWidth("70%");
        rightpnl.setHeight("82%");
        rightpnl.addComponent(metadataRecs);
        cssLayout.addComponent(rightpnl);
    }

    private void buildLeftCell(final Component itCnt) {
        // Adding some buttons
        final AbsoluteLayout absL = new AbsoluteLayout();
        absL.setWidth("100%");
        absL.setHeight(innerelementsHeight + "px");

        final Panel leftpnl = new Panel();
        leftpnl.setStyleName("floatleft");
        leftpnl.setScrollable(false);
        leftpnl.setWidth("30%");
        leftpnl.setHeight("82%");
        leftpnl.addComponent(itCnt);
        cssLayout.addComponent(leftpnl);
    }

    private void bindProperties() {
        // ContainerView DescMetadata1
        final Label descMetadata1 =
            new Label("ID: " + resourceProxy.getId() + " <br /> " + STATUS + resourceProxy.getStatus(),
                Label.CONTENT_RAW);
        descMetadata1.setStyleName("floatleft columnheight50");
        descMetadata1.setWidth("30%");
        cssLayout.addComponent(descMetadata1);

        // ContainerView DescMetadata2
        final Label descMetadata2 =
            new Label(CREATED_BY + "<a href='#'> " + resourceProxy.getCreator() + "</a> "
                + resourceProxy.getCreatedOn() + " <br>" + LAST_MODIFIED_BY + " <a href='#"
                + resourceProxy.getModifier() + "'>" + resourceProxy.getModifier() + "</a> "
                + resourceProxy.getModifiedOn() + " " + getHistory(), Label.CONTENT_RAW);
        descMetadata2.setStyleName("floatright columnheight50");
        descMetadata2.setWidth("70%");
        cssLayout.addComponent(descMetadata2);

    }

    private void bindHrRuler() {
        // ContainerView Horizontal Ruler
        final Label descRuler =
            new Label(
                "____________________________________________________________________________________________________");
        descRuler.setStyleName("hr");
        cssLayout.addComponent(descRuler);
    }

    private void bindDescription() {
        final Label descContext1 = new Label(resourceProxy.getDescription());
        descContext1.setStyleName(FULLWIDHT_STYLE_NAME);
        cssLayout.addComponent(descContext1);
    }

    private void bindNametoHeader() {
        final Label headerContext = new Label(RESOURCE_NAME + resourceProxy.getName());
        headerContext.setDescription("header");
        headerContext.setStyleName("h1 fullwidth");
        cssLayout.addComponent(headerContext);
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
        innerelementsHeight = appHeight - 420;
        accordionHeight = innerelementsHeight - 20;
    }

    /**
     * Checks if a resource has previous history and returns a string TODO in the future it should be a Link (Button
     * Link) that holds a reference to the history of the resource
     * 
     * @return String
     */
    private String getHistory() {
        String strHistory;
        if (resourceProxy.getPreviousVersion() == null) {
            strHistory = " has no previous versions";
        }
        else {
            strHistory = " previous version";
        }
        return strHistory;
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