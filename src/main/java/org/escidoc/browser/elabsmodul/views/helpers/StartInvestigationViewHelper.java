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

import org.escidoc.browser.elabsmodul.interfaces.IInvestigationAction;
import org.escidoc.browser.elabsmodul.interfaces.ILabsPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;

public class StartInvestigationViewHelper {

    private final ILabsPanel labsPanel;

    private final IInvestigationAction investigationAction;

    private static Logger LOG = LoggerFactory.getLogger(StartInvestigationViewHelper.class);

    public StartInvestigationViewHelper(ILabsPanel labsPanel, IInvestigationAction investigationAction) {
        Preconditions.checkNotNull(labsPanel, "labsPanel is null");
        Preconditions.checkNotNull(investigationAction, "investigationAction is null");
        this.labsPanel = labsPanel;
        this.investigationAction = investigationAction;
    }

    public void createStartButton(Panel panel) {
        Button startBtn = new Button("Start Investigation");
        startBtn.setWidth("100%");

        startBtn.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = -7563393988056484131L;

            @Override
            public void buttonClick(ClickEvent event) {
                if (event.getButton().getCaption().equals("Start Investigation")) {
                    labsPanel
                        .getReference().getApplication().getMainWindow().getWindow()
                        .showNotification("Starting Process...");
                    try {
                        Thread.sleep(500);
                    }
                    catch (InterruptedException e) {
                        LOG.error(e.getMessage());
                    }
                    investigationAction.getLabsService().start();
                    event.getButton().setCaption("Stop Investigation");
                }
                else {
                    labsPanel
                        .getReference().getApplication().getMainWindow().getWindow()
                        .showNotification("Halting Process...");

                    investigationAction.getLabsService().stop();
                    event.getButton().setCaption("Start Investigation");
                }
            }
        });
        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(startBtn);
        layout.setWidth("100%");
        layout.setComponentAlignment(startBtn, Alignment.MIDDLE_CENTER);
        panel.addComponent(layout);
    }
}
