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

import java.util.Collections;
import java.util.List;

import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.ui.helper.ResourceHierarchy;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class BreadCrumbMenu {

    String bCstring = "<ul id='crumbs'><li><a href='#'>Home</a></li>";

    /**
     * Context case
     * 
     * @param cssLayout
     * @param resourceProxy
     * @param repositories
     */
    public BreadCrumbMenu(final CssLayout cssLayout, final ResourceProxy resourceProxy) {
        String name;
        if (resourceProxy.getName().length() > 100) {
            name = resourceProxy.getName().substring(0, 100);
        }
        else {
            name = resourceProxy.getName();
        }
        cssLayout.addComponent(new Label("<ul id='crumbs'><li><a href='#'>Home</a></li><li><a href='#'>" + name
            + "... </a></li></ul>", Label.CONTENT_RAW));
    }

    /**
     * This is a Breadcrumb for the ContainerView
     * 
     * @param cssLayout
     * @param resourceProxy
     * @param mainWindow
     * @param escidocServiceLocation
     * @param repositories
     */
    public BreadCrumbMenu(final CssLayout cssLayout, final ContainerProxy resourceProxy, final Window mainWindow,
        final EscidocServiceLocation escidocServiceLocation, final Repositories repositories) {
        final ResourceHierarchy rs = new ResourceHierarchy(escidocServiceLocation, repositories);
        final StringBuffer buf = new StringBuffer();
        try {
            final List<ResourceModel> hierarchy = rs.getHierarchy(resourceProxy);
            Collections.reverse(hierarchy);
            for (final ResourceModel resourceModel : hierarchy) {
                buf.append("<li><a href='#'>" + resourceModel.getName() + "</a></li>");
            }
        }
        catch (final EscidocClientException e) {
            buf.append("<li><a href='#'>" + resourceProxy.getContext().getXLinkTitle() + "</a></li>");
        }
        cssLayout.addComponent(new Label(bCstring + buf.toString() + "<li>" + resourceProxy.getName() + "</li></ul>",
            Label.CONTENT_RAW));
    }

    /**
     * BreadCrumb for the Item
     * 
     * @param cssLayout
     * @param resourceProxy
     * @param mainWindow
     * @param escidocServiceLocation
     */
    public BreadCrumbMenu(final CssLayout cssLayout, final ItemProxyImpl resourceProxy, final Window mainWindow,
        final EscidocServiceLocation escidocServiceLocation, final Repositories repositories) {

        final String bCstring = "<ul id='crumbs'><li><a href='#'>Home Item</a></li>";
        final ResourceHierarchy rs = new ResourceHierarchy(escidocServiceLocation, repositories);

        final StringBuffer buf = new StringBuffer();
        try {
            final List<ResourceModel> hierarchy = rs.getHierarchy(resourceProxy);
            Collections.reverse(hierarchy);
            for (final ResourceModel resourceModel : hierarchy) {
                buf.append("<li><a href='#'>" + resourceModel.getName() + "</a></li>");
            }
            buf.append("<li><a href='#'>" + resourceProxy.getName() + "</a></li>");
        }
        catch (final Exception e) {
            buf.append("<li><a href='#'>" + resourceProxy.getContext().getXLinkTitle() + "</a></li>");
        }
        cssLayout.addComponent(new Label(bCstring + buf.toString(), Label.CONTENT_RAW));
    }
}
