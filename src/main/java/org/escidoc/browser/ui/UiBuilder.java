package org.escidoc.browser.ui;

import java.util.List;

import org.escidoc.browser.model.ResourceContainerImpl;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.listeners.TreeExpandListener;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class UiBuilder {

    public NavigationTreeView buildNavigationTree(
        final Repository repository, final Repository containerRepository,
        final Repository itemRepository, final MainSite mainSite)
        throws EscidocClientException {

        final NavigationTreeView navigationTreeView =
            new NavigationTreeViewImpl(mainSite);

        final List<ResourceModel> contexts = repository.findAll();

        final ResourceContainerImpl resourceContainer =
            new ResourceContainerImpl(contexts);
        resourceContainer.init();

        navigationTreeView.setDataSource(resourceContainer, mainSite);
        navigationTreeView.addExpandListener(new TreeExpandListener(repository,
            containerRepository, resourceContainer));
        navigationTreeView.addClickListener(new TreeClickListener(repository,
            containerRepository, itemRepository, mainSite));

        return navigationTreeView;
    }

    public NavigationTreeView buildContextDirectMemberTree(
        final Repository contextRepository,
        final Repository containerRepository, final Repository itemRepository,
        final MainSite mainSite, String parentID) throws EscidocClientException {

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
            contextRepository, containerRepository, itemRepository, mainSite));

        return navigationTreeView;
    }

    public NavigationTreeView buildContainerDirectMemberTree(
        final Repository contextRepository,
        final Repository containerRepository, final Repository itemRepository,
        final MainSite mainSite, String parentID) throws EscidocClientException {

        final NavigationTreeView navigationTreeView =
            new NavigationTreeViewImpl(mainSite);

        final List<ResourceModel> container =
            containerRepository.findTopLevelMembersById(parentID);

        final ResourceContainerImpl resourceContainer =
            new ResourceContainerImpl(container);
        resourceContainer.init();

        navigationTreeView.setDataSource(resourceContainer, mainSite);
        navigationTreeView.addExpandListener(new TreeExpandListener(
            contextRepository, containerRepository, resourceContainer));
        navigationTreeView.addClickListener(new TreeClickListener(
            contextRepository, containerRepository, itemRepository, mainSite));

        return navigationTreeView;
    }

}
