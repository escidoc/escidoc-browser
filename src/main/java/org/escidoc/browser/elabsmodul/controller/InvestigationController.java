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
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.elabsmodul.cache.ELabsCache;
import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
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
import org.escidoc.browser.model.internal.ContainerProxyImpl;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.repository.internal.ContainerRepository;
import org.escidoc.browser.repository.internal.UserService;
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
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.container.Container;

/**
 * 
 */
public class InvestigationController extends Controller implements IInvestigationAction {

    private static final Logger LOG = LoggerFactory.getLogger(InstrumentController.class);

    private static final String ESCIDOC = "escidoc";

    private static final String URI_DC = "http://purl.org/dc/elements/1.1/";

    private static final String URI_EL = "http://escidoc.org/ontologies/bw-elabs/re#";

    private static final String URI_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    private final EscidocServiceLocation serviceLocation;

    private final Window mainWindow;

    private final ILabsService labsService;

    private IBeanModel beanModel;

    private final Object LOCK_1 = new Object() {
        // empty
    };

    private final Object LOCK_2 = new Object() {
        // empty
    };

    /*
     * (non-Javadoc)
     * 
     * @see org.escidoc.browser.controller.Controller#init(org.escidoc.browser.model.EscidocServiceLocation,
     * org.escidoc.browser.repository.Repositories, org.escidoc.browser.ui.Router,
     * org.escidoc.browser.model.ResourceProxy, com.vaadin.ui.Window, org.escidoc.browser.model.CurrentUser)
     */
    public InvestigationController(final Repositories repositories, final Router router,
        final ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        Preconditions.checkArgument(resourceProxy instanceof ContainerProxy, "ResourceProxy is not a ContainerProxy");
        this.serviceLocation = router.getServiceLocation();
        this.mainWindow = router.getMainWindow();
        this.labsService = new ELabsService(repositories, router, resourceProxy.getId());

        loadAdminDescriptorInfo();
        getUsers();
        createView();
        this.setResourceName(resourceProxy.getName() + "#" + resourceProxy.getId());

    }

    private void loadAdminDescriptorInfo() {

        ContextProxyImpl context;
        final List<String> depositEndPointUrls = new ArrayList<String>();
        try {
            context =
                (ContextProxyImpl) getRepositories().context().findById(getResourceProxy().getContext().getObjid());
            if (context == null) {
                LOG.error("Context is null");
                showError("Internal error");
                return;
            }

            final Element content = context.getAdminDescription().get("elabs").getContent();
            if (content != null) {
                if (ELabsCache.getDepositEndpoints().isEmpty()) {
                    final NodeList nodeList = content.getElementsByTagName("el:deposit-endpoint");
                    if (nodeList != null) {
                        for (int i = 0; i < nodeList.getLength(); i++) {
                            final Node node = nodeList.item(i);
                            depositEndPointUrls.add(node.getTextContent());
                        }
                    }
                    synchronized (ELabsCache.getDepositEndpoints()) {
                        if (!depositEndPointUrls.isEmpty()) {
                            ELabsCache.setDepositEndpoints(Collections.unmodifiableList(depositEndPointUrls));
                        }
                    }
                }
            }
        }
        catch (final EscidocClientException e) {
            LOG.error("Could not load Admin Descriptor 'elabs'. " + e.getMessage(), e);
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
                new UserService(this.serviceLocation.getEscidocUri(), getRouter().getApp().getCurrentUser().getToken());
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

    private InvestigationBean loadBeanData(final ContainerProxy containerProxy) {
        InvestigationBean investigationBean = new InvestigationBean();
        investigationBean.setObjid(containerProxy.getId());
        final Element e = containerProxy.getMedataRecords().get(ESCIDOC).getContent();
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
                    investigationBean.setMaxRuntimeInMin(Integer.valueOf(node.getTextContent()));
                }
                catch (final NumberFormatException nfe) {
                    investigationBean.setMaxRuntimeInMin(0);
                }
            }
            else if ("deposit-endpoint".equals(nodeName) && URI_EL.equals(nsUri)) {
                investigationBean.setDepositEndpoint(node.getTextContent());
            }
            else if ("investigator".equals(nodeName) && URI_EL.equals(nsUri)) {
                final String investigatorId = node.getAttributes().getNamedItemNS(URI_RDF, "resource").getTextContent();
                investigationBean.setInvestigator(investigatorId);
            }
            else if ("rig".equals(nodeName) && URI_EL.equals(nsUri)) {
                final String rigId = node.getAttributes().getNamedItemNS(URI_RDF, "resource").getTextContent();
                if (StringUtils.notEmpty(rigId)) {
                    final RigBean rigBean = new RigBean();
                    rigBean.setObjectId(rigId);

                    try {
                        final Element rigElement =
                            ((ItemProxy) getRepositories().item().findById(rigId))
                                .getMedataRecords().get(ESCIDOC).getContent();

                        if (!(("Rig".equals(rigElement.getLocalName()) && URI_EL.equals(rigElement.getNamespaceURI())) || "el:Rig"
                            .equals(rigElement.getTagName()))) {
                            LOG.error("Container is not an eLabs Rig");
                            return investigationBean;
                        }
                        final NodeList rigNodeList = rigElement.getChildNodes();

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
                    investigationBean.setRigComplexId(rigBean.getComplexId());
                }
            }
            else if ("instrument".equals(nodeName) && URI_EL.equals(nsUri)) {
                final String instrument = node.getAttributes().getNamedItemNS(URI_RDF, "resource").getTextContent();
                final String folder = node.getTextContent().trim();
                investigationBean.getInstrumentFolder().put(instrument, folder);
            }
        }
        return investigationBean;
    }

    /**
     * @param resourceProxy
     * @return
     */
    private RigBean loadRelatedRigBeanData(final ItemProxy rigItem) {
        Preconditions.checkNotNull(rigItem, "Resource is null");
        final RigBean rigBean = new RigBean();
        rigBean.setObjectId(rigItem.getId());

        final NodeList nodeList = rigItem.getMedataRecords().get(ESCIDOC).getContent().getChildNodes();

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

    private List<ResourceModel> createBreadCrumbModel() {
        final ResourceHierarchy rs = new ResourceHierarchy(this.serviceLocation, getRepositories());
        List<ResourceModel> hierarchy = null;
        try {
            hierarchy = rs.getHierarchy(getResourceProxy());
            Collections.reverse(hierarchy);
            hierarchy.add(getResourceProxy());
            return hierarchy;
        }
        catch (final EscidocClientException e) {
            LOG.error("Fatal error, could not load BreadCrumb " + e.getLocalizedMessage());
            showError(e.getLocalizedMessage());
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
        this.beanModel = model;
        this.mainWindow.addWindow(new YesNoDialog(ELabsViewContants.DIALOG_SAVE_INVESTIGATION_HEADER,
            ELabsViewContants.DIALOG_SAVE_INVESTIGATION_TEXT, new YesNoDialog.Callback() {
                @Override
                public void onDialogResult(final boolean resultIsYes) {
                    if (resultIsYes) {
                        saveModel();
                    }
                    ((InvestigationView) InvestigationController.this.view).hideButtonLayout();
                }
            }));
    }

    /**
     * 
     * @throws EscidocClientException
     */
    private void saveModel() {
        synchronized (LOCK_1) {
            Preconditions.checkNotNull(this.beanModel, "Model is NULL. Can not save.");
            final ContainerRepository containerRepository = getRepositories().container();
            try {
                validateBean(this.beanModel);
            }
            catch (final EscidocBrowserException e) {
                LOG.error(e.getMessage());
                return;
            }
            final InvestigationBean investigationBean = (InvestigationBean) this.beanModel;
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
                showError(e.getLocalizedMessage());
            }
            finally {
                this.beanModel = null;
            }
            LOG.info("Investigation is successfully saved.");
            showTrayMessage("Success", "Investigation is saved.");
        }
    }

    public static Element createInvestigationDOMElementByBeanModel(final InvestigationBean investigationBean) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setValidating(true);
        DocumentBuilder builder;
        final String prefixElement = "el";
        final String prefixAttr = "rdf";
        try {
            builder = factory.newDocumentBuilder();
            final Document doc = builder.newDocument();

            final Element investigation = doc.createElementNS(URI_EL, "Investigation");
            investigation.setPrefix(prefixElement);

            final Element title = doc.createElementNS(URI_DC, "title");
            title.setPrefix("dc");
            title.setTextContent(investigationBean.getName());
            investigation.appendChild(title);

            final Element description = doc.createElementNS(URI_DC, "description");
            description.setPrefix("dc");
            description.setTextContent(investigationBean.getDescription());
            investigation.appendChild(description);

            final Element maxRuntime = doc.createElementNS(URI_EL, "max-runtime");
            maxRuntime.setPrefix(prefixElement);
            maxRuntime.setTextContent("" + investigationBean.getMaxRuntimeInMin());
            investigation.appendChild(maxRuntime);

            final Element depositEndpoint = doc.createElementNS(URI_EL, "deposit-endpoint");
            depositEndpoint.setPrefix(prefixElement);
            depositEndpoint.setTextContent(investigationBean.getDepositEndpoint());
            investigation.appendChild(depositEndpoint);

            final Element investigator = doc.createElementNS(URI_EL, "investigator");
            investigator.setPrefix(prefixElement);
            investigator.setAttributeNS(URI_RDF, prefixAttr + ":resource", investigationBean.getInvestigator());
            investigation.appendChild(investigator);

            final Element rig = doc.createElementNS(URI_EL, "rig");
            rig.setPrefix(prefixElement);
            rig.setAttributeNS(URI_RDF, prefixAttr + ":resource", investigationBean.getRigBean().getObjectId());
            investigation.appendChild(rig);

            for (final Entry<String, String> instrumentFolder : investigationBean.getInstrumentFolder().entrySet()) {
                final Element instrument = doc.createElementNS(URI_EL, "instrument");
                instrument.setPrefix(prefixElement);
                instrument.setAttributeNS(URI_RDF, prefixAttr + ":resource", instrumentFolder.getKey());

                final Element folder = doc.createElementNS(URI_EL, "monitored-folder");
                folder.setPrefix(prefixElement);
                folder.setTextContent(instrumentFolder.getValue());
                instrument.appendChild(folder);
                investigation.appendChild(instrument);
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
            return getRepositories()
                .pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_CONTAINER)
                .forResource(getResourceProxy().getId()).permitted();
        }
        catch (final UnsupportedOperationException e) {
            showError("Inernal error");
            LOG.error(e.getMessage());
            return false;
        }
        catch (final EscidocClientException e) {
            showError("Inernal error");
            LOG.error(e.getMessage());
            return false;
        }
        catch (final URISyntaxException e) {
            showError("Inernal error");
            LOG.error(e.getMessage());
            return false;
        }
    }

    @Override
    public List<RigBean> getAvailableRigs() {
        synchronized (LOCK_2) {
            final List<RigBean> result = new ArrayList<RigBean>();
            try {
                List<ResourceModel> items = null;
                for (final String cmmId : ELabsCache.getRigCMMIds()) {
                    items = getRepositories().item().findItemsByContentModel(cmmId);
                    for (final ResourceModel itemModel : items) {
                        if (itemModel instanceof ItemProxy) {
                            final ItemProxy itemProxy = (ItemProxy) itemModel;
                            result.add(loadRelatedRigBeanData(itemProxy));
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

    private String setDurationLabel(final String storedDuration) {
        try {
            int minute = Integer.valueOf(storedDuration).intValue(), day = 0, hour = 0;
            day = minute / 1440;
            hour = (minute - day * 1440) / 60;
            minute = (minute - day * 1440 - hour * 60);

            final StringBuilder sb = new StringBuilder();
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
            return sb.toString();
        }
        catch (final NumberFormatException e) {
            showError("Internal error");
            LOG.debug(e.getMessage());
            return null;
        }
    }

    /**
     * @return the labsService
     */
    @Override
    public ILabsService getLabsService() {
        return this.labsService;
    }

    protected void validateBean(final IBeanModel beanModel) throws EscidocBrowserException {
        Preconditions.checkNotNull(beanModel, "Input is null");
        InvestigationBean investigationBean = null;
        try {
            investigationBean = (InvestigationBean) beanModel;
        }
        catch (final ClassCastException e) {
            showError("Internal error");
            throw new EscidocBrowserException("Wrong type of model", e);
        }

        if (StringUtils.isEmpty(investigationBean.getName()) || StringUtils.isEmpty(investigationBean.getDescription())
            || StringUtils.isEmpty(investigationBean.getDepositEndpoint()) || investigationBean.getRigBean() == null
            || investigationBean.getMaxRuntimeInMin() == 0) {
            showError("Please fill out all of the requried fields!");
            throw new EscidocBrowserException("Some required field is null");
        }
    }

    @Override
    public void createView() {
        view =
            new InvestigationView(loadBeanData((ContainerProxyImpl) getResourceProxy()), this, createBreadCrumbModel(),
                (ContainerProxyImpl) getResourceProxy(), getRouter());
    }
}
