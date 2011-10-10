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

import org.escidoc.browser.elabsmodul.constants.ELabViewContants;
import org.escidoc.browser.elabsmodul.constants.ELabsIcons;
import org.escidoc.browser.ui.ViewConstants;

import com.vaadin.event.Action;

public class ActionList {

    // original browser Actions
    static final Action ACTION_ADD_CONTAINER = new Action(ViewConstants.ADD_CONTAINER);

    static final Action ACTION_ADD_ITEM = new Action(ViewConstants.ADD_ITEM);

    static final Action ACTION_DELETE_ITEM = new Action(ViewConstants.DELETE_RESOURCE);

    static final Action ACTION_DELETE_CONTAINER = new Action(ViewConstants.DELETE_CONTAINER);

    // new eLabs Actions
    static final Action ACTION_ADD_STUDY = new Action(ELabViewContants.ADD_STUDY, ELabsIcons.ADD_ELEMENT_ICON);

    static final Action ACTION_MODIFY_STUDY = new Action(ELabViewContants.MODIFY_STUDY, ELabsIcons.MODIFY_ELEMENT_ICON);

    static final Action ACTION_DELETE_STUDY = new Action(ELabViewContants.DELETE_STUDY, ELabsIcons.DELETE_ELEMENT_ICON);

    static final Action ACTION_ADD_INVESTIGATION = new Action(ELabViewContants.ADD_INVESTIGATION,
        ELabsIcons.ADD_ELEMENT_ICON);

    static final Action ACTION_MODIFY_INVESTIGATION = new Action(ELabViewContants.MODIFY_INVESTIGATION,
        ELabsIcons.MODIFY_ELEMENT_ICON);

    static final Action ACTION_DELETE_INVESTIGATION = new Action(ELabViewContants.DELETE_INVESTIGATION,
        ELabsIcons.DELETE_ELEMENT_ICON);

    static final Action ACTION_ADD_RIG = new Action(ELabViewContants.ADD_RIG, ELabsIcons.ADD_ELEMENT_ICON);

    static final Action ACTION_MODIFY_RIG = new Action(ELabViewContants.MODIFY_RIG, ELabsIcons.MODIFY_ELEMENT_ICON);

    static final Action ACTION_DELETE_RIG = new Action(ELabViewContants.DELETE_RIG, ELabsIcons.DELETE_ELEMENT_ICON);

    static final Action ACTION_ADD_INSTRUMENT =
        new Action(ELabViewContants.ADD_INSTRUMENT, ELabsIcons.ADD_ELEMENT_ICON);

    static final Action ACTION_MODIFY_INSTRUMENT = new Action(ELabViewContants.MODIFY_INSTRUMENT,
        ELabsIcons.MODIFY_ELEMENT_ICON);

    static final Action ACTION_DELETE_INSTRUMENT = new Action(ELabViewContants.DELETE_INSTRUMENT,
        ELabsIcons.DELETE_ELEMENT_ICON);
    //

}