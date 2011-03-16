package org.escidoc.browser.ui;

import java.util.List;

import org.escidoc.browser.model.ResourceContainerImpl;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.repository.Repository;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class UiBuilder {

    public NavigationTreeView buildNavigationTree(final Repository repository)
        throws EscidocClientException {
        final NavigationTreeView navigationTreeView =
            new NavigationTreeViewImpl();

        final List<ResourceModel> contexts = repository.findAll();

        final ResourceContainerImpl resourceContainer =
            new ResourceContainerImpl(contexts);

        navigationTreeView.setDataSource(resourceContainer);

        return navigationTreeView;
    }

}
