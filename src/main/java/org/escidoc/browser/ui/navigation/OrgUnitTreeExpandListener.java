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
package org.escidoc.browser.ui.navigation;

import com.google.common.base.Preconditions;

import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;
import com.vaadin.ui.Window;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.repository.internal.OrganizationUnitRepository;
import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class OrgUnitTreeExpandListener implements ExpandListener {

    private static final Logger LOG = LoggerFactory.getLogger(OrgUnitTreeExpandListener.class);

    private final OrganizationUnitRepository repository;

    private final Window mainWindow;

    private final TreeDataSource dataSource;

    public OrgUnitTreeExpandListener(final OrganizationUnitRepository repository, final Window mainWindow,
        final TreeDataSource dataSource) {
        Preconditions.checkNotNull(repository, "repository is null: %s", repository);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(dataSource, "dataSource is null: %s", dataSource);

        this.repository = repository;
        this.mainWindow = mainWindow;
        this.dataSource = dataSource;
    }

    @Override
    public void nodeExpand(final ExpandEvent event) {
        Preconditions.checkNotNull(event, "event is null: %s", event);
        if (event.getItemId() instanceof ResourceModel) {
            addChildren(getSelected(event), fetchChildren(getSelected(event)));
        }
    }

    private void addChildren(final ResourceModel parent, final List<ResourceModel> children) {
        dataSource.addChildren(parent, children);
    }

    private static ResourceModel getSelected(final ExpandEvent event) {
        return (ResourceModel) event.getItemId();
    }

    private List<ResourceModel> fetchChildren(final ResourceModel rm) {
        try {
            return repository.findTopLevelMembersById(rm.getId());
        }
        catch (final EscidocClientException e) {
            final String msg = "Can not fetch children of " + rm + ". Reason: " + e.getMessage();
            LOG.error(msg, e);
            mainWindow.showNotification(ViewConstants.ERROR, msg, Window.Notification.TYPE_ERROR_MESSAGE);
        }
        return Collections.emptyList();
    }
}