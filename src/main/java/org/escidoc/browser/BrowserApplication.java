package org.escidoc.browser;

import org.escidoc.browser.ui.Constant;
import org.escidoc.browser.ui.MainSite;

import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class BrowserApplication extends Application {
    private final Window mainWindow = new Window(Constant.MAIN_WINDOW_TITLE);

    @Override
    public void init() {
        setApplicationTheme();
        buildMainWindow();
        setMainWindow(mainWindow);
    }

    private void setApplicationTheme() {
        setTheme(Constant.THEME_NAME);
    }

    private void buildMainWindow() {
        mainWindow.setImmediate(true);
        mainWindow.setScrollable(true);
        setMainWindowContent();
        setMainWindowHeight();
    }

    private void setMainWindowContent() {
        mainWindow
            .setContent(createMainSite(mainWindow, getApplicationHeight()));
    }

    private void setMainWindowHeight() {
        mainWindow.getContent().setHeight(getApplicationHeight(),
            Sizeable.UNITS_PIXELS);
    }

    private int getApplicationHeight() {
        final WebApplicationContext ctx = (WebApplicationContext) getContext();
        final int height = ctx.getBrowser().getScreenHeight();

        final int appHeight = (height / 100 * 86 - 5);
        return appHeight;
    }

    private MainSite createMainSite(final Window mainWindow, final int appHeight) {
        final MainSite mnSite = new MainSite(mainWindow, appHeight);
        mnSite.setHeight("100%");
        mnSite.setWidth("100%");
        return mnSite;
    }

}