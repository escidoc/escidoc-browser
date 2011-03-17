package org.escidoc.browser.model;

import java.util.List;

import com.vaadin.data.Container;

public interface ResourceContainer {

    int size();

    Container getContainer();

    void addChildren(ResourceModel parent, List<ResourceModel> children);

}
