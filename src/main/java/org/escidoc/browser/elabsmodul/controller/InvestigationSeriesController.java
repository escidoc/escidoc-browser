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

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.exceptions.EscidocBrowserException;
import org.escidoc.browser.elabsmodul.interfaces.IBeanModel;
import org.escidoc.browser.elabsmodul.interfaces.ISaveAction;
import org.escidoc.browser.elabsmodul.model.InvestigationSeriesBean;
import org.escidoc.browser.elabsmodul.views.InvestigationSeriesView;
import org.escidoc.browser.elabsmodul.views.YesNoDialog;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.repository.internal.ContainerRepository;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.helper.ResourceHierarchy;
import org.escidoc.browser.util.StringUtils;
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

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.container.Container;

public class InvestigationSeriesController extends Controller implements ISaveAction {

    private static final Logger LOG = LoggerFactory.getLogger(InvestigationSeriesController.class);

    private static final String ESCIDOC = "escidoc";

    private final EscidocServiceLocation serviceLocation;

    private final Repositories repositories;

    private final Router router;

    private final ContainerProxy resourceProxy;

    private final Window mainWindow;

    private InvestigationSeriesBean investigationSeriesBean;

    private IBeanModel beanModel;

    private final Object LOCK = new Object() {
    };

    public InvestigationSeriesController(Repositories repositories, Router router, ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(router, "mainSite is null: %s", router);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkArgument(resourceProxy instanceof ContainerProxy, "resourceProxy is not a Containerproxy");
        this.router = router;
        this.mainWindow = router.getMainWindow();
        this.serviceLocation = router.getServiceLocation();
        this.repositories = repositories;
        this.resourceProxy = (ContainerProxy) resourceProxy;
        setResourceName(resourceProxy.getName() + "#" + resourceProxy.getId());
        resourceToBean();
        this.view = createView();
    }

    @Override
    public void saveAction(IBeanModel beanModel) {
        Preconditions.checkNotNull(beanModel, "DataBean to store is NULL");
        this.beanModel = beanModel;
        this.mainWindow.addWindow(new YesNoDialog(ELabsViewContants.DIALOG_SAVE_INVESTIGATION_SERIES_HEADER,
            ELabsViewContants.DIALOG_SAVE_INVESTIGATION_SERIES_TEXT, new YesNoDialog.Callback() {
                @Override
                public void onDialogResult(boolean resultIsYes) {
                    if (resultIsYes) {
                        saveModel();
                    }
                    ((InvestigationSeriesView) InvestigationSeriesController.this.view).hideButtonLayout();
                }
            }));
    }

    /**
     * 
     * @throws EscidocClientException
     */
    private void saveModel() {
        synchronized (LOCK) {
            Preconditions.checkNotNull(this.beanModel, "DataBean to store is NULL");
            ContainerRepository containerRepositories = repositories.container();
            try {
                validateBean(this.beanModel);
            }
            catch (EscidocBrowserException e) {
                LOG.error(e.getMessage());
                return;
            }

            try {
                Container container = containerRepositories.findContainerById(this.resourceProxy.getId());
                MetadataRecord metadataRecord = container.getMetadataRecords().get(ESCIDOC);
                metadataRecord.setContent(beanToDom(this.investigationSeriesBean));
                containerRepositories.update(container);
            }
            catch (EscidocClientException e) {
                LOG.error(e.getLocalizedMessage());
                showError(e.getLocalizedMessage());
            }
            finally {
                this.beanModel = null;
            }
            LOG.info("InvestigationSeries is successfully saved.");
            showTrayMessage("Success", "Investigation series is saved.");
        }
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

            final Element instrument =
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

    private void resourceToBean() {
        final NodeList nodeList = resourceProxy.getMedataRecords().get(ESCIDOC).getContent().getChildNodes();

        this.investigationSeriesBean = new InvestigationSeriesBean();
        for (int i = 0; i < nodeList.getLength(); i++) {

            final Node node = nodeList.item(i);
            if ("title".equals(node.getLocalName()) && AppConstants.DC_NAMESPACE.equals(node.getNamespaceURI())) {
                this.investigationSeriesBean.setName((node.getFirstChild() != null) ? node
                    .getFirstChild().getNodeValue() : null);
            }
            else if ("description".equals(node.getLocalName())
                && AppConstants.DC_NAMESPACE.equals(node.getNamespaceURI())) {
                this.investigationSeriesBean.setDescription((node.getFirstChild() != null) ? node
                    .getFirstChild().getNodeValue() : null);
            }
        }
    }

    private Component createView() {
        return new InvestigationSeriesView(this.resourceProxy, this.investigationSeriesBean, createBreadCrumbModel(),
            this, this.router);
    }

    private List<ResourceModel> createBreadCrumbModel() {
        final ResourceHierarchy rs = new ResourceHierarchy(this.serviceLocation, this.repositories);
        try {
            List<ResourceModel> hierarchy = rs.getHierarchy(this.resourceProxy);
            Collections.reverse(hierarchy);
            hierarchy.add(this.resourceProxy);
            return hierarchy;
        }
        catch (EscidocClientException e) {
            LOG.error("Fatal error, could not load BreadCrumb " + e.getLocalizedMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean hasUpdateAccess() {
        try {
            return this.repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_CONTAINER).forResource(resourceProxy.getId())
                .permitted();
        }
        catch (UnsupportedOperationException e) {
            showError("Internal error");
            LOG.error(e.getMessage());
            return false;
        }
        catch (EscidocClientException e) {
            showError("Internal error");
            LOG.error(e.getMessage());
            return false;
        }
        catch (URISyntaxException e) {
            showError("Internal error");
            LOG.error(e.getMessage());
            return false;
        }
    }

    protected void validateBean(IBeanModel beanModel) throws EscidocBrowserException {
        Preconditions.checkNotNull(beanModel, "Input is null");
        InvestigationSeriesBean investigationSeriesBean = null;
        try {
            investigationSeriesBean = (InvestigationSeriesBean) beanModel;
        }
        catch (ClassCastException e) {
            showError("Internal error");
            throw new EscidocBrowserException("Wrong type of model", e);
        }

        if (StringUtils.isEmpty(investigationSeriesBean.getName())
            || StringUtils.isEmpty(investigationSeriesBean.getDescription())) {
            showError("Please fill out all of the requried fields!");
            throw new EscidocBrowserException("Some required field is null");
        }
    }
}
