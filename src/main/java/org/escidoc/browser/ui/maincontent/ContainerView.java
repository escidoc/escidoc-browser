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
import org.escidoc.browser.repository.ContainerProxy;
import org.escidoc.browser.ui.MainSite;

import com.google.common.base.Preconditions;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

/**
 * @author ARB
 * 
 */
public class ContainerView extends VerticalLayout {

    private final int appHeight;

    private final MainSite mainSite;

    private final ContainerProxy resourceProxy;

    private final CssLayout cssLayout = new CssLayout();

    private static final String DESCRIPTION = "Description: ";

    private static final String CREATED_BY = "Created by";

    private static final String NAME = "Name: ";

    private static final String FULLWIDHT_STYLE_NAME = "fullwidth";

    private static final String LAST_MODIFIED_BY = "Last modification by";

    private static final String DIRECT_MEMBERS = "Direct Members";

    private static final String RESOURCE_NAME = "Container: ";

    private static final String STATUS = "Status is ";

    private int accordionHeight;

    private int innerelementsHeight;

    private final EscidocServiceLocation serviceLocation;

    private final Window mainWindow;

    private final CurrentUser currentUser;

    public ContainerView(final EscidocServiceLocation serviceLocation, final MainSite mainSite,
        final ResourceProxy resourceProxy, final Window mainWindow, final CurrentUser currentUser)
        throws EscidocClientException {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkArgument(resourceProxy instanceof ContainerProxy, resourceProxy.getClass()
            + " is not an instance of ContainerProxy.class");
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkArgument(resourceProxy instanceof ContainerProxy, resourceProxy.getClass()
            + " is not an instance of ContainerProxy.class");
        this.serviceLocation = serviceLocation;
        this.mainSite = mainSite;
        appHeight = mainSite.getApplicationHeight();
        this.resourceProxy = (ContainerProxy) resourceProxy;
        this.mainWindow = mainWindow;
        this.currentUser = currentUser;
        init();
    }

    private void init() throws EscidocClientException {
        configureLayout();
        createBreadCrumb();
        bindNameToHeader();
        bindDescription();
        addHorizontalRuler();
        bindProperties();
        addDirectMembers();
        addMetadataRecords();
        addComponent(cssLayout);
    }

    private void addMetadataRecords() {
        final MetadataRecs metaData = new MetadataRecs(resourceProxy, accordionHeight, mainWindow, serviceLocation);
        rightCell(metaData.asAccord());
    }

    private void addDirectMembers() throws EscidocClientException {
        final DirectMember directMembers =
            new DirectMember(serviceLocation, mainSite, resourceProxy.getId(), mainWindow, currentUser);
        leftCell(DIRECT_MEMBERS, directMembers.containerAsTree());
    }

    /**
     * This is the inner Right Cell within a Context By default a set of Organizational Unit / Admin Description /
     * RelatedItem / Resources are bound
     * 
     * @param comptoBind
     */
    private void rightCell(final Component comptoBind) {
        final Panel rightpnl = new Panel();
        rightpnl.setStyleName("floatright");
        rightpnl.setWidth("70%");
        rightpnl.setHeight("82%");
        rightpnl.addComponent(comptoBind);
        cssLayout.addComponent(rightpnl);
    }

    private void leftCell(final String string, final Component comptoBind) {
        final Panel leftpnl = new Panel();

        leftpnl.setStyleName("directmembers floatleft paddingtop10 ");
        leftpnl.setScrollable(false);

        leftpnl.setWidth("30%");
        leftpnl.setHeight("82%");

        final Label nameofPanel = new Label("<strong>" + DIRECT_MEMBERS + "</string>", Label.CONTENT_RAW);
        leftpnl.addComponent(nameofPanel);

        leftpnl.addComponent(comptoBind);

        // Adding some buttons
        final AbsoluteLayout absL = new AbsoluteLayout();
        absL.setWidth("100%");
        absL.setHeight(innerelementsHeight + "px");
        final HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.addComponent(new Button("Add"));
        horizontal.addComponent(new Button("Delete"));
        horizontal.addComponent(new Button("Edit"));
        leftpnl.addComponent(horizontal);

        absL.addComponent(horizontal, "left: 0px; top: 380px;");
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
        descMetadata1.setStyleName("floatleft columnheight50");
        descMetadata1.setWidth("35%");
        cssLayout.addComponent(descMetadata1);

        // RIGHT SIDE
        final Label descMetadata2 =
            new Label(CREATED_BY + "<a href='#'> " + resourceProxy.getCreator() + "</a> "
                + resourceProxy.getCreatedOn() + "<br>" + LAST_MODIFIED_BY + " <a href='#user/"
                + resourceProxy.getModifier() + "'>" + resourceProxy.getModifier() + "</a>", Label.CONTENT_RAW);
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
        final BreadCrumbMenu bm = new BreadCrumbMenu(cssLayout, resourceProxy, mainWindow, serviceLocation);
    }

    private void bindNameToHeader() {
        final Label headerContext = new Label(RESOURCE_NAME + resourceProxy.getName());
        headerContext.setStyleName("h1 fullwidth");
        cssLayout.addComponent(headerContext);
    }

    private void configureLayout() {
        setMargin(true);
        this.setHeight("100%");

        cssLayout.setWidth("100%");
        cssLayout.setHeight("100%");

        // this is an assumption of the height that should be left for the
        // accordion or elements of the DirectMember in the same level
        // I remove 420px that are taken by elements on the de.escidoc.esdc.page
        // and 40px for the accordion elements?
        final int innerelementsHeight = appHeight - 420;
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
        if (resourceProxy.getPreviousVersion()) {
            strHistory = " previous version";
        }
        else {
            strHistory = " has no previous history";
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
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContainerView other = (ContainerView) obj;
        if (resourceProxy == null) {
            if (other.resourceProxy != null)
                return false;
        }
        else if (!resourceProxy.equals(other.resourceProxy))
            return false;
        return true;
    }
}
