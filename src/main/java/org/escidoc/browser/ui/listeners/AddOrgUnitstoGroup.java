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
package org.escidoc.browser.ui.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.controller.UserGroupController;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.UserGroupModel;
import org.escidoc.browser.repository.internal.OrgUnitService;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.view.helpers.DragnDropHelper;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.resources.aa.usergroup.Selector;
import de.escidoc.core.resources.oum.OrganizationalUnit;

/**
 * Removing the Organizational Units from a Context
 * 
 */
@SuppressWarnings("serial")
public class AddOrgUnitstoGroup extends DragnDropHelper {

    private UserGroupController controller;

    private UserGroupModel resourceProxy;

    private HierarchicalContainer orgUnitContainer;

    private OrgUnitService orgUnitService;

    public AddOrgUnitstoGroup(Router router, ResourceProxy resourceProxy, Controller controller)
        throws EscidocClientException {
        setSpacing(true);
        this.router = router;
        this.resourceProxy = (UserGroupModel) resourceProxy;
        this.controller = (UserGroupController) controller;
        orgUnitService =
            new OrgUnitService(router.getServiceLocation().getEscidocUri(), router.getApp().getCurrentUser().getToken());
        createDragComponents();
    }

    @Override
    protected HierarchicalContainer populateContainerTree() throws EscidocClientException {
        // Create new container
        orgUnitContainer = new HierarchicalContainer();
        // Create containerproperty for name
        orgUnitContainer.addContainerProperty(PROPERTY_NAME, String.class, null);
        orgUnitContainer.addContainerProperty(PROPERTY_HREF, String.class, null);

        Collection<OrganizationalUnit> allOrgs = orgUnitService.retrieveTopLevelOrgUnits();
        for (OrganizationalUnit organizationalUnit : allOrgs) {
            Item item = orgUnitContainer.addItem(organizationalUnit.getObjid());
            item.getItemProperty(PROPERTY_NAME).setValue(organizationalUnit.getProperties().getName());
            item.getItemProperty(PROPERTY_HREF).setValue(organizationalUnit.getObjid());
        }
        return orgUnitContainer;
    }

    protected void addActionListener() throws InternalClientException, EscidocClientException {
        tree.addListener(new ExpandListener() {

            @Override
            public void nodeExpand(ExpandEvent event) {
                router.getMainWindow().showNotification(event.getItemId().toString());
                try {
                    Collection<OrganizationalUnit> children =
                        orgUnitService.retrieveChildren(event.getItemId().toString());
                    for (OrganizationalUnit childrenOrgUnits : children) {
                        Item item = orgUnitContainer.addItem(childrenOrgUnits.getObjid());
                        if (item != null) {
                            item.getItemProperty(PROPERTY_NAME).setValue(childrenOrgUnits.getProperties().getName());
                            item.getItemProperty(PROPERTY_HREF).setValue(childrenOrgUnits.getObjid());
                            boolean bole =
                                orgUnitContainer.setParent(childrenOrgUnits.getObjid(), event.getItemId().toString());
                        }

                    }

                }
                catch (EscidocClientException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected HierarchicalContainer populateContainerTable() throws EscidocClientException {
        // Create new container
        HierarchicalContainer orgUnitContainer = new HierarchicalContainer();

        // Create containerproperty for name
        orgUnitContainer.addContainerProperty(PROPERTY_NAME, String.class, null);
        orgUnitContainer.addContainerProperty(PROPERTY_HREF, String.class, null);

        List<ResourceModel> allOrgs = new ArrayList<ResourceModel>();
        for (final Selector s : resourceProxy.getSelector()) {
            ResourceProxy findById = router.getRepositories().organization().findById(s.getContent());
            allOrgs.add(findById);
        }

        for (ResourceModel organizationalUnit : allOrgs) {
            Item item = orgUnitContainer.addItem(organizationalUnit.getId());
            item.getItemProperty(PROPERTY_NAME).setValue(organizationalUnit.getName());
            item.getItemProperty(PROPERTY_HREF).setValue(organizationalUnit.getId());
        }
        return orgUnitContainer;
    }

    @Override
    protected boolean canRemoveOperation() {
        return controller.canRemoveOUs();
    }

    @Override
    protected void removeElementController(Object sourceItemId) {
        Repositories repositories = router.getRepositories();
        try {
            repositories.group().removeOrganization(resourceProxy.getId(), (String) sourceItemId);
            // selectorTable.removeItem(sourceItemId);
            router.getMainWindow().showNotification(
                "Organization with the id " + resourceProxy.getId() + " is removed from the group.",
                Window.Notification.TYPE_TRAY_NOTIFICATION);
        }
        catch (EscidocClientException e) {
            router.getMainWindow().showNotification("Error ", e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);

        }
    }

    @Override
    protected void addOnController(Object sourceItemId) {
        try {
            controller.addOrgUnitToGroup(resourceProxy, sourceItemId.toString());
            controller.refreshView();
            router.getMainWindow().showNotification("Group, " + resourceProxy.getName() + ", is updated",
                Window.Notification.TYPE_TRAY_NOTIFICATION);
        }
        catch (EscidocClientException e) {
            router.getMainWindow().showNotification(
                "Group, " + resourceProxy.getName() + ", could not be updated" + e.getLocalizedMessage(),
                Window.Notification.TYPE_TRAY_NOTIFICATION);
        }

    }
}