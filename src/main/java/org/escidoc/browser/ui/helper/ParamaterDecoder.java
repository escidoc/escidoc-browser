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
package org.escidoc.browser.ui.helper;

import org.escidoc.browser.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import biz.source_code.base64Coder.Base64Coder;

public class ParamaterDecoder {

    private static final Logger LOG = LoggerFactory.getLogger(ParamaterDecoder.class);

    public static String parseAndDecodeToken(final Map<String, String[]> parameters) {
        return tryToDecode(findEscidocToken(parameters));
    }

    private static String findEscidocToken(final Map<String, String[]> parameters) {
        final String[] escidocHandeList = parameters.get(AppConstants.ESCIDOC_USER_HANDLE);
        if (escidocHandeList.length > 1) {
            LOG.warn("Found more than one eSciDoc token. The first will be used.");

        }
        return escidocHandeList[0];
    }

    private static String tryToDecode(final String parameter) {
        return Base64Coder.decodeString(parameter);
    }
}
