package org.escidoc.browser.controller;

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.UserRepositoryImpl;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.tools.UserProfileView;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class UserProfileController extends Controller {

    private Repositories repositories;

    private Router router;

    private UserRepositoryImpl userRep;

    public UserProfileController(Repositories repositories, Router router, ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        this.repositories = repositories;
        this.router = router;
        this.userRep = getUserRepository();
        try {
            this.view = createView();
        }
        catch (EscidocClientException e) {
            router.getMainWindow().showNotification(
                ViewConstants.VIEW_ERROR_CANNOT_LOAD_VIEW + e.getLocalizedMessage(), Notification.TYPE_ERROR_MESSAGE);
        }
        this.setResourceName(ViewConstants.EDIT_PROFILE);
    }

    private CurrentUser getCurrentUser() {
        return userRep.findCurrentUser();
    }

    private UserRepositoryImpl getUserRepository() {
        userRep = new UserRepositoryImpl(this.router.getServiceLocation());
        userRep.withToken(this.router.getApp().getCurrentUser().getToken());
        return userRep;
    }

    private Component createView() throws EscidocClientException {
        return new UserProfileView(router, repositories, this, getCurrentUser());
    }

    public void updateProfile(String name, String password) throws EscidocClientException {
        updateProfile(name);
        userRep.updatePassword(password);

    }

    public void updateProfile(String name) throws EscidocClientException {
        userRep.updateName(name);
    }

}
