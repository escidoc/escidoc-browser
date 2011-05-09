package org.escidoc.browser.ui.helper;

import java.net.URI;
import java.util.ArrayList;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.internal.HasNoNameResourceImpl;
import org.escidoc.browser.repository.UtilRepository;
import org.escidoc.browser.repository.internal.UtilRepositoryImpl;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ResourceHierarchy {

    private UtilRepository repository;

    private final ArrayList<ResourceModel> containerHierarchy = new ArrayList<ResourceModel>();;

    public void itShouldReturnListOfResourceWithContextAsItsLastElement(String id) throws Exception {
        initRepo();
        // when
        final ResourceModel[] result = repository.findAncestors(new HasNoNameResourceImpl(id, ResourceType.ITEM));
    }

    public ResourceModel getReturnParentOfItem(String id) throws Exception {
        initRepo();
        final ResourceModel parent = repository.findParent(new HasNoNameResourceImpl(id, ResourceType.ITEM));
        return parent;
    }

    public ResourceModel getParentOfContainer(String id) throws EscidocClientException {
        initRepo();
        final ResourceModel parent = repository.findParent(new HasNoNameResourceImpl(id, ResourceType.CONTAINER));
        return parent;
    }

    public String itShouldReturnContextOfContainer() throws Exception {
        final ResourceModel parent =
            repository.findParent(new HasNoNameResourceImpl("escidoc:16048", ResourceType.CONTAINER));

        return parent.getId();
    }

    private void createContainerHierarchy(String id) {
        try {
            if (getParentOfContainer(id) != null) {
                containerHierarchy.add(getParentOfContainer(id));
                createContainerHierarchy(getParentOfContainer(id).getId());
            }
        }
        catch (EscidocClientException e) {
            System.out.print("q");
            // containerHierarchy.add(null);
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }

    }

    public ArrayList<ResourceModel> getHierarchy(String id) throws EscidocClientException {
        createContainerHierarchy(id);

        return containerHierarchy;
    }

    private void initRepo() {
        repository = new UtilRepositoryImpl(new EscidocServiceLocation() {

            @Override
            public void setEscidocUri(final URI escidocUri) {
                throw new UnsupportedOperationException("Not yet implemented");

            }

            @Override
            public void setApplicationUri(final URI appUri) {
                throw new UnsupportedOperationException("Not yet implemented");

            }

            @Override
            public String getLogoutUri() {
                throw new UnsupportedOperationException("Not yet implemented");

            }

            @Override
            public String getLoginUri() {
                throw new UnsupportedOperationException("Not yet implemented");
            }

            @Override
            public String getEscidocUri() {
                return "http://escidev4:8080";
            }
        });
    }
}
