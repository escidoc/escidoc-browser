package org.escidoc.browser.ui.dnd;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.repository.StagingRepository;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.gwt.server.AbstractWebApplicationContext;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("serial")
public class DragAndDropFileUpload extends VerticalLayout {
    private final CssLayout dropPane = new CssLayout();

    private final ProgressIndicator progressView = new ProgressIndicator();

    private final Panel panel = new Panel();

    private final StagingRepository stagingRepository;

    public DragAndDropFileUpload(final StagingRepository stagingRepository) {
        Preconditions.checkNotNull(stagingRepository, "stagingRepository is null: %s", stagingRepository);
        this.stagingRepository = stagingRepository;

        removeAllComponents();
        addImageDropBoxInPanel();
        addProgressIndicator();
    }

    private void addProgressIndicator() {
        progressView.setIndeterminate(true);
        progressView.setVisible(false);
        addComponent(progressView);
    }

    private void addImageDropBoxInPanel() {
        panel.setSizeUndefined();
        panel.addStyleName("no-vertical-drag-hints");
        panel.addStyleName("no-horizontal-drag-hints");
        panel.setContent(filesDropBox());
        addComponent(panel);
    }

    private FilesDropBox filesDropBox() {
        configureDropPane();

        final FilesDropBox dropBox = new FilesDropBox(stagingRepository, dropPane, progressView);
        dropBox.setSizeUndefined();

        return dropBox;
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