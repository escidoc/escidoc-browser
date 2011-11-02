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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.exceptions.EscidocBrowserException;
import org.escidoc.browser.elabsmodul.interfaces.IBeanModel;
import org.escidoc.browser.elabsmodul.interfaces.ISaveAction;
import org.escidoc.browser.elabsmodul.model.InstrumentBean;
import org.escidoc.browser.elabsmodul.views.InstrumentView;
import org.escidoc.browser.elabsmodul.views.YesNoDialog;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.repository.internal.ItemRepository;
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
import de.escidoc.core.resources.om.item.Item;

/**
 * 
 */
public final class InstrumentController extends Controller implements ISaveAction {

    private static Logger LOG = LoggerFactory.getLogger(InstrumentController.class);

    private Repositories repositories;

    private EscidocServiceLocation serviceLocation;

    private ResourceProxy resourceProxy;

    private Window mainWindow;

    // the bean model to store
    private IBeanModel beanModel = null;

    // contains default eSychDaemon URLs
    private List<String> eSyncDaemonUrls = new ArrayList<String>();

    // contains default deposit endpoint URLs
    private List<String> depositEndPointUrls = new ArrayList<String>();

    @Override
    public void init(
        EscidocServiceLocation serviceLocation, Repositories repositories, Router mainSite,
        ResourceProxy resourceProxy, Window mainWindow, CurrentUser currentUser) {
        Preconditions.checkNotNull(repositories, "Repository ref is null");
        Preconditions.checkNotNull(serviceLocation, "ServiceLocation ref is null");
        this.serviceLocation = serviceLocation;
        this.resourceProxy = resourceProxy;
        this.repositories = repositories;
        this.mainWindow = mainWindow;
        loadAdminDescriptorInfo();
        view = createView(resourceProxy);
        this.getResourceName(resourceProxy.getName());
    }

    /**
     * 
     * @param resourceProxy
     *            resource ref
     * @return controlled bean
     * @throws EscidocBrowserException
     *             exception
     */
    private synchronized InstrumentBean loadBeanData(final ResourceProxy resourceProxy) throws EscidocBrowserException {

        if (resourceProxy == null || !(resourceProxy instanceof ItemProxy)) {
            throw new EscidocBrowserException("NOT an ItemProxy", null);
        }

        final ItemProxy itemProxy = (ItemProxy) resourceProxy;
        final InstrumentBean instrumentBean = new InstrumentBean();

        try {
            final Element e = itemProxy.getMedataRecords().get("escidoc").getContent();

            final NodeList nodeList = e.getChildNodes();

            instrumentBean.setObjectId(itemProxy.getId());

            final String URI_DC = "http://purl.org/dc/elements/1.1/";
            final String URI_EL = "http://escidoc.org/ontologies/bw-elabs/re#";
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                final String nodeName = node.getLocalName();
                final String nsUri = node.getNamespaceURI();

                if ("title".equals(nodeName) && URI_DC.equals(nsUri)) {
                    instrumentBean.setName((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }

                else if ("description".equals(nodeName) && URI_DC.equals(nsUri)) {
                    instrumentBean
                        .setDescription((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }

                else if ("requires-configuration".equals(nodeName) && URI_EL.equals(nsUri)) {
                    final String value = node.getFirstChild().getNodeValue();
                    if (value.equals("no")) {
                        instrumentBean.setConfiguration(false);
                    }
                    else if (value.equals("yes")) {
                        instrumentBean.setConfiguration(true);
                    }

                }
                else if ("requires-calibration".equals(nodeName) && URI_EL.equals(nsUri)) {
                    final String value = node.getFirstChild().getNodeValue();
                    if (value.equals("no")) {
                        instrumentBean.setCalibration(false);
                    }
                    else if (value.equals("yes")) {
                        instrumentBean.setCalibration(true);
                    }
                }
                else if ("esync-endpoint".equals(nodeName) && URI_EL.equals(nsUri)) {
                    instrumentBean
                        .setESyncDaemon((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }
                else if ("monitored-folder".equals(nodeName) && URI_EL.equals(nsUri)) {
                    instrumentBean
                        .setFolder((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }
                else if ("result-mime-type".equals(nodeName) && URI_EL.equals(nsUri)) {
                    instrumentBean
                        .setFileFormat((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }
                else if ("responsible-person".equals(nodeName) && URI_EL.equals(nsUri)
                    && node.getAttributes().getNamedItem("rdf:resource") != null) {
                    instrumentBean
                        .setDeviceSupervisor(node.getAttributes().getNamedItem("rdf:resource").getNodeValue());
                }
                else if ("institution".equals(nodeName) && URI_EL.equals(nsUri)
                    && node.getAttributes().getNamedItem("rdf:resource") != null) {
                    instrumentBean.setInstitute(node.getAttributes().getNamedItem("rdf:resource").getNodeValue());
                }

            }
        }
        catch (final TransformerException e) {
            LOG.error(e.getLocalizedMessage());
        }

        return instrumentBean;
    }

    private synchronized static Element createInstrumentDOMElementByBeanModel(final InstrumentBean instrumentBean) {

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setValidating(true);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            final Document doc = builder.newDocument();

            Element instrument = doc.createElementNS("http://escidoc.org/ontologies/bw-elabs/re#", "Instrument");
            instrument.setPrefix("el");

            // e.g. <dc:title xmlns:dc="http://purl.org/dc/elements/1.1/">FRS
            // Instrument 01</dc:title>
            final Element title = doc.createElementNS("http://purl.org/dc/elements/1.1/", "title");
            title.setPrefix("dc");
            title.setTextContent(instrumentBean.getName());
            instrument.appendChild(title);

            // e.g. <dc:description
            // xmlns:dc="http://purl.org/dc/elements/1.1/">A
            // description.</dc:description>
            final Element description = doc.createElementNS("http://purl.org/dc/elements/1.1/", "description");
            description.setPrefix("dc");
            description.setTextContent(instrumentBean.getDescription());
            instrument.appendChild(description);

            // <el:identity-number></el:identity-number>
            instrument = createDOMElementWithoutNamespace(doc, instrument, "identity-number", "");

            // <el:requires-configuration>no</el:requires-configuration>
            instrument =
                createDOMElementWithoutNamespace(doc, instrument, "requires-configuration",
                    booleanToHumanReadable(instrumentBean.getConfiguration()));

            // <el:requires-calibration>no</el:requires-calibration>
            instrument =
                createDOMElementWithoutNamespace(doc, instrument, "requires-calibration",
                    booleanToHumanReadable(instrumentBean.getCalibration()));

            // <el:esync-endpoint>http://my.es/ync/endpoint</el:esync-endpoint>
            instrument =
                createDOMElementWithoutNamespace(doc, instrument, "esync-endpoint", instrumentBean.getESyncDaemon());

            // <el:monitored-folder>C:\tmp</el:monitored-folder>
            instrument =
                createDOMElementWithoutNamespace(doc, instrument, "monitored-folder", instrumentBean.getFolder());

            // <el:result-mime-type>application/octet-stream</el:result-mime-type>
            instrument =
                createDOMElementWithoutNamespace(doc, instrument, "result-mime-type", instrumentBean.getFileFormat());

            // <el:responsible-person
            // xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
            // rdf:resource="escidoc:42"></el:responsible-person>
            final Element responsiblePerson = doc.createElement("el:responsible-person");
            responsiblePerson.setAttributeNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:resource",
                instrumentBean.getDeviceSupervisor());
            instrument.appendChild(responsiblePerson);

            // <el:institution
            // xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
            // rdf:resource="escidoc:1001"></el:institution>
            final Element institution = doc.createElement("el:institution");
            institution.setAttributeNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:resource",
                instrumentBean.getInstitute());
            instrument.appendChild(institution);
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

    private static String booleanToHumanReadable(final boolean value) {
        return (value) ? "yes" : "no";
    }

    private static Element createDOMElementWithoutNamespace(
        Document doc, Element instrument, String attributeValue, String value) {
        final Element element = doc.createElementNS("http://escidoc.org/ontologies/bw-elabs/re#", attributeValue);
        element.setTextContent(value);
        element.setPrefix("el");
        instrument.appendChild(element);
        return instrument;
    }

    /**
     * Used to retrieve eSyncDaemonsUrls and depositEndPointUrl
     * 
     * @throws EscidocClientException
     */
    public void loadAdminDescriptorInfo() {
        ContextProxyImpl context;
        try {
            context = (ContextProxyImpl) repositories.context().findById(resourceProxy.getContext().getObjid());
            Element content = context.getAdminDescription().get("elabs").getContent();
            NodeList nodeList = content.getElementsByTagName("el:esync-endpoint");
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                eSyncDaemonUrls.add(node.getTextContent());
            }

        }
        catch (EscidocClientException e) {
            LOG.debug("Error occurred. Could not load Admin Descriptors" + e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

    private Component createView(final ResourceProxy resourceProxy) {
        Preconditions.checkNotNull(resourceProxy, "ResourceProxy is NULL");

        ItemProxyImpl itemProxyImpl = null;
        InstrumentBean instumentBean = null;

        if (resourceProxy instanceof ItemProxyImpl) {
            itemProxyImpl = (ItemProxyImpl) resourceProxy;
        }

        try {
            instumentBean = loadBeanData(itemProxyImpl);
        }
        catch (final EscidocBrowserException e) {
            mainWindow.showNotification(new Notification("Error", e.getMessage(), Notification.TYPE_ERROR_MESSAGE));
            LOG.error(e.getLocalizedMessage());
        }
        return new InstrumentView(instumentBean, this, createBeadCrumbModel(), resourceProxy, eSyncDaemonUrls);
    }

    private List<ResourceModel> createBeadCrumbModel() {
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

    @Override
    public void saveAction(final IBeanModel beanModel) {
        Preconditions.checkNotNull(beanModel, "DataBean to store is NULL");
        this.beanModel = beanModel;

        mainWindow.addWindow(new YesNoDialog(ELabsViewContants.DIALOG_SAVEINSTRUMENT_HEADER,
            ELabsViewContants.DIALOG_SAVEINSTRUMENT_TEXT, new YesNoDialog.Callback() {

                @Override
                public void onDialogResult(boolean resultIsYes) {
                    if (resultIsYes) {
                        InstrumentController.this.saveModel();
                    }
                    else {
                        ((InstrumentView) InstrumentController.this.view).hideButtonLayout();
                    }
                }
            }));
    }

    private synchronized void saveModel() {
        Preconditions.checkNotNull(beanModel, "DataBean to store is NULL");
        ItemRepository itemRepositories = repositories.item();
        final String ESCIDOC = "escidoc";

        InstrumentBean instrumentBean = null;
        Item item = null;

        if (beanModel instanceof InstrumentBean) {
            instrumentBean = (InstrumentBean) beanModel;
        }
        final Element metaDataContent = InstrumentController.createInstrumentDOMElementByBeanModel(instrumentBean);

        try {
            item = itemRepositories.findItemById(instrumentBean.getObjectId());
            MetadataRecord metadataRecord = item.getMetadataRecords().get(ESCIDOC);
            metadataRecord.setContent(metaDataContent);
            itemRepositories.update(item.getObjid(), item);
        }
        catch (EscidocClientException e) {
            LOG.error(e.getLocalizedMessage());
        }
        finally {
            beanModel = null;
        }
        LOG.info("Instument is successfully saved.");
    }

    public List<String> geteSyncDaemonUrls() {
        return eSyncDaemonUrls;
    }

    public List<String> getDepositEndPointUrl() {
        return depositEndPointUrls;
    }
}
