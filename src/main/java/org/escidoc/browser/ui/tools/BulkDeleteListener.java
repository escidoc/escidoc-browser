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
package org.escidoc.browser.ui.tools;

import java.util.Map;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.repository.BulkRepository.DeleteResult;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
final class BulkDeleteListener implements ClickListener {

    private static final Logger LOG = LoggerFactory.getLogger(BulkDeleteListener.class);

    private final FilterButtonListener filterButtonListener;

    private final Repositories repositories;

    private final Window mainWindow;

    private final BeanItemContainer<ResourceModel> resultDataSource;

    BulkDeleteListener(final FilterButtonListener filterButtonListener, final Repositories repositories,
        final Window mainWindow, final BeanItemContainer<ResourceModel> resultDataSource) {
        Preconditions.checkNotNull(filterButtonListener, "filterButtonListener is null: %s", filterButtonListener);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(mainWindow, "mainWindow is null: %s", mainWindow);
        Preconditions.checkNotNull(resultDataSource, "resultDataSource is null: %s", resultDataSource);

        this.filterButtonListener = filterButtonListener;
        this.repositories = repositories;
        this.mainWindow = mainWindow;
        this.resultDataSource = resultDataSource;
    }

    @Override
    public void buttonClick(final ClickEvent event) {
        final DeleteResult result = deleteSelectedResources();
        handleSuccesful(result);
        handleFailed(result);
    }

    private DeleteResult deleteSelectedResources() {
        return repositories.bulkTasks().delete(this.filterButtonListener.getSelectedResources());
    }

    private void handleFailed(final DeleteResult result) {
        final Map<ResourceModel, String> failedResults = result.getFail();
        if (failedResults.isEmpty()) {
            return;
        }

        showFailMessage(failedResults);
    }

    private void showFailMessage(final Map<ResourceModel, String> failedResults) {
        showModalWindow(failedResults);
    }

    private void showModalWindow(final Map<ResourceModel, String> failedResults) {
        final Window subWindow = buildModalWindow();
        addMessage(failedResults, subWindow);
        addSpace(subWindow);
        addCloseButton(subWindow);
        showModalWindow(subWindow);
    }

    private void showModalWindow(final Window subWindow) {
        mainWindow.addWindow(subWindow);
    }

    private void addMessage(final Map<ResourceModel, String> failedResults, final Window subWindow) {
        final String message = buildMessage(failedResults);
        subWindow.addComponent(new Label(message, Label.CONTENT_XHTML));
    }

    private void addSpace(final Window subWindow) {
        subWindow.addComponent(Utils.createSpace());
    }

    private void addCloseButton(final Window subWindow) {
        final HorizontalLayout buttonLayout = new HorizontalLayout();
        subWindow.addComponent(buttonLayout);
        buttonLayout.setSpacing(true);

        buttonLayout.addComponent(new Button(ViewConstants.OK, new ClickListener() {

            @Override
            public void buttonClick(final ClickEvent event) {
                closeDialog(subWindow);
            }

        }));
    }

    private String buildMessage(final Map<ResourceModel, String> failedResults) {
        final StringBuilder builder = new StringBuilder();
        builder.append("Failed to delete: ");

        builder.append("<ul>");
        for (final ResourceModel rM : failedResults.keySet()) {
            builder.append("<li>");
            builder.append(rM.getType().getLabel());
            builder.append(" ");
            builder.append(rM.getId());
            builder.append(". Reason: ");
            builder.append(failedResults.get(rM));

            builder.append("</li>");
        }
        builder.append("</ul>");
        return builder.toString();
    }

    private Window buildModalWindow() {
        final Window subWindow = new Window(ViewConstants.WARNING);
        subWindow.setWidth("600px");
        subWindow.setModal(true);
        return subWindow;
    }

    private void closeDialog(final Window subWindow) {
        mainWindow.removeWindow(subWindow);
    }

    private void handleSuccesful(final DeleteResult result) {
        for (final ResourceModel rM : result.getSuccess()) {
            LOG.debug("Succesfully delete " + rM);
            resultDataSource.removeItem(rM);
        }
    }
}