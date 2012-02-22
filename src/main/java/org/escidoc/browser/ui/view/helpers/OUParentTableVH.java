package org.escidoc.browser.ui.view.helpers;

import java.util.List;

import org.escidoc.browser.controller.OrgUnitController;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.repository.internal.OrgUnitProxy;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;

public class OUParentTableVH extends TableContainerVH {
    private OrganizationalUnitRefs organizationalUnits;

    private OrgUnitController controller;

    private OrgUnitProxy orgUnitProxy;

    private Router router;

    private Action ACTION_DELETE = new Action("Delete Organizational Unit");

    private Action ACTION_ADD = new Action("Add Organizational Unit");

    private Action ACTION_EDIT = new Action("Edit Organizational Unit");

    private Action[] ACTIONS_LIST = new Action[] { ACTION_ADD, ACTION_EDIT, ACTION_DELETE };

    private HierarchicalContainer tableContainer;

    public OUParentTableVH(OrgUnitProxy orgUnitProxy, Router router, OrgUnitController controller) {
        this.orgUnitProxy = orgUnitProxy;
        this.router = router;
        this.controller = controller;
        table.setContainerDataSource(populateContainerTable());
    }

    @Override
    protected void addActionLists() {
        table.addActionHandler(new Action.Handler() {
            @Override
            public Action[] getActions(Object target, Object sender) {
                return ACTIONS_LIST;
            }

            @Override
            public void handleAction(Action action, Object sender, Object target) {
                if (ACTION_DELETE == action) {
                    confirmActionWindow(target);
                }
                else {
                    openViewAddRemoveOUs();
                }

            }

            private void openViewAddRemoveOUs() {
                final Window subwindow = new Window("A modal subwindow");
                subwindow.setModal(true);
                subwindow.setWidth("650px");
                VerticalLayout layout = (VerticalLayout) subwindow.getContent();
                layout.setMargin(true);
                layout.setSpacing(true);

                // try {
                // // subwindow.addComponent(new AddOrgUnitstoContext(router, resourceProxy, controller, resourceProxy
                // // .getOrganizationalUnit()));
                // }
                // // catch (EscidocClientException e) {
                // // controller.showError(e);
                // // }
                Button close = new Button(ViewConstants.CLOSE, new Button.ClickListener() {
                    @Override
                    public void buttonClick(@SuppressWarnings("unused")
                    ClickEvent event) {
                        subwindow.getParent().removeWindow(subwindow);
                    }
                });
                layout.addComponent(close);
                layout.setComponentAlignment(close, Alignment.TOP_RIGHT);

                router.getMainWindow().addWindow(subwindow);
            }
        });

    }

    @Override
    protected boolean hasRightstoContextMenu() {
        return true;
    }

    @Override
    protected void removeAction(Object target) {
        controller.removeParent(target.toString());
        tableContainer.removeItem(target);
    }

    @Override
    protected HierarchicalContainer populateContainerTable() {
        tableContainer = new HierarchicalContainer();
        // Create container property for name
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_NAME, String.class, null);
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_LINK, Button.class, null);

        List<ResourceModel> l = orgUnitProxy.getParentList();
        for (final ResourceModel rm : l) {
            Item item = tableContainer.addItem(rm.getId());

            final Button parentOrgUnitLink = new Button("Link");
            parentOrgUnitLink.setStyleName(BaseTheme.BUTTON_LINK);
            parentOrgUnitLink.addListener(new ClickListener() {
                @Override
                public void buttonClick(@SuppressWarnings("unused")
                ClickEvent event) {
                    try {
                        router.show(rm, true);
                    }
                    catch (EscidocClientException e) {
                        controller.showError(e.getMessage());
                    }
                }
            });
            if (item != null) {
                item.getItemProperty(ViewConstants.PROPERTY_NAME).setValue(rm.getName());
                item.getItemProperty(ViewConstants.PROPERTY_LINK).setValue(parentOrgUnitLink);
            }

        }
        table.setColumnWidth(ViewConstants.PROPERTY_LINK, 40);
        return tableContainer;
    }
    
    protected void initializeTable() {
        // size
        table.setWidth("100%");
        // selectable
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setImmediate(true); // react at once when something is selected
        // turn on column reordering and collapsing
        table.setColumnReorderingAllowed(true);
        table.setColumnCollapsingAllowed(true);
    }

}
