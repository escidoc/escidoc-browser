package org.escidoc.browser.controller;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.maincontent.FolderView;

import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class FolderController extends Controller {

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

}
