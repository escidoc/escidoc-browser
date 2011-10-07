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
package org.escidoc.browser.elabsmodul.view.listeners;

import static org.escidoc.browser.elabsmodul.constants.ELabViewContants.USER_DESCR_ON_FORM_LAYOUT_TO_SAVE;
import static org.escidoc.browser.elabsmodul.constants.ELabViewContants.USER_DESCR_ON_HOR_LAYOUT_TO_EDIT;
import static org.escidoc.browser.elabsmodul.constants.ELabViewContants.USER_DESCR_ON_HOR_LAYOUT_TO_SAVE;
import static org.escidoc.browser.elabsmodul.constants.ELabViewContants.USER_DESCR_ON_LABEL_TO_EDIT;
import static org.escidoc.browser.elabsmodul.constants.ELabViewContants.USER_DESCR_ON_LABEL_TO_SAVE;
import static org.escidoc.browser.elabsmodul.constants.ELabViewContants.USER_DESCR_ON_TEXTFIELD_TO_SAVE_OR_CANCEL;

import java.util.List;

import org.escidoc.browser.elabsmodul.view.helper.LabsLayoutHelper;
import org.escidoc.browser.elabsmodul.view.subcontent.ILabsAction;
import org.escidoc.browser.elabsmodul.view.subcontent.ILabsPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Listener to handle all events of the integrated former eLabs Components
 */
public final class LabsClientViewEventHandler implements LayoutClickListener {

    private static final long serialVersionUID = -2295431922009026522L;

    private static final Logger LOG = LoggerFactory.getLogger(LabsClientViewEventHandler.class);

    private final List<HorizontalLayout> registeredComponents;

    private final VerticalLayout mainComponent;

    private ILabsPanel containerPanel = null;

    private ILabsAction containerAction = null;

    private Window mainWindow = null;

    private final LabsClientTextFieldEventHandler clientTextFieldEventHandler;

    public LabsClientViewEventHandler(final List<HorizontalLayout> registeredComponents, final VerticalLayout mainLayout, final ILabsPanel containerPanel,
        final ILabsAction containerAction) {
        LOG.info("Constructor created.");
        this.mainWindow = containerPanel.getMainWindow();
        this.clientTextFieldEventHandler = new LabsClientTextFieldEventHandler();
        this.registeredComponents = registeredComponents;
        this.mainComponent = mainLayout;
        this.containerPanel = containerPanel;
        this.containerAction = containerAction;
        this.containerPanel.getReference().addActionHandler(this.clientTextFieldEventHandler);
    }

    @Override
    public void layoutClick(LayoutClickEvent event) {
        final Component component = event.getComponent();
        final Component childComponent = event.getChildComponent();
        final Object source = event.getSource();
        LOG.info("Layout Click Event happpened. \n" + "childComponent: " + childComponent + "\nComponent" + component + "\nsource: " + source);

        synchronized (component) {
            if (!(component instanceof VerticalLayout)) {
                LOG.error("Wrong EventHandler function!");
                return;
            }
            Component modifiedComponent = this.containerPanel.getModifiedComponent();
            if (modifiedComponent != null && (childComponent == null || !modifiedComponent.equals(childComponent))) {
                Component dataComponent = ((HorizontalLayout) modifiedComponent).getComponent(1);

                if (!(dataComponent instanceof TextField)) {
                    LOG.error("ModifiedComponent's dataComponent is not a Textfield!");
                    return;
                }
                this.mainComponent.setDescription(null);
                LOG.info("Change TextField to Label");
                Component newComponent = LabsLayoutHelper.createLabelFromTextField(((TextField) dataComponent).getPropertyDataSource());
                ((HorizontalLayout) modifiedComponent).replaceComponent(dataComponent, newComponent);
                ((HorizontalLayout) modifiedComponent).setComponentAlignment(newComponent, Alignment.MIDDLE_LEFT);
                ((Label) ((HorizontalLayout) modifiedComponent).getComponent(0)).setDescription(USER_DESCR_ON_LABEL_TO_EDIT);
                ((HorizontalLayout) modifiedComponent).setDescription(USER_DESCR_ON_HOR_LAYOUT_TO_EDIT);
                this.containerPanel.setModifiedComponent(null);
            }
            else {
                LOG.info("Nothing was modified");
            }

            if (childComponent instanceof HorizontalLayout) {

                if (!registeredComponents.contains(childComponent)) {
                    LOG.error("Action of an UnRegistered Component!");
                    return;
                }
                Component dataComponent = ((HorizontalLayout) childComponent).getComponent(1);

                if (dataComponent instanceof AbstractComponent) {
                    containerAction.showButtonLayout();
                }

                if (dataComponent instanceof Label) {
                    LOG.info("Change Label to TextField");
                    if (this.containerPanel.getModifiedComponent() != null) {
                        LOG.error("LastModifiedComponent must be saved already at this point!!!");
                        return;
                    }
                    this.containerPanel.setModifiedComponent(childComponent);

                    Component newComponent = LabsLayoutHelper.createTextFieldFromLabel(((Label) dataComponent).getPropertyDataSource());
                    ((HorizontalLayout) childComponent).replaceComponent(dataComponent, newComponent);
                    ((HorizontalLayout) childComponent).setComponentAlignment(newComponent, Alignment.MIDDLE_LEFT);
                    ((Label) ((HorizontalLayout) childComponent).getComponent(0)).setDescription(USER_DESCR_ON_LABEL_TO_SAVE);
                    ((HorizontalLayout) childComponent).setDescription(USER_DESCR_ON_HOR_LAYOUT_TO_SAVE);
                    this.mainComponent.setDescription(USER_DESCR_ON_FORM_LAYOUT_TO_SAVE);
                }
                else if (dataComponent instanceof TextField) {

                    if (((HorizontalLayout) childComponent).getComponent(1).equals(dataComponent)) {
                        LOG.info("TextField is clicked , DO NOT any ACTIoN!");
                        ((TextField) dataComponent).setDescription(USER_DESCR_ON_TEXTFIELD_TO_SAVE_OR_CANCEL);
                    }
                    else {
                        LOG.info("Change TextField to Label");
                        Component newComponent = LabsLayoutHelper.createLabelFromTextField(((TextField) dataComponent).getPropertyDataSource());
                        ((HorizontalLayout) childComponent).replaceComponent(dataComponent, newComponent);
                        ((HorizontalLayout) childComponent).setComponentAlignment(newComponent, Alignment.MIDDLE_LEFT);
                        ((Label) ((HorizontalLayout) childComponent).getComponent(0)).setDescription(USER_DESCR_ON_LABEL_TO_EDIT);
                        ((HorizontalLayout) childComponent).setDescription(USER_DESCR_ON_HOR_LAYOUT_TO_EDIT);
                    }
                }
                else if (dataComponent instanceof CheckBox) {
                    boolean value = (Boolean) ((CheckBox) dataComponent).getValue();
                }
                LOG.info("Child's Data Component: " + dataComponent);
                LOG.info("Actual modified Component: " + this.containerPanel.getModifiedComponent());
            }
        }
    }

    private class LabsClientTextFieldEventHandler implements Handler {

        private static final long serialVersionUID = 4678928548456125573L;

        private final Action action_ok = new ShortcutAction("Enter key", ShortcutAction.KeyCode.ENTER, null);

        private final Action action_esc = new ShortcutAction("Escape key", ShortcutAction.KeyCode.ESCAPE, null);

        public LabsClientTextFieldEventHandler() {
        }

        @Override
        public Action[] getActions(Object target, Object sender) {
            return new Action[] { action_ok, action_esc };
        }

        @Override
        public void handleAction(Action action, Object sender, Object target) {

            Component modifiedComponent = LabsClientViewEventHandler.this.containerPanel.getModifiedComponent();

            if (modifiedComponent == null || !registeredComponents.contains(modifiedComponent)) {
                LOG.error("Lastmodified component must not be null OR must be a registered component this point!");
                return;
            }

            if (!modifiedComponent.equals(((TextField) target).getParent())) {
                LOG.error("Unsynchronized LastModified Component!");
                return;
            }

            if (action.equals(action_ok)) {
                LOG.info("Enter Key is hit.");

                ((TextField) target).commit();

                // LOG.info("Save Modified Component...");
                // saveComponent(modifiedComponent);
                LabsClientViewEventHandler.this.mainComponent.setDescription(null);
            }
            else if (action.equals(action_esc)) {
                LOG.info("Escape Key is hit.");
                LOG.info("DO NOT Save Component...");
                ((TextField) target).discard();
            }
            Component dataComponent = ((HorizontalLayout) modifiedComponent).getComponent(1);
            Component staticLabelComponent = ((HorizontalLayout) modifiedComponent).getComponent(0);
            Component newComponent = LabsLayoutHelper.createLabelFromTextField(((TextField) target).getPropertyDataSource());

            ((HorizontalLayout) modifiedComponent).replaceComponent(dataComponent, newComponent);
            ((HorizontalLayout) modifiedComponent).setComponentAlignment(newComponent, Alignment.MIDDLE_LEFT);
            ((Label) staticLabelComponent).setDescription(USER_DESCR_ON_LABEL_TO_EDIT);
            ((HorizontalLayout) modifiedComponent).setDescription(USER_DESCR_ON_HOR_LAYOUT_TO_EDIT);

            LabsClientViewEventHandler.this.containerPanel.setModifiedComponent(null);
        }
    }

    public List<HorizontalLayout> getRegisteredComponents() {
        return registeredComponents;
    }

    public AbstractComponent getFormComponent() {
        return mainComponent;
    }

    public LabsClientTextFieldEventHandler getClientTextFieldEventHandler() {
        return clientTextFieldEventHandler;
    }

    private void saveComponent(Component component) {
        this.containerPanel.saveAction();
    }
}