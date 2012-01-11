package org.escidoc.browser.ui.tools;

import com.google.common.base.Preconditions;

import com.vaadin.data.Validator.EmptyValueException;
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

import org.escidoc.browser.controller.CreateResourcesController;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.OrgUnitService;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.View;

import java.util.Collection;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.oum.OrganizationalUnit;

@SuppressWarnings("serial")
public class CreateResourcesView extends View {

    private final Router router;

    private final Repositories repositories;

    private final CreateResourcesController controller;

    public CreateResourcesView(final Router router, final Repositories repositories,
        final CreateResourcesController controller) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        this.router = router;
        this.repositories = repositories;
        this.controller = controller;
        init();
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
        final Panel contentPanel = new Panel();
        contentPanel.setImmediate(false);
        contentPanel.setWidth("100.0%");
        contentPanel.setHeight("100.0%");

        // vlContentPanel
        final VerticalLayout vlContentPanel = buildVlContentPanel();
        contentPanel.setContent(vlContentPanel);

        return contentPanel;
    }

    private VerticalLayout buildVlContentPanel() {
        // common part: create layout
        final VerticalLayout vlContentPanel = new VerticalLayout();
        vlContentPanel.setImmediate(false);
        vlContentPanel.setWidth("100.0%");
        vlContentPanel.setHeight("100.0%");
        vlContentPanel.setMargin(false);

        // pnlCreateContext
        final Accordion pnlCreateContext = buildPnlCreateContext();
        vlContentPanel.addComponent(pnlCreateContext);
        vlContentPanel.setExpandRatio(pnlCreateContext, 0.4f);

        // pnlCreate
        final Accordion pnlCreate = buildCreateContentModel();
        vlContentPanel.addComponent(pnlCreate);
        vlContentPanel.setExpandRatio(pnlCreate, 0.3f);

        // pnlCreateOrgUnit
        final Accordion pnlCreateOrgUnit = buildPnlCreateOrgUnit();
        vlContentPanel.addComponent(pnlCreateOrgUnit);
        vlContentPanel.setExpandRatio(pnlCreateOrgUnit, 0.3f);

        return vlContentPanel;
    }

    private Accordion buildPnlCreateContext() {
        // common part: create layout
        final Accordion accCreateContext = new Accordion();
        accCreateContext.setImmediate(false);
        accCreateContext.setWidth("100.0%");
        accCreateContext.setHeight("100.0%");

        // vlPnlCreateContext
        final VerticalLayout vlAccCreateContext = new VerticalLayout();
        vlAccCreateContext.setImmediate(false);
        vlAccCreateContext.setWidth("100.0%");
        vlAccCreateContext.setHeight("100.0%");
        vlAccCreateContext.setMargin(false);
        vlAccCreateContext.setSpacing(false);
        vlAccCreateContext.setSizeUndefined();

        // AddContext Form
        try {
            formAddContext(vlAccCreateContext);
        }
        catch (final EscidocClientException e) {
            router.getMainWindow().showNotification(ViewConstants.ERROR_CREATING_RESOURCE + e.getLocalizedMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }
        accCreateContext.addTab(vlAccCreateContext, "Create Context");

        return accCreateContext;
    }

    private Accordion buildCreateContentModel() {
        // common part: create layout
        final Accordion accCreateContentModel = new Accordion();
        accCreateContentModel.setImmediate(false);
        accCreateContentModel.setWidth("100.0%");
        accCreateContentModel.setHeight("100.0%");

        // vlPnlCreate
        final VerticalLayout vlAccCreate = new VerticalLayout();
        vlAccCreate.setImmediate(false);
        vlAccCreate.setWidth("100.0%");
        vlAccCreate.setHeight("100.0%");
        vlAccCreate.setMargin(false);
        formAddContentModel(vlAccCreate);
        accCreateContentModel.addTab(vlAccCreate, "Create Content Model");

        return accCreateContentModel;
    }

    private Accordion buildPnlCreateOrgUnit() {
        // common part: create layout
        final Accordion accCreateOrgUnit = new Accordion();
        accCreateOrgUnit.setImmediate(false);
        accCreateOrgUnit.setWidth("100.0%");
        accCreateOrgUnit.setHeight("100.0%");

        // vlPnlCreateOrgUnit
        final VerticalLayout vlAccCreateOrgUnit = new VerticalLayout();
        vlAccCreateOrgUnit.setImmediate(false);
        vlAccCreateOrgUnit.setWidth("100.0%");
        vlAccCreateOrgUnit.setHeight("100.0%");
        vlAccCreateOrgUnit.setMargin(false);
        formAddOrgUnit(vlAccCreateOrgUnit);
        accCreateOrgUnit.addTab(vlAccCreateOrgUnit, "Create Organizational Units");

        return accCreateOrgUnit;
    }

    private void formAddOrgUnit(final VerticalLayout vlAccCreateOrgUnit) {
        vlAccCreateOrgUnit.addComponent(buildCreateOrgUnitForm());
    }

    private Form buildCreateOrgUnitForm() {
        final Form form = new Form();
        form.setImmediate(true);

        // Name
        final TextField txtNameContext = new TextField();
        txtNameContext.setCaption("Name");
        txtNameContext.setImmediate(false);
        txtNameContext.setWidth("-1px");
        txtNameContext.setHeight("-1px");
        txtNameContext.setInvalidAllowed(false);
        txtNameContext.setRequired(true);
        form.addField("txtNameContext", txtNameContext);

        // Description
        final TextField txtDescContext = new TextField("Description");
        txtDescContext.setImmediate(false);
        txtDescContext.setWidth("-1px");
        txtDescContext.setHeight("-1px");
        form.addField("txtDescContext", txtDescContext);

        // btnAddContext
        final Button btnAddContext = new Button("Submit", new Button.ClickListener() {
            private static final long serialVersionUID = -1373866726572059290L;

            @Override
            public void buttonClick(final ClickEvent event) {
                try {
                    form.commit();
                    controller.createResourceAddOrgUnit(txtNameContext.getValue().toString(), txtDescContext
                        .getValue().toString(), router, router.getServiceLocation());
                    router.getMainWindow().showNotification(
                        "Organizational Unit " + txtNameContext.getValue().toString() + " created successfully ",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
                    form.getField("txtNameContext").setValue("");
                    form.getField("txtDescContext").setValue("");
                }
                catch (final EmptyValueException e) {
                    router.getMainWindow().showNotification("Please fill in all the required elements in the form",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
                }
                catch (final Exception e) {
                    router.getMainWindow().showNotification(
                        ViewConstants.ERROR_CREATING_RESOURCE + e.getLocalizedMessage(),
                        Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });

        btnAddContext.setWidth("-1px");
        btnAddContext.setHeight("-1px");
        form.getLayout().addComponent(btnAddContext);

        form.getField("txtNameContext").setRequired(true);
        form.getField("txtNameContext").setRequiredError("Name is missing");

        form.getField("txtDescContext").setRequired(true);
        form.getField("txtDescContext").setRequiredError("Description is missing");
        return form;
    }

    private void formAddContentModel(final VerticalLayout vlAccCreateContentModel) {
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

        // btnAddContext
        final Button btnAddContext = new Button("Submit", new Button.ClickListener() {
            private static final long serialVersionUID = -6461338505705399082L;

            @Override
            public void buttonClick(final ClickEvent event) {
                try {
                    frm.commit();
                    controller.createResourceAddContentModel(txtNameContext.getValue().toString(), txtDescContext
                        .getValue().toString(), router, router.getServiceLocation());
                    router.getMainWindow().showNotification(
                        "ContentModel " + txtNameContext.getValue().toString() + " created successfully ",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
                    frm.getField("txtNameContext").setValue("");
                    frm.getField("txtDescContext").setValue("");
                }
                catch (final EmptyValueException e) {
                    router.getMainWindow().showNotification("Please fill in all the required elements in the form",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
                }
                catch (final EscidocClientException e) {
                    router.getMainWindow().showNotification(
                        ViewConstants.ERROR_CREATING_RESOURCE + e.getLocalizedMessage(),
                        Window.Notification.TYPE_ERROR_MESSAGE);
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

        vlAccCreateContentModel.addComponent(frm);
    }

    private void formAddContext(final VerticalLayout vlAccCreateContext) throws EscidocClientException {
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
        final NativeSelect slOrgUnit = new NativeSelect("Organizational Unit");
        slOrgUnit.setImmediate(true);
        slOrgUnit.setWidth("-1px");
        slOrgUnit.setHeight("-1px");
        slOrgUnit.setRequired(true);
        slOrgUnit.setNullSelectionAllowed(false);
        frm.addField("slOrgUnit", slOrgUnit);

        final OrgUnitService orgUnitService =
            new OrgUnitService(router.getServiceLocation().getEscidocUri(), router.getApp().getCurrentUser().getToken());
        final Collection<OrganizationalUnit> orgUnits = orgUnitService.findAll();
        for (final OrganizationalUnit organizationalUnit : orgUnits) {
            slOrgUnit.addItem(organizationalUnit.getObjid());
            slOrgUnit.setItemCaption(organizationalUnit.getObjid(), organizationalUnit.getXLinkTitle());
        }
        frm.getLayout().addComponent(slOrgUnit);

        // btnAddContext
        final Button btnAddContext = new Button("Submit", new Button.ClickListener() {
            private static final long serialVersionUID = -4696167135894721166L;

            @Override
            public void buttonClick(final ClickEvent event) {
                try {
                    frm.commit();
                    controller.createResourceAddContext(txtNameContext.getValue().toString(), txtDescContext
                        .getValue().toString(), txtType.getValue().toString(), slOrgUnit.getValue().toString(),
                        repositories);
                    router.getMainWindow().showNotification(
                        "Context " + txtNameContext.getValue().toString() + " created successfully ",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
                    frm.getField("txtNameContext").setValue("");
                    frm.getField("txtDescContext").setValue("");
                    frm.getField("txtType").setValue("");
                    frm.getField("slOrgUnit").setValue(null);
                    // Ideally here should be a sync method to sync the tree

                }
                catch (final EmptyValueException e) {
                    router.getMainWindow().showNotification("Please fill in all the required elements in the form",
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
                }
                catch (final EscidocClientException e) {
                    router.getMainWindow().showNotification(
                        ViewConstants.ERROR_CREATING_RESOURCE + e.getLocalizedMessage(),
                        Window.Notification.TYPE_ERROR_MESSAGE);
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
        //
        frm.getField("slOrgUnit").setRequired(true);
        frm.getField("slOrgUnit").setRequiredError("Organizazional Unit is required");

        vlAccCreateContext.addComponent(frm);
    }
}
