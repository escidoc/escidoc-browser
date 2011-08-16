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

import java.net.MalformedURLException;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.PdpRepository;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.StagingRepository;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.InternalClientException;

public class RepositoriesImpl implements Repositories {

    private final EscidocServiceLocation serviceLocation;

    private ContextRepository contextRepository;

    private ContainerRepository containerRepository;

    private ItemRepository itemRepository;

    private StagingRepository stagingRepository;

    private PdpRepository pdpRepository;

    private ContentModelRepository contentModelRepository;

    private final Window mainWindow;

    public RepositoriesImpl(final EscidocServiceLocation serviceLocation, Window mainWindow)
        throws MalformedURLException {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        this.serviceLocation = serviceLocation;
        this.mainWindow = mainWindow;
    }

    public Repositories createAllRepositories() throws MalformedURLException {
        contextRepository = new ContextRepository(serviceLocation);
        containerRepository = new ContainerRepository(serviceLocation, mainWindow);
        itemRepository = new ItemRepository(serviceLocation, mainWindow);
        stagingRepository = new StagingRepositoryImpl(serviceLocation);
        pdpRepository = new PdpRepositoryImpl(serviceLocation.getEscidocUrl());
        contentModelRepository = new ContentModelRepository(serviceLocation);
        return this;
    }

    @Override
    public void loginWith(final String token) throws InternalClientException {
        contextRepository.loginWith(token);
        containerRepository.loginWith(token);
        itemRepository.loginWith(token);
        stagingRepository.loginWith(token);
        pdpRepository.loginWith(token);
    }

    @Override
    public ContextRepository context() {
        Preconditions.checkNotNull(contextRepository, "contextRepository is null: %s", contextRepository);
        return contextRepository;
    }

    @Override
    public ContainerRepository container() {
        Preconditions.checkNotNull(containerRepository, "containerRepository is null: %s", containerRepository);
        return containerRepository;
    }

    @Override
    public ItemRepository item() {
        Preconditions.checkNotNull(itemRepository, "itemRepository is null: %s", itemRepository);
        return itemRepository;
    }

    @Override
    public StagingRepository staging() {
        Preconditions.checkNotNull(stagingRepository, "stagingRepository is null: %s", stagingRepository);
        return stagingRepository;
    }

    @Override
    public PdpRepository pdp() {
        Preconditions.checkNotNull(pdpRepository, "pdpRepository is null: %s", pdpRepository);
        return pdpRepository;
    }

    @Override
    public ContentModelRepository contentModel() {
        Preconditions
            .checkNotNull(contentModelRepository, "contentModelRepository is null: %s", contentModelRepository);
        return contentModelRepository;
    }
}