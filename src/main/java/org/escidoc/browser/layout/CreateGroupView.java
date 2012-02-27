package org.escidoc.browser.layout;

import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.View;
import org.escidoc.browser.ui.navigation.NavigationTreeBuilder;
import org.escidoc.browser.ui.orgunit.OrgUnitTreeView;

@SuppressWarnings("serial")
public class CreateGroupView extends View {

    private NavigationTreeBuilder builder;

    public CreateGroupView(NavigationTreeBuilder builder) {
        super();
        this.builder = builder;
    }

    public Panel buildContentPanel() {
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
        TextField tf = new TextField(ViewConstants.ORGANIZATIONAL_UNIT);
        layout.addComponent(tf);
        tf.setWidth("300px");
        addOrgTree(layout);
    }

    private void addOrgTree(VerticalLayout layout) {
        OrgUnitTreeView tree = builder.buildOrgUnitTree();
        Panel panel = new Panel();
        panel.addComponent(tree);
        layout.addComponent(panel);
    }

    private void addNameField(VerticalLayout layout) {
        TextField tf = new TextField(ViewConstants.NAME);
        tf.setWidth("300px");
        layout.addComponent(tf);
    }

    private VerticalLayout createMainLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setImmediate(false);
        layout.setWidth("100.0%");
        layout.setHeight("100.0%");
        layout.setMargin(true, true, false, true);
        return layout;
    }
}