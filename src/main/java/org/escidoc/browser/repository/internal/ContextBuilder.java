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

import de.escidoc.core.resources.om.context.AdminDescriptors;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;

public class ContextBuilder {

    private final Context context;

    public ContextBuilder(final Context context) {
        this.context = context;
    }

    public ContextBuilder name(final String newName) {
        context.getProperties().setName(newName);
        return this;
    }

    public ContextBuilder description(final String newDescription) {
        context.getProperties().setDescription(newDescription);
        return this;
    }

    public ContextBuilder type(final String newType) {
        context.getProperties().setType(newType);
        return this;
    }

    public ContextBuilder orgUnits(final OrganizationalUnitRefs orgUnitRefs) {
        context.getProperties().setOrganizationalUnitRefs(orgUnitRefs);
        return this;
    }

    public ContextBuilder adminDescriptors(final AdminDescriptors newAdminDescriptors) {
        if (isNotSet(newAdminDescriptors)) {
            return this;
        }
        context.setAdminDescriptors(newAdminDescriptors);
        return this;
    }

    private boolean isNotSet(final AdminDescriptors adminDescriptors) {
        return adminDescriptors == null;
    }

    public Context build() {
        return context;
    }
}