/**
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License, Version 1.0 only (the "License"). You may not use
 * this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * https://www.escidoc.org/license/ESCIDOC.LICENSE . See the License for the
 * specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and
 * include the License file at license/ESCIDOC.LICENSE. If applicable, add the
 * following below this CDDL HEADER, with the fields enclosed by brackets "[]"
 * replaced with your own identifying information: Portions Copyright [yyyy]
 * [name of copyright owner]
 * 
 * CDDL HEADER END
 * 
 * 
 * 
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur
 * Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to
 * license terms.
 */
package org.escidoc.browser.ui.dnd;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractWebApplicationContext;

public class WebBrowserUtil {

    private WebBrowserUtil() {
        // util class
    }

    static boolean isNewerFirefox(final Application app) {
        return isFirefox(app) && (isFirefoxVersionFour(app) || isFirefoxVersionThreePointSix(app));
    }

    static boolean isHtml5FileDropNotSupported(final boolean isFirefox, final Application app) {
        return !(isFirefox || isChromeOrSafari(app));
    }

    static boolean isChromeOrSafari(final Application app) {
        return isGoogleChrome(app) || isSafariVersionFour(app);
    }

    static boolean isSafariVersionFour(final Application app) {
        final AbstractWebApplicationContext context = (AbstractWebApplicationContext) app.getContext();
        return context.getBrowser().isSafari() && context.getBrowser().getBrowserMajorVersion() > 4;
    }

    static boolean isGoogleChrome(final Application app) {
        final AbstractWebApplicationContext context = (AbstractWebApplicationContext) app.getContext();
        return context.getBrowser().isChrome();
    }

    static boolean isFirefoxVersionFour(final Application app) {
        final AbstractWebApplicationContext context = (AbstractWebApplicationContext) app.getContext();
        return (context).getBrowser().getBrowserMajorVersion() >= 4;
    }

    static boolean isFirefoxVersionThreePointSix(final Application app) {

        final AbstractWebApplicationContext context = (AbstractWebApplicationContext) app.getContext();
        return ((context).getBrowser().getBrowserMajorVersion() == 3 && (context).getBrowser().getBrowserMinorVersion() >= 6);
    }

    static boolean isFirefox(final Application app) {
        final AbstractWebApplicationContext context = (AbstractWebApplicationContext) app.getContext();
        return (context).getBrowser().isFirefox();
    }
}