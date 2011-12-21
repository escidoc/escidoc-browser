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

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.exceptions.EscidocBrowserException;
import org.escidoc.browser.elabsmodul.interfaces.IBeanModel;
import org.escidoc.browser.elabsmodul.interfaces.ISaveAction;
import org.escidoc.browser.elabsmodul.model.StudyBean;
import org.escidoc.browser.elabsmodul.views.StudyView;
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

public class StudyController extends Controller implements ISaveAction {

    private final EscidocServiceLocation serviceLocation;

    private final ResourceProxy resourceProxy;

    private final Repositories repositories;

    private final Window mainWindow;

    private final Router router;

    private IBeanModel beanModel = null;

    private final Object LOCK = new Object() {
    };

    private static final Logger LOG = LoggerFactory.getLogger(InstrumentController.class);

    public StudyController(Repositories repositories, Router router, ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        this.router = router;
        this.serviceLocation = router.getServiceLocation();
        this.resourceProxy = resourceProxy;
        this.repositories = repositories;
        this.mainWindow = router.getMainWindow();
        this.view = createView(resourceProxy);
        this.setResourceName(resourceProxy.getName() + "#" + resourceProxy.getId());
    }

    @Override
    public void saveAction(IBeanModel beanModel) {
        Preconditions.checkNotNull(beanModel, "DataBean to store is NULL");
        this.beanModel = beanModel;

        mainWindow.addWindow(new YesNoDialog(ELabsViewContants.DIALOG_SAVESTUDY_HEADER,
            ELabsViewContants.DIALOG_SAVESTUDY_TEXT, new YesNoDialog.Callback() {

                @Override
                public void onDialogResult(boolean resultIsYes) {
                    if (resultIsYes) {
                        saveModel();
                    }
                    ((StudyView) StudyController.this.view).hideButtonLayout();
                }
            }));
    }

    /**
     * @throws EscidocClientException
     */
    private void saveModel() {
        synchronized (LOCK) {
            Preconditions.checkNotNull(beanModel, "DataBean to store is NULL");
            ContainerRepository containerRepositories = repositories.container();
            final String ESCIDOC = "escidoc";

            try {
                validateBean(this.beanModel);
            }
            catch (EscidocBrowserException e) {
                LOG.error(e.getMessage());
                return;
            }
            StudyBean studyBean = (StudyBean) beanModel;
            final Element metaDataContent = StudyController.createInstrumentDOMElementByBeanModel(studyBean);

            try {
                Container container = containerRepositories.findContainerById(studyBean.getObjectId());
                MetadataRecord metadataRecord = container.getMetadataRecords().get(ESCIDOC);
                metadataRecord.setContent(metaDataContent);
                containerRepositories.update(container);
            }
            catch (EscidocClientException e) {
                LOG.error(e.getLocalizedMessage());
                showError(e.getLocalizedMessage());
            }
            finally {
                beanModel = null;
            }
            LOG.info("Study is successfully saved.");
            showTrayMessage("Success", "Study is saved.");
        }
    }

    private static Element createInstrumentDOMElementByBeanModel(StudyBean studyBean) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setValidating(true);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            final Document doc = builder.newDocument();

            Element study = doc.createElementNS("http://escidoc.org/ontologies/bw-elabs/re#", "Study");
            study.setPrefix("el");

            final Element title = doc.createElementNS("http://purl.org/dc/elements/1.1/", "title");
            title.setPrefix("dc");
            title.setTextContent(studyBean.getName());
            study.appendChild(title);

            final Element description = doc.createElementNS("http://purl.org/dc/elements/1.1/", "description");
            description.setPrefix("dc");
            description.setTextContent(studyBean.getDescription());
            study.appendChild(description);

            for (String publicationPath : studyBean.getResultingPublication()) {
                if (publicationPath != null && !publicationPath.equals("")) {
                    final Element resultingPublication = doc.createElement("el:resulting-publication");
                    resultingPublication.setAttributeNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:resource",
                        publicationPath);
                    study.appendChild(resultingPublication);
                }
            }

            for (String publicationPath : studyBean.getMotivatingPublication()) {
                if (publicationPath != null && !publicationPath.equals("")) {
                    final Element motivatingPublication = doc.createElement("el:motivating-publication");
                    motivatingPublication.setAttributeNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:resource",
                        publicationPath);
                    study.appendChild(motivatingPublication);
                }
            }
            return study;
        }
        catch (DOMException e) {
            LOG.error(e.getLocalizedMessage());
        }
        catch (ParserConfigurationException e) {
            LOG.error(e.getLocalizedMessage());
        }
        return null;
    }

    private Component createView(ResourceProxy resourceProxy) {
        Preconditions.checkNotNull(resourceProxy, "ResourceProxy is NULL");
        StudyBean studyBean = null;
        if (!(resourceProxy instanceof ContainerProxy)) {
            LOG.error("Wrong item type!");
        }
        try {
            studyBean = loadBeanData();
        }
        catch (final EscidocBrowserException e) {
            LOG.error(e.getLocalizedMessage());
            // TODO show error to the user
        }
        return new StudyView(studyBean, this, this.createBeadCrumbModel(), resourceProxy, router);
    }

    private StudyBean loadBeanData() throws EscidocBrowserException {
        if (resourceProxy == null || !(resourceProxy instanceof ContainerProxy)) {
            throw new EscidocBrowserException("NOT an ContainerProxy", null);
        }

        final ContainerProxy containerProxy1 = (ContainerProxy) resourceProxy;
        final StudyBean studyBean = new StudyBean();
        studyBean.setObjectId(containerProxy1.getId());

        final Element e = containerProxy1.getMedataRecords().get("escidoc").getContent();
        if (e != null && e.getChildNodes() != null) {
            final NodeList nodeList = e.getChildNodes();
            final String URI_DC = "http://purl.org/dc/elements/1.1/";
            final String URI_EL = "http://escidoc.org/ontologies/bw-elabs/re#";

            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                final String nodeName = node.getLocalName();
                final String nsUri = node.getNamespaceURI();

                if (nodeName == null || nodeName.equals("")) {
                    continue;
                }

                if ("title".equals(nodeName) && URI_DC.equals(nsUri)) {
                    studyBean.setName((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }
                else if ("description".equals(nodeName) && URI_DC.equals(nsUri)) {
                    studyBean
                        .setDescription((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }
                else if ("motivating-publication".equals(nodeName) && URI_EL.equals(nsUri)) {
                    studyBean.getMotivatingPublication().add(
                        node.getAttributes().getNamedItem("rdf:resource").getNodeValue());
                }
                else if ("resulting-publication".equals(nodeName) && URI_EL.equals(nsUri)) {
                    studyBean.getResultingPublication().add(
                        node.getAttributes().getNamedItem("rdf:resource").getNodeValue());
                }
            }
        }
        return studyBean;
    }

    private List<ResourceModel> createBeadCrumbModel() {
        try {
            List<ResourceModel> hierarchy =
                new ResourceHierarchy(serviceLocation, repositories).getHierarchy(resourceProxy);
            Collections.reverse(hierarchy);
            hierarchy.add(resourceProxy);
            return hierarchy;
        }
        catch (EscidocClientException e) {
            LOG.error("Fatal error, could not load BreadCrumb " + e.getLocalizedMessage());
            showError(e.getLocalizedMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean hasUpdateAccess() {
        try {
            return repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_CONTAINER).forResource(resourceProxy.getId())
                .permitted();
        }
        catch (UnsupportedOperationException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            LOG.error(e.getMessage());
            return false;
        }
        catch (EscidocClientException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            LOG.error(e.getMessage());
            return false;
        }
        catch (URISyntaxException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            LOG.error(e.getMessage());
            return false;
        }
    }

    protected void validateBean(IBeanModel beanModel) throws EscidocBrowserException {
        Preconditions.checkNotNull(beanModel, "Input is null");
        StudyBean studyBean = null;
        try {
            studyBean = (StudyBean) beanModel;
        }
        catch (ClassCastException e) {
            showError("Internal error");
            throw new EscidocBrowserException("Wrong type of model", e);
        }

        if (StringUtils.isEmpty(studyBean.getName()) || StringUtils.isEmpty(studyBean.getDescription())) {
            showError("Please fill out all of the requried fields!");
            throw new EscidocBrowserException("Some required field is null");
        }
    }
}
