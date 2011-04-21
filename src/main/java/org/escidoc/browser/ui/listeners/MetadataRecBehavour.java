package org.escidoc.browser.ui.listeners;

import org.escidoc.browser.model.EscidocServiceLocation;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

import de.escidoc.core.resources.common.MetadataRecord;

public class MetadataRecBehavour implements ClickListener {

    private static final String NAME = "Name :";

    private static final String RECORD_TYPE = "Record Type";

    private static final String RECORD_SCHEMA = "Record Schema ";

    private static final String LINK = "Link ";

    private static final String CONTENT = "Content ";

    private final Window mainWindow;

    MetadataRecord metadataRecord;

    private final EscidocServiceLocation escidocServiceLocation;

    public MetadataRecBehavour(MetadataRecord metadataRecord, Window mainWindow,
        EscidocServiceLocation escidocServiceLocation) {
        this.mainWindow = mainWindow;
        this.metadataRecord = metadataRecord;
        this.escidocServiceLocation = escidocServiceLocation;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        Window subwindow = new Window("MetadataRecs");
        subwindow.setWidth("600px");
        subwindow.setModal(true);

        String mtRecinfo =
            new String(NAME + metadataRecord.getName() + "<br />" + CONTENT + metadataRecord.getContent() + "<br />"
                + RECORD_TYPE + metadataRecord.getMdType() + "<br />" + RECORD_SCHEMA + metadataRecord.getSchema()
                + "<br />" + LINK + "<a href='" + escidocServiceLocation.getEscidocUri()
                + metadataRecord.getXLinkHref() + "' target='_blank'>" + metadataRecord.getXLinkTitle() + "</a><br />");

        Label msgWindow = new Label(mtRecinfo, Label.CONTENT_RAW);

        subwindow.addComponent(msgWindow);
        if (subwindow.getParent() != null) {
            mainWindow.showNotification("Window is already open");
        }
        else {
            mainWindow.addWindow(subwindow);
        }
    }
}
