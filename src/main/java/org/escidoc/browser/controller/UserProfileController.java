package org.escidoc.browser.controller;

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.UserRepositoryImpl;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.tools.UserProfileView;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.aa.useraccount.Preference;
import de.escidoc.core.resources.aa.useraccount.Preferences;

public class UserProfileController extends Controller {

    private Router router;

    private UserRepositoryImpl userRep;

    public UserProfileController(Repositories repositories, Router router, ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        this.router = router;
        this.userRep = getUserRepository();
        createView();
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

    @Override
    public void createView() {
        view = new UserProfileView(router, this, getCurrentUser());
    }

    public void updateProfile(String name, String password) throws EscidocClientException {
        updateProfile(name);
        userRep.updatePassword(password);

    }

    public void updateProfile(String name) throws EscidocClientException {
        userRep.updateName(name);
    }

    public Preferences getUserPreferences() throws EscidocClientException {
        return userRep.getUserPreferences();
    }

    public Preference createPreference(String key, String value) throws EscidocClientException {
        Preference preference = new Preference(key, value);
        return userRep.createUserPreference(preference);
    }

    public void removePreference(String preferenceName) throws EscidocClientException {
        userRep.removeUserPreference(preferenceName);
    }
}