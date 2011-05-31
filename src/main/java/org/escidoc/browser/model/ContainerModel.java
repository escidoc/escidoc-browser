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
package org.escidoc.browser.model;

import com.google.common.base.Preconditions;

import de.escidoc.core.resources.Resource;

public class ContainerModel implements ResourceModel {

    private final String id;

    private final String name;

    public ContainerModel(final Resource resource) {
        Preconditions.checkNotNull(resource, "resource is null: %s", resource);
        id = resource.getObjid();
        name = resource.getXLinkTitle();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ResourceType getType() {
        return ResourceType.CONTAINER;
    }

    @Override
    public String toString() {
        return "ContainerModel [getType()=" + getType() + ", getId()=" + getId() + ", getName()=" + getName() + "]";
    }

    public static boolean isContainer(final ResourceModel resource) {
        return resource.getType().equals(ResourceType.CONTAINER);
    }

}