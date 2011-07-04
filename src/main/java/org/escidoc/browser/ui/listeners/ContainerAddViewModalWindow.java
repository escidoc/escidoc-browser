package org.escidoc.browser.ui.listeners;

import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.internal.ContentModelRepository;
import org.escidoc.browser.ui.ViewConstants;

import java.net.MalformedURLException;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.resources.Resource;

public class ContainerAddViewModalWindow {

    private final FormLayout addContainerForm = new FormLayout();

    private EscidocServiceLocation serviceLocation;

    private Window mainWindow;

    private NativeSelect contentModelSelect;

    private Window subwindow;

    private Button addButton;

    private TextField nameField;

    public void showContainerAddView() throws MalformedURLException, EscidocClientException {
        buildContainerForm();
        buildSubWindowUsingContainerForm();
        openSubWindow();
    }

    public void buildContainerForm() throws MalformedURLException, EscidocClientException {
        addContainerForm.setImmediate(true);
        addNameField();
        addContentModelSelect();
        addButton();
    }

    private void addNameField() {
        nameField = new TextField(ViewConstants.CONTAINER_NAME);
        nameField.setRequired(true);
        nameField.setRequiredError(ViewConstants.PLEASE_ENTER_A_CONTAINER_NAME);
        nameField.addValidator(new StringLengthValidator(ViewConstants.CONTAINER_NAME_MUST_BE_3_25_CHARACTERS, 3, 25,
            false));
        nameField.setImmediate(true);
        addContainerForm.addComponent(nameField);
    }

    private void addContentModelSelect() throws MalformedURLException, EscidocException, InternalClientException,
        TransportException {
        contentModelSelect = new NativeSelect(ViewConstants.PLEASE_SELECT_CONTENT_MODEL);
        contentModelSelect.setRequired(true);

        final ContentModelRepository cMS = new ContentModelRepository(serviceLocation);

        for (final Resource resource : cMS.findPublicOrReleasedResources()) {
            contentModelSelect.addItem(resource.getObjid());
        }

        addContainerForm.addComponent(contentModelSelect);
    }

    private void addButton() {
        addButton = new Button(ViewConstants.ADD);// , new AddCreateContainerListener(this));
        addContainerForm.addComponent(addButton);
    }

    private void buildSubWindowUsingContainerForm() {
        subwindow = new Window(ViewConstants.CREATE_CONTAINER);
        subwindow.setWidth("600px");
        subwindow.setModal(true);
        subwindow.addComponent(addContainerForm);
    }

    private void openSubWindow() {
        mainWindow.addWindow(subwindow);
    }
}
