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
package org.escidoc.browser.ui.listeners;

import org.escidoc.browser.layout.LayoutDesign;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.maincontent.ContainerView;
import org.escidoc.browser.ui.maincontent.ItemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.ResourceType;
import de.escidoc.core.resources.common.Relation;
import de.escidoc.core.resources.common.Relations;

@SuppressWarnings("serial")
public class RelationsClickListener implements ClickListener {

    private static final Logger LOG = LoggerFactory.getLogger(RelationsClickListener.class);

    private ItemProxy itemProxy;

    private final Window mainWindow;

    private ContainerProxy containerProxy;

    private Layout content;

    final private Repository itemOrContainerRepository;

    private ResourceType type;

    private EscidocServiceLocation escidocServiceLocation;

    private Repositories repositories;

    private CurrentUser currentUser;

    private Router mainSite;

    protected Component cmpView;

    protected LayoutDesign layout;

    /**
     * Container for the ItemProxy case
     * 
     * @param resourceProxy
     * @param mainWindow
     * @param repositories
     * @param escidocServiceLocation
     */
    public RelationsClickListener(final ItemProxy resourceProxy, final Window mainWindow,
        final EscidocServiceLocation escidocServiceLocation, final Repositories repositories, final Router mainSite,
        LayoutDesign layout, final CurrentUser currentUser) {
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions
            .checkNotNull(escidocServiceLocation, "escidocServiceLocation is null: %s", escidocServiceLocation);

        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);
        itemProxy = resourceProxy;
        Preconditions.checkNotNull(itemProxy, "resourceProxy is null: %s", itemProxy);
        this.mainWindow = mainWindow;
        this.currentUser = currentUser;
        this.mainSite = mainSite;
        this.escidocServiceLocation = escidocServiceLocation;
        this.repositories = repositories;
        this.layout = layout;
        itemOrContainerRepository = repositories.item();
    }

    /**
     * Constructor for the ContainerProxy
     * 
     * @param resourceProxy
     * @param mainWindow
     * @param escidocServiceLocation
     * @param repositories
     * @param currentUser
     * @param mainSite
     */
    public RelationsClickListener(final ContainerProxy resourceProxy, final Window mainWindow,
        final EscidocServiceLocation escidocServiceLocation, final Repositories repositories, CurrentUser currentUser,
        Router mainSite) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null.");

        containerProxy = resourceProxy;
        this.mainWindow = mainWindow;
        this.repositories = repositories;
        this.currentUser = currentUser;
        this.mainSite = mainSite;
        itemOrContainerRepository = repositories.container();
    }

    public Layout getRelations(final Repository cr, final String id, final Window subwindow)
        throws EscidocClientException {

        final Relations relations = cr.getRelations(id);
        HorizontalLayout hl = new HorizontalLayout();

        for (final Relation relation : relations) {
            String predicate;
            if (relation.getPredicate().indexOf("#") != -1) {
                predicate =
                    relation.getPredicate().substring(relation.getPredicate().lastIndexOf('#'),
                        relation.getPredicate().length());
            }
            else {
                predicate = relation.getPredicate();
            }

            String prefixPath = relation.getXLinkHref().substring(0, relation.getXLinkHref().lastIndexOf('/'));
            type = ResourceType.getValue(prefixPath);

            Button btnRelation =
                new Button(itemProxy.getName() + " relation as " + predicate + " of " + relation.getXLinkTitle());
            btnRelation.setStyleName(BaseTheme.BUTTON_LINK);
            btnRelation.addListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    if (type.name().equals("CONTAINER")) {
                        try {
                            cmpView =
                                new ContainerView(escidocServiceLocation, mainSite, (ContainerProxy) repositories
                                    .container().findById(relation.getObjid()), mainWindow, currentUser, repositories);
                        }
                        catch (EscidocClientException e) {
                            mainWindow.showNotification(e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }
                    else if (type.name().equals("ITEM")) {
                        try {
                            cmpView =
                                new ItemView(escidocServiceLocation, repositories, mainSite, layout,
                                    (ItemProxy) repositories.item().findById(relation.getObjid()), mainWindow,
                                    currentUser);
                        }
                        catch (EscidocClientException e) {
                            mainWindow.showNotification(e.getLocalizedMessage());
                        }
                    }
                    (subwindow.getParent()).removeWindow(subwindow);
                    mainSite.openTab(cmpView, relation.getXLinkTitle());

                }
            });
            hl.addComponent(btnRelation);
            LOG.debug("relation title: " + relation.getXLinkTitle());
        }

        return hl;

    }

    @Override
    public void buttonClick(final ClickEvent event) {
        final Window subwindow = new Window("Relations");
        subwindow.setWidth("600px");
        subwindow.setModal(true);

        String id = "";
        if (event.getButton().getCaption().equals("Container Content Relations")) {
            id = containerProxy.getId();
        }
        else if (event.getButton().getCaption().equals("Item Content Relations")) {
            id = itemProxy.getId();
        }
        else {
            throw new RuntimeException("Bug: unexpected event button: " + event.getButton());
        }

        try {
            content = getRelations(itemOrContainerRepository, id, subwindow);
        }
        catch (final EscidocClientException e) {
            content = new HorizontalLayout();
            content.addComponent(new Label("No information available"));
        }

        subwindow.addComponent(content);
        if (subwindow.getParent() != null) {
            mainWindow.showNotification("Window is already open");
        }
        else {
            mainWindow.addWindow(subwindow);
        }
    }

}
