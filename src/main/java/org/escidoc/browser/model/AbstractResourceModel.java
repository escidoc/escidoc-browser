package org.escidoc.browser.model;

import de.escidoc.core.resources.Resource;

public abstract class AbstractResourceModel implements ResourceModel {

    private final String id;

    private final String name;

    public AbstractResourceModel(final Resource resource) {
        id = resource.getObjid();
        name = resource.getXLinkTitle();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public abstract ResourceType getType();
}