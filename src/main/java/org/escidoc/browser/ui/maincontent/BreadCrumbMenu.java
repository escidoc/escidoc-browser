package org.escidoc.browser.ui.maincontent;

import java.util.ArrayList;
import java.util.Collections;

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.ContainerProxy;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.ui.helper.ResourceHierarchy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class BreadCrumbMenu {
    private static final Logger LOG = LoggerFactory.getLogger(BrowserApplication.class);

    String bCstring = "<ul id='crumbs'><li><a href='#'>Home</a></li>";

    public BreadCrumbMenu(final CssLayout cssLayout, ResourceProxy resourceProxy) {
        String name;
        if (resourceProxy.getName().length() > 100)
            name = resourceProxy.getName().substring(0, 100);
        else
            name = resourceProxy.getName();
        cssLayout.addComponent(new Label("<ul id='crumbs'><li><a href='#'>Home</a></li><li><a href='#'>" + name
            + "... </a></li></ul>", Label.CONTENT_RAW));
    }

    /**
     * This is a Breadcrumb for the ContainerView
     * 
     * @param cssLayout
     * @param resourceProxy
     * @param mainWindow
     * @param escidocServiceLocation
     */
    public BreadCrumbMenu(CssLayout cssLayout, ContainerProxy resourceProxy, Window mainWindow,
        EscidocServiceLocation escidocServiceLocation) {

        ResourceHierarchy rs = new ResourceHierarchy();
        try {
            ArrayList<ResourceModel> hierarchy = rs.getHierarchy(resourceProxy.getId());
            Collections.reverse(hierarchy);
            for (ResourceModel resourceModel : hierarchy) {
                bCstring +=
                    "<li><a href='/browser/mainWindow?tab=" + resourceModel.getId() + "&type="
                        + resourceModel.getType() + "&escidocurl=" + escidocServiceLocation.getEscidocUri()
                        + "' target='_blank'>" + resourceModel.getName() + "</a></li>";
            }
        }
        catch (EscidocClientException e) {
            bCstring +=
                "<li><a href='/browser/mainWindow?tab=" + resourceProxy.getContext().getObjid()
                    + "&type=CONTEXT&escidocurl=" + escidocServiceLocation + "' target='_blank'>"
                    + resourceProxy.getContext().getXLinkTitle() + "</a></li>";
        }
        cssLayout
            .addComponent(new Label(bCstring + "<li>" + resourceProxy.getName() + "</li></ul>", Label.CONTENT_RAW));

    }

    /**
     * BreadCrumb for the Item
     * 
     * @param cssLayout
     * @param resourceProxy
     * @param mainWindow
     * @param escidocServiceLocation
     */
    public BreadCrumbMenu(CssLayout cssLayout, ItemProxyImpl resourceProxy, Window mainWindow,
        EscidocServiceLocation escidocServiceLocation) {
        String bCstring = "<ul id='crumbs'><li><a href='#'>Home Item</a></li>";
        ResourceHierarchy rs = new ResourceHierarchy();

        try {
            String parentId;
            parentId = rs.getReturnParentOfItem(resourceProxy.getId()).getId();
            ArrayList<ResourceModel> hierarchy = rs.getHierarchy(parentId);
            Collections.reverse(hierarchy);
            for (ResourceModel resourceModel : hierarchy) {
                bCstring +=
                    "<li><a href='/browser/mainWindow?tab=" + resourceModel.getId() + "&type="
                        + resourceModel.getType() + "&escidocurl=" + escidocServiceLocation.getEscidocUri()
                        + "' target='_blank'>" + resourceModel.getName() + "</a></li>";
            }
        }
        catch (Exception e) {
            bCstring +=
                "<li><a href='/browser/mainWindow?tab=" + resourceProxy.getContext().getObjid()
                    + "&type=CONTEXT&escidocurl=" + escidocServiceLocation.getEscidocUri() + "' target='_blank'>"
                    + resourceProxy.getContext().getXLinkTitle() + "</a></li>";
        }

        cssLayout
            .addComponent(new Label(bCstring + "<li>" + resourceProxy.getName() + "</li></ul>", Label.CONTENT_RAW));
    }

    /*
     * Search case
     */
    public BreadCrumbMenu(CssLayout cssLayout, String string) {
        // TODO Auto-generated constructor stub
    }
}
