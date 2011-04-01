package org.escidoc.browser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.EscidocServiceLocationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class ParameterHandlerImpl implements EscidocParameterHandler {

    private static final Logger LOG = LoggerFactory
        .getLogger(ParameterHandlerImpl.class);

    private static final int TEN_SECONDS = 10000;

    private final EscidocServiceLocation serviceLocation;

    private final SessionHandlerImpl sessionHandler;

    private final BrowserApplication app;

    public ParameterHandlerImpl(final BrowserApplication app,
        final EscidocServiceLocation serviceLocation,
        final SessionHandlerImpl sessionHandler) {
        this.app = app;
        this.serviceLocation = new EscidocServiceLocationImpl();
        this.sessionHandler = sessionHandler;
    }

    @Override
    public void handleParameters(final Map<String, String[]> parameters) {
        if (Util.isEscidocUrlExists(parameters)
            && Util.isTokenExist(parameters)) {
            LOG.debug("both escidocurl and token exists");
            serviceLocation
                .setUri(tryToParseEscidocUriFromParameter(parameters));
            sessionHandler.doLogin(ParamaterDecoder
                .parseAndDecodeToken(parameters));
        }
        if (Util.isTokenExist(parameters)) {
            LOG.debug("only token exists");
            sessionHandler.doLogin(ParamaterDecoder
                .parseAndDecodeToken(parameters));
        }
        else if (Util.isEscidocUrlExists(parameters)
            && hasNotEscidocHandler(parameters)) {
            LOG.debug("escidocurl exists but no escidocHandler");
            if (isServerOnline(tryToParseEscidocUriFromParameter(parameters))) {
                serviceLocation
                    .setUri(tryToParseEscidocUriFromParameter(parameters));
                app.setServiceLocation(serviceLocation);
                app.buildMainView();
            }
        }
        else if (!Util.isEscidocUrlExists(parameters)
            && hasNotEscidocHandler(parameters)) {
            LOG.debug("nothing");
        }
    }

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
        return !Util.isTokenExist(parameters);
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
