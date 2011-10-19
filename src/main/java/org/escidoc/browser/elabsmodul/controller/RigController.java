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
import javax.xml.transform.TransformerException;

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.controller.utils.DOM2String;
import org.escidoc.browser.elabsmodul.exceptions.EscidocBrowserException;
import org.escidoc.browser.elabsmodul.interfaces.IBeanModel;
import org.escidoc.browser.elabsmodul.interfaces.ISaveAction;
import org.escidoc.browser.elabsmodul.model.RigBean;
import org.escidoc.browser.elabsmodul.views.RigView;
import org.escidoc.browser.elabsmodul.views.YesNoDialog;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
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
public final class RigController extends Controller implements ISaveAction {

    private static Logger LOG = LoggerFactory.getLogger(RigController.class);

    private Repositories repositories;

    private EscidocServiceLocation serviceLocation;

    private ResourceProxy resourceProxy;

    private Window mainWindow;

    // the bean model to store
    private IBeanModel beanModel = null;

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
    private synchronized RigBean loadBeanData(final ResourceProxy resourceProxy) throws EscidocBrowserException {

        if (resourceProxy == null || !(resourceProxy instanceof ItemProxy)) {
            throw new EscidocBrowserException("NOT an ItemProxy", null);
        }

        final ItemProxy itemProxy = (ItemProxy) resourceProxy;
        final RigBean rigBean = new RigBean();

        try {
            final Element e = itemProxy.getMedataRecords().get("escidoc").getContent();
            final String xml = DOM2String.convertDom2String(e);

            final String URI_DC = "http://purl.org/dc/elements/1.1/";
            final String URI_EL = "http://escidoc.org/ontologies/bw-elabs/re#";
            final String URI_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

            final NodeList nodeList = e.getChildNodes();
            rigBean.setObjectId(itemProxy.getId());

            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                final String nodeName = node.getLocalName();
                final String nsUri = node.getNamespaceURI();

                if ("title".equals(nodeName) && URI_DC.equals(nsUri)) {
                    rigBean.setTitle((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }

                else if ("description".equals(nodeName) && URI_DC.equals(nsUri)) {
                    rigBean.setDescription((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }
                else if ("instrument".equals(nodeName) && URI_EL.equals(nsUri)) {
                    if (node.getAttributes() != null
                        && node.getAttributes().getNamedItemNS(URI_RDF, "resource") != null) {
                        Node attributeNode = node.getAttributes().getNamedItemNS(URI_RDF, "resource");
                        String instrumentID = attributeNode.getNodeValue();

                        // get the whole instrBean

                    }
                }
            }

            LOG.debug(xml);
        }
        catch (final TransformerException e) {
            LOG.error(e.getLocalizedMessage());
        }

        return rigBean;
    }

    public synchronized static Element createRigDOMElementByBeanModel(final RigBean rigBean) {

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setValidating(true);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            final Document doc = builder.newDocument();

            Element rig = doc.createElementNS("http://escidoc.org/ontologies/bw-elabs/re#", "Rig");
            rig.setPrefix("el");

            final Element title = doc.createElementNS("http://purl.org/dc/elements/1.1/", "title");
            title.setPrefix("dc");
            title.setTextContent(rigBean.getTitle());
            rig.appendChild(title);

            final Element description = doc.createElementNS("http://purl.org/dc/elements/1.1/", "description");
            description.setPrefix("dc");
            description.setTextContent(rigBean.getDescription());
            rig.appendChild(description);

            // TODO CONTENT element

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
        Document doc, Element rig, String attributeValue, String value) {
        final Element element = doc.createElementNS("http://escidoc.org/ontologies/bw-elabs/re#", attributeValue);
        element.setTextContent(value);
        element.setPrefix("el");
        rig.appendChild(element);
        return rig;
    }

    private Component createView(final ResourceProxy resourceProxy) {
        Preconditions.checkNotNull(resourceProxy, "ResourceProxy is NULL");

        ItemProxyImpl itemProxyImpl = null;
        RigBean rigBean = null;

        if (resourceProxy instanceof ItemProxyImpl) {
            itemProxyImpl = (ItemProxyImpl) resourceProxy;
        }

        try {
            rigBean = loadBeanData(itemProxyImpl);
        }
        catch (final EscidocBrowserException e) {
            mainWindow.showNotification(new Notification("Error", e.getMessage(), Notification.TYPE_ERROR_MESSAGE));
            LOG.error(e.getLocalizedMessage());
        }
        return new RigView(rigBean, this, createBeadCrumbModel(), resourceProxy);
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

        mainWindow.addWindow(new YesNoDialog(ELabsViewContants.DIALOG_SAVERIG_HEADER,
            ELabsViewContants.DIALOG_SAVERIG_TEXT, new YesNoDialog.Callback() {

                @Override
                public void onDialogResult(boolean resultIsYes) {
                    if (resultIsYes) {
                        RigController.this.saveModel();
                    }
                    else {
                        ((RigView) RigController.this.view).hideButtonLayout();
                    }
                }
            }));
    }

    private synchronized void saveModel() {
        Preconditions.checkNotNull(beanModel, "DataBean to store is NULL");
        ItemRepository itemRepositories = repositories.item();
        final String ESCIDOC = "escidoc";

        RigBean rigBean = null;
        Item item = null;

        if (beanModel instanceof RigBean) {
            rigBean = (RigBean) beanModel;
        }
        final Element metaDataContent = RigController.createRigDOMElementByBeanModel(rigBean);

        try {
            item = itemRepositories.findItemById(rigBean.getObjectId());
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
}
