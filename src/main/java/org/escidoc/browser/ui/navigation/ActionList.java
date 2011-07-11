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

import org.escidoc.browser.ui.ViewConstants;

import com.vaadin.event.Action;

public class ActionList {

    static final Action ACTION_ADD_CONTAINER = new Action(ViewConstants.ADD_CONTAINER);

    static final Action ACTION_ADD_ITEM = new Action(ViewConstants.ADD_ITEM);

    static final Action ACTION_DELETE_ITEM = new Action(ViewConstants.DELETE_RESOURCE);

    static final Action ACTION_DELETE_CONTAINER = new Action(ViewConstants.DELETE_CONTAINER);

    static final Action[] ACTIONS_CONTAINER = new Action[] { ACTION_ADD_CONTAINER, ACTION_ADD_ITEM,
        ACTION_DELETE_CONTAINER };

}
