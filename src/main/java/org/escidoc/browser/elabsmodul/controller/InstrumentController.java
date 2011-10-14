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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.elabsmodul.controller.utils.DOM2String;
import org.escidoc.browser.elabsmodul.exceptions.EscidocBrowserException;
import org.escidoc.browser.elabsmodul.interfaces.IBeanModel;
import org.escidoc.browser.elabsmodul.interfaces.ISaveAction;
import org.escidoc.browser.elabsmodul.model.InstrumentBean;
import org.escidoc.browser.elabsmodul.views.LabsInstrumentPanel;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.repository.internal.ItemRepository;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.item.Item;

/**
 * 
 */
public final class InstrumentController extends Controller implements ISaveAction {

    private static Logger LOG = LoggerFactory.getLogger(InstrumentController.class);

    private Repositories repositories;

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
            final String xml = DOM2String.convertDom2String(e);

            final NodeList nodeList = e.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                final String nodeName = node.getNodeName();

                if (nodeName.equals("dc:title")) {
                    instrumentBean.setName((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }

                else if (nodeName.equals("dc:description")) {
                    instrumentBean
                        .setDescription((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }

                else if (nodeName.equals("el:requires-configuration")) {
                    final String value = node.getFirstChild().getNodeValue();
                    if (value.equals("no")) {
                        instrumentBean.setConfiguration(false);
                    }
                    else if (value.equals("yes")) {
                        instrumentBean.setConfiguration(true);
                    }

                }
                else if (nodeName.equals("el:requires-calibration")) {
                    final String value = node.getFirstChild().getNodeValue();
                    if (value.equals("no")) {
                        instrumentBean.setCalibration(false);
                    }
                    else if (value.equals("yes")) {
                        instrumentBean.setCalibration(true);
                    }
                }
                else if (nodeName.equals("el:esync-endpoint")) {
                    instrumentBean
                        .setESyncDaemon((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }
                else if (nodeName.equals("el:monitored-folder")) {
                    instrumentBean
                        .setFolder((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }
                else if (nodeName.equals("el:result-mime-type")) {
                    instrumentBean
                        .setFileFormat((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }
                else if (nodeName.equals("el:responsible-person")
                    && node.getAttributes().getNamedItem("rdf:resource") != null) {
                    instrumentBean
                        .setDeviceSupervisor(node.getAttributes().getNamedItem("rdf:resource").getNodeValue());
                }
                else if (nodeName.equals("el:institution") && node.getAttributes().getNamedItem("rdf:resource") != null) {
                    instrumentBean.setInstitute(node.getAttributes().getNamedItem("rdf:resource").getNodeValue());
                }

            }
            LOG.debug(xml);
        }
        catch (final TransformerException e) {
            LOG.error(e.getLocalizedMessage());
        }

        return instrumentBean;
    }

    // TODO
    public synchronized static Element createInstrumentDOMElementByBeanModel(final InstrumentBean instrumentBean) {

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
            instrument = createWithoutNamespace(doc, instrument, "identity-number", "");

            // <el:requires-configuration>no</el:requires-configuration>
            instrument =
                createWithoutNamespace(doc, instrument, "requires-configuration",
                    booleanToHumanReadable(instrumentBean.getConfiguration()));

            // <el:requires-calibration>no</el:requires-calibration>
            instrument =
                createWithoutNamespace(doc, instrument, "requires-calibration",
                    booleanToHumanReadable(instrumentBean.getCalibration()));

            // <el:esync-endpoint>http://my.es/ync/endpoint</el:esync-endpoint>
            instrument = createWithoutNamespace(doc, instrument, "esync-endpoint", instrumentBean.getESyncDaemon());

            // <el:monitored-folder>C:\tmp</el:monitored-folder>
            instrument = createWithoutNamespace(doc, instrument, "monitored-folder", instrumentBean.getFolder());

            // <el:result-mime-type>application/octet-stream</el:result-mime-type>
            instrument = createWithoutNamespace(doc, instrument, "result-mime-type", instrumentBean.getFileFormat());

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

    private static Element createWithoutNamespace(Document doc, Element instrument, String attributeValue, String value) {
        final Element element = doc.createElementNS("http://escidoc.org/ontologies/bw-elabs/re#", attributeValue);
        element.setTextContent(value);
        element.setPrefix("el");
        instrument.appendChild(element);
        return instrument;
    }

    @Override
    public void init(
        EscidocServiceLocation serviceLocation, Repositories repositories, Router mainSite,
        ResourceProxy resourceProxy, Window mainWindow, CurrentUser currentUser) {
        Preconditions.checkNotNull(repositories, "Repository ref is null");

        this.repositories = repositories;
        this.view = createView(resourceProxy);
        this.view.setCaption("Default Caption");
    }

    private Component createView(final ResourceProxy resourceProxy) {
        Preconditions.checkNotNull(resourceProxy, "ResourceProxy is NULL");
        final String VIEWCAPTION = "Instument View";
        final String LAST_MODIFIED_BY = "Last modification by ";
        final String FLOAT_LEFT = "floatleft";
        final String FLOAT_RIGHT = "floatright";

        ItemProxyImpl itemProxyImpl = null;
        InstrumentBean instumentBean = null;

        final VerticalLayout rootCompoent = new VerticalLayout();
        rootCompoent.setCaption(VIEWCAPTION);

        if (resourceProxy instanceof ItemProxyImpl) {
            itemProxyImpl = (ItemProxyImpl) resourceProxy;
        }

        try {
            instumentBean = loadBeanData(itemProxyImpl);
        }
        catch (final EscidocBrowserException e) {
            LOG.error(e.getLocalizedMessage());
            instumentBean = null;
        }

        /* Create all the subviews */
        // BreadCrumbp View
        VerticalLayout breadCrumbpView = new VerticalLayout();
        breadCrumbpView.setCaption("BreadCrumbp View Caption");

        // Item title
        final Label titleLabel = new Label(ViewConstants.RESOURCE_NAME + resourceProxy.getName());
        titleLabel.setDescription("header");
        titleLabel.setStyleName("h2 fullwidth");

        // HR Ruler
        final Label descRuler = new Label("<hr/>", Label.CONTENT_RAW);
        descRuler.setStyleName("hr");

        // ItemProperties View
        final HorizontalLayout propertiesView = new HorizontalLayout();
        final Label descMetadata1 = new Label("ID: " + instumentBean.getObjectId());
        final Label descMetadata2 =
            new Label(LAST_MODIFIED_BY + " " + resourceProxy.getModifier() + " on " + resourceProxy.getModifiedOn(),
                Label.CONTENT_XHTML);

        final Panel pnlPropertiesLeft = new Panel();
        pnlPropertiesLeft.setWidth("40%");
        pnlPropertiesLeft.setHeight("60px");
        pnlPropertiesLeft.setStyleName(FLOAT_LEFT);
        pnlPropertiesLeft.addStyleName(Runo.PANEL_LIGHT);
        pnlPropertiesLeft.addComponent(descMetadata1);

        final Panel pnlPropertiesRight = new Panel();
        pnlPropertiesRight.setWidth("60%");
        pnlPropertiesRight.setHeight("60px");
        pnlPropertiesRight.setStyleName(FLOAT_RIGHT);
        pnlPropertiesRight.addStyleName(Runo.PANEL_LIGHT);
        pnlPropertiesRight.addComponent(descMetadata2);
        propertiesView.addComponent(pnlPropertiesLeft);
        propertiesView.addComponent(pnlPropertiesRight);

        // Instrument View
        final Component instrumentView = new LabsInstrumentPanel(instumentBean, this); // Controller as saveComponent is
                                                                                       // pushed into InstrumentView

        /* Add subelements on to RootComponent */
        rootCompoent.addComponent(breadCrumbpView);
        rootCompoent.addComponent(titleLabel);
        rootCompoent.addComponent(descRuler);
        rootCompoent.addComponent(propertiesView);
        rootCompoent.addComponent(instrumentView);

        return rootCompoent;
    }

    @Override
    public void saveAction(final IBeanModel dataBean) {
        Preconditions.checkNotNull(dataBean, "DataBean to store is NULL");

        ItemRepository itemRepositories = repositories.item();
        final String ESCIDOC = "escidoc";

        InstrumentBean instrumentBean = null;
        Item item = null;

        if (dataBean instanceof InstrumentBean) {
            instrumentBean = (InstrumentBean) dataBean;
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
        LOG.info("Instument is successfully saved.");
    }
}
