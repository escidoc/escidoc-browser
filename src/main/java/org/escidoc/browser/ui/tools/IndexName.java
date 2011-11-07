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
package org.escidoc.browser.ui.tools;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Preconditions;

public enum IndexName {

    REINDEX_ALL("all"), CONTEXT_ADMIN("context_admin"), CONTENT_RELATION_ADMIN("content_relation_admin"), REINDEX_ESCIDOC_OU(
        "escidocou_all"), OU_ADMIN("ou_admin"), ESCIDOCOAIPMH_ALL("escidocoaipmh_all"), ITEM_CONTAINER_ADMIN(
        "item_container_admin"), REINDEX_ESCIDOC("escidoc_all"), CONTENT_MODEL_ADMIN("content_model_admin");

    private String internalName;

    IndexName(String internalName) {
        Preconditions.checkNotNull(internalName, "internalName is null: %s", internalName);
        this.internalName = internalName;
    }

    public String asInternalName() {
        return internalName;
    }

    private final static Set<String> set = new HashSet<String>(values().length);

    public static Collection<?> all() {
        for (final IndexName indexName : values()) {
            set.add(indexName.asInternalName());
        }
        return set;
    }

}
