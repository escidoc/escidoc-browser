package org.escidoc.browser.ui;

import java.util.List;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceContainerImpl;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.listeners.TreeClickListener;
import org.escidoc.browser.ui.listeners.TreeExpandListener;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class UiBuilder {

    private final EscidocServiceLocation servicelocation;

    public UiBuilder(final EscidocServiceLocation serviceLocation) {
        Preconditions.checkNotNull(serviceLocation,
            "serviceLocation is null: %s", serviceLocation);
        servicelocation = serviceLocation;
    }

    public NavigationTreeView buildNavigationTree(
        final Repository repository, final Repository containerRepository,
        final Repository itemRepository, final MainSite mainSite,
        final Window mainWindow) throws EscidocClientException {

        final NavigationTreeView navigationTreeView =
            new NavigationTreeViewImpl(mainSite);

        final List<ResourceModel> contexts = repository.findAll();

        final ResourceContainerImpl resourceContainer =
            new ResourceContainerImpl(contexts);
        resourceContainer.init();

        navigationTreeView.setDataSource(resourceContainer, mainSite);
        navigationTreeView.addExpandListener(new TreeExpandListener(repository,
            containerRepository, resourceContainer));
        navigationTreeView.addClickListener(new TreeClickListener(
            servicelocation, repository, containerRepository, itemRepository,
            mainWindow, mainSite));

        return navigationTreeView;
    }

    public NavigationTreeView buildContextDirectMemberTree(
        final Repository contextRepository,
        final Repository containerRepository, final Repository itemRepository,
        final MainSite mainSite, final String parentID, final Window mainWindow)
        throws EscidocClientException {

        final NavigationTreeView navigationTreeView =
            new NavigationTreeViewImpl(mainSite);

        final List<ResourceModel> contexts =
            contextRepository.findTopLevelMembersById(parentID);

        final ResourceContainerImpl resourceContainer =
            new ResourceContainerImpl(contexts);
        resourceContainer.init();

        navigationTreeView.setDataSource(resourceContainer, mainSite);
        navigationTreeView.addExpandListener(new TreeExpandListener(
            contextRepository, containerRepository, resourceContainer));
        navigationTreeView.addClickListener(new TreeClickListener(
            servicelocation, contextRepository, containerRepository,
            itemRepository, mainWindow, mainSite));

        return navigationTreeView;
    }

    public NavigationTreeView buildContainerDirectMemberTree(
        final Repository contextRepository,
        final Repository containerRepository, final Repository itemRepository,
        final MainSite mainSite, final String parentID, final Window mainWindow)
        throws EscidocClientException {

        final NavigationTreeView navigationTreeView =
            new NavigationTreeViewImpl(mainSite);

        final List<ResourceModel> container =
            containerRepository.findTopLevelMembersById(parentID);

        final ResourceContainerImpl resourceContainer =
            new ResourceContainerImpl(container);
        resourceContainer.init();

        navigationTreeView.setDataSource(resourceContainer, mainSite);
        navigationTreeView.addClickListener(new TreeClickListener(servicelocation, contextRepository,
            containerRepository, itemRepository,mainWindow, mainSite));
        return navigationTreeView;
    }

}
