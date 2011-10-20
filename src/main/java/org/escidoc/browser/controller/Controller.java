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
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

public abstract class Controller {
    protected Component view;

    String resouceName;

    public abstract void init(
        final EscidocServiceLocation serviceLocation, final Repositories repositories, final Router mainSite,
        final ResourceProxy resourceProxy, final Window mainWindow, final CurrentUser currentUser);

    public void showView(final LayoutDesign layout) {
        layout.openView(this.view, this.getResourceName());
    }

    public String getResourceName() {
        return resouceName;
    }

    // FIX should be changed to: void setResourceName(String name)
    public void getResourceName(String name) {
        this.resouceName = name;
    }

}
