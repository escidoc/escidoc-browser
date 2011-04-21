package org.escidoc.browser.ui.listeners;

import java.util.Iterator;

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
import de.escidoc.core.resources.common.Relation;
import de.escidoc.core.resources.common.Relations;

@SuppressWarnings("serial")
public class RelationsClickListener implements ClickListener {

    private ItemProxy itemProxy = null;

    private final Window mainWindow;

    private ContainerProxy containerProxy = null;

    private final EscidocServiceLocation escidocServiceLocation;

    private String wndContent = "No information available";

    final private Repository cr;

    /**
     * Container for the ItemProxy case
     * 
     * @param resourceProxy
     * @param mainWindow
     * @param escidocServiceLocation2
     */
    public RelationsClickListener(final ItemProxy resourceProxy,
        final Window mainWindow,
        final EscidocServiceLocation escidocServiceLocation) {
        itemProxy = resourceProxy;
        this.mainWindow = mainWindow;
        this.escidocServiceLocation = escidocServiceLocation;
        cr = new ItemRepository(escidocServiceLocation);

    }

    /**
     * Constructor for the ContainerProxy
     * 
     * @param resourceProxy
     * @param mainWindow
     * @param escidocServiceLocation
     */
    public RelationsClickListener(final ContainerProxy resourceProxy,
        final Window mainWindow,
        final EscidocServiceLocation escidocServiceLocation) {
        containerProxy = resourceProxy;
        this.mainWindow = mainWindow;
        this.escidocServiceLocation = escidocServiceLocation;

        cr = new ContainerRepository(escidocServiceLocation);
    }

    public String getRelations(final Repository cr, final String id)
        throws EscidocClientException {

        final Relations relations = cr.getRelations(id);

        final Iterator<Relation> itr = relations.iterator();
        while (itr.hasNext()) {
            System.out.println(itr.hasNext());
        }
        final String versionHistory = "";

        return versionHistory;

    }

    @Override
    public void buttonClick(final ClickEvent event) {
        final Window subwindow = new Window("Relations");
        subwindow.setWidth("600px");
        subwindow.setModal(true);

        String id = "";
        if (event
            .getButton().getCaption().equals("Container Content Relations")) {
            id = containerProxy.getId();
        }
        else if (event
            .getButton().getCaption().equals("Item Content Relations")) {
            id = itemProxy.getId();
        }
        else {
            throw new RuntimeException("Bug: unexpected event button: "
                + event.getButton());
        }

        try {
            wndContent = getRelations(cr, id);
        }
        catch (final EscidocClientException e) {
            wndContent = "No information";
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