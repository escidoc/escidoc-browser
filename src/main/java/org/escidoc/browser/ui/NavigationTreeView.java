package org.escidoc.browser.ui;

import org.escidoc.browser.model.ResourceContainer;
import org.escidoc.browser.model.ResourceModel;

import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree.ExpandListener;

public interface NavigationTreeView extends Component {

    void addClickListener(ItemClickListener clickListener);

    void addExpandListener(ExpandListener clickListener);

    ResourceModel getSelected();

    void setDataSource(ResourceContainer container, MainSite mainSite);

}