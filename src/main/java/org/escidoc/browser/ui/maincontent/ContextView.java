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
import org.escidoc.browser.ui.MainSite;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class ContextView extends VerticalLayout {

    private static final String CREATED_BY = "Created by";

    private static final String FULLWIDHT_STYLE_NAME = "fullwidth";

    private static final String LAST_MODIFIED_BY = "Last modification by";

    private static final String DIRECT_MEMBERS = "Direct Members";

    private static final String RESOURCE_NAME = "Workspace: ";

    private static final String STATUS = "Status is ";

    private final CssLayout cssLayout = new CssLayout();

    private final MainSite mainSite;

    private final ResourceProxy resourceProxy;

    private int appHeight;

    private int accordionHeight;

    private final EscidocServiceLocation serviceLocation;

    private final Window mainWindow;

    private final CurrentUser currentUser;

    private final Repositories repositories;

    public ContextView(final EscidocServiceLocation serviceLocation, final MainSite mainSite,
        final ResourceProxy resourceProxy, final Window mainWindow, final CurrentUser currentUser,
        final Repositories repositories) throws EscidocClientException {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        this.serviceLocation = serviceLocation;
        this.mainSite = mainSite;
        this.resourceProxy = resourceProxy;
        this.mainWindow = mainWindow;
        this.currentUser = currentUser;
        this.repositories = repositories;
        appHeight = mainSite.getApplicationHeight();
        init();
    }

    private void init() throws EscidocClientException {
        configureLayout();
        createBreadCrumb();
        bindNameToHeader();
        bindDescription();
        addHorizontalRuler();
        bindProperties();
        addDirectMembersView();
        addContextDetailsView();
        addComponent(cssLayout);
    }

    private void addContextDetailsView() {
        final MetadataRecsContext cnxAddinfo =
            new MetadataRecsContext(resourceProxy, accordionHeight, mainWindow, serviceLocation);
        rightCell(cnxAddinfo.asAccord());
    }

    private void addDirectMembersView() throws EscidocClientException {
        leftCell(DIRECT_MEMBERS, new DirectMember(serviceLocation, mainSite, resourceProxy.getId(), mainWindow,
            currentUser, repositories).contextAsTree());
    }

    /**
     * This is the inner Right Cell within a Context By default a set of Organizational Unit / Admin Description /
     * RelatedItem / Resources are bound
     * 
     * @param comptoBind
     */
    private void rightCell(final Component comptoBind) {
        final Panel rightCell = new Panel();
        rightCell.setStyleName("floatright");
        rightCell.setWidth("70%");
        rightCell.setHeight("82%");
        rightCell.addComponent(comptoBind);
        cssLayout.addComponent(rightCell);
    }

    /**
     * This is the inner Left Cell within a Context By default the Direct Members are bound here
     * 
     * @param directMembers
     * 
     * @param comptoBind
     */
    private void leftCell(final String directMembers, final Component comptoBind) {
        final Panel leftpnl = new Panel();

        leftpnl.setStyleName("directmembers floatleft");
        leftpnl.setScrollable(false);
        leftpnl.setWidth("30%");
        leftpnl.setHeight("82%");

        final Label nameofPanel = new Label("<strong>" + DIRECT_MEMBERS + "</string>", Label.CONTENT_RAW);
        leftpnl.addComponent(nameofPanel);

        leftpnl.addComponent(comptoBind);
        cssLayout.addComponent(leftpnl);
    }

    /**
     * Bindind Context Properties 2 sets of labels in 2 rows
     */
    private void bindProperties() {
        // LEFT SIde
        final Label descMetadata1 =
            new Label("ID: " + resourceProxy.getId() + " <br /> " + STATUS + resourceProxy.getStatus(),
                Label.CONTENT_RAW);
        descMetadata1.setWidth("35%");
        descMetadata1.setStyleName("floatleft columnheight50");
        cssLayout.addComponent(descMetadata1);

        // RIGHT SIDE
        final Label descMetadata2 =
            new Label(CREATED_BY + " <a href='#'>" + resourceProxy.getCreator() + "</a> "
                + resourceProxy.getCreatedOn() + "<br>" + LAST_MODIFIED_BY + " <a href='#"
                + resourceProxy.getModifier() + "'>" + resourceProxy.getModifier() + "</a> "
                + resourceProxy.getModifiedOn() + " <br>", Label.CONTENT_RAW);
        descMetadata2.setStyleName("floatright columnheight50");
        descMetadata2.setWidth("65%");
        cssLayout.addComponent(descMetadata2);
    }

    // TODO Fix this ruler! I cannot believe I did that line as a ruler
    private void addHorizontalRuler() {
        final Label descRuler =
            new Label(
                "____________________________________________________________________________________________________");
        descRuler.setStyleName("hr");
        cssLayout.addComponent(descRuler);
    }

    private void bindDescription() {
        final Label description = new Label(resourceProxy.getDescription());
        description.setStyleName(FULLWIDHT_STYLE_NAME);
        cssLayout.addComponent(description);
    }

    private void createBreadCrumb() {
        new BreadCrumbMenu(cssLayout, resourceProxy, repositories);
    }

    private void bindNameToHeader() {
        final Label headerContext = new Label(RESOURCE_NAME + resourceProxy.getName());
        headerContext.setStyleName("h1 fullwidth");
        cssLayout.addComponent(headerContext);
    }

    private void configureLayout() {
        appHeight = mainSite.getApplicationHeight();

        setMargin(true);
        setHeight(100, Sizeable.UNITS_PERCENTAGE);

        cssLayout.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        cssLayout.setHeight(100, Sizeable.UNITS_PERCENTAGE);

        // this is an assumtion of the height that should be left for the
        // accordion or elements of the DirectMember in the same level
        accordionHeight = appHeight - 420;
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
        final ContextView other = (ContextView) obj;
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
