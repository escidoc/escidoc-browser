package org.escidoc.browser.ui;

import org.escidoc.browser.model.ContextModel;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class TreeClickListener implements ItemClickListener {
    private static final Logger LOG = LoggerFactory
        .getLogger(TreeClickListener.class);

    private final Repository repository;

    public TreeClickListener(final Repository repository) {
        Preconditions.checkNotNull(repository, "repository is null: %s",
            repository);
        this.repository = repository;
    }

    @Override
    public void itemClick(final ItemClickEvent event) {
        final ResourceModel clickedResource = (ResourceModel) event.getItemId();

        if (ContextModel.isContext(clickedResource)) {
            try {
                final ResourceProxy resourceProxy =
                    repository.findById(clickedResource.getId());
            }
            catch (final EscidocClientException e) {
                showErrorMessageToUser(clickedResource, e);
            }
        }
        else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    // TODO implement notification.
    private void showErrorMessageToUser(
        final ResourceModel hasChildrenResource, final EscidocClientException e) {
        LOG.error("Can not find member of: " + hasChildrenResource.getId(), e);
    }
}
