/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or https://www.escidoc.org/license/ESCIDOC.LICENSE .
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.elabsmodul.views;

import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;

import com.google.common.base.Preconditions;
import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AddNewStudyPublicationWindow extends Window implements Button.ClickListener {

    private static final long serialVersionUID = 2444046110793999051L;

    private Callback callback;

    private Button okButton = null, cancelButton = null;

    private TextField publicationTextField;

    private static final String OK_BUTTON_TEXT = "Ok", CANCEL_BUTTON_TEXT = "Cancel";

    private static final String HTTP = "http://", HTTPS = "https://";

    private final boolean isMotPub;

    public AddNewStudyPublicationWindow(final Callback callback, boolean isMotPub) {
        Preconditions.checkNotNull(callback, "callback is null");
        this.callback = callback;
        this.isMotPub = isMotPub;

        setModal(true);
        setWidth("600px");
        setHeight("100px");
        setClosable(true);
        setResizable(true);
        setScrollable(false);
        addComponent(buildGUI());
        if (isMotPub) {
            setCaption("Motivating publication");
        }
        else {
            setCaption("Resulting publication");
        }
        center();
    }

    @SuppressWarnings("serial")
    private Component buildGUI() {
        final VerticalLayout rootLayout = new VerticalLayout();
        final HorizontalLayout inputLayout = new HorizontalLayout();
        inputLayout.setSpacing(true);
        publicationTextField = new TextField();
        publicationTextField.setEnabled(true);
        publicationTextField.setVisible(true);
        publicationTextField.setNullRepresentation("");
        publicationTextField.setValue("http://");
        publicationTextField.setImmediate(true);
        publicationTextField.setRequired(true);
        publicationTextField.setRequiredError("Document URL cannot be empty!");
        publicationTextField.setWidth("350px");
        publicationTextField.focus();

        if (isMotPub) {
            inputLayout.addComponent(new Label("New motivating publication's URL:"), 0);
        }
        else {
            inputLayout.addComponent(new Label("New resulting publication's URL:"), 0);
        }
        inputLayout.addComponent(publicationTextField, 1);

        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        okButton = new Button(OK_BUTTON_TEXT, this);
        okButton.setIcon(ELabsViewContants.ICON_16_OK);
        okButton.setEnabled(true);
        cancelButton = new Button(CANCEL_BUTTON_TEXT, this);
        cancelButton.setIcon(ELabsViewContants.ICON_16_CANCEL);
        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(okButton);

        rootLayout.addComponent(inputLayout);
        rootLayout.addComponent(buttonLayout);

        Panel panel = new Panel();
        panel.setContent(rootLayout);

        panel.addActionHandler(new Action.Handler() {
            private final Action action_ok = new ShortcutAction("Enter key", ShortcutAction.KeyCode.ENTER, null);

            private final Action action_esc = new ShortcutAction("Escape key", ShortcutAction.KeyCode.ESCAPE, null);

            @Override
            public void handleAction(Action action, Object sender, Object target) {
                if (action.equals(action_ok)) {
                    AddNewStudyPublicationWindow.this.closeMe(true);
                }
                else if (action.equals(action_esc)) {
                    AddNewStudyPublicationWindow.this.closeMe(false);
                }
            }

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { action_ok, action_esc };
            }
        });
        return panel;
    }

    @Override
    public void buttonClick(final ClickEvent event) {
        if (getParent() != null) {
            getParent().removeWindow(this);
        }
        String input = null;
        if (event.getButton().getCaption().equals(OK_BUTTON_TEXT)) {
            if (validateURL(input = (String) publicationTextField.getValue())) {
                callback.onAcceptAction(input);
            }
        }
    }

    private void closeMe(boolean withSave) {
        if (getParent() != null) {
            getParent().removeWindow(this);
        }
        String input = null;
        if (withSave) {
            if (validateURL(input = (String) publicationTextField.getValue())) {
                callback.onAcceptAction(input);
            }
        }
    }

    private boolean validateURL(final String URL) {
        if (URL == null || URL.trim().equals("") || URL.trim().equals(HTTP) || URL.trim().equals(HTTPS)) {
            return false;
        }
        else {
            return true;
        }
    }

    public interface Callback {
        void onAcceptAction(final String inputURLText);
    }
}
