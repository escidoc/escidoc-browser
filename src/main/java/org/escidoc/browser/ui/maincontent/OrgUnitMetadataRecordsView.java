package org.escidoc.browser.ui.maincontent;

import com.google.common.base.Preconditions;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

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

    private VerticalLayout vl = new VerticalLayout();

    private Panel outerPanel = new Panel();

    private OrgUnitProxy ou;

    private Router router;

    private OrgUnitView view;

    public OrgUnitMetadataRecordsView(ResourceProxy resourceProxy, Router router, OrgUnitView view) {
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(view, "view is null: %s", view);

        ou = (OrgUnitProxy) resourceProxy;
        this.router = router;
        this.view = view;
    }

    public Accordion asAccord() {
        final Accordion accordion = new Accordion();
        accordion.setSizeFull();
        accordion.addTab(buildMetaDataTab(), ViewConstants.METADATA, null);
        return accordion;
    }

    private Component buildMetaDataTab() {
        Panel innerPanel = new Panel();
        innerPanel.setHeight("100%");

        if (canAddMetadata()) {
            final Button addNewOrgUnitBtn =
                new Button(ViewConstants.ADD_NEW_META_DATA, new OnAddOrgUnitMetadata(ou, router.getRepositories(),
                    router.getMainWindow()));
            addNewOrgUnitBtn.setStyleName(BaseTheme.BUTTON_LINK);
            innerPanel.addComponent(addNewOrgUnitBtn);
        }

        final MetadataRecords mdList = ou.getMedataRecords();
        for (final MetadataRecord metadataRecord : mdList) {
            final HorizontalLayout hl = new HorizontalLayout();
            hl.setStyleName("metadata");
            vl.addComponent(hl);

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
            innerPanel.addComponent(hl);
        }
        outerPanel.addComponent(new Label("&nbsp;", Label.CONTENT_RAW));
        outerPanel.addComponent(vl);
        return innerPanel;
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
