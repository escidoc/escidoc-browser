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
package org.escidoc.browser.controller;

import java.net.URISyntaxException;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.maincontent.ItemView2;

import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ItemController extends Controller {

	public ItemController(final Repositories repositories, final Router router,
			final ResourceProxy resourceProxy) {
		super(repositories, router, resourceProxy);
		createView();
	}

	@Override
	public void createView() {
		try {
			// view = new ItemView(getRouter(), getResourceProxy(), this);
			view = new ItemView2(getRouter(), getResourceProxy(), this);
		} catch (EscidocClientException e) {
			getRouter().getMainWindow().showNotification("Error",
					e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
		}
	}

	/**
	 * Check if the user has access and provide some operations on the view
	 * 
	 * @return boolean
	 */
	public boolean canUpdateItem() {
		try {
			return repositories.pdp().forCurrentUser()
					.isAction(ActionIdConstants.UPDATE_ITEM)
					.forResource(resourceProxy.getId()).permitted();
		} catch (final EscidocClientException e) {
			router.getMainWindow().showNotification(e.getMessage(),
					Window.Notification.TYPE_ERROR_MESSAGE);
			return false;
		} catch (final URISyntaxException e) {
			router.getMainWindow().showNotification(e.getMessage(),
					Window.Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}
}