package org.escidoc.browser.model.internal;

import org.escidoc.browser.model.CurrentUser;

import de.escidoc.core.resources.aa.useraccount.UserAccount;

public class LoggedInUser implements CurrentUser {

    private final UserAccount currentUser;

    public LoggedInUser(final UserAccount currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public boolean isGuest() {
        return false;
    }

}
