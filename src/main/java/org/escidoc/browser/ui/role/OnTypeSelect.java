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

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.ViewConstants;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class OnTypeSelect implements ValueChangeListener {
    private VerticalLayout layout;

    private ListSelect select;

    private Repositories repositories;

    private Window mainWindow;

    protected OnTypeSelect(Window mainWindow, VerticalLayout layout, ListSelect select, Repositories repositories) {
        Preconditions.checkNotNull(mainWindow, "mw is null: %s", mainWindow);
        Preconditions.checkNotNull(layout, "layout is null: %s", layout);
        Preconditions.checkNotNull(select, "resourceSelect is null: %s", select);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        this.mainWindow = mainWindow;
        this.layout = layout;
        this.select = select;
        this.repositories = repositories;
    }

    @Override
    public void valueChange(final ValueChangeEvent event) {
        try {
            onSelectedResourceType(event);
        }
        catch (UnsupportedOperationException e) {
            mainWindow.showNotification(ViewConstants.ERROR, e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
        catch (EscidocClientException e) {
            mainWindow.showNotification(ViewConstants.ERROR, e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private void onSelectedResourceType(final ValueChangeEvent event) throws UnsupportedOperationException,
        EscidocClientException {
        final Object value = event.getProperty().getValue();
        if (value instanceof ResourceType) {
            Component newComponent = assignComponent();
            loadData((ResourceType) value);
            final Iterator<Component> it = layout.getComponentIterator();
            if (it.hasNext()) {
                layout.replaceComponent(it.next(), newComponent);
            }
            else {
                layout.addComponent(newComponent);
            }
        }
    }

    private void loadData(ResourceType type) throws UnsupportedOperationException, EscidocClientException {
        final BeanItemContainer<ResourceModel> dataSource = newContainer();
        for (final ResourceModel rm : findAll(type)) {
            dataSource.addItem(rm);
        }
        configureList(dataSource);
    }

    private void configureList(final BeanItemContainer<ResourceModel> container) {
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(PropertyId.NAME);
    }

    private List<ResourceModel> findAll(ResourceType type) throws EscidocClientException {
        List<ResourceModel> list = repositories.findByType(type).findAll();
        Collections.sort(list, new Comparator<ResourceModel>() {

            @Override
            public int compare(ResourceModel o1, ResourceModel o2) {
                ResourceModel p1 = (ResourceModel) o1;
                ResourceModel p2 = (ResourceModel) o2;
                return p1.getName().compareToIgnoreCase(p2.getName());
            }

        });

        return list;
    }

    private static BeanItemContainer<ResourceModel> newContainer() {
        return new BeanItemContainer<ResourceModel>(ResourceModel.class);
    }

    private Component assignComponent() {
        Component newComponent = select;
        return newComponent;
    }
}