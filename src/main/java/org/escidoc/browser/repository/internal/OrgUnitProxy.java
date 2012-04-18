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
package org.escidoc.browser.repository.internal;

import java.util.ArrayList;
import java.util.List;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;

import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.escidoc.core.resources.oum.Parent;
import de.escidoc.core.resources.oum.Parents;

public class OrgUnitProxy implements ResourceProxy {

    private OrganizationalUnit ou;

    public OrgUnitProxy(OrganizationalUnit ou) {
        this.ou = ou;
    }

    @Override
    public ResourceType getType() {
        return ResourceType.ORG_UNIT;
    }

    @Override
    public String getName() {
        return ou.getXLinkTitle();
    }

    @Override
    public String getId() {
        return ou.getObjid();
    }

    @Override
    public String getVersionStatus() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getStatus() {
        return ou.getProperties().getPublicStatus().toString();
    }

    @Override
    public List<String> getRelations() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getModifier() {
        return ou.getProperties().getModifiedBy().getXLinkTitle();
    }

    @Override
    public String getModifiedOn() {
        return ou.getLastModificationDate().toString("d.M.y, H:m");
    }

    @Override
    public String getLockStatus() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getDescription() {
        return ou.getProperties().getDescription();
    }

    @Override
    public String getCreator() {
        return ou.getProperties().getCreatedBy().getXLinkTitle();
    }

    @Override
    public String getCreatedOn() {
        return ou.getProperties().getCreationDate().toString("d.M.y, H:m");
    }

    @Override
    public Resource getContext() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public Resource getContentModel() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    public MetadataRecords getMedataRecords() {
        return ou.getMetadataRecords();
    }

    public List<ResourceModel> getParentList() {
        Parents parents = ou.getParents();
        List<ResourceModel> list = new ArrayList<ResourceModel>(parents.size());
        for (final Parent parent : parents) {
            ResourceModel pm = new ResourceModel() {

                @Override
                public ResourceType getType() {
                    return ResourceType.ORG_UNIT;
                }

                @Override
                public String getName() {
                    return parent.getXLinkTitle();
                }

                @Override
                public String getId() {
                    return parent.getObjid();
                }
            };
            list.add(pm);

        }
        return list;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ou == null) ? 0 : ou.hashCode());
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
        OrgUnitProxy other = (OrgUnitProxy) obj;
        if (ou == null) {
            if (other.ou != null) {
                return false;
            }
        }
        else if (!ou.equals(other.ou)) {
            return false;
        }
        return true;
    }

}
