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
package org.escidoc.browser;

public final class AppConstants {

    public static final String SYSADMIN = "sysadmin";

    private AppConstants() {
        // not to be initialized
    }

    public static final String MIMETYPE_ICON_LOCATION = "src/main/webapp/VAADIN/themes/myTheme/images/filetypes/";

    public static final String PENDING = "pending";

    public static final String UNLOCK = "unlock";

    public static final String IN_REVISION = "in-revision";

    public static final String ESCIDOC_LOGIN_PATH_AND_PARAMETER = "/aa/login?target=";

    public static final String LOGIN_TARGET = "/aa/login?target=";

    public static final String LOGOUT_TARGET = "/aa/logout?target=";

    public static final String ESCIDOC_URL = "escidocurl";

    public static final String ESCIDOC_USER_HANDLE = "eSciDocUserHandle";

    public static final String EMPTY_STRING = "";

    public static final String ARG_TAB = "id";

    public static final String ARG_TYPE = "type";

    public static final String XACML_ACTION_ID = "urn:oasis:names:tc:xacml:1.0:action:action-id";

    public static final String SUBJECT_ID = "urn:oasis:names:tc:xacml:1.0:subject:subject-id";

    public static final String RESOURCE_ID = "urn:oasis:names:tc:xacml:1.0:resource:resource-id";

    public static final String SMALL_BUTTON = "small";

    public static final String ESCIDOC_LOGO = "images/SchriftLogo.jpg";

    public static final String NOT_SUPPORTED_BROWSERS =
        "File drop is only supported on Firefox 3.6 and later. Text can be dropped into the box on other browsers.";

    public static final String ESCIDOC = "escidoc";

    public static final String DC_NAMESPACE = "http://purl.org/dc/elements/1.1/";

    public static final String COOKIE_NAME = "eSciDocUserHandle";

    public static final String COOKIE_PATH = "/browser";

    public static final String ESCIDOC_METADATA_TERMS_NS = "http://purl.org/escidoc/metadata/terms/0.1/";

    public static final int TWO_HOURS = 7200;

    public static final String MAX_RESULT_SIZE = "1000";

    public static final String CMM_DESCRIPTION_MATCHER = "org.escidoc.browser.Controller=([^;]*);";

    public static final String CMM_DESCRIPTION_RIG = "org.escidoc.bwelabs.Rig";

    public static final String CMM_DESCRIPTION_STUDY = "org.escidoc.bwelabs.Study";

    public static final String CMM_DESCRIPTION_INSTRUMENT = "org.escidoc.bwelabs.Instrument";

    public static final String CMM_DESCRIPTION_INVESTIGATION = "org.escidoc.bwelabs.Investigation";

    public static final String CMM_DESCRIPTION_INVESTIGATION_RESULTS = "org.escidoc.bwelabs.InvestigationResult";

    public static final String ESCIDOC_DEFAULT_METADATA_NAME = "escidoc";

    public static final String ESCIDOC_ADMIN_ROLE = "escidoc:role-administrator";

    public static final String ESCIDOC_SYSADMIN_ROLE = "escidoc:role-system-administrator";

    public static final Object ESCIDOC_SYS_INSPECTOR_ROLE = "escidoc:role-system-inspector";

    public static final String ESCIDOC_USER_ADMIN_ROLE = "escidoc:role-user-account-administrator";
}