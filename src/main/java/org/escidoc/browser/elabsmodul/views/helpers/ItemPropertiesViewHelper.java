package org.escidoc.browser.elabsmodul.views.helpers;

import java.util.List;

import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.maincontent.BreadCrumbMenu;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.Runo;

public class ItemPropertiesViewHelper {

    final String LAST_MODIFIED_BY = "Last modification by ";

    final String FLOAT_LEFT = "floatleft";

    final String FLOAT_RIGHT = "floatright";

    private ItemProxy itemProxy;

    private List<ResourceModel> breadCrumbModel;

    public ItemPropertiesViewHelper(ItemProxy itemProxy, List<ResourceModel> breadCrumbModel) {
        this.itemProxy = itemProxy;
        this.breadCrumbModel = breadCrumbModel;
    }

    public Panel generatePropertiesView() {
        // Item title
        final Label titleLabel = new Label(ViewConstants.RESOURCE_NAME + itemProxy.getName());
        titleLabel.setDescription("header");
        titleLabel.setStyleName("h2 fullwidth");

        // HR Ruler
        final Label descRuler = new Label("<hr/>", Label.CONTENT_RAW);
        descRuler.setStyleName("hr");

        // ItemProperties View
        final CssLayout propertiesView = new CssLayout();
        propertiesView.setWidth("100%");
        propertiesView.setHeight("100%");

        final Label descMetadata1 = new Label("ID: " + itemProxy.getId());
        final Label descMetadata2 =
            new Label(LAST_MODIFIED_BY + " " + itemProxy.getModifier() + " on " + itemProxy.getModifiedOn(),
                Label.CONTENT_XHTML);

        final Panel pnlPropertiesLeft = buildLeftPanel();
        pnlPropertiesLeft.addComponent(descMetadata1);

        final Panel pnlPropertiesRight = buildRightPanel();
        pnlPropertiesRight.addComponent(descMetadata2);

        propertiesView.addComponent(pnlPropertiesLeft);
        propertiesView.addComponent(pnlPropertiesRight);

        Panel viewHandler = buildmainView();

        new BreadCrumbMenu(viewHandler, breadCrumbModel);

        viewHandler.addComponent(titleLabel);
        viewHandler.addComponent(descRuler);
        viewHandler.addComponent(propertiesView);
        viewHandler.addComponent(descRuler);

        return viewHandler;
    }

    private Panel buildmainView() {
        Panel viewHandler = new Panel();
        viewHandler.getLayout().setMargin(false);
        viewHandler.setStyleName(Runo.PANEL_LIGHT);
        return viewHandler;
    }

    private Panel buildRightPanel() {
        final Panel pnlPropertiesRight = new Panel();
        pnlPropertiesRight.setWidth("60%");
        pnlPropertiesRight.setHeight("60px");
        pnlPropertiesRight.setStyleName(FLOAT_RIGHT);
        pnlPropertiesRight.addStyleName(Runo.PANEL_LIGHT);
        return pnlPropertiesRight;
    }

    private Panel buildLeftPanel() {
        final Panel pnlPropertiesLeft = new Panel();
        pnlPropertiesLeft.setWidth("40%");
        pnlPropertiesLeft.setHeight("70px");
        pnlPropertiesLeft.setStyleName(FLOAT_LEFT);
        pnlPropertiesLeft.addStyleName(Runo.PANEL_LIGHT);
        return pnlPropertiesLeft;
    }
}
