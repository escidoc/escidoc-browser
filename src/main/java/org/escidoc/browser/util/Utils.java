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
package org.escidoc.browser.util;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.text.DecimalFormat;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.types.NonNegativeInteger;
import org.escidoc.browser.AppConstants;
import org.w3c.dom.Document;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

public final class Utils {

    private Utils() {
        // Utility class
    }

    public static Document createNewDocument() throws ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }

    public static String readableFileSize(final long size) {
        if (size <= 0) {
            return "0";
        }

        final int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " "
            + new String[] { "B", "KB", "MB", "GB", "TB" }[digitGroups];
    }

    public static void copy(final InputStream in, final OutputStream out) throws IOException {
        final byte[] buffer = new byte[1024];
        while (true) {
            final int readCount = in.read(buffer);
            if (readCount < 0) {
                break;
            }
            out.write(buffer, 0, readCount);
        }
    }

    public static Proxy createHttpProxy(final URI uri) {
        Preconditions.checkNotNull(uri, "uri is null: %s", uri);
        return new Proxy(Type.HTTP, findHttpProxy(uri));
    }

    public static SocketAddress findHttpProxy(final URI uri) {
        for (final Proxy proxy : ProxySelector.getDefault().select(uri)) {
            if (proxy.address() == null) {
                return Proxy.NO_PROXY.address();
            }
            return proxy.address();
        }
        return Proxy.NO_PROXY.address();
    }

    public final static SearchRetrieveRequestType createEmptyFilter() {
        final SearchRetrieveRequestType srrt = new SearchRetrieveRequestType();
        srrt.setMaximumRecords(new NonNegativeInteger(AppConstants.MAX_RESULT_SIZE));
        return srrt;
    }

    public static Component createSpace() {
        return new Label("<br/>", Label.CONTENT_XHTML);
    }
}
