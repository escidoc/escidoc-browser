package org.escidoc.browser;

import java.util.Map;

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.internal.EscidocServiceLocationImpl;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.helper.EscidocParameterHandler;
import org.escidoc.browser.ui.helper.internal.EscidocParameterHandlerImpl;
import org.escidoc.browser.ui.landingview.LandingViewImpl;
import org.escidoc.browser.ui.listeners.StartButtonListener;
import org.escidoc.browser.ui.listeners.WindowResizeListener;
import org.escidoc.browser.ui.listeners.WindowResizeObserver;
import org.escidoc.browser.ui.listeners.WindowResizeObserverImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.Application;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class BrowserApplication extends Application {

    static final Logger LOG = LoggerFactory.getLogger(BrowserApplication.class);

    final Window mainWindow = new Window(ViewConstants.MAIN_WINDOW_TITLE);

    private EscidocServiceLocation serviceLocation = new EscidocServiceLocationImpl();

    private EscidocParameterHandler paramaterHandler;

    private WindowResizeListener windowResizeListener;

    WindowResizeObserver observer;

    private Map<String, String[]> parameters;

    @Override
    public void init() {
        setApplicationTheme();
        setMainWindow();
        addParameterHandler();
        addWindowDimensionDetection();
    }

    private void setApplicationTheme() {
        setTheme(ViewConstants.THEME_NAME);
    }

    private void setMainWindow() {
        setMainWindow(mainWindow);
    }

    private void addParameterHandler() {
        paramaterHandler = new EscidocParameterHandlerImpl(this, serviceLocation);
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
            showLandingView();
        }
    }

    private void showLandingView() {
        mainWindow.removeAllComponents();
        mainWindow.addComponent(new LandingViewImpl(serviceLocation, new StartButtonListener(observer, mainWindow,
            serviceLocation, this)));
    }

    public void buildMainWindow(final EscidocServiceLocation serviceLocation) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        mainWindow.setImmediate(true);
        mainWindow.setScrollable(true);
        setMainWindowContent(serviceLocation);
        setMainWindowHeight();
    }

    private void setMainWindowContent(final EscidocServiceLocation serviceLocation) {
        try {
            mainWindow.setContent(createMainSite(serviceLocation, mainWindow, observer));
        }
        catch (final EscidocClientException e) {
            mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
                Notification.TYPE_ERROR_MESSAGE));
        }
    }

    private MainSite createMainSite(
        final EscidocServiceLocation serviceLocation, final Window mainWindow, final WindowResizeObserver observer)
        throws EscidocClientException {

        final MainSite mainSite = new MainSite(mainWindow, serviceLocation, this, (CurrentUser) getUser());
        mainSite.setHeight(getApplicationHeight() + "px");
        mainSite.setWidth("100%");
        return mainSite;
    }

    private void setMainWindowHeight() {
        mainWindow.getContent().setHeight(getApplicationHeight() + "px");
    }

    public int getApplicationHeight() {
        Preconditions.checkArgument(observer.getDimension().getHeight() > 0, "Can not get window size");
        return Math.round(observer.getDimension().getHeight());
    }

    public int getApplicationWidth() {
        Preconditions.checkArgument(observer.getDimension().getWidth() > 0, "Can not get window size");
        return Math.round(observer.getDimension().getWidth());
    }

    public void setServiceLocation(final EscidocServiceLocation serviceLocation) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        this.serviceLocation = serviceLocation;
    }

    public void setParameters(final Map<String, String[]> parameters) {
        this.parameters = parameters;
    }

    public Map<String, String[]> getParameters() {
        return parameters;
    }

    public EscidocServiceLocation getServiceLocation() {
        return serviceLocation;
    }
}