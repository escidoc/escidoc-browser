package org.escidoc.browser.ui.maincontent;

import com.google.common.base.Preconditions;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.BaseTheme;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ActionIdConstants;
import org.escidoc.browser.repository.internal.OrgUnitProxy;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class OrgUnitMetadataRecordsView {

    private final static Logger LOG = LoggerFactory.getLogger(OrgUnitMetadataRecordsView.class);

    private OrgUnitProxy ou;

    private Repositories repositories;

    public OrgUnitMetadataRecordsView(ResourceProxy resourceProxy, Router router) {
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        ou = (OrgUnitProxy) resourceProxy;
        repositories = router.getRepositories();
    }

    public Accordion asAccord() {
        final Accordion metadataRecs = new Accordion();
        metadataRecs.setSizeFull();
        addComponentAsTabs(metadataRecs);
        return metadataRecs;
    }

    private void addComponentAsTabs(Accordion metadataRecs) {
        metadataRecs.addTab(buildMetaData(), ViewConstants.METADATA, null);
    }

    private Component buildMetaData() {
        Panel panel = new Panel();
        panel.setHeight("100%");
        if (hasAccess()) {
            final Button btnAddNew = new Button(ViewConstants.ADD_NEW_META_DATA, new OnAddOrgUnitMetadata());
            btnAddNew.setStyleName(BaseTheme.BUTTON_LINK);
            panel.addComponent(btnAddNew);
        }

        // final MetadataRecords mdRecs = ou.getMedataRecords();
        // for (final MetadataRecord metadataRecord : mdRecs) {
        // buildMDButtons(btnaddContainer, metadataRecord);
        // }
        // pnl.addComponent(new Label("&nbsp;", Label.CONTENT_RAW));
        // pnl.addComponent(btnaddContainer);
        return panel;
    }

    private boolean hasAccess() {
        try {
            return repositories
                .pdp().forCurrentUser().isAction(ActionIdConstants.UPDATE_ORG_UNIT).forResource(ou.getId()).permitted();
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
