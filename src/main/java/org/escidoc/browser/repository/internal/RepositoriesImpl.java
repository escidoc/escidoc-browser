package org.escidoc.browser.repository.internal;

import java.net.MalformedURLException;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.StagingRepository;
import org.escidoc.browser.service.PdpService;
import org.escidoc.browser.service.PdpServiceImpl;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.exceptions.InternalClientException;

public class RepositoriesImpl implements Repositories {

    private ContextRepository contextRepository;

    private ContainerRepository containerRepository;

    private ItemRepository itemRepository;

    private StagingRepository stagingRepository;

    private PdpService pdpRepository;

    private final EscidocServiceLocation serviceLocation;

    public RepositoriesImpl(final EscidocServiceLocation serviceLocation) throws MalformedURLException {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        this.serviceLocation = serviceLocation;
    }

    public Repositories createAllRepositories() throws MalformedURLException {
        contextRepository = new ContextRepository(serviceLocation);
        containerRepository = new ContainerRepository(serviceLocation);
        itemRepository = new ItemRepository(serviceLocation);
        stagingRepository = new StagingRepositoryImpl(serviceLocation);
        pdpRepository = new PdpServiceImpl(serviceLocation.getEscidocUrl());
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
    public PdpService pdp() {
        Preconditions.checkNotNull(pdpRepository, "pdpRepository is null: %s", pdpRepository);
        return pdpRepository;
    }
}