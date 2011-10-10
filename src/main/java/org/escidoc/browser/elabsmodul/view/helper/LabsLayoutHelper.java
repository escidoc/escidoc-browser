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
package org.escidoc.browser.elabsmodul.view.helper;

import static org.escidoc.browser.elabsmodul.constants.ELabViewContants.DIV_ALIGN_RIGHT;
import static org.escidoc.browser.elabsmodul.constants.ELabViewContants.DIV_END;
import static org.escidoc.browser.elabsmodul.constants.ELabViewContants.HOR_PANEL_HEIGHT;
import static org.escidoc.browser.elabsmodul.constants.ELabViewContants.LABEL_WIDTH;
import static org.escidoc.browser.elabsmodul.constants.ELabViewContants.STYLE_ELABS_HOR_PANEL;
import static org.escidoc.browser.elabsmodul.constants.ELabViewContants.STYLE_ELABS_TEXT;
import static org.escidoc.browser.elabsmodul.constants.ELabViewContants.STYLE_ELABS_TEXT_AS_LABEL;
import static org.escidoc.browser.elabsmodul.constants.ELabViewContants.TEXT_WIDTH;
import static org.escidoc.browser.elabsmodul.constants.ELabViewContants.USER_DESCR_ON_HOR_LAYOUT_TO_EDIT;
import static org.escidoc.browser.elabsmodul.constants.ELabViewContants.USER_DESCR_ON_LABEL_TO_EDIT;
import static org.escidoc.browser.elabsmodul.constants.ELabViewContants.USER_DESCR_ON_TEXTFIELD_TO_SAVE_OR_CANCEL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class LabsLayoutHelper {

    private static LabsLayoutHelper instance = null;

    private static Object synchObject = new Object();

    private static Logger LOG = LoggerFactory.getLogger(LabsLayoutHelper.class);

    private LabsLayoutHelper() {
    }

    // TODO delete because there is only static class methods
    public static synchronized LabsLayoutHelper getInstance() {
        if (instance == null) {
            synchronized (synchObject) {
                if (instance == null) {
                    instance = new LabsLayoutHelper();
                }
            }
        }
        return instance;
    }

    public static synchronized HorizontalLayout createHorizontalLayoutWithELabsLabelAndLabelData(
        final String labelTxt, Property dataProperty) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeUndefined();
        horizontalLayout.setDescription(USER_DESCR_ON_HOR_LAYOUT_TO_EDIT);
        horizontalLayout.setEnabled(true);
        horizontalLayout.setSpacing(true);
        horizontalLayout.setHeight(HOR_PANEL_HEIGHT);

        Label label = new Label();
        label.setWidth(LABEL_WIDTH);
        label.setValue(DIV_ALIGN_RIGHT + labelTxt + DIV_END);
        label.setContentMode(Label.CONTENT_XHTML);
        label.setDescription(USER_DESCR_ON_LABEL_TO_EDIT);

        Label textLabel = new Label(dataProperty);
        textLabel.setWidth(TEXT_WIDTH);
        textLabel.setDescription(USER_DESCR_ON_LABEL_TO_EDIT);
        if (dataProperty != null) {
            horizontalLayout.setStyleName(STYLE_ELABS_HOR_PANEL);
            textLabel.setStyleName(STYLE_ELABS_TEXT_AS_LABEL);
        }
        horizontalLayout.addComponent(label, 0);
        horizontalLayout.addComponent(textLabel, 1);
        horizontalLayout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
        horizontalLayout.setComponentAlignment(textLabel, Alignment.MIDDLE_RIGHT);

        return horizontalLayout;
    }

    public static synchronized HorizontalLayout createHorizontalLayoutWithELabsLabelAndCheckBoxData(
        final String labelTxt, final String checkBoxDescription, Property dataProperty) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeUndefined();
        horizontalLayout.setDescription(USER_DESCR_ON_HOR_LAYOUT_TO_EDIT);
        horizontalLayout.setEnabled(true);
        horizontalLayout.setSpacing(true);
        horizontalLayout.setHeight(HOR_PANEL_HEIGHT);
        horizontalLayout.setStyleName(STYLE_ELABS_HOR_PANEL);

        Label label = new Label();
        label.setWidth(LABEL_WIDTH);
        label.setValue(DIV_ALIGN_RIGHT + labelTxt + DIV_END);
        label.setContentMode(Label.CONTENT_XHTML);
        label.setDescription(USER_DESCR_ON_LABEL_TO_EDIT);

        CheckBox checkBox = new CheckBox(checkBoxDescription, dataProperty);
        checkBox.setEnabled(true);
        checkBox.setVisible(true);
        checkBox.setWidth(TEXT_WIDTH);
        checkBox.setStyleName(STYLE_ELABS_TEXT_AS_LABEL);
        checkBox.setDescription(USER_DESCR_ON_LABEL_TO_EDIT);

        horizontalLayout.addComponent(label, 0);
        horizontalLayout.addComponent(checkBox, 1);
        horizontalLayout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
        horizontalLayout.setComponentAlignment(checkBox, Alignment.MIDDLE_RIGHT);

        return horizontalLayout;
    }

    public static synchronized AbstractComponent createLabelFromTextField(Property property) {
        Label label = new Label(property);
        label.setWidth(TEXT_WIDTH);
        label.setStyleName(STYLE_ELABS_TEXT_AS_LABEL);
        label.setDescription(USER_DESCR_ON_LABEL_TO_EDIT);

        return label;
    }

    public static synchronized AbstractComponent createTextFieldFromLabel(Property property) {
        TextField textField = new TextField(property);
        textField.setWidth(TEXT_WIDTH);
        textField.setStyleName(STYLE_ELABS_TEXT);
        textField.setDescription(USER_DESCR_ON_TEXTFIELD_TO_SAVE_OR_CANCEL);
        textField.commit();
        textField.focus();

        return textField;
    }

    public static HorizontalLayout createButtonLayout() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");
        horizontalLayout.addComponent(cancelButton, 0);
        horizontalLayout.addComponent(saveButton, 1);

        return horizontalLayout;
    }
}
