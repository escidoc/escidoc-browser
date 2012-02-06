package org.escidoc.browser.ui.maincontent;

import com.google.common.base.Preconditions;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.internal.OrgUnitProxy;
import org.escidoc.browser.ui.ViewConstants;

import java.util.List;

public class ParentsView {

    private final OrgUnitProxy orgUnitProxy;

    public ParentsView(ResourceProxy resourceProxy) {
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        this.orgUnitProxy = (OrgUnitProxy) resourceProxy;
    }

    public Component asAccord() {
        final Accordion accordion = new Accordion();
        accordion.setSizeFull();
        accordion.addTab(buildParentsList(), ViewConstants.PARENTS, null);
        return accordion;
    }

    private Component buildParentsList() {
        Table table = new Table();
        table.setSizeFull();

        List<ResourceModel> l = orgUnitProxy.getParentList();
        BeanItemContainer<ResourceModel> dataSource = new BeanItemContainer<ResourceModel>(ResourceModel.class, l);
        dataSource.addNestedContainerProperty(PropertyId.NAME);

        table.setContainerDataSource(dataSource);
        table.setVisibleColumns(new String[] { PropertyId.NAME });
        table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
        table.setSelectable(true);
        return table;
    }
}