package org.escidoc.browser.ui.helper;

import java.util.ArrayList;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.internal.HasNoNameResourceImpl;
import org.escidoc.browser.repository.UtilRepository;
import org.escidoc.browser.repository.internal.UtilRepositoryImpl;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ResourceHierarchy {

    private final UtilRepository repository;

    private final ArrayList<ResourceModel> containerHierarchy = new ArrayList<ResourceModel>();

    public ResourceHierarchy(EscidocServiceLocation serviceLocation) {
        repository = new UtilRepositoryImpl(serviceLocation);
    }

    public ResourceModel getReturnParentOfItem(String id) throws Exception {
        final ResourceModel parent = repository.findParent(new HasNoNameResourceImpl(id, ResourceType.ITEM));
        return parent;
    }

    public ResourceModel getParentOfContainer(String id) throws EscidocClientException {
        final ResourceModel parent = repository.findParent(new HasNoNameResourceImpl(id, ResourceType.CONTAINER));
        return parent;
    }

    private void createContainerHierarchy(String id) {
        try {
            if (getParentOfContainer(id) != null) {
                containerHierarchy.add(getParentOfContainer(id));
                createContainerHierarchy(getParentOfContainer(id).getId());
            }
        }
        catch (EscidocClientException e) {
            System.out.print("q" + id);
        }
    }

    public ArrayList<ResourceModel> getHierarchy(String id) throws EscidocClientException {
        createContainerHierarchy(id);

        return containerHierarchy;
    }

}
