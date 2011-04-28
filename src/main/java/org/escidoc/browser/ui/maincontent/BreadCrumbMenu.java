package org.escidoc.browser.ui.maincontent;

import java.util.ArrayList;

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.ContainerProxy;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.ui.helper.ResourceHierarchy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class BreadCrumbMenu {
    private static final Logger LOG = LoggerFactory.getLogger(BrowserApplication.class);

    public BreadCrumbMenu(final CssLayout cssLayout, ResourceProxy resourceProxy) {
        cssLayout.addComponent(new Label("<ul id='crumbs'><li><a href='#'>Context</a></li><li><a href='#'>"
            + resourceProxy.getName() + "</a></li></ul>", Label.CONTENT_RAW));
    }

    public BreadCrumbMenu(CssLayout cssLayout, ContainerProxy resourceProxy) {
        ResourceHierarchy rs = new ResourceHierarchy();
        try {
            LOG.debug("THE ID IS " + resourceProxy.getId());
            ArrayList<String> hierarchy = rs.getHierarchy(resourceProxy.getId());
            for (String string : hierarchy) {
                System.out.println(string);
            }
            System.out.println("The content of arraylist is: " + hierarchy);
        }
        catch (EscidocClientException e) {
            // TODO Auto-generated catch block
            // LOG.debug("I ended up in an error");
            e.printStackTrace();
        }
        cssLayout.addComponent(new Label(
            "<ul id='crumbs'><li><a href='#'>Container</a></li><li><a href='#'>...</a></li><li><a href='#'>"
                + resourceProxy.getName() + "</a></li></ul>", Label.CONTENT_RAW));

    }

    public BreadCrumbMenu(CssLayout cssLayout, ItemProxyImpl resourceProxy) {
        cssLayout.addComponent(new Label(
            "<ul id='crumbs'><li><a href='#'>Item</a></li><li><a href='#'>...</a></li><li><a href='#'>"
                + resourceProxy.getName() + "</a></li></ul>", Label.CONTENT_RAW));
    }

    /*
     * Search case
     */
    public BreadCrumbMenu(CssLayout cssLayout, String string) {
        // TODO Auto-generated constructor stub
    }
}
