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
import com.vaadin.ui.Window.Notification;

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.elabsmodul.cache.ELabsCache;
import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.exceptions.EscidocBrowserException;
import org.escidoc.browser.elabsmodul.interfaces.IBeanModel;
import org.escidoc.browser.elabsmodul.interfaces.ISaveAction;
import org.escidoc.browser.elabsmodul.model.FileFormatBean;
import org.escidoc.browser.elabsmodul.model.InstrumentBean;
import org.escidoc.browser.elabsmodul.model.OrgUnitBean;
import org.escidoc.browser.elabsmodul.model.UserBean;
import org.escidoc.browser.elabsmodul.views.InstrumentView;
import org.escidoc.browser.elabsmodul.views.YesNoDialog;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.repository.internal.ItemRepository;
import org.escidoc.browser.repository.internal.OrgUnitService;
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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.oum.OrganizationalUnit;

public final class InstrumentController extends Controller implements ISaveAction {

    private static final Logger LOG = LoggerFactory.getLogger(InstrumentController.class);

    private static final String ESCIDOC = "escidoc";

    private static final String URI_DC = "http://purl.org/dc/elements/1.1/";

    private static final String URI_EL = "http://escidoc.org/ontologies/bw-elabs/re#";

    private static final String URI_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    private final EscidocServiceLocation serviceLocation;

    private final Window mainWindow;

    private final Object LOCK = new Object() {
        // Empty
    };

    private IBeanModel beanModel;

    public InstrumentController(final Repositories repositories, final Router router, final ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        Preconditions.checkArgument(resourceProxy instanceof ItemProxy, "ResourceProxy is not an ItemProxy");
        this.setResourceName(resourceProxy.getName() + "#" + resourceProxy.getId());
        this.serviceLocation = getRouter().getServiceLocation();
        this.mainWindow = getRouter().getMainWindow();
        getOrgUnits();
        getUsers();
        loadAdminDescriptorInfo();
    }

    /**
     * 
     * @param resourceProxy
     *            resource ref
     * @return controlled bean
     * @throws EscidocBrowserException
     *             exception
     */
    private static InstrumentBean loadBeanData(final ResourceProxy resourceProxy) throws EscidocBrowserException {
        final ItemProxy itemProxy = (ItemProxy) resourceProxy;
        final InstrumentBean instrumentBean = new InstrumentBean();
        instrumentBean.setObjectId(itemProxy.getId());
        final Element e = itemProxy.getMedataRecords().get(ESCIDOC).getContent();
        final NodeList nodeList = e.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);
            final String nodeName = node.getLocalName();
            final String nsUri = node.getNamespaceURI();

            if (nodeName == null || nodeName.equals("")) {
                continue;
            }
            else if (nsUri == null || nsUri.equals("")) {
                continue;
            }

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
                instrumentBean.setFolder((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
            }
            else if ("result-mime-type".equals(nodeName) && URI_EL.equals(nsUri)) {
                instrumentBean
                    .setFileFormat((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
            }
            else if ("responsible-person".equals(nodeName) && URI_EL.equals(nsUri)
                && node.getAttributes().getNamedItem("rdf:resource") != null) {
                final String supervisorId = node.getAttributes().getNamedItem("rdf:resource").getNodeValue();
                instrumentBean.setDeviceSupervisor(supervisorId);
            }
            else if ("institution".equals(nodeName) && URI_EL.equals(nsUri)
                && node.getAttributes().getNamedItem("rdf:resource") != null) {
                final String instituteId = node.getAttributes().getNamedItem("rdf:resource").getNodeValue();
                instrumentBean.setInstitute(instituteId);
            }
        }
        return instrumentBean;
    }

    private static Element createInstrumentDOMElementByBeanModel(final InstrumentBean instrumentBean) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setValidating(true);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            final Document doc = builder.newDocument();
            Element instrument = doc.createElementNS(URI_EL, "Instrument");
            instrument.setPrefix("el");
            final Element title = doc.createElementNS(URI_DC, "title");
            title.setPrefix("dc");
            title.setTextContent(instrumentBean.getName());
            instrument.appendChild(title);
            final Element description = doc.createElementNS(URI_DC, "description");
            description.setPrefix("dc");
            description.setTextContent(instrumentBean.getDescription());
            instrument.appendChild(description);
            instrument = createDOMElementWithoutNamespace(doc, instrument, "identity-number", "");
            instrument =
                createDOMElementWithoutNamespace(doc, instrument, "requires-configuration",
                    booleanToHumanReadable(instrumentBean.getConfiguration()));
            instrument =
                createDOMElementWithoutNamespace(doc, instrument, "requires-calibration",
                    booleanToHumanReadable(instrumentBean.getCalibration()));
            instrument =
                createDOMElementWithoutNamespace(doc, instrument, "esync-endpoint", instrumentBean.getESyncDaemon());
            instrument =
                createDOMElementWithoutNamespace(doc, instrument, "monitored-folder", instrumentBean.getFolder());
            instrument =
                createDOMElementWithoutNamespace(doc, instrument, "result-mime-type", instrumentBean.getFileFormat());
            final Element responsiblePerson = doc.createElement("el:responsible-person");
            responsiblePerson.setAttributeNS(URI_RDF, "rdf:resource", instrumentBean.getDeviceSupervisor());
            instrument.appendChild(responsiblePerson);
            final Element institution = doc.createElement("el:institution");
            institution.setAttributeNS(URI_RDF, "rdf:resource", instrumentBean.getInstitute());
            instrument.appendChild(institution);
            return instrument;
        }
        catch (final DOMException e) {
            LOG.error(e.getLocalizedMessage());
        }
        catch (final ParserConfigurationException e) {
            LOG.error(e.getLocalizedMessage());
        }
        return null;
    }

    private static String booleanToHumanReadable(final boolean value) {
        return (value) ? "yes" : "no";
    }

    private static Element createDOMElementWithoutNamespace(
        final Document doc, final Element instrument, final String attributeValue, final String value) {
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
    private void loadAdminDescriptorInfo() {
        ContextProxyImpl context;
        try {
            context =
                (ContextProxyImpl) getRepositories().context().findById(getResourceProxy().getContext().getObjid());
            if (context == null) {
                LOG.error("Context is null");
                showError("Internal error");
                return;
            }

            final Element content = context.getAdminDescription().get("elabs").getContent();
            if (content == null) {
                LOG.error("Context's admin descriptor is null");
                showError("Internal error");
                return;
            }

            final List<String> eSychEndpoints = new ArrayList<String>();
            if (ELabsCache.getEsyncEndpoints().isEmpty()) {
                final NodeList nodeList = content.getElementsByTagName("el:esync-endpoint");
                if (nodeList != null) {
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        final Node node = nodeList.item(i);
                        eSychEndpoints.add(node.getTextContent());
                    }
                }
                synchronized (ELabsCache.getEsyncEndpoints()) {
                    if (!eSychEndpoints.isEmpty()) {
                        ELabsCache.setEsyncEndpoints(Collections.unmodifiableList(eSychEndpoints));
                    }
                }
            }

            if (ELabsCache.getFileFormats().isEmpty()) {
                final List<FileFormatBean> fileFormatList = new ArrayList<FileFormatBean>();
                final String mimeTypeURI = "http://escidoc.org/ontologies/bw-elabs.owl#";
                final NodeList fileFormatNodeList = content.getElementsByTagName("el:FileFormat");
                if (fileFormatNodeList != null) {
                    for (int i = 0; i < fileFormatNodeList.getLength(); i++) {
                        final Node node = fileFormatNodeList.item(i);
                        if (node.hasChildNodes()) {
                            final NodeList interNodeList = node.getChildNodes();
                            final FileFormatBean bean = new FileFormatBean();
                            for (int j = 0; j < interNodeList.getLength(); j++) {
                                final Node formatNode = interNodeList.item(j);
                                final String nodeName = formatNode.getLocalName();
                                final String nsUri = formatNode.getNamespaceURI();

                                if (nodeName == null || nsUri == null) {
                                    continue;
                                }
                                if ("title".equals(nodeName) && URI_DC.equals(nsUri)) {
                                    bean.setTitle(formatNode.getTextContent().trim());
                                }
                                else if ("mime-type".equals(nodeName) && mimeTypeURI.equals(nsUri)) {
                                    bean.setMimeType(formatNode.getTextContent().trim());
                                }
                            }
                            fileFormatList.add(bean);
                            LOG.debug("Added to FileFormat Cache " + bean.getTitle() + " + " + bean.getMimeType());
                        }
                    }
                }
                synchronized (ELabsCache.getFileFormats()) {
                    if (ELabsCache.getFileFormats().isEmpty() && !fileFormatList.isEmpty()) {
                        ELabsCache.setFileFormats(Collections.unmodifiableList(fileFormatList));
                    }
                }
            }

            if (ELabsCache.getEsyncEndpoints().isEmpty()) {
                final List<String> eSycDaemonEndPointUrls = new ArrayList<String>();
                final NodeList nodeList = content.getElementsByTagName("el:esync-endpoint");
                if (nodeList != null) {
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        final Node node = nodeList.item(i);
                        eSycDaemonEndPointUrls.add(node.getTextContent());
                    }
                }
                synchronized (ELabsCache.getDepositEndpoints()) {
                    if (!eSycDaemonEndPointUrls.isEmpty()) {
                        ELabsCache.setEsyncEndpoints(Collections.unmodifiableList(eSycDaemonEndPointUrls));
                    }
                }
            }
        }
        catch (final EscidocClientException e) {
            LOG.debug("Error occurred. Could not load Admin Descriptors" + e.getMessage());
        }
    }

    private List<ResourceModel> createBeadCrumbModel() {
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
        catch (final EscidocClientException e) {
            LOG.error(e.getMessage());
        }
    }

    private void getOrgUnits() {
        if (!ELabsCache.getOrgUnits().isEmpty()) {
            return;
        }
        Collection<OrganizationalUnit> orgUnits = null;
        final List<OrgUnitBean> orgUnitList = new ArrayList<OrgUnitBean>();
        try {
            final OrgUnitService orgUnitService =
                new OrgUnitService(this.serviceLocation.getEscidocUri(), getRouter()
                    .getApp().getCurrentUser().getToken());
            orgUnits = orgUnitService.findAll();
            OrgUnitBean bean = null;
            for (final OrganizationalUnit orgUnitBean : orgUnits) {
                bean = new OrgUnitBean();
                bean.setId(orgUnitBean.getObjid());
                bean.setName(orgUnitBean.getXLinkTitle());
                orgUnitList.add(bean);
            }
            synchronized (ELabsCache.getOrgUnits()) {
                if (!orgUnitList.isEmpty()) {
                    ELabsCache.setOrgUnits(Collections.unmodifiableList(orgUnitList));
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
    }

    @Override
    public void saveAction(final IBeanModel beanModel) {
        Preconditions.checkNotNull(beanModel, "DataBean to store is NULL");
        this.beanModel = beanModel;
        this.mainWindow.addWindow(new YesNoDialog(ELabsViewContants.DIALOG_SAVE_INSTRUMENT_HEADER,
            ELabsViewContants.DIALOG_SAVE_INSTRUMENT_TEXT, new YesNoDialog.Callback() {
                @Override
                public void onDialogResult(final boolean resultIsYes) {
                    if (resultIsYes) {
                        saveModel();
                    }
                    ((InstrumentView) InstrumentController.this.view).hideButtonLayout();
                }
            }));
    }

    /**
     * 
     * @throws EscidocClientException
     */
    private void saveModel() {
        synchronized (LOCK) {
            Preconditions.checkNotNull(this.beanModel, "DataBean to store is NULL");
            final ItemRepository itemRepositories = getRepositories().item();
            try {
                validateBean(this.beanModel);
            }
            catch (final EscidocBrowserException e) {
                LOG.error(e.getMessage());
                return;
            }
            final InstrumentBean instrumentBean = (InstrumentBean) this.beanModel;
            final Element metaDataContent = InstrumentController.createInstrumentDOMElementByBeanModel(instrumentBean);

            try {
                final Item item = itemRepositories.findItemById(instrumentBean.getObjectId());
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
            LOG.info("Instument is successfully saved.");
            showTrayMessage("Success", "Instrument is saved.");
        }
    }

    @Override
    public boolean hasUpdateAccess() {
        try {
            return getRepositories()
                .pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_ITEM).forResource(getResourceProxy().getId())
                .permitted();
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
        InstrumentBean instrumentBean = null;
        try {
            instrumentBean = (InstrumentBean) beanModel;
        }
        catch (final ClassCastException e) {
            showError("Internal error");
            throw new EscidocBrowserException("Wrong type of model", e);
        }

        if (StringUtils.isEmpty(instrumentBean.getName()) || StringUtils.isEmpty(instrumentBean.getDescription())
            || StringUtils.isEmpty(instrumentBean.getESyncDaemon()) || StringUtils.isEmpty(instrumentBean.getFolder())) {
            showError("Please fill out all of the requried fields!");
            throw new EscidocBrowserException("Some required field is null");
        }
    }

    @Override
    public void createView() {
        InstrumentBean instumentBean = null;
        try {
            instumentBean = loadBeanData(getResourceProxy());
        }
        catch (final EscidocBrowserException e) {
            this.mainWindow
                .showNotification(new Notification("Error", e.getMessage(), Notification.TYPE_ERROR_MESSAGE));
            LOG.error(e.getLocalizedMessage());
        }
        view = new InstrumentView(instumentBean, this, createBeadCrumbModel(), getResourceProxy(), serviceLocation);
    }
}
