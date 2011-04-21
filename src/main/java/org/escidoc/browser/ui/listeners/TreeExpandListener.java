package org.escidoc.browser.ui.listeners;

import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.ContextModel;
import org.escidoc.browser.model.ItemModel;
import org.escidoc.browser.model.ResourceContainer;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public final class TreeExpandListener implements Tree.ExpandListener {

    private static final Logger LOG = LoggerFactory.getLogger(TreeExpandListener.class);

    private final Repository contextRepository;

    private final Repository containerRepository;

    private final ResourceContainer container;

    public TreeExpandListener(final Repository contextRepository, final Repository containerRepository,
        final ResourceContainer container) {
        Preconditions.checkNotNull(contextRepository, "repository is null: %s", contextRepository);
        Preconditions.checkNotNull(containerRepository, "containerRepository is null: %s", containerRepository);
        Preconditions.checkNotNull(container, "container is null: %s", container);

        this.contextRepository = contextRepository;
        this.containerRepository = containerRepository;
        this.container = container;
    }

    @Override
    public void nodeExpand(final ExpandEvent event) {
        final ResourceModel resource = (ResourceModel) event.getItemId();
        LOG.debug("Node to expand: " + resource.toString());

        if (ContextModel.isContext(resource)) {
            addContextChildren(resource);
        }
        else if (ContainerModel.isContainer(resource)) {
            addContainerChildren(resource);
        }
        else if (ItemModel.isItem(resource)) {
            LOG.debug("do nothing, an item does not have any members.");
        }
        else {
            throw new UnsupportedOperationException("Unknown Type: " + resource);
        }

    }

    private void addContainerChildren(final ResourceModel resource) {
        try {
            container.addChildren(resource, containerRepository.findTopLevelMembersById(resource.getId()));
        }
        catch (final EscidocClientException e) {
            showErrorMessageToUser(resource, e);
        }
    }

    private void addContextChildren(final ResourceModel resource) {
        try {
            container.addChildren(resource, contextRepository.findTopLevelMembersById(resource.getId()));
        }
        catch (final EscidocClientException e) {
            showErrorMessageToUser(resource, e);
        }
    }

    // TODO: show notification to user, not just log.
    private void showErrorMessageToUser(final ResourceModel hasChildrenResource, final EscidocClientException e) {
        LOG.error("Can not find member of: " + hasChildrenResource.getId(), e);
    }
}