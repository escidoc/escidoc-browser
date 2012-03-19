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
        panel.addStyleName("no-vertical-drag-hints drophere");
        panel.addStyleName("no-horizontal-drag-hints drophere");
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