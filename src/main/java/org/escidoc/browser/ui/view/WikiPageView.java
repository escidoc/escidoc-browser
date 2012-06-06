package org.escidoc.browser.ui.view;

import org.escidoc.browser.controller.WikiPageController;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.listeners.ExampleUtil;
import org.escidoc.browser.ui.maincontent.View;

import com.google.common.base.Preconditions;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

public class WikiPageView extends View {

    private Router router;

    private ResourceProxy resourceProxy;

    private WikiPageController controller;

    public WikiPageView(Router router, ResourceProxy resourceProxy, WikiPageController wikiPageController) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(wikiPageController, "wikiPageController is null: %s", wikiPageController);

        this.router = router;
        this.resourceProxy = resourceProxy;
        this.controller = wikiPageController;
        this.setViewName(resourceProxy.getName());
        buildContentPanel();
    }

    public Panel buildContentPanel() {
        this.setImmediate(false);
        this.setWidth("100.0%");
        this.setHeight("100.0%");
        // this.setStyleName(Runo.PANEL_LIGHT);
        this.setCaption("Wiki Page View");

        // vlContentPanel assign a layout to this panel
        this.setContent(buildVlContentPanel());
        return this;
    }

    private VerticalLayout buildVlContentPanel() {
        // common part: create layout
        VerticalLayout vlContentPanel = new VerticalLayout();
        vlContentPanel.setImmediate(false);
        vlContentPanel.setWidth("100.0%");
        vlContentPanel.setHeight("100.0%");
        vlContentPanel.setMargin(true, true, false, true);

        buildHeaderLayout(vlContentPanel);
        buildContentSection(vlContentPanel);
        buildTableVersions(vlContentPanel);

        return vlContentPanel;
    }

    private void buildTableVersions(VerticalLayout vlContentPanel) {
        Table tbl = new Table();
        tbl.setWidth("100%");
        tbl.setContainerDataSource(getVersionHistory());
        tbl.setSortDisabled(true);
        tbl.setPageLength(7);
        vlContentPanel.addComponent(tbl);
    }

    public static IndexedContainer getVersionHistory() {
        IndexedContainer c = new IndexedContainer();
        VersionHistoryContainer(c);
        return c;
    }

    private static void VersionHistoryContainer(IndexedContainer container) {
        container.addContainerProperty("Version", String.class, null);
        container.addContainerProperty("Modified Date", String.class, null);
        container.addContainerProperty("Icon", String.class, null);

        Item item = container.addItem(1);
        item.getItemProperty("Version").setValue("Version 1");
        item.getItemProperty("Modified Date").setValue("12.4.2012 at 18:08");
        item.getItemProperty("Icon").setValue("Some Icons");
        Item item2 = container.addItem(2);
        item2.getItemProperty("Version").setValue("Version 2");
        item2.getItemProperty("Modified Date").setValue("12.4.2012 at 18:08");
        item2.getItemProperty("Icon").setValue("Some Icons");
        Item item3 = container.addItem(3);
        item3.getItemProperty("Version").setValue("Version 3");
        item3.getItemProperty("Modified Date").setValue("12.4.2012 at 18:08");
        item3.getItemProperty("Icon").setValue("Some Icons");

        container.sort(new Object[] { "Version" }, new boolean[] { true });
    }

    private void buildContentSection(VerticalLayout vlContentPanel) {
        Label text = new Label(ExampleUtil.lorem, Label.CONTENT_XHTML);
        text.setWidth("100%");
        vlContentPanel.addComponent(text);

    }

    /* Begin Header Assets */
    /**
     * Put ID + Modification and main Operation Icons here
     * 
     * @param vlContentPanel
     */
    private void buildHeaderLayout(VerticalLayout vlContentPanel) {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("100%");
        Label lblGeneralInfo =
            new Label("ID: " + resourceProxy.getId() + " " + "Modified on " + resourceProxy.getModifiedOn());
        hl.addComponent(lblGeneralInfo);
        hl.setExpandRatio(lblGeneralInfo, 0.7f);
        HorizontalLayout mainOperationIcons = buildMainOperationIconsLayout();
        hl.addComponent(mainOperationIcons);
        hl.setExpandRatio(mainOperationIcons, 0.3f);
        hl.setComponentAlignment(mainOperationIcons, Alignment.MIDDLE_RIGHT);

        HorizontalLayout horizontalRuler = new HorizontalLayout();
        horizontalRuler.setWidth("100%");
        horizontalRuler.addComponent(new Label("<hr>", Label.CONTENT_RAW));

        vlContentPanel.addComponent(hl);
        vlContentPanel.addComponent(horizontalRuler);

    }

    private HorizontalLayout buildMainOperationIconsLayout() {
        HorizontalLayout mainOperationIcons = new HorizontalLayout();

        Button edit = showEdit(resourceProxy);
        mainOperationIcons.addComponent(edit);
        Button delete = showDelete(resourceProxy);
        mainOperationIcons.addComponent(delete);
        Button share = showShare(resourceProxy);
        mainOperationIcons.addComponent(share);
        Button download = downloadShow(resourceProxy);
        mainOperationIcons.addComponent(download);
        return mainOperationIcons;
    }

    private Button showEdit(final ResourceModel child) {
        Button edit = new Button();
        edit.setStyleName(BaseTheme.BUTTON_LINK);
        edit.setDescription("Edit");
        edit.setIcon(new ThemeResource("images/wpzoom/pencil.png"));
        edit.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                controller
                    .getRouter().getMainWindow()
                    .showNotification("Not yet Implemented " + child.getId(), Notification.TYPE_HUMANIZED_MESSAGE);

            }
        });
        return edit;
    }

    private Button showDelete(final ResourceModel child) {
        Button edit = new Button();
        edit.setStyleName(BaseTheme.BUTTON_LINK);
        edit.setDescription(ViewConstants.PROPERTY_DELETE);
        edit.setIcon(new ThemeResource("images/wpzoom/trash.png"));
        edit.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                controller
                    .getRouter().getMainWindow()
                    .showNotification("Not yet Implemented " + child.getId(), Notification.TYPE_HUMANIZED_MESSAGE);

            }
        });
        return edit;
    }

    private Button showShare(final ResourceModel child) {
        Button edit = new Button();
        edit.setStyleName(BaseTheme.BUTTON_LINK);
        edit.setDescription(ViewConstants.PROPERTY_SHARE);
        edit.setIcon(new ThemeResource("images/wpzoom/share.png"));
        edit.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                controller
                    .getRouter().getMainWindow()
                    .showNotification("Not yet Implemented " + child.getId(), Notification.TYPE_HUMANIZED_MESSAGE);

            }
        });
        return edit;
    }

    private Button downloadShow(final ResourceModel child) {
        Button edit = new Button();
        edit.setStyleName(BaseTheme.BUTTON_LINK);
        edit.setDescription(ViewConstants.PROPERTY_DOWNLOAD);
        edit.setIcon(new ThemeResource("images/wpzoom/eye.png"));
        edit.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                controller
                    .getRouter().getMainWindow()
                    .showNotification("Not yet Implemented " + child.getId(), Notification.TYPE_HUMANIZED_MESSAGE);

            }
        });
        return edit;
    }
    /* End Header Assets */
}
