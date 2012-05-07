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
package org.escidoc.browser.ui.view.helpers;

import org.escidoc.browser.controller.ContextController;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;

public class OrganizationalUnitsTableVH extends TableContainerVH {

    private OrganizationalUnitRefs organizationalUnits;

    private ContextController controller;

    private Router router;

    private Action ACTION_DELETE = new Action("Delete Organizational Unit");

    private Action ACTION_ADD = new Action("Add/Edit Organizational Units");

    private Action[] ACTIONS_LIST = new Action[] { ACTION_ADD, ACTION_DELETE };

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
    }

    public void buildTable() {
        table.setContainerDataSource(populateContainerTable());
        if (hasRightstoContextMenu()) {
            this.addActionLists();
        }
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
                if ((ACTION_DELETE == action) && (target != null)) {
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

                try {
                    subwindow.addComponent(new AddOrgUnitstoContext(router, resourceProxy, controller, resourceProxy
                        .getOrganizationalUnit()));
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
        });
        // }
    }

    @Override
    protected boolean hasRightstoContextMenu() {
        return controller.canAddOUs();
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
        tableContainer.addContainerProperty(ViewConstants.PROPERTY_LINK, Button.class, null);

        for (final OrganizationalUnitRef organizationalUnit : organizationalUnits) {
            Item item = tableContainer.addItem(organizationalUnit.getObjid());
            if (item != null) {
                item.getItemProperty(ViewConstants.PROPERTY_NAME).setValue(organizationalUnit.getXLinkTitle());
                final Button openInNewTabLink = new Button("View");
                openInNewTabLink.setStyleName(BaseTheme.BUTTON_LINK);
                openInNewTabLink.addListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(@SuppressWarnings("unused")
                    ClickEvent event) {
                        try {
                            router.show(new ResourceModel() {
                                @Override
                                public ResourceType getType() {
                                    return ResourceType.ORG_UNIT;
                                }

                                @Override
                                public String getName() {
                                    return organizationalUnit.getXLinkTitle();
                                }

                                @Override
                                public String getId() {
                                    return organizationalUnit.getObjid();
                                }
                            }, true);
                        }
                        catch (EscidocClientException e) {
                            controller.showError(e);
                        }
                    }
                });

                item.getItemProperty(ViewConstants.PROPERTY_LINK).setValue(openInNewTabLink);
            }
        }
        table.setColumnWidth(ViewConstants.PROPERTY_LINK, 40);
        return tableContainer;
    }

    protected void initializeTable() {
        table.setWidth("100%");
        table.setSelectable(true);
        table.setImmediate(true); // react at once when something is selected
        table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
    }

}
