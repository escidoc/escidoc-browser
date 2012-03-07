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