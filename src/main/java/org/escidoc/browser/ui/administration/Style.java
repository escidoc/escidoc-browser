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
package org.escidoc.browser.ui.administration;

import com.vaadin.ui.Label;
import com.vaadin.ui.themes.Reindeer;

public class Style {

    private Style() {
        // do not init.
    }

    public static class Ruler extends Label {
        private static final long serialVersionUID = -4909196895183387829L;

        public Ruler() {
            super("<hr />", Label.CONTENT_XHTML);
        }
    }

    public static class H1 extends Label {
        private static final long serialVersionUID = -2843233317747887008L;

        public H1(final String caption) {
            super(caption);
            setSizeUndefined();
            setStyleName(Reindeer.LABEL_H1);
        }
    }

    public static class H2 extends Label {
        private static final long serialVersionUID = 1210257960304559971L;

        public H2(final String caption) {
            super(caption);
            setSizeUndefined();
            setStyleName(Reindeer.LABEL_H2);
        }
    }

}
