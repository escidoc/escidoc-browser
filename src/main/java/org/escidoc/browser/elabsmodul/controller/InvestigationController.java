/**
 * 
 */
package org.escidoc.browser.elabsmodul.controller;

import java.util.ArrayList;
import java.util.List;

import org.escidoc.browser.controller.Controller;
import org.escidoc.browser.elabsmodul.exceptions.EscidocBrowserException;
import org.escidoc.browser.elabsmodul.interfaces.IBeanModel;
import org.escidoc.browser.elabsmodul.interfaces.ISaveAction;
import org.escidoc.browser.elabsmodul.model.InvestigationBean;
import org.escidoc.browser.elabsmodul.views.InvestigationView;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ContainerProxyImpl;
import org.escidoc.browser.ui.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

/**
 * @author frs
 *
 */
public class InvestigationController extends Controller implements ISaveAction {

    private static Logger LOG = LoggerFactory.getLogger(InstrumentController.class);

    // contains default deposit endpoint URLs
    private List<String> depositEndPointUrls = new ArrayList<String>();

    private Repositories repositories;

    private Router router;

    private ResourceProxy resourceProxy;

    /* (non-Javadoc)
     * @see org.escidoc.browser.elabsmodul.interfaces.ISaveAction#saveAction(org.escidoc.browser.elabsmodul.interfaces.IBeanModel)
     */
    @Override
    public void saveAction(IBeanModel dataBean) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.escidoc.browser.controller.Controller#init(org.escidoc.browser.model.EscidocServiceLocation, org.escidoc.browser.repository.Repositories, org.escidoc.browser.ui.Router, org.escidoc.browser.model.ResourceProxy, com.vaadin.ui.Window, org.escidoc.browser.model.CurrentUser)
     */
    @Override
    public void init(
        EscidocServiceLocation serviceLocation, Repositories repositories, Router router,
        ResourceProxy resourceProxy, Window mainWindow, CurrentUser currentUser) {
        Preconditions.checkNotNull(repositories, "Repository ref is null");
        this.repositories = repositories;
        this.router = router;
        this.resourceProxy = resourceProxy;

        this.loadAdminDescriptorInfo();
        this.view = createView(resourceProxy);
        this.getResourceName(resourceProxy.getName());

    }

    private void loadAdminDescriptorInfo() {
        ContextProxyImpl context;
        try {
            context = (ContextProxyImpl) repositories.context().findById(resourceProxy.getContext().getObjid());
            Element content = context.getAdminDescription().get("elabs").getContent();
            NodeList nodeList = content.getElementsByTagName("el:deposit-endpoint");
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                depositEndPointUrls.add(node.getTextContent());
            }

        }
        catch (EscidocClientException e) {
            LOG.error("Could not load Admin Descriptor 'elabs'. " + e.getLocalizedMessage(), e);
        }
    }

    private Component createView(ResourceProxy resourceProxy) {
        Preconditions.checkNotNull(resourceProxy, "ResourceProxy is NULL");

        ContainerProxyImpl containerProxyImpl = null;
        InvestigationBean investigationBean = null;
        List<ResourceModel> breadCrumbModel = null;

        if (resourceProxy instanceof ContainerProxyImpl) {
            containerProxyImpl = (ContainerProxyImpl) resourceProxy;
        }

        try {
            investigationBean = loadBeanData(containerProxyImpl);
        }
        catch (final EscidocBrowserException e) {
            LOG.error(e.getLocalizedMessage());
            investigationBean = null;
        }
        
        breadCrumbModel = createBreadCrumbModel();
        
        Component investigationView =
            new InvestigationView(investigationBean, this, breadCrumbModel, resourceProxy, depositEndPointUrls);
        return investigationView;
    }

    private InvestigationBean loadBeanData(ContainerProxyImpl containerProxyImpl) throws EscidocBrowserException {
        // TODO Auto-generated method stub
        return null;
    }

    private List<ResourceModel> createBreadCrumbModel() {
        // TODO Auto-generated method stub
        return null;
    }

}
