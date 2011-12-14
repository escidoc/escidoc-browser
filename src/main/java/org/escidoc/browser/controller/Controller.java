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

import org.escidoc.browser.layout.LayoutDesign;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window.Notification;

public abstract class Controller {

    protected Component view;

    private String resouceName;

    private final Repositories repositories;

    private final Router router;

    private final ResourceProxy resourProxy;

    public Controller(final Repositories repositories, final Router router, final ResourceProxy resourceProxy) {
        this.repositories = repositories;
        this.router = router;
        this.resourProxy = resourceProxy;
    }

    public void showView(final LayoutDesign layout) {
        layout.openView(this.view, this.getResourceName());
    }

    public void showViewByReloading(final LayoutDesign layout) {
        layout.openViewByReloading(this.view, this.getResourceName());
    }

    public String getResourceName() {
        return resouceName;
    }

    public void setResourceName(final String name) {
        this.resouceName = name;
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