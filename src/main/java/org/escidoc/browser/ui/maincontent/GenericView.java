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
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.layout.LayoutDesign;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.ui.Router;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class GenericView {
    private ItemProxyImpl resourceProxy;

    private Repositories repositories;

    private Window mainWindow;

    private Router router;

    private LayoutDesign layout;

    private EscidocServiceLocation serviceLocation;

    private Panel contentPanel;

    private VerticalLayout vlContentPanel;

    private Panel breadCrumpPanel;

    private VerticalLayout vlBreadCrump;

    private Panel resourcePropertiesPanel;

    private VerticalLayout vlResourceProperties;

    private Label label_1;

    private Panel metaViewsPanel;

    private HorizontalLayout hlMetaViews;

    private Panel leftPanel;

    private Panel rightPanel;

    private VerticalLayout vlLeftPanel;

    private Panel directMembersPanel;

    private VerticalLayout vlDirectMember;

    private VerticalLayout vlRightPanel;

    private Accordion metaDataRecsAcc;

    public GenericView(final EscidocServiceLocation serviceLocation, final Repositories repositories,
        final Router router, final LayoutDesign layout, final ResourceProxy resourceProxy, final Window mainWindow) {

        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null.");
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(router, "mainSite is null.");
        // Preconditions.checkNotNull(resourceProxy, "resourceProxy is null.");
        Preconditions.checkNotNull(mainWindow, "mainWindow is null.");

        this.resourceProxy = (ItemProxyImpl) resourceProxy;
        this.repositories = repositories;
        this.mainWindow = mainWindow;
        this.router = router;
        this.layout = layout;
        this.serviceLocation = serviceLocation;
        // init();

    }

    public Panel buildContentPanel() {
        // common part: create layout
        contentPanel = new Panel();
        contentPanel.setImmediate(false);
        contentPanel.setWidth("100.0%");
        contentPanel.setHeight("100.0%");
        contentPanel.setStyleName("red");

        // vlContentPanel
        vlContentPanel = buildVlContentPanel();
        contentPanel.setContent(vlContentPanel);

        return contentPanel;
    }

    private VerticalLayout buildVlContentPanel() {
        // common part: create layout
        vlContentPanel = new VerticalLayout();
        vlContentPanel.setImmediate(false);
        vlContentPanel.setWidth("100.0%");
        vlContentPanel.setHeight("100.0%");
        vlContentPanel.setMargin(true);

        // breadCrumpPanel
        breadCrumpPanel = buildBreadCrumpPanel();
        vlContentPanel.addComponent(breadCrumpPanel);

        // resourcePropertiesPanel
        resourcePropertiesPanel = buildResourcePropertiesPanel();
        vlContentPanel.addComponent(resourcePropertiesPanel);
        vlContentPanel.setExpandRatio(resourcePropertiesPanel, 1.5f);

        // metaViewsPanel
        metaViewsPanel = buildMetaViewsPanel();
        vlContentPanel.addComponent(metaViewsPanel);
        vlContentPanel.setExpandRatio(metaViewsPanel, 8.0f);

        return vlContentPanel;
    }

    private Panel buildBreadCrumpPanel() {
        // common part: create layout
        breadCrumpPanel = new Panel();
        breadCrumpPanel.setImmediate(false);
        breadCrumpPanel.setWidth("100.0%");
        breadCrumpPanel.setHeight("30px");

        // vlBreadCrump
        vlBreadCrump = new VerticalLayout();
        vlBreadCrump.setImmediate(false);
        vlBreadCrump.setWidth("100.0%");
        vlBreadCrump.setHeight("100.0%");
        vlBreadCrump.setMargin(false);
        breadCrumpPanel.setContent(vlBreadCrump);

        return breadCrumpPanel;
    }

    private Panel buildResourcePropertiesPanel() {
        // common part: create layout
        resourcePropertiesPanel = new Panel();
        resourcePropertiesPanel.setImmediate(false);
        resourcePropertiesPanel.setWidth("100.0%");
        resourcePropertiesPanel.setHeight("100.0%");

        // vlResourceProperties
        vlResourceProperties = buildVlResourceProperties();
        resourcePropertiesPanel.setContent(vlResourceProperties);

        return resourcePropertiesPanel;
    }

    private VerticalLayout buildVlResourceProperties() {
        // common part: create layout
        vlResourceProperties = new VerticalLayout();
        vlResourceProperties.setImmediate(false);
        vlResourceProperties.setWidth("100.0%");
        vlResourceProperties.setHeight("100.0%");
        vlResourceProperties.setMargin(false);

        // label_1
        label_1 = new Label();
        label_1.setImmediate(false);
        label_1.setWidth("100.0%");
        label_1.setHeight("100.0%");
        label_1
            .setValue("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent iaculis arcu vel nulla mattis pellentesque. Aenean diam justo, ullamcorper sit amet suscipit ut, fermentum in dolor. Phasellus laoreet metus nec justo interdum sit amet tempus libero luctus. Integer a dui justo, et sagittis quam. Nam mattis tempus ullamcorper. Aliquam erat volutpat. In eleifend metus sit amet sem feugiat ut elementum elit pellentesque. Maecenas adipiscing venenatis lobortis. Proin ut tortor lorem, at tempor purus. Nulla facilisi. Fusce pulvinar nisi ac mi sagittis luctus.  Vivamus metus sapien, pulvinar ac iaculis eu, mattis non velit. Aliquam erat volutpat. In hac habitasse platea dictumst. Nulla lacus velit, condimentum ut vehicula vel, fermentum et sem. Praesent augue diam, hendrerit bibendum tincidunt eget, vulputate vel elit. Nam sed ligula ipsum. Vestibulum nibh velit, bibendum a fermentum in, pharetra sed ante. Quisque vitae tellus mauris, vitae porta augue. Proin lobortis mauris et lectus consectetur mollis. Maecenas aliquam convallis justo at scelerisque. Vestibulum eros purus, rutrum at sagittis suscipit, vulputate et erat. Sed hendrerit interdum velit, vel dictum risus sollicitudin non. Duis ac arcu ipsum, quis accumsan lacus. ");
        vlResourceProperties.addComponent(label_1);

        return vlResourceProperties;
    }

    private Panel buildMetaViewsPanel() {
        // common part: create layout
        metaViewsPanel = new Panel();
        metaViewsPanel.setImmediate(false);
        metaViewsPanel.setWidth("100.0%");
        metaViewsPanel.setHeight("100.0%");

        // hlMetaViews
        hlMetaViews = buildHlMetaViews();
        metaViewsPanel.setContent(hlMetaViews);

        return metaViewsPanel;
    }

    private HorizontalLayout buildHlMetaViews() {
        // common part: create layout
        hlMetaViews = new HorizontalLayout();
        hlMetaViews.setImmediate(false);
        hlMetaViews.setWidth("100.0%");
        hlMetaViews.setHeight("100.0%");
        hlMetaViews.setMargin(false);

        // leftPanel
        leftPanel = buildLeftPanel();
        hlMetaViews.addComponent(leftPanel);
        hlMetaViews.setExpandRatio(leftPanel, 5.0f);

        // rightPanel
        rightPanel = buildRightPanel();
        hlMetaViews.addComponent(rightPanel);
        hlMetaViews.setExpandRatio(rightPanel, 5.0f);

        return hlMetaViews;
    }

    private Panel buildLeftPanel() {
        // common part: create layout
        leftPanel = new Panel();
        leftPanel.setImmediate(false);
        leftPanel.setWidth("100.0%");
        leftPanel.setHeight("100.0%");

        // vlLeftPanel
        vlLeftPanel = buildVlLeftPanel();
        leftPanel.setContent(vlLeftPanel);

        return leftPanel;
    }

    private VerticalLayout buildVlLeftPanel() {
        // common part: create layout
        vlLeftPanel = new VerticalLayout();
        vlLeftPanel.setImmediate(false);
        vlLeftPanel.setWidth("100.0%");
        vlLeftPanel.setHeight("100.0%");
        vlLeftPanel.setMargin(false);

        // directMembersPanel
        directMembersPanel = buildDirectMembersPanel();
        vlLeftPanel.addComponent(directMembersPanel);

        return vlLeftPanel;
    }

    private Panel buildDirectMembersPanel() {
        // common part: create layout
        directMembersPanel = new Panel();
        directMembersPanel.setCaption("Direct Member");
        directMembersPanel.setImmediate(false);
        directMembersPanel.setWidth("100.0%");
        directMembersPanel.setHeight("100.0%");

        // vlDirectMember
        vlDirectMember = new VerticalLayout();
        vlDirectMember.setImmediate(false);
        vlDirectMember.setWidth("100.0%");
        vlDirectMember.setHeight("100.0%");
        vlDirectMember.setMargin(false);
        directMembersPanel.setContent(vlDirectMember);

        return directMembersPanel;
    }

    private Panel buildRightPanel() {
        // common part: create layout
        rightPanel = new Panel();
        rightPanel.setImmediate(false);
        rightPanel.setWidth("100.0%");
        rightPanel.setHeight("100.0%");

        // vlRightPanel
        vlRightPanel = buildVlRightPanel();
        rightPanel.setContent(vlRightPanel);

        return rightPanel;
    }

    private VerticalLayout buildVlRightPanel() {
        // common part: create layout
        vlRightPanel = new VerticalLayout();
        vlRightPanel.setImmediate(false);
        vlRightPanel.setWidth("100.0%");
        vlRightPanel.setHeight("100.0%");
        vlRightPanel.setMargin(false);

        // metaDataRecsAcc
        metaDataRecsAcc = buildMetaDataRecsAcc();
        vlRightPanel.addComponent(metaDataRecsAcc);

        return vlRightPanel;
    }

    private Accordion buildMetaDataRecsAcc() {
        // common part: create layout
        metaDataRecsAcc = new Accordion();
        metaDataRecsAcc.setImmediate(false);
        metaDataRecsAcc.setWidth("100.0%");
        metaDataRecsAcc.setHeight("100.0%");

        // l1
        Label l1 = new Label();
        l1.setImmediate(false);
        l1.setWidth("-1px");
        l1.setHeight("-1px");
        l1.setValue("Label");
        metaDataRecsAcc.addTab(l1, "Saved actions", null);

        // l2
        Label l2 = new Label();
        l2.setImmediate(false);
        l2.setWidth("-1px");
        l2.setHeight("-1px");
        l2.setValue("Label");
        metaDataRecsAcc.addTab(l2, "Notes", null);

        // l3
        Label l3 = new Label();
        l3.setImmediate(false);
        l3.setWidth("-1px");
        l3.setHeight("-1px");
        l3.setValue("Label");
        metaDataRecsAcc.addTab(l3, "Issues", null);

        return metaDataRecsAcc;
    }
}
