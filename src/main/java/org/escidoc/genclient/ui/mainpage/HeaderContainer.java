package org.escidoc.genclient.ui.mainpage;

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

        final TextField username = new TextField("Username");
        username.setWidth("100px");
        username.setHeight("20px");
        username.setImmediate(false);

        // password
        final PasswordField password = new PasswordField("Password");
        password.setWidth("100px");
        password.setHeight("20px");
        password.setImmediate(false);

        // Login
        final Button login = new Button();
        login.setWidth("60px");
        login.setHeight("15px");
        login.setCaption("Login");
        login.setImmediate(true);

        custom.addComponent(login, "login");
        custom.addComponent(password, "password");
        custom.addComponent(username, "username");

    }
}
