package org.escidoc.browser.repository.internal;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.StagingRepository;

import com.google.common.base.Preconditions;

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