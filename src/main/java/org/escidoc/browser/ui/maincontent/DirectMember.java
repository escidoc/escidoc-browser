package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.ContainerRepository;
import org.escidoc.browser.repository.ContextRepository;
import org.escidoc.browser.repository.ItemRepository;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.NavigationTreeView;
import org.escidoc.browser.ui.UiBuilder;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class DirectMember {
    private final EscidocServiceLocation serviceLocation;

    private final String parentId;

    private final MainSite mainSite;

    private final Window mainWindow;

    public DirectMember(final EscidocServiceLocation serviceLocation,
        final MainSite mainSite, final String parentID, final Window mainWindow) {
        Preconditions.checkNotNull(serviceLocation,
            "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);
        Preconditions.checkNotNull(parentID, "parentID is null: %s", parentID);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s",
            mainWindow);
        this.serviceLocation = serviceLocation;
        parentId = parentID;
        this.mainSite = mainSite;
        this.mainWindow = mainWindow;
    }

    public NavigationTreeView contextAsTree() throws EscidocClientException {
        final NavigationTreeView tree =
            new UiBuilder(serviceLocation).buildContextDirectMemberTree(
                new ContextRepository(serviceLocation),
                new ContainerRepository(serviceLocation), new ItemRepository(
                    serviceLocation), mainSite, parentId, mainWindow);
        tree.setSizeFull();
        return tree;

    }

    public NavigationTreeView containerAsTree() throws EscidocClientException {

        final ContextRepository contextRepository =
            new ContextRepository(serviceLocation);

        final NavigationTreeView tree =
            new UiBuilder(serviceLocation).buildContainerDirectMemberTree(
                contextRepository, new ContainerRepository(serviceLocation),
                new ItemRepository(serviceLocation), mainSite, parentId,
                mainWindow);
        tree.setSizeFull();
        return tree;

    }

}
