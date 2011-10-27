package org.escidoc.browser.ui.view.helpers;

import java.net.MalformedURLException;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.ui.maincontent.ItemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Link;

public class CreatePermanentLinkVH {
    private static final String COULD_NOT_RETRIEVE_APPLICATION_URL = "Could not retrieve application URL";

    private static final Logger LOG = LoggerFactory.getLogger(ItemView.class);

    public CreatePermanentLinkVH(String url, String id, String type, CssLayout cssLayout,
        EscidocServiceLocation serviceLocation) {
        Link l;
        try {
            l =
                new Link("", new ExternalResource(url + "?id=" + id + "&type=" + type + "&escidocurl="
                    + serviceLocation.getEscidocUrl()));
            l.setDescription("Permanent Link");
            l.setIcon(new ThemeResource("images/assets/link.png"));
            l.setStyleName("permanentLink");
            cssLayout.addComponent(l);
        }
        catch (MalformedURLException e) {
            cssLayout.getWindow().showNotification(COULD_NOT_RETRIEVE_APPLICATION_URL);
            LOG.error(COULD_NOT_RETRIEVE_APPLICATION_URL + e.getLocalizedMessage());
        }
    }

}
