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

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.Reindeer;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.ui.ViewConstants;

@SuppressWarnings("serial")
public class Footer {

    private HorizontalLayout footerLayout;

    public Footer(HorizontalLayout footerLayout, final EscidocServiceLocation serviceLocation) {
        this.footerLayout = footerLayout;
        buildView(serviceLocation);
    }

    private void buildView(final EscidocServiceLocation serviceLocation) {
        Label lblBaseUrl =
            new Label(ViewConstants.PRODUCT_NAME + ViewConstants.VERSION + " on "
                + serviceLocation.getEscidocUri().toString() + "", Label.CONTENT_RAW);
        footerLayout.addComponent(lblBaseUrl);
        footerLayout.setExpandRatio(lblBaseUrl, 1f);

        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("150px");
        hl.setStyleName("floatright");

        hl.setMargin(false);

        final Button btnChange = new Button(ViewConstants.CHANGE, new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                footerLayout.getApplication().close();
            }
        });
        btnChange.setStyleName(Reindeer.BUTTON_LINK);
        hl.addComponent(btnChange);

        footerLayout.addComponent(hl);
        // footerLayout.setExpandRatio(hl, 0.1f);

    }
}