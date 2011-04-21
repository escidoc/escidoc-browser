package org.escidoc.browser.ui.maincontent;

import java.util.Iterator;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.ContainerProxy;
import org.escidoc.browser.ui.listeners.CmDefBehaviourClickListener;
import org.escidoc.browser.ui.listeners.RelationsClickListener;
import org.escidoc.browser.ui.listeners.VersionHistoryClickListener;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class MetadataRecs {
    private final int height;

    private final ContainerProxy resourceProxy;

    private final Window mainWindow;

    private final EscidocServiceLocation escidocServiceLocation;

    public MetadataRecs(final ResourceProxy resourceProxy, final int innerelementsHeight, final Window mainWindow,
        final EscidocServiceLocation escidocServiceLocation) {
        Preconditions.checkNotNull(mainWindow, "resource is null.");
        height = innerelementsHeight;
        this.resourceProxy = (ContainerProxy) resourceProxy;
        this.mainWindow = mainWindow;
        this.escidocServiceLocation = escidocServiceLocation;
    }

    public Accordion asAccord() {
        final Accordion metadataRecs = new Accordion();
        metadataRecs.setSizeFull();

        final Label l1 = lblMetadaRecs();
        // final Label l2 = lblRelations();
        final Panel pnl = lblAddtionalResources();

        // Add the components as tabs in the Accordion.
        metadataRecs.addTab(l1, "Metadata Records", null);
        // metadataRecs.addTab(l2, "Relations", null);
        metadataRecs.addTab(pnl, "Additional Resources", null);
        return metadataRecs;
    }

    private Panel lblAddtionalResources() {

        final Button btnVersionHistoryContainer =
            new Button("Container Version History", new VersionHistoryClickListener(resourceProxy, mainWindow,
                escidocServiceLocation));
        btnVersionHistoryContainer.setStyleName(BaseTheme.BUTTON_LINK);
        btnVersionHistoryContainer.setDescription("Show Version history in a Pop-up");

        final Button btnContentRelation =
            new Button("Container Content Relations", new RelationsClickListener(resourceProxy, mainWindow,
                escidocServiceLocation));
        btnContentRelation.setStyleName(BaseTheme.BUTTON_LINK);
        btnContentRelation.setDescription("Show Version history in a Pop-up");

        final Button btnCMDefBehavior =
            new Button("CM-Def-Behavior", new CmDefBehaviourClickListener(
                escidocServiceLocation));
        btnCMDefBehavior.setStyleName(BaseTheme.BUTTON_LINK);
        btnCMDefBehavior.setDescription("CM-Def-Behavior");

        final Panel pnl = new Panel();
        pnl.setHeight(height + "px");
        pnl.addComponent(btnVersionHistoryContainer);
        pnl.addComponent(btnContentRelation);
        pnl.addComponent(btnCMDefBehavior);
        return pnl;
    }

    private Label lblRelations() {
        final Label l2 = new Label("Relations - Not implemented in JCLib");
        l2.setHeight(height + "px");
        return l2;
    }

    private Label lblMetadaRecs() {
        final Iterator itr = resourceProxy.getMedataRecords().iterator();
        String mtRecords = "";
        while (itr.hasNext()) {
            mtRecords += "<a href='/MISSING'>" + itr.next() + "</a><br />";
        }

        final Label l1 = new Label(mtRecords, Label.CONTENT_RAW);
        l1.setHeight(height + "px");
        return l1;
    }

}
