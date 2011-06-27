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
package org.escidoc.browser.ui.navigation;

import java.net.URISyntaxException;

import org.escidoc.browser.ActionIdConstants;
import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.ContextModel;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemModel;
import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceContainer;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ContainerProxyImpl;
import org.escidoc.browser.repository.internal.ContainerRepository;
import org.escidoc.browser.repository.internal.ItemRepository;
import org.escidoc.browser.service.PdpService;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.TreeCreateContainer;
import org.escidoc.browser.ui.listeners.TreeCreateItem;

import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandListener;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class NavigationTreeViewImpl extends CustomComponent implements Action.Handler, NavigationTreeView {

    private static final String ADD_ITEM = "Add Item";

    private static final String ADD_CONTAINER = "Add Container";

    private final Tree tree = new Tree();

    // Actions for the context menu
    private static final Action ACTION_ADD_CONTAINER = new Action(ADD_CONTAINER);

    private static final Action ACTION_ADD_ITEM = new Action(ADD_ITEM);

    private static final Action ACTION_DELETE = new Action(ViewConstants.DELETE_RESOURCE);

    private static final Action[] ACTIONSCONTAINER = new Action[] { ACTION_ADD_CONTAINER, ACTION_ADD_ITEM,
        ACTION_DELETE };

    private static final Action ACTION_ADD_COMPONENT = new Action("Add Component");

    private static final Action[] ACTIONSITEM = new Action[] { ACTION_ADD_COMPONENT };

    private final ContainerRepository containerRepo;

    private final ItemRepository itemRepo;

    private final EscidocServiceLocation serviceLocation;

    private ResourceContainer container;

    private final ContainerProxyImpl resourceProxy = null;

    private ContainerModel contModel = null;

    private ItemModel itemModel = null;

    private final PdpService pdpService;

    private final CurrentUser currentUser;

    public NavigationTreeViewImpl(Repositories repositories, EscidocServiceLocation serviceLocation,
        CurrentUser currentUser) {
        this.containerRepo = repositories.container();
        this.itemRepo = repositories.item();
        this.serviceLocation = serviceLocation;
        this.pdpService = repositories.pdp();
        this.currentUser = currentUser;

        setCompositionRoot(tree);
        tree.setImmediate(true);
        tree.addActionHandler(this);
    }

    @Override
    public void addClickListener(final ItemClickListener clickListener) {
        tree.addListener(clickListener);
    }

    @Override
    public void addExpandListener(final ExpandListener expandListener) {
        tree.addListener(expandListener);
    }

    @Override
    public ResourceModel getSelected() {
        return (ResourceModel) tree.getValue();
    }

    @Override
    public void setDataSource(final ResourceContainer container, final MainSite mainSite) {
        this.container = container;
        tree.setContainerDataSource(container.getContainer());
        tree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        tree.setItemCaptionPropertyId(PropertyId.NAME);
        tree.setItemIconPropertyId(PropertyId.ICON);
        tree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
    }

    @Override
    public void handleAction(Action action, Object sender, Object target) {
        String contextId = "";

        if (target instanceof ContextModel) {
            contextId = (((ContextModel) target).getId());
        }
        else if (target instanceof ContainerModel) {
            contModel = (ContainerModel) target;
            try {
                contextId = containerRepo.findById(contModel.getId()).getContext().getObjid();
            }
            catch (final EscidocClientException e) {
                getWindow().showNotification("Not Able to retrieve a context");
            }
        }
        else if (target instanceof ItemModel) {
            itemModel = (ItemModel) target;
            try {
                contextId = itemRepo.findById(itemModel.getId()).getContext().getObjid();
            }
            catch (final EscidocClientException e) {
                getWindow().showNotification("Not Able to retrieve a context");
            }
        }
        if (action == ACTION_ADD_CONTAINER) {
            final TreeCreateContainer tcc =
                new TreeCreateContainer(target, contextId, serviceLocation, getWindow(), containerRepo, container);
            tcc.createContainer();
            resourceProxy.setStruct(contModel.getId());
        }
        else if (action == ACTION_ADD_ITEM) {
            final TreeCreateItem tci =
                new TreeCreateItem(target, contextId, serviceLocation, getWindow(), itemRepo, container);
            tci.createItem();
        }
        else if (action == ACTION_DELETE) {
            getWindow().showNotification("Not implemented yet");
        }
        else if (action == ACTION_ADD_COMPONENT) {
            getWindow().showNotification("Not implemented yet");
        }
    }

    @Override
    public Action[] getActions(Object target, Object sender) {
        try {
            if (pdpService
                .forUser(currentUser.getUserId()).isAction(ActionIdConstants.CREATE_ITEM).forResource("").permitted() == false) {
                return null;
            }
            else {
                if (target instanceof ItemModel) {
                    System.out.println(currentUser.getUserId());
                    return ACTIONSITEM;
                }
                return ACTIONSCONTAINER;
            }
        }
        catch (final EscidocClientException e) {
            getWindow().showNotification(e.getMessage());
        }
        catch (final URISyntaxException e) {
            getWindow().showNotification(e.getMessage());
        }
        return null;
    }
}