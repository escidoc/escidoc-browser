package org.escidoc.browser.ui.mainpage;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class HeaderContainer extends VerticalLayout {

    public HeaderContainer() {

        // found at myTheme/layouts/header.html
        final CustomLayout custom = new CustomLayout("header");
        addComponent(custom);

        final TextField searchfld = new TextField("Search");
        searchfld.setWidth("100px");
        searchfld.setHeight("20px");
        searchfld.setImmediate(false);


        // Login
        final Button login = new Button();
        login.setWidth("60px");
        login.setHeight("15px");
        login.setCaption("Login");
        login.setImmediate(true);

        custom.addComponent(login, "login");
        custom.addComponent(searchfld, "password");

    }
}
