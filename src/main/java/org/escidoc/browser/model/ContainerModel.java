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
package org.escidoc.browser.model;

import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.common.structmap.StructMap;
import de.escidoc.core.resources.om.container.Container;

public class ContainerModel extends AbstractResourceModel {

    public ContainerModel(final Resource resource) {
        super(resource);
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

    public boolean hasMember() {
        return !(empty(getStructMap()) || (emptyContainerAndItem(getStructMap())));
    }

    private StructMap getStructMap() {
        return ((Container) super.getResource()).getStructMap();
    }

    private static boolean emptyContainerAndItem(final StructMap structMap) {
        return emptyContainerMember(structMap) && emptyItemMember(structMap);
    }

    private static boolean emptyItemMember(final StructMap structMap) {
        return structMap.getItems() == null || structMap.getItems().size() == 0;
    }

    private static boolean emptyContainerMember(final StructMap structMap) {
        return structMap.getContainers() == null || structMap.getContainers().size() == 0;
    }

    private static boolean empty(final StructMap structMap) {
        return structMap == null || structMap.size() == 0;
    }
}