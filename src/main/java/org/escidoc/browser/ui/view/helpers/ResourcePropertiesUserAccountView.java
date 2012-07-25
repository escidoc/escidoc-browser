package org.escidoc.browser.ui.view.helpers;

import org.escidoc.browser.controller.UserAccountController;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.UserProxy;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;

import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class ResourcePropertiesUserAccountView extends ResourceProperties {

    private UserProxy userProxy;

    private Router router;

    private UserAccountController controller;

    public ResourcePropertiesUserAccountView(ResourceProxy resourceProxy, Router router, UserAccountController uac) {
        this.resourceProxy = resourceProxy;
        this.router = router;
        this.userProxy = (UserProxy) resourceProxy;
        this.controller = uac;

    }

    protected void bindProperties() {

        final Panel pnlPropertiesLeft = buildLeftPropertiesPnl();
        final Panel pnlPropertiesRight = buildRightPnlProperties();

        final Label descMetadata1 = new Label("Login Name: " + userProxy.getLoginName());

        status = resourceProxy.getType().getLabel() + " is ";
        lblStatus = new Label(status + resourceProxy.getStatus(), Label.CONTENT_RAW);
        lblStatus.setDescription(ViewConstants.DESC_STATUS2);

        final Label descMetadata2 =
            new Label(ViewConstants.CREATED_BY + " " + resourceProxy.getCreator() + " on "
                + resourceProxy.getCreatedOn() + "<br/>" + ViewConstants.LAST_MODIFIED_BY + " "
                + resourceProxy.getModifier() + " on " + resourceProxy.getModifiedOn(), Label.CONTENT_XHTML);

        vlPropertiesLeft.addComponent(descMetadata1);
        vlPropertiesLeft.addComponent(lblStatus);

        pnlPropertiesLeft.addComponent(vlPropertiesLeft);
        cssLayout.addComponent(pnlPropertiesLeft);

        pnlPropertiesRight.addComponent(descMetadata2);
        cssLayout.addComponent(pnlPropertiesRight);
    }

    protected void bindNametoHeader() {
        nameLabel = new Label(resourceProxy.getName());
        nameLabel.setDescription("header");
        nameLabel.setStyleName("h1 fullwidth");
        cssLayout.addComponent(nameLabel);
    }

    protected void createResourceLinks() {
        new CreateResourceLinksVH(router.getMainWindow().getURL().toString(), userProxy, cssLayout, router, controller);
    }
}
