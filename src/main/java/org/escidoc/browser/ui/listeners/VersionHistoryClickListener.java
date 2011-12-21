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
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
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

    private static final Logger LOG = LoggerFactory.getLogger(VersionHistoryClickListener.class);

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
     * @param repositories
     * @param escidocServiceLocation2
     */
    public VersionHistoryClickListener(final ItemProxy resourceProxy, final Window mainWindow,
        final Repositories repositories) {
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        itemProxy = resourceProxy;
        this.mainWindow = mainWindow;
        repository = repositories.item();
    }

    /**
     * Constructor for the ContainerProxy
     * 
     * @param resourceProxy
     * @param mainWindow
     * @param escidocServiceLocation
     * @param repositories
     */
    public VersionHistoryClickListener(final ContainerProxy resourceProxy, final Window mainWindow,
        final EscidocServiceLocation escidocServiceLocation, final Repositories repositories) {
        Preconditions.checkNotNull(resourceProxy, "resource is null.");
        Preconditions.checkNotNull(mainWindow, "mainWindow is null.");
        Preconditions.checkNotNull(repositories, "repositories is null.");
        containerProxy = resourceProxy;
        this.mainWindow = mainWindow;
        repository = repositories.container();
    }

    public String getVersionHistory(final Repository cr, final String id) throws EscidocClientException {
        final VersionHistory vH = cr.getVersionHistory(id);
        final Collection<Version> versions = vH.getVersions();
        String versionHistory = "";
        for (final Version version : versions) {
            versionHistory += "<strong>Version: " + version.getVersionNumber() + "</strong><br />";
            versionHistory += "TimeStamp: " + version.getTimestamp() + "<br />";
            versionHistory += "Version Status: " + version.getVersionStatus() + "<br />";
            versionHistory += "Comment: " + version.getComment() + "<br /><hr/>";
            final Collection<Event> events = version.getEvents();
            for (final Event event : events) {
                versionHistory +=
                    "Event Identifier Type: " + event.getEventIdentifier().getEventIdentifierType() + "<br />";
                versionHistory +=
                    "Event Identifier Value: " + event.getEventIdentifier().getEventIdentifierValue() + "<br />";
                versionHistory += "Event Type: " + event.getEventType() + "<br />";
                versionHistory += "Event DateTime: " + event.getEventDateTime() + "<br />";
                versionHistory += "Event Detail: " + event.getEventDetail() + "<br /><hr />";
                versionHistory += "Linking Agent Identifier: " + "<br />";
                versionHistory +=
                    "Linking Agent Identifier Type: "
                        + event.getLinkingAgentIdentifier().getLinkingAgentIdentifierType() + "<br />";
                versionHistory +=
                    "Linking Agent Identifier Value: "
                        + event.getLinkingAgentIdentifier().getLinkingAgentIdentifierValue() + "<br /><hr />";

                versionHistory += "Linking Object Identifier: " + "<br />";
                versionHistory +=
                    "Linking Object Identifier Type: "
                        + event.getLinkingObjectIdentifier().getLinkingObjectIdentifierType() + "<br />";
                versionHistory +=
                    "Linking Object Identifier Value: "
                        + event.getLinkingObjectIdentifier().getLinkingObjectIdentifierValue() + "<br /><hr />";
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
        if (event.getButton().getCaption().equals("Container Version History")
            || event.getButton().getCaption().equals(" Has previous version")) {
            id = containerProxy.getId();
        }
        else if (event.getButton().getCaption().equals("Item Version History")
            || event.getButton().getCaption().equals(" Has previous versions")) {
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