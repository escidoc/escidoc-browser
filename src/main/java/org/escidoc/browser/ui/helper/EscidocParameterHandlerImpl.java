package org.escidoc.browser.ui.helper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class EscidocParameterHandlerImpl implements EscidocParameterHandler {

    private static final Logger LOG = LoggerFactory
        .getLogger(EscidocParameterHandlerImpl.class);

    private static final int TEN_SECONDS = 10000;

    private final EscidocServiceLocation serviceLocation;

    private final BrowserApplication app;

    public EscidocParameterHandlerImpl(final BrowserApplication app,
        final EscidocServiceLocation serviceLocation) {
        Preconditions.checkNotNull(app, "app is null: %s", app);
        Preconditions.checkNotNull(serviceLocation,
            "serviceLocation is null: %s", serviceLocation);
        this.app = app;
        this.serviceLocation = serviceLocation;
    }

    @Override
    public void handleParameters(final Map<String, String[]> parameters) {
        LOG.debug("parameters: " + parameters.toString());
        if (Util.isEscidocUrlExists(parameters)
            && Util.doesTokenExist(parameters)) {
            LOG.debug("both escidocurl and token exists");
            serviceLocation
                .setEscidocUri(tryToParseEscidocUriFromParameter(parameters));
            serviceLocation.setApplicationUri(toUri(app.getURL()));
            app.setLogoutURL(serviceLocation.getLogoutUri());
            login(parameters);
        }
        if (Util.doesTokenExist(parameters)
            && serviceLocation.getEscidocUri() != null) {
            LOG.debug("only token exists");
            login(parameters);
            app.setLogoutURL(serviceLocation.getLogoutUri());
        }
        else if (Util.isEscidocUrlExists(parameters)
            && hasNotEscidocHandler(parameters)) {
            LOG.debug("escidocurl exists but no escidocHandler");
            if (isServerOnline(tryToParseEscidocUriFromParameter(parameters))) {
                serviceLocation
                    .setEscidocUri(tryToParseEscidocUriFromParameter(parameters));
                serviceLocation.setApplicationUri(toUri(app.getURL()));
                app.setServiceLocation(serviceLocation);
                app.setLogoutURL(serviceLocation.getLogoutUri());
                app.buildMainView();
            }
        }
        else if (!Util.isEscidocUrlExists(parameters)
            && hasNotEscidocHandler(parameters)) {
            LOG.debug("nothing");
        }
    }

    private URI toUri(final URL url) {
        try {
            return url.toURI();
        }
        catch (final URISyntaxException e) {
            LOG.warn("Malformed URL: " + e);
            app.getMainWindow().showNotification(
                new Notification(e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE));
        }
        return null;
    }

    private void login(final Map<String, String[]> parameters) {
        final String escidocToken =
            ParamaterDecoder.parseAndDecodeToken(parameters);
        // fire event UserLoggedIn to the listeners

        // set logout URI
    }

    // TODO refactor this method, does not belong in this class.
    private boolean isServerOnline(final URI escidocUri) {
        URLConnection connection;
        try {
            connection = new URL(escidocUri.toString()).openConnection();
            connection.setConnectTimeout(TEN_SECONDS);
            connection.connect();
            final int responseCode =
                ((HttpURLConnection) connection).getResponseCode();
            return responseCode == 200;
        }
        catch (final IllegalArgumentException e) {
            LOG.warn("Malformed URL: " + e);
            app.getMainWindow().showNotification(
                new Notification(e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE));
            return false;
        }
        catch (final MalformedURLException e) {
            LOG.warn("Malformed URL: " + e);
            app.getMainWindow().showNotification(
                new Notification(e.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE));
            return false;
        }
        catch (final IOException e) {
            LOG.warn("IOException: " + e);
            app.getMainWindow().showNotification(
                new Notification("Can not connect to: " + escidocUri,
                    Notification.TYPE_ERROR_MESSAGE));
            return false;
        }
    }

    private boolean hasNotEscidocHandler(final Map<String, String[]> parameters) {
        return !Util.doesTokenExist(parameters);
    }

    private URI tryToParseEscidocUriFromParameter(
        final Map<String, String[]> parameters) {
        try {
            return Util.parseEscidocUriFrom(parameters);
        }
        catch (final URISyntaxException e) {
            LOG.error("Wrong URI syntax", e);
        }
        // FIXME: do not return null.
        return null;
    }

    @Override
    public EscidocServiceLocation getServiceLocation() {
        return serviceLocation;
    }
}
