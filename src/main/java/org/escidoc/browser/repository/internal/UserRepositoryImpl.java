package org.escidoc.browser.repository.internal;

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.internal.GuestUser;
import org.escidoc.browser.model.internal.LoggedInUser;
import org.escidoc.browser.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.escidoc.core.client.TransportProtocol;
import de.escidoc.core.client.UserAccountHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;

public class UserRepositoryImpl implements UserRepository {

    private static final Logger LOG = LoggerFactory
        .getLogger(UserRepositoryImpl.class);

    private final UserAccountHandlerClient client;

    public UserRepositoryImpl(final EscidocServiceLocation serviceLocation) {
        client = new UserAccountHandlerClient(serviceLocation.getEscidocUri());
        client.setTransport(TransportProtocol.REST);
    }

    public void withToken(final String token) {
        client.setHandle(token);
    }

    public CurrentUser findCurrentUser() {
        try {
            return new LoggedInUser(client.retrieveCurrentUser());
        }
        catch (final EscidocClientException e) {
            LOG.info("e: " + e.getMessage());
            return new GuestUser();
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("UserRepositoryImpl [");
        if (client != null) {
            builder.append("client=").append(client);
        }
        builder.append("]");
        return builder.toString();
    }

}
