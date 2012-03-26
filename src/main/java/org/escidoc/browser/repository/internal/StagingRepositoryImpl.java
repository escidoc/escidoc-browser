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
package org.escidoc.browser.repository.internal;

import com.google.common.base.Preconditions;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.StagingRepository;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;

import de.escidoc.core.client.StagingHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.interfaces.StagingHandlerClientInterface;

public class StagingRepositoryImpl implements StagingRepository {

    private final StagingHandlerClientInterface client;

    public StagingRepositoryImpl(final EscidocServiceLocation serviceLocation) throws MalformedURLException {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        client = new StagingHandlerClient(serviceLocation.getEscidocUrl());
    }

    @Override
    public void loginWith(final String handle) throws InternalClientException {
        client.setHandle(handle);
    }

    @Override
    public URL putFileInStagingServer(final ByteArrayInputStream inputStream) throws EscidocClientException {
        return client.upload(inputStream);
    }
}