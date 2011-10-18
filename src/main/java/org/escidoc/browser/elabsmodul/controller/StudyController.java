package org.escidoc.browser.elabsmodul.controller;

import java.util.Collections;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.elabsmodul.controller.utils.DOM2String;
import org.escidoc.browser.elabsmodul.exceptions.EscidocBrowserException;
import org.escidoc.browser.elabsmodul.interfaces.IBeanModel;
import org.escidoc.browser.elabsmodul.interfaces.ISaveAction;
import org.escidoc.browser.elabsmodul.model.StudyBean;
import org.escidoc.browser.elabsmodul.views.StudyView;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.helper.ResourceHierarchy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class StudyController extends Controller implements ISaveAction {

    private EscidocServiceLocation serviceLocation;

    private ResourceProxy resourceProxy;

    private Repositories repositories;

    private Window mainWindow;

    private static Logger LOG = LoggerFactory.getLogger(InstrumentController.class);

    @Override
    public void saveAction(IBeanModel dataBean) {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(
        EscidocServiceLocation serviceLocation, Repositories repositories, Router mainSite,
        ResourceProxy resourceProxy, Window mainWindow, CurrentUser currentUser) {
        this.serviceLocation = serviceLocation;
        this.resourceProxy = resourceProxy;
        this.repositories = repositories;
        this.mainWindow = mainWindow;
        this.view = createView(resourceProxy);
        this.getResourceName(resourceProxy.getName());

    }

    private Component createView(ResourceProxy resourceProxy) {
        Preconditions.checkNotNull(resourceProxy, "ResourceProxy is NULL");

        ContainerProxy containerProxy = null;
        StudyBean studyBean = null;

        if (resourceProxy instanceof ContainerProxy) {
            containerProxy = (ContainerProxy) resourceProxy;
        }

        try {
            studyBean = loadBeanData(containerProxy);
        }
        catch (final EscidocBrowserException e) {
            LOG.error(e.getLocalizedMessage());
            studyBean = null;
        }
        // Instrument View
        Component instrumentView = new StudyView(studyBean, this, this.BreadCrumbModel(), resourceProxy);
        return instrumentView; // pushed into InstrumentView
    }

    private synchronized StudyBean loadBeanData(ContainerProxy containerProxy) throws EscidocBrowserException {
        // TODO NOT YET IMPLEMENTED
        if (resourceProxy == null || !(resourceProxy instanceof ContainerProxy)) {
            throw new EscidocBrowserException("NOT an ItemProxy", null);
        }

        final ContainerProxy containerProxy1 = (ContainerProxy) resourceProxy;
        final StudyBean studyBean = new StudyBean();

        try {
            final Element e = containerProxy1.getMedataRecords().get("escidoc").getContent();
            final String xml = DOM2String.convertDom2String(e);

            final NodeList nodeList = e.getChildNodes();

            studyBean.setObjectId(containerProxy1.getId());

            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                final String nodeName = node.getNodeName();

                if (nodeName.equals("dc:title")) {
                    studyBean.setName((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }

                else if (nodeName.equals("dc:description")) {
                    studyBean
                        .setDescription((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }

                else if (nodeName.equals("el:requires-configuration")) {
                    final String value = node.getFirstChild().getNodeValue();
                    if (value.equals("no")) {
                        studyBean.setConfiguration(false);
                    }
                    else if (value.equals("yes")) {
                        studyBean.setConfiguration(true);
                    }

                }
                else if (nodeName.equals("el:requires-calibration")) {
                    final String value = node.getFirstChild().getNodeValue();
                    if (value.equals("no")) {
                        studyBean.setCalibration(false);
                    }
                    else if (value.equals("yes")) {
                        studyBean.setCalibration(true);
                    }
                }
                else if (nodeName.equals("el:esync-endpoint")) {
                    studyBean
                        .setESyncDaemon((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }
                else if (nodeName.equals("el:monitored-folder")) {
                    studyBean.setFolder((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }
                else if (nodeName.equals("el:result-mime-type")) {
                    studyBean
                        .setFileFormat((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }
                else if (nodeName.equals("el:responsible-person")
                    && node.getAttributes().getNamedItem("rdf:resource") != null) {
                    studyBean.setDeviceSupervisor(node.getAttributes().getNamedItem("rdf:resource").getNodeValue());
                }
                else if (nodeName.equals("el:institution") && node.getAttributes().getNamedItem("rdf:resource") != null) {
                    studyBean.setInstitute(node.getAttributes().getNamedItem("rdf:resource").getNodeValue());
                }

            }
            LOG.debug(xml);
        }
        catch (final TransformerException e) {
            LOG.error(e.getLocalizedMessage());
        }
        return studyBean;
    }

    private List<ResourceModel> BreadCrumbModel() {
        final ResourceHierarchy rs = new ResourceHierarchy(serviceLocation, repositories);
        List<ResourceModel> hierarchy = null;
        try {
            hierarchy = rs.getHierarchy(resourceProxy);
        }
        catch (EscidocClientException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            LOG.debug("Fatal error, could not load BreadCrumb " + e.getLocalizedMessage());
        }
        Collections.reverse(hierarchy);
        hierarchy.add(resourceProxy);
        return hierarchy;
    }

}
