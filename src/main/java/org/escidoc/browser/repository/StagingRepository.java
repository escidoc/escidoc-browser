package org.escidoc.browser.repository;

import java.io.ByteArrayInputStream;
import java.net.URL;

import de.escidoc.core.client.exceptions.InternalClientException;

public interface StagingRepository {

    void loginWith(final String handle) throws InternalClientException;

    URL putFileInStagingServer(ByteArrayInputStream byteArrayInputStream);

}
