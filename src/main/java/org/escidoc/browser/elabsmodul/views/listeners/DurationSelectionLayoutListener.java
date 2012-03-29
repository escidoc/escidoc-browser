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
 * Copyright 2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.elabsmodul.views.listeners;

import com.google.common.base.Preconditions;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

import org.escidoc.browser.elabsmodul.interfaces.ILabsInvestigationAction;
import org.escidoc.browser.elabsmodul.interfaces.ILabsPanel;
import org.escidoc.browser.elabsmodul.model.DurationBean;
import org.escidoc.browser.elabsmodul.views.DatePickerWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DurationSelectionLayoutListener implements LayoutClickListener {

    private static final long serialVersionUID = -4350955223851977866L;

    private static final Logger LOG = LoggerFactory.getLogger(DurationSelectionLayoutListener.class);

    private final ILabsPanel labsPanel;

    private final ILabsInvestigationAction labsInvestigationAction;

    public DurationSelectionLayoutListener(ILabsPanel labsPanel, ILabsInvestigationAction labsInvestigationAction) {
        Preconditions.checkNotNull(labsPanel, "LabsPanel is null");
        Preconditions.checkNotNull(labsInvestigationAction, "LabsInvestigationAction is null");
        this.labsPanel = labsPanel;
        this.labsInvestigationAction = labsInvestigationAction;
    }

    @Override
    public void layoutClick(LayoutClickEvent event) {
        final Component component = event.getComponent();

        if (!(component instanceof HorizontalLayout)) {
            LOG.error("This listener is defined only for horizontalLayout");
            return;
        }
        Component dataComponent = null;
        try {
            if ((dataComponent = ((HorizontalLayout) component).getComponent(1)) == null) {
                return;
            }
            this.labsPanel
                .getReference().getApplication().getMainWindow()
                .addWindow(new DatePickerWindow("Add investigation duration", new DatePickerWindow.Callback() {

                    @Override
                    public void onDialogResult(boolean resultIsOk, final DurationBean durationBean) {
                        if (resultIsOk && durationBean != null) {
                            DurationSelectionLayoutListener.this.labsInvestigationAction.setDuration(durationBean);
                        }
                    }
                }));
        }
        catch (IndexOutOfBoundsException e) {
            LOG.error(e.getLocalizedMessage());
        }
    }
}
