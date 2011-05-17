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
package org.escidoc.browser;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.ui.ViewConstants;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class LandingViewImpl extends CustomComponent implements LandingView {

    private static final String HTTP = "http://";

    private static final String ESCIDOC_URI_CAN_NOT_BE_EMPTY = "eSciDoc URI can not be empty.";

    private final VerticalLayout viewLayout = new VerticalLayout();

    private final Panel panel = new Panel();

    private final FormLayout formLayout = new FormLayout();

    private final HorizontalLayout horizontalLayout = new HorizontalLayout();

    private final TextField escidocServiceUrl = new TextField(ViewConstants.ESCIDOC_URI_TEXTFIELD);

    private final Button startButton = new Button(ViewConstants.OK_LABEL);

    private final StartButtonListener startButtonListener;

    private final EscidocServiceLocation serviceLocation;

    public LandingViewImpl(final EscidocServiceLocation serviceLocation, final StartButtonListener startButtonListener) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(startButtonListener, "startButtonListener is null: %s", startButtonListener);

        this.serviceLocation = serviceLocation;
        this.startButtonListener = startButtonListener;

        setCompositionRoot(viewLayout);

        setSizeFull();
        viewLayout.setSizeFull();

        viewLayout.addComponent(panel);
        viewLayout.setComponentAlignment(panel, Alignment.MIDDLE_CENTER);

        panel.setWidth(ViewConstants.LOGIN_WINDOW_WIDTH);
        panel.setCaption(ViewConstants.WELCOMING_MESSAGE);

        addEscidocUrlField();
        addFooters();

        panel.addComponent(formLayout);
    }

    @Override
    public void addStartButton(final Button button) {
        Preconditions.checkNotNull(button, "button is null: %s", button);
        formLayout.addComponent(button);
    }

    private void addEscidocUrlField() {
        escidocServiceUrl.setWidth(265, UNITS_PIXELS);
        escidocServiceUrl.setImmediate(true);
        escidocServiceUrl.focus();
        escidocServiceUrl.setRequired(true);
        escidocServiceUrl.setRequiredError(ESCIDOC_URI_CAN_NOT_BE_EMPTY);
        setInputPrompt();
        formLayout.addComponent(escidocServiceUrl);
    }

    private void setInputPrompt() {
        if (serviceLocation.getEscidocUri() == null) {
            escidocServiceUrl.setInputPrompt(HTTP);
        }
        else {
            escidocServiceUrl.setInputPrompt(serviceLocation.getEscidocUri());
        }
    }

    private void addFooters() {
        horizontalLayout.setWidth(100, UNITS_PERCENTAGE);
        horizontalLayout.setMargin(true);
        formLayout.addComponent(horizontalLayout);
        addStartButton();
    }

    private void addStartButton() {
        startButtonListener.setInputField(escidocServiceUrl);
        startButton.addListener(startButtonListener);
        horizontalLayout.addComponent(startButton);
        horizontalLayout.setComponentAlignment(startButton, Alignment.MIDDLE_RIGHT);
    }
}