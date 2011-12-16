package org.escidoc.browser.ui.listeners;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ContextBuilder;
import org.escidoc.browser.ui.maincontent.ItemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;

public class CreateResourceAddContextListener {
    private static final Logger LOG = LoggerFactory.getLogger(ItemView.class);

    private Context createdContext;

    public CreateResourceAddContextListener(String name, String description, String type, String orgUnit,
        Repositories repositories, EscidocServiceLocation serviceLocation) throws EscidocClientException {
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
        createdContext = repositories.context().create(cntx.build());
    }

    public Context getCreatedContext() {
        return createdContext;
    }

}
