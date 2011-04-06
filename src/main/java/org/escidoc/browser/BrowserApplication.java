package org.escidoc.browser;

import javax.servlet.http.HttpSession;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.ViewConstant;
import org.escidoc.browser.ui.listeners.WindowResizeListener;
import org.escidoc.browser.ui.listeners.WindowResizeObserver;
import org.escidoc.browser.ui.listeners.WindowResizeObserverImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class BrowserApplication extends Application {

    private static final Logger LOG = LoggerFactory
        .getLogger(BrowserApplication.class);

    private final static Window mainWindow = new Window(
        ViewConstant.MAIN_WINDOW_TITLE);

    private EscidocServiceLocation serviceLocation;

    private EscidocParameterHandler paramaterHandler;

    private SessionHandlerImpl sessionHandler;

    private WindowResizeListener windowResizeListener;

    private WindowResizeObserver observer;

    @Override
    public void init() {
        setApplicationTheme();
        setMainWindow();
        addParameterHandler();

        observer = new WindowResizeObserverImpl();
        windowResizeListener = new WindowResizeListener(observer);
        mainWindow.addListener(windowResizeListener);

    }

    private void setMainWindow() {
        setMainWindow(mainWindow);
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
        Preconditions.checkNotNull(serviceLocation,
            "serviceLocation is null: %s", serviceLocation);
        LOG.debug("Window height is: " + observer.getHeight());
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
        return sessionHandler.isLoggedin();
    }

    private int getApplicationHeight() {
        return observer.getHeight();
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
        Preconditions.checkNotNull(serviceLocation,
            "serviceLocation is null: %s", serviceLocation);
        this.serviceLocation = serviceLocation;
    }

    public void buildMainView() {
        LOG.debug("Height is: " + observer.getHeight());
        buildMainWindow(serviceLocation);
    }

}