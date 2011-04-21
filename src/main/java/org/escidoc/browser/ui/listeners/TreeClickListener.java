package org.escidoc.browser.ui.listeners;

import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.ContextModel;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ItemModel;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.ViewConstant;
import org.escidoc.browser.ui.maincontent.ContainerView;
import org.escidoc.browser.ui.maincontent.ContextView;
import org.escidoc.browser.ui.maincontent.ItemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class TreeClickListener implements ItemClickListener {

    private static final Logger LOG = LoggerFactory
        .getLogger(TreeClickListener.class);

    private final Repository contextRepository;

    private final MainSite mainSite;

    private final Repository containerRepository;

    private final Repository itemRepository;

    private final EscidocServiceLocation serviceLocation;

    private final Window mainWindow;

    // TODO RepositoryFactory
    public TreeClickListener(final EscidocServiceLocation serviceLocation,
        final Repository contextRepository,
        final Repository containerRepository, final Repository itemRepository,
        final Window mainWindow, final MainSite mainSite) {

        Preconditions.checkNotNull(serviceLocation,
            "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(contextRepository,
            "contextRepository is null: %s", contextRepository);
        Preconditions.checkNotNull(containerRepository,
            "containerRepository is null: %s", containerRepository);
        Preconditions.checkNotNull(itemRepository,
            "itemRepository is null: %s", itemRepository);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s",
            mainWindow);
        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);

        this.contextRepository = contextRepository;
        this.containerRepository = containerRepository;
        this.itemRepository = itemRepository;

        this.mainWindow = mainWindow;
        this.mainSite = mainSite;
        this.serviceLocation = serviceLocation;
    }

    @Override
    public void itemClick(final ItemClickEvent event) {
        final ResourceModel clickedResource = (ResourceModel) event.getItemId();
        if (event.isDoubleClick()) {
            if (ContextModel.isContext(clickedResource)) {
                try {
                    openInNewTab(new ContextView(serviceLocation, mainSite,
                        tryToFindResource(contextRepository, clickedResource),
                        mainWindow), clickedResource);
                }
                catch (final EscidocClientException e) {
                    showErrorMessageToUser(clickedResource, e);
                }
            }

            else if (ContainerModel.isContainer(clickedResource)) {
                try {
                    openInNewTab(
                        new ContainerView(serviceLocation, mainSite,
                            tryToFindResource(containerRepository,
                                clickedResource), mainWindow), clickedResource);
                }
                catch (final EscidocClientException e) {
                    showErrorMessageToUser(clickedResource, e);
                }
            }
            else if (ItemModel.isItem(clickedResource)) {
                openInNewTab(new ItemView(serviceLocation, mainSite,
                    tryToFindResource(itemRepository, clickedResource),
                    mainWindow), clickedResource);
            }
            else {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        }
    }

    private ResourceProxy tryToFindResource(
        final Repository repository, final ResourceModel clickedResource) {
        try {
            return repository.findById(clickedResource.getId());
        }
        catch (final EscidocClientException e) {
            showErrorMessageToUser(clickedResource, e);
        }
        return null;
    }

    private void openInNewTab(
        final Component component, final ResourceModel clickedResource) {
        mainSite.openTab(component, clickedResource.getName());
    }

    private void showErrorMessageToUser(
        final ResourceModel hasChildrenResource, final EscidocClientException e) {
        LOG.error("Can not find member of: " + hasChildrenResource.getId(), e);
        mainWindow.showNotification(new Window.Notification(ViewConstant.ERROR,
            e.getMessage(), Notification.TYPE_ERROR_MESSAGE));
    }
}