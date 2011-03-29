package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.model.ResourceProxy;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class ContextAddInfo {
    private int height;

    private ResourceProxy resourceProxy;

    public ContextAddInfo(ResourceProxy resourceProxy, int innerelementsHeight) {
        this.height = innerelementsHeight;
        this.resourceProxy = resourceProxy;
    }

    public Panel addPanels() {

        Panel mainpnl = new Panel();
        mainpnl.setHeight("100%");
        mainpnl.setWidth("100%");

        int elementHeight = this.height / 4;
        Panel orgUnit = new Panel("Organizational Unit");
        orgUnit.setWidth("100%");
        orgUnit.setHeight(elementHeight + "px");
        Label cnt =
            new Label("<a href='/ESCD/#Unit/FIZ'>FIZ</a><br />"
                + "<a href='/ESCD/#Unit/JUG'>Java User Group</a>",
                Label.CONTENT_RAW);
        orgUnit.addComponent(cnt);

        Panel admDescriptors = new Panel("Admin Descriptors");
        admDescriptors.setWidth("100%");
        admDescriptors.setHeight(elementHeight + "px");
        Label lblAdmDescriptor =
            new Label("<a href='/ESCD/#Unit/FIZ'>PubMan Descriptor</a><br />"
                + "<a href='/ESCD/#Unit/JUG'>Java User Group</a>",
                Label.CONTENT_RAW);
        admDescriptors.addComponent(lblAdmDescriptor);

        Panel relations = new Panel("Relations");
        relations.setWidth("100%");
        relations.setHeight(elementHeight + "px");
        Label lblrelations =
            new Label(
                "isRelatedTo <a href='/ESCD/#Context/123'>Other Context</a><br />",
                Label.CONTENT_RAW);
        relations.addComponent(lblrelations);

        Panel resources = new Panel("Resources");
        resources.setWidth("100%");
        resources.setHeight(elementHeight + "px");
        Label lblresources =
            new Label(
                "<a href='/ESCD/#Resources/id'>Members Filtered</a><br />",
                Label.CONTENT_RAW);
        resources.addComponent(lblresources);

        mainpnl.addComponent(orgUnit);
        mainpnl.addComponent(admDescriptors);
        mainpnl.addComponent(relations);
        mainpnl.addComponent(resources);

        return mainpnl;

    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
