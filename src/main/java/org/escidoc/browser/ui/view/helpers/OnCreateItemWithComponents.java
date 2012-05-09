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

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.internal.ItemBuilder;
import org.escidoc.browser.repository.Repositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.reference.ContentModelRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.om.item.component.Components;

@SuppressWarnings("serial")
public final class OnCreateItemWithComponents implements Button.ClickListener {

    private final static Logger LOG = LoggerFactory.getLogger(OnCreateItemWithComponents.class);

    private final Repositories repositories;

    private ResourceModel parent;

    private String contentModelId;

    private String itemName;

    private String contextId;

    OnCreateItemWithComponents(Repositories repositories, ItemBuilderHelper helper) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(helper, "helper is null: %s", helper);
        this.repositories = repositories;

        this.itemName = helper.getName();
        this.contentModelId = helper.getContentModelId();
        this.contextId = helper.getContextId();
        this.parent = helper.getParent();
    }

    @Override
    public void buttonClick(@SuppressWarnings("unused") com.vaadin.ui.Button.ClickEvent event) {
        // TODO depends on the selection
        // TODO if creating one item with several components.
        // TODO itemRepository.withComponents(urls);
        // TODO else creating several items with one component each.
        // TODO .foreach( url in urlList)
        // ////create item with component(url)
        createItem();
    }

    private void createItem() {
        try {
            Item build =
                new ItemBuilder(new ContextRef(getContextId()), new ContentModelRef(getContentModelId()), getMetadata())
                    .build(getResourceName());
            // TODO pass the staged file.
            Components componentList = new Components();
            Component component = new Component();
            componentList.add(component);
            build.setComponents(componentList);
            Item newItem = repositories.item().createWithParent(build, getParentId());
        }
        catch (EscidocClientException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private ResourceModel getParentId() {
        return parent;
    }

    private String getContentModelId() {
        return contentModelId;
    }

    private String getResourceName() {
        return itemName;
    }

    // FIXME metadata other than escidoc default metadata is _not_ required.
    private String getMetadata() {
        return AppConstants.EMPTY_STRING;
    }

    private String getContextId() {
        return contextId;
    }
}