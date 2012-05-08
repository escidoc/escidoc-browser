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

import com.google.common.base.Preconditions;

import com.vaadin.ui.Button;
import com.vaadin.ui.OptionGroup;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.internal.ItemBuilder;
import org.escidoc.browser.repository.Repositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.reference.ContentModelRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.om.item.Item;

@SuppressWarnings("serial")
public final class OnCreateItemWithComponents implements Button.ClickListener {

    private final static Logger LOG = LoggerFactory.getLogger(OnCreateItemWithComponents.class);

    private final OptionGroup og;

    private final Repositories repositories;

    OnCreateItemWithComponents(OptionGroup og, Repositories repositories) {
        Preconditions.checkNotNull(og, "og is null: %s", og);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        this.og = og;
        this.repositories = repositories;
    }

    @Override
    public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
        LOG.debug("Creating... " + og.getValue());
        // TODO depends on the selection
        // TODO if creating one item with several components.
        // TODO itemRepository.withComponents(urls);
        // TODO else creating several items with one component each.
        // TODO .foreach( url in urlList)
        // ////create item with component(url)
        String selected = (String) og.getValue();
        if (selected.equals(DropableBox.ONE_ITEM)) {
            createItem();
        }
        else if (selected.equals(DropableBox.SEVERAL_ITEMS)) {
            LOG.debug(selected + " is not yet supported.");
        }
    }

    private void createItem() {
        try {
            Item newItem =
                repositories.item().createWithParent(
                    new ItemBuilder(new ContextRef(getContextId()), new ContentModelRef(getContentModelId()),
                        getMetadata()).build(getResourceName()), getParentId());
        }
        catch (EscidocClientException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private ResourceModel getParentId() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    private String getContentModelId() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    private String getResourceName() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    private String getMetadata() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    private String getContextId() {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }
}