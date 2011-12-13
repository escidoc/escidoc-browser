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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import org.escidoc.browser.model.OrgUnitService;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.UserService;
import org.escidoc.browser.model.internal.ContextProxyImpl;
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
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.oum.OrganizationalUnit;

/**
 * 
 */
public final class InstrumentController extends Controller implements ISaveAction {

    private static Logger LOG = LoggerFactory.getLogger(InstrumentController.class);

    private Repositories repositories;

    private EscidocServiceLocation serviceLocation;

    private ResourceProxy resourceProxy;

    private Window mainWindow;

    private IBeanModel beanModel;

    private Router router;

    @Override
    public void init(final Repositories repositories, final Router router, final ResourceProxy resourceProxy) {
        Preconditions.checkNotNull(repositories, "Repository ref is null");
        Preconditions.checkNotNull(router, "Router ref is null");
        this.router = router;

        this.serviceLocation = router.getServiceLocation();
        this.resourceProxy = resourceProxy;
        this.repositories = repositories;
        this.mainWindow = router.getMainWindow();
        getOrgUnits();
        getUsers();
        loadAdminDescriptorInfo();
        view = createView(resourceProxy);
        this.setResourceName(resourceProxy.getName() + "#" + resourceProxy.getId());
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

        final Element e = itemProxy.getMedataRecords().get("escidoc").getContent();
        final NodeList nodeList = e.getChildNodes();

        instrumentBean.setObjectId(itemProxy.getId());

        final String URI_DC = "http://purl.org/dc/elements/1.1/";
        final String URI_EL = "http://escidoc.org/ontologies/bw-elabs/re#";
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

    private synchronized static Element createInstrumentDOMElementByBeanModel(final InstrumentBean instrumentBean) {

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setValidating(true);
        DocumentBuilder builder;

        final String URI_DC = "http://purl.org/dc/elements/1.1/";
        final String URI_EL = "http://escidoc.org/ontologies/bw-elabs/re#";
        final String URI_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

        try {
            builder = factory.newDocumentBuilder();
            final Document doc = builder.newDocument();

            Element instrument = doc.createElementNS(URI_EL, "Instrument");
            instrument.setPrefix("el");

            // e.g. <dc:title xmlns:dc="http://purl.org/dc/elements/1.1/">FRS
            // Instrument 01</dc:title>
            final Element title = doc.createElementNS(URI_DC, "title");
            title.setPrefix("dc");
            title.setTextContent(instrumentBean.getName());
            instrument.appendChild(title);

            // e.g. <dc:description
            // xmlns:dc="http://purl.org/dc/elements/1.1/">A
            // description.</dc:description>
            final Element description = doc.createElementNS(URI_DC, "description");
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
            responsiblePerson.setAttributeNS(URI_RDF, "rdf:resource", instrumentBean.getDeviceSupervisor());
            instrument.appendChild(responsiblePerson);

            // <el:institution
            // xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
            // rdf:resource="escidoc:1001"></el:institution>
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
            context = (ContextProxyImpl) repositories.context().findById(resourceProxy.getContext().getObjid());
            final Element content = context.getAdminDescription().get("elabs").getContent();

            List<String> eSychEndpoints = new ArrayList<String>();

            if (ELabsCache.getEsyncEndpoints().isEmpty()) {
                final NodeList nodeList = content.getElementsByTagName("el:esync-endpoint");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    final Node node = nodeList.item(i);
                    eSychEndpoints.add(node.getTextContent());
                }
                synchronized (ELabsCache.getEsyncEndpoints()) {
                    if (!eSychEndpoints.isEmpty()) {
                        ELabsCache.setEsyncEndpoints(Collections.unmodifiableList(eSychEndpoints));
                    }
                }
            }

            if (ELabsCache.getFileFormats().isEmpty()) {
                List<FileFormatBean> fileFormatList = new ArrayList<FileFormatBean>();
                final String URI_DC = "http://purl.org/dc/elements/1.1/";
                final String URI_EL = "http://escidoc.org/ontologies/bw-elabs.owl#";
                final NodeList fileFormatNodeList = content.getElementsByTagName("el:FileFormat");
                for (int i = 0; i < fileFormatNodeList.getLength(); i++) {
                    final Node node = fileFormatNodeList.item(i);
                    if (node.hasChildNodes()) {
                        final NodeList interNodeList = node.getChildNodes();
                        FileFormatBean bean = new FileFormatBean();
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
                            else if ("mime-type".equals(nodeName) && URI_EL.equals(nsUri)) {
                                bean.setMimeType(formatNode.getTextContent().trim());
                            }
                        }
                        fileFormatList.add(bean);
                        LOG.debug("Added to FileFormat Cache " + bean.getTitle() + " + " + bean.getMimeType());
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
                try {

                    final NodeList nodeList = content.getElementsByTagName("el:esync-endpoint");
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        final Node node = nodeList.item(i);
                        eSycDaemonEndPointUrls.add(node.getTextContent());
                    }
                    synchronized (ELabsCache.getDepositEndpoints()) {
                        if (!eSycDaemonEndPointUrls.isEmpty()) {
                            ELabsCache.setEsyncEndpoints(Collections.unmodifiableList(eSycDaemonEndPointUrls));
                        }
                    }
                }
                catch (final NullPointerException e) {
                    LOG.debug("Admin Description is null in the context " + resourceProxy.getContext().getObjid());
                }
            }

        }
        catch (final EscidocClientException e) {
            LOG.debug("Error occurred. Could not load Admin Descriptors" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        catch (final NullPointerException e) {
            LOG.debug("Admin Description is null in the context " + resourceProxy.getContext().getObjid());
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
        return new InstrumentView(instumentBean, this, createBeadCrumbModel(), resourceProxy, serviceLocation);
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
        catch (final EscidocClientException e) {
            LOG.error("Fatal error, could not load BreadCrumb " + e.getLocalizedMessage());
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

    private void getOrgUnits() {
        if (!ELabsCache.getOrgUnits().isEmpty()) {
            return;
        }
        Collection<OrganizationalUnit> orgUnits = null;
        final List<OrgUnitBean> orgUnitList = new ArrayList<OrgUnitBean>();
        try {
            final OrgUnitService orgUnitService =
                new OrgUnitService(serviceLocation.getEscidocUri(), router.getApp().getCurrentUser().getToken());
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

        mainWindow.addWindow(new YesNoDialog(ELabsViewContants.DIALOG_SAVEINSTRUMENT_HEADER,
            ELabsViewContants.DIALOG_SAVEINSTRUMENT_TEXT, new YesNoDialog.Callback() {

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
                    ((InstrumentView) InstrumentController.this.view).hideButtonLayout();
                }
            }));
    }

    /**
     * 
     * @throws EscidocClientException
     */
    private synchronized void saveModel() throws EscidocClientException {
        Preconditions.checkNotNull(beanModel, "DataBean to store is NULL");
        final ItemRepository itemRepositories = repositories.item();
        final String ESCIDOC = "escidoc";

        if (!(beanModel instanceof InstrumentBean)) {
            return;
        }

        final InstrumentBean instrumentBean = (InstrumentBean) beanModel;
        final Element metaDataContent = InstrumentController.createInstrumentDOMElementByBeanModel(instrumentBean);

        try {
            final Item item = itemRepositories.findItemById(instrumentBean.getObjectId());
            final MetadataRecord metadataRecord = item.getMetadataRecords().get(ESCIDOC);
            metadataRecord.setContent(metaDataContent);
            itemRepositories.update(item.getObjid(), item);
        }
        catch (final EscidocClientException e) {
            LOG.error(e.getLocalizedMessage());
            // TODO show error message to user
        }
        finally {
            beanModel = null;
        }
        LOG.info("Instument is successfully saved.");
    }

    @Override
    public boolean hasUpdateAccess() {
        try {
            return repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_ITEM).forResource(resourceProxy.getId())
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
    public String checkBeanDataBeforeSave(IBeanModel dataBean) {
        // TODO Auto-generated method stub
        return null;
    }
}
