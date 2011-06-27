package org.escidoc.browser.ui;

import com.google.common.base.Preconditions;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.ContainerRepository;
import org.escidoc.browser.repository.ContextRepository;
import org.escidoc.browser.repository.ItemRepository;
import org.escidoc.browser.repository.StagingRepository;
import org.escidoc.browser.service.PdpService;
import org.escidoc.browser.service.PdpServiceImpl;

import java.net.MalformedURLException;
import java.net.URL;

import de.escidoc.core.client.exceptions.InternalClientException;

public class RepositoriesImpl implements Repositories {

    private final EscidocServiceLocation serviceLocation;

    private final ContextRepository contextRepository;

    private final ContainerRepository containerRepository;

    private final ItemRepository itemRepository;

    private final StagingRepositoryImpl stagingRepository;

    private final PdpServiceImpl pdpService;

    public RepositoriesImpl(EscidocServiceLocation serviceLocation) throws MalformedURLException {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        this.serviceLocation = serviceLocation;

        contextRepository = new ContextRepository(serviceLocation);
        containerRepository = new ContainerRepository(serviceLocation);
        itemRepository = new ItemRepository(serviceLocation);
        stagingRepository = new StagingRepositoryImpl(serviceLocation);
        pdpService = new PdpServiceImpl(new URL(serviceLocation.getEscidocUri()));
    }

    @Override
    public void loginWith(String token) throws InternalClientException {
        contextRepository.loginWith(token);
        containerRepository.loginWith(token);
        itemRepository.loginWith(token);
        stagingRepository.loginWith(token);
        pdpService.loginWith(token);
    }

    @Override
    public ContextRepository context() {
        return contextRepository;
    }

    @Override
    public ContainerRepository container() {
        return containerRepository;
    }

    @Override
    public ItemRepository item() {
        return itemRepository;
    }

    @Override
    public StagingRepository staging() {
        return staging();
    }

    @Override
    public PdpService pdp() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

}
