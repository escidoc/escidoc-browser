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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import org.escidoc.browser.controller.ItemController;
import org.escidoc.browser.ui.Router;

public class ChangeComponentCategoryTypeHelper {
    private Window subwindow;

    private final Router router;

    private final String categoryType;

    private final ItemController controller;

    private final String itemId;

    private final String componentId;

    public ChangeComponentCategoryTypeHelper(Router router, String categoryType, String componentId,
        ItemController controller, String itemId) {
        this.router = router;
        this.categoryType = categoryType;
        this.componentId = componentId;

        this.controller = controller;
        this.itemId = itemId;

    }

    public void showWindow() {

        subwindow = new Window("Change Category Type");
        subwindow.setModal(true);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);

        final TextField txtField = new TextField("Change Category Type");
        txtField.setValue(categoryType);
        subwindow.addComponent(txtField);

        Button close = new Button("Close", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        Button save = new Button("Save", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                String newCatType = txtField.getValue().toString();
                controller.updateComponentCategory(componentId, newCatType, itemId);
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(save);
        hl.addComponent(close);

        layout.addComponent(hl);
        subwindow.setWidth("350px");
        subwindow.addComponent(layout);
        router.getMainWindow().addWindow(subwindow);

    }

}
