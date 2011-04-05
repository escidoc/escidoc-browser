package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.repository.ItemProxyImpl;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;

import de.escidoc.core.resources.om.item.component.Component;

public class ItemContent extends CustomLayout {

    private ItemProxyImpl itemproxy;

    public ItemContent(int accordionHeight, ItemProxyImpl resourceProxy) {
        this.setTemplateName("itemtemplate");
        this.itemproxy=resourceProxy;
        if (itemproxy.hasComponents()){
            buildMainElement();


            Label lbladdmetadata = new Label();
            lbladdmetadata.setCaption("Original (image/jpeg)");
            lbladdmetadata.setIcon(new ThemeResource(
            "../runo/icons/16/document-image.png"));

            Label lbladdmetadata2 = new Label();
            lbladdmetadata2.setCaption("WebResolution (image/jpeg)");
            lbladdmetadata2.setIcon(new ThemeResource(
            "../runo/icons/16/document-image.png"));

            addComponent(lbladdmetadata, "addmeta");
            addComponent(lbladdmetadata2, "addmeta2");
        }
    }

    private void buildMainElement()
    {
        Component itemProperties = itemproxy.getFirstelementProperties();

        //get the file type from the mime:
        String mimeType = itemProperties.getProperties().getMimeType();
        String[] last = mimeType.split("/");
        String lastOne = last[last.length-1];

        Embedded e =
            new Embedded("", new ThemeResource("../myTheme/images/filetypes/"+lastOne+".png"));
        addComponent(e, "thumbnail");
        Label lblmetadata =
            new Label( itemProperties.getXLinkTitle()+" ("+itemProperties.getProperties().getMimeType()+")<hr />" + "<br />"
                + "Metadata <a href=\"/ESDC#Metadata/13\">\"escidoc\"</a>"
                + "Metadata <a href=\"/ESDC#Metadata/13\">\"exeif\"</a>"
                + "Md5 990b87dd9014b258cdc85b1bc29d483e", Label.CONTENT_RAW);
        addComponent(lblmetadata, "metadata");  
    }
}
