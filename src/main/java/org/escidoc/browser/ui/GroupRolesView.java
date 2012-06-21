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

import com.google.common.base.Preconditions;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.RoleRepository.RoleModel;
import org.escidoc.browser.ui.role.OnRoleSelect;
import org.escidoc.browser.ui.useraccount.UserRolesView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.aa.role.ScopeDef;
import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.common.reference.Reference;

public class GroupRolesView extends Panel {

    private final static Logger LOG = LoggerFactory.getLogger(UserRolesView.class);

    private final class OnRemoveGrant implements Button.ClickListener {

        private Grant grant;

        private Repositories repos;

        private Window mainWindow;

        private String groupId;

        public OnRemoveGrant(Grant grant, String groupId, Repositories repos, Window mainWindow) {
            Preconditions.checkNotNull(grant, "grant is null: %s", grant);
            Preconditions.checkNotNull(groupId, "groupId is null: %s", groupId);
            Preconditions.checkNotNull(repos, "repo is null: %s", repos);
            Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);

            this.grant = grant;
            this.repos = repos;
            this.groupId = groupId;
            this.mainWindow = mainWindow;
        }

        @Override
        public void buttonClick(final ClickEvent event) {
            try {
                revokeGrantInServer();
                updateView(event);
                showSuccessMessage();
            }
            catch (EscidocClientException e) {
                showErrorMessage(e);
            }
        }

        private void showErrorMessage(EscidocClientException e) {
            mainWindow.showNotification("Error Message", "Something wrong happens. Cause: " + e.getMessage(),
                Notification.TYPE_ERROR_MESSAGE);
        }

        private void showSuccessMessage() {
            mainWindow.showNotification("", "Sucessfully revoke " + grant.getXLinkTitle() + " from " + groupId,
                Notification.TYPE_TRAY_NOTIFICATION);
        }

        private void revokeGrantInServer() throws EscidocClientException {
            repos.user().revokeGrant(groupId, grant);
        }

        private void updateView(final ClickEvent event) {
            VerticalLayout component = (VerticalLayout) event.getButton().getParent().getParent();
            component.removeComponent(event.getButton().getParent());
        }

    }

    private final ComponentContainer rolesLayout = new VerticalLayout();

    private final String groupId;

    private final Repositories repositories;

    private final Router router;

    // TODO CRUD View for User's Roles.
    // TODO View Layout: roleName | scopedOnResourceType | resourceName (resourceId) | removeBtn <-- row layout
    // TODO duplicate code for assigning grant and updating/showing grant
    // TODO Update: Edit existing role for the selected user. Change Role, Scope, Resource, etc
    // TODO PDP request???
    public GroupRolesView(String groupId, Repositories repositories, Router router) throws EscidocClientException {

        this.groupId = groupId;
        this.repositories = repositories;
        this.router = router;

        init();
    }

    private void init() throws EscidocClientException {
        setCaption(ViewConstants.USER_ROLES);
        setCaption("Roles Panel");
        ((VerticalLayout) rolesLayout).setMargin(true);
        setContent(rolesLayout);

        listRolesForUser(rolesLayout);
    }

    private void listRolesForUser(ComponentContainer grantListLayout) throws EscidocClientException {
        grantListLayout.removeAllComponents();

        if (repositories.group().getGrantsForGroup(groupId).size() == 0) {
            grantListLayout.addComponent(new Label("<h2>The user has no roles.</h2>", Label.CONTENT_XHTML));
        }
        else {
            showExistingGrants(grantListLayout);
        }

        grantListLayout.addComponent(buildGrantRowAddView());
    }

    private void showExistingGrants(ComponentContainer grantListLayout) throws EscidocClientException {
        int rowNumber = 0;
        for (Grant grant : repositories.group().getGrantsForGroup(groupId)) {
            HorizontalLayout existingGrantRowLayout = buildEditGrantRowView(rowNumber, grant);
            existingGrantRowLayout.setData(Integer.valueOf(rowNumber));
            rowNumber++;
            bind(existingGrantRowLayout, grant);
            grantListLayout.addComponent(existingGrantRowLayout);
        }
    }

    private Component buildGrantRowAddView() throws EscidocClientException {
        HorizontalLayout addGrantRow = newRowGrantLayout();
        final NativeSelect resourceTypeSelect = buildResourceTypeSelect();
        final NativeSelect resourceSelect = buildResourceSelect(resourceTypeSelect);
        final NativeSelect roleNameSelect = buildRoleNameSelect(resourceTypeSelect, resourceSelect);

        addGrantRow.addComponent(roleNameSelect);
        addGrantRow.addComponent(resourceTypeSelect);
        addGrantRow.addComponent(resourceSelect);

        final Button saveButton = buildAddGrantButton(resourceSelect, roleNameSelect);
        addGrantRow.addComponent(saveButton);

        bindRoleName(roleNameSelect);
        return addGrantRow;
    }

    private HorizontalLayout buildEditGrantRowView(int rowNumber, Grant grant) {
        HorizontalLayout editGrantRow = newRowGrantLayout();

        NativeSelect resourceTypeSelect = buildResourceTypeSelect();
        NativeSelect resourceSelect = buildResourceSelect(resourceTypeSelect);
        NativeSelect roleNameSelect = buildRoleNameSelect(resourceTypeSelect, resourceSelect);

        editGrantRow.addComponent(roleNameSelect);
        editGrantRow.addComponent(resourceTypeSelect);
        editGrantRow.addComponent(resourceSelect);

        Button removeButton = buildRemoveButton(rowNumber, grant);
        editGrantRow.addComponent(removeButton);

        return editGrantRow;
    }

    private NativeSelect buildResourceSelect(NativeSelect resouceTypeSelect) {
        final NativeSelect resourceSelect = buildResourceSelect();

        resouceTypeSelect.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (event.getProperty().getValue() instanceof ResourceType) {
                    try {
                        bind(resourceSelect, loadData((ResourceType) event.getProperty().getValue()));
                    }
                    catch (UnsupportedOperationException e) {
                        router.getMainWindow().showNotification(ViewConstants.ERROR, e.getMessage(),
                            Window.Notification.TYPE_ERROR_MESSAGE);
                    }
                    catch (EscidocClientException e) {
                        router.getMainWindow().showNotification(ViewConstants.ERROR, e.getMessage(),
                            Window.Notification.TYPE_ERROR_MESSAGE);
                    }
                }

            }

            private void bind(NativeSelect resourceSelect, BeanItemContainer<ResourceModel> dataSource) {
                resourceSelect.setContainerDataSource(dataSource);
                resourceSelect.setItemCaptionPropertyId(PropertyId.NAME);
            }

            private BeanItemContainer<ResourceModel> loadData(ResourceType selectedType) throws EscidocClientException {
                final BeanItemContainer<ResourceModel> dataSource = newContainer();
                for (final ResourceModel rm : findAll(selectedType)) {
                    dataSource.addItem(rm);
                }
                return dataSource;
            }

            private List<ResourceModel> findAll(ResourceType type) throws EscidocClientException {
                List<ResourceModel> list = repositories.findByType(type).findAll();
                Collections.sort(list, new Comparator<ResourceModel>() {

                    @Override
                    public int compare(ResourceModel o1, ResourceModel o2) {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }

                });

                return list;
            }
        });
        return resourceSelect;
    }

    private static NativeSelect buildResourceTypeSelect() {
        final NativeSelect resourceTypeSelect = buildResourceSelect();
        resourceTypeSelect.setWidth("100px");
        return resourceTypeSelect;
    }

    private Button buildAddGrantButton(final NativeSelect resourceSelect, final NativeSelect roleNameSelect) {
        Button saveButton = new Button("+");
        saveButton.setStyleName("small");
        saveButton.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(@SuppressWarnings("unused") ClickEvent event) {
                try {
                    Grant grant = assignGrantInServer();
                    updateView();
                    showSuccessMessage(grant);
                }
                catch (EscidocClientException e) {
                    router.getMainWindow().showNotification("Error Message",
                        "Something wrong happens. Cause: " + e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
                }
            }

            private void showSuccessMessage(Grant grant) {
                router.getMainWindow().showNotification("",
                    "Sucessfully revoke " + grant.getXLinkTitle() + " from " + groupId,
                    Notification.TYPE_TRAY_NOTIFICATION);

            }

            private Grant assignGrantInServer() throws EscidocClientException {
                return repositories
                    .user().assign(groupId).withRole(getSelectedRole()).onResources(getSelectedResources()).execute();
            }

            private RoleModel getSelectedRole() {
                final Object value = roleNameSelect.getValue();
                if (value instanceof RoleModel) {
                    return (RoleModel) value;
                }
                return null;
            }

            private Set<ResourceModel> getSelectedResources() {
                final Object value = resourceSelect.getValue();
                if (value instanceof ResourceModel) {
                    return Collections.singleton((ResourceModel) value);
                }
                return Collections.emptySet();
            }

            private void updateView() throws EscidocClientException {
                listRolesForUser(rolesLayout);
            }
        });
        return saveButton;
    }

    private void bindRoleName(NativeSelect roleNameSelect) throws EscidocClientException {
        roleNameSelect.setContainerDataSource(buildRoleNameDataSource());
        roleNameSelect.setItemCaptionPropertyId(PropertyId.NAME);
    }

    private static HorizontalLayout newRowGrantLayout() {
        HorizontalLayout grantLayout = new HorizontalLayout();
        grantLayout.setSpacing(true);
        grantLayout.setMargin(true, false, false, false);
        return grantLayout;
    }

    private static NativeSelect buildResourceSelect() {
        NativeSelect select = new NativeSelect();

        select.setWidth("250px");
        select.setNullSelectionAllowed(false);
        select.setMultiSelect(false);
        select.setNewItemsAllowed(false);
        select.setImmediate(true);

        return select;
    }

    private NativeSelect buildRoleNameSelect(final NativeSelect resourceTypeSelect, final NativeSelect resourceSelect) {
        NativeSelect roleNameSelect = new NativeSelect();
        roleNameSelect.setMultiSelect(false);
        roleNameSelect.setNewItemsAllowed(false);
        roleNameSelect.setNullSelectionAllowed(false);
        roleNameSelect.setImmediate(true);

        roleNameSelect.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (event.getProperty().getValue() instanceof RoleModel) {
                    final List<ResourceType> resourceTypeList = new ArrayList<ResourceType>();
                    for (final ResourceType resourceType : buildScopeDefinitions((RoleModel) event
                        .getProperty().getValue())) {
                        if (resourceType != null && !resourceType.equals(ResourceType.COMPONENT)) {
                            resourceTypeList.add(resourceType);
                        }
                    }
                    resourceTypeSelect.removeAllItems();
                    bind(resourceTypeSelect, resourceTypeList);
                }
            }

            private void bind(NativeSelect resourceTypeSelect, List<ResourceType> resourceTypeList) {
                enableScoping(resourceTypeSelect, resourceTypeList.size() > 0);
                final BeanItemContainer<ResourceType> dataSource =
                    new BeanItemContainer<ResourceType>(ResourceType.class, resourceTypeList);
                resourceTypeSelect.setContainerDataSource(dataSource);
                resourceTypeSelect.setItemCaptionPropertyId(PropertyId.NAME);
                if (dataSource.size() > 0) {
                    resourceTypeSelect.setValue(dataSource.getIdByIndex(0));
                }
            }

            private void enableScoping(NativeSelect resourceTypeSelect, boolean isEnabled) {
                resourceTypeSelect.setVisible(isEnabled);
                resourceSelect.setVisible(isEnabled);
            }
        });
        return roleNameSelect;
    }

    private void bind(HorizontalLayout grantRow, Grant grant) throws EscidocClientException {
        NativeSelect roleNameSelect = (NativeSelect) grantRow.getComponent(0);
        roleNameSelect.setContainerDataSource(buildRoleNameDataSource());
        roleNameSelect.setItemCaptionPropertyId(PropertyId.NAME);

        // // FIXME this causes a bad performance
        Collection<?> collection = roleNameSelect.getContainerDataSource().getItemIds();
        for (Object object : collection) {
            ResourceModel rm = (ResourceModel) object;
            if (grant.getProperties() == null || grant.getProperties().getRole() == null
                || grant.getProperties().getRole().getXLinkTitle() == null) {
                return;
            }
            if (rm.getName().equalsIgnoreCase(grant.getProperties().getRole().getXLinkTitle())) {
                roleNameSelect.setValue(rm);
            }
        }

        bindResourceType(grantRow, (RoleModel) roleNameSelect.getValue(), grant);
    }

    private void bindResourceType(HorizontalLayout grantRow, RoleModel value, Grant grant)
        throws EscidocClientException {

        final List<ResourceType> resourceTypeList = buildScopeDefinitions(value);

        NativeSelect resourceTypeSelect = (NativeSelect) grantRow.getComponent(1);
        final BeanItemContainer<ResourceType> dataSource =
            new BeanItemContainer<ResourceType>(ResourceType.class, resourceTypeList);
        resourceTypeSelect.setContainerDataSource(dataSource);
        resourceTypeSelect.setItemCaptionPropertyId(PropertyId.NAME);

        // TODO refactor this
        if (dataSource.size() > 0 && (NativeSelect) grantRow.getComponent(2) != null) {
            resourceTypeSelect.setValue(dataSource.getIdByIndex(0));

            loadData((NativeSelect) grantRow.getComponent(2), dataSource.getIdByIndex(0));

            for (Object object : ((NativeSelect) grantRow.getComponent(2)).getContainerDataSource().getItemIds()) {
                Reference assignedOn = grant.getProperties().getAssignedOn();
                if (assignedOn != null && getRoleName(object).equalsIgnoreCase(assignedOn.getXLinkTitle())) {
                    ((NativeSelect) grantRow.getComponent(2)).select(object);
                }
            }
        }
    }

    private static String getRoleName(Object object) {
        return ((ResourceModel) object).getName();
    }

    private static List<ResourceType> buildScopeDefinitions(RoleModel value) {
        final List<ResourceType> resourceTypeList = new ArrayList<ResourceType>();
        for (ScopeDef scopeDef : OnRoleSelect.getScopeDefinitions(value)) {
            final ResourceType resourceType = ResourceType.convert(scopeDef.getRelationAttributeObjectType());
            if (resourceType != null && !resourceType.equals(ResourceType.COMPONENT)) {
                resourceTypeList.add(resourceType);
            }
        }
        return resourceTypeList;
    }

    private void loadData(NativeSelect assignedOn, ResourceType type) throws UnsupportedOperationException,
        EscidocClientException {
        final BeanItemContainer<ResourceModel> dataSource = newContainer();
        for (final ResourceModel rm : findAll(type)) {
            dataSource.addItem(rm);
        }
        configureList(assignedOn, dataSource);
    }

    private static void configureList(NativeSelect assignedOn, final BeanItemContainer<ResourceModel> container) {
        assignedOn.setContainerDataSource(container);
        assignedOn.setItemCaptionPropertyId(PropertyId.NAME);
    }

    private static BeanItemContainer<ResourceModel> newContainer() {
        return new BeanItemContainer<ResourceModel>(ResourceModel.class);
    }

    private List<ResourceModel> findAll(ResourceType type) throws EscidocClientException {
        List<ResourceModel> list = repositories.findByType(type).findAll();
        Collections.sort(list, new Comparator<ResourceModel>() {

            @Override
            public int compare(ResourceModel o1, ResourceModel o2) {
                ResourceModel p1 = o1;
                ResourceModel p2 = o2;
                return p1.getName().compareToIgnoreCase(p2.getName());
            }

        });

        return list;
    }

    private BeanItemContainer<ResourceModel> roleNameDataSource;

    private BeanItemContainer<ResourceModel> buildRoleNameDataSource() throws EscidocClientException {
        if (roleNameDataSource == null) {
            roleNameDataSource = new BeanItemContainer<ResourceModel>(ResourceModel.class);
            for (ResourceModel resourceModel : repositories.role().findAll()) {
                if (RoleModel.isValid(resourceModel)) {
                    BeanItem<ResourceModel> item = roleNameDataSource.addItem(resourceModel);
                    Preconditions.checkNotNull(item, "item is null: %s", item);
                }
                roleNameDataSource.addItem(resourceModel.getName());
            }
        }
        return roleNameDataSource;
    }

    private static NativeSelect buildRoleNameSelect() {
        NativeSelect roleNameSelect = new NativeSelect();
        roleNameSelect.setMultiSelect(false);
        roleNameSelect.setNewItemsAllowed(false);
        roleNameSelect.setNullSelectionAllowed(false);
        roleNameSelect.setImmediate(true);
        return roleNameSelect;
    }

    private Button buildRemoveButton(int rowNumber, Grant grant) {
        Button removeButton = new Button("-");
        removeButton.setStyleName("small");
        removeButton.setData(Integer.valueOf(rowNumber));
        removeButton.addListener(new OnRemoveGrant(grant, groupId, repositories, router.getMainWindow()));
        return removeButton;
    }

}
