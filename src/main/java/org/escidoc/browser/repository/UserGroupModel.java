package org.escidoc.browser.repository;

import org.escidoc.browser.model.AbstractResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;

import java.util.List;

import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.aa.usergroup.UserGroup;

public class UserGroupModel extends AbstractResourceModel implements ResourceProxy {

    private UserGroup userGroup;

    public UserGroupModel(UserGroup userGroup) {
        super(userGroup);
        this.userGroup = userGroup;
    }

    @Override
    public String getDescription() {
        return userGroup.getProperties().getDescription();
    }

    @Override
    public String getCreator() {
        return userGroup.getProperties().getCreatedBy().getXLinkTitle();
    }

    @Override
    public String getModifier() {
        return userGroup.getProperties().getModifiedBy().getXLinkTitle();
    }

    // FIXME date format
    @Override
    public String getCreatedOn() {
        return userGroup.getProperties().getCreationDate().toString();
    }

    @Override
    public String getModifiedOn() {
        return userGroup.getLastModificationDate().toString();
    }

    @Override
    public List<String> getRelations() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getStatus() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public Resource getContext() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getLockStatus() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getVersionStatus() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public Resource getContentModel() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public ResourceType getType() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }
}