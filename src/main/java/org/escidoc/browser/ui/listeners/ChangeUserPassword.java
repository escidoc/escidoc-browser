package org.escidoc.browser.ui.listeners;

import java.net.URISyntaxException;

import org.escidoc.browser.controller.UserAccountController;
import org.escidoc.browser.model.internal.UserProxy;
import org.escidoc.browser.repository.internal.UserAccountRepository;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.jfree.util.Log;

import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ChangeUserPassword implements ClickListener {

    private UserProxy userProxy;

    private Router router;

    private UserAccountController controller;

    private Window subwindow;

    private UserAccountRepository ur;

    public ChangeUserPassword(UserProxy userProxy, Router router, UserAccountController controller) {
        this.userProxy = userProxy;
        this.router = router;
        this.controller = controller;
        this.ur = router.getRepositories().user();
    }

    @Override
    public void buttonClick(ClickEvent event) {
        SubwindowModalExample();

    }

    private void SubwindowModalExample() {
        subwindow = new Window("Change Password");
        subwindow.setModal(true);
        subwindow.setWidth("500px");

        VerticalLayout layout = (VerticalLayout) subwindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);

        try {
            buildEditUserForm(layout);
        }
        catch (EscidocClientException e) {
            Log.debug(ViewConstants.ERROR + e.getLocalizedMessage());
        }
        catch (URISyntaxException e) {
            Log.debug(ViewConstants.ERROR + e.getLocalizedMessage());
        }

        Button close = new Button("Close", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        // layout.addComponent(close);
        // layout.setComponentAlignment(close, Alignment.TOP_RIGHT);
        router.getMainWindow().addWindow(subwindow);
    }

    private void buildEditUserForm(VerticalLayout vlAccCreateContext) throws EscidocClientException, URISyntaxException {
        final Form form = new Form();
        form.setImmediate(true);

        final PasswordField passwordField = buildPasswordField(form);
        final PasswordField verifyPasswordField = buildVerifyPasswordField(form);

        Button saveButton =
            new Button(ViewConstants.SAVE, new OnSaveClick(passwordField, form, verifyPasswordField, subwindow));

        saveButton.setWidth("-1px");
        saveButton.setHeight("-1px");
        form.getLayout().addComponent(saveButton);

        Panel formPanel = new Panel(ViewConstants.USER_PASS_FORM);
        VerticalLayout vl = new VerticalLayout();
        vl.setMargin(false, true, false, true);
        vl.addComponent(form);
        formPanel.setContent(vl);

        vlAccCreateContext.addComponent(formPanel);

        setEnability(passwordField, verifyPasswordField);
    }

    private static PasswordField buildVerifyPasswordField(final Form form) {
        final PasswordField verifyPasswordField = new PasswordField("Verify Password");
        verifyPasswordField.setImmediate(false);
        verifyPasswordField.setWidth("-1px");
        verifyPasswordField.setHeight("-1px");
        form.addField("txtPassword2", verifyPasswordField);
        return verifyPasswordField;
    }

    private static PasswordField buildPasswordField(final Form form) {
        final PasswordField passwordField = new PasswordField("Password");
        passwordField.setImmediate(false);
        passwordField.setNullSettingAllowed(false);
        passwordField.setWidth("-1px");
        passwordField.setHeight("-1px");
        form.addField("txtPassword", passwordField);
        return passwordField;
    }

    private void setEnability(final PasswordField passwordField, final PasswordField verifyPasswordField)
        throws EscidocClientException, URISyntaxException {

        boolean allowedToUpdate = true;
        passwordField.setEnabled(allowedToUpdate);
        verifyPasswordField.setEnabled(allowedToUpdate);
    }

    private final class OnSaveClick implements Button.ClickListener {

        private final PasswordField passwordField;

        private final Form form;

        private final PasswordField verifyPasswordField;

        private Window subwindow;

        private OnSaveClick(PasswordField passwordField, Form form, PasswordField verifyPasswordField, Window subwindow) {
            this.passwordField = passwordField;
            this.form = form;
            this.verifyPasswordField = verifyPasswordField;
            this.subwindow = subwindow;
        }

        @Override
        public void buttonClick(@SuppressWarnings("unused")
        com.vaadin.ui.Button.ClickEvent event) {
            try {
                form.commit();
                if (!passwordField.getValue().equals(verifyPasswordField.getValue())) {
                    router
                        .getMainWindow()
                        .showNotification(
                            "Password verification failed, please try again and make sure you are typing the same password twice ",
                            Window.Notification.TYPE_TRAY_NOTIFICATION);
                    return;
                }
                if (passwordField.getValue().toString() != "") {
                    ur.updatePassword(userProxy, passwordField.getValue().toString());
                }
                router.getMainWindow().showNotification("User updateds successfully ",
                    Window.Notification.TYPE_TRAY_NOTIFICATION);
                (subwindow.getParent()).removeWindow(subwindow);
            }
            catch (EmptyValueException e) {
                router.getMainWindow().showNotification("Please fill in all the required elements in the form",
                    Window.Notification.TYPE_TRAY_NOTIFICATION);
            }
            catch (EscidocClientException e) {
                router.getMainWindow().showNotification(ViewConstants.ERROR_UPDATING_USER + e.getLocalizedMessage(),
                    Window.Notification.TYPE_ERROR_MESSAGE);
            }
        }
    }
}
