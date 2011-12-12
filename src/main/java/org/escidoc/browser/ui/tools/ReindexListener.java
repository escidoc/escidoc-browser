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

import java.util.List;

import org.escidoc.browser.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.Application;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.adm.AdminStatus;
import de.escidoc.core.resources.adm.MessagesStatus;

@SuppressWarnings("serial")
final class ReindexListener implements ClickListener {

    private final VerticalLayout statusLayout = new VerticalLayout();

    private final AbstractField indexNameSelect;

    private final AdminRepository adminRepository;

    private final Application application;

    private final CheckBox clearIndexBox;

    private final Button reindexResourceBtn;

    private final ProgressIndicator progressIndicator;

    private final ReindexView reindexView;

    public ReindexListener(final Application application, final CheckBox clearIndexBox,
        final AbstractField indexNameSelect, final Button reindexButton, final ProgressIndicator progressIndicator,
        final ReindexView reindexView, final AdminRepository adminRepository) {
        Preconditions.checkNotNull(application, "app is null: %s", application);
        Preconditions.checkNotNull(clearIndexBox, "clearIndexBox is null: %s", clearIndexBox);
        Preconditions.checkNotNull(indexNameSelect, "indexNameSelect is null: %s", indexNameSelect);
        Preconditions.checkNotNull(reindexButton, "reindexResourceBtn is null: %s", reindexButton);
        Preconditions.checkNotNull(progressIndicator, "progressIndicator is null: %s", progressIndicator);

        this.application = application;
        this.clearIndexBox = clearIndexBox;
        this.indexNameSelect = indexNameSelect;
        this.reindexResourceBtn = reindexButton;
        this.progressIndicator = progressIndicator;
        this.reindexView = reindexView;
        this.adminRepository = adminRepository;
    }

    @Override
    public void buttonClick(final ClickEvent event) {
        checkPreconditions();
        tryReindex();
    }

    private void checkPreconditions() {
        Preconditions.checkNotNull(getIndexName(), "indexName is null: %s", getIndexName());
        Preconditions.checkArgument(!getIndexName().isEmpty(), " indexName is empty", getIndexName());
    }

    private String getIndexName() {
        return (String) indexNameSelect.getValue();
    }

    private void tryReindex() {
        try {
            showReindexStatus(reindex());
            new AskStatusThread().start();
            progressIndicator.setEnabled(true);
            progressIndicator.setVisible(true);
            progressIndicator.setStyleName("big");
            progressIndicator.setValue(new Float(0f));
            makeReindexButtonInvisible();
        }
        catch (final EscidocClientException e) {
            application.getMainWindow().showNotification(
                new Notification(e.getMessage(), Notification.TYPE_ERROR_MESSAGE));
        }
    }

    private void makeReindexButtonInvisible() {
        reindexResourceBtn.setVisible(false);
    }

    private class AskStatusThread extends Thread {

        private final Logger LOG = LoggerFactory.getLogger(ReindexListener.AskStatusThread.class);

        private MessagesStatus reindexStatus;

        @Override
        public void run() {
            for (;;) {
                try {
                    reindexStatus = getReindexStatus();
                    if (reindexStatus.getStatusCode() == AdminStatus.STATUS_FINISHED) {
                        showFinishStatus(reindexStatus);
                        progressIndicator.setVisible(false);
                        progressIndicator.setEnabled(false);
                        reindexResourceBtn.setVisible(true);
                        progressIndicator.setValue(new Float(1f));
                        return;
                    }
                    showReindexStatus(reindexStatus);
                    Thread.sleep(1000);
                }
                catch (final InterruptedException e) {
                    LOG.warn(e.getMessage());
                }
                catch (final EscidocClientException e) {
                    LOG.error(e.getMessage());
                    application.getMainWindow().showNotification(e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
                }
                // All modifications to Vaadin components should be synchronized
                // over application instance. For normal requests this is done
                // by the servlet. Here we are changing the application state
                // via a separate thread.
                // Application application = reindexResourceViewImpl.getApplication();
                synchronized (application) {
                    updateProgressIndicator();
                }
            }
        }

        private MessagesStatus getReindexStatus() throws EscidocClientException {
            return adminRepository.retrieveReindexStatus();
        }

        private void updateProgressIndicator() {
            if (reindexStatus.getStatusCode() == AdminStatus.STATUS_FINISHED) {
                progressIndicator.setEnabled(false);
                reindexResourceBtn.setVisible(true);
                progressIndicator.setValue(new Float(1f));
            }

        }
    }

    private MessagesStatus reindex() throws EscidocClientException {
        return adminRepository.reindex(shouldClearIndex(), getIndexName());
    }

    private Boolean shouldClearIndex() {
        return (Boolean) clearIndexBox.getValue();
    }

    private void showReindexStatus(final MessagesStatus status) {
        if (status.getStatusCode() == AdminStatus.STATUS_INVALID_RESULT) {
            showErrorMessage(status);
        }
        if (status.getStatusCode() == AdminStatus.STATUS_FINISHED) {
            showFinishStatus(status);
            progressIndicator.setVisible(false);
            progressIndicator.setEnabled(false);
            reindexResourceBtn.setVisible(true);
            progressIndicator.setValue(new Float(1f));
            return;
        }
        else if (status.getStatusCode() == AdminStatus.STATUS_IN_PROGRESS) {
            showInProgresStatus(status.getMessages());
        }
        else {
            showErrorMessage(status);
        }
    }

    private void showFinishStatus(final MessagesStatus status) {
        statusLayout.removeAllComponents();
        statusLayout.addComponent(new Label(status.getStatusMessage()));
    }

    private void showInProgresStatus(final List<String> messageList) {
        Preconditions.checkNotNull(messageList, "messageList is null: %s", messageList);

        if (reindexView.getComponentIndex(statusLayout) < 0) {
            reindexView.addComponent(statusLayout);
        }
        else {
            statusLayout.removeAllComponents();
        }

        for (final String message : messageList) {
            statusLayout.addComponent(new Label(message));
        }

    }

    private void showErrorMessage(final MessagesStatus status) {
        application.getMainWindow().showNotification(
            new Notification(status.getStatusMessage(), Notification.TYPE_ERROR_MESSAGE));
    }

}