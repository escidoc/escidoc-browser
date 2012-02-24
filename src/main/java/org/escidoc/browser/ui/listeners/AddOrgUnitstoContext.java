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
package org.escidoc.browser.ui.listeners;

import java.util.Collection;

import org.escidoc.browser.controller.ContextController;
import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.repository.internal.OrgUnitService;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.view.helpers.DragnDropHelper;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;
import de.escidoc.core.resources.oum.OrganizationalUnit;

/**
 * Removing the Organizational Units from a Context
 * 
 */
@SuppressWarnings("serial")
public class AddOrgUnitstoContext extends DragnDropHelper {

    private OrganizationalUnitRefs orgUnits;

    private ContextController controller;

    private ContextProxyImpl resourceProxy;

    public AddOrgUnitstoContext(Router router, ResourceProxy resourceProxy, Controller contextController,
        OrganizationalUnitRefs orgUnits) throws EscidocClientException {
        setSpacing(true);
        this.router = router;
        this.orgUnits = orgUnits;
        this.resourceProxy = (ContextProxyImpl) resourceProxy;
        this.controller = (ContextController) contextController;

        createDragComponents();
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
    protected HierarchicalContainer populateContainerTable() throws EscidocClientException {
        // Create new container
        HierarchicalContainer orgUnitContainer = new HierarchicalContainer();
        // Create containerproperty for name
        orgUnitContainer.addContainerProperty(PROPERTY_NAME, String.class, null);
        orgUnitContainer.addContainerProperty(PROPERTY_HREF, String.class, null);
        Collection<OrganizationalUnitRef> allOrgs = orgUnits;
        for (OrganizationalUnitRef organizationalUnit : allOrgs) {
            Item item = orgUnitContainer.addItem(organizationalUnit.getObjid());
            item.getItemProperty(PROPERTY_NAME).setValue(organizationalUnit.getXLinkTitle());
            item.getItemProperty(PROPERTY_HREF).setValue(organizationalUnit.getXLinkHref());
        }
        return orgUnitContainer;
    }

    @Override
    protected boolean canRemoveOperation() {
        return controller.canRemoveOUs();
    }

    @Override
    protected void removeElementController(Object sourceItemId) {
        controller.removeOrgUnitFromContext(sourceItemId.toString());
        controller.refreshView();
    }

    @Override
    protected void addOnController(Object sourceItemId) {
        controller.addOrgUnitToContext(resourceProxy, sourceItemId.toString());
        controller.refreshView();
    }
}