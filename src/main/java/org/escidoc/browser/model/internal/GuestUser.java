package org.escidoc.browser.model.internal;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.CurrentUser;

public class GuestUser implements CurrentUser {

    @Override
    public boolean isGuest() {
        return true;
    }

    @Override
    public String getToken() {
        return AppConstants.EMPTY_STRING;
    }

}
