package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.controller.ItemController;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ItemProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;

import com.google.common.base.Preconditions;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class ItemView2 extends View {
	private ItemProxyImpl resourceProxy;
	private Repositories repositories;
	private Window mainWindow;
	private Router router;
	private EscidocServiceLocation serviceLocation;
	private Panel panelView;
	private ItemController controller;

	public ItemView2(Router router, ResourceProxy resourceProxy,
			ItemController itemController) throws EscidocClientException {
		Preconditions.checkNotNull(itemController,
				"itemController is null: %s", itemController);
		Preconditions.checkNotNull(router, "router is null.");
		Preconditions.checkNotNull(resourceProxy, "resourceProxy is null.");

		this.resourceProxy = (ItemProxyImpl) resourceProxy;
		this.repositories = router.getRepositories();
		this.setViewName(resourceProxy.getName());
		this.mainWindow = router.getMainWindow();
		this.router = router;
		this.serviceLocation = router.getServiceLocation();
		this.controller = itemController;
		panelView = buildContentPanel();
	}

	private Panel buildContentPanel() throws EscidocClientException {
		this.setImmediate(false);
		this.setWidth("100.0%");
		this.setHeight("100.0%");
		this.setStyleName(Runo.PANEL_LIGHT);

		// vlContentPanel assign a layout to this panel
		this.setContent(buildVlContentPanel());
		return this;
	}

	// the main panel has a Layout.
	// Elements of the view are bound in this layout of the main Panel
	private VerticalLayout buildVlContentPanel() throws EscidocClientException {
		// common part: create layout
		VerticalLayout vlContentPanel = new VerticalLayout();
		vlContentPanel.setImmediate(false);
		vlContentPanel.setWidth("100.0%");
		vlContentPanel.setHeight("100.0%");
		vlContentPanel.setMargin(false);

		final HorizontalSplitPanel horiz = buildHorizontalSplit();
		vlContentPanel.addComponent(horiz);

		return vlContentPanel;
	}

	private HorizontalSplitPanel buildHorizontalSplit() {
		final HorizontalSplitPanel horiz = new HorizontalSplitPanel();
		horiz.setStyleName(Runo.SPLITPANEL_SMALL);
		horiz.setSplitPosition(80); // percent
		horiz.addComponent(new Label("Whatever"));
		VerticalLayout sidebar = buildSidebar();
		horiz.addComponent(sidebar);

		return horiz;
	}

	private VerticalLayout buildSidebar() {
		VerticalLayout sidebar = new VerticalLayout();
		sidebar.setStyleName(Reindeer.LAYOUT_BLUE);
		Panel properties = new Panel("Properties");

		sidebar.addComponent(properties);
		return sidebar;
	}
}
