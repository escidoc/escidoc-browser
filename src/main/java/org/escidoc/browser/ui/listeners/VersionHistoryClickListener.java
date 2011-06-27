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
package org.escidoc.browser.ui.listeners;

import java.util.Collection;

import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.repository.internal.ContainerRepository;
import org.escidoc.browser.repository.internal.ItemRepository;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.versionhistory.Event;
import de.escidoc.core.resources.common.versionhistory.Version;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;

@SuppressWarnings("serial")
public class VersionHistoryClickListener implements ClickListener {

    private ItemProxy itemProxy;

    private final Window mainWindow;

    private ContainerProxy containerProxy;

    private String wndContent;

    final private Repository repository;

    /**
     * Container for the ItemProxy case
     * 
     * @param resourceProxy
     * @param mainWindow
     * @param escidocServiceLocation2
     */
    public VersionHistoryClickListener(final ItemProxy resourceProxy, final Window mainWindow,
        final EscidocServiceLocation escidocServiceLocation) {
        itemProxy = resourceProxy;
        this.mainWindow = mainWindow;
        repository = new ItemRepository(escidocServiceLocation);
    }

    /**
     * Constructor for the ContainerProxy
     * 
     * @param resourceProxy
     * @param mainWindow
     * @param escidocServiceLocation
     */
    public VersionHistoryClickListener(final ContainerProxy resourceProxy, final Window mainWindow,
        final EscidocServiceLocation escidocServiceLocation) {
        containerProxy = resourceProxy;
        this.mainWindow = mainWindow;
        repository = new ContainerRepository(escidocServiceLocation);
    }

    public String getVersionHistory(final Repository cr, final String id) throws EscidocClientException {
        final VersionHistory vH = cr.getVersionHistory(id);
        final Collection<Version> versions = vH.getVersions();
        String versionHistory = "";
        for (final Version version : versions) {
            versionHistory += "Version: " + version.getVersionNumber() + "<br />";
            versionHistory += "TimeStamp: " + version.getTimestamp() + "<br />";
            versionHistory += "Version Status: " + version.getVersionStatus() + "<br />";
            versionHistory += "Comment: " + version.getComment() + "<br />< hr/>";
            final Collection<Event> events = version.getEvents();
            for (final Event event : events) {
                versionHistory += "event :  @xmlID=" + event.getXmlID() + "<br />";
                versionHistory +=
                    "Event Identifier Type: " + event.getEventIdentifier().getEventIdentifierType() + "<br />";
                versionHistory +=
                    "Event Identifier Value: " + event.getEventIdentifier().getEventIdentifierValue() + "<br />";
                versionHistory += "Event Type: " + event.getEventType() + "<br />";
                versionHistory += "Event DateTime: " + event.getEventDateTime() + "<br />";
                versionHistory += "Event Detail: " + event.getEventDetail() + "<br /><hr />";
                versionHistory += "Linking Agent Identifier: " + event.getLinkingAgentIdentifier() + "<br />";
                versionHistory +=
                    "Linking Agent Identifier Type: "
                        + event.getLinkingAgentIdentifier().getLinkingAgentIdentifierType() + "<br />";
                versionHistory +=
                    "Linking Agent Identifier Value: "
                        + event.getLinkingAgentIdentifier().getLinkingAgentIdentifierValue() + "<br /><hr />";

                versionHistory += "Linking Object Identifier: " + event.getLinkingObjectIdentifier() + "<br />";
                versionHistory +=
                    "Linking Object Identifier Type: "
                        + event.getLinkingObjectIdentifier().getLinkingObjectIdentifierType() + "<br />";
                versionHistory +=
                    "Linking Object Identifier Value: "
                        + event.getLinkingObjectIdentifier().getLinkingObjectIdentifierValue() + "<br />";
            }
        }
        return versionHistory;

    }

    @Override
    public void buttonClick(final ClickEvent event) {
        final Window subwindow = new Window("Version History");
        subwindow.setWidth("600px");
        subwindow.setModal(true);
        String id = "";
        if (event.getButton().getCaption().equals("Container Version History")) {
            id = containerProxy.getId();
        }
        else if (event.getButton().getCaption().equals("Item Version History")) {
            id = itemProxy.getId();
        }
        else {
            throw new RuntimeException("Bug: unexpected event button: " + event.getButton());
        }

        try {

            wndContent = getVersionHistory(repository, id);
        }
        catch (final EscidocClientException e) {

            wndContent = "No information ?" + e.getMessage();
        }

        final Label msgWindow = new Label(wndContent, Label.CONTENT_RAW);

        subwindow.addComponent(msgWindow);
        if (subwindow.getParent() != null) {
            mainWindow.showNotification("Window is already open");
        }
        else {
            mainWindow.addWindow(subwindow);
        }
    }
}