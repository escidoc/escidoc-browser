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
package org.escidoc.browser.controller;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.UserProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.useraccount.UserAccountView;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.aa.useraccount.Grants;

public class UserAccountController extends Controller {

    public UserAccountController(final Repositories repositories, final Router router, final ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        createView();
    }

    @Override
    public void createView() {
        view = new UserAccountView(getRouter(), (UserProxy) getResourceProxy(), getRepositories().user(), this);
        if (!((UserProxy) getResourceProxy()).getLoginName().equals(ViewConstants.SYSADMIN)) {
            view = new UserAccountView(getRouter(), (UserProxy) getResourceProxy(), getRepositories().user(), this);
        }
    }

    /**
     * Check if the user can access attributes Right now, only the SysAdmin (role administrator) can access this
     * resource
     * 
     * @param userId
     * @return
     */
    public boolean hasAccessOnAttributes(String userId) {
        try {
            for (Grant grant : repositories.user().getGrants(userId)) {
                if (hasSysAdminRole(grant)) {
                    return hasSysAdminRole(grant);
                }
            }
        }
        catch (EscidocClientException e) {
            showError(e);
        }
        return false;
    }

    private static boolean hasSysAdminRole(Grant grant) {
        return grant.getProperties().getRole().getObjid().equals(AppConstants.ESCIDOC_SYSADMIN_ROLE);
    }

    // TODO Fix this method with PDP
    public boolean hasAccessOnPreferences(String id) {
        return true;
    }

    public Grants getGrantsForUser(String id) {
        try {
            return repositories.user().getGrants(id);
        }
        catch (EscidocClientException e) {
            showError(e);
        }
        return null;
    }
}