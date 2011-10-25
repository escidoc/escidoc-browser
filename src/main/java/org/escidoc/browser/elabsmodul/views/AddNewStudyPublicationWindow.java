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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AddNewStudyPublicationWindow extends Window implements Button.ClickListener {

    private static final long serialVersionUID = 2444046110793999051L;

    private Callback callback;

    private Button okButton = null, cancelButton = null;

    private TextField publicationTextField;

    private final String OK_BUTTON_TEXT = "Ok", CANCEL_BUTTON_TEXT = "Cancel";

    public AddNewStudyPublicationWindow(final Callback callback) {
        Preconditions.checkNotNull(callback, "callback is null");
        this.callback = callback;

        setModal(true);
        setWidth("50%");
        setHeight("100px");
        setClosable(true);
        setResizable(true);
        setScrollable(false);
        addComponent(buildGUI());
        center();
    }

    private Component buildGUI() {
        final VerticalLayout rootLayout = new VerticalLayout();

        final HorizontalLayout inputLayout = new HorizontalLayout();
        inputLayout.setSpacing(true);
        publicationTextField = new TextField();
        publicationTextField.setEnabled(true);
        publicationTextField.setVisible(true);
        publicationTextField.setNullRepresentation("");
        publicationTextField.setInputPrompt("Input a new document URL...");
        publicationTextField.setImmediate(true);
        publicationTextField.setRequired(true);
        publicationTextField.setRequiredError("Document URL cannot be empty!");
        publicationTextField.focus();
        inputLayout.addComponent(new Label("New publication's URL:"), 0);
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

        return rootLayout;
    }

    @Override
    public void buttonClick(final ClickEvent event) {
        if (getParent() != null) {
            getParent().removeWindow(this);
        }

        if (event.getButton().getCaption().equals(OK_BUTTON_TEXT)) {
            String input = (String) publicationTextField.getValue();
            if (input != null && !input.equals("")) {
                callback.onAcceptAction(input);
            }
        }
    }

    public interface Callback {
        void onAcceptAction(final String inputURLText);
    }
}
