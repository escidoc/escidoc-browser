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
package org.escidoc.browser.ui.view.helpers;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Link;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

public class CreatePermanentLinkVH {

    private static final Logger LOG = LoggerFactory.getLogger(CreatePermanentLinkVH.class);

    public CreatePermanentLinkVH(String url, String id, String type, AbstractComponentContainer componentContainer,
        EscidocServiceLocation serviceLocation) {
        Link l;
        try {
            l =
                new Link("", new ExternalResource(url + "?id=" + id + "&type=" + type + "&escidocurl="
                    + serviceLocation.getEscidocUrl()));
            l.setDescription(ViewConstants.PERMANENT_LINK);
            l.setIcon(new ThemeResource("images/assets/link.png"));
            l.setStyleName("permanentLink");
            componentContainer.addComponent(l);
        }
        catch (MalformedURLException e) {
            componentContainer.getWindow().showNotification(ViewConstants.COULD_NOT_RETRIEVE_APPLICATION_URL);
            LOG.error(ViewConstants.COULD_NOT_RETRIEVE_APPLICATION_URL + e.getLocalizedMessage());
        }
    }

}
