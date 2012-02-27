package org.escidoc.browser.layout;

import com.vaadin.data.Container.Filterable;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.View;
import org.escidoc.browser.ui.navigation.NavigationTreeBuilder;
import org.escidoc.browser.ui.orgunit.OrgUnitTreeView;

@SuppressWarnings("serial")
public class CreateGroupView extends View {

    private OrgUnitTreeView tree;

    private TextField orgUnitFilter;

    public CreateGroupView(NavigationTreeBuilder builder) {
        super();
        tree = builder.buildOrgUnitTree();
    }

    public Panel buildContentPanel() {

        tree.setClickListener(new ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                Object itemId = event.getItemId();
                ResourceModel rm = (ResourceModel) itemId;
                orgUnitFilter.setValue(rm.getName());
            }
        });
        setImmediate(false);
        setWidth("100.0%");
        setHeight("100.0%");
        setStyleName(Runo.PANEL_LIGHT);

        setContent(buildVlContentPanel());
        return this;
    }

    private ComponentContainer buildVlContentPanel() {
        VerticalLayout layout = createMainLayout();
        addNameField(layout);
        addOrgUnitSelection(layout);
        return layout;
    }

    private void addOrgUnitSelection(VerticalLayout layout) {
        orgUnitFilter = new TextField(ViewConstants.ORGANIZATIONAL_UNIT);
        layout.addComponent(orgUnitFilter);
        orgUnitFilter.setWidth("300px");

        orgUnitFilter.addListener(new TextChangeListener() {

            private SimpleStringFilter filter;

            @Override
            public void textChange(TextChangeEvent event) {
                // TODO refactor this, the list should not return the data source
                Filterable ds = (Filterable) tree.getDataSource();
                ds.removeAllContainerFilters();
                filter = new SimpleStringFilter(PropertyId.NAME, event.getText(), true, false);
                ds.addContainerFilter(filter);
            }
        });
        addOrgTree(layout);
    }

    private void addOrgTree(VerticalLayout layout) {
        VerticalLayout vl = new VerticalLayout();
        vl.setHeight("60%");

        vl.addComponent(tree);
        layout.addComponent(vl);
    }

    private static void addNameField(VerticalLayout layout) {
        TextField tf = new TextField(ViewConstants.NAME);
        tf.setWidth("300px");
        layout.addComponent(tf);
    }

    private static VerticalLayout createMainLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setImmediate(false);
        layout.setWidth("100.0%");
        layout.setHeight("100.0%");
        layout.setMargin(true, true, false, true);
        return layout;
    }
}