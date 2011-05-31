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
 * Copyright ${year} Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui.maincontent;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.om.item.component.Components;

@SuppressWarnings("serial")
public class ItemContent extends CustomLayout {

    private static final Logger LOG = LoggerFactory.getLogger(ItemContent.class);

    private final ItemProxyImpl itemproxy;

    private final EscidocServiceLocation serviceLocation;

    private final Window mainWindow;

    Panel pnlComponents;

    public ItemContent(final ItemProxyImpl resourceProxy, EscidocServiceLocation serviceLocation,
        final Window mainWindow) {

        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null.");
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null.");

        this.serviceLocation = serviceLocation;
        this.mainWindow = mainWindow;

        setTemplateName("itemtemplate");
        buildComponentPanel();

        itemproxy = resourceProxy;
        if (itemproxy.hasComponents()) {
            buildComponents();
        }
        addComponent(pnlComponents, "components");
    }

    /**
     * 
     */
    private void buildComponents() {
        final Components itemComponents = itemproxy.getElements();
        for (final Component component : itemComponents) {
            buildComponentElement(component);
        }
    }

    private void buildComponentPanel() {
        pnlComponents = new Panel();
        pnlComponents.addStyleName(Runo.PANEL_LIGHT);
    }

    private void buildComponentElement(Component comp) {
        final Component itemProperties = comp;

        // get the file type from the mime:
        final String mimeType = itemProperties.getProperties().getMimeType();
        final String[] last = mimeType.split("/");
        final String lastOne = last[last.length - 1];

        final Embedded e = new Embedded("", new ThemeResource("images/filetypes/" + lastOne + ".png"));
        pnlComponents.addComponent(e);

        final Label lblmetadata =
            new Label(itemProperties.getContent().getXLinkTitle() + "<br />"
                + itemProperties.getProperties().getVisibility() + "<br />"
                + itemProperties.getProperties().getChecksumAlgorithm() + " "
                + itemProperties.getProperties().getChecksum(), Label.CONTENT_RAW);
        Link lnk =
            new Link("Download File", new ExternalResource(serviceLocation.getEscidocUri()
                + itemProperties.getContent().getXLinkHref()));
        lnk.setIcon(new ThemeResource("images/download.png"));
        pnlComponents.addComponent(lnk);
        pnlComponents.addComponent(lblmetadata);
    }

    private String getMimeType(String url) throws IOException {
        URL u = new URL(url);
        URLConnection uc = null;
        uc = u.openConnection();
        System.out.println(uc.getContentLength());

        return uc.getContentType();
    }

}
