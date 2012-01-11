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

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;

import com.google.common.base.Preconditions;

import de.escidoc.core.resources.oum.OrganizationalUnit;

public final class OrgUnitModel implements ResourceModel {
    private final OrganizationalUnit ou;

    public OrgUnitModel(final OrganizationalUnit ou) {
        Preconditions.checkNotNull(ou, "ou is null: %s", ou);
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
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("OrgUnitModel [");
        if (getType() != null) {
            builder.append("getType()=").append(getType()).append(", ");
        }
        if (getName() != null) {
            builder.append("getName()=").append(getName()).append(", ");
        }
        if (getId() != null) {
            builder.append("getId()=").append(getId());
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ou == null) ? 0 : ou.hashCode());
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
        final OrgUnitModel other = (OrgUnitModel) obj;
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

    public static boolean isOrgUnit(final ResourceModel clickedResource) {
        return clickedResource.getType().equals(ResourceType.ORG_UNIT);
    }

}