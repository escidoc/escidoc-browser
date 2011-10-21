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
import org.escidoc.browser.elabsmodul.model.StudyBean;
import org.escidoc.browser.elabsmodul.views.StudyView;
import org.escidoc.browser.elabsmodul.views.YesNoDialog;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ContainerRepository;
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

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.container.Container;

public class StudyController extends Controller implements ISaveAction {

    private EscidocServiceLocation serviceLocation;

    private ResourceProxy resourceProxy;

    private Repositories repositories;

    private Window mainWindow;

    // the bean model to store
    private IBeanModel beanModel = null;

    private Router router;

    private static Logger LOG = LoggerFactory.getLogger(InstrumentController.class);

    @Override
    public void init(
        EscidocServiceLocation serviceLocation, Repositories repositories, Router router, ResourceProxy resourceProxy,
        Window mainWindow, CurrentUser currentUser) {
        this.serviceLocation = serviceLocation;
        this.resourceProxy = resourceProxy;
        this.repositories = repositories;
        this.mainWindow = mainWindow;
        this.router = router;
        this.view = createView(resourceProxy);
        this.getResourceName(resourceProxy.getName());

    }

    @Override
    public void saveAction(IBeanModel beanModel) {
        Preconditions.checkNotNull(beanModel, "DataBean to store is NULL");
        this.beanModel = beanModel;

        mainWindow.addWindow(new YesNoDialog(ELabsViewContants.DIALOG_SAVESTUDY_HEADER,
            ELabsViewContants.DIALOG_SAVESTUDY_TEXT, new YesNoDialog.Callback() {

                @Override
                public void onDialogResult(boolean resultIsYes) {
                    if (resultIsYes) {
                        StudyController.this.saveModel();
                    }
                    else {
                        ((StudyView) StudyController.this.view).hideButtonLayout();
                    }
                }
            }));
    }

    protected void saveModel() {

        Preconditions.checkNotNull(beanModel, "DataBean to store is NULL");
        ContainerRepository containerRepositories = repositories.container();
        final String ESCIDOC = "escidoc";

        StudyBean studyBean = null;
        Container container = null;

        if (beanModel instanceof StudyBean) {
            studyBean = (StudyBean) beanModel;
        }
        final Element metaDataContent = StudyController.createInstrumentDOMElementByBeanModel(studyBean);

        try {
            container = containerRepositories.findContainerById(studyBean.getObjectId());
            MetadataRecord metadataRecord = container.getMetadataRecords().get(ESCIDOC);
            metadataRecord.setContent(metaDataContent);
            containerRepositories.update(container);
        }
        catch (EscidocClientException e) {
            LOG.error(e.getLocalizedMessage());
        }
        finally {
            beanModel = null;
        }
        LOG.info("Study is successfully saved.");

    }

    private static Element createInstrumentDOMElementByBeanModel(StudyBean studyBean) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setValidating(true);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            final Document doc = builder.newDocument();

            Element study = doc.createElementNS("http://escidoc.org/ontologies/bw-elabs/re#", "Instrument");
            study.setPrefix("el");

            final Element title = doc.createElementNS("http://purl.org/dc/elements/1.1/", "title");
            title.setPrefix("dc");
            title.setTextContent(studyBean.getName());
            study.appendChild(title);

            final Element description = doc.createElementNS("http://purl.org/dc/elements/1.1/", "description");
            description.setPrefix("dc");
            description.setTextContent(studyBean.getDescription());
            study.appendChild(description);

            final Element resultingPublication = doc.createElement("el:resulting-publication");
            resultingPublication.setAttributeNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:resource",
                studyBean.getResultingPublication());
            study.appendChild(resultingPublication);

            final Element motivatingProperties = doc.createElement("el:motivating-publication");
            motivatingProperties.setAttributeNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:resource",
                studyBean.getMotivatingProperties());
            study.appendChild(motivatingProperties);
            return study;

        }
        catch (DOMException e) {
            LOG.error(e.getLocalizedMessage());
        }
        catch (ParserConfigurationException e) {
            LOG.error(e.getLocalizedMessage());
        }

        return null;
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
        // Study View
        Component studyView = new StudyView(studyBean, this, this.createBeadCrumbModel(), resourceProxy, router);
        return studyView; // pushed into StudyView
    }

    private synchronized StudyBean loadBeanData(ContainerProxy containerProxy) throws EscidocBrowserException {
        if (resourceProxy == null || !(resourceProxy instanceof ContainerProxy)) {
            throw new EscidocBrowserException("NOT an ContainerProxy", null);
        }

        final ContainerProxy containerProxy1 = (ContainerProxy) resourceProxy;
        final StudyBean studyBean = new StudyBean();

        try {
            final Element e = containerProxy1.getMedataRecords().get("escidoc").getContent();
            final String xml = DOM2String.convertDom2String(e);

            final NodeList nodeList = e.getChildNodes();

            studyBean.setObjectId(containerProxy1.getId());
            final String URI_DC = "http://purl.org/dc/elements/1.1/";
            final String URI_EL = "http://escidoc.org/ontologies/bw-elabs/re#";
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                final String nodeName = node.getLocalName();
                final String nsUri = node.getNamespaceURI();

                if ("title".equals(nodeName) && URI_DC.equals(nsUri)) {
                    studyBean.setName((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }

                else if ("description".equals(nodeName) && URI_DC.equals(nsUri)) {
                    studyBean
                        .setDescription((node.getFirstChild() != null) ? node.getFirstChild().getNodeValue() : null);
                }
                else if ("motivating-publication".equals(nodeName) && URI_EL.equals(nsUri)) {
                    studyBean.setMotivatingProperties(node.getAttributes().getNamedItem("rdf:resource").getNodeValue());
                }
                else if ("resulting-publication".equals(nodeName) && URI_EL.equals(nsUri)) {
                    studyBean.setResultingPublication(node.getAttributes().getNamedItem("rdf:resource").getNodeValue());
                }
            }
            LOG.debug(xml);
        }

        catch (final TransformerException e) {
            LOG.error(e.getLocalizedMessage());
        }
        return studyBean;
    }

    private List<ResourceModel> createBeadCrumbModel() {
        final ResourceHierarchy rs = new ResourceHierarchy(serviceLocation, repositories);
        List<ResourceModel> hierarchy = null;
        try {
            hierarchy = rs.getHierarchy(resourceProxy);
        }
        catch (EscidocClientException e) {
            LOG.debug("Fatal error, could not load BreadCrumb " + e.getLocalizedMessage());
        }
        Collections.reverse(hierarchy);
        hierarchy.add(resourceProxy);
        return hierarchy;
    }

}
