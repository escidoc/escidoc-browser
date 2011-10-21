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
package org.escidoc.browser.elabsmodul.controller;

import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.interfaces.IBeanModel;
import org.escidoc.browser.elabsmodul.interfaces.ISaveAction;
import org.escidoc.browser.elabsmodul.model.InvestigationSeriesBean;
import org.escidoc.browser.elabsmodul.views.InvestigationSeriesView;
import org.escidoc.browser.elabsmodul.views.YesNoDialog;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.helper.ResourceHierarchy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.container.Container;

public class InvestigationSeriesController extends Controller implements ISaveAction {
    private static final Logger LOG = LoggerFactory.getLogger(InvestigationSeriesController.class);

    private EscidocServiceLocation serviceLocation;

    private Repositories repositories;

    private Router router;

    private ContainerProxy resourceProxy;

    private Window mainWindow;

    private CurrentUser currentUser;

    private InvestigationSeriesBean isb;

    @Override
    public void saveAction(IBeanModel beanModel) {
        Preconditions.checkNotNull(beanModel, "DataBean to store is NULL");

        mainWindow.addWindow(new YesNoDialog(ELabsViewContants.DIALOG_SAVEINSTRUMENT_HEADER,
            ELabsViewContants.DIALOG_SAVEINSTRUMENT_TEXT, new YesNoDialog.Callback() {

                @Override
                public void onDialogResult(boolean resultIsYes) {
                    if (resultIsYes) {
                        try {
                            saveModel();
                        }
                        catch (EscidocClientException e) {
                            LOG.error(e.getMessage());
                            mainWindow.showNotification("Error", e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
                        }
                    }
                    else {
                        ((InvestigationSeriesView) InvestigationSeriesController.this.view).hideButtonLayout();
                    }
                }

            }));

    }

    private void saveModel() throws EscidocClientException {
        Container container = repositories.container().findContainerById(resourceProxy.getId());
        MetadataRecord metadataRecord = container.getMetadataRecords().get("escidoc");
        metadataRecord.setContent(beanToDom(isb));
        repositories.container().update(container);
    }

    private Element beanToDom(InvestigationSeriesBean isb) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setValidating(true);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            final Document doc = builder.newDocument();

            Element instrument =
                doc.createElementNS("http://escidoc.org/ontologies/bw-elabs/re#", "InvestigationSeries");
            instrument.setPrefix("el");

            final Element title = doc.createElementNS("http://purl.org/dc/elements/1.1/", "title");
            title.setPrefix("dc");
            title.setTextContent(isb.getName());
            instrument.appendChild(title);

            final Element description = doc.createElementNS("http://purl.org/dc/elements/1.1/", "description");
            description.setPrefix("dc");
            description.setTextContent(isb.getDescription());
            instrument.appendChild(description);
            return instrument;

        }
        catch (DOMException e) {
            LOG.error(e.getLocalizedMessage());
        }
        catch (ParserConfigurationException e) {
            LOG.error(e.getLocalizedMessage());
        }

        return null;

    }

    @Override
    public void init(
        EscidocServiceLocation serviceLocation, Repositories repositories, Router router, ResourceProxy resourceProxy,
        Window mainWindow, CurrentUser currentUser) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(router, "mainSite is null: %s", router);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);
        Preconditions.checkArgument(resourceProxy instanceof ContainerProxy, "resourceProxy is not container proxy");

        this.serviceLocation = serviceLocation;
        this.repositories = repositories;
        this.router = router;
        this.resourceProxy = (ContainerProxy) resourceProxy;
        this.mainWindow = mainWindow;
        this.currentUser = currentUser;

        // FIXME a little bit weird
        getResourceName(resourceProxy.getName());

        isb = resourceToBean();
        view = createView();
    }

    private InvestigationSeriesBean resourceToBean() {
        InvestigationSeriesBean isb = new InvestigationSeriesBean();

        final NodeList nodeList = resourceProxy.getMedataRecords().get("escidoc").getContent().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {

            final Node node = nodeList.item(i);
            final String nodeName = node.getNodeName();

            if (nodeName.equals("dc:title")) {
                isb.setName((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
            }

            else if (nodeName.equals("dc:description")) {
                isb.setDescription((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
            }
        }

        return isb;
    }

    private Component createView() {
        return new InvestigationSeriesView(resourceProxy, isb, createBreadCrumbModel(), this, router);
    }

    private List<ResourceModel> createBreadCrumbModel() {
        final ResourceHierarchy rs = new ResourceHierarchy(serviceLocation, repositories);
        try {
            List<ResourceModel> hierarchy = rs.getHierarchy(resourceProxy);
            Collections.reverse(hierarchy);
            hierarchy.add(resourceProxy);
            return hierarchy;
        }
        catch (EscidocClientException e) {
            LOG.error("Fatal error, could not load BreadCrumb " + e.getLocalizedMessage());
        }
        return Collections.emptyList();
    }
}