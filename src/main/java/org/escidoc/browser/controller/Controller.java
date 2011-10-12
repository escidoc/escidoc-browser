package org.escidoc.browser.controller;

import org.escidoc.browser.layout.LayoutDesign;
import org.escidoc.browser.model.ResourceProxy;

public abstract class Controller {
    public abstract void init(ResourceProxy resourceProxy);

    public abstract void showView(LayoutDesign layout);
}
