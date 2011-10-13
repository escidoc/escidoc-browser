package org.escidoc.browser.controller;

import org.escidoc.browser.layout.LayoutDesign;
import org.escidoc.browser.model.ResourceProxy;

import com.vaadin.ui.Component;

public abstract class Controller {
    protected Component view;

    public abstract void init(ResourceProxy resourceProxy);

    public void showView(LayoutDesign layout) {
        layout.openView(this.view, this.view.getCaption());
    }
}
