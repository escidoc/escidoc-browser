/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or https://www.escidoc.org/license/ESCIDOC.LICENSE .
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *
 * Copyright 2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui.view;

import org.escidoc.browser.controller.WikiPageController;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.resources.common.MetadataRecord;

public class WikiPageView extends View {

    private Router router;

    private static final Logger LOG = LoggerFactory.getLogger(WikiPageView.class);

    private ResourceProxy resourceProxy;

    private WikiPageController controller;

    private Label wikiContent;

    private TextArea txtWikiContent;

    private Button saveContent;

    private VerticalLayout vlContentPanel;

    private Button edit;

    private MetadataRecord md;

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
        this.setCaption("Wiki Page View");

        // vlContentPanel assign a layout to this panel
        this.setContent(buildVlContentPanel());
        return this;
    }

    private VerticalLayout buildVlContentPanel() {
        // common part: create layout
        vlContentPanel = new VerticalLayout();
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

    private void buildContentSection(final VerticalLayout vlContentPanel) {
        String content;
        // controller.getWikiPageContent();

        String[] arrayContent = controller.getWikiPageContent();
        String title = arrayContent[0];
        content = arrayContent[1];

        wikiContent = new Label(controller.parseCreole(content), Label.CONTENT_XHTML);
        controller.getWikiTitle(content);

        wikiContent.setWidth("100%");
        wikiContent.setHeight("400px");
        // Invisible resources
        txtWikiContent = new TextArea("Wiki Content", "=" + title + "=\n\r" + content);
        txtWikiContent.setWidth("100%");
        txtWikiContent.setHeight("400px");
        saveContent = new Button("Save");
        saveContent.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (txtWikiContent.getValue().toString() != "") {
                    controller.getWikiTitle(txtWikiContent.getValue().toString());
                    controller.createWikiContent("Title", txtWikiContent.getValue().toString());
                    // set Label Content
                    wikiContent.setValue(controller.parseCreole(txtWikiContent.getValue().toString()));
                    // Swap to Label
                    vlContentPanel.replaceComponent(txtWikiContent, wikiContent);
                    // Enable Edit Button
                    edit.setEnabled(true);
                }
            }
        });

        vlContentPanel.addComponent(wikiContent);
        vlContentPanel.addComponent(saveContent);
        saveContent.setVisible(false);

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

        HorizontalLayout mainOperationIcons = buildMainOperationIconsLayout();
        hl.addComponent(mainOperationIcons);

        hl.setComponentAlignment(mainOperationIcons, Alignment.TOP_RIGHT);
        hl.setHeight("20px");

        HorizontalLayout horizontalRuler = new HorizontalLayout();
        horizontalRuler.setWidth("100%");
        horizontalRuler.setHeight("9px");
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
        edit = new Button();
        edit.setStyleName(BaseTheme.BUTTON_LINK);
        edit.setDescription("Edit");
        edit.setIcon(new ThemeResource("images/wpzoom/pencil.png"));
        edit.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // Reswap Label Header + Label Content + Disable Edit Button
                vlContentPanel.replaceComponent(wikiContent, txtWikiContent);
                saveContent.setVisible(true);
                edit.setEnabled(false);
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
