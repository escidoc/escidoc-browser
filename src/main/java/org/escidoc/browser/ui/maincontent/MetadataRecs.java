package org.escidoc.browser.ui.maincontent;

import java.util.Iterator;
import java.util.List;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.ContainerProxy;

import com.vaadin.Application;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;

import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.versionhistory.Version;

public class MetadataRecs{
    private int height;

    private ContainerProxy resourceProxy;

    private Application app;

    public MetadataRecs(ResourceProxy resourceProxy, int innerelementsHeight, Application application) {
        this.height = innerelementsHeight;
        this.resourceProxy = (ContainerProxy)resourceProxy;
        this.app= application;
    }

    public Accordion asAccord() {
        Accordion metadataRecs = new Accordion();
        metadataRecs.setSizeFull();

        Label l1 = lblMetadaRecs();
        Label l2 = lblRelations();
        Panel pnl = lblAddtionalResources();
        

        // Add the components as tabs in the Accordion.
        metadataRecs.addTab(l1, "Metadata Records", null);
        metadataRecs.addTab(l2, "Relations", null);
        metadataRecs.addTab(pnl, "Additional Resources", null);
        return metadataRecs;
    }

    private Panel lblAddtionalResources() {

        Button btnVersion = new Button("Version History",this,"versionHonClick");

        
        Panel pnl = new Panel();
        pnl.setHeight(height + "px");
        pnl.addComponent(btnVersion);   
        return pnl;
    }

    private Label lblRelations() {
        Label l2 = new Label("Relations - Not implemented in JCLib");
        l2.setHeight(height + "px");
        return l2;
    }

    private Label lblMetadaRecs() {
        Iterator itr = resourceProxy.getMedataRecords().iterator();
        String mtRecords = "";
        while (itr.hasNext()) {
            mtRecords += "<a href='/MISSING'>" + itr.next() + "</a><br />";
        }

        Label l1 = new Label(mtRecords, Label.CONTENT_RAW);
        l1.setHeight(height + "px");
        return l1;
    }
    
    public void versionHonClick(Button.ClickEvent event) {
        Window subwindow = new Window("A modal subwindow");
        subwindow.setModal(true);
        if (subwindow.getParent() != null) {
            // window is already showing
            app.getMainWindow().showNotification(
                    "Window is already open");
        } else {
            // Open the subwindow by adding it to the parent
            // window
            app.getMainWindow().addWindow(subwindow);
        }


       
    }

}
