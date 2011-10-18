package org.escidoc.browser.controller;

import org.escidoc.browser.layout.LayoutDesign;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

public abstract class Controller {
    protected Component view;

    String resouceName;

    public abstract void init(
        final EscidocServiceLocation serviceLocation, final Repositories repositories, final Router mainSite,
        final ResourceProxy resourceProxy, final Window mainWindow, final CurrentUser currentUser);

    public void showView(final LayoutDesign layout) {
        layout.openView(this.view, this.getResourceName());
    }

    public String getResourceName() {
        return resouceName;
    }

    // FIX should be changed to: void setResourceName(String name)
    public void getResourceName(String name) {
        this.resouceName = name;
    }

}
