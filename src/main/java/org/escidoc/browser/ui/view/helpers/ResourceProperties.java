package org.escidoc.browser.ui.view.helpers;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

public class ResourceProperties {

    protected static final Logger LOG = LoggerFactory.getLogger(ResourcePropertiesVH.class);

    protected String status;

    protected Label lblStatus;

    protected ResourceProxy resourceProxy;

    protected CssLayout cssLayout;

    protected final VerticalLayout vlPropertiesLeft = new VerticalLayout();

    protected Label nameLabel;

    public ResourceProperties() {
        super();
    }

    public void buildViews() {
        createLayout();
        createBreadcrump();
        createPermanentLink();
        createResourceLinks();
        bindNametoHeader();
        bindDescription();
        bindHrRuler();
        bindProperties();
    }

    protected void createPermanentLink() {

    }

    protected void createResourceLinks() {
        // TODO Auto-generated method stub

    }

    protected void bindDescription() {
        Label descLabel = new Label(ViewConstants.DESCRIPTION_LBL + resourceProxy.getDescription());
        descLabel.setDescription("header");
        cssLayout.addComponent(descLabel);
    }

    public CssLayout getContentLayout() {
        return cssLayout;
    }

    public void createLayout() {
        cssLayout = new CssLayout();
        cssLayout.setWidth("100%");
        cssLayout.setHeight("100%");
        cssLayout.setMargin(false);
    }

    protected void bindProperties() {

        final Panel pnlPropertiesLeft = buildLeftPropertiesPnl();
        final Panel pnlPropertiesRight = buildRightPnlProperties();

        final Label descMetadata1 = new Label("ID: " + resourceProxy.getId());

        status = resourceProxy.getType().getLabel() + " is ";
        lblStatus = new Label(status + resourceProxy.getStatus(), Label.CONTENT_RAW);
        lblStatus.setDescription(ViewConstants.DESC_STATUS2);

        final Label descMetadata2 =
            new Label(ViewConstants.CREATED_BY + " " + resourceProxy.getCreator() + " on "
                + resourceProxy.getCreatedOn() + "<br/>" + ViewConstants.LAST_MODIFIED_BY + " "
                + resourceProxy.getModifier() + " on " + resourceProxy.getModifiedOn(), Label.CONTENT_XHTML);

        vlPropertiesLeft.addComponent(descMetadata1);
        vlPropertiesLeft.addComponent(lblStatus);

        pnlPropertiesLeft.addComponent(vlPropertiesLeft);
        cssLayout.addComponent(pnlPropertiesLeft);

        pnlPropertiesRight.addComponent(descMetadata2);
        cssLayout.addComponent(pnlPropertiesRight);

    }

    protected Panel buildLeftPropertiesPnl() {
        final Panel pnlPropertiesLeft = new Panel();
        pnlPropertiesLeft.setWidth("40%");
        pnlPropertiesLeft.setHeight("50px");
        pnlPropertiesLeft.setStyleName(ViewConstants.FLOAT_LEFT);
        pnlPropertiesLeft.addStyleName(Runo.PANEL_LIGHT);
        pnlPropertiesLeft.getLayout().setMargin(false);
        return pnlPropertiesLeft;
    }

    protected Panel buildRightPnlProperties() {
        final Panel pnlPropertiesRight = new Panel();
        pnlPropertiesRight.setWidth("60%");
        pnlPropertiesRight.setHeight("50px");
        pnlPropertiesRight.setStyleName(ViewConstants.FLOAT_RIGHT);
        pnlPropertiesRight.addStyleName(Runo.PANEL_LIGHT);
        pnlPropertiesRight.getLayout().setMargin(false);
        return pnlPropertiesRight;
    }

    protected void bindHrRuler() {
        final Label descRuler = new Label("<hr/>", Label.CONTENT_RAW);
        descRuler.setStyleName("hr");
        cssLayout.addComponent(descRuler);
    }

    protected void bindNametoHeader() {
        nameLabel = new Label(resourceProxy.getName());
        nameLabel.setDescription("header");
        nameLabel.setStyleName("h1 fullwidth");
        cssLayout.addComponent(nameLabel);
    }

    protected void createBreadcrump() {
        new BreadCrumbMenu(cssLayout, resourceProxy.getName().toString());
    }

    protected void showEditableFields() {
    }

    protected CssLayout getPropertiesLayout() {
        return cssLayout;
    }
}