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
package org.escidoc.browser.ui.view.helpers;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import org.escidoc.browser.ui.Router;

import java.util.HashMap;
import java.util.Map;

public class DeleteContainerShowLogsHelper {

    private final HashMap<String, String> listNotDeleted;

    private final HashMap<String, String> listDeleted;

    private final Router router;

    public DeleteContainerShowLogsHelper(HashMap<String, String> listDeleted, HashMap<String, String> listNotDeleted,
        Router router) {
        this.listDeleted = listDeleted;
        this.listNotDeleted = listNotDeleted;

        this.router = router;
    }

    public void showWindow() {
        final Window subwindow = new Window("Change Category Type");
        subwindow.setModal(true);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);

        Table tblDeleted = new Table("Successfully deleted resources");
        tblDeleted.setWidth("90%");
        tblDeleted.addContainerProperty("Id", String.class, null);
        tblDeleted.addContainerProperty("Resource ", String.class, null);

        for (Map.Entry<String, String> entry : listDeleted.entrySet()) {
            tblDeleted.addItem(new Object[] { entry.getKey(), entry.getValue() }, entry.getKey());
        }
        layout.addComponent(tblDeleted);

        Table tblNotDeleted = new Table("Resources that could not be deleted");
        tblNotDeleted.setWidth("90%");
        tblNotDeleted.addContainerProperty("Resource Id", String.class, null);
        tblNotDeleted.addContainerProperty("Resource & Error", String.class, null);

        for (Map.Entry<String, String> entry : listNotDeleted.entrySet()) {
            tblNotDeleted.addItem(new Object[] { entry.getKey(), entry.getValue() }, entry.getKey());
        }
        layout.addComponent(tblNotDeleted);

        Button close = new Button("Close", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });

        layout.addComponent(close);

        subwindow.setWidth("600px");
        subwindow.addComponent(layout);
        router.getMainWindow().addWindow(subwindow);

    }

}
