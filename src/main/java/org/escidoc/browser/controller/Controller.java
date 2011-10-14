package org.escidoc.browser.controller;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

import org.escidoc.browser.layout.LayoutDesign;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;

public abstract class Controller {
    protected Component view;

    public abstract void init(
        final EscidocServiceLocation serviceLocation, final Repositories repositories, final Router mainSite,
        final ResourceProxy resourceProxy, final Window mainWindow, final CurrentUser currentUser);

    public void showView(final LayoutDesign layout) {
        layout.openView(this.view, this.view.getCaption());
    }
}
