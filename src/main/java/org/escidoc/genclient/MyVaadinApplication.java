package org.escidoc.genclient;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class MyVaadinApplication extends Application {
    private Window window;

    @Override
    public void init() {
        window = new Window("My Vaadin Application");
        setMainWindow(window);
        window.addComponent(new Button("Click Me"));
    }

}
