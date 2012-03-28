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
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui.maincontent;

import com.google.common.base.Preconditions;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;

import org.escidoc.browser.controller.OrgUnitController;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.internal.OrgUnitService;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.view.helpers.DragnDropHelper;

import java.util.Collection;
import java.util.List;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.oum.OrganizationalUnit;

@SuppressWarnings("serial")
public class OrgUnitParentEditView extends DragnDropHelper {

    private List<ResourceModel> parentList;

    private OrgUnitController controller;

    public OrgUnitParentEditView(ResourceProxy resourceProxy, List<ResourceModel> parentList, Router router,
        OrgUnitController controller) throws EscidocClientException {
        Preconditions.checkNotNull(resourceProxy, "parentList is null: %s", resourceProxy);
        Preconditions.checkNotNull(parentList, "parentList is null: %s", parentList);
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(controller, "controller is null: %s", controller);
        this.resourceProxy = resourceProxy;
        this.parentList = parentList;
        this.router = router;
        this.controller = controller;
        createDragComponents();
    }

    @Override
    protected boolean canRemoveOperation() {
        return true;
    }

    @Override
    protected HierarchicalContainer populateContainerTable() throws EscidocClientException {
        // Create new container
        HierarchicalContainer orgUnitContainer = new HierarchicalContainer();
        // Create containerproperty for name
        orgUnitContainer.addContainerProperty(PROPERTY_NAME, String.class, null);
        orgUnitContainer.addContainerProperty(PROPERTY_HREF, String.class, null);
        List<ResourceModel> parents = parentList;
        for (ResourceModel rm : parents) {
            Item item = orgUnitContainer.addItem(rm.getId());
            item.getItemProperty(PROPERTY_NAME).setValue(rm.getName());
        }
        return orgUnitContainer;
    }

    @Override
    protected HierarchicalContainer populateContainerTree() throws EscidocClientException {
        // Create new container
        HierarchicalContainer orgUnitContainer = new HierarchicalContainer();
        // Create containerproperty for name
        orgUnitContainer.addContainerProperty(PROPERTY_NAME, String.class, null);
        orgUnitContainer.addContainerProperty(PROPERTY_HREF, String.class, null);

        OrgUnitService orgUnitService =
            new OrgUnitService(router.getServiceLocation().getEscidocUri(), router.getApp().getCurrentUser().getToken());

        Collection<OrganizationalUnit> allOrgs = orgUnitService.retrieveTopLevelOrgUnits();
        for (OrganizationalUnit organizationalUnit : allOrgs) {
            Item item = orgUnitContainer.addItem(organizationalUnit.getObjid());
            item.getItemProperty(PROPERTY_NAME).setValue(organizationalUnit.getProperties().getName());
            item.getItemProperty(PROPERTY_HREF).setValue(organizationalUnit.getXLinkHref());
            Collection<OrganizationalUnit> children = orgUnitService.retrieveChildren(organizationalUnit.getObjid());
            for (OrganizationalUnit childrenOrgUnits : children) {
                item = orgUnitContainer.addItem(childrenOrgUnits.getObjid());
                if (item != null) {
                    item.getItemProperty(PROPERTY_NAME).setValue(childrenOrgUnits.getProperties().getName());
                    item.getItemProperty(PROPERTY_HREF).setValue(childrenOrgUnits.getXLinkHref());
                    orgUnitContainer.setParent(childrenOrgUnits.getObjid(), organizationalUnit.getObjid());
                    orgUnitContainer.setChildrenAllowed(childrenOrgUnits.getObjid(), false);
                }
            }
        }
        return orgUnitContainer;
    }

    @Override
    protected void removeElementController(Object sourceItemId) {
        controller.removeParent(sourceItemId.toString());
        controller.refreshView();
    }

    @Override
    protected void addOnController(Object sourceItemId) {
        controller.addParent(resourceProxy, sourceItemId.toString());
        controller.refreshView();
    }
}