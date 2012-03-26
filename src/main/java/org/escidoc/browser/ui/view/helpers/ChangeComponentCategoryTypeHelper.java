package org.escidoc.browser.ui.view.helpers;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import org.escidoc.browser.controller.ItemController;
import org.escidoc.browser.ui.Router;

public class ChangeComponentCategoryTypeHelper {
    private Window subwindow;

    private final Router router;

    private final String categoryType;

    private final ItemController controller;

    private final String itemId;

    private final String componentId;

    public ChangeComponentCategoryTypeHelper(Router router, String categoryType, String componentId,
        ItemController controller, String itemId) {
        this.router = router;
        this.categoryType = categoryType;
        this.componentId = componentId;

        this.controller = controller;
        this.itemId = itemId;

    }

    public void showWindow() {

        subwindow = new Window("Change Category Type");
        subwindow.setModal(true);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);

        final TextField txtField = new TextField("Change Category Type");
        txtField.setValue(categoryType);
        subwindow.addComponent(txtField);

        Button close = new Button("Close", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        Button save = new Button("Save", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                String newCatType = txtField.getValue().toString();
                controller.updateComponentCategory(componentId, newCatType, itemId);
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });
        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(save);
        hl.addComponent(close);

        layout.addComponent(hl);
        subwindow.setWidth("350px");
        subwindow.addComponent(layout);
        router.getMainWindow().addWindow(subwindow);

    }

}
