package org.escidoc.browser;

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.internal.EscidocServiceLocationImpl;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.ViewConstant;
import org.escidoc.browser.ui.helper.EscidocParameterHandler;
import org.escidoc.browser.ui.helper.internal.EscidocParameterHandlerImpl;
import org.escidoc.browser.ui.listeners.WindowResizeListener;
import org.escidoc.browser.ui.listeners.WindowResizeObserver;
import org.escidoc.browser.ui.listeners.WindowResizeObserverImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class BrowserApplication extends Application {

    private static final Logger LOG = LoggerFactory
        .getLogger(BrowserApplication.class);

    private final static Window mainWindow = new Window(
        ViewConstant.MAIN_WINDOW_TITLE);

    private EscidocServiceLocation serviceLocation =
        new EscidocServiceLocationImpl();

    private EscidocParameterHandler paramaterHandler;

    private WindowResizeListener windowResizeListener;

    private WindowResizeObserver observer;

    @Override
    public void init() {
        setApplicationTheme();
        setMainWindow();
        addParameterHandler();
        addWindowDimensionDetection();
    }

    private void setApplicationTheme() {
        setTheme(ViewConstant.THEME_NAME);
    }

    private void setMainWindow() {
        setMainWindow(mainWindow);
    }

    private void addParameterHandler() {
        paramaterHandler =
            new EscidocParameterHandlerImpl(this, serviceLocation);
        mainWindow.addParameterHandler(paramaterHandler);
    }

    private void addWindowDimensionDetection() {
        observer = new WindowResizeObserverImpl();
        windowResizeListener = new WindowResizeListener(observer);
        mainWindow.addListener(windowResizeListener);
    }

    public void buildMainView() {
        if (observer.getDimension().getHeight() > 0) {
            LOG.debug("Dimension is: " + observer.getDimension());
            buildMainWindow(serviceLocation);
        }
        else {
            final Button button = new Button("Start");
            mainWindow.addComponent(button);
            button.addListener(new Button.ClickListener() {

                @Override
                public void buttonClick(final ClickEvent event) {
                    Preconditions.checkArgument(observer
                        .getDimension().getHeight() > 0,
                        "Can not get window size");
                    LOG.debug("Dimension is: " + observer.getDimension());
                    mainWindow.removeComponent(button);
                    buildMainWindow(serviceLocation);
                }
            });
        }
    }

    private void buildMainWindow(final EscidocServiceLocation serviceLocation) {
        Preconditions.checkNotNull(serviceLocation,
            "serviceLocation is null: %s", serviceLocation);
        mainWindow.setImmediate(true);
        mainWindow.setScrollable(true);
        setMainWindowContent(serviceLocation);
        setMainWindowHeight();
    }

    private void setMainWindowContent(
        final EscidocServiceLocation serviceLocation) {
        try {
            mainWindow.setContent(createMainSite(serviceLocation, mainWindow,
                observer));
        }
        catch (final EscidocClientException e) {
            mainWindow.showNotification(new Window.Notification(
                ViewConstant.ERROR, e.getMessage(),
                Notification.TYPE_ERROR_MESSAGE));
        }
    }

    private MainSite createMainSite(
        final EscidocServiceLocation serviceLocation, final Window mainWindow,
        final WindowResizeObserver observer) throws EscidocClientException {

        final MainSite mainSite =
            new MainSite(mainWindow, serviceLocation, observer, this,
                (CurrentUser) getUser());
        mainSite.setHeight("100%");
        mainSite.setWidth("100%");
        return mainSite;
    }

    private void setMainWindowHeight() {
        mainWindow.getContent().setHeight(100, Sizeable.UNITS_PERCENTAGE);
    }

    public int getApplicationHeight() {
        Preconditions.checkArgument(observer.getDimension().getHeight() > 0,
            "Can not get window size");
        return Math.round(observer.getDimension().getHeight());
    }

    public int getApplicationWidth() {
        Preconditions.checkArgument(observer.getDimension().getWidth() > 0,
            "Can not get window size");
        return Math.round(observer.getDimension().getWidth());
    }

    public void setServiceLocation(final EscidocServiceLocation serviceLocation) {
        Preconditions.checkNotNull(serviceLocation,
            "serviceLocation is null: %s", serviceLocation);
        this.serviceLocation = serviceLocation;
    }
}