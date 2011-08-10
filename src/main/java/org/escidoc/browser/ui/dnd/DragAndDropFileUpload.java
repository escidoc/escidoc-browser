package org.escidoc.browser.ui.dnd;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.maincontent.ItemContent;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.gwt.server.AbstractWebApplicationContext;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class DragAndDropFileUpload extends VerticalLayout {

    private final ProgressIndicator progressView = new ProgressIndicator();

    private final Panel panel = new Panel();

    private final Repositories repositories;

    private final ItemProxy itemProxy;

    private final ItemContent componentListView;

    private final Component verticalLayout;

    public DragAndDropFileUpload(final Repositories repositories, final ItemProxy itemProxy,
        final ItemContent componentListView, final VerticalLayout verticalLayout) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(itemProxy, "itemProxy is null: %s", itemProxy);
        Preconditions.checkNotNull(componentListView, "componentListView is null: %s", componentListView);
        Preconditions.checkNotNull(verticalLayout, "verticalLayout is null: %s", verticalLayout);
        this.repositories = repositories;
        this.itemProxy = itemProxy;
        this.componentListView = componentListView;
        this.verticalLayout = verticalLayout;
        initView();
    }

    private void initView() {
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
        panel.addStyleName("no-vertical-drag-hints");
        panel.addStyleName("no-horizontal-drag-hints");
        panel.setContent(filesDropBox());
        addComponent(panel);
    }

    private FilesDropBox filesDropBox() {
        final FilesDropBox dropBox =
            new FilesDropBox(repositories, itemProxy, verticalLayout, progressView, componentListView);
        dropBox.setSizeFull();
        return dropBox;
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