package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.repository.ItemProxyImpl;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;

import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.om.item.component.Components;

public class ItemContent extends CustomLayout {

    private final ItemProxyImpl itemproxy;

    public ItemContent(int accordionHeight, ItemProxyImpl resourceProxy) {
        this.setTemplateName("itemtemplate");
        this.itemproxy = resourceProxy;
        if (itemproxy.hasComponents()) {
            buildMainElement();
            buildOtherComponents();
        }
    }

    /**
     * 
     */
    private void buildOtherComponents() {
        Components itemComponents = itemproxy.getElements();
        for (Component component : itemComponents) {
            buildLabel(component.getProperties().getFileName());
        }
    }

    /**
     * @param itemContent
     */
    private void buildLabel(String fileName) {
        System.out.println("Label u thirra per " + fileName);
        Label lbladdmetadata2 = new Label();
        lbladdmetadata2.setCaption(fileName + "(image/jpeg)");
        lbladdmetadata2.setIcon(new ThemeResource(
            "../runo/icons/16/document-image.png"));
        this.addComponent(lbladdmetadata2);
    }

    private void buildMainElement() {
        Component itemProperties = itemproxy.getFirstelementProperties();

        // get the file type from the mime:
        String mimeType = itemProperties.getProperties().getMimeType();
        String[] last = mimeType.split("/");
        String lastOne = last[last.length - 1];

        Embedded e =
            new Embedded("", new ThemeResource("../myTheme/images/filetypes/"
                + lastOne + ".png"));
        addComponent(e, "thumbnail");
        Label lblmetadata =
            new Label(itemProperties.getXLinkTitle() + " ("
                + itemProperties.getProperties().getMimeType() + ")<hr />"
                + "<br />" + "Metadata <a href=\"/#\">"
                + itemProperties.getProperties().getVisibility() + "</a>"
                + "Metadata <a href=\"/ESDC#Metadata/13\">"
                + itemProperties.getProperties().getXLinkTitle() + "</a><br />"
                + itemProperties.getProperties().getChecksumAlgorithm() + " "
                + itemProperties.getProperties().getChecksum(),
                Label.CONTENT_RAW);
        addComponent(lblmetadata, "metadata");

    }
}
