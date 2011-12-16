package org.escidoc.browser.ui.listeners;

import org.escidoc.browser.model.ContentModelService;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.ui.Router;

import com.google.common.base.Preconditions;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.cmm.ContentModel;
import de.escidoc.core.resources.cmm.ContentModelProperties;

public class CreateResourceAddContentModel {

    public CreateResourceAddContentModel(String name, String description, Router router,
        EscidocServiceLocation serviceLocation) throws EscidocClientException {
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

}
