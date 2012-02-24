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
package org.escidoc.browser.layout;

import com.google.common.base.Preconditions;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;

import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.orgunit.OrgUnitTreeView;
import org.escidoc.browser.ui.orgunit.Reloadable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public final class OnNavigationTabChange implements SelectedTabChangeListener {

    private final static Logger LOG = LoggerFactory.getLogger(OnNavigationTabChange.class);

    @Override
    public void selectedTabChange(final SelectedTabChangeEvent event) {
        Preconditions.checkNotNull(event, "event is null: %s", event);

        final Object source = event.getSource();

        Preconditions.checkNotNull(source, "source is null: %s", source);
        if (!(source instanceof Accordion)) {
            return;
        }

        if (isOrgUniTabSelected(source)) {
            reloadOrgUnitTree(source);
        }
    }

    private static void reloadOrgUnitTree(final Object source) {
        try {
            reloadContent(source);
        }
        catch (final EscidocClientException e) {
            LOG.error("Can not reload data source: " + e.getMessage(), e);
        }
    }

    private static boolean isOrgUniTabSelected(final Object source) {
        return getSelectedTabCaption(source).equalsIgnoreCase(ViewConstants.ORG_UNITS)
            && getTabContent(source) instanceof OrgUnitTreeView;
    }

    private static void reloadContent(final Object source) throws EscidocClientException {
        ((Reloadable) getTabContent(source)).reload();
    }

    private static Component getTabContent(final Object source) {
        return ((Accordion) source).getSelectedTab();
    }

    private static String getSelectedTabCaption(final Object source) {
        return ((Accordion) source).getTab(getTabContent(source)).getCaption();
    }
}