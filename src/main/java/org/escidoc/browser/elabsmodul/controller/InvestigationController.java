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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.elabsmodul.cache.ELabsCache;
import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.controller.utils.DOM2String;
import org.escidoc.browser.elabsmodul.exceptions.EscidocBrowserException;
import org.escidoc.browser.elabsmodul.interfaces.IBeanModel;
import org.escidoc.browser.elabsmodul.interfaces.IInvestigationAction;
import org.escidoc.browser.elabsmodul.interfaces.ILabsService;
import org.escidoc.browser.elabsmodul.model.InstrumentBean;
import org.escidoc.browser.elabsmodul.model.InvestigationBean;
import org.escidoc.browser.elabsmodul.model.RigBean;
import org.escidoc.browser.elabsmodul.model.UserBean;
import org.escidoc.browser.elabsmodul.service.ELabsService;
import org.escidoc.browser.elabsmodul.views.InvestigationView;
import org.escidoc.browser.elabsmodul.views.YesNoDialog;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.UserService;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.repository.internal.ContainerProxyImpl;
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
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.container.Container;

/**
 * @author frs
 * 
 */
public class InvestigationController extends Controller implements IInvestigationAction {

    private static Logger LOG = LoggerFactory.getLogger(InstrumentController.class);

    // FIXME move to Repositories
    private EscidocServiceLocation serviceLocation;

    private Repositories repositories;

    private Router router;

    private ResourceProxy resourceProxy;

    private IBeanModel model;

    private ILabsService labsService;

    private static Window mainWindow;

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.controller.Controller#init(org.escidoc.browser.model.EscidocServiceLocation,
     * org.escidoc.browser.repository.Repositories, org.escidoc.browser.ui.Router,
     * org.escidoc.browser.model.ResourceProxy, com.vaadin.ui.Window, org.escidoc.browser.model.CurrentUser)
     */
    @Override
    public void init(final Repositories repositories, final Router router, final ResourceProxy resourceProxy) {
        Preconditions.checkNotNull(repositories, "Repository ref is null");
        Preconditions.checkNotNull(resourceProxy, "ResourceProxy ref is null");
        this.router = router;
        this.serviceLocation = router.getServiceLocation();
        this.repositories = repositories;
        this.resourceProxy = resourceProxy;
        this.mainWindow = router.getMainWindow();
        this.labsService = new ELabsService(repositories, router, resourceProxy.getId());

        loadAdminDescriptorInfo();
        getUsers();
        view = createView(resourceProxy);
        this.setResourceName(resourceProxy.getName() + "#" + resourceProxy.getId());

    }

    private void loadAdminDescriptorInfo() {

        ContextProxyImpl context;
        final List<String> depositEndPointUrls = new ArrayList<String>();
        try {
            context = (ContextProxyImpl) repositories.context().findById(resourceProxy.getContext().getObjid());
            final Element content = context.getAdminDescription().get("elabs").getContent();

            if (ELabsCache.getDepositEndpoints().isEmpty()) {
                final NodeList nodeList = content.getElementsByTagName("el:deposit-endpoint");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    final Node node = nodeList.item(i);
                    depositEndPointUrls.add(node.getTextContent());
                }
                synchronized (ELabsCache.getDepositEndpoints()) {
                    if (!depositEndPointUrls.isEmpty()) {
                        ELabsCache.setDepositEndpoints(Collections.unmodifiableList(depositEndPointUrls));
                    }
                }
            }

        }
        catch (final EscidocClientException e) {
            LOG.error("Could not load Admin Descriptor 'elabs'. " + e.getMessage(), e);
        }
        catch (final NullPointerException e) {
            LOG.debug("Admin Description is null in the context " + resourceProxy.getContext().getObjid());
        }
    }

    private void getUsers() {

        if (!ELabsCache.getUsers().isEmpty()) {
            return;
        }
        final List<UserBean> userAccountList = new ArrayList<UserBean>();
        Collection<UserAccount> userAccounts = null;
        try {
            final UserService userService =
                new UserService(serviceLocation.getEscidocUri(), router.getApp().getCurrentUser().getToken());
            userAccounts = userService.findAll();
            UserBean bean = null;
            for (final UserAccount account : userAccounts) {
                bean = new UserBean();
                bean.setId(account.getObjid());
                bean.setName(account.getXLinkTitle());
                userAccountList.add(bean);
            }

            synchronized (ELabsCache.getUsers()) {
                if (!userAccountList.isEmpty()) {
                    ELabsCache.setUsers(Collections.unmodifiableList(userAccountList));
                }
            }
        }
        catch (final InternalClientException e) {
            LOG.error(e.getMessage());
        }
        catch (final EscidocException e) {
            LOG.error(e.getMessage());
        }
        catch (final TransportException e) {
            LOG.error(e.getMessage());
        }
        catch (final EscidocClientException e) {
            LOG.error(e.getMessage());
        }
    }

    private Component createView(final ResourceProxy resourceProxy) {
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

        final Component investigationView =
            new InvestigationView(investigationBean, this, breadCrumbModel, containerProxy, router);
        return investigationView;
    }

    private InvestigationBean loadBeanData(final ContainerProxy containerProxy) throws EscidocBrowserException {
        final String URI_DC = "http://purl.org/dc/elements/1.1/";
        final String URI_EL = "http://escidoc.org/ontologies/bw-elabs/re#";
        final String URI_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

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

            if (!(("Investigation".equals(e.getLocalName()) && URI_EL.equals(e.getNamespaceURI())) || "el:Investigation"
                .equals(e.getTagName()))) {
                LOG.error("Container is not an eLabs Investigation");
                return investigationBean;
            }

            final NodeList nodeList = e.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                final String nodeName = node.getLocalName();
                final String nsUri = node.getNamespaceURI();

                if (nodeName == null || nsUri == null) {
                    continue;
                }

                if ("title".equals(nodeName) && URI_DC.equals(nsUri)) {
                    investigationBean.setName(node.getTextContent());
                }
                else if ("description".equals(nodeName) && URI_DC.equals(nsUri)) {
                    investigationBean.setDescription(node.getTextContent());
                }
                else if ("max-runtime".equals(nodeName) && URI_EL.equals(nsUri)) {
                    investigationBean.setMaxRuntime(setDurationLabel(node.getTextContent()));
                    try {
                        investigationBean.setMaxRuntimeInMin(new Integer(node.getTextContent()));
                    }
                    catch (NumberFormatException nfe) {
                        investigationBean.setMaxRuntimeInMin(0);
                    }
                }
                else if ("deposit-endpoint".equals(nodeName) && URI_EL.equals(nsUri)) {
                    investigationBean.setDepositEndpoint(node.getTextContent());
                }
                else if ("investigator".equals(nodeName) && URI_EL.equals(nsUri)) {
                    final String investigatorId =
                        node.getAttributes().getNamedItemNS(URI_RDF, "resource").getTextContent();
                    investigationBean.setInvestigator(investigatorId);
                }
                else if ("rig".equals(nodeName) && URI_EL.equals(nsUri)) {
                    final String rigId = node.getAttributes().getNamedItemNS(URI_RDF, "resource").getTextContent();
                    if (StringUtils.notEmpty(rigId)) {
                        final RigBean rigBean = new RigBean();

                        try {
                            final Element rigElement =
                                ((ItemProxy) repositories.item().findById(rigId))
                                    .getMedataRecords().get("escidoc").getContent();

                            if (!(("Rig".equals(rigElement.getLocalName()) && URI_EL.equals(rigElement
                                .getNamespaceURI())) || "el:Rig".equals(rigElement.getTagName()))) {
                                LOG.error("Container is not an eLabs Rig");
                                return investigationBean;
                            }

                            final NodeList rigNodeList = rigElement.getChildNodes();

                            rigBean.setObjectId(rigId);
                            for (int j = 0; j < rigNodeList.getLength(); j++) {
                                final Node rigNode = rigNodeList.item(j);
                                final String rigNodeName = rigNode.getLocalName();
                                final String rigNsUri = rigNode.getNamespaceURI();

                                if (rigNodeName == null || rigNsUri == null) {
                                    continue;
                                }

                                if ("title".equals(rigNodeName) && URI_DC.equals(rigNsUri)) {
                                    rigBean.setName((rigNode.getFirstChild() != null) ? rigNode
                                        .getFirstChild().getNodeValue() : null);
                                }

                                else if ("description".equals(rigNodeName) && URI_DC.equals(rigNsUri)) {
                                    rigBean.setDescription((rigNode.getFirstChild() != null) ? rigNode
                                        .getFirstChild().getNodeValue() : null);
                                }
                            }
                        }
                        catch (final Exception ex) {
                            LOG.error(ex.getMessage());
                        }
                        investigationBean.setRigBean(rigBean);

                        if (rigBean != null) {
                            investigationBean.setRigComplexId(rigBean.getComplexId());
                        }
                    }
                }
                else if ("instrument".equals(nodeName) && URI_EL.equals(nsUri)) {
                    final String instrument = node.getAttributes().getNamedItemNS(URI_RDF, "resource").getTextContent();
                    final String folder = node.getTextContent().trim();
                    investigationBean.getInstrumentFolder().put(instrument, folder);
                }
            }
        }
        catch (final TransformerException e) {
            LOG.error(e.getLocalizedMessage());
        }

        return investigationBean;
    }

    /**
     * @param resourceProxy
     * @return
     */
    private synchronized RigBean loadRelatedRigBeanData(final ItemProxy rigItem) {
        Preconditions.checkNotNull(rigItem, "Resource is null");

        final RigBean rigBean = new RigBean();
        final String URI_DC = "http://purl.org/dc/elements/1.1/";
        final String URI_EL = "http://escidoc.org/ontologies/bw-elabs/re#";
        final String URI_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

        final NodeList nodeList = rigItem.getMedataRecords().get("escidoc").getContent().getChildNodes();

        rigBean.setObjectId(rigItem.getId());
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
                        final ItemProxy instrumentProxy = (ItemProxy) repositories.item().findById(instrumentID);
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

    private List<ResourceModel> createBreadCrumbModel() {
        final ResourceHierarchy rs = new ResourceHierarchy(serviceLocation, repositories);
        List<ResourceModel> hierarchy = null;
        try {
            hierarchy = rs.getHierarchy(resourceProxy);
            Collections.reverse(hierarchy);
            hierarchy.add(resourceProxy);
            return hierarchy;
        }
        catch (final EscidocClientException e) {
            LOG.error("Fatal error, could not load BreadCrumb " + e.getLocalizedMessage());
        }
        return Collections.emptyList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.escidoc.browser.elabsmodul.interfaces.ISaveAction#saveAction(org.escidoc.browser.elabsmodul.interfaces.IBeanModel
     * )
     */
    @Override
    public void saveAction(final IBeanModel model) {
        Preconditions.checkNotNull(model, "Model is null.");
        this.model = model;

        mainWindow.addWindow(new YesNoDialog(ELabsViewContants.DIALOG_SAVE_INVESTIGATION_HEADER,
            ELabsViewContants.DIALOG_SAVE_INVESTIGATION_TEXT, new YesNoDialog.Callback() {

                @Override
                public void onDialogResult(final boolean resultIsYes) {
                    if (resultIsYes) {
                        try {
                            saveModel();
                        }
                        catch (final EscidocClientException e) {
                            LOG.error(e.getMessage());
                            mainWindow.showNotification("Error", e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
                        }
                    }
                    ((InvestigationView) InvestigationController.this.view).hideButtonLayout();
                }
            }));
    }

    /**
     * 
     * @throws EscidocClientException
     */
    private synchronized void saveModel() throws EscidocClientException {
        Preconditions.checkNotNull(model, "Model is NULL. Can not save.");
        final ContainerRepository containerRepository = repositories.container();
        final String ESCIDOC = "escidoc";

        if (!(model instanceof InvestigationBean)) {
            return;
        }

        final InvestigationBean investigationBean = (InvestigationBean) model;
        // The first try checks if the bean can be created Correctly. Maybe the RIG selection is empty
        try {
            final Element metaDataContent =
                InvestigationController.createInvestigationDOMElementByBeanModel(investigationBean);
            try {
                final Container container = containerRepository.findContainerById(investigationBean.getObjid());
                final MetadataRecord metadataRecord = container.getMetadataRecords().get(ESCIDOC);
                metadataRecord.setContent(metaDataContent);
                containerRepository.update(container);
            }
            catch (final EscidocClientException e) {
                LOG.error(e.getLocalizedMessage());
                this.mainWindow.showNotification("Error", "Element not found!", Notification.TYPE_ERROR_MESSAGE);
            }
            finally {
                model = null;
            }
            LOG.info("Investigation is successfully saved.");
        }
        catch (final Exception e) {
            mainWindow.showNotification(ELabsViewContants.ERROR_INVESTIGATION_VIEW_NO_RIG_SELECTED,
                Window.Notification.TYPE_WARNING_MESSAGE);
        }

    }

    public synchronized static Element createInvestigationDOMElementByBeanModel(
        final InvestigationBean investigationBean) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setValidating(true);
        DocumentBuilder builder;
        final String NSURI_ELABS_RE = "http://escidoc.org/ontologies/bw-elabs/re#";
        final String NSURI_ELABS_DC = "http://purl.org/dc/elements/1.1/";
        final String NSPREFIX_ELABS_RE = "el";
        final String NSURI_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
        final String NSPREFIX_RDF = "rdf";
        try {
            builder = factory.newDocumentBuilder();
            final Document doc = builder.newDocument();

            final Element investigation = doc.createElementNS(NSURI_ELABS_RE, "Investigation");
            investigation.setPrefix(NSPREFIX_ELABS_RE);

            final Element title = doc.createElementNS(NSURI_ELABS_DC, "title");
            title.setPrefix("dc");
            title.setTextContent(investigationBean.getName());
            investigation.appendChild(title);

            final Element description = doc.createElementNS(NSURI_ELABS_DC, "description");
            description.setPrefix("dc");
            description.setTextContent(investigationBean.getDescription());
            investigation.appendChild(description);

            final Element maxRuntime = doc.createElementNS(NSURI_ELABS_RE, "max-runtime");
            maxRuntime.setPrefix(NSPREFIX_ELABS_RE);
            maxRuntime.setTextContent("" + investigationBean.getMaxRuntimeInMin());
            investigation.appendChild(maxRuntime);

            final Element depositEndpoint = doc.createElementNS(NSURI_ELABS_RE, "deposit-endpoint");
            depositEndpoint.setPrefix(NSPREFIX_ELABS_RE);
            depositEndpoint.setTextContent(investigationBean.getDepositEndpoint());
            investigation.appendChild(depositEndpoint);

            final Element investigator = doc.createElementNS(NSURI_ELABS_RE, "investigator");
            investigator.setPrefix(NSPREFIX_ELABS_RE);
            investigator.setAttributeNS(NSURI_RDF, NSPREFIX_RDF + ":resource", investigationBean.getInvestigator());
            investigation.appendChild(investigator);

            final Element rig = doc.createElementNS(NSURI_ELABS_RE, "rig");
            rig.setPrefix(NSPREFIX_ELABS_RE);
            rig.setAttributeNS(NSURI_RDF, NSPREFIX_RDF + ":resource", investigationBean.getRigBean().getObjectId());
            investigation.appendChild(rig);

            for (final Entry<String, String> instrumentFolder : investigationBean.getInstrumentFolder().entrySet()) {
                final Element instrument = doc.createElementNS(NSURI_ELABS_RE, "instrument");
                instrument.setPrefix(NSPREFIX_ELABS_RE);
                instrument.setAttributeNS(NSURI_RDF, NSPREFIX_RDF + ":resource", instrumentFolder.getKey());

                final Element folder = doc.createElementNS(NSURI_ELABS_RE, "monitored-folder");
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
                catch (final TransformerException e) {
                    e.printStackTrace();
                }
                LOG.debug(xml);
            }
            return investigation;
        }
        catch (final DOMException e) {
            LOG.error(e.getLocalizedMessage());
        }
        catch (final ParserConfigurationException e) {
            LOG.error(e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public boolean hasUpdateAccess() {
        try {
            return repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_CONTAINER).forResource(resourceProxy.getId())
                .permitted();
        }
        catch (final UnsupportedOperationException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            LOG.error(e.getMessage());
            return false;
        }
        catch (final EscidocClientException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            LOG.error(e.getMessage());
            return false;
        }
        catch (final URISyntaxException e) {
            mainWindow.showNotification(e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            LOG.error(e.getMessage());
            return false;
        }
    }

    @Override
    public synchronized List<RigBean> getAvailableRigs() {
        final List<RigBean> result = new ArrayList<RigBean>();

        try {
            List<ResourceModel> items = null;
            for (Iterator<String> iterator = ELabsCache.getRigCMMIds().iterator(); iterator.hasNext();) {
                String cmmId = iterator.next();
                items = repositories.item().findItemsByContentModel(cmmId);
                for (Iterator<ResourceModel> iterator2 = items.iterator(); iterator2.hasNext();) {
                    ResourceModel itemModel = iterator2.next();
                    if (itemModel instanceof ItemProxy) {
                        ItemProxy itemProxy = (ItemProxy) itemModel;
                        result.add(loadRelatedRigBeanData(itemProxy));
                    }
                }
            }
        }
        catch (EscidocClientException e) {
            LOG.error(e.getMessage());
        }

        return result;
    }

    private String setDurationLabel(String storedDuration) {

        try {
            int minute = new Integer(storedDuration).intValue(), day = 0, hour = 0;

            day = minute / 1440;
            hour = (minute - day * 1440) / 60;
            minute = (minute - day * 1440 - hour * 60);

            StringBuilder sb = new StringBuilder();
            if (day != 0) {
                sb.append(day);
                sb.append((day == 1) ? " day " : " days ");
            }
            if (hour != 0) {
                sb.append(hour);
                sb.append((hour == 1) ? " hour " : " hours ");
            }
            sb.append(minute);
            sb.append((minute == 0 || minute == 1) ? " minute" : " minutes");

            LOG.debug("setDurationLabel: " + day + "|" + hour + "|" + minute);

            return sb.toString();
        }
        catch (NumberFormatException e) {
            LOG.debug(e.getMessage());
            return null;
        }

    }

    /**
     * @return the labsService
     */
    @Override
    public ILabsService getLabsService() {
        return labsService;
    }
}
