package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.ui.Constant;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class BreadCrumbMenu {

    public BreadCrumbMenu(final CssLayout cssLayout,String bcType) {
        cssLayout.addComponent(new Label(Constant.BREAD_CRUMP_CONTENT,
            Label.CONTENT_RAW));
    }
}
