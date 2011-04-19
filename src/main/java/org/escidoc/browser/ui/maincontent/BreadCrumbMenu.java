package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.repository.ContainerProxy;
import org.escidoc.browser.repository.internal.ItemProxyImpl;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class BreadCrumbMenu {

    public BreadCrumbMenu(final CssLayout cssLayout, String bcType) {
        cssLayout
            .addComponent(new Label(
                "<ul id='crumbs'><li><a href='#'>Context</a></li><li><a href='#'>Main section</a></li><li><a href='#'>Sub section</a></li><li><a href='#'>Sub sub section</a></li><li>The page you are on right now</li></ul>",
                Label.CONTENT_RAW));
    }

    public BreadCrumbMenu(CssLayout cssLayout, ContainerProxy resourceProxy) {
        cssLayout
            .addComponent(new Label(
                "<ul id='crumbs'><li><a href='#'>Container</a></li><li><a href='#'>...</a></li><li><a href='#'>"
                    + resourceProxy.getName() + "</a></li></ul>",
                Label.CONTENT_RAW));

    }

    public BreadCrumbMenu(CssLayout cssLayout, ItemProxyImpl resourceProxy) {
        cssLayout
            .addComponent(new Label(
                "<ul id='crumbs'><li><a href='#'>Item</a></li><li><a href='#'>...</a></li><li><a href='#'>"
                    + resourceProxy.getName() + "</a></li></ul>",
                Label.CONTENT_RAW));
    }
}
