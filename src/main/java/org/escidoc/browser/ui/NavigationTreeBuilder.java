package org.escidoc.browser.ui;

import java.util.List;

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceContainer;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.internal.ResourceContainerImpl;
import org.escidoc.browser.repository.ContextRepository;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.listeners.TreeClickListener;
import org.escidoc.browser.ui.listeners.TreeExpandListener;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class NavigationTreeBuilder {

    private final EscidocServiceLocation serviceLocation;

    private final CurrentUser currentUser;

    private TreeClickListener clickListener;

    public NavigationTreeBuilder(final EscidocServiceLocation serviceLocation, final CurrentUser currentUser) {
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);
        this.serviceLocation = serviceLocation;
        this.currentUser = currentUser;

    }

    public NavigationTreeView buildNavigationTree(
        final Repository contextRepository, final Repository containerRepository, final Repository itemRepository,
        final MainSite mainSite, final Window mainWindow) throws EscidocClientException {

        final NavigationTreeView navigationTreeView = new NavigationTreeViewImpl(mainSite);

        final ResourceContainer resourceContainer =
            new ResourceContainerImpl(((ContextRepository) contextRepository).findAllWithChildrenInfo());
        resourceContainer.init();

        clickListener =
            new TreeClickListener(serviceLocation, contextRepository, containerRepository, itemRepository, mainWindow,
                mainSite, currentUser);

        navigationTreeView.setDataSource(resourceContainer, mainSite);
        navigationTreeView.addExpandListener(new TreeExpandListener(contextRepository, containerRepository,
            resourceContainer));
        navigationTreeView.addClickListener(clickListener);

        return navigationTreeView;
    }

    public NavigationTreeView buildContextDirectMemberTree(
        final Repository contextRepository, final Repository containerRepository, final Repository itemRepository,
        final MainSite mainSite, final String parentID, final Window mainWindow) throws EscidocClientException {

        final NavigationTreeView navigationTreeView = new NavigationTreeViewImpl(mainSite);

        final List<ResourceModel> contexts = contextRepository.findTopLevelMembersById(parentID);

        final ResourceContainerImpl resourceContainer = new ResourceContainerImpl(contexts);
        resourceContainer.init();

        clickListener =
            new TreeClickListener(serviceLocation, contextRepository, containerRepository, itemRepository, mainWindow,
                mainSite, currentUser);

        navigationTreeView.setDataSource(resourceContainer, mainSite);
        navigationTreeView.addClickListener(clickListener);

        return navigationTreeView;
    }

    public NavigationTreeView buildContainerDirectMemberTree(
        final Repository contextRepository, final Repository containerRepository, final Repository itemRepository,
        final MainSite mainSite, final String parentID, final Window mainWindow) throws EscidocClientException {

        final NavigationTreeView navigationTreeView = new NavigationTreeViewImpl(mainSite);

        final List<ResourceModel> container = containerRepository.findTopLevelMembersById(parentID);

        final ResourceContainerImpl resourceContainer = new ResourceContainerImpl(container);
        resourceContainer.init();

        navigationTreeView.setDataSource(resourceContainer, mainSite);

        clickListener =
            new TreeClickListener(serviceLocation, contextRepository, containerRepository, itemRepository, mainWindow,
                mainSite, currentUser);
        navigationTreeView.addClickListener(clickListener);
        return navigationTreeView;
    }

}
