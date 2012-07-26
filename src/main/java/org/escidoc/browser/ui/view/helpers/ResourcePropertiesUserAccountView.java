package org.escidoc.browser.ui.view.helpers;

import org.escidoc.browser.controller.UserAccountController;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.UserProxy;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

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
        final TextField nameTextField = new TextField();
        nameTextField.setValue(nameLabel.getValue().toString());
        final VerticalLayout vl = new VerticalLayout();
        vl.setWidth("100%");
        vl.addComponent(nameLabel);

        editName(nameTextField, vl);
        cssLayout.addComponent(vl);

    }

    private void editName(final TextField nameTextField, final VerticalLayout vl) {
        if (controller.isAllowedToUpdate()) {
            vl.addListener(new LayoutClickListener() {
                @Override
                public void layoutClick(LayoutClickEvent event) {
                    // Check if vl contains label or TextField
                    if (event.getChildComponent() != null
                        && event.getChildComponent().getClass().getCanonicalName() == "com.vaadin.ui.Label") {
                        vl.replaceComponent(nameLabel, nameTextField);
                    }
                    else {
                        try {
                            router.getRepositories().user().updateName(userProxy, nameTextField.getValue().toString());
                            router.getMainWindow().showNotification("User updateds successfully ",
                                Window.Notification.TYPE_TRAY_NOTIFICATION);
                            nameLabel.setValue(nameTextField.getValue().toString());
                            vl.replaceComponent(nameTextField, nameLabel);
                        }
                        catch (EscidocClientException e) {
                            router.getMainWindow().showNotification(
                                "There was a problem with the operation " + e.getLocalizedMessage(),
                                Window.Notification.TYPE_TRAY_NOTIFICATION);
                            LOG.debug(ViewConstants.ERROR + e.getLocalizedMessage());
                        }
                    }
                }
            });
        }
    }

    protected void createResourceLinks() {
        if (controller.isAllowedToUpdate()) {
            new CreateResourceLinksVH(router.getMainWindow().getURL().toString(), userProxy, cssLayout, router,
                controller);
        }
    }

}
