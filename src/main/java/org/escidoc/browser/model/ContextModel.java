package org.escidoc.browser.model;

import de.escidoc.core.resources.Resource;

public class ContextModel implements ResourceModel {

    private final Resource context;

    private String id;

    private String name;

    public ContextModel(final Resource context) {
        this.context = context;
    }

    @Override
    public String getId() {
        id = context.getObjid();
        return id;
    }

    @Override
    public String getName() {
        name = context.getXLinkTitle();
        return name;
    }

    @Override
    public String getType() {
        return "Context";
    }

}
