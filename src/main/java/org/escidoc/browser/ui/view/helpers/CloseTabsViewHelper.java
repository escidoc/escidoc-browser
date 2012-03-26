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
package org.escidoc.browser.ui.view.helpers;

import com.google.common.base.Preconditions;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.themes.BaseTheme;

import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class CloseTabsViewHelper implements ClickListener {

    private CssLayout layout;

    private static final Logger LOG = LoggerFactory.getLogger(CloseTabsViewHelper.class);

    private static final String FLOAT_RIGHT = "floatright";

    TabSheet ts = null;

    public CloseTabsViewHelper(CssLayout pnlContent, TabSheet mainContentTabs) {
        Preconditions.checkNotNull(pnlContent, "Layout is null");
        Preconditions.checkNotNull(mainContentTabs, "mainContentTabs is null");
        this.ts = mainContentTabs;
        this.layout = pnlContent;
        buildButton();
    }

    private void buildButton() {
        Button btnRemoveTabs = new Button();
        ThemeResource icon = new ThemeResource("images/assets/close.gif");
        btnRemoveTabs.setStyleName(BaseTheme.BUTTON_LINK);
        btnRemoveTabs.addStyleName("closeallTabs");
        btnRemoveTabs.setDescription(ViewConstants.CLOSE_ALL_OPEN_TABS);
        btnRemoveTabs.setIcon(icon);
        btnRemoveTabs.addStyleName(ViewConstants.FLOAT_RIGHT);
        btnRemoveTabs.addListener(this);
        layout.addComponent(btnRemoveTabs);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (ts == null) {
            ts = (TabSheet) getParent(layout);
        }

        for (int i = ts.getComponentCount() - 1; i >= 0; i--) {
            ts.removeTab(ts.getTab(i));
        }
    }

    /**
     * Recursive procedure to find the parent of a Layout that is a TabSheet
     * 
     * @param son
     * @return
     * @throws NullPointerException
     */
    private Component getParent(Component son) throws NullPointerException {
        Component father;
        if (son.getParent().getClass().toString().equals("class com.vaadin.ui.TabSheet")) {
            return son.getParent();
        }
        else {
            father = getParent(son.getParent());
        }
        return father;
    }
}
