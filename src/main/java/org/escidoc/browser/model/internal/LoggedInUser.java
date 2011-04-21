package org.escidoc.browser.model.internal;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.CurrentUser;

import com.google.common.base.Preconditions;

import de.escidoc.core.resources.aa.useraccount.UserAccount;

public class LoggedInUser implements CurrentUser {

    private final UserAccount currentUser;

    private String token = AppConstants.EMPTY_STRING;

    public LoggedInUser(final UserAccount currentUser, final String token) {
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s",
            currentUser);
        Preconditions.checkNotNull(token, "token is null: %s", token);
        this.currentUser = currentUser;
        this.token = token;
    }

    @Override
    public boolean isGuest() {
        return false;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("LoggedInUser [");
        if (currentUser != null) {
            builder.append("currentUser=").append(currentUser).append(", ");
        }
        if (token != null) {
            builder.append("token=").append(token);
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result
                + ((currentUser == null) ? 0 : currentUser.hashCode());
        result = prime * result + ((token == null) ? 0 : token.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LoggedInUser other = (LoggedInUser) obj;
        if (currentUser == null) {
            if (other.currentUser != null) {
                return false;
            }
        }
        else if (!currentUser.equals(other.currentUser)) {
            return false;
        }
        if (token == null) {
            if (other.token != null) {
                return false;
            }
        }
        else if (!token.equals(other.token)) {
            return false;
        }
        return true;
    }

}