package org.escidoc.browser.repository.internal;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;

import java.util.ArrayList;
import java.util.List;

import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.escidoc.core.resources.oum.Parent;
import de.escidoc.core.resources.oum.Parents;

public class OrgUnitProxy implements ResourceProxy {

    private OrganizationalUnit ou;

    public OrgUnitProxy(OrganizationalUnit ou) {
        this.ou = ou;
    }

    @Override
    public ResourceType getType() {
        return ResourceType.ORG_UNIT;
    }

    @Override
    public String getName() {
        return ou.getXLinkTitle();
    }

    @Override
    public String getId() {
        return ou.getObjid();
    }

    @Override
    public String getVersionStatus() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getStatus() {
        return ou.getProperties().getPublicStatus().toString();
    }

    @Override
    public List<String> getRelations() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getModifier() {
        return ou.getProperties().getModifiedBy().getXLinkTitle();
    }

    @Override
    public String getModifiedOn() {
        return ou.getLastModificationDate().toString("d.M.y, H:m");
    }

    @Override
    public String getLockStatus() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getCreator() {
        return ou.getProperties().getCreatedBy().getXLinkTitle();
    }

    @Override
    public String getCreatedOn() {
        return ou.getProperties().getCreationDate().toString("d.M.y, H:m");
    }

    @Override
    public Resource getContext() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public Resource getContentModel() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    public MetadataRecords getMedataRecords() {
        return ou.getMetadataRecords();
    }

    public List<ResourceModel> getParentList() {
        Parents parents = ou.getParents();
        List<ResourceModel> list = new ArrayList<ResourceModel>(parents.size());
        for (final Parent parent : parents) {
            ResourceModel pm = new ResourceModel() {

                @Override
                public ResourceType getType() {
                    return ResourceType.ORG_UNIT;
                }

                @Override
                public String getName() {
                    return parent.getXLinkTitle();
                }

                @Override
                public String getId() {
                    return parent.getObjid();
                }
            };
            list.add(pm);

        }
        return list;
    }
}
