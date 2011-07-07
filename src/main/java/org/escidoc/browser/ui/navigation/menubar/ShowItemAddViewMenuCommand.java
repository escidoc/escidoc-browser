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
package org.escidoc.browser.ui.navigation.menubar;

import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.repository.Repositories;

import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class ShowItemAddViewMenuCommand implements Command {

    public ShowItemAddViewMenuCommand(final Repositories repositories, final Window mainWindow,
        final String contextIdForContainer, final TreeDataSource treeDataSource) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void menuSelected(final MenuItem selectedItem) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");

    }

}
