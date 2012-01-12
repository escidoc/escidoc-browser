package org.escidoc.browser.controller;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.escidoc.browser.model.ContentModelService;
import org.escidoc.browser.model.ContextModel;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.OrgUnitService;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.OrgUnitBuilder;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ContextBuilder;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.tools.CreateResourcesView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.cmm.ContentModel;
import de.escidoc.core.resources.cmm.ContentModelProperties;
import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;

public class CreateResourcesController extends Controller {

    private Repositories repositories;

    private Router router;

    private static final Logger LOG = LoggerFactory.getLogger(CreateResourcesController.class);

    public CreateResourcesController(Repositories repositories, Router router, ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        this.repositories = repositories;
        this.router = router;

        try {
            this.view = createView();
        }
        catch (EscidocClientException e) {
            router.getMainWindow().showNotification(
                ViewConstants.VIEW_ERROR_CANNOT_LOAD_VIEW + e.getLocalizedMessage(), Notification.TYPE_ERROR_MESSAGE);
        }
        this.setResourceName(ViewConstants.CREATE_RESOURCES);
    }

    private Component createView() throws EscidocClientException {
        return new CreateResourcesView(router, repositories, this);
    }

    public void createResourceAddOrgUnit(
        String name, String description, Router router, EscidocServiceLocation serviceLocation)
        throws EscidocClientException, ParserConfigurationException, SAXException, IOException {
        Preconditions.checkNotNull(name, "Name of Context is Null");
        Preconditions.checkNotNull(description, "txtDescContext is Null");

        OrgUnitBuilder orgBuilder = new OrgUnitBuilder();

        OrgUnitService orgService =
            new OrgUnitService(serviceLocation.getEscidocUri(), router.getApp().getCurrentUser().getToken());

        orgService.create(orgBuilder.with(name, description).build());
    }

    public void createResourceAddContentModel(
        String name, String description, Router router, EscidocServiceLocation serviceLocation)
        throws EscidocClientException {
        Preconditions.checkNotNull(name, "Name of Context is Null");
        Preconditions.checkNotNull(description, "txtDescContext is Null");

        final ContentModel contentModel = new ContentModel();
        final ContentModelProperties contentModelProperties = new ContentModelProperties();
        contentModelProperties.setName(name);
        contentModelProperties.setDescription(description);
        contentModel.setProperties(contentModelProperties);

        ContentModelService cntService =
            new ContentModelService(serviceLocation.getEscidocUri(), router.getApp().getCurrentUser().getToken());
        cntService.create(contentModel);

    }

    public void createResourceAddContext(
        String name, String description, String type, String orgUnit, Boolean openedContext, Repositories repositories,
        EscidocServiceLocation serviceLocation) throws EscidocClientException {
        Preconditions.checkNotNull(name, "Name of Context is Null");
        Preconditions.checkNotNull(orgUnit, "Organizational Unit is null is Null");
        Preconditions.checkNotNull(description, "txtDescContext is Null");
        Preconditions.checkNotNull(type, "Type is Null");

        ContextBuilder cntx = new ContextBuilder(new Context());
        cntx.name(name);
        cntx.description(name);
        cntx.type(type);

        OrganizationalUnitRefs orgRefs = new OrganizationalUnitRefs();
        orgRefs.add(new OrganizationalUnitRef(orgUnit));
        cntx.orgUnits(orgRefs);
        Context newContext = repositories.context().create(cntx.build());

        // Open Context for Public
        if (openedContext) {
            repositories.context().open(newContext);
        }
        // Updating the tree
        router.getLayout().getTreeDataSource().addTopLevelResource(new ContextModel(newContext));
        router.openControllerView(
            new ContextController(repositories, router, repositories.context().findById(newContext.getObjid())), true);
    }
}
