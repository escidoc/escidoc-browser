package org.escidoc.browser.model;

import de.escidoc.core.resources.Resource;

public class ContextModel extends AbstractResourceModel {

    public ContextModel(final Resource resource) {
        super(resource);
    }

    @Override
    public ResourceType getType() {
        return ResourceType.CONTEXT;
    }

}
