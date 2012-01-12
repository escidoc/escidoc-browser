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
package org.escidoc.browser.ui.mainpage;

import org.escidoc.browser.model.EscidocServiceLocation;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("serial")
public class Footer {

    private static final String ADMIN_TOOL = "Admin Tool";

    private static final String CHANGE = "Switch Instance of eSciDoc";

    private HorizontalLayout footerLayout;

    public Footer(HorizontalLayout footerLayout, final EscidocServiceLocation serviceLocation) {
        this.footerLayout = footerLayout;
        buildView(serviceLocation);
    }

    private void buildView(final EscidocServiceLocation serviceLocation) {
        System.out.println(serviceLocation.getEscidocUri().toString());
        Label lblBaseUrl =
            new Label(" eSciDoc Browser 0.3.9 on " + serviceLocation.getEscidocUri().toString() + "", Label.CONTENT_RAW);
        footerLayout.addComponent(lblBaseUrl);
        footerLayout.setExpandRatio(lblBaseUrl, 1f);

        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("200px");
        hl.setStyleName("floatright");

        hl.setMargin(false);

        final Button btnChange = new Button(CHANGE, new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                footerLayout.getApplication().close();
            }
        });
        btnChange.setStyleName(Reindeer.BUTTON_LINK);
        hl.addComponent(btnChange);
        hl.setExpandRatio(btnChange, 0.5f);

        final Button btnAdminTl = new Button(ADMIN_TOOL, new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                footerLayout.getWindow().open(new ExternalResource(serviceLocation.getEscidocUri() + "/AdminTool"),
                    "_self");
            }
        });
        btnAdminTl.setStyleName(Reindeer.BUTTON_LINK);
        hl.addComponent(btnAdminTl);
        hl.setExpandRatio(btnAdminTl, 0.5f);

        footerLayout.addComponent(hl);
        // footerLayout.setExpandRatio(hl, 0.1f);

    }
}