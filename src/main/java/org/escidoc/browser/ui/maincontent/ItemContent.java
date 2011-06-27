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

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.StagingRepository;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.ui.dnd.DragAndDropFileUpload;

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

@SuppressWarnings("serial")
public class ItemContent extends CustomLayout {

    private final Panel panelComponent = new Panel();

    private final ItemProxyImpl itemProxy;

    private final EscidocServiceLocation serviceLocation;

    private final StagingRepository stagingRepository;

    public ItemContent(final StagingRepository stagingRepository, final ItemProxyImpl itemProxy,
        final EscidocServiceLocation serviceLocation, final Window mainWindow) {
        Preconditions.checkNotNull(stagingRepository, "stagingRepository is null: %s", stagingRepository);
        Preconditions.checkNotNull(itemProxy, "resourceProxy is null.");
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null.");
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);

        this.stagingRepository = stagingRepository;
        this.serviceLocation = serviceLocation;
        this.itemProxy = itemProxy;

        initView();
    }

    private void initView() {
        setTemplateName("itemtemplate");
        buildComponentPanel();

        if (hasComponents()) {
            buildComponents();
        }

        addComponent(panelComponent, "components");
    }

    private boolean hasComponents() {
        return itemProxy.hasComponents().booleanValue();
    }

    private void buildComponents() {
        for (final Component component : itemProxy.getElements()) {
            buildComponentElement(component);
        }
        panelComponent.addComponent(new DragAndDropFileUpload(stagingRepository));
    }

    private void buildComponentPanel() {
        panelComponent.addStyleName(Runo.PANEL_LIGHT);
    }

    private void buildComponentElement(final Component comp) {
        panelComponent.addComponent(createEmbeddedImage(comp));
        panelComponent.addComponent(createDownloadLink(comp));
        panelComponent.addComponent(createLabelForMetadata(comp));
    }

    private Link createDownloadLink(final Component comp) {
        final Link link =
            new Link("Download File", new ExternalResource(serviceLocation.getEscidocUri()
                + comp.getContent().getXLinkHref()));
        link.setIcon(new ThemeResource("images/download.png"));
        return link;
    }

    private Label createLabelForMetadata(final Component comp) {
        final Label labelMetadata =
            new Label(comp.getContent().getXLinkTitle() + "<br />" + comp.getProperties().getVisibility() + "<br />"
                + comp.getProperties().getChecksumAlgorithm() + " " + comp.getProperties().getChecksum(),
                Label.CONTENT_RAW);
        return labelMetadata;
    }

    private Embedded createEmbeddedImage(final Component comp) {
        return new Embedded("", new ThemeResource("images/filetypes/" + getFileType(comp) + ".png"));
    }

    private String getFileType(final Component itemProperties) {
        final String mimeType = itemProperties.getProperties().getMimeType();
        final String[] last = mimeType.split("/");
        final String lastOne = last[last.length - 1];
        return lastOne;
    }
}