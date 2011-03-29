package org.escidoc.browser.repository;

import java.util.List;

import org.escidoc.browser.model.ContextProxyImpl;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;

import de.escidoc.core.resources.om.container.Container;
import de.escidoc.core.resources.om.context.Context;

public class ContainerProxyImpl implements ResourceProxy {
    private final Container containerFromCore;

    public ContainerProxyImpl(final Container resource) {
        containerFromCore = resource;
    }

    @Override
    public String getId() {
        return containerFromCore.getObjid();
    }

    @Override
    public String getName() {
        return containerFromCore.getXLinkTitle();
    }

    @Override
    public ResourceType getType() {
        return ResourceType.valueOf(containerFromCore
            .getResourceType().toString());
    }

    @Override
    public String getDescription() {
        return containerFromCore.getProperties().getDescription();
    }

    @Override
    public String getStatus() {
        return containerFromCore.getProperties().getPublicStatus();
    }

    @Override
    public String getCreator() {
        return containerFromCore.getProperties().getCreatedBy().getObjid();
    }

    @Override
    public String getCreatedOn() {
        return containerFromCore.getProperties().getCreationDate().toString();
    }

    @Override
    public String getModifier() {
        return containerFromCore.getProperties().getCreatedBy().getObjid();
    }

    @Override
    public String getModifiedOn() {
        return containerFromCore.getLastModificationDate().toString();
    }

    @Override
    public List<String> getRelations() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
