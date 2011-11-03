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

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.view.helpers.CreatePermanentLinkVH;
import org.escidoc.browser.ui.view.helpers.DirectMember;

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

    private final CssLayout cssLayout = new CssLayout();

    private final Router router;

    private final ResourceProxy resourceProxy;

    private int appHeight;

    private int accordionHeight;

    private final EscidocServiceLocation serviceLocation;

    private final Window mainWindow;

    private final Repositories repositories;

    public ContextView(final EscidocServiceLocation serviceLocation, final Router router,
        final ResourceProxy resourceProxy, final Window mainWindow, final Repositories repositories)
        throws EscidocClientException {

        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(router, "mainSite is null: %s", router);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);

        this.serviceLocation = serviceLocation;
        this.router = router;
        this.resourceProxy = resourceProxy;
        this.mainWindow = mainWindow;
        this.repositories = repositories;
        appHeight = router.getApplicationHeight();
        init();
    }

    private void init() throws EscidocClientException {
        configureLayout();
        createBreadCrump();
        new CreatePermanentLinkVH(mainWindow.getURL().toString(), resourceProxy.getId(), resourceProxy
            .getType().toString(), cssLayout, serviceLocation);
        bindNameToHeader();
        bindDescription();
        addHorizontalRuler();
        bindProperties();
        leftCell();
        addContextDetailsView();
        addComponent(cssLayout);
    }

    private void addContextDetailsView() {
        rightCell(new MetadataRecsContext(resourceProxy, accordionHeight, mainWindow).asAccord());
    }

    /**
     * This is the inner Right Cell within a Context By default a set of Organizational Unit / Admin Description /
     * RelatedItem / Resources are bound
     * 
     * @param comptoBind
     */
    @SuppressWarnings("deprecation")
    private void rightCell(final Component comptoBind) {
        final Panel rightCell = new Panel();
        rightCell.setStyleName("floatright");
        rightCell.setWidth("70%");
        rightCell.setHeight("82%");
        rightCell.getLayout().setMargin(false);
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
    @SuppressWarnings("deprecation")
    private void leftCell() throws EscidocClientException {
        final Panel leftPanel = new Panel();

        leftPanel.setStyleName("directmembers floatleft");
        leftPanel.setScrollable(false);
        leftPanel.setWidth("30%");
        leftPanel.setHeight("82%");
        leftPanel.getLayout().setMargin(false);

        new DirectMember(serviceLocation, router, resourceProxy.getId(), mainWindow, repositories, leftPanel)
            .contextAsTree();
        cssLayout.addComponent(leftPanel);
    }

    /**
     * Bindind Context Properties 2 sets of labels in 2 rows
     */
    private void bindProperties() {
        final Label descMetadata1 =
            new Label("ID: " + resourceProxy.getId() + " <br /> " + resourceProxy.getType().asLabel() + " is "
                + resourceProxy.getStatus(), Label.CONTENT_RAW);
        descMetadata1.setWidth("35%");
        descMetadata1.setStyleName("floatleft columnheight50");
        cssLayout.addComponent(descMetadata1);

        // RIGHT SIDE
        final Label descMetadata2 =
            new Label(CREATED_BY + " " + resourceProxy.getCreator() + " on " + resourceProxy.getCreatedOn() + "<br/>"
                + LAST_MODIFIED_BY + " " + resourceProxy.getModifier() + " on " + resourceProxy.getModifiedOn(),
                Label.CONTENT_XHTML);

        descMetadata2.setStyleName("floatright columnheight50");
        descMetadata2.setWidth("65%");
        cssLayout.addComponent(descMetadata2);
    }

    private void addHorizontalRuler() {
        final Label descRuler = new Label("<hr />", Label.CONTENT_RAW);
        descRuler.setStyleName("hr");
        cssLayout.addComponent(descRuler);
    }

    private void bindDescription() {
        final Label description = new Label(resourceProxy.getDescription());
        description.setStyleName(FULLWIDHT_STYLE_NAME);
        cssLayout.addComponent(description);
    }

    private void createBreadCrump() {
        new BreadCrumbMenu(cssLayout, resourceProxy);
    }

    private void bindNameToHeader() {
        final Label headerContext = new Label(RESOURCE_NAME + resourceProxy.getName());
        headerContext.setStyleName("h1 fullwidth");
        cssLayout.addComponent(headerContext);
    }

    private void configureLayout() {
        appHeight = router.getApplicationHeight();

        setMargin(true, true, false, true);
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
