package org.escidoc.browser.ui.maincontent;

import java.util.List;
import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.EscidocServiceLocationImpl;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.ContainerRepository;
import org.escidoc.browser.repository.ContextRepository;
import org.escidoc.browser.repository.ItemRepository;
import org.escidoc.browser.ui.MainSite;
import org.escidoc.browser.ui.NavigationTreeView;
import org.escidoc.browser.ui.UiBuilder;

import com.vaadin.ui.Component;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Tree;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class DirectMember {
	final EscidocServiceLocationImpl serviceLocation =
        new EscidocServiceLocationImpl(AppConstants.HARDCODED_ESCIDOC_URI);
	private String parentID;
	private MainSite mainSite;

	public DirectMember(MainSite mainSite,String parentID) {
		this.parentID=parentID;
		this.mainSite = mainSite;
	}

    public NavigationTreeView contextasTree() throws EscidocClientException {
        final NavigationTreeView tree =
            new UiBuilder().buildContextDirectMemberTree(new ContextRepository(
                serviceLocation), new ContainerRepository(serviceLocation),
                new ItemRepository(serviceLocation), mainSite, parentID);
        
        tree.setSizeFull();
        return tree;

    }

    public NavigationTreeView containerasTree() throws EscidocClientException {
        final NavigationTreeView tree =
            new UiBuilder().buildContainerDirectMemberTree(new ContextRepository(
                serviceLocation), new ContainerRepository(serviceLocation),
                new ItemRepository(serviceLocation), mainSite, parentID);
        
        tree.setSizeFull();
        return tree;

    }

}
