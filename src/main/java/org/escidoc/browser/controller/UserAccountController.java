package org.escidoc.browser.controller;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;

public class UserAccountController extends Controller {

    public UserAccountController(final Repositories repositories, final Router router, final ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        createView();
        setResourceName(getResourceProxy().getName());
    }

    @Override
    public void createView() {
        view = new UserAccountView(getRouter());
    }
}