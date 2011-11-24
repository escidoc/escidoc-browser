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
package org.escidoc.browser.elabsmodul.cache;

import java.util.Collections;
import java.util.List;

import org.escidoc.browser.elabsmodul.model.OrgUnitBean;
import org.escidoc.browser.elabsmodul.model.UserBean;

public class ELabsCache {

    private static List<String> depositEndpoints = Collections.emptyList();

    private static List<String> esyncEndpoints;

    private static List<UserBean> users = Collections.emptyList();

    private static List<OrgUnitBean> orgUnits = Collections.emptyList();

    /**
     * @return the depositEndpoints
     */
    public static List<String> getDepositEndpoints() {
        return depositEndpoints;
    }

    /**
     * @param depositEndpoints
     *            the depositEndpoints to set
     */
    public static void setDepositEndpoints(List<String> depositEndpoints) {
        ELabsCache.depositEndpoints = depositEndpoints;
    }

    /**
     * @return the esyncEndpoints
     */
    public static List<String> getEsyncEndpoints() {
        return esyncEndpoints;
    }

    /**
     * @param esyncEndpoints
     *            the esyncEndpoints to set
     */
    public static void setEsyncEndpoints(List<String> esyncEndpoints) {
        ELabsCache.esyncEndpoints = esyncEndpoints;
    }

    /**
     * @return the users
     */
    public static List<UserBean> getUsers() {
        return users;
    }

    /**
     * @param users
     *            the users to set
     */
    public static void setUsers(List<UserBean> users) {
        ELabsCache.users = users;
    }

    /**
     * @return the orgUnits
     */
    public static List<OrgUnitBean> getOrgUnits() {
        return orgUnits;
    }

    /**
     * @param orgUnits the orgUnits to set
     */
    public static void setOrgUnits(List<OrgUnitBean> orgUnits) {
        ELabsCache.orgUnits = orgUnits;
    }
}
