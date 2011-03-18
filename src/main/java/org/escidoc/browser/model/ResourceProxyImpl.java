package org.escidoc.browser.model;


import de.escidoc.core.resources.Resource;

public class ResourceProxyImpl implements ResourceProxy, ResourceModel {

    private final Resource resource;

    public ResourceProxyImpl(final Resource resource) {
        this.resource = resource;
    }

    @Override
    public String getId() {
        return resource.getObjid();
    }

    @Override
    public String getName() {
        return resource.getXLinkTitle();
    }

    @Override
    public ResourceType getType() {
        return ResourceType.valueOf(resource.getResourceType().toString());
    }

}
