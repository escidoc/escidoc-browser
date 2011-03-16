package org.escidoc.genclient;

import org.escidoc.genclient.ui.MainSite;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class BrowserApplication extends Application {

    public int appHeight = 0;

    @Override
    public void init() {
        setTheme("myTheme");
        final Window mainWindow =
            new Window("eSciDoc Client (Generic Browser)");
        mainWindow.setImmediate(true);

        final Application myApplication = this;
        final WebApplicationContext ctx =
            (WebApplicationContext) myApplication.getContext();
        final int height = ctx.getBrowser().getScreenHeight();
        this.appHeight = (height / 100 * 86 - 5);
        mainWindow.showNotification("a Height is :" + height + "appheight "
            + appHeight);
        mainWindow.setScrollable(true);

        final MainSite mnSite = new MainSite(mainWindow, appHeight);
        mnSite.setHeight("100%");
        mnSite.setWidth("100%");
        mainWindow.setContent(mnSite);
        mainWindow.getContent().setHeight(appHeight + "px");
        setMainWindow(mainWindow);
    }

}