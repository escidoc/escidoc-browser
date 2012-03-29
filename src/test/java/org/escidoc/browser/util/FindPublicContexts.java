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
package org.escidoc.browser.util;

import static org.junit.Assert.assertTrue;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.internal.EscidocServiceLocationImpl;
import org.escidoc.browser.repository.internal.ContextRepository;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

public class FindPublicContexts {
    @Ignore
    @Test
    public void shouldListMinimalOneContext() throws Exception {
        // Given X0 && ...Xn
        // When
        EscidocServiceLocationImpl a = new EscidocServiceLocationImpl();
        a.setEscidocUri("http://esfedrep1.fiz-karlsruhe.de:8080");
        ContextRepository repository = new ContextRepository(a);
        List<ResourceModel> all = repository.findAll();
        // Then ensure that
        assertTrue(!all.isEmpty());
    }

}
