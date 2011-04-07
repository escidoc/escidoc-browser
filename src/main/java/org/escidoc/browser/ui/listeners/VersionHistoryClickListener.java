package org.escidoc.browser.ui.listeners;

import java.util.Collection;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.ContainerProxy;
import org.escidoc.browser.repository.ContainerRepository;
import org.escidoc.browser.repository.ItemProxy;
import org.escidoc.browser.repository.ItemRepository;
import org.escidoc.browser.repository.Repository;

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

    private EscidocServiceLocation escidocServiceLocation;

    private String wndContent;

    final private Repository cr;

    /**
     * Container for the ItemProxy case
     * 
     * @param resourceProxy
     * @param mainWindow
     */
    public VersionHistoryClickListener(ItemProxy resourceProxy,
        Window mainWindow) {
        this.itemProxy = resourceProxy;
        this.mainWindow = mainWindow;

        cr = new ItemRepository(escidocServiceLocation);

    }

    /**
     * Constructor for the ContainerProxy
     * 
     * @param resourceProxy
     * @param mainWindow
     * @param escidocServiceLocation
     */
    public VersionHistoryClickListener(ContainerProxy resourceProxy,
        Window mainWindow, EscidocServiceLocation escidocServiceLocation) {
        this.containerProxy = resourceProxy;
        this.mainWindow = mainWindow;
        this.escidocServiceLocation = escidocServiceLocation;

        cr = new ContainerRepository(escidocServiceLocation);

    }

    public String getVersionHistory(Repository cr)
        throws EscidocClientException {

        VersionHistory vH = cr.findVersionHistory(containerProxy.getId());

        Collection<Version> versions = vH.getVersions();
        String versionHistory = "";
        for (Version version : versions) {
            versionHistory +=
                "Version: " + version.getVersionNumber() + "<br />";
            versionHistory += "TimeStamp: " + version.getTimestamp() + "<br />";
            versionHistory +=
                "Version Status: " + version.getVersionStatus() + "<br />";
            versionHistory +=
                "Comment: " + version.getComment() + "<br />< hr/>";
            Collection<Event> events = version.getEvents();
            for (Event event : events) {
                versionHistory +=
                    "event :  @xmlID=" + event.getXmlID() + "<br />";
                versionHistory +=
                    "Event Identifier Type: "
                        + event.getEventIdentifier().getEventIdentifierType()
                        + "<br />";
                versionHistory +=
                    "Event Identifier Value: "
                        + event.getEventIdentifier().getEventIdentifierValue()
                        + "<br />";
                versionHistory +=
                    "Event Type: " + event.getEventType() + "<br />";
                versionHistory +=
                    "Event DateTime: " + event.getEventDateTime() + "<br />";
                versionHistory +=
                    "Event Detail: " + event.getEventDetail() + "<br /><hr />";
                versionHistory +=
                    "Linking Agent Identifier: "
                        + event.getLinkingAgentIdentifier() + "<br />";
                versionHistory +=
                    "Linking Agent Identifier Type: "
                        + event
                            .getLinkingAgentIdentifier()
                            .getLinkingAgentIdentifierType() + "<br />";
                versionHistory +=
                    "Linking Agent Identifier Value: "
                        + event
                            .getLinkingAgentIdentifier()
                            .getLinkingAgentIdentifierValue() + "<br /><hr />";

                versionHistory +=
                    "Linking Object Identifier: "
                        + event.getLinkingObjectIdentifier() + "<br />";
                versionHistory +=
                    "Linking Object Identifier Type: "
                        + event
                            .getLinkingObjectIdentifier()
                            .getLinkingObjectIdentifierType() + "<br />";
                versionHistory +=
                    "Linking Object Identifier Value: "
                        + event
                            .getLinkingObjectIdentifier()
                            .getLinkingObjectIdentifierValue() + "<br />";
            }
        }
        return versionHistory;

    }

    @Override
    public void buttonClick(ClickEvent event) {
        Window subwindow = new Window("Version History");
        subwindow.setWidth("600px");
        subwindow.setModal(true);
        try {
            this.wndContent = getVersionHistory(cr);
        }
        catch (EscidocClientException e) {
            this.wndContent = "No information";
        }
        // if
        // (event.getButton().getCaption().equals("Container Version History"))
        // {
        // try {
        // this.wndContent = getVersionHistory(cr);
        // }
        // catch (EscidocClientException e) {
        // this.wndContent = "No information";
        // }
        // }
        // else if
        // (event.getButton().getCaption().equals("Item Version History")) {
        //
        // }
        // else {
        // throw new RuntimeException("Bug: unexpected event button: "
        // + event.getButton());
        // }
        Label msgWindow = new Label(wndContent, Label.CONTENT_RAW);

        subwindow.addComponent(msgWindow);
        if (subwindow.getParent() != null) {
            mainWindow.showNotification("Window is already open");
        }
        else {
            mainWindow.addWindow(subwindow);
        }

    }
}
