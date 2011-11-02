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

import org.escidoc.browser.ui.Router;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.Runo;

public class StartInvestigationViewHelper {
    private Router router;

    public StartInvestigationViewHelper(Router router) {
        this.router = router;
    }

    public void createStartButton(Panel panel) {
        Button startBtn = new Button("Start");
        startBtn.setStyleName(Runo.BUTTON_BIG);
        startBtn.setWidth("100%");
        startBtn.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (event.getButton().getCaption().equals("Start")) {
                    router.getMainWindow().getWindow().showNotification("Starting Process");
                    event.getButton().setCaption("Stop");
                }
                else {
                    router.getMainWindow().getWindow().showNotification("Halting Process");
                    event.getButton().setCaption("Start");
                }

            }
        });
        panel.addComponent(startBtn);
    }

}
