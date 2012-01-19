package org.escidoc.browser.controller;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.escidoc.browser.model.ContentModelService;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContextModel;
import org.escidoc.browser.model.internal.OrgUnitBuilder;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ContextBuilder;
import org.escidoc.browser.repository.internal.OrgUnitService;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.tools.CreateResourcesView;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.cmm.ContentModel;
import de.escidoc.core.resources.cmm.ContentModelProperties;
import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;

public class CreateResourcesController extends Controller {

    public CreateResourcesController(final Repositories repositories, final Router router,
        final ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        this.setResourceName(ViewConstants.CREATE_RESOURCES);
        createView();
    }

    @Override
    public void createView() {
        view = new CreateResourcesView(getRouter(), getRepositories(), this);
    }

    public void createResourceAddOrgUnit(
        final String name, final String description, final Router router, final EscidocServiceLocation serviceLocation)
        throws EscidocClientException, ParserConfigurationException, SAXException, IOException {
        Preconditions.checkNotNull(name, "Name of Context is Null");
        Preconditions.checkNotNull(description, "txtDescContext is Null");

        final OrgUnitBuilder orgBuilder = new OrgUnitBuilder();

        final OrgUnitService orgService =
            new OrgUnitService(serviceLocation.getEscidocUri(), router.getApp().getCurrentUser().getToken());

        orgService.create(orgBuilder.with(name, description).build());
    }

    public void createResourceAddContentModel(
        final String name, final String description, final Router router, final EscidocServiceLocation serviceLocation)
        throws EscidocClientException {
        Preconditions.checkNotNull(name, "Name of Context is Null");
        Preconditions.checkNotNull(description, "txtDescContext is Null");

        final ContentModel contentModel = new ContentModel();
        final ContentModelProperties contentModelProperties = new ContentModelProperties();
        contentModelProperties.setName(name);
        contentModelProperties.setDescription(description);
        contentModel.setProperties(contentModelProperties);

        final ContentModelService cntService =
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
        final ContextBuilder cntx = new ContextBuilder(new Context());
        cntx.name(name);
        cntx.description(name);
        cntx.type(type);

        final OrganizationalUnitRefs orgRefs = new OrganizationalUnitRefs();
        orgRefs.add(new OrganizationalUnitRef(orgUnit));
        cntx.orgUnits(orgRefs);
        final Context newContext = repositories.context().create(cntx.build());

        // Open Context for Public
        if (openedContext) {
            repositories.context().open(newContext);
        }
        // Updating the tree
        getRouter().getLayout().getTreeDataSource().addTopLevelResource(new ContextModel(newContext));
        getRouter().openControllerView(
            new ContextController(repositories, getRouter(), repositories.context().findById(newContext.getObjid())),
            true);
    }
}