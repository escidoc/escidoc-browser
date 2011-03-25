package org.escidoc.browser;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.ui.Window;


@SuppressWarnings("serial")
public class ParameterHandlerImpl implements ParameterHandler {

    private static final Logger LOG = LoggerFactory
        .getLogger(ParameterHandlerImpl.class);

    private Window mainWindow;
	private BrowserApplication app;

	public ParameterHandlerImpl(final BrowserApplication app) {
            this.app = app;
    }
    
    @Override
    public void handleParameters(final Map<String, String[]> parameters) {
        if (Util.isEscidocUrlExists(parameters)
            && Util.isTokenExist(parameters)) {
            LOG.debug("both escidocurl and token exists");
            //app.setEscidocUri(parseEscidocUriFrom(parameters));
            // showMainView(parameters);
        }
        if (Util.isTokenExist(parameters)) {
            LOG.debug("only token exists");
            SessionHandlerImpl sess = app.getSessionHandler();
            ParamaterDecoder pardec = new ParamaterDecoder(app);
            sess.doLogin(pardec.parseAndDecodeToken(parameters));
            // showMainView(parameters);
        }
        else if (Util.isEscidocUrlExists(parameters)
            && !Util.isTokenExist(parameters)) {
            LOG.debug("escidocurl exists but no token");
            // app.setEscidocUri(parseEscidocUriFrom(parameters));
            // showLoginView();
        }
        else if (!Util.isEscidocUrlExists(parameters)
            && !Util.isTokenExist(parameters)) {
            LOG.debug("nothing");
            // app.showLandingView();
        }
    }
}
