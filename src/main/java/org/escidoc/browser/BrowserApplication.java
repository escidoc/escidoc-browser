package org.escidoc.browser;

import org.escidoc.browser.ui.Constant;
import org.escidoc.browser.ui.MainSite;

import com.vaadin.Application;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class BrowserApplication extends Application {
    private final Window mainWindow = new Window(Constant.MAIN_WINDOW_TITLE);

    private ParameterHandler paramaterHandler;

    @Override
    public void init() {
        setApplicationTheme();
        buildMainWindow();
        setMainWindow(mainWindow);

        addParameterHandler();
    }

    private void addParameterHandler() {
        paramaterHandler = new ParameterHandlerImpl();
        mainWindow.addParameterHandler(paramaterHandler);
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
        try {
            mainWindow.setContent(createMainSite(mainWindow,
                getApplicationHeight()));
        }
        catch (final EscidocClientException e) {
            mainWindow.showNotification("Error");
        }
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

    private MainSite createMainSite(final Window mainWindow, final int appHeight)
        throws EscidocClientException {
        final MainSite mnSite = new MainSite(mainWindow, appHeight);
        mnSite.setHeight("100%");
        mnSite.setWidth("100%");
        return mnSite;
    }

}