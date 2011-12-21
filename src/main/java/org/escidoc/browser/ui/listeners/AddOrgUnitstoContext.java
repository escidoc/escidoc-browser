package org.escidoc.browser.ui.listeners;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.escidoc.browser.model.OrgUnitService;
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
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.AbstractSelect.AcceptItem;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TargetItemAllowsChildren;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.Window.Notification;

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
public class AddOrgUnitstoContext extends HorizontalLayout {

    private static String PROPERTY_NAME = "name";

    private static Object PROPERTY_HREF = "link";

    private Tree tree;

    private Table table;

    private Router router;

    private OrganizationalUnitRefs orgUnits;

    public static class OrgUnitRefTemp implements Serializable {
        private String name;

        private String href;

        public OrgUnitRefTemp(String name, String href) {
            this.name = name;
            this.href = href;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getHref() {
            return href;
        }
    }

    public AddOrgUnitstoContext(Router router, OrganizationalUnitRefs orgUnits) throws EscidocClientException {
        setSpacing(true);
        this.router = router;
        this.orgUnits = orgUnits;

        // First create the components to be able to refer to them as allowed
        // drag sources
        tree = new Tree("Drag from tree to table");
        table = new Table("Drag from table to tree");
        table.setWidth("100%");

        // Populate the tree and set up drag & drop
        initializeTree(new SourceIs(table));

        // Populate the table and set up drag & drop
        initializeTable(new SourceIs(tree));

        // Add components
        addComponent(tree);
        addComponent(table);
    }

    private void initializeTree(final ClientSideCriterion acceptCriterion) throws EscidocClientException {
        tree.setContainerDataSource(getOrgUnitRefsContainer());
        tree.setItemCaptionPropertyId(PROPERTY_NAME);

        // Expand all nodes
        for (Iterator<?> it = tree.rootItemIds().iterator(); it.hasNext();) {
            tree.expandItemsRecursively(it.next());
        }
        tree.setDragMode(TreeDragMode.NODE);
        tree.setDropHandler(new DropHandler() {
            public void drop(DragAndDropEvent dropEvent) {
                // criteria verify that this is safe
                DataBoundTransferable t = (DataBoundTransferable) dropEvent.getTransferable();
                Container sourceContainer = t.getSourceContainer();
                Object sourceItemId = t.getItemId();
                Item sourceItem = sourceContainer.getItem(sourceItemId);
                String name = sourceItem.getItemProperty("name").toString();
                String category = sourceItem.getItemProperty("category").toString();

                AbstractSelectTargetDetails dropData = ((AbstractSelectTargetDetails) dropEvent.getTargetDetails());
                Object targetItemId = dropData.getItemIdOver();

                // find category in target: the target node itself or its parent
                if (targetItemId != null && name != null && category != null) {
                    String treeCategory = getTreeNodeName(tree, targetItemId);
                    if (category.equals(treeCategory)) {
                        // move item from table to category'
                        Object newItemId = tree.addItem();
                        tree.getItem(newItemId).getItemProperty(PROPERTY_NAME).setValue(name);
                        tree.setParent(newItemId, targetItemId);
                        tree.setChildrenAllowed(newItemId, false);

                        sourceContainer.removeItem(sourceItemId);
                    }
                    else {
                        String message = name + " is not a " + treeCategory.toLowerCase().replaceAll("s$", "");
                        getWindow().showNotification(message, Notification.TYPE_WARNING_MESSAGE);
                    }
                }
            }

            public AcceptCriterion getAcceptCriterion() {
                // Only allow dropping of data bound transferables within
                // folders.
                // In this example, checking for the correct category in drop()
                // rather than in the criteria.
                return new And(acceptCriterion, TargetItemAllowsChildren.get(), AcceptItem.ALL);
            }
        });
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
                Object parentItemId = source.getParent(sourceItemId);
                // map from moved source item Id to the corresponding Hardware
                LinkedHashMap<Object, OrgUnitRefTemp> orgUnitRefMap = new LinkedHashMap<Object, OrgUnitRefTemp>();
                if (parentItemId == null) {
                    // move the whole subtree
                    String parent = getTreeNodeName(source, sourceItemId);
                    Collection<?> children = source.getChildren(sourceItemId);
                    if (children != null) {
                        for (Object childId : children) {
                            String name = getTreeNodeName(source, childId);
                            orgUnitRefMap.put(childId, new OrgUnitRefTemp(name, parent));
                        }
                    }
                }
                else {
                    // move a single hardware item
                    String category = getTreeNodeName(source, parentItemId);
                    String name = getTreeNodeName(source, sourceItemId);
                    orgUnitRefMap.put(sourceItemId, new OrgUnitRefTemp(name, category));
                }

                // move item(s) to the correct location in the table

                AbstractSelectTargetDetails dropData = ((AbstractSelectTargetDetails) dropEvent.getTargetDetails());
                Object targetItemId = dropData.getItemIdOver();

                for (Object sourceId : orgUnitRefMap.keySet()) {
                    OrgUnitRefTemp newElement = orgUnitRefMap.get(sourceId);
                    if (targetItemId != null) {
                        switch (dropData.getDropLocation()) {
                            case BOTTOM:
                                tableContainer.addItemAfter(targetItemId, newElement);
                                break;
                            case MIDDLE:
                            case TOP:
                                Object prevItemId = tableContainer.prevItemId(targetItemId);
                                tableContainer.addItemAfter(prevItemId, newElement);
                                break;
                        }
                    }
                    else {
                        tableContainer.addItem(newElement);
                    }
                    source.removeItem(sourceId);
                }
            }

            public AcceptCriterion getAcceptCriterion() {
                return new And(acceptCriterion, AcceptItem.ALL);
            }
        });
    }

    private static String getTreeNodeName(Container.Hierarchical source, Object sourceId) {
        return (String) source.getItem(sourceId).getItemProperty(PROPERTY_NAME).getValue();
    }

    private HierarchicalContainer getOrgUnitRefsContainer() throws EscidocClientException {
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