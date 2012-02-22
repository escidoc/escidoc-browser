package org.escidoc.browser.ui.view.helpers;

import org.escidoc.browser.controller.ContextController;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.AddOrgUnitstoContext;

import com.google.common.base.Preconditions;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;

public class OrganizationalUnitsTableVH extends TableContainerVH {

    private OrganizationalUnitRefs organizationalUnits;

    private ContextController controller;

    private Router router;

    private Action ACTION_DELETE = new Action("Delete Organizational Unit");

    private Action ACTION_ADD = new Action("Add Organizational Unit");

    private Action ACTION_EDIT = new Action("Edit Organizational Unit");

    private Action[] ACTIONS_LIST = new Action[] { ACTION_ADD, ACTION_EDIT, ACTION_DELETE };

    private HierarchicalContainer tableContainer;

    private ContextProxyImpl resourceProxy;

    public OrganizationalUnitsTableVH(ContextController contextController, OrganizationalUnitRefs organizationalUnits,
        Router router, ContextProxyImpl resourceProxy) {
        Preconditions.checkNotNull(contextController, "contextController is null: %s", contextController);
        Preconditions.checkNotNull(organizationalUnits, "organizationalUnits is null: %s", organizationalUnits);

        this.controller = contextController;
        this.organizationalUnits = organizationalUnits;
        this.router = router;
        this.resourceProxy = resourceProxy;
        table.setContainerDataSource(populateContainerTable());

    }

    @Override
    protected void addActionLists() {
        // if (contextController.canUpdateContext()) {
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
                    final Window subwindow = new Window("A modal subwindow");
                    subwindow.setModal(true);
                    subwindow.setWidth("650px");
                    VerticalLayout layout = (VerticalLayout) subwindow.getContent();
                    layout.setMargin(true);
                    layout.setSpacing(true);

                    try {
                        subwindow.addComponent(new AddOrgUnitstoContext(router, resourceProxy, controller,
                            resourceProxy.getOrganizationalUnit()));
                    }
                    catch (EscidocClientException e) {
                        controller.showError(e);
                    }
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

            }
        });
        // }
    }

    @Override
    protected boolean hasRightstoContextMenu() {
        return true;
    }

    @Override
    protected void removeAction(Object target) {
        controller.removeOrgUnitFromContext(target.toString());
        tableContainer.removeItem(target);

    }

    @Override
    protected HierarchicalContainer populateContainerTable() {
        // Create new container
        tableContainer = new HierarchicalContainer();
        // Create container property for name
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_NAME, String.class, null);
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_LINK, Label.class, null);

        for (final OrganizationalUnitRef organizationalUnit : organizationalUnits) {
            Item item = tableContainer.addItem(organizationalUnit.getObjid());
            if (item != null) {
                item.getItemProperty(ViewConstants.PROPERTY_NAME).setValue(organizationalUnit.getXLinkTitle());
                item.getItemProperty(ViewConstants.PROPERTY_LINK).setValue(
                    new Label("<a href=\"" + router.getServiceLocation().getEscidocUri()
                        + organizationalUnit.getXLinkHref() + "\" target=\"_blank\">Link</a>", Label.CONTENT_RAW));
            }
        }
        table.setColumnWidth(ViewConstants.PROPERTY_LINK, 40);
        return tableContainer;
    }

}
