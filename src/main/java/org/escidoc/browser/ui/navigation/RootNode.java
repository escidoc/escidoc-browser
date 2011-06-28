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
package org.escidoc.browser.ui.navigation;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.ViewConstants;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Tree;

@SuppressWarnings("serial")
public class RootNode extends CustomComponent {

    private final class RootNodeActionHandler implements Handler {
        @Override
        public void handleAction(final Action action, final Object sender, final Object target) {
            getApplication().getMainWindow().showNotification("Adding a new context is not yet implemented");
        }

        @Override
        public Action[] getActions(final Object target, final Object sender) {
            return new Action[] { new Action(ViewConstants.ADD_CONTEXT) };
        }
    }

    private final Tree tree = new Tree();

    public RootNode(final EscidocServiceLocation serviceLocation, final MainSite mainSite) {
        setCompositionRoot(tree);
        tree.setSelectable(false);
        tree.addItem(serviceLocation.getEscidocUri());
        tree.setChildrenAllowed(serviceLocation.getEscidocUri(), false);
        if (mainSite.isUserAllowedToCreateContext()) {
            addContextMenu();
        }
    }

    private void addContextMenu() {
        final Handler actionHandler = new RootNodeActionHandler();
        tree.addActionHandler(actionHandler);
    }
}
