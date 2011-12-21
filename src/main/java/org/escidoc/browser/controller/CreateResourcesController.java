package org.escidoc.browser.controller;

import com.google.common.base.Preconditions;

import com.vaadin.ui.Component;

import org.escidoc.browser.model.ContentModelService;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.OrgUnitBuilder;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ContextBuilder;
import org.escidoc.browser.repository.internal.OrgUnitService;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.tools.CreateResourcesView;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.cmm.ContentModel;
import de.escidoc.core.resources.cmm.ContentModelProperties;
import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;

public class CreateResourcesController extends Controller {

    private final Repositories repositories;

    private final Router router;

    public CreateResourcesController(final Repositories repositories, final Router router,
        final ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        this.repositories = repositories;
        this.router = router;

        this.view = createView();
        this.setResourceName(ViewConstants.CREATE_RESOURCES);
    }

    private Component createView() {
        return new CreateResourcesView(router, repositories, this);
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

    public void createResourceAddContextListener(
        final String name, final String description, final String type, final String orgUnit,
        final Repositories repositories, final EscidocServiceLocation serviceLocation) throws EscidocClientException {
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
        repositories.context().create(cntx.build());
    }

    @Override
    protected Component createView(final ResourceProxy resourceProxy) throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }
}
