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
package org.escidoc.browser.ui.view.helpers;

import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ContainerProxyImpl;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.maincontent.ContainerAddView;
import org.escidoc.browser.ui.navigation.NavigationTreeBuilder;
import org.escidoc.browser.ui.navigation.NavigationTreeView;
import org.escidoc.browser.ui.navigation.menubar.ItemAddView;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class DirectMember {

    private final String parentId;

    private final Router router;

    private final Window mainWindow;

    private final NavigationTreeBuilder navigationTreeBuilder;

    private Panel panel;

    private static final String DIRECT_MEMBERS = "Direct Members";

    /**
     * The method retrieves a Panel as the View where it should place itself and binds there a List of Members and some
     * activity buttons
     * 
     * @param serviceLocation
     * @param router
     * @param parentId
     * @param mainWindow
     * @param currentUser
     * @param repositories
     * @param leftPanel
     */
    public DirectMember(final EscidocServiceLocation serviceLocation, final Router router, final String parentId,
        final Window mainWindow, final CurrentUser currentUser, final Repositories repositories, Panel leftPanel) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(router, "Router is null: %s", router);
        Preconditions.checkNotNull(parentId, "parentID is null: %s", parentId);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(leftPanel, "Panel from the View is null: %s", repositories);

        this.parentId = parentId;
        this.router = router;
        this.mainWindow = mainWindow;
        this.panel = leftPanel;

        try {
            createButtons();
        }
        catch (EscidocClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        navigationTreeBuilder = new NavigationTreeBuilder(serviceLocation, currentUser, repositories);
    }

    public void contextAsTree() throws EscidocClientException {
        final NavigationTreeView tree = createContextDirectMembers();
        tree.setSizeFull();
        bindDirectMembersInTheContainer(tree);

    }

    private NavigationTreeView createContextDirectMembers() throws EscidocClientException {
        return navigationTreeBuilder.buildContextDirectMemberTree(router, parentId, mainWindow);
    }

    public void containerAsTree() throws EscidocClientException {
        final NavigationTreeView tree = createContainerDirectMembers();
        tree.setSizeFull();
        bindDirectMembersInTheContainer(tree);
    }

    private NavigationTreeView createContainerDirectMembers() throws EscidocClientException {
        return navigationTreeBuilder.buildContainerDirectMemberTree(router, parentId, mainWindow);
    }

    @SuppressWarnings("serial")
    protected void createButtons() throws EscidocClientException {
        final Label nameofPanel =
            new Label("<div class=\"v-accordion-item-caption\"><div class=\"v-caption\"><div class=\"v-captiontext\">"
                + DIRECT_MEMBERS + "</div><div class=\"v-caption-clearelem\"></div></div></div>", Label.CONTENT_RAW);
        nameofPanel.setStyleName("accordion v-captiontext");

        nameofPanel.setWidth("100%");
        panel.addComponent(nameofPanel);
        // the changes start here
        VerticalLayout panelLayout = (VerticalLayout) panel.getContent();
        panelLayout.setHeight("100%");
        // panelLayout.addStyleName("my-panel");

        Button addContainerButton = new Button("+Container  ");
        addContainerButton.setStyleName(Reindeer.BUTTON_SMALL);

        final ContainerProxy containerProxy =
            new ContainerProxyImpl(router.getRepositories().container().findContainerById(parentId));
        final String contextId = containerProxy.getContext().getObjid();
        addContainerButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    new ContainerAddView(router.getRepositories(), router.getMainWindow(), containerProxy, contextId,
                        router).openSubWindow();
                }
                catch (final EscidocClientException e) {
                    mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });

        Button addItemButton = new Button("+Item  ");
        addItemButton.setStyleName(Reindeer.BUTTON_SMALL);
        addItemButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    new ItemAddView(router.getRepositories(), router.getMainWindow(), containerProxy, contextId, router)
                        .openSubWindow();
                }
                catch (final EscidocClientException e) {
                    mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
                }

            }
        });

        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("200px");

        hl.setHeight("20px");

        panelLayout.addComponent(hl);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        hl.addComponent(buttonLayout);

        buttonLayout.addComponent(addContainerButton);
        buttonLayout.addComponent(addItemButton);

    }

    protected void bindDirectMembersInTheContainer(Component comptoBind) {
        VerticalLayout panelLayout = (VerticalLayout) panel.getContent();
        panelLayout.addComponent(comptoBind);
        panelLayout.setExpandRatio(comptoBind, 1.0f);

    }
}
