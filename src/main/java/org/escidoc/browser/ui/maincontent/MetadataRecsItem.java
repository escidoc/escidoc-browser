package org.escidoc.browser.ui.maincontent;

import java.util.Iterator;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.ItemProxy;
import org.escidoc.browser.ui.listeners.RelationsClickListener;
import org.escidoc.browser.ui.listeners.VersionHistoryClickListener;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class MetadataRecsItem {
    private int height;

    private final ItemProxy resourceProxy;

    private final Window mainWindow;

    private final EscidocServiceLocation escidocServiceLocation;

    public MetadataRecsItem(ItemProxy resourceProxy, int innerelementsHeight,
        Window mainWindow, EscidocServiceLocation escidocServiceLocation) {
        Preconditions.checkNotNull(mainWindow, "resource is null.");
        this.height = innerelementsHeight;
        if (this.height < 1)
            this.height = 400;
        this.resourceProxy = resourceProxy;
        this.mainWindow = mainWindow;
        this.escidocServiceLocation = escidocServiceLocation;
    }

    public Accordion asAccord() {
        Accordion metadataRecs = new Accordion();
        metadataRecs.setSizeFull();

        Label l1 = lblMetadaRecs();
        Label l2 = lblRelations();
        Panel pnl = lblAddtionalResources();

        // Add the components as tabs in the Accordion.
        metadataRecs.addTab(l1, "Metadata Records", null);
        metadataRecs.addTab(l2, "Relations", null);
        metadataRecs.addTab(pnl, "Additional Resources", null);
        return metadataRecs;
    }

    private Panel lblAddtionalResources() {

        Button btnVersionHistory =
            new Button("Item Version History", new VersionHistoryClickListener(
                resourceProxy, mainWindow, escidocServiceLocation));
        btnVersionHistory.setStyleName(BaseTheme.BUTTON_LINK);
        btnVersionHistory.setDescription("Show Version history in a Pop-up");

        Button btnContentRelation =
            new Button("Item Content Relations", new RelationsClickListener(
                resourceProxy, mainWindow, escidocServiceLocation));
        btnContentRelation.setStyleName(BaseTheme.BUTTON_LINK);
        btnContentRelation.setDescription("Show Version history in a Pop-up");

        Button btnCMDefBehavior =
            new Button("CM-Def-Behavior", new VersionHistoryClickListener(
                resourceProxy, mainWindow, escidocServiceLocation));
        btnCMDefBehavior.setStyleName(BaseTheme.BUTTON_LINK);
        btnCMDefBehavior.setDescription("CM-Def-Behavior");

        Panel pnl = new Panel();
        pnl.setHeight(height + "px");
        pnl.addComponent(btnVersionHistory);
        pnl.addComponent(btnContentRelation);
        pnl.addComponent(btnCMDefBehavior);
        return pnl;
    }

    private Label lblRelations() {
        Iterator itr = resourceProxy.getRelations().iterator();
        String relRecords = "";
        while (itr.hasNext()) {
            relRecords += "<a href='/MISSING'>" + itr.next() + "</a><br />";
        }

        Label l2 = new Label(relRecords, Label.CONTENT_RAW);
        l2.setHeight(height + "px");
        return l2;
    }

    private Label lblMetadaRecs() {
        Iterator itr = resourceProxy.getMedataRecords().iterator();
        String mtRecords = "";
        while (itr.hasNext()) {
            mtRecords += "<a href='/MISSING'>" + itr.next() + "</a><br />";
        }

        Label l1 = new Label(mtRecords, Label.CONTENT_RAW);
        l1.setHeight(height + "px");
        return l1;
    }
}
