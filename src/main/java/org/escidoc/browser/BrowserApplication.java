package org.escidoc.browser;

import javax.servlet.http.HttpSession;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.ViewConstant;
import org.escidoc.browser.ui.listeners.TreeClickListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class BrowserApplication extends Application {
    private final static Window mainWindow = new Window(
        ViewConstant.MAIN_WINDOW_TITLE);

    private static final Logger LOG = LoggerFactory
        .getLogger(TreeClickListener.class);

    private EscidocServiceLocation serviceLocation;

    private EscidocParameterHandler paramaterHandler;

    private SessionHandlerImpl sessionHandler;

    @Override
    public void init() {
        setApplicationTheme();
        setMainWindow(mainWindow);
        addParameterHandler();
    }

    private void addParameterHandler() {
        paramaterHandler =
            new ParameterHandlerImpl(this, serviceLocation, sessionHandler);
        mainWindow.addParameterHandler(paramaterHandler);
    }

    private void setApplicationTheme() {
        setTheme(ViewConstant.THEME_NAME);
    }

    private void buildMainWindow(final EscidocServiceLocation serviceLocation) {
        mainWindow.setImmediate(true);
        mainWindow.setScrollable(true);
        setMainWindowContent(serviceLocation);
        setMainWindowHeight();
    }

    private void setMainWindowContent(
        final EscidocServiceLocation serviceLocation) {
        try {
            mainWindow.setContent(createMainSite(serviceLocation, mainWindow,
                getApplicationHeight()));
        }
        catch (final EscidocClientException e) {
            mainWindow.showNotification(new Window.Notification(
                ViewConstant.ERROR, e.getMessage(),
                Notification.TYPE_ERROR_MESSAGE));
        }
    }

    private void setMainWindowHeight() {
        mainWindow.getContent().setHeight(100, Sizeable.UNITS_PERCENTAGE);
    }

    public SessionHandlerImpl getSessionHandler() {
        final WebApplicationContext ctx = (WebApplicationContext) getContext();
        final HttpSession session = ctx.getHttpSession();
        final SessionHandlerImpl sessionhndl = new SessionHandlerImpl(session);
        System.out.println("Outputting the cookie"
            + sessionhndl.geteSDBHandlervalue());
        return sessionhndl;
    }

    public boolean isLoggedin() {
        if (sessionHandler.isLoggedin()) {
            return true;
        }
        else {
            return false;
        }
    }

    // TODO Fix the manual Height
    private int getApplicationHeight() {
        final WebApplicationContext ctx = (WebApplicationContext) getContext();
        final int height = ctx.getBrowser().getScreenHeight();
        LOG.debug("I AM IN BrowserApplication.java and Height is: " + height);
        LOG.debug("Getting mainwindow Height"
            + mainWindow.getContent().getHeight());
        return 860;
    }

    private MainSite createMainSite(
        final EscidocServiceLocation serviceLocation, final Window mainWindow,
        final int appHeight) throws EscidocClientException {
        final MainSite mainSite =
            new MainSite(mainWindow, serviceLocation, appHeight, this);
        mainSite.setHeight("100%");
        mainSite.setWidth("100%");
        return mainSite;
    }

    public void setServiceLocation(final EscidocServiceLocation serviceLocation) {
        this.serviceLocation = serviceLocation;
    }

    public void buildMainView() {
        buildMainWindow(serviceLocation);
    }

}