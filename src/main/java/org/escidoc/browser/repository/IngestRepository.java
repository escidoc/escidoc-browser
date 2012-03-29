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
package org.escidoc.browser.repository;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.core.client.ingest.zip.ZipIngester;
import org.escidoc.core.tme.IngestResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;

public class IngestRepository {

    private ZipIngester zipIngester;

    private URL serviceLocationUrl;

    public IngestRepository(EscidocServiceLocation serviceLocation) throws MalformedURLException {
        serviceLocationUrl = serviceLocation.getEscidocUrl();
    }

    public void loginWith(String token) {
        zipIngester = new ZipIngester(serviceLocationUrl, token);
    }

    public List<IngestResult> ingestZip(InputStream inputStream) throws EscidocException, InternalClientException,
        TransportException, UnsupportedEncodingException, IOException {
        return zipIngester.ingest(inputStream);
    }
}
