package org.escidoc.browser.layout;

import com.google.common.base.Preconditions;

import com.vaadin.data.Container.Filterable;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

import org.escidoc.browser.model.GroupModel;
import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.TreeDataSource;
import org.escidoc.browser.repository.GroupRepository;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.View;
import org.escidoc.browser.ui.navigation.NavigationTreeBuilder;
import org.escidoc.browser.ui.orgunit.OrgUnitTreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.aa.usergroup.UserGroup;

@SuppressWarnings("serial")
public class CreateGroupView extends View {

    private static final String GROUP_NAME = "Group Name";

    private final static Logger LOG = LoggerFactory.getLogger(CreateGroupView.class);

    private OrgUnitTreeView tree;

    private TextField orgUnitFilter;

    private GroupRepository r;

    private List<ResourceModel> list;

    private TreeDataSource ds;

    private Window mw;

    public CreateGroupView(Window mw, NavigationTreeBuilder builder, GroupRepository r, TreeDataSource ds) {
        super();
        Preconditions.checkNotNull(mw, "mw is null: %s", mw);
        Preconditions.checkNotNull(builder, "builder is null: %s", builder);
        Preconditions.checkNotNull(r, "r is null: %s", r);
        Preconditions.checkNotNull(ds, "ds is null: %s", ds);
        this.mw = mw;
        tree = builder.buildOrgUnitTree();
        this.r = r;
        this.ds = ds;
    }

    public Panel buildContentPanel() {

        tree.setClickListener(new ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                Object itemId = event.getItemId();
                ResourceModel rm = (ResourceModel) itemId;
                list.add(rm);
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
        addSaveButton(layout);
        addNameField(layout);
        addOrgUnitSelection(layout);
        return layout;
    }

    private void addSaveButton(VerticalLayout layout) {
        Button saveButton = new Button(ViewConstants.SAVE, new Button.ClickListener() {

            @Override
            public void buttonClick(@SuppressWarnings("unused") ClickEvent event) {
                try {
                    ds.addTopLevelResource(new GroupModel(r.createGroup(GROUP_NAME, list)));
                }
                catch (EscidocClientException e) {
                    StringBuilder errorMessage = new StringBuilder();
                    errorMessage.append("Can not create a group. Reason: ");
                    errorMessage.append(e.getMessage());
                    LOG.warn(errorMessage.toString());
                    mw.showNotification(ViewConstants.ERROR, errorMessage.toString(),
                        Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });
        layout.addComponent(saveButton);
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