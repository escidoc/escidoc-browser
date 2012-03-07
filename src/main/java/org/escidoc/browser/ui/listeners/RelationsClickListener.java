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

import org.escidoc.browser.controller.ContainerController;
import org.escidoc.browser.controller.ItemController;
import org.escidoc.browser.layout.LayoutDesign;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.Router;
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

    private final Repositories repositories;

    private final Router router;

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
    public RelationsClickListener(final ItemProxy resourceProxy, final Repositories repositories, final Router router) {
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);

        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(router, "mainSite is null: %s", router);
        itemProxy = resourceProxy;
        Preconditions.checkNotNull(itemProxy, "resourceProxy is null: %s", itemProxy);
        this.router = router;
        this.mainWindow = router.getMainWindow();
        this.repositories = repositories;

        itemOrContainerRepository = repositories.item();
    }

    /**
     * Constructor for the ContainerProxy
     * 
     * @param resourceProxy
     * @param mainWindow
     * @param escidocServiceLocation
     * @param repositories
     * @param mainSite
     */
    public RelationsClickListener(final ContainerProxy resourceProxy, final Window mainWindow,
        final EscidocServiceLocation escidocServiceLocation, final Repositories repositories, final Router mainSite) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null.");

        containerProxy = resourceProxy;
        this.mainWindow = mainWindow;
        this.repositories = repositories;
        this.router = mainSite;
        itemOrContainerRepository = repositories.container();
    }

    public Layout getRelations(final Repository cr, final String id, final Window subwindow)
        throws EscidocClientException {

        final Relations relations = cr.getRelations(id);
        final HorizontalLayout hl = new HorizontalLayout();

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

            final String prefixPath = relation.getXLinkHref().substring(0, relation.getXLinkHref().lastIndexOf('/'));
            type = ResourceType.getValue(prefixPath);

            final Button btnRelation =
                new Button(itemProxy.getName() + " relation as " + predicate + " of " + relation.getXLinkTitle());
            btnRelation.setStyleName(BaseTheme.BUTTON_LINK);
            btnRelation.addListener(new ClickListener() {

                @Override
                public void buttonClick(final ClickEvent event) {
                    if (type.name().equals("CONTAINER")) {
                        try {
                            new ContainerController(repositories, router, repositories.container().findById(
                                relation.getObjid()));
                        }
                        catch (final EscidocClientException e) {
                            mainWindow.showNotification(e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }
                    else if (type.name().equals("ITEM")) {
                        try {
                            new ItemController(repositories, router, repositories.item().findById(relation.getObjid()));
                        }
                        catch (final EscidocClientException e) {
                            mainWindow.showNotification(e.getLocalizedMessage());
                        }
                    }

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
