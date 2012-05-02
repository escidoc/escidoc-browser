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
package org.escidoc.browser.ui;

import java.util.ArrayList;
import java.util.List;

import org.escidoc.browser.controller.UserGroupController;
import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.UserGroupModel;
import org.escidoc.browser.ui.listeners.AddOrgUnitstoGroup;
import org.escidoc.browser.ui.maincontent.View;
import org.escidoc.browser.ui.orgunit.OrgUnitTreeView;
import org.escidoc.browser.ui.view.helpers.ResourcePropertiesVH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.aa.usergroup.Selector;
import de.escidoc.core.resources.aa.usergroup.UserGroup;

@SuppressWarnings("serial")
public class UserGroupView extends View {

    private final static Logger LOG = LoggerFactory.getLogger(UserGroupView.class);

    private Action ACTION_DELETE = new Action("Remove Organizational Unit");

    private Action ACTION_ADD = new Action("Add Organizational Unit");

    private Action[] ACTIONS_LIST = new Action[] { ACTION_ADD, ACTION_DELETE };

    private static TextField nameField;

    private final Router router;

    private final UserGroupModel resourceProxy;

    private final Repositories repositories;

    private final UserGroupController controller;

    private OrgUnitTreeView tree;

    private Window mainWindow;

    private Table selectorTable;

    private ResourcePropertiesVH resoucePropertiesView;

    public UserGroupView(Router router, ResourceProxy resourceProxy, Repositories repositories,
        UserGroupController controller) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(resourceProxy, "gm is null: %s", resourceProxy);
        Preconditions.checkNotNull(repositories, "gr is null: %s", repositories);
        Preconditions.checkNotNull(controller, "c is null: %s", controller);
        this.router = router;
        this.resourceProxy = (UserGroupModel) resourceProxy;
        this.repositories = repositories;
        this.controller = controller;
        this.mainWindow = router.getMainWindow();
    }

    public Panel buildContentPanel() {
        setImmediate(false);
        setStyleName(Runo.PANEL_LIGHT);
        setSizeFull();
        Panel contentPanel = createContentPanel();
        contentPanel.setContent(buildVlContentPanel());
        setContent(contentPanel);
        bindModelToView();
        return this;
    }

    private void bindModelToView() {
        nameField.setValue(resourceProxy.getName());
    }

    private static Panel createContentPanel() {
        Panel contentPanel = new Panel();
        contentPanel.setImmediate(false);
        contentPanel.setSizeFull();

        return contentPanel;
    }

    private ComponentContainer buildVlContentPanel() {
        VerticalLayout layout = createMainLayout();
        layout.addComponent(buildVlResourceProperties());
        addNameField(layout);
        addOrgUnitTable(layout);
        addSaveButton(layout);
        return layout;
    }

    private VerticalLayout buildVlResourceProperties() {
        // common part: create layout
        VerticalLayout vlResourceProperties = new VerticalLayout();
        vlResourceProperties.setImmediate(false);
        vlResourceProperties.setWidth("100.0%");
        vlResourceProperties.setHeight("100.0%");
        vlResourceProperties.setMargin(false);

        // creating the properties / without the breadcrump
        resoucePropertiesView = new ResourcePropertiesVH(resourceProxy, router);
        vlResourceProperties.addComponent(resoucePropertiesView.getContentLayout());
        return vlResourceProperties;
    }

    private void addOrgUnitTable(VerticalLayout layout) {
        selectorTable = new Table();
        selectorTable.setWidth("60%");
        selectorTable.setSelectable(true);
        selectorTable.setImmediate(true);
        selectorTable.setColumnReorderingAllowed(true);

        final BeanItemContainer<ResourceModel> dataSource;
        try {
            dataSource = populateContainerTable();

            selectorTable.setContainerDataSource(dataSource);
            selectorTable.setVisibleColumns(new String[] { PropertyId.NAME, (String) PropertyId.ID });
            selectorTable.addActionHandler(new Action.Handler() {

                @Override
                public Action[] getActions(@SuppressWarnings("unused")
                Object target, @SuppressWarnings("unused")
                Object sender) {
                    return ACTIONS_LIST;
                }

                @Override
                public void handleAction(Action action, @SuppressWarnings("unused")
                Object sender, Object target) {
                    if (action.equals(ACTION_ADD)) {
                        // mainWindow.addWindow(new OrganizationSelectionView(repositories, resourceProxy, nameField,
                        // mainWindow, dataSource).modalWindow());
                        openViewAddRemoveOUs();
                    }
                    else {
                        try {
                            repositories.group().removeOrganization(resourceProxy.getId(),
                                ((ResourceModel) target).getId());
                            selectorTable.removeItem(target);
                            mainWindow.showNotification("Organization with the id " + resourceProxy.getId()
                                + " is removed from the group.", Window.Notification.TYPE_TRAY_NOTIFICATION);
                        }
                        catch (EscidocClientException e) {
                            mainWindow.showNotification("Error removing organizationunit: ", e.getMessage(),
                                Window.Notification.TYPE_ERROR_MESSAGE);

                        }
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
                        subwindow.addComponent(new AddOrgUnitstoGroup(router, resourceProxy, controller));
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
            layout.addComponent(selectorTable);

        }
        catch (EscidocClientException e1) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Something wrong happens with the app, reason: ");
            errorMessage.append(e1.getMessage());
            LOG.warn(errorMessage.toString());
            mainWindow.showNotification(ViewConstants.ERROR, errorMessage.toString(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }

    }

    protected BeanItemContainer<ResourceModel> populateContainerTable() throws EscidocClientException {
        // BeanItemContainer<Selector> dataSource = new
        // BeanItemContainer<Selector>(Selector.class, selectorList);

        List<ResourceModel> orgList = new ArrayList<ResourceModel>();
        for (final Selector s : resourceProxy.getSelector()) {
            ResourceProxy findById = repositories.organization().findById(s.getContent());
            orgList.add(findById);
        }

        BeanItemContainer<ResourceModel> dataSource =
            new BeanItemContainer<ResourceModel>(ResourceModel.class, orgList);

        return dataSource;
    }

    private static VerticalLayout createMainLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setImmediate(false);
        layout.setMargin(true, true, false, true);
        layout.setSpacing(true);
        return layout;
    }

    private void addSaveButton(VerticalLayout layout) {
        Button saveButton = new Button(ViewConstants.SAVE, new Button.ClickListener() {

            @Override
            public void buttonClick(@SuppressWarnings("unused")
            ClickEvent event) {
                try {
                    if (!nameField.isValid()) {
                        mainWindow.showNotification("A group name is required",
                            Window.Notification.TYPE_WARNING_MESSAGE);
                    }
                    else {
                        UserGroup updateGroup =
                            repositories.group().updateGroup(resourceProxy.getId(), (String) nameField.getValue());
                        mainWindow.showNotification("Group, " + updateGroup.getXLinkTitle() + ", is updated",
                            Window.Notification.TYPE_TRAY_NOTIFICATION);
                    }
                }
                catch (EscidocClientException e) {
                    StringBuilder errorMessage = new StringBuilder();
                    errorMessage.append("Can not update a group. Reason: ");
                    errorMessage.append(e.getMessage());
                    LOG.warn(errorMessage.toString());
                    mainWindow.showNotification(ViewConstants.ERROR, errorMessage.toString(),
                        Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });
        layout.addComponent(saveButton);
    }

    private static void addNameField(VerticalLayout layout) {
        nameField = new TextField(ViewConstants.NAME);
        nameField.setWidth("300px");
        nameField.setRequired(true);
        nameField.setRequiredError("A group name is required.");
        layout.addComponent(nameField);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resourceProxy == null) ? 0 : resourceProxy.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UserGroupView other = (UserGroupView) obj;
        if (resourceProxy == null) {
            if (other.resourceProxy != null) {
                return false;
            }
        }
        else if (!resourceProxy.equals(other.resourceProxy)) {
            return false;
        }
        return true;
    }
}