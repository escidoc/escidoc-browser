package org.escidoc.browser.repository;

import org.escidoc.browser.model.CurrentUser;

import de.escidoc.core.client.exceptions.EscidocClientException;

public interface UserRepository {
    CurrentUser findCurrentUser() throws EscidocClientException;
}