package org.escidoc.browser.ui.view.helpers;

import org.escidoc.browser.ui.maincontent.ItemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.themes.BaseTheme;

public class CloseTabsViewHelper implements ClickListener {
    private AbstractLayout layout;

    private static final Logger LOG = LoggerFactory.getLogger(ItemView.class);

    private static final String FLOAT_RIGHT = "floatright";

    public CloseTabsViewHelper(AbstractLayout layout) {
        this.layout = layout;
    }

    public void bindtoCssLayout() {
        Button btnRemoveTabs = new Button();
        ThemeResource icon = new ThemeResource("../runo/icons/16/cancel.png");
        btnRemoveTabs.setStyleName(BaseTheme.BUTTON_LINK);
        btnRemoveTabs.setDescription("Remove all open Tabs");
        btnRemoveTabs.setIcon(icon);
        btnRemoveTabs.addStyleName(FLOAT_RIGHT);
        btnRemoveTabs.addListener(this);
        layout.addComponent(btnRemoveTabs);
    }

    // TODO FIX the navigation to the Parent which is a TabSheet
    @Override
    public void buttonClick(ClickEvent event) {
        LOG.debug((layout.getParent().getParent().getClass().toString()));
        TabSheet ts = (TabSheet) layout.getParent().getParent();
        for (int i = ts.getComponentCount() - 1; i >= 0; i--) {
            ts.removeTab(ts.getTab(i));
        }
    }
}
