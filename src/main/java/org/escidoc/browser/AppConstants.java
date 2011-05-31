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
 * Copyright ${year} Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser;

public final class AppConstants {

    // public static final String HARDCODED_ESCIDOC_URI =
    // "http://escidev6:8080"
    // "http://zbmed.fiz-karlsruhe.de/"
    // "http://escidev4:8080";

    public static final String ESCIDOC_LOGIN_PATH_AND_PARAMETER = "/aa/login?target=";

    private AppConstants() {
        // not to be initialized
    }

    public static final String LOGIN_TARGET = "/aa/login?target=";

    public static final String LOGOUT_TARGET = "/aa/logout?target=";

    public static final String ESCIDOC_URL = "escidocurl";

    public static final String ESCIDOC_USER_HANDLE = "eSciDocUserHandle";

    public static final String EMPTY_STRING = "";

    public static final String ARG_TAB = "tab";

    public static final String ARG_TYPE = "type";

}
