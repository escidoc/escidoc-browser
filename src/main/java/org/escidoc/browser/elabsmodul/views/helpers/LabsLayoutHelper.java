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
package org.escidoc.browser.elabsmodul.views.helpers;

import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.COMBOBOX_WIDTH;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.DIV_ALIGN_RIGHT;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.DIV_END;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.HOR_PANEL_HEIGHT;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.LABEL_WIDTH;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.STYLE_ELABS_HOR_PANEL;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.STYLE_ELABS_HOR_PANEL_FOR_TABLE;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.STYLE_ELABS_TEXT;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.STYLE_ELABS_TEXT_AS_LABEL;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.TEXT_WIDTH;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.USER_DESCR_ON_HOR_LAYOUT_TO_EDIT;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.USER_DESCR_ON_LABEL_TO_EDIT;
import static org.escidoc.browser.elabsmodul.constants.ELabsViewContants.USER_DESCR_ON_TEXTFIELD_TO_SAVE_OR_CANCEL;

import java.util.Collection;

import org.escidoc.browser.StringUtils;
import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.interfaces.ILabsInstrumentAction;
import org.escidoc.browser.elabsmodul.interfaces.ILabsInvestigationAction;
import org.escidoc.browser.elabsmodul.interfaces.IRigAction;
import org.escidoc.browser.elabsmodul.model.OrgUnitBean;
import org.escidoc.browser.elabsmodul.model.RigBean;
import org.escidoc.browser.elabsmodul.model.UserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
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
        textLabel.setStyleName(STYLE_ELABS_TEXT_AS_LABEL);

        horizontalLayout.setStyleName(STYLE_ELABS_HOR_PANEL);
        horizontalLayout.addComponent(label, 0);
        horizontalLayout.addComponent(textLabel, 1);
        horizontalLayout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
        horizontalLayout.setComponentAlignment(textLabel, Alignment.MIDDLE_RIGHT);

        return horizontalLayout;
    }

    public static synchronized HorizontalLayout createHorizontalLayoutWithELabsLabelAndLabelComplexData(
        final String labelTxt, String dataTxt) {
        Preconditions.checkNotNull(labelTxt, "Label is null");

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

        Label textLabel = new Label(dataTxt);
        textLabel.setWidth(TEXT_WIDTH);
        textLabel.setDescription(USER_DESCR_ON_LABEL_TO_EDIT);
        textLabel.setStyleName(STYLE_ELABS_TEXT_AS_LABEL);

        horizontalLayout.setStyleName(STYLE_ELABS_HOR_PANEL);
        horizontalLayout.addComponent(label, 0);
        horizontalLayout.addComponent(textLabel, 1);
        horizontalLayout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
        horizontalLayout.setComponentAlignment(textLabel, Alignment.MIDDLE_RIGHT);

        return horizontalLayout;
    }

    public static synchronized HorizontalLayout createHorizontalLayoutWithELabsLabelAndRelatedDataForRig(
        final String labelTxt, Property dataProperty, RigBean rigBean, final IRigAction controller,
        LabsRigTableHelper helper) {
        Preconditions.checkNotNull(labelTxt, "Label is null");
        Preconditions.checkNotNull(dataProperty, "DataSource is null");
        Preconditions.checkNotNull(rigBean, "RigBean is null");
        Preconditions.checkNotNull(helper, "Helper is null");
        Preconditions.checkNotNull(controller, "Controller is null");

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeUndefined();
        horizontalLayout.setEnabled(true);
        horizontalLayout.setSpacing(true);
        horizontalLayout.setStyleName(STYLE_ELABS_HOR_PANEL_FOR_TABLE);

        Label label = new Label();
        label.setWidth(LABEL_WIDTH);
        label.setValue(DIV_ALIGN_RIGHT + labelTxt + DIV_END);
        label.setContentMode(Label.CONTENT_XHTML);
        label.setDescription(USER_DESCR_ON_LABEL_TO_EDIT);
        label.setStyleName(STYLE_ELABS_HOR_PANEL);

        horizontalLayout.addComponent(label, 0);
        horizontalLayout.addComponent(helper.createTableLayoutForRig(rigBean, controller), 1);
        horizontalLayout.setComponentAlignment(label, Alignment.TOP_LEFT);

        return horizontalLayout;
    }

    public static synchronized HorizontalLayout createHorizontalLayoutWithPublicationDataForStudy(
        final String labelTxt, Property dataProperty, boolean isMotNotResPublication, LabsStudyTableHelper helper) {
        Preconditions.checkNotNull(labelTxt, "Label is null");
        Preconditions.checkNotNull(dataProperty, "DataSource is null");
        Preconditions.checkNotNull(helper, "Helper is null");

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeUndefined();
        horizontalLayout.setEnabled(true);
        horizontalLayout.setSpacing(true);
        horizontalLayout.setStyleName(STYLE_ELABS_HOR_PANEL_FOR_TABLE);

        Label label = new Label();
        label.setWidth(LABEL_WIDTH);
        label.setValue(DIV_ALIGN_RIGHT + labelTxt + DIV_END);
        label.setContentMode(Label.CONTENT_XHTML);
        label.setDescription(USER_DESCR_ON_LABEL_TO_EDIT);
        label.setStyleName(STYLE_ELABS_HOR_PANEL);

        horizontalLayout.addComponent(label, 0);
        if (isMotNotResPublication) {
            horizontalLayout.addComponent(helper.createTableLayoutForMotPublications(), 1);
        }
        else {
            horizontalLayout.addComponent(helper.createTableLayoutForResPublications(), 1);
        }
        horizontalLayout.setComponentAlignment(label, Alignment.TOP_LEFT);
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

    /**
     * Returns with a label based on the edited field.
     * 
     * @param property
     * @return boolean an element switch happened or not
     */
    public static synchronized boolean switchToLabelFromEditedField(HorizontalLayout parentLayout) {
        Preconditions.checkNotNull(parentLayout, "ParentLayout on DynamicLayout is null");
        Component staticLabelComponent = null, dataComponent = null;
        try {
            staticLabelComponent = parentLayout.getComponent(0);
            dataComponent = parentLayout.getComponent(1);
        }
        catch (IndexOutOfBoundsException e) {
            return false;
        }
        Property dataProperty = null;
        Label label = null;
        if (dataComponent instanceof TextField) {
            dataProperty = ((TextField) dataComponent).getPropertyDataSource();
            label = new Label(dataProperty);
        }
        else if (dataComponent instanceof ComboBox) {
            if (((ComboBox) dataComponent).getValue() instanceof String) {
                dataProperty = ((ComboBox) dataComponent).getPropertyDataSource();
                label = new Label(dataProperty);
            }
            else if (((ComboBox) dataComponent).getValue() instanceof RigBean) {
                label = new Label(((RigBean) ((ComboBox) dataComponent).getValue()).getComplexId());
            }
            else if (((ComboBox) dataComponent).getValue() instanceof UserBean) {
                label = new Label(((UserBean) ((ComboBox) dataComponent).getValue()).getComplexId());
            }
            else if (((ComboBox) dataComponent).getValue() instanceof OrgUnitBean) {
                label = new Label(((OrgUnitBean) ((ComboBox) dataComponent).getValue()).getComplexId());
            }
            else {
                LOG.error("Incorrect data type at the switch to label");
            }
        }

        if (label == null) {
            return false;
        }

        label.setWidth(TEXT_WIDTH);
        label.setStyleName(STYLE_ELABS_TEXT_AS_LABEL);
        label.setDescription(USER_DESCR_ON_LABEL_TO_EDIT);

        parentLayout.replaceComponent(dataComponent, label);
        parentLayout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
        ((Label) staticLabelComponent).setDescription(USER_DESCR_ON_LABEL_TO_EDIT);
        parentLayout.setDescription(USER_DESCR_ON_HOR_LAYOUT_TO_EDIT);
        return true;
    }

    /**
     * @param property
     * @return
     */
    public static synchronized AbstractComponent createTextFieldFromLabel(Property property) {
        Preconditions.checkNotNull(property, "Datasource is null");

        TextField textField = new TextField(property);
        textField.setWidth(TEXT_WIDTH);
        textField.setStyleName(STYLE_ELABS_TEXT);
        textField.setDescription(USER_DESCR_ON_TEXTFIELD_TO_SAVE_OR_CANCEL);
        textField.setNullRepresentation("");
        textField.commit();
        textField.focus();
        return textField;
    }

    /**
     * @param property
     * @return
     */
    public static synchronized AbstractComponent createStaticComboBoxFieldFromLabel(
        Property property, Collection<?> options) {
        Preconditions.checkNotNull(property, "Datasource is null");
        Preconditions.checkNotNull(options, "Options collection is null");
        ComboBox comboBox = new ComboBox(null, options);
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

    /**
     * @param property
     * @return
     */
    @SuppressWarnings("serial")
    public static synchronized AbstractComponent createDynamicComboBoxFieldForInvestigation(
        final ILabsInvestigationAction labsInvestigationAction, Property property, String itemCaptionProperty,
        final Container itemContainer) {
        Preconditions.checkNotNull(labsInvestigationAction, "LabsInvestigationAction is null");
        Preconditions.checkNotNull(itemContainer, "ItemContainer is null");

        try {
            final ComboBox comboBox = new ComboBox(StringUtils.EMPTY_STRING, itemContainer);

            if (property != null && itemCaptionProperty == null) {
                comboBox.setPropertyDataSource(property);
            }
            else if (property == null && itemCaptionProperty != null) {
                comboBox.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_PROPERTY);
                comboBox.setItemCaptionPropertyId(itemCaptionProperty);
            }

            comboBox.setEnabled(true);
            comboBox.setVisible(true);
            comboBox.setImmediate(true);
            comboBox.setMultiSelect(false);
            comboBox.setNullSelectionAllowed(false);
            comboBox.setReadOnly(false);
            comboBox.setWidth(COMBOBOX_WIDTH);
            comboBox.setStyleName(STYLE_ELABS_TEXT_AS_LABEL);
            comboBox.setDescription(USER_DESCR_ON_LABEL_TO_EDIT);

            comboBox.addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    if ((comboBox.getValue() instanceof String)) {
                    }
                    else if (comboBox.getValue() instanceof RigBean) {
                        labsInvestigationAction.setRigBean((RigBean) comboBox.getValue());
                    }
                    else if (comboBox.getValue() instanceof UserBean) {
                        labsInvestigationAction.setInvestigator(((UserBean) comboBox.getValue()).getId());
                    }
                    else {
                        LOG.error("Wrong data type in combobox");
                    }
                }
            });
            return comboBox;
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    /**
     * @param property
     * @return
     */
    @SuppressWarnings("serial")
    public static synchronized AbstractComponent createDynamicComboBoxFieldForInstrument(
        final ILabsInstrumentAction labsInstrumentAction, Property property, String itemCaptionProperty,
        final Container itemContainer) {
        Preconditions.checkNotNull(labsInstrumentAction, "LabsInstrumentAction is null");
        Preconditions.checkNotNull(itemContainer, "ItemContainer is null");

        try {
            final ComboBox comboBox = new ComboBox(StringUtils.EMPTY_STRING, itemContainer);

            if (property != null && itemCaptionProperty == null) {
                comboBox.setPropertyDataSource(property);
            }
            else if (property == null && itemCaptionProperty != null) {
                comboBox.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_PROPERTY);
                comboBox.setItemCaptionPropertyId(itemCaptionProperty);
            }

            comboBox.setEnabled(true);
            comboBox.setVisible(true);
            comboBox.setImmediate(true);
            comboBox.setMultiSelect(false);
            comboBox.setNullSelectionAllowed(false);
            comboBox.setReadOnly(false);
            comboBox.setWidth(COMBOBOX_WIDTH);
            comboBox.setStyleName(STYLE_ELABS_TEXT_AS_LABEL);
            comboBox.setDescription(USER_DESCR_ON_LABEL_TO_EDIT);

            comboBox.addListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    if ((comboBox.getValue() instanceof String)) {
                    }
                    else if (comboBox.getValue() instanceof UserBean) {
                        labsInstrumentAction.setDeviceSupervisor(((UserBean) comboBox.getValue()).getId());
                    }
                    else if (comboBox.getValue() instanceof OrgUnitBean) {
                        labsInstrumentAction.setInstitute(((OrgUnitBean) comboBox.getValue()).getId());
                    }
                    else {
                        LOG.error("Wrong data type in combobox");
                    }
                }
            });
            return comboBox;
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    public static HorizontalLayout createButtonLayout() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        Button saveButton = new Button("Save");
        saveButton.setIcon(ELabsViewContants.ICON_16_OK);
        Label blank = new Label("");
        blank.setWidth(LABEL_WIDTH);
        horizontalLayout.addComponent(blank, 0);
        horizontalLayout.addComponent(saveButton, 1);
        return horizontalLayout;
    }
}
