package org.escidoc.browser.model;

import de.escidoc.core.resources.Resource;

public class UserModel extends AbstractResourceModel {

    public UserModel(Resource resource) {
        super(resource);
    }

    @Override
    public ResourceType getType() {
        return ResourceType.USER_ACCOUNT;
    }

}
