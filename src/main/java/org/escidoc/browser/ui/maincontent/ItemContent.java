package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;

import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.om.item.component.Components;

@SuppressWarnings("serial")
public class ItemContent extends CustomLayout {

    private static final Logger LOG = LoggerFactory
        .getLogger(ItemContent.class);

    private final ItemProxyImpl itemproxy;

    public ItemContent(final int accordionHeight,
        final ItemProxyImpl resourceProxy) {
        setTemplateName("itemtemplate");
        itemproxy = resourceProxy;
        if (itemproxy.hasComponents()) {
            buildMainElement();
            buildOtherComponents();
        }
    }

    /**
     * 
     */
    private void buildOtherComponents() {
        final Components itemComponents = itemproxy.getElements();
        for (final Component component : itemComponents) {
            buildLabel(component.getProperties().getFileName());
        }
    }

    /**
     * @param itemContent
     */
    private void buildLabel(final String fileName) {
        LOG.debug("Label is : " + fileName);
        final Label lbladdmetadata2 = new Label();
        lbladdmetadata2.setCaption(fileName + "(image/jpeg)");
        lbladdmetadata2.setIcon(new ThemeResource(
            "../runo/icons/16/document-image.png"));
        this.addComponent(lbladdmetadata2);
    }

    private void buildMainElement() {
        final Component itemProperties = itemproxy.getFirstelementProperties();

        // get the file type from the mime:
        final String mimeType = itemProperties.getProperties().getMimeType();
        final String[] last = mimeType.split("/");
        final String lastOne = last[last.length - 1];

        final Embedded e =
            new Embedded("", new ThemeResource("../myTheme/images/filetypes/"
                + lastOne + ".png"));
        addComponent(e, "thumbnail");
        final Label lblmetadata =
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
