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
 * Copyright 2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui.view.helpers;

import java.net.MalformedURLException;

import org.escidoc.browser.controller.UserAccountController;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.internal.UserProxy;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.ChangeUserPassword;
import org.escidoc.browser.ui.listeners.VersionHistoryClickListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class CreateResourceLinksVH {

    private static final Logger LOG = LoggerFactory.getLogger(CreateResourceLinksVH.class);

    private static final ThemeResource ICON_HISTORY = new ThemeResource("images/wpzoom/clock.png");

    private static final ThemeResource ICON_KEY = new ThemeResource("images/wpzoom/key.png");

    private Link l;

    private HorizontalLayout hl;

    private Button editsaveBtn;

    private HorizontalLayout vl;

    private ResourceProperties resourceProperties;

    private Window mainWindow;

    public CreateResourceLinksVH(String url, ResourceProxy resourceProxy, ResourceProperties resourceProperties,
        AbstractComponentContainer componentContainer, Router router) {
        if (resourceProperties.getClass().toString().contains("ResourcePropertiesContainerView")) {
            this.resourceProperties = (ResourcePropertiesContainerView) resourceProperties;
        }
        else if (resourceProperties.getClass().toString().contains("ResourcePropertiesContextView")) {
            this.resourceProperties = (ResourcePropertiesContextView) resourceProperties;
        }
        else {
            this.resourceProperties = (ResourcePropertiesItemView) resourceProperties;
        }
        this.mainWindow = router.getMainWindow();

        vl = buildInitialElements();
        Button edit = showEdit(resourceProxy);

        vl.addComponent(edit);
        if (resourceProxy.getType().equals(ResourceType.ITEM)) {
            vl.setStyleName("permanentLinkItem");
        }
        else {
            vl.setStyleName("permanentLink");
        }
        if (!resourceProxy.getType().equals(ResourceType.CONTEXT)) {

            final Button btnVersionHistory =
                new Button(" ", new VersionHistoryClickListener(resourceProxy, router.getMainWindow(),
                    router.getRepositories()));
            btnVersionHistory.setStyleName(BaseTheme.BUTTON_LINK);
            btnVersionHistory.setDescription("Show Version history in a Pop-up");
            btnVersionHistory.addStyleName("paddingright10");
            btnVersionHistory.setIcon(ICON_HISTORY);
            vl.addComponent(btnVersionHistory);
        }
        try {
            l =
                new Link("",
                    new ExternalResource(url + "?id=" + resourceProxy.getId() + "&type="
                        + resourceProxy.getType().toString() + "&escidocurl="
                        + router.getServiceLocation().getEscidocUrl()));
            l.setDescription(ViewConstants.PERMANENT_LINK);
            l.setIcon(new ThemeResource("images/wpzoom/globe-1.png"));
            vl.addComponent(l);

        }
        catch (MalformedURLException e) {
            mainWindow.showNotification(ViewConstants.COULD_NOT_RETRIEVE_APPLICATION_URL);
            LOG.error(ViewConstants.COULD_NOT_RETRIEVE_APPLICATION_URL + e.getLocalizedMessage());
        }
        componentContainer.addComponent(vl);
    }

    private HorizontalLayout buildInitialElements() {
        hl = new HorizontalLayout();
        hl.setMargin(false);

        return hl;
    }

    private Button showEdit(final ResourceProxy resourceProxy) {
        editsaveBtn = new Button();
        editsaveBtn.setStyleName(BaseTheme.BUTTON_LINK);
        editsaveBtn.setDescription("Edit");
        editsaveBtn.setIcon(new ThemeResource("images/wpzoom/pencil.png"));
        editsaveBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (editsaveBtn.getDescription().equals("Edit")) {
                    editsaveBtn.setDescription("Save");
                    editsaveBtn.setIcon(new ThemeResource("images/wpzoom/save.png"));
                    // Call viewComponent and show the views
                    resourceProperties.showEditableFields();
                }
                else {
                    editsaveBtn.setDescription("Edit");
                    editsaveBtn.setIcon(new ThemeResource("images/wpzoom/pencil.png"));
                    // call viewComponent-controller to do the saves
                    resourceProperties.saveActionWindow();
                    // Messages for successfull operation?
                }

            }
        });
        return editsaveBtn;
    }

    /**
     * Constructor called for the UserAccount only
     * 
     * @param url
     * @param userProxy
     * @param componentContainer
     * @param router
     * @param controller
     */
    public CreateResourceLinksVH(String url, UserProxy userProxy, AbstractComponentContainer componentContainer,
        Router router, UserAccountController controller) {
        buildInitialElements();
        // Checking if has access on changing password here as well
        HorizontalLayout hl = buildInitialElements();
        hl.setStyleName("permanentLink");
        final Button btnVersionHistory = new Button(" ", new ChangeUserPassword(userProxy, router, controller));
        btnVersionHistory.setStyleName(BaseTheme.BUTTON_LINK);
        btnVersionHistory.setDescription("Change user password");
        btnVersionHistory.addStyleName("paddingright10");
        btnVersionHistory.setIcon(ICON_KEY);
        hl.addComponent(btnVersionHistory);
        componentContainer.addComponent(hl);
    }
}
