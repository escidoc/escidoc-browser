package org.escidoc.browser.ui.listeners;

import java.util.Collection;
import java.util.Iterator;

import org.escidoc.browser.controller.ContextController;
import org.escidoc.browser.model.OrgUnitService;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.ui.Router;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.And;
import com.vaadin.event.dd.acceptcriteria.ClientSideCriterion;
import com.vaadin.event.dd.acceptcriteria.SourceIs;
import com.vaadin.ui.AbstractSelect.AcceptItem;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.VerticalLayout;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;
import de.escidoc.core.resources.oum.OrganizationalUnit;

/**
 * Demonstrate moving data back and forth between a table and a tree using drag and drop.
 * 
 * The tree and the table use different data structures: The category is a separate node in the tree and each item just
 * has a String, whereas the table contains items with both a name and a category. Data conversions between these
 * representations are made during drop processing.
 */
public class AddOrgUnitstoContext extends VerticalLayout {

    private static String PROPERTY_NAME = "name";

    private static Object PROPERTY_HREF = "link";

    private Tree tree;

    private Table table;

    private Router router;

    private OrganizationalUnitRefs orgUnits;

    private ContextProxyImpl resourceProxy;

    private ContextController controller;

    private Table tableDelete;

    public AddOrgUnitstoContext(Router router, ContextProxyImpl resourceProxy, ContextController contextController,
        OrganizationalUnitRefs orgUnits) throws EscidocClientException {
        setSpacing(true);
        this.router = router;
        this.orgUnits = orgUnits;
        this.resourceProxy = resourceProxy;
        this.controller = contextController;

        createDragComponents();
    }

    private void createDragComponents() throws EscidocClientException {
        tree = new Tree("Drag from tree to table");
        table = new Table("Drag from table to tree");
        table.setWidth("100%");

        // Populate the tree and set up as drag
        initializeTree(new SourceIs(table));
        // Populate the table and set up drag & drop
        initializeTable(new SourceIs(tree));

        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(tree);
        hl.addComponent(table);

        VerticalLayout vl = new VerticalLayout();
        tableDelete = new Table("Drag here te remove");

        initializeDeleteTable(new SourceIs(table));

        vl.addComponent(tableDelete);

        addComponent(hl);
        addComponent(vl);
    }

    private void initializeDeleteTable(final ClientSideCriterion acceptCriterion) throws EscidocClientException {
        tableDelete.setWidth("100%");
        tableDelete.setHeight("100px");
        tableDelete.setDropHandler(new DropHandler() {
            public void drop(DragAndDropEvent dropEvent) {
                DataBoundTransferable t = (DataBoundTransferable) dropEvent.getTransferable();
                if (!(t.getSourceContainer() instanceof Container.Hierarchical)) {
                    return;
                }
                Container.Hierarchical source = (Container.Hierarchical) t.getSourceContainer();
                source.removeItem(t.getItemId());
                Object sourceItemId = t.getItemId();
                controller.removeOrgUnitFromContext(resourceProxy, sourceItemId.toString());
                controller.refreshView();
            }

            public AcceptCriterion getAcceptCriterion() {
                return new And(acceptCriterion, AcceptItem.ALL);
            }
        });

    }

    private void initializeTree(final ClientSideCriterion acceptCriterion) throws EscidocClientException {
        tree.setContainerDataSource(getOrgUnitRefsContainerTree());
        tree.setItemCaptionPropertyId(PROPERTY_NAME);

        // Expand all nodes
        for (Iterator<?> it = tree.rootItemIds().iterator(); it.hasNext();) {
            tree.expandItemsRecursively(it.next());
        }
        tree.setDragMode(TreeDragMode.NODE);
    }

    private void initializeTable(final ClientSideCriterion acceptCriterion) throws EscidocClientException {
        final HierarchicalContainer tableContainer = getOrgUnitRefsContainerTable();
        table.setContainerDataSource(tableContainer);
        table.setItemCaptionPropertyId(PROPERTY_NAME);
        table.setVisibleColumns(new Object[] { PROPERTY_NAME, PROPERTY_HREF });

        // Handle drop in table: move hardware item or subtree to the table
        table.setDragMode(TableDragMode.ROW);
        table.setDropHandler(new DropHandler() {
            public void drop(DragAndDropEvent dropEvent) {
                // criteria verify that this is safe
                DataBoundTransferable t = (DataBoundTransferable) dropEvent.getTransferable();
                if (!(t.getSourceContainer() instanceof Container.Hierarchical)) {
                    return;
                }
                Container.Hierarchical source = (Container.Hierarchical) t.getSourceContainer();
                Object sourceItemId = t.getItemId();
                if (tableContainer.getItem(sourceItemId) == null) {
                    createItem(tableContainer, sourceItemId.toString(), getTreeNodeName(source, sourceItemId),
                        getTreeNodeHref(source, sourceItemId));
                    controller.addOrgUnitToContext(resourceProxy, sourceItemId.toString());
                    controller.refreshView();
                }
            }

            public AcceptCriterion getAcceptCriterion() {
                return new And(acceptCriterion, AcceptItem.ALL);
            }
        });
    }

    private Item createItem(HierarchicalContainer tableContainer, String itemId, String itemName, String itemHref) {
        Item item = tableContainer.addItem(itemId);
        item.getItemProperty(PROPERTY_NAME).setValue(itemName);
        item.getItemProperty(PROPERTY_HREF).setValue(itemHref);
        return item;
    }

    private static String getTreeNodeName(Container.Hierarchical source, Object sourceId) {
        return (String) source.getItem(sourceId).getItemProperty(PROPERTY_NAME).getValue();
    }

    private static String getTreeNodeHref(Container.Hierarchical source, Object sourceId) {
        return (String) source.getItem(sourceId).getItemProperty(PROPERTY_HREF).getValue();
    }

    private HierarchicalContainer getOrgUnitRefsContainerTree() throws EscidocClientException {
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

    private HierarchicalContainer getOrgUnitRefsContainerTable() throws EscidocClientException {
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
}