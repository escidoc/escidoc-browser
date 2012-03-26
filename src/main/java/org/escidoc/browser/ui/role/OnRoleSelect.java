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
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui.role;

import com.google.common.base.Preconditions;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;

import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.repository.RoleRepository.RoleModel;

import java.util.ArrayList;
import java.util.List;

import de.escidoc.core.resources.aa.role.ScopeDef;

@SuppressWarnings("serial")
public class OnRoleSelect implements ValueChangeListener {

    private NativeSelect type;

    private HorizontalLayout footer;

    private ListSelect resourceResult;

    public OnRoleSelect(NativeSelect type, HorizontalLayout footer, ListSelect resourceResult) {
        Preconditions.checkNotNull(type, "type is null: %s", type);
        Preconditions.checkNotNull(footer, "footer is null: %s", footer);
        Preconditions.checkNotNull(resourceResult, "resouceResult is null: %s", resourceResult);
        this.type = type;
        this.footer = footer;
        this.resourceResult = resourceResult;
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        if (event.getProperty().getValue() instanceof RoleModel) {

            final List<ResourceType> resourceTypeList = new ArrayList<ResourceType>();
            for (final ScopeDef scopeDef : getScopeDefinitions((RoleModel) event.getProperty().getValue())) {
                final ResourceType resourceType = ResourceType.convert(scopeDef.getRelationAttributeObjectType());
                if (resourceType != null && !resourceType.equals(ResourceType.COMPONENT)) {
                    resourceTypeList.add(resourceType);
                }
            }
            type.removeAllItems();
            bindView(resourceTypeList);
            showSaveButton();
        }
    }

    private void bindView(List<ResourceType> resourceTypeList) {
        final BeanItemContainer<ResourceType> dataSource =
            new BeanItemContainer<ResourceType>(ResourceType.class, resourceTypeList);
        type.setContainerDataSource(dataSource);
        type.setItemCaptionPropertyId(PropertyId.NAME);
        if (dataSource.size() > 0) {
            type.setValue(dataSource.getIdByIndex(0));
        }
        enableScoping(resourceTypeList.size() > 0);
    }

    private static List<ScopeDef> getScopeDefinitions(final RoleModel roleModel) {
        return roleModel.getScopeDefinitions();
    }

    private void showSaveButton() {
        footer.setVisible(true);
    }

    private void enableScoping(final boolean isScopingEnabled) {
        type.setEnabled(isScopingEnabled);
        resourceResult.setEnabled(isScopingEnabled);
    }
}