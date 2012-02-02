package org.escidoc.browser.ui.view.helpers;

import java.io.File;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.controller.ItemController;
import org.escidoc.browser.model.EscidocServiceLocation;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.om.item.component.Components;

public class ItemComponentsView extends TableContainerVH {

    private static final Object COMPONENT_CATEGORY = "Category";

    private static final Object COMPONENT_MIMETYPE = "MimeType";

    private static final Object COMPONENT_CREATEDDATE = "Created Date";

    private static final Object COMPONENT_METADATA = "MetaData";

    private static final Object COMPONENT_ICON = "";

    private static final Object COMPONENT_DOWNLOAD = "Download";

    private ItemController controller;

    private Components componentsList;

    private HierarchicalContainer tableContainer;

    private EscidocServiceLocation serviceLocation;

    private Window mainWindow;

    public ItemComponentsView(Components components, ItemController controller, EscidocServiceLocation serviceLocation,
        Window mainWindow) {
        this.componentsList = components;
        this.controller = controller;
        this.serviceLocation = serviceLocation;
        this.mainWindow = mainWindow;
        table.setContainerDataSource(populateContainerTable());
    }

    @Override
    protected void removeAction(Object target) {
        // TODO Auto-generated method stub

    }

    @Override
    protected HierarchicalContainer populateContainerTable() {
        // Create new container
        tableContainer = new HierarchicalContainer();
        // Create container property for name
        tableContainer.addContainerProperty(COMPONENT_ICON, Embedded.class, null);
        tableContainer.addContainerProperty(PROPERTY_NAME, String.class, null);
        tableContainer.addContainerProperty(COMPONENT_CATEGORY, String.class, null);
        tableContainer.addContainerProperty(COMPONENT_MIMETYPE, String.class, null);
        tableContainer.addContainerProperty(COMPONENT_CREATEDDATE, String.class, null);
        tableContainer.addContainerProperty(COMPONENT_DOWNLOAD, Button.class, null);
        tableContainer.addContainerProperty(COMPONENT_METADATA, String.class, null);

        for (Component component : componentsList) {
            Item item = tableContainer.addItem(component.getObjid());
            if (item != null) {
                item.getItemProperty(COMPONENT_ICON).setValue(createEmbeddedImage(component));
                item.getItemProperty(PROPERTY_NAME).setValue(component.getProperties().getFileName());
                item.getItemProperty(COMPONENT_CATEGORY).setValue(component.getProperties().getContentCategory());
                item.getItemProperty(COMPONENT_MIMETYPE).setValue(component.getProperties().getMimeType());
                item.getItemProperty(COMPONENT_CREATEDDATE).setValue(
                    component.getProperties().getCreationDate().toString("d.M.y, H:mm"));
                item.getItemProperty(COMPONENT_DOWNLOAD).setValue(createDownloadLink(component));
                item.getItemProperty(COMPONENT_METADATA).setValue("metadata");
            }
        }
        boolean[] ascending = { true, false };
        Object[] propertyId = { PROPERTY_NAME, PROPERTY_VALUE };
        tableContainer.sort(propertyId, ascending);
        return tableContainer;
    }

    private Embedded createEmbeddedImage(final Component comp) {
        final String currentDir = new File(".").getAbsolutePath();
        final File file =
            new File(currentDir.substring(0, currentDir.length() - 1) + AppConstants.MIMETYPE_ICON_LOCATION
                + getFileType(comp) + ".png");
        final boolean exists = file.exists();
        if (exists) {
            return new Embedded("", new ThemeResource("images/filetypes/" + getFileType(comp) + ".png"));
        }
        return new Embedded("", new ThemeResource("images/filetypes/article.png"));
    }

    private String getFileType(final Component itemProperties) {
        final String mimeType = itemProperties.getProperties().getMimeType();
        if (mimeType == null) {
            return AppConstants.EMPTY_STRING;
        }
        final String[] last = mimeType.split("/");
        final String lastOne = last[last.length - 1];
        return lastOne;
    }

    private Button createDownloadLink(final Component comp) {
        final Button link = new Button();
        link.setStyleName(BaseTheme.BUTTON_LINK);
        link.setIcon(new ThemeResource("images/download.png"));
        link.addListener(new Button.ClickListener() {

            private static final long serialVersionUID = 651483473875504715L;

            @Override
            public void buttonClick(final ClickEvent event) {
                mainWindow.open(new ExternalResource(
                    serviceLocation.getEscidocUri() + comp.getContent().getXLinkHref(), comp
                        .getProperties().getMimeType()), "_new");
            }
        });
        return link;
    }
}
