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
 * Copyright ${year} Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.model.internal;

import org.escidoc.browser.model.ResourceType;

public class HasNoNameResourceImpl implements HasNoNameResource {

    private final String resourceId;

    private final ResourceType type;

    public HasNoNameResourceImpl(final String resourceId, final ResourceType type) {
        this.resourceId = resourceId;
        this.type = type;
    }

    public String getId() {
        return resourceId;
    }

    public ResourceType getType() {
        return type;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("HasNoNameResource [");
        if (getId() != null) {
            builder.append("getId()=").append(getId()).append(", ");
        }
        if (getType() != null) {
            builder.append("getType()=").append(getType());
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        final HasNoNameResourceImpl other = (HasNoNameResourceImpl) obj;
        if (resourceId == null) {
            if (other.resourceId != null) {
                return false;
            }
        }
        else if (!resourceId.equals(other.resourceId)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }

}
