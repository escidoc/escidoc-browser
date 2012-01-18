package org.escidoc.browser.controller;

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.UserRepositoryImpl;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.tools.UserProfileView;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class UserProfileController extends Controller {

    private UserRepositoryImpl userRep;

    public UserProfileController(final Repositories repositories, final Router router, final ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        this.userRep = getUserRepository();
        this.setResourceName(ViewConstants.EDIT_PROFILE);
    }

    private CurrentUser getCurrentUser() {
        return getUserRepository().findCurrentUser();
    }

    private UserRepositoryImpl getUserRepository() {
        userRep = new UserRepositoryImpl(getRouter().getServiceLocation());
        userRep.withToken(getRouter().getApp().getCurrentUser().getToken());
        return userRep;
    }

    @Override
    public void createView() {
        view = new UserProfileView(getRouter(), getRepositories(), this, getCurrentUser());
    }

    public void updateProfile(final String name, final String password) throws EscidocClientException {
        updateProfile(name);
        userRep.updatePassword(password);

    }

    public void updateProfile(final String name) throws EscidocClientException {
        userRep.updateName(name);
    }
}