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
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

import org.escidoc.browser.repository.AdminRepository;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.tools.Style.H2;
import org.escidoc.browser.ui.tools.Style.Ruler;

@SuppressWarnings("serial")
public class ReindexView extends VerticalLayout {

    private final FormLayout formLayout = new FormLayout();

    private final Button reindexResourceBtn = new Button(ViewConstants.REINDEX);

    private final CheckBox clearIndexBox = new CheckBox(ViewConstants.CLEAR_INDEX);

    private final ComboBox indexNameSelect = new ComboBox(ViewConstants.INDEX_NAME, IndexName.all());

    private final ProgressIndicator progressIndicator = new ProgressIndicator(new Float(0f));

    private final Router router;

    private final AdminRepository adminRepository;

    public ReindexView(final Router router, final AdminRepository adminRepository) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(adminRepository, "adminRepository is null: %s", adminRepository);
        this.router = router;
        this.adminRepository = adminRepository;
    }

    public void init() {
        addHeader();
        addRuler();
        addDescription();

        addComponent(formLayout);
        setMargin(true);

        addIndexNameSelection();
        addClearIndexBox();
        addReindexButton();

        buildProgressIndicator();
        addReindexButtonListener();

        addComponent(formLayout);
    }

    private void addDescription() {
        final Label text = new Label("<p>" + ViewConstants.REINDEX_TEXT + "</p>", Label.CONTENT_XHTML);
        addComponent(text);
    }

    private void addRuler() {
        addComponent(new Ruler());
    }

    private void addHeader() {
        final Label text = new H2(ViewConstants.REINDEX);
        text.setContentMode(Label.CONTENT_XHTML);
        addComponent(text);
    }

    private void addClearIndexBox() {
        setDefaultAsTrue();
        formLayout.addComponent(clearIndexBox);
    }

    private void addIndexNameSelection() {
        indexNameSelect.setNullSelectionAllowed(false);

        // TODO replace hardcoded IndexName with
        // adminRepository.getIndexConfiguration(
        indexNameSelect.select(IndexName.REINDEX_ALL.asInternalName());
        formLayout.addComponent(indexNameSelect);
    }

    private void setDefaultAsTrue() {
        clearIndexBox.setValue(Boolean.TRUE);
    }

    private void addReindexButton() {
        reindexResourceBtn.setStyleName(Reindeer.BUTTON_SMALL);
        formLayout.addComponent(reindexResourceBtn);
    }

    private void addReindexButtonListener() {
        reindexResourceBtn.addListener(new ReindexListener(router.getApp(), clearIndexBox, indexNameSelect,
            reindexResourceBtn, progressIndicator, this, adminRepository));
    }

    private void buildProgressIndicator() {
        progressIndicator.setImmediate(true);
        progressIndicator.setEnabled(false);
        progressIndicator.setVisible(false);
        addComponent(progressIndicator);
    }
}
