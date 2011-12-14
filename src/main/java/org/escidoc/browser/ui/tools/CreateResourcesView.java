package org.escidoc.browser.ui.tools;

import java.util.Collection;

import org.escidoc.browser.model.OrgUnitService;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.listeners.CreateResourceAddContextListener;
import org.escidoc.browser.ui.maincontent.View;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.oum.OrganizationalUnit;

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
        try {
            formAddContext(vlAccCreateContext);
        }
        catch (EscidocClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        accCreateContext.addTab(vlAccCreateContext, "Create Context");

        return accCreateContext;
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

    private void formAddContext(VerticalLayout vlAccCreateContext) throws EscidocClientException {
        final Form frm = new Form();
        frm.setImmediate(true);
        // Name
        final TextField txtNameContext = new TextField();
        txtNameContext.setCaption("Name");
        txtNameContext.setImmediate(false);
        txtNameContext.setWidth("-1px");
        txtNameContext.setHeight("-1px");
        txtNameContext.setInvalidAllowed(false);
        txtNameContext.setRequired(true);
        frm.addField("txtNameContext", txtNameContext);

        // Description
        final TextField txtDescContext = new TextField("Description");
        txtDescContext.setImmediate(false);
        txtDescContext.setWidth("-1px");
        txtDescContext.setHeight("-1px");
        frm.addField("txtDescContext", txtDescContext);

        // Description
        final TextField txtType = new TextField("Type");
        txtType.setImmediate(false);
        txtType.setWidth("-1px");
        txtType.setHeight("-1px");
        frm.addField("txtType", txtType);

        // OrgUnit
        final NativeSelect slOrgUnit = new NativeSelect("Pick your Organizational Unit");
        slOrgUnit.setImmediate(true);
        slOrgUnit.setWidth("-1px");
        slOrgUnit.setHeight("-1px");
        slOrgUnit.setRequired(true);
        slOrgUnit.setNullSelectionAllowed(false);
        frm.addField("slOrgUnit", slOrgUnit);

        final OrgUnitService orgUnitService =
            new OrgUnitService(router.getServiceLocation().getEscidocUri(), router.getApp().getCurrentUser().getToken());
        Collection<OrganizationalUnit> orgUnits = orgUnitService.findAll();
        for (OrganizationalUnit organizationalUnit : orgUnits) {
            slOrgUnit.addItem(organizationalUnit.getObjid());
            slOrgUnit.setItemCaption(organizationalUnit.getObjid(), organizationalUnit.getXLinkTitle());
        }
        frm.getLayout().addComponent(slOrgUnit);

        // btnAddContext
        Button btnAddContext = new Button("Submit", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    frm.commit();
                    new CreateResourceAddContextListener(txtNameContext.getValue().toString(), txtDescContext
                        .getValue().toString(), txtType.getValue().toString(), slOrgUnit.getValue().toString(),
                        repositories, router.getServiceLocation());
                    router.getMainWindow().showNotification(
                        "Context " + txtNameContext.getValue().toString() + " created successfully ",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
                }
                catch (EscidocClientException e) {
                    router.getMainWindow().showNotification("Please fill in all the required elements in the form",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
                }
            }
        });

        btnAddContext.setWidth("-1px");
        btnAddContext.setHeight("-1px");
        frm.getLayout().addComponent(btnAddContext);

        frm.getField("txtNameContext").setRequired(true);
        frm.getField("txtNameContext").setRequiredError("Name is missing");

        frm.getField("txtDescContext").setRequired(true);
        frm.getField("txtDescContext").setRequiredError("Description is missing");

        frm.getField("txtType").setRequired(true);
        frm.getField("txtType").setRequiredError("Context Type is missing");

        frm.getField("slOrgUnit").setRequired(true);
        frm.getField("slOrgUnit").setRequiredError("Organizazional Unit is required");

        vlAccCreateContext.addComponent(frm);
    }
}
