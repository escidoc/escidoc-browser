package org.escidoc.browser.ui.listeners;

import java.util.Collection;

import org.escidoc.browser.controller.ContextController;
import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.model.OrgUnitService;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextProxyImpl;
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
        Item item = null;
        // Create new container
        HierarchicalContainer orgUnitContainer = new HierarchicalContainer();
        // Create containerproperty for name
        orgUnitContainer.addContainerProperty(PROPERTY_NAME, String.class, null);
        orgUnitContainer.addContainerProperty(PROPERTY_HREF, String.class, null);
        OrgUnitService orgUnitService =
            new OrgUnitService(router.getServiceLocation().getEscidocUri(), router.getApp().getCurrentUser().getToken());
        Collection<OrganizationalUnit> allOrgs = orgUnitService.retrieveTopLevelOrgUnits();
        for (OrganizationalUnit organizationalUnit : allOrgs) {
            item = orgUnitContainer.addItem(organizationalUnit.getObjid());
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
    protected HierarchicalContainer populateContainerTable() throws EscidocClientException {
        Item item = null;
        // Create new container
        HierarchicalContainer orgUnitContainer = new HierarchicalContainer();
        // Create containerproperty for name
        orgUnitContainer.addContainerProperty(PROPERTY_NAME, String.class, null);
        orgUnitContainer.addContainerProperty(PROPERTY_HREF, String.class, null);
        Collection<OrganizationalUnitRef> allOrgs = orgUnits;
        for (OrganizationalUnitRef organizationalUnit : allOrgs) {
            item = orgUnitContainer.addItem(organizationalUnit.getObjid());
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
        controller.removeOrgUnitFromContext(resourceProxy, sourceItemId.toString());
        controller.refreshView();
    }

    @Override
    protected void addOnController(Object sourceItemId) {
        controller.addOrgUnitToContext(resourceProxy, sourceItemId.toString());
        controller.refreshView();
    }
}