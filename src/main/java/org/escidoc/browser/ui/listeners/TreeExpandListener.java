package org.escidoc.browser.ui.listeners;

import java.util.List;

import org.escidoc.browser.model.ResourceContainer;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public final class TreeExpandListener implements Tree.ExpandListener {

    private static final Logger LOG = LoggerFactory
        .getLogger(TreeExpandListener.class);

    private final Repository repository;

    public TreeExpandListener(final Repository repository,
        final ResourceContainer container) {
        this.repository = repository;
        this.container = container;
    }

    private final ResourceContainer container;

    @Override
    public void nodeExpand(final ExpandEvent event) {
        final ResourceModel hasChildrenResource =
            (ResourceModel) event.getItemId();
        LOG.debug("Node to expand: " + hasChildrenResource.toString());

        if (hasChildrenResource.getType().equals(ResourceType.CONTEXT)) {
            try {
                final List<ResourceModel> children =
                    repository.findMembersById(hasChildrenResource.getId());
                for (final ResourceModel resourceModel : children) {
                    LOG.debug("child: " + resourceModel.getName());
                }
                container.addChildren(hasChildrenResource, children);
            }
            catch (final EscidocClientException e) {
                showErrorMessageToUser(hasChildrenResource, e);
            }
        }
        else {
            throw new UnsupportedOperationException("Not yet implemented");

        }
    }

    private void showErrorMessageToUser(
        final ResourceModel hasChildrenResource, final EscidocClientException e) {
        LOG.error("Can not find member of: " + hasChildrenResource.getId(), e);
    }
}