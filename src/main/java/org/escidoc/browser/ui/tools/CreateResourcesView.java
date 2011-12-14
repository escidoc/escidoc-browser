package org.escidoc.browser.ui.tools;

import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.maincontent.View;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

public class CreateResourcesView extends View {

    private Router router;

    private Repositories repositories;

    public CreateResourcesView(Router router, Repositories repositories) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        this.router = router;
        this.repositories = repositories;

    }

    public void init() {
        this.setImmediate(false);
        this.setWidth("100.0%");
        this.setHeight("100.0%");
        this.setStyleName(Runo.PANEL_LIGHT);
        this.setContent(buildContentPanel());

    }

    private Panel buildContentPanel() {
        // common part: create layout
        Panel contentPanel = new Panel();
        contentPanel.setImmediate(false);
        contentPanel.setWidth("100.0%");
        contentPanel.setHeight("100.0%");

        // vlContentPanel
        VerticalLayout vlContentPanel = buildVlContentPanel();
        contentPanel.setContent(vlContentPanel);

        return contentPanel;
    }

    private VerticalLayout buildVlContentPanel() {
        // common part: create layout
        VerticalLayout vlContentPanel = new VerticalLayout();
        vlContentPanel.setImmediate(false);
        vlContentPanel.setWidth("100.0%");
        vlContentPanel.setHeight("100.0%");
        vlContentPanel.setMargin(true);

        // breadCrumpPanel
        Panel breadCrumpPanel = buildBreadCrumpPanel();
        vlContentPanel.addComponent(breadCrumpPanel);

        // pnlCreateContext
        Accordion pnlCreateContext = buildPnlCreateContext();
        vlContentPanel.addComponent(pnlCreateContext);
        vlContentPanel.setExpandRatio(pnlCreateContext, 0.3f);

        // pnlCreate
        Accordion pnlCreate = buildCreateContentModel();
        vlContentPanel.addComponent(pnlCreate);
        vlContentPanel.setExpandRatio(pnlCreate, 0.3f);

        // pnlCreateOrgUnit
        Accordion pnlCreateOrgUnit = buildPnlCreateOrgUnit();
        vlContentPanel.addComponent(pnlCreateOrgUnit);
        vlContentPanel.setExpandRatio(pnlCreateOrgUnit, 0.3f);

        return vlContentPanel;
    }

    private Panel buildBreadCrumpPanel() {
        // common part: create layout
        Panel breadCrumpPanel = new Panel();
        breadCrumpPanel.setImmediate(false);
        breadCrumpPanel.setWidth("100.0%");
        breadCrumpPanel.setHeight("30px");

        // vlBreadCrump
        VerticalLayout vlBreadCrump = new VerticalLayout();
        vlBreadCrump.setImmediate(false);
        vlBreadCrump.setWidth("100.0%");
        vlBreadCrump.setHeight("100.0%");
        vlBreadCrump.setMargin(false);
        breadCrumpPanel.setContent(vlBreadCrump);

        return breadCrumpPanel;
    }

    private Accordion buildPnlCreateContext() {
        // common part: create layout
        Accordion accCreateContext = new Accordion();
        accCreateContext.setImmediate(false);
        accCreateContext.setWidth("100.0%");
        accCreateContext.setHeight("100.0%");

        // vlPnlCreateContext
        VerticalLayout vlAccCreateContext = new VerticalLayout();
        vlAccCreateContext.setImmediate(false);
        vlAccCreateContext.setWidth("100.0%");
        vlAccCreateContext.setHeight("100.0%");
        vlAccCreateContext.setMargin(true);
        vlAccCreateContext.setSpacing(true);

        // AddContext Form
        formAddContext(vlAccCreateContext);
        accCreateContext.addTab(vlAccCreateContext, "Create Context");

        return accCreateContext;
    }

    private void formAddContext(VerticalLayout vlAccCreateContext) {
        // Name
        TextField txtNameContext = new TextField();
        txtNameContext.setCaption("Name");
        txtNameContext.setImmediate(false);
        txtNameContext.setWidth("-1px");
        txtNameContext.setHeight("-1px");
        txtNameContext.setInvalidAllowed(false);
        txtNameContext.setRequired(true);
        vlAccCreateContext.addComponent(txtNameContext);

        // Description
        TextField txtDescContext = new TextField("Description");
        txtDescContext.setImmediate(false);
        txtDescContext.setWidth("-1px");
        txtDescContext.setHeight("-1px");
        vlAccCreateContext.addComponent(txtDescContext);

        // OrgUnit
        NativeSelect slOrgUnit = new NativeSelect("Pick your Organizational Unit");
        slOrgUnit.setImmediate(false);
        slOrgUnit.setWidth("-1px");
        slOrgUnit.setHeight("-1px");
        vlAccCreateContext.addComponent(slOrgUnit);

        // button_1
        Button btnAddContext = new Button("Submit");
        btnAddContext.setImmediate(false);
        btnAddContext.setWidth("-1px");
        btnAddContext.setHeight("-1px");
        vlAccCreateContext.addComponent(btnAddContext);
    }

    private Accordion buildCreateContentModel() {
        // common part: create layout
        Accordion accCreateContentModel = new Accordion();
        accCreateContentModel.setImmediate(false);
        accCreateContentModel.setWidth("100.0%");
        accCreateContentModel.setHeight("100.0%");

        // vlPnlCreate
        VerticalLayout vlAccCreate = new VerticalLayout();
        vlAccCreate.setImmediate(false);
        vlAccCreate.setWidth("100.0%");
        vlAccCreate.setHeight("100.0%");
        vlAccCreate.setMargin(false);
        accCreateContentModel.addTab(vlAccCreate, "Create Content Model");

        return accCreateContentModel;
    }

    private Accordion buildPnlCreateOrgUnit() {
        // common part: create layout
        Accordion accCreateOrgUnit = new Accordion();
        accCreateOrgUnit.setImmediate(false);
        accCreateOrgUnit.setWidth("100.0%");
        accCreateOrgUnit.setHeight("100.0%");

        // vlPnlCreateOrgUnit
        VerticalLayout vlAccCreateOrgUnit = new VerticalLayout();
        vlAccCreateOrgUnit.setImmediate(false);
        vlAccCreateOrgUnit.setWidth("100.0%");
        vlAccCreateOrgUnit.setHeight("100.0%");
        vlAccCreateOrgUnit.setMargin(false);
        accCreateOrgUnit.addTab(vlAccCreateOrgUnit, "Create Organizational Units");

        return accCreateOrgUnit;
    }

}
