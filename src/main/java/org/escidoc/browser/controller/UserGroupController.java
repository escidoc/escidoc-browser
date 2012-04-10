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

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.UserGroupModel;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.UserGroupView;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class UserGroupController extends Controller {

    public UserGroupController(Repositories repositories, Router router, ResourceProxy resourceProxy)
        throws EscidocClientException {
        super(repositories, router, resourceProxy);
        createView();
    }

    @Override
    public void createView() throws EscidocClientException {
        view = new UserGroupView(getRouter(), getResourceProxy(), getRepositories(), this);
        ((UserGroupView) view).buildContentPanel();
    }

    public boolean canRemoveOUs() {
        // TODO PDP Implement
        return true;
    }

    public void removeOrgUnitFromGroup(String string) {
        // TODO Auto-generated method stub

    }

    public void addOrgUnitToGroup(UserGroupModel resourceProxy, String id) throws EscidocClientException {
        repositories.group().addOrgUnit(resourceProxy.getId(), id);
    }
}
