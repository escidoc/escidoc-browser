package org.escidoc.browser.ui;

import java.util.List;

import org.escidoc.browser.model.ResourceContainerImpl;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.listeners.TreeExpandListener;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class UiBuilder {

    public NavigationTreeView buildNavigationTree(
        final Repository repository, final Repository containerRepository, MainSite mainSite)
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
        navigationTreeView.addClickListener(new TreeClickListener(repository, mainSite));

        return navigationTreeView;
    }

}
