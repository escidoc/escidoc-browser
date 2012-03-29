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

import org.escidoc.browser.model.AbstractResourceModel;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;

import de.escidoc.core.resources.Resource;

public class ContextModel extends AbstractResourceModel {

    private boolean hasChildren;

    public ContextModel(final Resource resource) {
        super(resource);
    }

    @Override
    public ResourceType getType() {
        return ResourceType.CONTEXT;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ContextModel [");
        if (getType() != null) {
            builder.append("getType()=").append(getType()).append(", ");
        }
        if (getId() != null) {
            builder.append("getId()=").append(getId()).append(", ");
        }
        if (getName() != null) {
            builder.append("getName()=").append(getName());
        }
        builder.append("]");
        return builder.toString();
    }

    public static boolean isContext(final ResourceModel resource) {
        return resource.getType().equals(ResourceType.CONTEXT);
    }

    public void hasChildren(final boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public boolean hasChildren() {
        return hasChildren;
    }
}