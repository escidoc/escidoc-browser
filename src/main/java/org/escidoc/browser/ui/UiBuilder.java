package org.escidoc.browser.ui;

import java.util.List;

import org.escidoc.browser.model.ResourceContainerImpl;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.listeners.TreeExpandListener;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class UiBuilder {

    public NavigationTreeView buildNavigationTree(final Repository repository)
        throws EscidocClientException {

        final NavigationTreeView navigationTreeView =
            new NavigationTreeViewImpl();

        final List<ResourceModel> contexts = repository.findAll();

        final ResourceContainerImpl resourceContainer =
            new ResourceContainerImpl(contexts);
        resourceContainer.init();

        navigationTreeView.setDataSource(resourceContainer);
        navigationTreeView.addExpandListener(new TreeExpandListener(repository,
            resourceContainer));
        navigationTreeView.addClickListener(new TreeClickListener(repository));

        return navigationTreeView;
    }

}
