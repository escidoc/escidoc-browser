/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or https://www.escidoc.org/license/ESCIDOC.LICENSE .
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui.dnd;

import com.google.common.base.Preconditions;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.maincontent.ItemContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import de.escidoc.core.resources.om.item.component.Components;

@SuppressWarnings("serial")
class FilesDropBox extends DragAndDropWrapper implements DropHandler {

    private static final Logger LOG = LoggerFactory.getLogger(FilesDropBox.class);

    private static final int FILE_SIZE_IN_MEGABYTE = 20;

    private static final long FILE_SIZE_LIMIT = FILE_SIZE_IN_MEGABYTE * 1024 * 1024;

    private final ProgressIndicator progressView;

    private final Repositories repositories;

    private final ItemProxy itemProxy;

    private final ItemContent componentListView;

    private int numberOfFiles;

    FilesDropBox(final Repositories repositories, final ItemProxy itemProxy, final Component root,
        final ProgressIndicator progressView, final ItemContent componentListView) {
        super(root);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(itemProxy, "itemProxy is null: %s", itemProxy);
        Preconditions.checkNotNull(root, "root is null: %s", root);
        Preconditions.checkNotNull(progressView, "progressView is null: %s", progressView);
        Preconditions.checkNotNull(componentListView, "componentListView is null: %s", componentListView);
        this.repositories = repositories;
        this.itemProxy = itemProxy;
        this.progressView = progressView;
        this.componentListView = componentListView;
        setDropHandler(this);
    }

    @Override
    public void drop(final DragAndDropEvent dropEvent) {
        if (isTextOnly(dropEvent)) {
            showText((WrapperTransferable) dropEvent.getTransferable());
        }
        else if (isFolder(dropEvent)) {
            showSourcePath(dropEvent);
            getApplication().getMainWindow().showNotification(
                new Notification("Dropping Folder is not supported", Notification.TYPE_WARNING_MESSAGE));
        }
        else {
            handleFiles(dropEvent);
        }
    }

    private void handleFiles(final DragAndDropEvent dropEvent) {
        numberOfFiles = getFilesFrom(dropEvent).length;

        for (final Html5File html5File : getFilesFrom(dropEvent)) {
            if (html5File.getFileSize() > FILE_SIZE_LIMIT) {
                showFileSizeWarning();
                return;
            }
            html5File.setStreamVariable(createStreamVariable(html5File));
            progressView.setVisible(true);
        }
    }

    private MultipleStreamVariable createStreamVariable(final Html5File html5File) {
        return new MultipleStreamVariable(progressView, getApplication().getMainWindow(), html5File, new Components(),
            this, repositories, itemProxy, componentListView);
    }

    @Override
    public AcceptCriterion getAcceptCriterion() {
        return AcceptAll.get();
    }

    private static void showSourcePath(final DragAndDropEvent dropEvent) {
        final Collection<String> dataFlavors = dropEvent.getTransferable().getDataFlavors();
        for (final String string : dataFlavors) {
            final Object data = dropEvent.getTransferable().getData(string);
            if (data instanceof String && ((String) data).startsWith("file:///")) {
                LOG.debug("File Path: " + data);
            }
        }
    }

    private static boolean isFolder(final DragAndDropEvent dropEvent) {
        for (final Html5File html5File : getFilesFrom(dropEvent)) {
            return html5File.getFileSize() == 0;
        }
        return false;
    }

    private static boolean isTextOnly(final DragAndDropEvent dropEvent) {
        return getFilesFrom(dropEvent) == null;
    }

    private static Html5File[] getFilesFrom(final DragAndDropEvent dropEvent) {
        return ((WrapperTransferable) dropEvent.getTransferable()).getFiles();
    }

    private void showFileSizeWarning() {
        getWindow().showNotification(
            "File rejected. Max " + FILE_SIZE_IN_MEGABYTE + "Mb files are accepted. Canceling all operation",
            Notification.TYPE_WARNING_MESSAGE);
    }

    private void showText(final WrapperTransferable trasferable) {
        final String text = trasferable.getText();
        if (text != null) {
            showText(text);
        }
    }

    private void showText(final String text) {
        showComponent(new Label(text), "Wrapped text content");
    }

    private void showComponent(final Component component, final String name) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();
        layout.setMargin(true);
        final Window window = new Window(name, layout);
        window.setSizeUndefined();
        component.setSizeUndefined();
        window.addComponent(component);
        getWindow().addWindow(window);
    }

    void decrementNumberOfFiles() {
        numberOfFiles--;
    }

    public int getNumberOfFiles() {
        return numberOfFiles;
    }
}