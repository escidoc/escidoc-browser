package org.escidoc.browser.ui.role;

import com.google.common.base.Preconditions;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

import org.escidoc.browser.AppConstants;

import de.escidoc.core.resources.Resource;

@SuppressWarnings("serial")
public class OnResourceSelect implements ValueChangeListener {

    private Property searchBox;

    public OnResourceSelect(Property searchBox) {
        Preconditions.checkNotNull(searchBox, "searchBox is null: %s", searchBox);
        this.searchBox = searchBox;
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        if (isSelected(event)) {
            searchBox.setReadOnly(false);
            searchBox.setValue(((Resource) event.getProperty().getValue()).getXLinkTitle());
            searchBox.setReadOnly(true);
        }
        else {
            searchBox.setReadOnly(false);
            searchBox.setValue(AppConstants.EMPTY_STRING);
            searchBox.setReadOnly(true);
        }
    }

    private static boolean isSelected(final ValueChangeEvent event) {
        return event.getProperty() != null && event.getProperty().getValue() != null
            && event.getProperty().getValue() instanceof Resource;
    }
}
