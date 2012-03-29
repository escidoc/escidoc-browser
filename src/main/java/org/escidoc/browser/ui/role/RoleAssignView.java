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
package org.escidoc.browser.ui.role;

import com.google.common.base.Preconditions;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;

import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.UserModel;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.RoleRepository.RoleModel;
import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.aa.useraccount.Grant;

@SuppressWarnings("serial")
public class RoleAssignView extends CustomComponent {

    private final static Logger LOG = LoggerFactory.getLogger(RoleAssignView.class);

    private final NativeSelect resourcetypeSelection = new NativeSelect(ViewConstants.TYPE);

    private final ListSelect resourceSelection = new ListSelect();

    private final HorizontalLayout footer = new HorizontalLayout();

    private final Button saveBtn = new Button(ViewConstants.SAVE, new SaveBtnListener());

    private final VerticalLayout footerLayout = new VerticalLayout();

    private final VerticalLayout resourceContainer = new VerticalLayout();

    private final TextField searchBox = new TextField(ViewConstants.RESOURCE_NAME);

    // private final Button searchButton = new
    // Button(ViewConstants.SEARCH_LABEL);

    private final Window mainWindow;

    private VerticalLayout verticalLayout;

    private Panel panel;

    private ComponentContainer mainLayout;

    private NativeSelect userSelection;

    private NativeSelect roleSelection;

    private UserModel selectedUser;

    private Repositories repositories;

    // TODO: add logged in user;
    public RoleAssignView(Window mw, Repositories repositories) {
        Preconditions.checkNotNull(mw, "mw is null: %s", mw);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        this.mainWindow = mw;
        this.repositories = repositories;
    }

    public void init() {
        initLayout();
        addUserField();
        addRoleSelection();
        addTypeSelection();
        addResourceSelection();
        addFooter();
        tryToBindData();
    }

    private void tryToBindData() {
        try {
            bindData();
        }
        catch (EscidocClientException e) {
            LOG.warn(e.getMessage());
            mainWindow.showNotification(e.getMessage());
        }
    }

    private void initLayout() {
        panel = new Panel();
        setCompositionRoot(panel);
        panel.setStyleName(Runo.PANEL_LIGHT);
        panel.setSizeFull();
        setSizeFull();

        verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("100%");

        mainLayout = new FormLayout();
        mainLayout.setWidth(400, UNITS_PIXELS);

        panel.setContent(verticalLayout);

        // TODO how to make panel take the whole vertical screen, if it does not
        // contain any child component;
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(true, false, false, true);
        verticalLayout.addComponent(mainLayout);
    }

    private void addUserField() {
        userSelection = new NativeSelect(ViewConstants.USER_NAME);
        userSelection.setWidth("300px");
        userSelection.setNullSelectionAllowed(false);
        userSelection.setMultiSelect(false);
        userSelection.setRequired(true);
        userSelection.setNewItemsAllowed(false);
        userSelection.setImmediate(true);
        userSelection.setRequiredError("User is required");
        mainLayout.addComponent(userSelection);
    }

    private void addRoleSelection() {
        roleSelection = new NativeSelect(ViewConstants.SELECT_ROLE_LABEL);
        roleSelection.setWidth(300 + "px");
        roleSelection.setNullSelectionAllowed(false);
        roleSelection.setImmediate(true);
        roleSelection.setRequired(true);
        roleSelection.addListener(new OnRoleSelect(resourcetypeSelection, footer, resourceSelection));
        mainLayout.addComponent(roleSelection);
    }

    private void addTypeSelection() {
        resourcetypeSelection.setEnabled(false);
        resourcetypeSelection.setWidth(300 + "px");
        resourcetypeSelection.setImmediate(true);
        resourcetypeSelection.setNullSelectionAllowed(false);
        mainLayout.addComponent(resourcetypeSelection);
    }

    // private void addResourceSearchBox() {
    // searchBox.setWidth(Integer.toString(3 / 2 * 300) + "px");
    // searchBox.setEnabled(false);
    // searchButton.setEnabled(false);
    // searchBox.setReadOnly(true);
    // mainLayout.addComponent(searchBox);
    // }

    private void addResourceSelection() {
        resourceSelection.setSizeFull();
        resourceSelection.setNullSelectionAllowed(false);
        resourceSelection.setHeight("400px");
        resourceSelection.setImmediate(true);
        resourceSelection.addListener(new OnResourceSelect(searchBox));
        resourceContainer.setStyleName(Reindeer.PANEL_LIGHT);
        resourceContainer.setWidth(Integer.toString(3 / 2 * 300) + "px");
        resourceContainer.setHeight("400px");
        mainLayout.addComponent(resourceContainer);
    }

    private void addFooter() {
        footer.addComponent(saveBtn);
        footerLayout.addComponent(footer);
        footerLayout.setComponentAlignment(footer, Alignment.MIDDLE_RIGHT);
        footer.setVisible(false);
        mainLayout.addComponent(footerLayout);
    }

    private void bindData() throws EscidocClientException {
        bindUserAccountData();
        bindRoleData();
        bindType();
    }

    private void bindUserAccountData() throws EscidocClientException {
        Container userContainer =
            new BeanItemContainer<ResourceModel>(ResourceModel.class, repositories.user().findAll());
        userSelection.setContainerDataSource(userContainer);
        userSelection.setItemCaptionPropertyId(PropertyId.NAME);
    }

    private void bindRoleData() throws EscidocClientException {
        BeanItemContainer<ResourceModel> container = new BeanItemContainer<ResourceModel>(ResourceModel.class);
        for (ResourceModel resourceModel : repositories.role().findAll()) {
            if (RoleModel.isValid(resourceModel)) {
                BeanItem<ResourceModel> item = container.addItem(resourceModel);
                Preconditions.checkNotNull(item, "item is null: %s", item);
            }
            container.addItem(resourceModel.getName());
        }
        roleSelection.setContainerDataSource(container);
        roleSelection.setItemCaptionPropertyId(PropertyId.NAME);
    }

    private void bindType() {
        resourcetypeSelection.addListener(new OnTypeSelect(mainWindow, resourceContainer, resourceSelection,
            repositories));
    }

    public void selectUser(final UserModel userAccount) {
        selectedUser = userAccount;
        userSelection.select(userAccount);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mainWindow == null) ? 0 : mainWindow.hashCode());
        result = prime * result + ((repositories == null) ? 0 : repositories.hashCode());
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
        RoleAssignView other = (RoleAssignView) obj;
        if (mainWindow == null) {
            if (other.mainWindow != null) {
                return false;
            }
        }
        else if (!mainWindow.equals(other.mainWindow)) {
            return false;
        }
        if (repositories == null) {
            if (other.repositories != null) {
                return false;
            }
        }
        else if (!repositories.equals(other.repositories)) {
            return false;
        }
        return true;
    }

    private class SaveBtnListener implements Button.ClickListener {

        @Override
        public void buttonClick(@SuppressWarnings("unused") final ClickEvent event) {
            onSaveClick();
        }

        private void onSaveClick() {
            if (getSelectedUser() != null) {
                try {
                    Grant grant = assignRole();
                    showMessage(grant);
                }
                catch (EscidocClientException e) {
                    mainWindow.showNotification("Failed to grant the role. Reason: " + e.getMessage(),
                        Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
            else {
                userSelection.setComponentError(new UserError("User is required"));
            }
        }

        private void showMessage(Grant grant) {
            mainWindow.showNotification("Role " + grant.getXLinkTitle() + " have been granted to "
                + getSelectedUser().getName(), Window.Notification.TYPE_TRAY_NOTIFICATION);
        }

        private Grant assignRole() throws EscidocClientException {
            return repositories
                .user().assign(getSelectedUser()).withRole(getSelectedRole()).onResources(getSelectedResources())
                .execute();
        }

        private UserModel getSelectedUser() {
            if (selectedUser == null) {
                return (UserModel) userSelection.getValue();
            }
            return selectedUser;
        }

        private RoleModel getSelectedRole() {
            final Object value = roleSelection.getValue();
            if (value instanceof RoleModel) {
                return (RoleModel) value;
            }
            return null;
        }

        private Set<ResourceModel> getSelectedResources() {
            final Object value = resourceSelection.getValue();
            if (value instanceof ResourceModel) {
                return Collections.singleton((ResourceModel) value);
            }
            return Collections.emptySet();
        }
    }
}