package org.escidoc.browser.ui;

import com.google.common.base.Preconditions;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.StagingRepository;

import java.io.ByteArrayInputStream;
import java.net.URL;

import de.escidoc.core.client.exceptions.InternalClientException;

public class StagingRepositoryImpl implements StagingRepository {

    private final EscidocServiceLocation serviceLocation;

    public StagingRepositoryImpl(EscidocServiceLocation serviceLocation) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        this.serviceLocation = serviceLocation;
    }

    @Override
    public void loginWith(String handle) throws InternalClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public URL putFileInStagingServer(ByteArrayInputStream byteArrayInputStream) {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

}
