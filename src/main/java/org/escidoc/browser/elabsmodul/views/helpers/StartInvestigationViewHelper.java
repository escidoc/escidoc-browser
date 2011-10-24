package org.escidoc.browser.elabsmodul.views.helpers;

import org.escidoc.browser.ui.Router;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.Runo;

public class StartInvestigationViewHelper {
    private Router router;

    public StartInvestigationViewHelper(Router router) {
        this.router = router;
    }

    public void createStartButton(Panel panel) {
        Button startBtn = new Button("Start");
        startBtn.setStyleName(Runo.BUTTON_BIG);
        startBtn.setWidth("100%");
        startBtn.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (event.getButton().getCaption().equals("Start")) {
                    router.getMainWindow().getWindow().showNotification("Starting Process");
                    event.getButton().setCaption("Stop");
                }
                else {
                    router.getMainWindow().getWindow().showNotification("Halting Process");
                    event.getButton().setCaption("Start");
                }

            }
        });
        panel.addComponent(startBtn);
    }

}
