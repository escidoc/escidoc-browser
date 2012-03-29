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
import de.escidoc.core.resources.cmm.ContentModel;

public class ContentModelProxyImpl implements ResourceProxy {

    private ContentModel resource;

    public ContentModelProxyImpl(Resource resource) {
        Preconditions.checkNotNull(resource, "resource is null: %s", resource);
        this.resource = (ContentModel) resource;
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
        String d = resource.getProperties().getDescription();
        if (d == null) {
            return "";
        }
        return d;
    }

    @Override
    public String getStatus() {
        return "";
    }

    @Override
    public String getCreator() {
        return resource.getProperties().getCreatedBy().getXLinkTitle();
    }

    @Override
    public String getCreatedOn() {
        return resource.getProperties().getCreationDate().toString("d.M.y, H:m");
    }

    @Override
    public String getModifier() {
        return "";
    }

    @Override
    public String getModifiedOn() {
        return "";
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resource == null) ? 0 : resource.hashCode());
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
        ContentModelProxyImpl other = (ContentModelProxyImpl) obj;
        if (resource == null) {
            if (other.resource != null) {
                return false;
            }
        }
        else if (!resource.equals(other.resource)) {
            return false;
        }
        return true;
    }

}