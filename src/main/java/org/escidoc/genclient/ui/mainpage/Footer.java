package org.escidoc.genclient.ui.mainpage;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;

public class Footer extends VerticalLayout {

    public Footer() {
        // This is myTheme/layouts/footer.html
        final CustomLayout custom = new CustomLayout("footer");
        addComponent(custom);

    }
}