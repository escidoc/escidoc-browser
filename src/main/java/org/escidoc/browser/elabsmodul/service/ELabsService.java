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
package org.escidoc.browser.elabsmodul.service;

import com.google.common.base.Preconditions;

import com.vaadin.ui.Window.Notification;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.escidoc.browser.elabsmodul.cache.ELabsCache;
import org.escidoc.browser.elabsmodul.constants.ELabsServiceConstants;
import org.escidoc.browser.elabsmodul.exceptions.EscidocBrowserException;
import org.escidoc.browser.elabsmodul.interfaces.ILabsService;
import org.escidoc.browser.elabsmodul.model.InstrumentBean;
import org.escidoc.browser.elabsmodul.model.InvestigationBean;
import org.escidoc.browser.elabsmodul.model.RigBean;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ELabsService implements ILabsService {

    // FIXME read email from user attribute a
    private static final String FIZ_KARLSRUHE_DE = "@fiz-karlsruhe.de";

    private final String investigationId;

    private final Repositories repositories;

    private final Router router;

    private static final Logger LOG = LoggerFactory.getLogger(ELabsService.class);

    public ELabsService(final Repositories repositories, final Router router, final String investigationId) {
        Preconditions.checkNotNull(investigationId, "Investigation ID is null");
        Preconditions.checkNotNull(repositories, "Repository is null");
        Preconditions.checkNotNull(router, "Router is null");
        this.investigationId = investigationId;
        this.repositories = repositories;
        this.router = router;
    }

    private List<Map<String, String>> gatherConfigurationDataFromInfrastructure() throws EscidocBrowserException {
        final List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        Map<String, String> configurationMap;
        ContainerProxy investigationProxy = null;
        final StringBuilder errorStrings = new StringBuilder();
        final StringBuilder warningStrings = new StringBuilder();

        try {
            investigationProxy = (ContainerProxy) repositories.container().findById(investigationId);
            if (investigationProxy == null) {
                LOG.error("InvestigationProxy is null");
                showError("Internal error: Investigation is not found");
                return null;
            }
        }
        catch (final EscidocClientException e) {
            LOG.error(e.getMessage());
            showError("Internal error: Exceptionfound");
        }
        catch (final ClassCastException e) {
            LOG.error(e.getMessage());
        }

        InvestigationBean investigationBean = resolveInvestigation(investigationProxy);
        if (investigationBean == null) {
            LOG.error("InvestigationBean is null");
            showError("Internal error");
            return null;
        }

        if (investigationBean.getRigBean() == null) {
            LOG.error("RigBean is null");
            showError("Internal error");
            return null;
        }

        ItemProxy rigProxy = null;
        RigBean rigBean = null;
        try {
            rigProxy = (ItemProxy) repositories.item().findById(investigationBean.getRigBean().getObjectId());
            if (rigProxy == null) {
                LOG.error("RigProxy is null");
                showError("Internal error");
                return null;
            }
            rigBean = resolveRig(rigProxy);
        }
        catch (final EscidocClientException e) {
            LOG.error(e.getMessage());
            showError("Internal error");
            return null;
        }
        catch (final ClassCastException e) {
            LOG.error(e.getMessage());
            showError("Internal error");
            return null;
        }

        if (rigBean == null || rigBean.getContentList().isEmpty()) {
            final String error = "Rig is not set or does not contain any instruments at all";
            LOG.error(error);
            errorStrings.append(error + "<br/>");
        }

        final String workspaceId = investigationProxy.getContext().getObjid();
        if (workspaceId == null || workspaceId.isEmpty()) {
            final String error = "Contenxt id is not available";
            LOG.error(error);
            errorStrings.append(error + "<br/>");
        }

        final String userHandle = router.getApp().getCurrentUser().getToken();
        if (userHandle == null || userHandle.isEmpty()) {
            final String error = "Usertoken is not available";
            LOG.error(error);
            errorStrings.append(error + "<br/>");
        }

        final String infrastructureEndpoint = router.getServiceLocation().getEscidocUri();
        if (infrastructureEndpoint == null || infrastructureEndpoint.isEmpty()) {
            final String error = "Infrastructure's endpoint is not set";
            LOG.error(error);
            errorStrings.append(error + "<br/>");
        }

        final String contentModelId = ELabsCache.getGeneratedItemCMMId().get(0);
        if (contentModelId == null || contentModelId.isEmpty()) {
            final String error = "Contentmodel's id is not available";
            LOG.error(error);
            errorStrings.append(error + "<br/>");
        }

        final String checkSumType = "MD5";
        final String experimentId = investigationBean.getObjid();
        final String experimentTitle = investigationBean.getName();
        final String experimentDescr = investigationBean.getDescription();

        if (experimentId == null || experimentId.isEmpty()) {
            final String error = "Experiment's id is not available";
            LOG.error(error);
            errorStrings.append(error + "<br/>");
        }

        if (experimentTitle == null || experimentTitle.isEmpty()) {
            final String error = "Experiment's title is not set";
            LOG.error(error);
            errorStrings.append(error + "<br/>");
        }

        if (experimentDescr == null || experimentDescr.isEmpty()) {
            final String error = "Experiment's description is not set";
            LOG.warn(error);
            warningStrings.append(error + "<br/>");
        }

        final int duration = investigationBean.getMaxRuntimeInMin();
        if (duration <= 0) {
            final String error = "Investigation's duration is not set correctly";
            LOG.error(error);
            errorStrings.append(error + "<br/>");
        }

        final String depositEndpoint = investigationBean.getDepositEndpoint();
        if (depositEndpoint == null || depositEndpoint.isEmpty()) {
            final String error = "Deposit endpoint is not set";
            LOG.error(error);
            errorStrings.append(error + "<br/>");
        }

        final String investigationErrors = errorStrings.toString();
        final String investigationWarnings = warningStrings.toString();
        String instrumentsErrors = "";

        for (final InstrumentBean instrumentBean : rigBean.getContentList()) {
            final StringBuilder instrumentsError = new StringBuilder();
            configurationMap = new HashMap<String, String>();

            final String fileFormat = instrumentBean.getFileFormat();
            final String monitoredFolder = instrumentBean.getFolder();
            final String esyncDaemonEndpoint = instrumentBean.getESyncDaemon();

            if (fileFormat == null || fileFormat.isEmpty()) {
                final String error = "FileFormat is not set for instrument (id: " + instrumentBean.getObjectId() + ")";
                LOG.error(error);
                instrumentsError.append(error + "<br/>");
            }
            if (monitoredFolder == null || monitoredFolder.isEmpty()) {
                final String error =
                    "Monitoredfolder is not set for instrument (id: " + instrumentBean.getObjectId() + ")";
                LOG.error(error);
                instrumentsError.append(error + "<br/>");
            }
            if (esyncDaemonEndpoint == null || esyncDaemonEndpoint.isEmpty()) {
                final String error =
                    "EsyncDaemonEndpoint is not set for instrument (id: " + instrumentBean.getObjectId() + ")";
                LOG.error(error);
                instrumentsError.append(error + "<br/>");
            }

            final String configurationId = UUID.randomUUID().toString();
            if (configurationId == null || configurationId.isEmpty()) {
                LOG.error("ConfigurationId is null");
            }

            if (!instrumentsError.toString().isEmpty()) {
                instrumentsErrors += instrumentsError.toString();
                continue;
            }

            configurationMap.put(ELabsServiceConstants.WORKSPACE_ID, workspaceId);
            configurationMap.put(ELabsServiceConstants.USER_HANDLE, userHandle);
            configurationMap.put(ELabsServiceConstants.INFRASTRUCTURE_ENDPOINT, infrastructureEndpoint);
            configurationMap.put(ELabsServiceConstants.USER_EMAIL_ADDRESS, router
                .getApp().getCurrentUser().getLoginName()
                + FIZ_KARLSRUHE_DE);
            configurationMap.put(ELabsServiceConstants.CONTENT_MODEL_ID, contentModelId);
            configurationMap.put(ELabsServiceConstants.CHECK_SUM_TYPE, checkSumType);
            configurationMap.put(ELabsServiceConstants.EXPERIMENT_ID, experimentId);
            configurationMap.put(ELabsServiceConstants.EXPERIMENT_NAME, experimentTitle);
            configurationMap.put(ELabsServiceConstants.EXPERIMENT_DESCRIPTION, experimentDescr);
            configurationMap.put(ELabsServiceConstants.INSTRUMENT_NAME, createInstrumentNameAndId(instrumentBean));
            configurationMap.put(ELabsServiceConstants.MONITORING_DURATION, "" + duration);
            configurationMap.put(ELabsServiceConstants.DEPOSIT_SERVER_ENDPOINT, depositEndpoint);

            configurationMap.put(ELabsServiceConstants.FILETYPE, fileFormat);
            configurationMap.put(ELabsServiceConstants.MONITORED_FOLDER, monitoredFolder);
            configurationMap.put(ELabsServiceConstants.E_SYNC_DAEMON_ENDPOINT, esyncDaemonEndpoint);
            configurationMap.put(ELabsServiceConstants.CONFIGURATION_ID, configurationId);

            result.add(configurationMap);
        }

        if (!investigationErrors.isEmpty() || !instrumentsErrors.isEmpty()) {
            String errorMessage = "<hr/>";
            if (!investigationErrors.isEmpty()) {
                errorMessage += "<b>Investigation errors:</b><br/>";
                errorMessage += investigationErrors;
                errorMessage += "<br/>";
            }
            if (!instrumentsErrors.isEmpty()) {
                errorMessage += "<b>Instrument errors:</b><br/>";
                errorMessage += instrumentsErrors;
            }
            showError(errorMessage);
            return null;
        }

        if (!investigationWarnings.isEmpty()) {
            showWarning("<hr/>" + investigationWarnings);
        }

        return result;
    }

    private static String createInstrumentNameAndId(final InstrumentBean instrumentBean) {
        // @formatter:off
        return new StringBuilder(instrumentBean.getName())
            .append(' ')
            .append("(")
            .append(instrumentBean.getObjectId())
            .append(")")
            .toString();
        // @formatter:on
    }

    private static List<String[]> buildConfiguration(final List<Map<String, String>> list) {
        Preconditions.checkNotNull(list, "Config list is null");
        final List<String[]> result = new ArrayList<String[]>();

        if (list.isEmpty()) {
            LOG.error("There is not ConfigMap to process!");
            return null;
        }

        for (final Map<String, String> map : list) {
            final Properties config = new Properties();
            config.putAll(map);

            if (!config.isEmpty()) {
                LOG.debug(config.toString());
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    config.storeToXML(out, "");
                    LOG.debug("Configuration Properties XML \n" + out.toString());
                }
                catch (final IOException e) {
                    LOG.error(e.getMessage());
                }
                result.add(new String[] { config.getProperty(ELabsServiceConstants.E_SYNC_DAEMON_ENDPOINT),
                    out.toString() });
            }
            else {
                LOG.error("Configuration is empty!");
            }
        }
        return result;
    }

    private static boolean sendStartRequest(final List<String[]> propertyList) {
        LOG.info("Service> Send configuration to start...");

        boolean hasError = false;

        Preconditions.checkNotNull(propertyList, "Config list is null");
        final String configURLPart = "/configuration";

        for (final String[] configurationArray : propertyList) {
            String esyncEndpoint = configurationArray[0];
            LOG.debug("Service> sending start request for " + esyncEndpoint);
            while (esyncEndpoint.endsWith("/")) {
                esyncEndpoint = esyncEndpoint.substring(0, esyncEndpoint.length() - 1);
            }

            if (!esyncEndpoint.endsWith(configURLPart)) {
                esyncEndpoint = esyncEndpoint + configURLPart;
            }

            try {
                LOG.debug("Service> called HttpClient.");
                // FIXME set proxy
                final DefaultHttpClient httpClient = new DefaultHttpClient();
                httpClient.setKeepAliveStrategy(null);
                final HttpPut putMethod = new HttpPut(esyncEndpoint);
                putMethod.setEntity(new StringEntity(configurationArray[1], HTTP.UTF_8));
                final HttpResponse response = httpClient.execute(putMethod);
                final StatusLine statusLine = response.getStatusLine();
                if (isNotSuccessful(statusLine.getStatusCode())) {
                    LOG.error("Service> wrong method call: " + statusLine.getReasonPhrase());
                    hasError = true;
                }
                else {
                    LOG.info("Service> configuration is successfully sent!");
                }
            }
            catch (final UnsupportedEncodingException e) {
                LOG.error("Service> UnsupportedEncodingException: " + e.getMessage());
                hasError = true;
                continue;
            }
            catch (final ClientProtocolException e) {
                LOG.error("Service> ClientProtocolException: " + e.getMessage());
                hasError = true;
                continue;
            }
            catch (final IOException e) {
                LOG.error("Service> IOException: " + e.getMessage());
                hasError = true;
                continue;
            }
        }
        return hasError;
    }

    private static void sendStopRequest() {
        LOG.info("Service> Send configuration to stop...");
    }

    @Override
    public synchronized void start() throws EscidocBrowserException {
        LOG.info("Investigation's process is started.");
        try {
            final List<Map<String, String>> configMapList = gatherConfigurationDataFromInfrastructure();
            if (configMapList == null || configMapList.isEmpty()) {
                LOG.error("Config is not available to process");
                throw new EscidocBrowserException();
            }

            final List<String[]> configurationPropertyList = buildConfiguration(configMapList);
            if (configurationPropertyList == null || configurationPropertyList.isEmpty()) {
                LOG.error("Config is not available to process");
                throw new EscidocBrowserException();
            }
            if (sendStartRequest(configurationPropertyList)) {
                showError("Communication error");
                throw new EscidocBrowserException();
            }
            this.router
                .getApp().getMainWindow()
                .showNotification("Success", "Configuration is sent!", Notification.TYPE_TRAY_NOTIFICATION);
        }
        catch (final EscidocBrowserException e) {
            LOG.error(e.getMessage());
            throw new EscidocBrowserException(e);
        }
    }

    @Override
    public synchronized void stop() throws EscidocBrowserException {
        LOG.info("Investigation is stopped.");

        sendStopRequest();
    }

    private static InvestigationBean resolveInvestigation(final ContainerProxy containerProxy) {
        final String URI_DC = "http://purl.org/dc/elements/1.1/";
        final String URI_EL = "http://escidoc.org/ontologies/bw-elabs/re#";
        final String URI_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

        if (containerProxy == null) {
            throw new NullPointerException("Container Proxy is null.");
        }
        final InvestigationBean investigationBean = new InvestigationBean();
        final Element e = containerProxy.getMedataRecords().get("escidoc").getContent();
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
                investigationBean.setMaxRuntime("<<not used>>");
                try {
                    investigationBean.setMaxRuntimeInMin(Integer.valueOf(node.getTextContent()));
                }
                catch (final NumberFormatException nfe) {
                    LOG.error(nfe.getMessage());
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
                    investigationBean.setRigBean(rigBean);
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

    private RigBean resolveRig(final ItemProxy itemProxy) throws EscidocBrowserException {
        Preconditions.checkNotNull(itemProxy, "Resource is null");

        final RigBean rigBean = new RigBean();
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
                    final Node attributeNode = node.getAttributes().getNamedItemNS(URI_RDF, "resource");
                    final String instrumentID = attributeNode.getNodeValue();

                    try {
                        final ItemProxy instrumentProxy = (ItemProxy) repositories.item().findById(instrumentID);
                        rigBean.getContentList().add(resolveInstrument(instrumentProxy));
                    }
                    catch (final EscidocClientException e) {
                        LOG.error(e.getLocalizedMessage());
                    }
                }
            }
        }
        return rigBean;
    }

    private static InstrumentBean resolveInstrument(final ResourceProxy resourceProxy) throws EscidocBrowserException {

        if (resourceProxy == null || !(resourceProxy instanceof ItemProxy)) {
            throw new EscidocBrowserException("NOT an ItemProxy", null);
        }

        final ItemProxy itemProxy = (ItemProxy) resourceProxy;
        final InstrumentBean instrumentBean = new InstrumentBean();
        instrumentBean.setObjectId(itemProxy.getId());
        final Element e = itemProxy.getMedataRecords().get("escidoc").getContent();
        final NodeList nodeList = e.getChildNodes();
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

    private static boolean isNotSuccessful(final int statusCode) {
        return statusCode / 100 != 2;
    }

    private void showError(final String errorMessage) {
        Preconditions.checkNotNull(errorMessage, "ErrorMessage is null");
        this.router.getApp().getMainWindow().showNotification("Error", errorMessage, Notification.TYPE_ERROR_MESSAGE);
    }

    private void showWarning(final String warningMessage) {
        Preconditions.checkNotNull(warningMessage, "Warningmessage is null");
        this.router
            .getApp().getMainWindow().showNotification("Warning", warningMessage, Notification.TYPE_WARNING_MESSAGE);
    }
}
