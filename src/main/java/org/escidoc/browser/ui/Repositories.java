package org.escidoc.browser.ui;

import org.escidoc.browser.repository.ContainerRepository;
import org.escidoc.browser.repository.ContextRepository;
import org.escidoc.browser.repository.ItemRepository;
import org.escidoc.browser.repository.StagingRepository;
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
