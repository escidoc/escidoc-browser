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
/**
 * 
 */
package org.escidoc.browser.layout;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;

import java.net.URISyntaxException;

import de.escidoc.core.client.exceptions.EscidocClientException;

/**
 * @author ajb More should be added here
 */
@SuppressWarnings("serial")
public abstract class LayoutDesign extends VerticalLayout {

    public abstract void init(
        Window mainWindow, EscidocServiceLocation serviceLocation, BrowserApplication app, Repositories repositories,
        Router router) throws EscidocClientException, UnsupportedOperationException, URISyntaxException;

    public abstract void openView(Component component, String title);

    public abstract void openViewByReloading(Component component, String title);

    public abstract void closeView(ResourceModel model, ResourceModel parent, Object sender);

    public abstract TreeDataSource getTreeDataSource();

    public abstract Component getViewContainer();

}