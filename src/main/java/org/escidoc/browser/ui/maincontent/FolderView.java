package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.controller.FolderController;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ContainerProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.view.helpers.BreadCrumbMenu;
import org.escidoc.browser.ui.view.helpers.CreateResourceLinksVH;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class FolderView extends View {

    private EscidocServiceLocation serviceLocation;

    private Router router;

    private ContainerProxyImpl resourceProxy;

    private Repositories repositories;

    private FolderController folderController;

    private VerticalLayout vlContentPanel;

    public FolderView(Router router, ResourceProxy resourceProxy, Repositories repositories,
        FolderController folderController) throws EscidocClientException {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(folderController, "folderController is null: %s", folderController);

        this.serviceLocation = router.getServiceLocation();
        this.router = router;
        this.resourceProxy = (ContainerProxyImpl) resourceProxy;
        this.setViewName(resourceProxy.getName());
        this.repositories = repositories;
        this.folderController = folderController;
        buildContentPanel();
    }

    private Panel buildContentPanel() {
        this.setImmediate(false);
        this.setWidth("100.0%");
        this.setHeight("100.0%");
        this.setStyleName(Runo.PANEL_LIGHT);

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
        vlContentPanel.setMargin(false, true, false, true);

        // breadCrumpPanel
        vlContentPanel.addComponent(buildBreadCrumpPanel());
        // Permanent Link
        new CreateResourceLinksVH(router.getMainWindow().getURL().toString(), resourceProxy, vlContentPanel, router);

        // resourcePropertiesPanel
        // Panel resourcePropertiesPanel = buildResourcePropertiesPanel();
        // vlContentPanel.addComponent(resourcePropertiesPanel);
        // vlContentPanel.setExpandRatio(resourcePropertiesPanel, 1.5f);
        //
        // // metaViewsPanel contains Panel for the DirectMembers & for the Metas
        // Panel metaViewsPanel = buildMetaViewsPanel();
        // vlContentPanel.addComponent(metaViewsPanel);
        // vlContentPanel.setExpandRatio(metaViewsPanel, 8.0f);

        return vlContentPanel;
    }

    private Panel buildBreadCrumpPanel() {
        // common part: create layout
        Panel breadCrumpPanel = new Panel();
        breadCrumpPanel.setImmediate(false);
        breadCrumpPanel.setWidth("100.0%");
        breadCrumpPanel.setHeight("30px");
        breadCrumpPanel.setStyleName(Runo.PANEL_LIGHT);

        // vlBreadCrump
        VerticalLayout vlBreadCrump = new VerticalLayout();
        vlBreadCrump.setImmediate(false);
        vlBreadCrump.setWidth("100.0%");
        vlBreadCrump.setHeight("100.0%");
        vlBreadCrump.setMargin(false);
        breadCrumpPanel.setContent(vlBreadCrump);

        // BreadCreumb
        new BreadCrumbMenu(breadCrumpPanel, resourceProxy);

        return breadCrumpPanel;
    }

}
