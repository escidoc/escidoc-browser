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
package org.escidoc.browser.ui.listeners;

import com.google.common.base.Preconditions;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import org.escidoc.browser.ui.maincontent.ResourceAddView;

@SuppressWarnings("serial")
public final class AddResourceListener implements Button.ClickListener {

    private final ResourceAddView addView;

    public AddResourceListener(ResourceAddView resourceAddView) {
        Preconditions.checkNotNull(resourceAddView, "resourceAddView is null: %s", resourceAddView);
        addView = resourceAddView;
    }

    @Override
    public void buttonClick(final ClickEvent event) {
        if (addView.validateFields()) {
            addView.createResource();
        }
        else {
            addView.showRequiredMessage();
        }
    }
}