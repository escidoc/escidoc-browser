package org.escidoc.browser.ui.view.helpers;

import org.escidoc.browser.controller.UserGroupController;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.UserGroupModel;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;

public class ResourcePropertiesUserGroupView extends ResourceProperties {

    private UserGroupModel userProxy;

    private Router router;

    private UserGroupController controller;

    protected Component swapComponent;

    protected Component oldComponent;

    public ResourcePropertiesUserGroupView(ResourceProxy resourceProxy, Router router, UserGroupController controller) {
        this.resourceProxy = resourceProxy;
        this.router = router;
        this.userProxy = (UserGroupModel) resourceProxy;
        this.controller = controller;

    }

    protected void bindProperties() {

        final Panel pnlPropertiesLeft = buildLeftPropertiesPnl();
        final Panel pnlPropertiesRight = buildRightPnlProperties();

        final Label descMetadata1 = new Label("ID: " + resourceProxy.getId());

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
        handleClicksOnResourceLayout();

    }

    private void handleClicksOnResourceLayout() {
        vlPropertiesLeft.addListener(new LayoutClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void layoutClick(LayoutClickEvent event) {
                if (event.getChildComponent().getClass().getCanonicalName() == "com.vaadin.ui.Label") {
                    final Label child = (Label) event.getChildComponent();
                    if (child.getDescription() == ViewConstants.DESC_STATUS2) {
                        reSwapComponents();
                        oldComponent = event.getClickedComponent();
                        swapComponent = editStatus(child.getValue().toString().replace(status, "").trim());
                        vlPropertiesLeft.replaceComponent(oldComponent, swapComponent);
                        vlPropertiesLeft.removeComponent(event.getClickedComponent());
                    }
                }

            }

            private Component editStatus(final String activeStatus) {
                final ComboBox cmbStatus = new ComboBox();
                cmbStatus.setInvalidAllowed(false);
                cmbStatus.setNullSelectionAllowed(false);
                if ((activeStatus.equals("Active")) || (activeStatus.equals("Activate"))) {
                    cmbStatus.addItem("Deactivate");
                }
                else if ((activeStatus.equals("Not-Active")) || (activeStatus.equals("Deactivate"))) {
                    cmbStatus.addItem("Activate");

                }
                cmbStatus.select(1);
                cmbStatus.setImmediate(true);
                cmbStatus.addListener(new Property.ValueChangeListener() {

                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        if (event.getProperty().toString().equals("Not-Active")
                            || (event.getProperty().toString().equals("Deactivate"))) {
                            controller.deactivateUser();
                            lblStatus.setValue(status + "Not-Active");
                        }
                        else {
                            controller.activateUser();
                            lblStatus.setValue(status + "Active");
                        }
                        vlPropertiesLeft.replaceComponent(cmbStatus, lblStatus);
                    }
                });
                return cmbStatus;
            }

            private void reSwapComponents() {

                if (swapComponent != null) {
                    if (swapComponent instanceof Label) {
                        ((Label) oldComponent).setValue(((TextArea) swapComponent).getValue());
                    }
                    else if ((swapComponent instanceof ComboBox) && ((ComboBox) swapComponent).getValue() != null) {
                        ((Label) oldComponent).setValue(status + ((ComboBox) swapComponent).getValue());
                    }
                    vlPropertiesLeft.replaceComponent(swapComponent, oldComponent);
                    swapComponent = null;
                }
            }

        });

    }
}
