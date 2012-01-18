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
package org.escidoc.browser.controller;

import com.google.common.base.Preconditions;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window.Notification;

import org.escidoc.browser.layout.LayoutDesign;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;

import de.escidoc.core.client.exceptions.EscidocClientException;

public abstract class Controller {

    protected Component view;

    private final Repositories repositories;

    private final ResourceProxy resourceProxy;

    private final Router router;

    private final LayoutDesign layout;

    private String resourceName;

    public Controller(final Repositories repositories, final Router router, final ResourceProxy resourceProxy) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(router, "router is null: %s", router);
        this.repositories = repositories;
        this.router = router;
        this.resourceProxy = resourceProxy;
        this.layout = router.getLayout();
        setResourceName(resourceProxy.getName() + "#" + resourceProxy.getId());
    }

    public abstract void createView();

    protected Repositories getRepositories() {
        return repositories;
    }

    protected Router getRouter() {
        return router;
    }

    protected ResourceProxy getResourceProxy() {
        return resourceProxy;
    }

    public void showView() {
        Preconditions.checkNotNull(view, "view is null: %s", view);
        Preconditions.checkNotNull(layout, "layout is null: %s", layout);
        layout.openView(view, getResourceName());
    }

    public void showViewByReloading() {
        Preconditions.checkNotNull(view, "view is null: %s", view);
        Preconditions.checkNotNull(layout, "layout is null: %s", layout);
        layout.openViewByReloading(view, getResourceName());
    }

    // TODO There has to be a method from the Router to call a refresh on the view
    /**
     * Used for syncing resources etc
     */
    public void refreshView() {
        try {
            router.show(resourceProxy, true);
        }
        catch (final EscidocClientException e) {
            showError("Could not refresh view" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(final String name) {
        this.resourceName = name;
    }

    /**
     * Show error message to the user
     * 
     * @param errorMessage
     *            message content
     */
    protected void showError(final String errorMessage) {
        Preconditions.checkNotNull(errorMessage, "Errormessage is null");
        this.router.getApp().getMainWindow().showNotification("Error", errorMessage, Notification.TYPE_ERROR_MESSAGE);
    }

    /**
     * Show warning message to the user
     * 
     * @param warningMessage
     *            message content
     */
    protected void showWarning(final String warningMessage) {
        Preconditions.checkNotNull(warningMessage, "Warningmessage is null");
        this.router
            .getApp().getMainWindow().showNotification("Warning", warningMessage, Notification.TYPE_WARNING_MESSAGE);
    }

    /**
     * Show tray panel message to the user
     * 
     * @param trayTitle
     *            message header
     * @param trayMessage
     *            message content
     */
    protected void showTrayMessage(final String trayTitle, final String trayMessage) {
        Preconditions.checkNotNull(trayMessage, "Traymessage is null");
        Preconditions.checkNotNull(trayTitle, "trayTitle is null");
        this.router
            .getApp().getMainWindow().showNotification(trayTitle, trayMessage, Notification.TYPE_TRAY_NOTIFICATION);
    }

}