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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.elabsmodul.cache.ELabsCache;
import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.interfaces.IBeanModel;
import org.escidoc.browser.elabsmodul.interfaces.IRigAction;
import org.escidoc.browser.elabsmodul.model.InstrumentBean;
import org.escidoc.browser.elabsmodul.model.RigBean;
import org.escidoc.browser.elabsmodul.views.RigView;
import org.escidoc.browser.elabsmodul.views.YesNoDialog;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ItemProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
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

public final class RigController extends Controller implements IRigAction {

    private static Logger LOG = LoggerFactory.getLogger(RigController.class);

    private Repositories repositories;

    private EscidocServiceLocation serviceLocation;

    private ResourceProxy resourceProxy;

    private Window mainWindow;

    private IBeanModel beanModel = null;

    private Router router;

    private static List<String> cmmIds4Instrument = null, cmmIds4Rig = null;

    static {
        cmmIds4Instrument = new ArrayList<String>();
        cmmIds4Rig = new ArrayList<String>();
    }

    @Override
    public void init(Repositories repositories, Router router, ResourceProxy resourceProxy) {
        Preconditions.checkNotNull(repositories, "Repository ref is null");
        Preconditions.checkNotNull(router, "Router ref is null");
        this.router = router;
        this.serviceLocation = router.getServiceLocation();
        this.resourceProxy = resourceProxy;
        this.repositories = repositories;
        this.mainWindow = router.getMainWindow();
        view = createView(resourceProxy);
        this.setResourceName(resourceProxy.getName() + "#" + resourceProxy.getId());
    }

    /**
     * 
     * @param resourceProxy
     *            resource ref
     * @return controlled bean exception
     */
    private synchronized RigBean loadBeanData(final ItemProxy itemProxy) {
        Preconditions.checkNotNull(itemProxy, "Resource is null");

        RigBean rigBean = new RigBean();
        final String URI_DC = "http://purl.org/dc/elements/1.1/";
        final String URI_EL = "http://escidoc.org/ontologies/bw-elabs/re#";
        final String URI_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

        final NodeList nodeList = itemProxy.getMedataRecords().get("escidoc").getContent().getChildNodes();

        rigBean.setObjectId(itemProxy.getId());
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);
            final String nodeName = node.getLocalName();
            final String nsUri = node.getNamespaceURI();

            if ("title".equals(nodeName) && URI_DC.equals(nsUri)) {
                rigBean.setName((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
            }

            else if ("description".equals(nodeName) && URI_DC.equals(nsUri)) {
                rigBean.setDescription((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
            }
            else if ("instrument".equals(nodeName) && URI_EL.equals(nsUri)) {
                if (node.getAttributes() != null && node.getAttributes().getNamedItemNS(URI_RDF, "resource") != null) {
                    Node attributeNode = node.getAttributes().getNamedItemNS(URI_RDF, "resource");
                    String instrumentID = attributeNode.getNodeValue();

                    try {
                        ItemProxy instrumentProxy = (ItemProxy) repositories.item().findById(instrumentID);
                        rigBean.getContentList().add(loadRelatedInstrumentBeanData(instrumentProxy));
                    }
                    catch (EscidocClientException e) {
                        LOG.error(e.getLocalizedMessage());
                    }
                }
            }
        }
        return rigBean;
    }

    /**
     * @param resourceProxy
     * @return
     */
    private static synchronized InstrumentBean loadRelatedInstrumentBeanData(final ItemProxy instrumentItem) {
        Preconditions.checkNotNull(instrumentItem, "Resource is null");

        final InstrumentBean instrumentBean = new InstrumentBean();
        final String URI_DC = "http://purl.org/dc/elements/1.1/";
        final NodeList nodeList = instrumentItem.getMedataRecords().get("escidoc").getContent().getChildNodes();

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
            instrumentBean.setObjectId(instrumentItem.getId());
        }
        return instrumentBean;
    }

    private synchronized static Element createRigDOMElementByBeanModel(final RigBean rigBean) {
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
            title.setTextContent(rigBean.getName());
            rig.appendChild(title);

            final Element description = doc.createElementNS("http://purl.org/dc/elements/1.1/", "description");
            description.setPrefix("dc");
            description.setTextContent(rigBean.getDescription());
            rig.appendChild(description);

            for (InstrumentBean instrumentBean : rigBean.getContentList()) {
                final Element insturmentRelation =
                    doc.createElementNS("http://escidoc.org/ontologies/bw-elabs/re#", "instrument");
                insturmentRelation.setPrefix("el");
                insturmentRelation.setAttributeNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:resource",
                    instrumentBean.getObjectId());
                rig.appendChild(insturmentRelation);
            }
            return rig;
        }
        catch (DOMException e) {
            LOG.error(e.getLocalizedMessage());
        }
        catch (ParserConfigurationException e) {
            LOG.error(e.getLocalizedMessage());
        }
        return null;
    }

    private Component createView(final ResourceProxy resourceProxy) {
        Preconditions.checkNotNull(resourceProxy, "ResourceProxy is NULL");

        ItemProxyImpl itemProxyImpl = null;
        RigBean rigBean = null;

        if (resourceProxy instanceof ItemProxyImpl) {
            itemProxyImpl = (ItemProxyImpl) resourceProxy;
            rigBean = loadBeanData(itemProxyImpl);
        }

        return new RigView(rigBean, this, createBeadCrumbModel(), resourceProxy, serviceLocation);

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
                        try {
                            saveModel();
                        }
                        catch (EscidocClientException e) {
                            LOG.error(e.getMessage());
                            mainWindow.showNotification("Error", e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
                        }
                    }
                    ((RigView) RigController.this.view).hideButtonLayout();
                }
            }));
    }

    /**
     * 
     * @throws EscidocClientException
     */
    private synchronized void saveModel() throws EscidocClientException {
        Preconditions.checkNotNull(beanModel, "DataBean to store is NULL");
        ItemRepository itemRepositories = repositories.item();
        final String ESCIDOC = "escidoc";

        if (!(this.beanModel instanceof RigBean)) {
            return;
        }

        RigBean rigBean = (RigBean) beanModel;
        final Element metaDataContent = RigController.createRigDOMElementByBeanModel(rigBean);

        try {
            Item item = itemRepositories.findItemById(rigBean.getObjectId());
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
        LOG.info("Rig is successfully saved.");
    }

    @Override
    public synchronized List<InstrumentBean> getNewAvailableInstruments(final List<String> containedInstrumentIDs) {
        List<InstrumentBean> result = new ArrayList<InstrumentBean>();
        try {
            List<ResourceModel> items = null;
            for (Iterator<String> iterator = ELabsCache.getInstrumentCMMIds().iterator(); iterator.hasNext();) {
                String cmmId = iterator.next();
                items = repositories.item().findItemsByContentModel(cmmId);
                for (Iterator<ResourceModel> iterator2 = items.iterator(); iterator2.hasNext();) {
                    ResourceModel itemModel = iterator2.next();
                    if (itemModel instanceof ItemProxy) {
                        ItemProxy itemProxy = (ItemProxy) itemModel;
                        if (!containedInstrumentIDs.contains(itemProxy.getId())) {
                            result.add(loadRelatedInstrumentBeanData(itemProxy));
                        }
                    }
                }
            }
        }
        catch (EscidocClientException e) {
            LOG.error(e.getMessage());
        }
        return result;
    }

    @Override
    public boolean hasUpdateAccess() {
        try {
            return repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_ITEM).forResource(resourceProxy.getId())
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

    @Override
    public boolean isValidBean(IBeanModel dataBean) {
        // TODO Auto-generated method stub
        return false;
    }
}
