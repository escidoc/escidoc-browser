package org.escidoc.browser.ui.maincontent;

import com.google.common.base.Preconditions;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Runo;

import org.escidoc.browser.controller.OrgUnitController;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.repository.internal.OrgUnitProxy;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;

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
            addNewOrgUnitBtn.addListener(new OnAddOrgUnitMetadata(ou, controller, router.getRepositories(), router
                .getMainWindow()));
            addNewOrgUnitBtn.setStyleName(BaseTheme.BUTTON_LINK);
            addNewOrgUnitBtn.addStyleName("floatright paddingtop3");
            addNewOrgUnitBtn.setWidth("20px");
            addNewOrgUnitBtn.setIcon(ICON);
            cssLayout.addComponent(addNewOrgUnitBtn);
        }
        vl.addComponent(cssLayout);
        final MetadataRecords mdList = ou.getMedataRecords();
        for (final MetadataRecord metadataRecord : mdList) {
            final HorizontalLayout hl = new HorizontalLayout();
            hl.setStyleName("metadata");

            Link mdLink =
                new Link(metadataRecord.getName(), new ExternalResource(router.getServiceLocation().getEscidocUri()
                    + metadataRecord.getXLinkHref()));
            mdLink.setTargetName("_blank");
            mdLink.setStyleName(BaseTheme.BUTTON_LINK);
            mdLink.setDescription("Show metadata information in a separate window");
            hl.addComponent(mdLink);
            hl.addComponent(new Label("&nbsp; | &nbsp;", Label.CONTENT_RAW));
            if (canAddMetadata()) {
                final Button editMdBtn =
                    new Button("edit", new OnEditOrgUnitMetadata(metadataRecord, router, router.getRepositories(), ou,
                        view));
                editMdBtn.setStyleName(BaseTheme.BUTTON_LINK);
                editMdBtn.setDescription("Replace the metadata with a new content file");
                hl.addComponent(editMdBtn);
            }
            vl.addComponent(hl);
            vl.setExpandRatio(hl, 9);
        }

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
