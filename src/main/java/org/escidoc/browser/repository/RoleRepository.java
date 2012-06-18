/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or https://www.escidoc.org/license/ESCIDOC.LICENSE .
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *
 * Copyright 2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
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

            // role = (Role) resource;
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

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + ((role == null) ? 0 : role.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            RoleModel other = (RoleModel) obj;
            if (role == null) {
                if (other.role != null) {
                    return false;
                }
            }
            else if (!role.equals(other.role)) {
                return false;
            }
            return true;
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