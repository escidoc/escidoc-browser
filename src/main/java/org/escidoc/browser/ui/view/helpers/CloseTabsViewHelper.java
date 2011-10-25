package org.escidoc.browser.ui.view.helpers;

import org.escidoc.browser.ui.maincontent.ItemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.themes.BaseTheme;

public class CloseTabsViewHelper implements ClickListener {
    private AbstractLayout layout;

    private static final Logger LOG = LoggerFactory.getLogger(ItemView.class);

    private static final String FLOAT_RIGHT = "floatright";

    TabSheet ts = null;

    public CloseTabsViewHelper(AbstractLayout layout) {
        this.layout = layout;
    }

    public CloseTabsViewHelper(AbstractLayout layout, TabSheet mainContentTabs) {
        this.ts = mainContentTabs;
        this.layout = layout;
        buildButton(layout);
    }

    private void buildButton(AbstractLayout layout) {
        Button btnRemoveTabs = new Button();
        ThemeResource icon = new ThemeResource("../runo/icons/16/cancel.png");
        btnRemoveTabs.setStyleName(BaseTheme.BUTTON_LINK);
        btnRemoveTabs.addStyleName("closeallTabs");
        btnRemoveTabs.setDescription("Remove all open Tabs");
        btnRemoveTabs.setIcon(icon);
        btnRemoveTabs.addStyleName(FLOAT_RIGHT);
        btnRemoveTabs.addListener(this);
        layout.addComponent(btnRemoveTabs);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (ts == null) {
            ts = (TabSheet) getParent(layout);
        }

        for (int i = ts.getComponentCount() - 1; i >= 0; i--) {
            ts.removeTab(ts.getTab(i));
        }
    }

    /**
     * Recursive procedure to find the parent of a Layout that is a TabSheet
     * 
     * @param son
     * @return
     * @throws NullPointerException
     */
    private Component getParent(Component son) throws NullPointerException {
        Component father;
        if (son.getParent().getClass().toString().equals("class com.vaadin.ui.TabSheet")) {
            return (TabSheet) son.getParent();
        }
        else {
            father = getParent(son.getParent());
        }
        return father;
    }
}
