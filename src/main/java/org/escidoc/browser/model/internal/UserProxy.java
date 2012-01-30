package org.escidoc.browser.model.internal;

import com.google.common.base.Preconditions;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;

import java.util.List;

import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.aa.useraccount.UserAccount;

public class UserProxy implements ResourceProxy {

    private UserAccount ua;

    public UserProxy(UserAccount ua) {
        Preconditions.checkNotNull(ua, "ua is null: %s", ua);
        this.ua = ua;
    }

    @Override
    public String getName() {
        return ua.getXLinkTitle();
    }

    public String getLoginName() {
        return ua.getProperties().getLoginName();
    }

    @Override
    public String getId() {
        return ua.getObjid();
    }

    @Override
    public ResourceType getType() {
        return ResourceType.USER_ACCOUNT;
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getStatus() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getCreator() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getCreatedOn() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getModifier() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getModifiedOn() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public List<String> getRelations() {
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

    public UserAccount getResource() {
        return ua;
    }

}
