/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
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
 * All rights reserved. Use is subject to license terms.
 */
package org.escidoc.browser.ui;

import com.vaadin.data.Container.Filterable;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.navigation.OrgUnitDataSource;
import org.escidoc.browser.ui.orgunit.OrgUnitTreeView;

import java.util.ArrayList;
import java.util.List;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.aa.usergroup.UserGroup;

public class OrganizationSelectionView {

    private TextField orgUnitFilter;

    private OrgUnitTreeView tree;

    private Repositories repositories;

    private ResourceModel resourceProxy;

    private AbstractField nameField;

    private Window mw;

    private BeanItemContainer<ResourceModel> dataSource;

    public OrganizationSelectionView(Repositories repositories, ResourceProxy resourceProxy, AbstractField nameField,
        Window mw, BeanItemContainer<ResourceModel> dataSource) {
        this.repositories = repositories;
        this.resourceProxy = resourceProxy;
        this.nameField = nameField;
        this.mw = mw;
        this.dataSource = dataSource;
    }

    public Window modalWindow() {
        final Window modalWindow = new Window("Select an Organization");
        modalWindow.setHeight("600px");
        modalWindow.setWidth("400px");
        VerticalLayout modalWindowLayout = (VerticalLayout) modalWindow.getContent();
        modalWindow.setModal(true);

        modalWindow.setContent(modalWindowLayout);

        modalWindowLayout.setMargin(true);
        modalWindowLayout.setSpacing(true);

        // modalWindowLayout.setWidth("400px");
        // modalWindowLayout.setHeight("600px");
        modalWindowLayout.setSizeUndefined();

        orgUnitFilter = new TextField(ViewConstants.ORGANIZATIONAL_UNIT);

        orgUnitFilter.setWidth("300px");
        modalWindowLayout.addComponent(orgUnitFilter);

        orgUnitFilter.addListener(new TextChangeListener() {

            private SimpleStringFilter filter;

            @Override
            public void textChange(TextChangeEvent event) {
                // // TODO refactor this, the list should not return the data source
                Filterable ds = (Filterable) tree.getDataSource();
                ds.removeAllContainerFilters();
                filter = new SimpleStringFilter(PropertyId.NAME, event.getText(), true, false);
                ds.addContainerFilter(filter);
            }
        });

        buildOrganizationTreeView();
        modalWindowLayout.addComponent(tree);

        Button saveButton = new Button(ViewConstants.SAVE, new Button.ClickListener() {

            @Override
            public void buttonClick(@SuppressWarnings("unused") ClickEvent event) {
                try {
                    ResourceModel selected = tree.getSelected();
                    List<ResourceModel> list = new ArrayList<ResourceModel>();
                    list.add(selected);

                    UserGroup updateGroup =
                        repositories.group().updateGroup(resourceProxy.getId(), (String) nameField.getValue(), list);
                    mw.showNotification("Group, " + updateGroup.getXLinkTitle() + ", is updated",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);

                    dataSource.addBean(selected);
                    mw.removeWindow(modalWindow);
                }
                catch (EscidocClientException e) {
                    StringBuilder errorMessage = new StringBuilder();
                    errorMessage.append("Can not update a group. Reason: ");
                    errorMessage.append(e.getMessage());
                    mw.showNotification(ViewConstants.ERROR, errorMessage.toString(),
                        Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });
        modalWindowLayout.addComponent(saveButton);
        return modalWindow;
    }

    private void buildOrganizationTreeView() {
        tree = new OrgUnitTreeView();
        OrgUnitDataSource dataSource = new OrgUnitDataSource(repositories.organization());
        dataSource.init();
        tree.setDataSource(dataSource);

        tree.setClickListener(new ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                Object itemId = event.getItemId();
                ResourceModel rm = (ResourceModel) itemId;
                orgUnitFilter.setValue(rm.getName());
            }
        });

    }
}