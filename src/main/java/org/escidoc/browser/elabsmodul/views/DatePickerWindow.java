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

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.model.DurationBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class DatePickerWindow extends Window implements Button.ClickListener {

    private static final long serialVersionUID = -7866876459962725387L;

    private Callback callback;

    private Button ok = new Button("Ok", this);

    private Button cancel = new Button("Cancel", this);

    private InlineDateField dateField = null;

    private final String INPUT_WIDTH = "30px";

    private static final Logger LOG = LoggerFactory.getLogger(DatePickerWindow.class);

    private final TextField tfDays = new TextField(), tfHours = new TextField(), tfMinutes = new TextField();

    private final String propDuration = "Duration", propFinishingTime = "Finishing time";

    private Label lDay = new Label("days"), lHour = new Label("hours"), lMinutes = new Label("minutes");

    private OptionGroup optionGroup = null;

    public DatePickerWindow(String caption, Callback callback) {
        super(caption);
        this.callback = callback;
        createInLineDateField();
        Preconditions.checkNotNull(this.dateField, "InlineDateField is null");

        final VerticalLayout verticalLayout = new VerticalLayout();
        HorizontalLayout buttonLayout;
        verticalLayout.addComponent(createOptionGroup());
        verticalLayout.addComponent(new Label("<p/>", Label.CONTENT_XHTML));
        verticalLayout.addComponent(createInputField());
        verticalLayout.addComponent(new Label("<hr/>", Label.CONTENT_XHTML));
        verticalLayout.addComponent(this.dateField);
        verticalLayout.addComponent(new Label("<p/>", Label.CONTENT_XHTML));
        verticalLayout.addComponent(buttonLayout = createButtonLayout());
        verticalLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);
        addComponent(verticalLayout);

        setWidth("330px");
        setHeight("440px");
        setModal(true);
        setClosable(false);
        setResizable(true);
        setScrollable(false);
    }

    private HorizontalLayout createInputField() {
        tfDays.setWidth(INPUT_WIDTH);
        tfHours.setWidth(INPUT_WIDTH);
        tfMinutes.setWidth(INPUT_WIDTH);
        tfDays.setMaxLength(2);
        tfHours.setMaxLength(2);
        tfMinutes.setMaxLength(2);
        tfDays.setImmediate(true);
        tfHours.setImmediate(true);
        tfMinutes.setImmediate(true);
        tfDays.setTextChangeEventMode(TextChangeEventMode.LAZY);
        tfHours.setTextChangeEventMode(TextChangeEventMode.LAZY);
        tfMinutes.setTextChangeEventMode(TextChangeEventMode.LAZY);
        tfDays.setNullRepresentation("0");
        tfHours.setNullRepresentation("0");
        tfMinutes.setNullRepresentation("0");
        tfDays.setValue(0);
        tfHours.setValue(0);
        tfMinutes.setValue(0);
        lDay.setImmediate(true);
        lHour.setImmediate(true);
        lMinutes.setImmediate(true);

        HorizontalLayout inputFieldLayout = new HorizontalLayout();
        inputFieldLayout.setSpacing(true);
        inputFieldLayout.addComponent(tfDays);
        inputFieldLayout.addComponent(lDay);
        inputFieldLayout.addComponent(tfHours);
        inputFieldLayout.addComponent(lHour);
        inputFieldLayout.addComponent(tfMinutes);
        inputFieldLayout.addComponent(lMinutes);

        final FieldEvents.TextChangeListener dayTextChangeListener = new FieldEvents.TextChangeListener() {

            private static final long serialVersionUID = 2177375588542658659L;

            @Override
            public void textChange(TextChangeEvent event) {
                try {
                    synchronized (event) {
                        if (!event.getText().trim().isEmpty()) {
                            Integer.valueOf(event.getText().trim());

                            if (tfDays.getValue() instanceof String) {
                                throw new NumberFormatException();
                            }

                            final int days =
                                (event.getText() != null && (!event.getText().isEmpty())) ? Integer.valueOf(
                                    event.getText().trim()).intValue() : 0;

                            if (days < 0 || days > 14) {
                                tfDays.setValue(0);
                                tfDays.setInputPrompt("!");
                                tfDays.focus();
                                DatePickerWindow.this
                                    .getApplication()
                                    .getMainWindow()
                                    .showNotification("Wrong input", "Should be between 0 and 14",
                                        Notification.TYPE_WARNING_MESSAGE);
                                lDay.setValue((days == 0 || days == 1) ? "day" : "days");
                            }
                            tfMinutes.commit();
                        }
                    }
                }
                catch (ClassCastException e) {
                    LOG.error(e.getMessage());
                }
                catch (NumberFormatException e) {
                    DatePickerWindow.this
                        .getApplication().getMainWindow()
                        .showNotification("Only Number", "Only numbers are allowed", Notification.TYPE_WARNING_MESSAGE);
                    ((TextField) event.getSource()).setValue(0);
                    ((TextField) event.getSource()).setInputPrompt("!");
                }
            }
        };

        final FieldEvents.TextChangeListener hourTextChangeListener = new FieldEvents.TextChangeListener() {
            private static final long serialVersionUID = 5790627391829914023L;

            @Override
            public void textChange(TextChangeEvent event) {
                try {
                    synchronized (event) {
                        if (!event.getText().trim().isEmpty()) {
                            Integer.valueOf(event.getText().trim());

                            if (tfHours.getValue() instanceof String) {
                                throw new NumberFormatException();
                            }

                            final int hours =
                                (event.getText() != null && (!event.getText().isEmpty())) ? Integer.valueOf(
                                    event.getText().trim()).intValue() : 0;

                            if (hours < 0 || hours > 23) {
                                tfHours.setValue(0);
                                tfHours.setInputPrompt("!");
                                tfHours.focus();
                                DatePickerWindow.this
                                    .getApplication()
                                    .getMainWindow()
                                    .showNotification("Wrong input", "Should be between 0 and 23",
                                        Notification.TYPE_WARNING_MESSAGE);
                                lHour.setValue((hours == 0 || hours == 1) ? "hour" : "hours");
                            }
                            tfMinutes.commit();
                        }
                    }
                }
                catch (ClassCastException e) {
                    LOG.error(e.getMessage());
                }
                catch (NumberFormatException e) {
                    DatePickerWindow.this
                        .getApplication().getMainWindow()
                        .showNotification("Only Number", "Only numbers are allowed", Notification.TYPE_WARNING_MESSAGE);
                    ((TextField) event.getSource()).setValue(0);
                    ((TextField) event.getSource()).setInputPrompt("!");
                }
            }
        };

        final FieldEvents.TextChangeListener minuteTextChangeListener = new FieldEvents.TextChangeListener() {
            private static final long serialVersionUID = 1311566388273024875L;

            @Override
            public void textChange(TextChangeEvent event) {
                try {
                    synchronized (event) {
                        if (!event.getText().trim().isEmpty()) {
                            Integer.valueOf(event.getText().trim());

                            if (tfMinutes.getValue() instanceof String) {
                                throw new NumberFormatException();
                            }
                            final int minutes =
                                (event.getText() != null && (!event.getText().isEmpty())) ? Integer.valueOf(
                                    event.getText().trim()).intValue() : 0;

                            if (minutes < 0 || minutes > 59) {
                                tfMinutes.setValue(0);
                                tfMinutes.setInputPrompt("!");
                                tfMinutes.focus();
                                DatePickerWindow.this
                                    .getApplication()
                                    .getMainWindow()
                                    .showNotification("Wrong input", "Should be between 0 and 59",
                                        Notification.TYPE_WARNING_MESSAGE);
                                lMinutes.setValue((minutes == 0 || minutes == 1) ? "minute" : "minutes");
                            }
                            tfMinutes.commit();
                        }
                    }
                }
                catch (ClassCastException e) {
                    LOG.error(e.getMessage());
                }
                catch (NumberFormatException e) {
                    DatePickerWindow.this
                        .getApplication().getMainWindow()
                        .showNotification("Only Number", "Only numbers are allowed", Notification.TYPE_WARNING_MESSAGE);
                    ((TextField) event.getSource()).setValue(0);
                    ((TextField) event.getSource()).setInputPrompt("!");
                }
            }
        };
        tfDays.addListener(dayTextChangeListener);
        tfHours.addListener(hourTextChangeListener);
        tfMinutes.addListener(minuteTextChangeListener);

        return inputFieldLayout;
    }

    private OptionGroup createOptionGroup() {
        optionGroup = new OptionGroup("Choose type:");
        optionGroup.addItem(propDuration);
        optionGroup.addItem(propFinishingTime);
        optionGroup.setEnabled(true);
        optionGroup.setVisible(true);
        optionGroup.setImmediate(true);
        optionGroup.setMultiSelect(false);
        optionGroup.setNullSelectionAllowed(false);
        optionGroup.select(propDuration);

        optionGroup.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = 8998287008424355278L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean isDuration = (((OptionGroup) event.getProperty()).getValue()).equals(propDuration);
                tfDays.setEnabled(isDuration);
                tfHours.setEnabled(isDuration);
                tfMinutes.setEnabled(isDuration);
                lDay.setEnabled(isDuration);
                lHour.setEnabled(isDuration);
                lMinutes.setEnabled(isDuration);
                dateField.setEnabled(!isDuration);
            }
        });
        return optionGroup;
    }

    private HorizontalLayout createButtonLayout() {
        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        cancel.setIcon(ELabsViewContants.ICON_16_CANCEL);
        ok.setIcon(ELabsViewContants.ICON_16_OK);
        buttonLayout.addComponent(cancel);
        buttonLayout.addComponent(ok);
        addComponent(buttonLayout);

        return buttonLayout;
    }

    private void createInLineDateField() {
        this.dateField =
            new InlineDateField(ELabsViewContants.DATEPICKER_CAPTION, Calendar.getInstance(TimeZone.getDefault(),
                Locale.getDefault()).getTime());
        this.dateField.setResolution(InlineDateField.RESOLUTION_MIN);
        this.dateField.setImmediate(true);
        this.dateField.setShowISOWeekNumbers(true);
        this.dateField.setLocale(Locale.getDefault());
        this.dateField.setTimeZone(TimeZone.getDefault());
        this.dateField.setEnabled(false);

        this.dateField.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -2403060484955547831L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                LOG.debug("DataPicker's value changed to: " + dateField.getValue().toString());
                Calendar now = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
                Calendar selected = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
                selected.setTime((Date) dateField.getValue());
                Calendar limit = (Calendar) now.clone();
                limit.add(Calendar.DAY_OF_YEAR, 14);
                if (selected.before(now) || selected.after(limit)) {
                    DatePickerWindow.this
                        .getApplication()
                        .getMainWindow()
                        .showNotification("Wrong date", "Selected Date must be in the next two weeks from now",
                            Notification.TYPE_WARNING_MESSAGE);
                }
            }
        });
    }

    @Override
    public void buttonClick(final Button.ClickEvent event) {
        if (getParent() != null) {
            getParent().removeWindow(this);
        }
        int day = 0, hour = 0, minute = 0;

        if (optionGroup.getValue().toString().equals(propDuration)) {
            if (tfDays.getValue() instanceof String) {
                day = Integer.valueOf((String) tfDays.getValue()).intValue();
            }
            else if (tfDays.getValue() instanceof Integer) {
                day = (Integer) tfDays.getValue();
            }

            if (tfHours.getValue() instanceof String) {
                hour = Integer.valueOf((String) tfHours.getValue()).intValue();
            }
            else if (tfHours.getValue() instanceof Integer) {
                hour = (Integer) tfHours.getValue();
            }

            if (tfMinutes.getValue() instanceof String) {
                minute = Integer.valueOf((String) tfMinutes.getValue()).intValue();
            }
            else if (tfMinutes.getValue() instanceof Integer) {
                minute = (Integer) tfMinutes.getValue();
            }

            LOG.debug("date fields: " + day + "|" + hour + "|" + minute);
        }
        else {
            Date date = (Date) dateField.getValue();
            Date now = new Date();

            if (date.after(now)) {
                long sec = (date.getTime() - now.getTime()) / 1000;
                int seconds = Long.valueOf(sec).intValue();
                day = seconds / 86400;
                hour = (seconds - day * 86400) / 3600;
                minute = (seconds - day * 86400 - hour * 3600) / 60;
                LOG.debug("date picker: " + day + "|" + hour + "|" + minute);

                if (day > 14) {
                    day = 14;
                }
            }
            else {
                this.getApplication()
                    .getMainWindow()
                    .showNotification("Wrong date", "Selected Date must be in the next two weeks from now",
                        Notification.TYPE_WARNING_MESSAGE);
            }
        }

        DurationBean durationBean = new DurationBean();
        durationBean.setDays(day);
        durationBean.setHours(hour);
        durationBean.setMinutes(minute);

        callback.onDialogResult(event.getSource() == ok, durationBean);
    }

    public interface Callback {
        public void onDialogResult(boolean resultIsOk, final DurationBean durationBean);
    }
}
