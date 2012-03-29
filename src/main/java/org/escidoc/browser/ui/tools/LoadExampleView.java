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
package org.escidoc.browser.ui.tools;

import com.google.common.base.Preconditions;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

import org.escidoc.browser.repository.AdminRepository;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.tools.Style.H2;
import org.escidoc.browser.ui.tools.Style.Ruler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.adm.LoadExamplesResult.Entry;

@SuppressWarnings("serial")
public class LoadExampleView extends CustomComponent {

    private static final Logger LOG = LoggerFactory.getLogger(LoadExampleView.class);

    private Router router;

    private AdminRepository adminRepository;

    private CssLayout cssLayout = new CssLayout();

    private final Button loadExampleButton = new Button(ViewConstants.LOAD_EXAMPLE);

    public LoadExampleView(final Router router, final AdminRepository adminRepository) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(adminRepository, "adminRepository is null: %s", adminRepository);

        this.router = router;
        this.adminRepository = adminRepository;

        init();
    }

    private void init() {
        setCompositionRoot(cssLayout);
        cssLayout.setMargin(true);

        Label text = new H2(ViewConstants.LOAD_EXAMPLE);
        text.setContentMode(Label.CONTENT_XHTML);
        cssLayout.addComponent(text);

        cssLayout.addComponent(new Ruler());

        text = new Label(ViewConstants.LOAD_EXAMPLE_TEXT, Label.CONTENT_XHTML);
        cssLayout.addComponent(text);

        final HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.setWidth(100, UNITS_PERCENTAGE);
        hLayout.setHeight(100, UNITS_PERCENTAGE);

        loadExampleButton.setWidth(150, UNITS_PIXELS);
        loadExampleButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                try {

                    List<Entry> loadedExamples = adminRepository.loadCommonExamples();

                    for (Entry entry : loadedExamples) {
                        cssLayout.addComponent(new Label(entry.getMessage()));
                    }
                }
                catch (EscidocClientException e) {
                    String msg = "Internal Server Error while loading example set." + e.getMessage();
                    LOG.error(msg);
                    router.getMainWindow().showNotification("Error", msg, Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });

        hLayout.addComponent(loadExampleButton);
        cssLayout.addComponent(hLayout);
    }
}