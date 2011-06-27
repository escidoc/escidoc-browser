package org.escidoc.browser.repository;

import org.escidoc.browser.repository.internal.ContainerRepository;
import org.escidoc.browser.repository.internal.ContextRepository;
import org.escidoc.browser.repository.internal.ItemRepository;
import org.escidoc.browser.service.PdpService;

import de.escidoc.core.client.exceptions.InternalClientException;

public interface Repositories {

    void loginWith(String token) throws InternalClientException;

    ContextRepository context();

    ContainerRepository container();

    ItemRepository item();

    StagingRepository staging();

    PdpService pdp();

}
