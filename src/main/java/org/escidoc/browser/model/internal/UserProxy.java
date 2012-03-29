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
 * Copyright 2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.model.internal;

import com.google.common.base.Preconditions;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;

import java.util.List;

import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.aa.useraccount.UserAccount;

public class UserProxy implements ResourceProxy {

    private UserAccount ua;

    public UserProxy(UserAccount ua) {
        Preconditions.checkNotNull(ua, "ua is null: %s", ua);
        this.ua = ua;
    }

    @Override
    public String getName() {
        return ua.getXLinkTitle();
    }

    public String getLoginName() {
        return ua.getProperties().getLoginName();
    }

    @Override
    public String getId() {
        return ua.getObjid();
    }

    @Override
    public ResourceType getType() {
        return ResourceType.USER_ACCOUNT;
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getStatus() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getCreator() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getCreatedOn() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getModifier() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getModifiedOn() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public List<String> getRelations() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public Resource getContext() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getLockStatus() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getVersionStatus() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public Resource getContentModel() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    public UserAccount getResource() {
        return ua;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ua == null) ? 0 : ua.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UserProxy other = (UserProxy) obj;
        if (ua == null) {
            if (other.ua != null) {
                return false;
            }
        }
        else if (!ua.equals(other.ua)) {
            return false;
        }
        return true;
    }

}
