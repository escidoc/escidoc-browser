package org.escidoc.genclient.ui.mainpage;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class LoginForm extends VerticalLayout {

    @AutoGenerated
    private final Button Login;

    @AutoGenerated
    private TextField password;

    @AutoGenerated
    private TextField username;

    /**
     * The constructor should first build the main layout, set the composition
     * root and then do any custom initialization.
     * 
     * The constructor will not be automatically regenerated by the visual
     * editor.
     */
    public LoginForm() {
        final CssLayout cssLayout = new CssLayout();
        cssLayout.setWidth("500px");
        // // username
        // username = new TextField();
        // username.setWidth("100px");
        // username.setHeight("20px");
        // username.setImmediate(false);
        // //cssLayout.addComponent(username, "top:2.0px;left:2.0px;");
        // cssLayout.addComponent(username);
        //
        // // password
        // password = new TextField();
        // password.setWidth("100px");
        // password.setHeight("20px");
        // password.setImmediate(false);
        // //cssLayout.addComponent(password, "top:2.0px;left:204.0px;");
        // cssLayout.addComponent(password);

        // Login
        Login = new Button();
        // Login.setWidth("60px");
        // Login.setHeight("15px");
        Login.setCaption("Login");
        Login.setImmediate(true);
        // cssLayout.addComponent(Login, "top:2.0px;left:385.0px;");
        cssLayout.addComponent(Login);
        addComponent(cssLayout);
    }

}
