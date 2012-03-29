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
package org.escidoc.browser.repository;

import org.escidoc.browser.model.AbstractResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;

import java.util.List;

import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.aa.usergroup.Selectors;
import de.escidoc.core.resources.aa.usergroup.UserGroup;

public class UserGroupModel extends AbstractResourceModel implements ResourceProxy {

    private UserGroup userGroup;

    public UserGroupModel(UserGroup userGroup) {
        super(userGroup);
        this.userGroup = userGroup;
    }

    @Override
    public String getDescription() {
        return userGroup.getProperties().getDescription();
    }

    @Override
    public String getCreator() {
        return userGroup.getProperties().getCreatedBy().getXLinkTitle();
    }

    @Override
    public String getModifier() {
        return userGroup.getProperties().getModifiedBy().getXLinkTitle();
    }

    // FIXME date format
    @Override
    public String getCreatedOn() {
        return userGroup.getProperties().getCreationDate().toString();
    }

    @Override
    public String getModifiedOn() {
        return userGroup.getLastModificationDate().toString();
    }

    @Override
    public List<String> getRelations() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getStatus() {
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

    @Override
    public ResourceType getType() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    public Selectors getSelector() {
        return userGroup.getSelectors();
    }

}