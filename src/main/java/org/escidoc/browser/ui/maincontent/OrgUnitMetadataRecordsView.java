package org.escidoc.browser.ui.maincontent;

import java.net.URISyntaxException;

import org.escidoc.browser.controller.OrgUnitController;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.repository.internal.OrgUnitProxy;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.view.helpers.MetadataOUTableVH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class OrgUnitMetadataRecordsView {

    private final static Logger LOG = LoggerFactory.getLogger(OrgUnitMetadataRecordsView.class);

    private OrgUnitProxy ou;

    private Router router;

    private OrgUnitView view;

    private OrgUnitController controller;

    public OrgUnitMetadataRecordsView(ResourceProxy resourceProxy, Router router, OrgUnitController controller,
        OrgUnitView view) {
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(controller, "controller is null: %s", controller);
        Preconditions.checkNotNull(view, "view is null: %s", view);

        ou = (OrgUnitProxy) resourceProxy;
        this.router = router;
        this.controller = controller;
        this.view = view;
    }

    public Accordion asAccord() {
        final Accordion accordion = new Accordion();
        accordion.setSizeFull();
        accordion.addTab(buildMetaDataTab(), ViewConstants.METADATA, null);
        return accordion;
    }

    public Panel asPanel() {
        final Panel pnlmetadataRecs = new Panel();
        pnlmetadataRecs.setSizeFull();
        pnlmetadataRecs.setStyleName(Runo.PANEL_LIGHT);
        VerticalLayout vl = new VerticalLayout();
        vl.setImmediate(false);
        vl.setWidth("100.0%");
        vl.setHeight("100.0%");
        vl.setMargin(false);
        vl.addComponent(buildMetaDataTab());

        pnlmetadataRecs.setContent(vl);
        return pnlmetadataRecs;
    }

    private Component buildMetaDataTab() {
        Panel innerPanel = new Panel();
        innerPanel.setSizeFull();
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();

        final CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight("20px");
        buildPanelHeader(cssLayout, ViewConstants.METADATA);
        ThemeResource ICON = new ThemeResource("images/assets/plus.png");

        if (canAddMetadata()) {
            final Button addNewOrgUnitBtn = new Button();
            addNewOrgUnitBtn.addListener(new Button.ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    new OnAddOrgUnitMetadata(controller, router);

                }
            });
            addNewOrgUnitBtn.setStyleName(BaseTheme.BUTTON_LINK);
            addNewOrgUnitBtn.addStyleName("floatright paddingtop3");
            addNewOrgUnitBtn.setWidth("20px");
            addNewOrgUnitBtn.setIcon(ICON);
            cssLayout.addComponent(addNewOrgUnitBtn);
        }
        vl.addComponent(cssLayout);
        MetadataOUTableVH metadataTable = new MetadataOUTableVH(ou.getMedataRecords(), controller, router);
        vl.addComponent(metadataTable);
        vl.setExpandRatio(metadataTable, 9);
        innerPanel.setContent(vl);
        return innerPanel;
    }

    private void buildPanelHeader(CssLayout cssLayout, String name) {
        cssLayout.addStyleName("v-accordion-item-caption v-caption v-captiontext");
        cssLayout.setWidth("100%");
        cssLayout.setMargin(false);

        final Label nameofPanel = new Label(name, Label.CONTENT_RAW);
        nameofPanel.setStyleName("accordion v-captiontext");
        nameofPanel.setWidth("70%");
        cssLayout.addComponent(nameofPanel);
    }

    private boolean canAddMetadata() {
        try {
            return router
                .getRepositories().pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_ORG_UNIT)
                .forResource(ou.getId()).permitted();
        }
        catch (final EscidocClientException e) {
            LOG.debug(e.getLocalizedMessage());
            return false;
        }
        catch (final URISyntaxException e) {
            LOG.debug(e.getLocalizedMessage());
            return false;
        }
    }

}
