package org.escidoc.browser.repository;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.internal.HasNoNameResource;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.Resource;

public interface UtilRepository {

    ResourceModel[] findAncestors(HasNoNameResource resource) throws EscidocClientException;

    ResourceModel findParent(HasNoNameResource resource) throws EscidocClientException;

    Resource getParentContext(String id);

}
