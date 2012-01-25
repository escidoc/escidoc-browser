package org.escidoc.browser.controller;

import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.maincontent.View;

@SuppressWarnings("serial")
public class ContentModelView extends View {

    private Router r;

    public ContentModelView(Router router) {
        this.r = router;
    }

}