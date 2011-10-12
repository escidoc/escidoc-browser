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
package org.escidoc.browser.ui.listeners;

import java.util.Map;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.elabsmodul.constants.ELabsConstants;
import org.escidoc.browser.elabsmodul.view.maincontent.LabsInstrumentView;
import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.ContextModel;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemModel;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.ContainerView;
import org.escidoc.browser.ui.maincontent.ContextView;
import org.escidoc.browser.ui.maincontent.ItemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.application.invalid.InvalidContentModelException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.cmm.ContentModel;

@SuppressWarnings("serial")
public class TreeClickListener implements ItemClickListener {

    private static final Logger LOG = LoggerFactory.getLogger(TreeClickListener.class);

    private final EscidocServiceLocation serviceLocation;

    private final MainSite mainSite;

    private final Window mainWindow;

    private final CurrentUser currentUser;

    private final Repositories repositories;

    public TreeClickListener(final EscidocServiceLocation serviceLocation, final Repositories repositories,
        final Window mainWindow, final MainSite mainSite, final CurrentUser currentUser) {

        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);

        this.repositories = repositories;
        this.mainWindow = mainWindow;
        this.mainSite = mainSite;
        this.serviceLocation = serviceLocation;
        this.currentUser = currentUser;
    }

    @Override
    public void itemClick(final ItemClickEvent event) {
        openClickedResourceInNewTab((ResourceModel) event.getItemId());
    }

    private void openClickedResourceInNewTab(final ResourceModel clickedResource) {
        try {
            // TODO in new architecture, we do not decide based on Context but based on Content Model linked by
            // the Resource.
            if (findContextId(clickedResource).equals(
                org.escidoc.browser.elabsmodul.constants.ELabsConstants.ELABS_DEFAULT_CONTEXT_ID)) {
                openInNewTab(createBWeLabsView(clickedResource), clickedResource);
            }
            else {
                openInNewTab(createView(clickedResource), clickedResource);
            }
        }
        catch (final ContentModelNotFoundException e) {
            LOG.error(e.getMessage());
            // showErrorMessageToUser(clickedResource, e);
        }
        catch (final EscidocClientException e) {
            LOG.error(e.getMessage());
            showErrorMessageToUser(clickedResource, e);
        }
    }

    private Component createBWeLabsView(final ResourceModel clickedResource) throws EscidocClientException,
        ContentModelNotFoundException {
        final String contentModelId = findContentModelId(clickedResource);
        Preconditions.checkNotNull(contentModelId, "ContentModel is null!");
        if (ContextModel.isContext(clickedResource)) {
            return new ContextView(serviceLocation, mainSite,
                tryToFindResource(repositories.context(), clickedResource), mainWindow, currentUser, repositories);
        }
        else if (ContainerModel.isContainer(clickedResource)) {
            if (contentModelId.equals(ELabsConstants.ELABS_DEFAULT_STUDY_CMODEL_ID)) {
                return new ContainerView(serviceLocation, mainSite, tryToFindResource(repositories.container(),
                    clickedResource), mainWindow, currentUser, repositories);
            }
            else if (contentModelId.equals(ELabsConstants.ELABS_DEFAULT_INVESTIGATION_CMODEL_ID)) {
                return new ContainerView(serviceLocation, mainSite, tryToFindResource(repositories.container(),
                    clickedResource), mainWindow, currentUser, repositories);
            }
            else {
                throw new InvalidContentModelException();
            }
        }
        else if (ItemModel.isItem(clickedResource)) {
            if (contentModelId.equals(ELabsConstants.ELABS_DEFAULT_RIG_CMODEL_ID)) {
                return new ItemView(serviceLocation, repositories, mainSite, tryToFindResource(repositories.item(),
                    clickedResource), mainWindow, currentUser);
            }
            else if (contentModelId.equals(ELabsConstants.ELABS_DEFAULT_INSTR_CMODEL_ID)) {
                return new LabsInstrumentView(serviceLocation, repositories, mainSite, tryToFindResource(
                    repositories.item(), clickedResource), mainWindow, currentUser);
            }
            else if (contentModelId.equals(ELabsConstants.ELABS_DEFAULT_GENERATED_ITEM_CMODEL_ID)) {
                return new ItemView(serviceLocation, repositories, mainSite, tryToFindResource(repositories.item(),
                    clickedResource), mainWindow, currentUser);
            }
            else {
                throw new InvalidContentModelException();
            }
        }
        else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    Map<String, String> fooToClassName;

    private Component createView(final ResourceModel clickedResource) throws EscidocClientException {
        if (ContextModel.isContext(clickedResource)) {
            return new ContextView(serviceLocation, mainSite,
                tryToFindResource(repositories.context(), clickedResource), mainWindow, currentUser, repositories);
        }
        else if (ContainerModel.isContainer(clickedResource)) {
            return new ContainerView(serviceLocation, mainSite, tryToFindResource(repositories.container(), clickedResource), mainWindow, currentUser, repositories);
        }
        else if (ItemModel.isItem(clickedResource)) {
            // + we have to load the concrete Container or Item from the eSciDoc Infrastructure.
            final ResourceProxy resourceProxy = tryToFindResource(repositories.container(), clickedResource);

            // + Which Resource to initiate is written somewhere in Content Model attribute. For Example we store it in
            // description. Description=foo.
            final ContentModel contentModel = (ContentModel) resourceProxy.getContentModel();
            final String description = contentModel.getProperties().getDescription();

            // + Precondition: Mapping should loaded from properties file and store as a Map, i.e.
            // Map fooToClassName<String,String>. The content of the property file is in the format foo=bar. Bar is a
            // fully qualified class name to initiate via Reflection.
            final String className = fooToClassName.get(description);

            // + What is the responsibility of this class?
            // a. Convert ResourceProxy to a Bean and then
            // b. Create the View e.g ItemView Or InstrumentView
            return new ItemView(serviceLocation, repositories, mainSite, resourceProxy, mainWindow, currentUser);
        }
        else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    private ResourceProxy tryToFindResource(final Repository repository, final ResourceModel clickedResource)
        throws EscidocClientException {
        return repository.findById(clickedResource.getId());
    }

    private void openInNewTab(final Component component, final ResourceModel clickedResource) {
        mainSite.openTab(component, clickedResource.getName());
    }

    private void showErrorMessageToUser(final ResourceModel hasChildrenResource, final EscidocClientException e) {
        LOG.error("Can not find member of: " + hasChildrenResource.getId(), e);
        mainWindow.showNotification(new Window.Notification(ViewConstants.ERROR, e.getMessage(),
            Notification.TYPE_ERROR_MESSAGE));
    }

    private String findContextId(final ResourceModel clickedResource) {
        if (clickedResource instanceof ContextModel) {
            return ((ContextModel) clickedResource).getId();
        }
        else if (clickedResource instanceof ContainerModel) {
            final ContainerModel containerModel = (ContainerModel) clickedResource;
            try {
                return repositories.container().findById(containerModel.getId()).getContext().getObjid();
            }
            catch (final EscidocClientException e) {
                mainSite.getWindow().showNotification(ViewConstants.NOT_ABLE_TO_RETRIEVE_A_CONTEXT);
            }
        }
        else if (clickedResource instanceof ItemModel) {
            final ItemModel itemModel = (ItemModel) clickedResource;
            try {
                return repositories.item().findById(itemModel.getId()).getContext().getObjid();
            }
            catch (final EscidocClientException e) {
                mainSite.getWindow().showNotification(ViewConstants.NOT_ABLE_TO_RETRIEVE_A_CONTEXT);
            }
        }
        return AppConstants.EMPTY_STRING;
    }

    private String findContentModelId(final ResourceModel clickedResource) throws ContentModelNotFoundException {
        String contentModelId = "escidoc:";
        Resource eSciDocResource = null;
        try {
            if (clickedResource instanceof ContextModel) {
                contentModelId = AppConstants.EMPTY_STRING;
            }
            else if (clickedResource instanceof ContainerModel) {
                eSciDocResource = repositories.container().findById(clickedResource.getId()).getContentModel();
                contentModelId += (eSciDocResource.getXLinkHref().split(":"))[1];
            }
            else if (clickedResource instanceof ItemModel) {
                eSciDocResource = repositories.item().findById(clickedResource.getId()).getContentModel();
                contentModelId += (eSciDocResource.getXLinkHref().split(":"))[1];
            }
            else {
                contentModelId = null;
            }
        }
        catch (final EscidocClientException e) {
            LOG.error("Unable to retreive ContentModel data from repository object", e);
            contentModelId = null;
            throw new ContentModelNotFoundException();
        }
        return contentModelId;
    }
}