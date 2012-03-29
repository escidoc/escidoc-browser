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

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import org.escidoc.browser.repository.AdminRepository;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.tools.Style.H2;
import org.escidoc.browser.ui.tools.Style.Ruler;

import java.util.Map.Entry;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class RepositoryInfoView extends VerticalLayout {

    private final FormLayout formLayout = new FormLayout();

    private final AdminRepository adminRepository;

    public RepositoryInfoView(final AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public void init() throws EscidocClientException {
        addHeader();
        addRuler();
        setMargin(true);
        for (final Entry<String, String> entry : adminRepository.getRepositoryInfo().entrySet()) {
            formLayout.addComponent(createReadOnlyField(entry));
        }

        addComponent(formLayout);
    }

    private void addRuler() {
        addComponent(new Ruler());
    }

    private void addHeader() {
        final Label text = new H2(ViewConstants.REPOSITORY_INFORMATION);
        text.setContentMode(Label.CONTENT_XHTML);
        addComponent(text);
    }

    private TextField createReadOnlyField(final Entry<String, String> entry) {
        final TextField textField = new TextField();
        textField.setCaption(entry.getKey());
        textField.setValue(entry.getValue());
        textField.setWidth(400, UNITS_PIXELS);
        textField.setReadOnly(true);
        return textField;
    }
}
