package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.ui.Constant;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class BreadCMenu {

    public BreadCMenu(final CssLayout cssLayout) {
        cssLayout.addComponent(new Label(Constant.BREAD_CRUMP_CONTENT,
            Label.CONTENT_RAW));
    }
}
