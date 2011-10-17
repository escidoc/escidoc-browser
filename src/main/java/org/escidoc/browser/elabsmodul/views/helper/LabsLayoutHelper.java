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
package org.escidoc.browser.elabsmodul.views.helper;

import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.COMBOBOX_WIDTH;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.DIV_ALIGN_RIGHT;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.DIV_END;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.HOR_PANEL_HEIGHT;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.LABEL_WIDTH;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.STYLE_ELABS_HOR_PANEL;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.STYLE_ELABS_TEXT;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.STYLE_ELABS_TEXT_AS_LABEL;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.TEXT_WIDTH;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.USER_DESCR_ON_HOR_LAYOUT_TO_EDIT;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.USER_DESCR_ON_LABEL_TO_EDIT;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.USER_DESCR_ON_TEXTFIELD_TO_SAVE_OR_CANCEL;

import java.util.List;

import org.escidoc.browser.elabsmodul.enums.ELabsFileFormatsEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.Property;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

/**
 * Utility class.
 */
public final class LabsLayoutHelper {

    private static final Logger LOG = LoggerFactory.getLogger(LabsLayoutHelper.class);

    private LabsLayoutHelper() {
    }

    public static synchronized HorizontalLayout createHorizontalLayoutWithELabsLabelAndLabelData(
        final String labelTxt, Property dataProperty) {
        Preconditions.checkNotNull(labelTxt, "Label is null");
        Preconditions.checkNotNull(dataProperty, "DataSource is null");

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

    @Deprecated
    // TODO DELETE this method
    public static synchronized HorizontalLayout createHorizontalLayoutWithELabsLabelAndDropDownBox(
        final String labelTxt, final String dropDownDescription, Property dataProperty, final List<String> dataSource) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeUndefined();
        horizontalLayout.setDescription(USER_DESCR_ON_HOR_LAYOUT_TO_EDIT);
        horizontalLayout.setEnabled(true);
        horizontalLayout.setSpacing(true);
        horizontalLayout.setHeight(HOR_PANEL_HEIGHT);
        horizontalLayout.setStyleName(STYLE_ELABS_HOR_PANEL);

        final Label label = new Label();
        label.setWidth(LABEL_WIDTH);
        label.setValue(DIV_ALIGN_RIGHT + labelTxt + DIV_END);
        label.setContentMode(Label.CONTENT_XHTML);
        label.setDescription(USER_DESCR_ON_LABEL_TO_EDIT);

        final ComboBox comboBox = new ComboBox("", dataSource);
        comboBox.setEnabled(true);
        comboBox.setVisible(true);
        comboBox.setImmediate(true);
        comboBox.setMultiSelect(false);
        comboBox.setNullSelectionAllowed(false);
        comboBox.setPropertyDataSource(dataProperty);
        comboBox.setReadOnly(false);
        comboBox.setWidth(TEXT_WIDTH);
        comboBox.setStyleName(STYLE_ELABS_TEXT_AS_LABEL);
        comboBox.setDescription(USER_DESCR_ON_LABEL_TO_EDIT);

        horizontalLayout.addComponent(label, 0);
        horizontalLayout.addComponent(comboBox, 1);
        horizontalLayout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
        horizontalLayout.setComponentAlignment(comboBox, Alignment.MIDDLE_RIGHT);

        return horizontalLayout;
    }

    /**
     * Returns with a label based on the edited field.
     * 
     * @param property
     * @return
     */
    public static synchronized AbstractComponent createLabelFromEditedField(Property property) {
        Preconditions.checkNotNull(property, "Datasource is null");

        Label label = new Label(property);
        label.setWidth(TEXT_WIDTH);
        label.setStyleName(STYLE_ELABS_TEXT_AS_LABEL);
        label.setDescription(USER_DESCR_ON_LABEL_TO_EDIT);

        return label;
    }

    /**
     * It returns with textfield as default, but if the property arg is an combobox-property then returns with a Combox.
     * 
     * @param property
     * @return
     */
    public static synchronized AbstractComponent createEditableFieldFromLabel(Property property, boolean isComboItem) {
        Preconditions.checkNotNull(property, "Datasource is null");

        if (isComboItem) {
            // create ComboBox
            final ComboBox comboBox = new ComboBox(null, ELabsFileFormatsEnum.toList());
            comboBox.setEnabled(true);
            comboBox.setVisible(true);
            comboBox.setImmediate(true);
            comboBox.setMultiSelect(false);
            comboBox.setNullSelectionAllowed(false);
            comboBox.setPropertyDataSource(property);
            comboBox.setReadOnly(false);
            comboBox.setWidth(COMBOBOX_WIDTH);
            comboBox.setStyleName(STYLE_ELABS_TEXT_AS_LABEL);
            comboBox.setDescription(USER_DESCR_ON_LABEL_TO_EDIT);
            return comboBox;
        }
        else {
            // create TextField
            TextField textField = new TextField(property);
            textField.setWidth(TEXT_WIDTH);
            textField.setStyleName(STYLE_ELABS_TEXT);
            textField.setDescription(USER_DESCR_ON_TEXTFIELD_TO_SAVE_OR_CANCEL);
            textField.commit();
            textField.focus();
            return textField;
        }
    }

    public static HorizontalLayout createButtonLayout() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");
        Label blank = new Label("");
        blank.setWidth(LABEL_WIDTH);
        horizontalLayout.addComponent(blank, 0);
        horizontalLayout.addComponent(cancelButton, 1);
        horizontalLayout.addComponent(saveButton, 2);
        // horizontalLayout.setComponentAlignment(cancelButton, Alignment.MIDDLE_RIGHT);
        // horizontalLayout.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT);
        return horizontalLayout;
    }
}
