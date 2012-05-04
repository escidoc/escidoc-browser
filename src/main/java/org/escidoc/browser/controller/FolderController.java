package org.escidoc.browser.controller;

import java.net.URISyntaxException;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.maincontent.FolderView;
import org.escidoc.browser.ui.view.helpers.FolderChildrenVH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class FolderController extends Controller {
    private static final Logger LOG = LoggerFactory.getLogger(FolderChildrenVH.class);

    public FolderController(Repositories repositories, Router router, ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        createView();
    }

    @Override
    public void createView() {
        try {
            view = new FolderView(getRouter(), getResourceProxy(), getRepositories(), this);
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification("Error cannot create view: ", e.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    public boolean hasAccess() {
        try {
            return repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_CONTAINER).forResource(resourceProxy.getId())
                .permitted();
        }
        catch (final EscidocClientException e) {
            LOG.debug("No Access" + e.getLocalizedMessage());
            return false;
        }
        catch (final URISyntaxException e) {
            LOG.debug("Wrong URI " + e.getLocalizedMessage());
            return false;
        }
    }

}
