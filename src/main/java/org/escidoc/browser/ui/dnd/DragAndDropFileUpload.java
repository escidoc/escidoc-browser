package org.escidoc.browser.ui.dnd;

import com.google.common.base.Preconditions;

import com.vaadin.terminal.gwt.server.AbstractWebApplicationContext;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.repository.StagingRepository;

public class DragAndDropFileUpload extends VerticalLayout {
    private final CssLayout dropPane = new CssLayout();

    private final ProgressIndicator progressView = new ProgressIndicator();

    private final Panel panel = new Panel(withImageDropBox());

    private final StagingRepository stagingRepository;

    public DragAndDropFileUpload(StagingRepository stagingRepository) {
        Preconditions.checkNotNull(stagingRepository, "stagingRepository is null: %s", stagingRepository);
        this.stagingRepository = stagingRepository;

        removeAllComponents();
        addPanel();
        addProgressIndicator();
    }

    private FilesDropBox withImageDropBox() {
        configureDropPane();
        final FilesDropBox dropBox = new FilesDropBox(stagingRepository, dropPane, progressView);
        dropBox.setSizeUndefined();
        return dropBox;
    }

    private void addProgressIndicator() {
        progressView.setIndeterminate(true);
        progressView.setVisible(false);
        addComponent(progressView);
    }

    private void addPanel() {
        panel.setSizeUndefined();
        panel.addStyleName("no-vertical-drag-hints");
        panel.addStyleName("no-horizontal-drag-hints");
        addComponent(panel);
    }

    private void configureDropPane() {
        dropPane.setWidth("200px");
        dropPane.setHeight("200px");
        dropPane.addStyleName("image-drop-pane");
        dropPane.addStyleName(Reindeer.LAYOUT_BLUE);
    }

    @Override
    public void attach() {
        super.attach();
        checkForHtml5DropFeature();
    }

    private void checkForHtml5DropFeature() {
        if (getApplication().getContext() instanceof AbstractWebApplicationContext
            && WebBrowserUtil.isHtml5FileDropNotSupported(WebBrowserUtil.isNewerFirefox(getApplication()),
                getApplication())) {
            getWindow().showNotification(AppConstants.NOT_SUPPORTED_BROWSERS, Notification.TYPE_WARNING_MESSAGE);
        }
    }
}