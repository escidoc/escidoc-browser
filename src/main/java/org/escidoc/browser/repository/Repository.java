package org.escidoc.browser.repository;

import java.util.List;

import org.escidoc.browser.model.ResourceModel;

import de.escidoc.core.client.exceptions.EscidocClientException;

public interface Repository {

    List<ResourceModel> findAll() throws EscidocClientException;

}
