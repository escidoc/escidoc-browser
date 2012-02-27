package org.escidoc.browser.ui.navigation;

import com.google.common.base.Preconditions;

import com.vaadin.data.Container;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.repository.GroupRepository;

import java.util.List;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class GroupDataSource implements TreeDataSource {

    private GroupRepository group;

    public GroupDataSource(GroupRepository group) {
        Preconditions.checkNotNull(group, "group is null: %s", group);
        this.group = group;
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public Container getContainer() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public void addChildren(ResourceModel parent, List<ResourceModel> children) {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public void addChild(ResourceModel parent, ResourceModel child) {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public void addTopLevelResource(ResourceModel context) {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public boolean remove(ResourceModel resourceModel) {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public ResourceModel getParent(ResourceModel child) {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public void reload() throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

}