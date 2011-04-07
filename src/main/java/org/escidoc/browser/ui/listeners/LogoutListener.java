package org.escidoc.browser.ui.listeners;

import com.google.common.base.Preconditions;
import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public final class LogoutListener implements Button.ClickListener {
    private final Application app;

    public LogoutListener(final Application app) {
        Preconditions.checkNotNull(app, "app is null: %s", app);
        this.app = app;
    }

    @Override
    public void buttonClick(final ClickEvent event) {
        Preconditions.checkNotNull(event, "event is null: %s", event);
        app.close();
        // TODO invalidate escidoc token == remove token from URI path
    }
}