package org.escidoc.browser.ui;

import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceContainer;
import org.escidoc.browser.model.ResourceModel;

import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandListener;

@SuppressWarnings("serial")
public class NavigationTreeViewImpl extends CustomComponent
    implements NavigationTreeView {

    private final Tree tree = new Tree();

    public NavigationTreeViewImpl(MainSite mainSite) {
        setCompositionRoot(tree);
    }

    @Override
    public void addClickListener(final ItemClickListener clickListener) {
        tree.addListener(clickListener);
    }

    @Override
    public void addExpandListener(final ExpandListener expandListener) {
        tree.addListener(expandListener);
    }

    @Override
    public ResourceModel getSelected() {
        return (ResourceModel) tree.getValue();
    }

    @Override
    public void setDataSource(
        final ResourceContainer container, MainSite mainSite) {
        tree.setContainerDataSource(container.getContainer());
        tree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        tree.setItemCaptionPropertyId(PropertyId.NAME);
    }
}