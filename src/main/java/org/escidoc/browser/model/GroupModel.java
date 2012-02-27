package org.escidoc.browser.model;

import de.escidoc.core.resources.Resource;

public class GroupModel extends AbstractResourceModel implements ResourceModel {

    public GroupModel(Resource resource) {
        super(resource);
    }

    @Override
    public ResourceType getType() {
        return ResourceType.USER_GROUP;
    }

}