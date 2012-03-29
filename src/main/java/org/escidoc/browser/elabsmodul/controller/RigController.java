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

import com.google.common.base.Preconditions;

import com.vaadin.ui.Window;

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.elabsmodul.cache.ELabsCache;
import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.exceptions.EscidocBrowserException;
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
import org.escidoc.browser.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.item.Item;

public final class RigController extends Controller implements IRigAction {

    private static final Logger LOG = LoggerFactory.getLogger(RigController.class);

    private static final String ESCIDOC = "escidoc";

    private static final String URI_DC = "http://purl.org/dc/elements/1.1/";

    private static final String URI_EL = "http://escidoc.org/ontologies/bw-elabs/re#";

    private static final String URI_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    private final EscidocServiceLocation serviceLocation;

    private final Window mainWindow;

    private IBeanModel beanModel;

    private final Object LOCK_1 = new Object() {
        // empty
    };

    private final Object LOCK_2 = new Object() {
        // empty
    };

    public RigController(final Repositories repositories, final Router router, final ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        Preconditions.checkArgument(resourceProxy instanceof ItemProxy, "ResourceProxy is not an ItemProxy");
        this.serviceLocation = router.getServiceLocation();
        this.mainWindow = router.getMainWindow();
        createView();
        this.setResourceName(resourceProxy.getName() + "#" + resourceProxy.getId());
    }

    /**
     * 
     * @param resourceProxy
     *            resource ref
     * @return controlled bean exception
     */
    private RigBean loadBeanData(final ItemProxy itemProxy) {
        final NodeList nodeList = itemProxy.getMedataRecords().get(ESCIDOC).getContent().getChildNodes();
        final RigBean rigBean = new RigBean();
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
                    final Node attributeNode = node.getAttributes().getNamedItemNS(URI_RDF, "resource");
                    final String instrumentID = attributeNode.getNodeValue();
                    try {
                        final ItemProxy instrumentProxy = (ItemProxy) getRepositories().item().findById(instrumentID);
                        rigBean.getContentList().add(loadRelatedInstrumentBeanData(instrumentProxy));
                    }
                    catch (final EscidocClientException e) {
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
    private static InstrumentBean loadRelatedInstrumentBeanData(final ItemProxy instrumentItem) {
        Preconditions.checkNotNull(instrumentItem, "Resource is null");
        final InstrumentBean instrumentBean = new InstrumentBean();
        final NodeList nodeList = instrumentItem.getMedataRecords().get(ESCIDOC).getContent().getChildNodes();

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

    private static Element createRigDOMElementByBeanModel(final RigBean rigBean) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setValidating(true);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            final Document doc = builder.newDocument();

            final Element rig = doc.createElementNS(URI_EL, "Rig");
            rig.setPrefix("el");

            final Element title = doc.createElementNS(URI_DC, "title");
            title.setPrefix("dc");
            title.setTextContent(rigBean.getName());
            rig.appendChild(title);

            final Element description = doc.createElementNS(URI_DC, "description");
            description.setPrefix("dc");
            description.setTextContent(rigBean.getDescription());
            rig.appendChild(description);

            for (final InstrumentBean instrumentBean : rigBean.getContentList()) {
                final Element insturmentRelation = doc.createElementNS(URI_EL, "instrument");
                insturmentRelation.setPrefix("el");
                insturmentRelation.setAttributeNS(URI_RDF, "rdf:resource", instrumentBean.getObjectId());
                rig.appendChild(insturmentRelation);
            }
            return rig;
        }
        catch (final DOMException e) {
            LOG.error(e.getLocalizedMessage());
        }
        catch (final ParserConfigurationException e) {
            LOG.error(e.getLocalizedMessage());
        }
        return null;
    }

    private List<ResourceModel> createBeadCrumbModel() {
        final ResourceHierarchy rs = new ResourceHierarchy(this.serviceLocation, getRepositories());
        List<ResourceModel> hierarchy = null;
        try {
            hierarchy = rs.getHierarchy(this.getResourceProxy());
            Collections.reverse(hierarchy);
            hierarchy.add(this.getResourceProxy());
            return hierarchy;
        }
        catch (final EscidocClientException e) {
            LOG.error("Fatal error, could not load BreadCrumb " + e.getLocalizedMessage());
            showError(e.getLocalizedMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public void saveAction(final IBeanModel beanModel) {
        Preconditions.checkNotNull(beanModel, "DataBean to store is NULL");
        this.beanModel = beanModel;
        this.mainWindow.addWindow(new YesNoDialog(ELabsViewContants.DIALOG_SAVE_RIG_HEADER,
            ELabsViewContants.DIALOG_SAVE_RIG_TEXT, new YesNoDialog.Callback() {
                @Override
                public void onDialogResult(final boolean resultIsYes) {
                    if (resultIsYes) {
                        saveModel();
                    }
                    ((RigView) RigController.this.view).hideButtonLayout();
                }
            }));
    }

    /**
     * 
     * @throws EscidocClientException
     */
    private void saveModel() {
        synchronized (LOCK_1) {
            Preconditions.checkNotNull(this.beanModel, "DataBean to store is NULL");
            final ItemRepository itemRepositories = getRepositories().item();
            try {
                validateBean(this.beanModel);
            }
            catch (final EscidocBrowserException e) {
                LOG.error(e.getMessage());
                return;
            }
            final RigBean rigBean = (RigBean) this.beanModel;
            final Element metaDataContent = RigController.createRigDOMElementByBeanModel(rigBean);
            try {
                final Item item = itemRepositories.findItemById(rigBean.getObjectId());
                final MetadataRecord metadataRecord = item.getMetadataRecords().get(ESCIDOC);
                metadataRecord.setContent(metaDataContent);
                itemRepositories.update(item.getObjid(), item);
            }
            catch (final EscidocClientException e) {
                LOG.error(e.getLocalizedMessage());
                showError(e.getLocalizedMessage());
            }
            finally {
                this.beanModel = null;
            }
            LOG.info("Rig is successfully saved.");
            showTrayMessage("Success", "Rig is saved.");
        }
    }

    @Override
    public List<InstrumentBean> getNewAvailableInstruments(final List<String> containedInstrumentIDs) {
        synchronized (LOCK_2) {
            final List<InstrumentBean> result = new ArrayList<InstrumentBean>();
            try {
                List<ResourceModel> items = null;
                for (final String cmmId : ELabsCache.getInstrumentCMMIds()) {
                    items = getRepositories().item().findItemsByContentModel(cmmId);
                    for (final ResourceModel itemModel : items) {
                        if (itemModel instanceof ItemProxy) {
                            final ItemProxy itemProxy = (ItemProxy) itemModel;
                            if (!containedInstrumentIDs.contains(itemProxy.getId())) {
                                result.add(loadRelatedInstrumentBeanData(itemProxy));
                            }
                        }
                    }
                }
            }
            catch (final EscidocClientException e) {
                LOG.error(e.getMessage());
            }
            return result;
        }
    }

    @Override
    public boolean hasUpdateAccess() {
        try {
            return getRepositories().pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_ITEM).forResource(
                getResourceProxy().getId()).permitted();
        }
        catch (final UnsupportedOperationException e) {
            showError("Internal error");
            LOG.error(e.getMessage());
            return false;
        }
        catch (final EscidocClientException e) {
            showError("Internal error");
            LOG.error(e.getMessage());
            return false;
        }
        catch (final URISyntaxException e) {
            showError("Internal error");
            LOG.error(e.getMessage());
            return false;
        }
    }

    protected void validateBean(final IBeanModel beanModel) throws EscidocBrowserException {
        Preconditions.checkNotNull(beanModel, "Input is null");
        RigBean rigBean = null;
        try {
            rigBean = (RigBean) beanModel;
        }
        catch (final ClassCastException e) {
            showError("Internal error");
            throw new EscidocBrowserException("Wrong type of model", e);
        }

        if (StringUtils.isEmpty(rigBean.getName()) || StringUtils.isEmpty(rigBean.getDescription())
            || rigBean.getContentList().isEmpty()) {
            showError("Please fill out all of the requried fields!");
            throw new EscidocBrowserException("Some required field is null or the instrument's list is empty");
        }
    }

    @Override
    public void createView() {
        view =
            new RigView(loadBeanData((ItemProxyImpl) getResourceProxy()), this, createBeadCrumbModel(),
                getResourceProxy(), this.serviceLocation);
    }
}
