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

import java.net.MalformedURLException;

import org.escidoc.browser.model.EscidocServiceLocation;

import de.escidoc.core.client.IngestHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.interfaces.IngestHandlerClientInterface;

public class IngestRepository {

    private IngestHandlerClientInterface client;

    public IngestRepository(EscidocServiceLocation serviceLocation) throws MalformedURLException {
        client = new IngestHandlerClient(serviceLocation.getEscidocUrl());
    }

    public void loginWith(String token) throws EscidocClientException {
        client.setHandle(token);
    }

    public void ingest(String resourceXml) throws EscidocClientException {
        client.ingest(resourceXml);
    }
}
