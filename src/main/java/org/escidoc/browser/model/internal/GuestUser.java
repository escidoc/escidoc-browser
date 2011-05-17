package org.escidoc.browser.model.internal;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.ui.ViewConstants;

public class GuestUser implements CurrentUser {

    @Override
    public boolean isGuest() {
        return true;
    }

    @Override
    public String getToken() {
        return AppConstants.EMPTY_STRING;
    }

    @Override
    public String getLoginName() {
        return ViewConstants.GUEST;
    }

}
