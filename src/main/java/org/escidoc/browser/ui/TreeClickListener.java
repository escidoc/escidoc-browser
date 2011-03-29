package org.escidoc.browser.ui;

import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.ContextModel;
import org.escidoc.browser.model.ItemModel;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.maincontent.ContainerView;
import org.escidoc.browser.ui.maincontent.ContextView;
import org.escidoc.browser.ui.maincontent.ItemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Component;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class TreeClickListener implements ItemClickListener {

    private static final int APP_HEIGHT = 500;

    private static final Logger LOG = LoggerFactory
        .getLogger(TreeClickListener.class);

    private final Repository contextRepository;

    private final MainSite mainSite;

    private final int appHeight;

    private final Repository containerRepository;

    private final Repository itemRepository;

    // TODO RepositoryFactory
    // repoFactory.getContainerRepo();

    public TreeClickListener(final Repository contextRepository,
        final Repository containerRepository, final Repository itemRepository,
        final MainSite mainSite) {

        Preconditions.checkNotNull(contextRepository, "repository is null: %s",
            contextRepository);
        Preconditions.checkNotNull(containerRepository,
            "containerRepository is null: %s", containerRepository);
        Preconditions.checkNotNull(itemRepository,
            "itemRepository is null: %s", itemRepository);
        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);

        this.contextRepository = contextRepository;
        this.containerRepository = containerRepository;
        this.itemRepository = itemRepository;
        this.mainSite = mainSite;

        appHeight = mainSite.getApplicationHeight();
    }

    @Override
    public void itemClick(final ItemClickEvent event) {
        final ResourceModel clickedResource = (ResourceModel) event.getItemId();

        if (ContextModel.isContext(clickedResource)) {
            LOG.debug("this is a context");
            try {
				openInNewTab(
				    new ContextView(mainSite, tryToFindResource(contextRepository,
				        clickedResource)), clickedResource);
			} catch (EscidocClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        else if (ContainerModel.isContainer(clickedResource)) {
            LOG.debug("this is a container");

            try {
                final ResourceProxy containerProxy =
                    containerRepository.findById(clickedResource.getId());

                openInNewTab(new ContainerView(mainSite, tryToFindResource(containerRepository,
				        clickedResource)), clickedResource);
            }
            catch (final EscidocClientException e) {
                showErrorMessageToUser(clickedResource, e);
            }
        }
        else if (ItemModel.isItem(clickedResource)) {
            LOG.debug("this is a item");

            try {
                final ResourceProxy itemProxy =
                    itemRepository.findById(clickedResource.getId());

                openInNewTab(new ItemView(mainSite, appHeight), clickedResource);
            }
            catch (final EscidocClientException e) {
                showErrorMessageToUser(clickedResource, e);
            }
        }
        else {
            throw new UnsupportedOperationException("Not yet implemented");
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

    // TODO implement notification.
    private void showErrorMessageToUser(
        final ResourceModel hasChildrenResource, final EscidocClientException e) {
        LOG.error("Can not find member of: " + hasChildrenResource.getId(), e);
    }
}