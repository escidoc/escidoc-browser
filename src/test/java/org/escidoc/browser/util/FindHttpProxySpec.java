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
package org.escidoc.browser.util;

import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.escidoc.browser.ui.ViewConstants;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindHttpProxySpec {

    private static final Logger LOG = LoggerFactory.getLogger(FindHttpProxySpec.class);

    @Test
    public void shouldFindProxySetting() throws Exception {
        // Given:
        bar();
        // When:
        // AssertThat:
    }

    private void bar() {
        java.util.Properties props = System.getProperties();
        String message = props.getProperty("http.proxyHost", "NONE");
        LOG.debug("proxyHost: " + message);
    }

    void foo() throws URISyntaxException {
        List<Proxy> list = ProxySelector.getDefault().select(new URI(ViewConstants.DEFAULT_CONTENT_MODEL_URI));
        for (Proxy proxy : list) {
            // LOG.debug("proxy: " + proxy.address());

            System.out.println("proxy: " + proxy.address());

        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
