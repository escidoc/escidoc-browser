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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class AddNewEndpointURIWindow extends Window implements Button.ClickListener {

    private static final long serialVersionUID = 3399548401077016737L;

    private Callback callback;

    private Button okButton = null, cancelButton = null;

    private TextField endpointURITextField;

    private final String OK_BUTTON_TEXT = "Ok", CANCEL_BUTTON_TEXT = "Cancel";

    private static final String HTTP = "http://", HTTPS = "https://";

    private final boolean isEsyncURI;

    private static final Logger LOG = LoggerFactory.getLogger(AddNewEndpointURIWindow.class);

    public AddNewEndpointURIWindow(final Callback callback, boolean isEsyncURI) {
        LOG.debug("AddNewEndpointURIWindow is created");
        Preconditions.checkNotNull(callback, "callback is null");
        this.callback = callback;
        this.isEsyncURI = isEsyncURI;

        setModal(true);
        setWidth("550px");
        setHeight("100px");
        setClosable(false);
        setResizable(true);
        setScrollable(false);
        addComponent(buildGUI());
        if (isEsyncURI) {
            setCaption("eSyncDaemon endpoint URI");
        }
        else {
            setCaption("Depositor endpoint URI");
        }
        center();
    }

    @SuppressWarnings("serial")
    private Component buildGUI() {
        final VerticalLayout rootLayout = new VerticalLayout();
        final HorizontalLayout inputLayout = new HorizontalLayout();
        inputLayout.setSpacing(true);
        endpointURITextField = new TextField();
        endpointURITextField.setEnabled(true);
        endpointURITextField.setVisible(true);
        endpointURITextField.setNullRepresentation("");
        endpointURITextField.setValue("http://");
        endpointURITextField.setImmediate(true);
        endpointURITextField.setRequired(true);
        endpointURITextField.setRequiredError("URI cannot be empty!");
        endpointURITextField.setWidth("350px");
        endpointURITextField.focus();

        if (isEsyncURI) {
            inputLayout.addComponent(new Label("New eSync-endpoint URI:"), 0);
        }
        else {
            inputLayout.addComponent(new Label("New deposit-endpoint URI:"), 0);
        }
        inputLayout.addComponent(endpointURITextField, 1);

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
                    AddNewEndpointURIWindow.this.closeMe(true);
                }
                else if (action.equals(action_esc)) {
                    AddNewEndpointURIWindow.this.closeMe(false);
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
            if (validateURI(input = (String) endpointURITextField.getValue())) {
                callback.onAcceptAction(input);
            }
        }
        else {
            callback.onRefuseAction();
        }
        this.close();
    }

    private void closeMe(boolean withSave) {
        if (getParent() != null) {
            getParent().removeWindow(this);
        }
        if (withSave) {
            String input = null;
            if (validateURI(input = (String) endpointURITextField.getValue())) {
                callback.onAcceptAction(input);
            }
        }
        else {
            callback.onRefuseAction();
        }
        this.close();
    }

    private boolean validateURI(final String URL) {
        if (URL == null || URL.trim().equals("") || URL.trim().equals(HTTP) || URL.trim().equals(HTTPS)) {
            return false;
        }
        else {
            return true;
        }
    }

    public interface Callback {
        void onAcceptAction(final String inputURLText);

        void onRefuseAction();
    }
}