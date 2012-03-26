/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or https://www.escidoc.org/license/ESCIDOC.LICENSE .
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.model.internal;

import com.google.common.base.Preconditions;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.CurrentUser;

import de.escidoc.core.resources.aa.useraccount.UserAccount;

public class LoggedInUser implements CurrentUser {

    private final UserAccount currentUser;

    private String token = AppConstants.EMPTY_STRING;

    public LoggedInUser(final UserAccount currentUser, final String token) {
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);
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
        result = prime * result + ((currentUser == null) ? 0 : currentUser.hashCode());
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

    @Override
    public String getLoginName() {
        return currentUser.getProperties().getLoginName();
    }

    public String getUserId() {
        return currentUser.getObjid();
    }

    @Override
    public String getRealName() {
        return currentUser.getProperties().getName();
    }

}
