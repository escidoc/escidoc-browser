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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.controller.utils.DOM2String;
import org.escidoc.browser.elabsmodul.exceptions.EscidocBrowserException;
import org.escidoc.browser.elabsmodul.interfaces.IBeanModel;
import org.escidoc.browser.elabsmodul.interfaces.ISaveAction;
import org.escidoc.browser.elabsmodul.model.InvestigationBean;
import org.escidoc.browser.elabsmodul.views.InstrumentView;
import org.escidoc.browser.elabsmodul.views.InvestigationView;
import org.escidoc.browser.elabsmodul.views.YesNoDialog;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ContainerProxyImpl;
import org.escidoc.browser.repository.internal.ContainerRepository;
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

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.container.Container;

/**
 * @author frs
 * 
 */
public class InvestigationController extends Controller implements ISaveAction {

    private static Logger LOG = LoggerFactory.getLogger(InstrumentController.class);

    // contains default deposit endpoint URLs
    private List<String> depositEndPointUrls = new ArrayList<String>();

    // FIXME move to Repositories
    private EscidocServiceLocation serviceLocation;

    private Repositories repositories;

    private Router router;

    private ResourceProxy resourceProxy;

    private IBeanModel model;

    private Window mainWindow;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.escidoc.browser.elabsmodul.interfaces.ISaveAction#saveAction(org.escidoc.browser.elabsmodul.interfaces.IBeanModel
     * )
     */
    @Override
    public void saveAction(IBeanModel model) {
        Preconditions.checkNotNull(model, "Model is null.");
        this.model = model;

        this.mainWindow.addWindow(new YesNoDialog(ELabsViewContants.DIALOG_SAVEINSTRUMENT_HEADER,
            ELabsViewContants.DIALOG_SAVEINSTRUMENT_TEXT, new YesNoDialog.Callback() {

                @Override
                public void onDialogResult(boolean resultIsYes) {
                    if (resultIsYes) {
                        InvestigationController.this.saveModel();
                    }
                    else {
                        ((InstrumentView) InvestigationController.this.view).hideButtonLayout();
                    }
                }
            }));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.controller.Controller#init(org.escidoc.browser.model.EscidocServiceLocation,
     * org.escidoc.browser.repository.Repositories, org.escidoc.browser.ui.Router,
     * org.escidoc.browser.model.ResourceProxy, com.vaadin.ui.Window, org.escidoc.browser.model.CurrentUser)
     */
    @Override
    public void init(
        EscidocServiceLocation serviceLocation, Repositories repositories, Router router, ResourceProxy resourceProxy,
        Window mainWindow, CurrentUser currentUser) {
        Preconditions.checkNotNull(repositories, "Repository ref is null");
        this.serviceLocation = serviceLocation;
        this.repositories = repositories;
        this.router = router;
        this.resourceProxy = resourceProxy;
        this.mainWindow = mainWindow;

        this.loadAdminDescriptorInfo();
        this.view = createView(resourceProxy);
        this.getResourceName(resourceProxy.getName());

    }

    private void loadAdminDescriptorInfo() {
        ContextProxyImpl context;
        try {
            context = (ContextProxyImpl) repositories.context().findById(resourceProxy.getContext().getObjid());
            Element content = context.getAdminDescription().get("elabs").getContent();
            NodeList nodeList = content.getElementsByTagName("el:deposit-endpoint");
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                depositEndPointUrls.add(node.getTextContent());
            }

        }
        catch (EscidocClientException e) {
            LOG.error("Could not load Admin Descriptor 'elabs'. " + e.getLocalizedMessage(), e);
        }
    }

    private Component createView(ResourceProxy resourceProxy) {
        Preconditions.checkNotNull(resourceProxy, "ResourceProxy is NULL");

        ContainerProxyImpl containerProxy = null;
        InvestigationBean investigationBean = null;
        List<ResourceModel> breadCrumbModel = null;

        if (resourceProxy instanceof ContainerProxyImpl) {
            containerProxy = (ContainerProxyImpl) resourceProxy;
        }

        try {
            investigationBean = loadBeanData(containerProxy);
        }
        catch (final EscidocBrowserException e) {
            LOG.error(e.getLocalizedMessage());
            investigationBean = null;
        }

        breadCrumbModel = createBreadCrumbModel();

        Component investigationView =
            new InvestigationView(investigationBean, this, breadCrumbModel, containerProxy, depositEndPointUrls);
        return investigationView;
    }

    private InvestigationBean loadBeanData(ContainerProxy containerProxy) throws EscidocBrowserException {

        if (containerProxy == null) {
            throw new NullPointerException("Container Proxy is null.");
        }
        final InvestigationBean investigationBean = new InvestigationBean();

        try {
            final Element e = containerProxy.getMedataRecords().get("escidoc").getContent();
            if (LOG.isDebugEnabled()) {
                final String xml = DOM2String.convertDom2String(e);
                LOG.debug(xml);
            }

            investigationBean.setObjid(containerProxy.getId());

            // Check if this is really an Investigation
            if (!(("Investigation".equals(e.getLocalName()) && "http://escidoc.org/ontologies/bw-elabs/re#".equals(e
                .getNamespaceURI())) || "el:Investigation".equals(e.getTagName()))) {
                LOG.error("Container is not an eLabs Investigation");
                return investigationBean;
            }

            final NodeList nodeList = e.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                final String nodeName = node.getLocalName();
                final String nsUri = node.getNamespaceURI();

                if ("title".equals(nodeName) && "http://purl.org/dc/elements/1.1/".equals(nsUri)) {
                    // System.out.println("#" + node.getTextContent() + "#");
                    // System.out.println("#" + node.getFirstChild().getNodeValue() + "#");
                    investigationBean.setName(node.getTextContent());
                }

                else if ("description".equals(nodeName) && "http://purl.org/dc/elements/1.1/".equals(nsUri)) {
                    investigationBean.setDescription(node.getTextContent());
                }

                else if ("max-runtime".equals(nodeName) && "http://escidoc.org/ontologies/bw-elabs/re#".equals(nsUri)) {
                    investigationBean.setMaxRuntime(Long.parseLong(node.getTextContent()));
                }

                else if ("deposit-endpoint".equals(nodeName)
                    && "http://escidoc.org/ontologies/bw-elabs/re#".equals(nsUri)) {
                    investigationBean.setDepositEndpoint(node.getTextContent());
                }

                else if ("investigator".equals(nodeName) && "http://escidoc.org/ontologies/bw-elabs/re#".equals(nsUri)) {
                    investigationBean.setInvestigator(node
                        .getAttributes().getNamedItemNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "resource")
                        .getTextContent());
                }

                else if ("rig".equals(nodeName) && "http://escidoc.org/ontologies/bw-elabs/re#".equals(nsUri)) {
                    investigationBean.setRig(node
                        .getAttributes().getNamedItemNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "resource")
                        .getTextContent());
                }

                else if ("instrument".equals(nodeName) && "http://escidoc.org/ontologies/bw-elabs/re#".equals(nsUri)) {
                    String instrument =
                        node.getAttributes().getNamedItemNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "resource")
                            .getTextContent();
                    String folder = node.getTextContent().trim();
                    // System.out.println("#" + instrument + "#" + folder + "#");
                    investigationBean.getInstrumentFolder().put(instrument, folder);
                }

            }
        }
        catch (final TransformerException e) {
            LOG.error(e.getLocalizedMessage());
        }

        return investigationBean;
    }

    private List<ResourceModel> createBreadCrumbModel() {
        final ResourceHierarchy rs = new ResourceHierarchy(serviceLocation, repositories);
        List<ResourceModel> hierarchy = null;
        try {
            hierarchy = rs.getHierarchy(resourceProxy);
            Collections.reverse(hierarchy);
            hierarchy.add(resourceProxy);
            return hierarchy;
        }
        catch (EscidocClientException e) {
            LOG.error("Fatal error, could not load BreadCrumb " + e.getLocalizedMessage());
        }
        return Collections.emptyList();
    }

    private synchronized void saveModel() {
        Preconditions.checkNotNull(this.model, "Model is NULL. Can not save.");
        ContainerRepository containerRepository = repositories.container();
        final String ESCIDOC = "escidoc";

        InvestigationBean investigationBean = null;
        Container container = null;

        if (this.model instanceof InvestigationBean) {
            investigationBean = (InvestigationBean) this.model;
        }
        final Element metaDataContent =
            InvestigationController.createInvestigationDOMElementByBeanModel(investigationBean);

        try {
            container = containerRepository.findContainerById(investigationBean.getObjid());
            MetadataRecord metadataRecord = container.getMetadataRecords().get(ESCIDOC);
            metadataRecord.setContent(metaDataContent);
            containerRepository.update(container);
        }
        catch (EscidocClientException e) {
            LOG.error(e.getLocalizedMessage());
        }
        finally {
            this.model = null;
        }
        LOG.info("Investigation is successfully saved.");
    }

    public synchronized static Element createInvestigationDOMElementByBeanModel(final InvestigationBean instrumentBean) {

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setValidating(true);
        DocumentBuilder builder;
        final String NSURI_ELABS_RE = "http://escidoc.org/ontologies/bw-elabs/re#";
        final String NSPREFIX_ELABS_RE = "el";
        final String NSURI_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
        final String NSPREFIX_RDF = "rdf";
        try {
            builder = factory.newDocumentBuilder();
            final Document doc = builder.newDocument();

            Element investigation = doc.createElementNS(NSURI_ELABS_RE, "Investigation");
            investigation.setPrefix(NSPREFIX_ELABS_RE);

            // e.g. <dc:title xmlns:dc="http://purl.org/dc/elements/1.1/">FRS
            // Instrument 01</dc:title>
            final Element title = doc.createElementNS("http://purl.org/dc/elements/1.1/", "title");
            title.setPrefix("dc");
            title.setTextContent(instrumentBean.getName());
            investigation.appendChild(title);

            // e.g. <dc:description
            // xmlns:dc="http://purl.org/dc/elements/1.1/">A
            // description.</dc:description>
            final Element description = doc.createElementNS("http://purl.org/dc/elements/1.1/", "description");
            description.setPrefix("dc");
            description.setTextContent(instrumentBean.getDescription());
            investigation.appendChild(description);

            // private long maxRuntime;
            //
            // private String depositEndpoint;
            //
            // private String investigator;
            //
            // private String rig;
            //
            // private Map<String, String> instrumentFolder = new HashMap<String, String>();

            Element maxRuntime = doc.createElementNS(NSURI_ELABS_RE, "max-runtime");
            maxRuntime.setPrefix(NSPREFIX_ELABS_RE);
            maxRuntime.setTextContent("" + instrumentBean.getMaxRuntime());
            investigation.appendChild(maxRuntime);

            Element depositEndpoint = doc.createElementNS(NSURI_ELABS_RE, "deposit-endpoint");
            depositEndpoint.setPrefix(NSPREFIX_ELABS_RE);
            depositEndpoint.setTextContent(instrumentBean.getDepositEndpoint());
            investigation.appendChild(depositEndpoint);

            Element investigator = doc.createElementNS(NSURI_ELABS_RE, "investigator");
            investigator.setPrefix(NSPREFIX_ELABS_RE);
            investigator.setAttributeNS(NSURI_RDF, NSPREFIX_RDF + ":resource", instrumentBean.getInvestigator());
            investigation.appendChild(investigator);

            Element rig = doc.createElementNS(NSURI_ELABS_RE, "rig");
            rig.setPrefix(NSPREFIX_ELABS_RE);
            rig.setAttributeNS(NSURI_RDF, NSPREFIX_RDF + ":resource", instrumentBean.getRig());
            investigation.appendChild(rig);

            for (Entry<String, String> instrumentFolder : instrumentBean.getInstrumentFolder().entrySet()) {
                Element instrument = doc.createElementNS(NSURI_ELABS_RE, "instrument");
                instrument.setPrefix(NSPREFIX_ELABS_RE);
                instrument.setAttributeNS(NSURI_RDF, NSPREFIX_RDF + ":resource", instrumentFolder.getKey());

                Element folder = doc.createElementNS(NSURI_ELABS_RE, "monitored-folder");
                folder.setPrefix(NSPREFIX_ELABS_RE);
                folder.setTextContent(instrumentFolder.getValue());
                instrument.appendChild(folder);

                investigation.appendChild(instrument);
            }

            if (LOG.isDebugEnabled()) {
                String xml = null;
                try {
                    xml = DOM2String.convertDom2String(investigation);
                }
                catch (TransformerException e) {
                    e.printStackTrace();
                }
                LOG.debug(xml);
            }

            return investigation;

        }
        catch (DOMException e) {
            LOG.error(e.getLocalizedMessage());
        }
        catch (ParserConfigurationException e) {
            LOG.error(e.getLocalizedMessage());
        }

        return null;
    }

}
