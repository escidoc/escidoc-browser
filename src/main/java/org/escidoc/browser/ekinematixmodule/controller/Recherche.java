package org.escidoc.browser.ekinematixmodule.controller;

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.ekinematixmodule.views.RechercheView;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;

import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class Recherche extends Controller {

    public Recherche(Repositories repositories, Router router, ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        createView();
    }

    @Override
    public void createView() {
        try {
            view = new RechercheView(getRouter(), getResourceProxy(), getRepositories(), this);
        }
        catch (EscidocClientException e) {
            getRouter().getMainWindow().showNotification("Error cannot createview ", e.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }

    }

}
