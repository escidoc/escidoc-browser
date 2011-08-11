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
package org.escidoc.browser.ui.maincontent;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtil {

    private static final Logger LOG = LoggerFactory.getLogger(XmlUtil.class);

    private XmlUtil() {
        // utility class
    }

    /**
     * checking if the input String is well formed.
     * 
     * @param xml
     * @return boolean
     */
    public static boolean isWellFormed(final String xml) {
        final boolean isWellFormed = true;
        try {
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
            return isWellFormed;
        }
        catch (final SAXException e) {
            LOG.warn(e.getMessage());
            return !isWellFormed;
        }
        catch (final IOException e) {
            LOG.error(e.getMessage());
            return !isWellFormed;
        }
        catch (final ParserConfigurationException e) {
            LOG.error(e.getMessage());
            return !isWellFormed;
        }
    }
}
