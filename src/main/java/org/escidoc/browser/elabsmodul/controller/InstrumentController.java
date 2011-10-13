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
import javax.xml.transform.TransformerException;

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.elabsmodul.controller.utils.DOM2String;
import org.escidoc.browser.elabsmodul.exceptions.EscidocBrowserException;
import org.escidoc.browser.elabsmodul.model.InstrumentBean;
import org.escidoc.browser.elabsmodul.view.maincontent.LabsInstrumentView;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vaadin.ui.Component;

/**
 * 
 */
public final class InstrumentController extends Controller {

    private static Logger LOG = LoggerFactory.getLogger(InstrumentController.class);

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
    public synchronized static Element createDOMElementByBeanModel(final InstrumentBean instrumentBean) {

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
        catch (final Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
    public void init(ResourceProxy resourceProxy) {
        this.view = createView(resourceProxy);

    }

    private Component createView(final ResourceProxy resourceProxy) {

        ItemProxyImpl itemProxyImpl = (ItemProxyImpl) resourceProxy;

        InstrumentBean instumentBean = null;
        try {
            instumentBean = loadBeanData(itemProxyImpl);
        }
        catch (EscidocBrowserException e) {
            LOG.error(e.getLocalizedMessage());
            instumentBean = null;
        }

        return new LabsInstrumentView(instumentBean);
    }
}
