package org.escidoc.browser.ui.view.helpers;

import java.util.HashMap;
import java.util.Map;

import org.escidoc.browser.ui.Router;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class DeleteContainerShowLogsHelper {

    private final HashMap<String, String> listNotDeleted;

    private final HashMap<String, String> listDeleted;

    private final Router router;

    public DeleteContainerShowLogsHelper(HashMap<String, String> listDeleted, HashMap<String, String> listNotDeleted,
        Router router) {
        this.listDeleted = listDeleted;
        this.listNotDeleted = listNotDeleted;

        this.router = router;
    }

    public void showWindow() {
        final Window subwindow = new Window("Change Category Type");
        subwindow.setModal(true);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);

        Table tblDeleted = new Table("Successfully deleted resources");
        tblDeleted.setWidth("90%");
        tblDeleted.addContainerProperty("Id", String.class, null);
        tblDeleted.addContainerProperty("Resource ", String.class, null);

        for (Map.Entry<String, String> entry : listDeleted.entrySet()) {
            tblDeleted.addItem(new Object[] { entry.getKey(), entry.getValue() }, entry.getKey());
        }
        layout.addComponent(tblDeleted);

        Table tblNotDeleted = new Table("Resources that could not be deleted");
        tblNotDeleted.setWidth("90%");
        tblNotDeleted.addContainerProperty("Resource Id", String.class, null);
        tblNotDeleted.addContainerProperty("Resource & Error", String.class, null);

        for (Map.Entry<String, String> entry : listNotDeleted.entrySet()) {
            tblNotDeleted.addItem(new Object[] { entry.getKey(), entry.getValue() }, entry.getKey());
        }
        layout.addComponent(tblNotDeleted);

        Button close = new Button("Close", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                (subwindow.getParent()).removeWindow(subwindow);
            }
        });

        layout.addComponent(close);

        subwindow.setWidth("600px");
        subwindow.addComponent(layout);
        router.getMainWindow().addWindow(subwindow);

    }

}
