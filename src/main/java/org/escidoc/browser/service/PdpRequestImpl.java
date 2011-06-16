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
package org.escidoc.browser.service;

import java.net.URISyntaxException;

import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.aa.useraccount.UserAccount;

public final class PdpRequestImpl implements PdpRequest {

    private static final Logger LOG = LoggerFactory.getLogger(PdpRequestImpl.class);

    private final UserAccount currentUser;

    private final PdpService service;

    public PdpRequestImpl(final PdpService service, final UserAccount currentUser) {
        Preconditions.checkNotNull(service, "service is null: %s", service);
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);
        this.service = service;
        this.currentUser = currentUser;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.admintool.services.PdpRequest#isAllowed()
     */
    @Override
    public boolean isPermitted(final String actionId) {
        return evaluatePdpRequest(actionId, ViewConstants.EMPTY_STRING);
    }

    @Override
    public boolean isPermitted(final String actionId, final String resourceId) {
        return evaluatePdpRequest(actionId, resourceId);
    }

    private boolean evaluatePdpRequest(final String actionId, final String resourceId) {
        try {
            return service.isAction(actionId).forUser(currentUser.getObjid()).forResource(resourceId).permitted();
        }
        catch (final URISyntaxException e) {
            LOG.error(e.getMessage());
        }
        catch (final EscidocClientException e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean isDenied(final String actionId, final String selectedItemId) {
        return !isPermitted(actionId, selectedItemId);
    }

    @Override
    public boolean isDenied(final String actionId) {
        return !isPermitted(actionId);
    }
}
