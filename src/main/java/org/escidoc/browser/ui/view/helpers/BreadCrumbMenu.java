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

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;

import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.ui.helper.ResourceHierarchy;

import com.google.common.base.Preconditions;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
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
    public BreadCrumbMenu(final AbstractComponentContainer componentContainer, final ResourceProxy resourceProxy) {
        String name;
        if (resourceProxy.getName().length() > 100) {
            name = resourceProxy.getName().substring(0, 100);
        }
        else {
            name = resourceProxy.getName();
        }
        componentContainer.addComponent(new Label("<ul id='crumbs'><li><a href='#'>Home</a></li><li><a href='#'>"
            + name + "... </a></li></ul>", Label.CONTENT_RAW));
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
                try {
                    buf.append("<li><a href='"
                        + this.generateLink(resourceModel.getId(), resourceModel.getType().asLabel().toUpperCase(),
                            escidocServiceLocation.getEscidocUrl().toString()) + "'>" + resourceModel.getName()
                        + "</a></li>");
                }
                catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
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
                buf.append("<li><a href='"
                    + this.generateLink(resourceModel.getId(), resourceModel.getType().asLabel().toUpperCase(),
                        escidocServiceLocation.getEscidocUrl().toString()) + "'>" + resourceModel.getName()
                    + "</a></li>");
            }
            buf.append("<li><a href=''>" + resourceProxy.getName() + "</a></li>");
        }
        catch (final Exception e) {
            buf.append("<li><a href='#'>" + resourceProxy.getContext().getXLinkTitle() + "</a></li>");
        }
        cssLayout.addComponent(new Label(bCstring + buf.toString(), Label.CONTENT_RAW));
    }

    public BreadCrumbMenu(Component component, List<ResourceModel> breadCrumbModel,
        final EscidocServiceLocation escidocServiceLocation) {
        final StringBuffer buf = new StringBuffer();
        Preconditions.checkNotNull(component, "###BreadCrumbModel component is null");
        Preconditions.checkNotNull(breadCrumbModel, "###BreadCrumbModel ref is null");

        for (final ResourceModel resourceModel : breadCrumbModel) {
            buf.append("<li><a href='"
                + this.generateLink(resourceModel.getId(), resourceModel.getType().asLabel().toUpperCase(),
                    escidocServiceLocation.getEscidocUri().toString()) + "'>" + resourceModel.getName() + "</a></li>");
        }
        ((ComponentContainer) component).addComponent(new Label(bCstring + buf.toString() + "<li></ul>",
            Label.CONTENT_RAW));
    }

    private String generateLink(String id, String type, String url) {
        return "?id=" + id + "&type=" + type + "&escidocurl=" + url;
    }
}
