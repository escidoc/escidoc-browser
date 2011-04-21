package org.escidoc.browser.ui.listeners;

import javax.xml.transform.TransformerException;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.om.context.AdminDescriptor;

public class ContextAdminDescriptorsClickListener implements ClickListener {

    private AdminDescriptor adminDescriptor = null;

    private String content;

    private String wndname;

    private final Window mainWindow;

    private OrganizationalUnitRef organizationalUnitRef = null;

    public ContextAdminDescriptorsClickListener(AdminDescriptor adminDescriptor, Window mainWindow) {
        this.adminDescriptor = adminDescriptor;
        this.mainWindow = mainWindow;
        try {
            this.wndname = adminDescriptor.getName();
            this.content = adminDescriptor.getContentAsString();
        }
        catch (TransformerException e) {
            this.content = "No value is set";
        }
    }

    public ContextAdminDescriptorsClickListener(OrganizationalUnitRef organizationalUnitRef, Window mainWindow) {
        this.mainWindow = mainWindow;
        this.organizationalUnitRef = organizationalUnitRef;

        this.wndname = organizationalUnitRef.getXLinkTitle();
        this.content = organizationalUnitRef.getObjid();
    }

    @Override
    public void buttonClick(ClickEvent event) {
        Window subwindow = new Window(wndname);
        subwindow.setWidth("600px");
        subwindow.setModal(true);

        Label msgWindow = new Label(content, Label.CONTENT_RAW);

        subwindow.addComponent(msgWindow);
        if (subwindow.getParent() != null) {
            mainWindow.showNotification("Window is already open");
        }
        else {
            mainWindow.addWindow(subwindow);
        }
    }

}
