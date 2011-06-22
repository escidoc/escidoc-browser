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

import java.util.ArrayList;
import java.util.Collections;

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceModelFactory;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.repository.ContainerProxy;
import org.escidoc.browser.repository.ContainerRepository;
import org.escidoc.browser.repository.ContextRepository;
import org.escidoc.browser.repository.ItemRepository;
import org.escidoc.browser.repository.internal.ContainerProxyImpl;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.ui.helper.ResourceHierarchy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class BreadCrumbMenu {
    private static final Logger LOG = LoggerFactory.getLogger(BrowserApplication.class);

    String bCstring = "<ul id='crumbs'><li><a href='#'>Home</a></li>";

    /**
     * Context case
     * 
     * @param cssLayout
     * @param resourceProxy
     */
    public BreadCrumbMenu(final CssLayout cssLayout, ResourceProxy resourceProxy) {
        String name;
        if (resourceProxy.getName().length() > 100)
            name = resourceProxy.getName().substring(0, 100);
        else
            name = resourceProxy.getName();
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
     */
    public BreadCrumbMenu(CssLayout cssLayout, ContainerProxy resourceProxy, Window mainWindow,
        EscidocServiceLocation escidocServiceLocation) {

        ResourceHierarchy rs = new ResourceHierarchy(escidocServiceLocation);
        StringBuffer buf = new StringBuffer();
        try {
            ArrayList<ResourceModel> hierarchy = rs.getHierarchy(resourceProxy.getId());
            Collections.reverse(hierarchy);

            for (ResourceModel resourceModel : hierarchy) {
                // bCstring +=
                // "<li><a href='/browser/mainWindow?tab=" + resourceModel.getId() + "&type="
                // + resourceModel.getType() + "&escidocurl=" + escidocServiceLocation.getEscidocUri()
                // + "' target='_blank'>" + resourceModel.getName() + "</a></li>";
                buf.append("<li><a href='#'>" + resourceModel.getName() + "</a></li>");
            }
        }
        catch (EscidocClientException e) {
            // bCstring +=
            // "<li><a href='/browser/mainWindow?tab=" + resourceProxy.getContext().getObjid()
            // + "&type=CONTEXT&escidocurl=" + escidocServiceLocation + "' target='_blank'>"
            // + resourceProxy.getContext().getXLinkTitle() + "</a></li>";
            buf.append("<li><a href='#'>" + resourceProxy.getContext().getXLinkTitle() + "</a></li>");
        }
        cssLayout.addComponent(new Label(buf.toString() + "<li>" + resourceProxy.getName() + "</li></ul>",
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
    public BreadCrumbMenu(CssLayout cssLayout, ItemProxyImpl resourceProxy, Window mainWindow,
        EscidocServiceLocation escidocServiceLocation) {
        String bCstring = "<ul id='crumbs'><li><a href='#'>Home Item</a></li>";
        ResourceHierarchy rs = new ResourceHierarchy(escidocServiceLocation);

        StringBuffer buf = new StringBuffer();
        try {
            String parentId = rs.getReturnParentOfItem(resourceProxy.getId()).getId();
            ArrayList<ResourceModel> hierarchy = rs.getHierarchy(parentId);
            Collections.reverse(hierarchy);
            for (ResourceModel resourceModel : hierarchy) {
                // bCstring +=
                // "<li><a href='/browser/mainWindow?tab=" + resourceModel.getId() + "&type="
                // + resourceModel.getType() + "&escidocurl=" + escidocServiceLocation.getEscidocUri()
                // + "' target='_blank'>" + resourceModel.getName() + "</a></li>";
                buf.append("<li><a href='#'>" + resourceModel.getName() + "</a></li>");
            }
            ResourceModelFactory resourceFactory =
                new ResourceModelFactory(new ItemRepository(escidocServiceLocation), new ContainerRepository(
                    escidocServiceLocation), new ContextRepository(escidocServiceLocation));
            ContainerProxyImpl containerParent =
                (ContainerProxyImpl) resourceFactory.find(parentId, ResourceType.CONTAINER);
            buf.append("<li><a href='#'>" + containerParent.getName() + "</a></li>");
        }
        catch (Exception e) {
            // bCstring +=
            // "<li><a href='/browser/mainWindow?tab=" + resourceProxy.getContext().getObjid()
            // + "&type=CONTEXT&escidocurl=" + escidocServiceLocation.getEscidocUri() + "' target='_blank'>"
            // + resourceProxy.getContext().getXLinkTitle() + "</a></li>";
            buf.append("<li><a href='#'>" + resourceProxy.getContext().getXLinkTitle() + "</a></li>");
        }

        // cssLayout
        // .addComponent(new Label(
        // bCstring
        // + "<li>"
        // + resourceProxy.getName()
        // + "</li><a href=\"/browser/mainWindow?tab="
        // + resourceProxy.getId()
        // + "&type=ITEM&escidocurl="
        // + escidocServiceLocation.getEscidocUri()
        // +
        // "\" target=\"_blank\" alt=\"Permanent Link to resource\" class=\"floatright\"><img src=\"VAADIN/themes/myTheme/images/anchor.png\"/></a></ul>",
        // Label.CONTENT_RAW));
        cssLayout.addComponent(new Label(bCstring + buf.toString(), Label.CONTENT_RAW));
    }
}
