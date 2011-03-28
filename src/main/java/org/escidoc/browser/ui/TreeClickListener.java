package org.escidoc.browser.ui;

import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.ContextModel;
import org.escidoc.browser.model.ItemModel;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.maincontent.Container;
import org.escidoc.browser.ui.maincontent.Context;
import org.escidoc.browser.ui.maincontent.Item;
import org.escidoc.browser.ui.maincontent.SearchResults;
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
    private MainSite mainSite;
    public TreeClickListener(final Repository repository, MainSite mainSite) {
        Preconditions.checkNotNull(repository, "repository is null: %s",
            repository);
        this.repository = repository;
        this.mainSite=mainSite;
    }

    @Override
    public void itemClick(final ItemClickEvent event) {
        final ResourceModel clickedResource = (ResourceModel) event.getItemId();

        if (ContextModel.isContext(clickedResource)) {
        	System.out.println("this is a context");
            try {
                final ResourceProxy resourceProxy =
                    repository.findById(clickedResource.getId());
                Context cntx = new Context(mainSite,500);
        		mainSite.openTab(cntx, clickedResource.getName());
            }
            catch (final EscidocClientException e) {
                showErrorMessageToUser(clickedResource, e);
            }
        }
        else if (ContainerModel.isContainer(clickedResource)){
        	System.out.println("this is a container");
            try {
                final ResourceProxy resourceProxy =
                    repository.findById(clickedResource.getId());
        		
                Container cnt = new Container(mainSite,500);
        		mainSite.openTab(cnt, clickedResource.getName());
            }
            catch (final EscidocClientException e) {
                showErrorMessageToUser(clickedResource, e);
            }
        }    else if (ItemModel.isItem(clickedResource)){
        	System.out.println("this is a container");
            try {
                final ResourceProxy resourceProxy =
                    repository.findById(clickedResource.getId());
        		
                Item cnt = new Item(mainSite,500);
        		mainSite.openTab(cnt, clickedResource.getName());
            }
            catch (final EscidocClientException e) {
                showErrorMessageToUser(clickedResource, e);
            }
        }else{
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    // TODO implement notification.
    private void showErrorMessageToUser(
        final ResourceModel hasChildrenResource, final EscidocClientException e) {
        LOG.error("Can not find member of: " + hasChildrenResource.getId(), e);
    }
}
