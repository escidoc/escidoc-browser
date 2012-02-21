package org.escidoc.browser.ui.listeners;

import org.escidoc.browser.controller.ContextController;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.XmlUtil;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.resources.om.context.AdminDescriptor;

public class AdminDescriptorFormListener {
    private Router router;

    private ContextController contextController;

    public AdminDescriptorFormListener(Router router, ContextController contextController) {
        this.router = router;
        this.contextController = contextController;
    }

    public void adminDescriptorForm() {

        final Window subwindow = new Window("A modal subwindow");
        subwindow.setModal(true);
        subwindow.setWidth("650px");
        VerticalLayout layout = (VerticalLayout) subwindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);

        final TextField txtName = new TextField("Name");
        txtName.setImmediate(true);
        txtName.setValidationVisible(true);
        final TextArea txtContent = new TextArea("Content");
        txtContent.setColumns(30);
        txtContent.setRows(40);

        Button addAdmDescButton = new Button("Add Description");
        addAdmDescButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {

                if (txtName.getValue().toString() == null) {
                    router.getMainWindow().showNotification(ViewConstants.PLEASE_ENTER_A_NAME,
                        Notification.TYPE_ERROR_MESSAGE);
                }
                else if (!XmlUtil.isWellFormed(txtContent.getValue().toString())) {
                    router.getMainWindow().showNotification(ViewConstants.XML_IS_NOT_WELL_FORMED,
                        Notification.TYPE_ERROR_MESSAGE);
                }
                else {
                    AdminDescriptor newAdmDesc =
                        contextController.addAdminDescriptor(txtName.getValue().toString(), txtContent
                            .getValue().toString());
                    (subwindow.getParent()).removeWindow(subwindow);
                    router.getMainWindow().showNotification("Addedd Successfully", Notification.TYPE_HUMANIZED_MESSAGE);
                }
            }
        });

        subwindow.addComponent(txtName);
        subwindow.addComponent(txtContent);
        subwindow.addComponent(addAdmDescButton);

        Button close = new Button(ViewConstants.CLOSE, new Button.ClickListener() {
            @Override
            public void buttonClick(@SuppressWarnings("unused")
            ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        layout.addComponent(close);
        layout.setComponentAlignment(close, Alignment.TOP_RIGHT);

        router.getMainWindow().addWindow(subwindow);

    }

    /**
     * Editing since it has a parameter
     * 
     * @param adminDescriptor
     */
    public void adminDescriptorForm(AdminDescriptor adminDescriptor) {

        // Editing
        final Window subwindow = new Window("A modal subwindow");
        subwindow.setModal(true);
        subwindow.setWidth("650px");

        VerticalLayout layout = (VerticalLayout) subwindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);

        final TextField txtName = new TextField("Name");
        txtName.setValue(adminDescriptor.getName());
        txtName.setImmediate(true);
        txtName.setValidationVisible(true);
        final TextArea txtContent = new TextArea("Content");
        txtContent.setColumns(30);
        txtContent.setRows(40);
        try {
            txtContent.setValue(adminDescriptor.getContentAsString());
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        txtContent.setColumns(30);

        Button addAdmDescButton = new Button("Add Description");
        addAdmDescButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {

                if (txtName.getValue().toString() == null) {
                    router.getMainWindow().showNotification(ViewConstants.PLEASE_ENTER_A_NAME,
                        Notification.TYPE_ERROR_MESSAGE);

                }
                else if (!XmlUtil.isWellFormed(txtContent.getValue().toString())) {
                    router.getMainWindow().showNotification(ViewConstants.XML_IS_NOT_WELL_FORMED,
                        Notification.TYPE_ERROR_MESSAGE);
                }
                else {
                    contextController.addAdminDescriptor(txtName.getValue().toString(), txtContent
                        .getValue().toString());
                    (subwindow.getParent()).removeWindow(subwindow);
                    router.getMainWindow().showNotification("Addedd Successfully", Notification.TYPE_HUMANIZED_MESSAGE);
                }
            }
        });

        subwindow.addComponent(txtName);
        subwindow.addComponent(txtContent);
        subwindow.addComponent(addAdmDescButton);

        Button close = new Button(ViewConstants.CLOSE, new Button.ClickListener() {
            @Override
            public void buttonClick(@SuppressWarnings("unused")
            ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        layout.addComponent(close);
        layout.setComponentAlignment(close, Alignment.TOP_RIGHT);

        router.getMainWindow().addWindow(subwindow);

    }
}
