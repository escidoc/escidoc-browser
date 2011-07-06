package org.escidoc.browser.ui.dnd;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.ui.maincontent.ItemContent;

import com.google.common.base.Preconditions;
import com.vaadin.terminal.gwt.server.AbstractWebApplicationContext;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class DragAndDropFileUpload extends VerticalLayout {
    private final CssLayout dropPane = new CssLayout();

    private final ProgressIndicator progressView = new ProgressIndicator();

    private final Panel panel = new Panel();

    private final Repositories repositories;

    private final ItemProxyImpl itemProxy;

    private final ItemContent componentListView;

    private FilesDropBox dropBox;

    public DragAndDropFileUpload(final Repositories repositories, final ItemProxyImpl itemProxy,
        final ItemContent componentListView) {
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(itemProxy, "itemProxy is null: %s", itemProxy);
        Preconditions.checkNotNull(componentListView, "componentListView is null: %s", componentListView);
        this.repositories = repositories;
        this.itemProxy = itemProxy;
        this.componentListView = componentListView;
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
        panel.setSizeUndefined();
        panel.addStyleName("no-vertical-drag-hints");
        panel.addStyleName("no-horizontal-drag-hints");
        panel.setContent(filesDropBox());
        addComponent(panel);
    }

    private FilesDropBox filesDropBox() {
        configureDropPane();

        dropBox = new FilesDropBox(repositories, itemProxy, dropPane, progressView, componentListView);
        dropBox.setSizeUndefined();

        return dropBox;
    }

    private void configureDropPane() {
        dropPane.setHeight("100px");
        dropPane.addStyleName("image-drop-pane");
        final Label label = new Label("Drop Files here..");
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponent(label);
        layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        dropPane.addComponent(layout);

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