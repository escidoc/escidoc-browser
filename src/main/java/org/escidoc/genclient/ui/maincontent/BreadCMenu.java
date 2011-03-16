package org.escidoc.genclient.ui.maincontent;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

public class BreadCMenu {

    private final CssLayout cs;

    public BreadCMenu(final CssLayout cs) {
        this.cs = cs;
        final Label lb =
            new Label(
                "<ul id='crumbs'><li><a href='#'>Home</a></li><li><a href='#'>Main section</a></li><li><a href='#'>Sub section</a></li><li><a href='#'>Sub sub section</a></li><li>The page you are on right now</li></ul>",
                Label.CONTENT_RAW);
        cs.addComponent(lb);

    }
}
