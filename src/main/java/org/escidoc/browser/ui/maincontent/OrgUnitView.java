package org.escidoc.browser.ui.maincontent;

import com.google.common.base.Preconditions;

import com.vaadin.ui.VerticalLayout;

import org.escidoc.browser.model.ResourceProxy;

@SuppressWarnings("serial")
public class OrgUnitView extends VerticalLayout {

    private final ResourceProxy resourceProxy;

    public OrgUnitView(final ResourceProxy resourceProxy) {
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        this.resourceProxy = resourceProxy;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resourceProxy == null) ? 0 : resourceProxy.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OrgUnitView other = (OrgUnitView) obj;
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
