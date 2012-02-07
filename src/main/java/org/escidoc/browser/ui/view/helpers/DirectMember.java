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

import java.net.URISyntaxException;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.internal.ContainerProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.ResourceAddViewImpl;
import org.escidoc.browser.ui.navigation.NavigationTreeBuilder;
import org.escidoc.browser.ui.navigation.NavigationTreeView;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class DirectMember {

    private final String parentId;

    private final Router router;

    private final Window mainWindow;

    private final NavigationTreeBuilder navigationTreeBuilder;

    private final Panel panel;

    private final String resourceType;

    private final Repositories repositories;

    private String contextId;

    private ResourceProxy resourceProxy;

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
        final Window mainWindow, final Repositories repositories, final Panel leftPanel, final String resourceType) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(router, "Router is null: %s", router);
        Preconditions.checkNotNull(parentId, "parentID is null: %s", parentId);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(leftPanel, "Panel from the View is null: %s", repositories);

        this.parentId = parentId;
        this.router = router;
        this.mainWindow = mainWindow;
        this.panel = leftPanel;
        this.resourceType = resourceType;
        this.contextId = parentId;
        this.repositories = repositories;

        bindButtons();
        navigationTreeBuilder = new NavigationTreeBuilder(mainWindow, router, repositories);
    }

    private void bindButtons() {
        if (hasAccessAddResources()) {
            try {
                createButtons();
            }
            catch (final EscidocClientException e) {
                mainWindow.showNotification(ViewConstants.CANNOT_CREATE_BUTTONS + e.getLocalizedMessage(),
                    Window.Notification.TYPE_ERROR_MESSAGE);
            }
        }
    }

    public void contextAsTree() throws EscidocClientException {
        final NavigationTreeView tree = createContextDirectMembers();
        tree.setSizeFull();
        bindDirectMembersInTheContainer(tree);

    }

    private NavigationTreeView createContextDirectMembers() throws EscidocClientException {
        return navigationTreeBuilder.buildContextDirectMemberTree(parentId);
    }

    public void containerAsTree() throws EscidocClientException {
        final NavigationTreeView tree = createContainerDirectMembers();
        tree.setSizeFull();
        bindDirectMembersInTheContainer(tree);
    }

    private NavigationTreeView createContainerDirectMembers() throws EscidocClientException {
        return navigationTreeBuilder.buildContainerDirectMemberTree(parentId);
    }

    @SuppressWarnings("serial")
    protected void createButtons() throws EscidocClientException {
        CssLayout cssLayout = headerButton();

        panel.addComponent(cssLayout);
        panel.setStyleName("directmembers");
        panel.setScrollable(true);

        final VerticalLayout panelLayout = (VerticalLayout) panel.getContent();
        panelLayout.setSizeUndefined();
        panelLayout.setWidth("100%");
        panelLayout.setStyleName(Runo.PANEL_LIGHT);
    }

    private CssLayout headerButton() throws EscidocClientException {
        CssLayout cssLayout = new CssLayout();
        cssLayout.addStyleName("v-accordion-item-caption v-caption v-captiontext");
        cssLayout.setWidth("100%");
        cssLayout.setMargin(false);

        final Label nameofPanel = new Label(ViewConstants.DIRECT_MEMBERS, Label.CONTENT_RAW);
        nameofPanel.setStyleName("accordion v-captiontext");
        nameofPanel.setWidth("70%");
        cssLayout.addComponent(nameofPanel);

        ThemeResource ICON = new ThemeResource("images/assets/plus.png");
        if (resourceType == ResourceType.CONTAINER.toString()) {
            resourceProxy = new ContainerProxyImpl(router.getRepositories().container().findContainerById(parentId));
            contextId = resourceProxy.getContext().getObjid();
        }
        else {
            // It has to be a context
            resourceProxy = router.getRepositories().context().findById(parentId);
        }

        final Button addResourceButton = new Button();
        addResourceButton.setStyleName(BaseTheme.BUTTON_LINK);
        addResourceButton.addStyleName("floatright paddingtop3");
        addResourceButton.setWidth("20px");
        addResourceButton.setIcon(ICON);
        addResourceButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(final ClickEvent event) {
                try {
                    new ResourceAddViewImpl(resourceProxy, contextId, router).openSubWindow();
                }
                catch (final EscidocClientException e) {
                    mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
                }

            }
        });
        cssLayout.addComponent(nameofPanel);
        cssLayout.addComponent(addResourceButton);
        return cssLayout;
    }

    protected void bindDirectMembersInTheContainer(final Component comptoBind) {
        final VerticalLayout panelLayout = (VerticalLayout) panel.getContent();
        panelLayout.addComponent(comptoBind);
        panelLayout.setExpandRatio(comptoBind, 1.0f);
        panelLayout.setStyleName(Runo.PANEL_LIGHT);
    }

    private boolean hasAccessAddResources() {
        try {
            return repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.CREATE_ITEM).forResource(parentId).permitted();
        }
        catch (final UnsupportedOperationException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
        catch (final EscidocClientException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
        catch (final URISyntaxException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
}
