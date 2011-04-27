package org.escidoc.browser.ui.maincontent;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Window;

import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.om.item.component.Components;

@SuppressWarnings("serial")
public class ItemContent extends CustomLayout {

    private static final Logger LOG = LoggerFactory.getLogger(ItemContent.class);

    private final ItemProxyImpl itemproxy;

    private final EscidocServiceLocation serviceLocation;

    private final Window mainWindow;
    public ItemContent(final int accordionHeight, final ItemProxyImpl resourceProxy,
        EscidocServiceLocation serviceLocation, final Window mainWindow) {

        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null.");
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null.");

        this.serviceLocation = serviceLocation;
        this.mainWindow = mainWindow;

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
        final Label lbladdmetadata2 = new Label();
        lbladdmetadata2.setCaption(fileName + "(image/jpeg)");
        lbladdmetadata2.setIcon(new ThemeResource("../runo/icons/16/document-image.png"));
        this.addComponent(lbladdmetadata2);
    }

    private void buildMainElement() {
        final Component itemProperties = itemproxy.getFirstelementProperties();

        // get the file type from the mime:
        final String mimeType = itemProperties.getProperties().getMimeType();
        final String[] last = mimeType.split("/");
        final String lastOne = last[last.length - 1];
        try {
            System.out
                .println(getMimeType(serviceLocation.getEscidocUri() + itemProperties.getContent().getXLinkHref()));
        }
        catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        final Embedded e = new Embedded("", new ThemeResource("images/filetypes/" + lastOne + ".png"));
        addComponent(e, "thumbnail");

        final Label lblmetadata =
            new Label(itemProperties.getProperties().getXLinkTitle()
                + "<br />"
                // + " ("
                // + itemProperties.getProperties().getMimeType() + ")<hr />" + "<br />" + "Metadata <a href=\"/#\">"
                + itemProperties.getProperties().getVisibility() + "<br />"
                + itemProperties.getProperties().getChecksumAlgorithm() + " "
                + itemProperties.getProperties().getChecksum(), Label.CONTENT_RAW);

        Link lnk =
            new Link("Download File", new ExternalResource(serviceLocation.getEscidocUri()
                + itemProperties.getContent().getXLinkHref()));
        lnk.setIcon(new ThemeResource("images/download.png"));
        addComponent(lnk, "addmeta2");

        addComponent(lblmetadata, "metadata");

    }

    private String getMimeType(String url) throws IOException {
        URL u = new URL(url);
        URLConnection uc = null;
        uc = u.openConnection();
        System.out.println(uc.getContentLength());

        return uc.getContentType();
    }

}
