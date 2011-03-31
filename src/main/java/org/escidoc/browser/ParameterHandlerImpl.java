package org.escidoc.browser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.EscidocServiceLocationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class ParameterHandlerImpl implements EscidocParameterHandler {

    private static final Logger LOG = LoggerFactory
        .getLogger(ParameterHandlerImpl.class);

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
            // showMainView(parameters);
        }
        else if (Util.isEscidocUrlExists(parameters)
            && hasNotEscidocHandler(parameters)) {
            LOG.debug("escidocurl exists but no escidocHandler");
            final URI escidocUri =
                tryToParseEscidocUriFromParameter(parameters);
            isEscidocOnline(escidocUri);
            serviceLocation.setUri(escidocUri);
            app.setServiceLocation(serviceLocation);
            app.buildMainView();
        }
        else if (!Util.isEscidocUrlExists(parameters)
            && hasNotEscidocHandler(parameters)) {
            LOG.debug("nothing");
            // app.showLandingView();
        }
    }

    private void isEscidocOnline(final URI escidocUri) {

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
