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

import java.util.List;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;

import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.cmm.ContentModel;

public class ContentModelProxyImpl implements ResourceProxy {

    private Resource resource;

    public ContentModelProxyImpl(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String getId() {
        return resource.getObjid();
    }

    @Override
    public String getName() {
        return resource.getXLinkTitle();
    }

    @Override
    public ResourceType getType() {
        return ResourceType.CONTENT_MODEL;
    }

    @Override
    public String getDescription() {
        return ((ContentModel) resource).getProperties().getDescription();
    }

    @Override
    public String getStatus() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getCreator() {
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public String getCreatedOn() {
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public String getModifier() {
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public String getModifiedOn() {
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public List<String> getRelations() {
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public Resource getContext() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getLockStatus() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getVersionStatus() {
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public Resource getContentModel() {
        return resource;
    }

}
