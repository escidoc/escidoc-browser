package org.escidoc.browser.model;

import java.util.List;

import de.escidoc.core.resources.om.context.Context;

public class ContextProxyImpl implements ResourceProxy, ResourceModel {

    private final Context contextFromCore;

    public ContextProxyImpl(final Context resource) {
        this.contextFromCore = resource;
    }

    @Override
    public String getId() {
        return contextFromCore.getObjid();
    }

    @Override
    public String getName() {
        return contextFromCore.getXLinkTitle();
    }

    @Override
    public ResourceType getType() {
        return ResourceType.valueOf(contextFromCore.getResourceType().toString());
    }

    @Override
    public String getDescription() {
        return contextFromCore.getProperties().getDescription();
    }

    @Override
    public String getStatus() {
        return contextFromCore.getProperties().getPublicStatus();
    }

    @Override
    public String getCreator() {
        return contextFromCore.getProperties().getCreatedBy().getObjid();
    }

    @Override
    public String getCreatedOn() {
        return contextFromCore.getProperties().getCreationDate().toString();
    }

    @Override
    public String getModifier() {
        return contextFromCore.getProperties().getModifiedBy().getObjid();
    }

    @Override
    public String getModifiedOn() {
        return contextFromCore.getLastModificationDate().toString();
    }

    @Override
    public List<String> getRelations() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
