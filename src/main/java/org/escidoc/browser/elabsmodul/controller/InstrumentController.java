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

import javax.xml.transform.TransformerException;

import org.escidoc.browser.elabsmodul.controller.utils.DOM2String;
import org.escidoc.browser.elabsmodul.exceptions.EscidocBrowserException;
import org.escidoc.browser.elabsmodul.model.InstrumentBean;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.ResourceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class InstrumentController extends AbstractELabsController {

    private static Object syncObject = new Object();

    private static InstrumentController singleton = null;

    private static Logger LOG = LoggerFactory.getLogger(InstrumentController.class);

    public static InstrumentController getInstance() {
        if (singleton == null) {
            synchronized (syncObject) {
                if (singleton == null) {
                    singleton = new InstrumentController();
                }
            }
        }
        return singleton;
    }

    private InstrumentController() {
        this.controlledBeanClass = InstrumentBean.class;
    }

    public synchronized InstrumentBean loadBeanData(ResourceProxy resourceProxy) throws EscidocBrowserException {

        if (resourceProxy == null || !(resourceProxy instanceof ItemProxy)) {
            throw new EscidocBrowserException("NOT an ItemProxy", null);
        }

        ItemProxy itemProxy = (ItemProxy) resourceProxy;
        InstrumentBean instrumentBean = new InstrumentBean();

        try {
            Element e = itemProxy.getMedataRecords().get("escidoc").getContent();
            final String xml = DOM2String.convertDom2String(e);

            NodeList nodeList = e.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                String nodeName = node.getNodeName();

                if (nodeName.equals("dc:title")) {
                    instrumentBean.setName((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }

                else if (nodeName.equals("dc:description")) {
                    instrumentBean
                        .setDescription((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }

                else if (nodeName.equals("el:requires-configuration")) {
                    String value = node.getFirstChild().getNodeValue();
                    if (value.equals("no")) {
                        instrumentBean.setConfiguration(false);
                    }
                    else if (value.equals("yes")) {
                        instrumentBean.setConfiguration(true);
                    }

                }
                else if (nodeName.equals("el:requires-calibration")) {
                    String value = node.getFirstChild().getNodeValue();
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
                else if (nodeName.equals("el:responsible-person")) {
                    instrumentBean.setDeviceSupervisor((node.getFirstChild() != null) ? node
                        .getFirstChild().getNodeValue() : null);
                }
                else if (nodeName.equals("el:institution")) {
                    instrumentBean
                        .setInstitute((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }
            }
            LOG.debug(xml);
        }
        catch (TransformerException e) {
            LOG.error(e.getLocalizedMessage());
        }

        return instrumentBean;
    }
}
