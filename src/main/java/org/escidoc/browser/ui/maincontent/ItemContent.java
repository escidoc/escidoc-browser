package org.escidoc.browser.ui.maincontent;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.VerticalLayout;

public class ItemContent extends CustomLayout {

    public ItemContent(int accordionHeight) {;
    	this.setTemplateName("itemtemplate");
        Embedded e = new Embedded("",
                new ThemeResource("../runo/icons/64/user.png"));
        addComponent(e,"thumbnail");
        
        Label lblmetadata = new Label("001 (image/Jpeg)<hr />"+
        		"THUMBNAIL<br />"+
        		"Metadata <a href=\"/ESDC#Metadata/13\">\"escidoc\"</a>"+
        		"Metadata <a href=\"/ESDC#Metadata/13\">\"exeif\"</a>"+
        		"Md5 990b87dd9014b258cdc85b1bc29d483e",
                Label.CONTENT_RAW);
        addComponent(lblmetadata,"metadata");
        
        Label lbladdmetadata = new Label();
        lbladdmetadata.setCaption("Original (image/jpeg)");
        lbladdmetadata.setIcon(new ThemeResource("../runo/icons/16/document-image.png"));
        
        Label lbladdmetadata2 = new Label();
        lbladdmetadata2.setCaption("WebResolution (image/jpeg)");
        lbladdmetadata2.setIcon(new ThemeResource("../runo/icons/16/document-image.png"));

        addComponent(lbladdmetadata,"addmeta");
        addComponent(lbladdmetadata2,"addmeta2");
    }
}
