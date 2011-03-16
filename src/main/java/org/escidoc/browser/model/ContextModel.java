package org.escidoc.browser.model;

import de.escidoc.core.resources.Resource;

public class ContextModel implements ResourceModel {

    private final Resource context;

    public ContextModel(final Resource context) {
        this.context = context;
    }

    @Override
    public String getId() {
        return context.getObjid();
    }

    @Override
    public String getName() {
        return context.getXLinkTitle();
    }

    @Override
    public String getType() {
        return "Context";
    }

}
