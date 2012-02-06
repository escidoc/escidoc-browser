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
                item.getItemProperty(PROPERTY_NAME).setValue(childrenOrgUnits.getProperties().getName());
                item.getItemProperty(PROPERTY_HREF).setValue(childrenOrgUnits.getXLinkHref());
                orgUnitContainer.setParent(childrenOrgUnits.getObjid(), organizationalUnit.getObjid());
                orgUnitContainer.setChildrenAllowed(childrenOrgUnits.getObjid(), false);
            }
        }
        return orgUnitContainer;
    }

    @Override
    protected void removeElementController(Object sourceItemId) {
        controller.removeParent(resourceProxy, sourceItemId.toString());
        controller.refreshView();
    }

    @Override
    protected void addOnController(Object sourceItemId) {
        controller.addParent(resourceProxy, sourceItemId.toString());
        controller.refreshView();
    }

}