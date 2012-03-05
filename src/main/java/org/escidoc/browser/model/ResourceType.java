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

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;

public enum ResourceType {

    CONTEXT("Context"), CONTAINER("Container"), ITEM("Item"), CONTENT_MODEL("Content Model"), ORG_UNIT(
        "Organizational Unit"), USER_ACCOUNT("User Account"), ROLE("Role"), CONTENT_RELATION("Content Relation"), USER_GROUP(
        "User Group"), COMPONENT("Component");

    @SuppressWarnings("serial")
    private static final Map<de.escidoc.core.resources.ResourceType, ResourceType> map =
        new HashMap<de.escidoc.core.resources.ResourceType, ResourceType>() {
            {
                put(de.escidoc.core.resources.ResourceType.ITEM, ITEM);
                put(de.escidoc.core.resources.ResourceType.CONTAINER, CONTAINER);
                put(de.escidoc.core.resources.ResourceType.CONTEXT, CONTEXT);
                put(de.escidoc.core.resources.ResourceType.ORGANIZATIONAL_UNIT, ORG_UNIT);
                put(de.escidoc.core.resources.ResourceType.CONTENT_MODEL, CONTENT_MODEL);
                put(de.escidoc.core.resources.ResourceType.COMPONENT, COMPONENT);
                put(de.escidoc.core.resources.ResourceType.CONTENT_RELATION, CONTENT_RELATION);
                put(de.escidoc.core.resources.ResourceType.USERACCOUNT, USER_ACCOUNT);
                put(de.escidoc.core.resources.ResourceType.USERGROUP, USER_GROUP);
            }
        };

    private String label;

    private ResourceType(final String label) {
        Preconditions.checkNotNull(label, "value is null: %s", label);
        this.label = label;
    }

    private ResourceType() {
        label = "";
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return label;
    }

    public static ResourceType convert(de.escidoc.core.resources.ResourceType relationAttributeObjectType) {
        return map.get(relationAttributeObjectType);
    }
}