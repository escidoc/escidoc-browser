package org.escidoc.browser.repository;

import com.google.common.base.Preconditions;

import org.escidoc.browser.model.AbstractResourceModel;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.util.Utils;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import de.escidoc.core.client.RoleHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.interfaces.RoleHandlerClientInterface;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.aa.role.Role;
import de.escidoc.core.resources.aa.role.ScopeDef;

public class RoleRepository {

    public static class RoleModel extends AbstractResourceModel {

        private Role role;

        public RoleModel(Resource resource) {
            super(resource);

            role = (Role) resource;
        }

        @Override
        public ResourceType getType() {
            return ResourceType.ROLE;
        }

        public static boolean isValid(ResourceModel resourceModel) {
            return notUserGroup(resourceModel.getName()) && notStatistic(resourceModel.getName())
                && notContentRelation(resourceModel.getName()) && notAudience(resourceModel.getName())
                && notOrgUnitAdmint(resourceModel.getName());
        }

        static boolean notContentRelation(final String roleName) {
            return !roleName.startsWith("ContentRelation");
        }

        static boolean notOrgUnitAdmint(final String roleName) {
            return !roleName.startsWith("OU-Admin");
        }

        static boolean notAudience(final String roleName) {
            return !roleName.startsWith("Audience");
        }

        static boolean notStatistic(final String roleName) {
            return !roleName.startsWith("Statistics");
        }

        static boolean notUserGroup(final String roleName) {
            return !roleName.startsWith("User-Group");
        }

        public List<ScopeDef> getScopeDefinitions() {
            return role.getScope().getScopeDefinitions();
        }
    }

    private RoleHandlerClientInterface c;

    public RoleRepository(EscidocServiceLocation esl) throws MalformedURLException {
        Preconditions.checkNotNull(esl, "esl is null: %s", esl);
        c = new RoleHandlerClient(esl.getEscidocUrl());
    }

    public List<ResourceModel> findAll() throws EscidocClientException {
        List<Role> list = c.retrieveRolesAsList(Utils.createEmptyFilter());
        List<ResourceModel> retVal = new ArrayList<ResourceModel>(list.size());
        for (Role role : list) {
            retVal.add(new RoleModel(role));
        }
        return retVal;
    }

    public void loginWith(String token) throws EscidocClientException {
        c.setHandle(token);
    }
}