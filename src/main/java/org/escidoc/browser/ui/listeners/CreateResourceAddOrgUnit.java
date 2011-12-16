package org.escidoc.browser.ui.listeners;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.OrgUnitService;
import org.escidoc.browser.model.internal.OrgUnitBuilder;
import org.escidoc.browser.ui.Router;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class CreateResourceAddOrgUnit {

    public CreateResourceAddOrgUnit(String name, String description, Router router,
        EscidocServiceLocation serviceLocation) throws EscidocClientException, ParserConfigurationException,
        SAXException, IOException {
        Preconditions.checkNotNull(name, "Name of Context is Null");
        Preconditions.checkNotNull(description, "txtDescContext is Null");

        OrgUnitBuilder orgBuilder = new OrgUnitBuilder();

        OrgUnitService orgService =
            new OrgUnitService(serviceLocation.getEscidocUri(), router.getApp().getCurrentUser().getToken());

        orgService.create(orgBuilder.with(name, description).build());

    }

}
