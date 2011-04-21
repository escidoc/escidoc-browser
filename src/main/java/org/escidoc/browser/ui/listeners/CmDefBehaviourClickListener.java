package org.escidoc.browser.ui.listeners;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.ContainerProxy;
import org.escidoc.browser.repository.ItemProxy;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class CmDefBehaviourClickListener implements ClickListener {

    private final Window mainWindow;

    public CmDefBehaviourClickListener(final ContainerProxy resourceProxy, final Window mainWindow,
        final EscidocServiceLocation escidocServiceLocation) {
        this.mainWindow = mainWindow;
    }

    public CmDefBehaviourClickListener(final ItemProxy resourceProxy, final Window mainWindow,
        final EscidocServiceLocation escidocServiceLocation) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void buttonClick(final ClickEvent event) {
        final Window subwindow = new Window("Relations");
        subwindow.setWidth("600px");
        subwindow.setModal(true);

        final Label msgWindow = new Label("Not implemented yet", Label.CONTENT_RAW);

        subwindow.addComponent(msgWindow);
        if (subwindow.getParent() != null) {
            mainWindow.showNotification("Window is already open");
        }
        else {
            mainWindow.addWindow(subwindow);
        }
    }

}
