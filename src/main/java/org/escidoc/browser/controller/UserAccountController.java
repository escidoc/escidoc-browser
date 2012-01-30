package org.escidoc.browser.controller;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.UserProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.useraccount.UserAccountView;

public class UserAccountController extends Controller {

    public UserAccountController(final Repositories repositories, final Router router, final ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        createView();
        setResourceName(getResourceProxy().getName());
    }

    @Override
    public void createView() {
        view = new UserAccountView(getRouter(), (UserProxy) getResourceProxy(), getRepositories().user(), this);
    }
}