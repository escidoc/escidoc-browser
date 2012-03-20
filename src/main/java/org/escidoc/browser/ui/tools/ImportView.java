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

import com.google.common.base.Preconditions;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

import org.escidoc.browser.repository.IngestRepository;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.tools.Style.H2;
import org.escidoc.browser.util.Utils;
import org.escidoc.core.tme.IngestResult;
import org.escidoc.core.tme.SucessfulIngestResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.List;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;

@SuppressWarnings("serial")
public final class ImportView extends VerticalLayout {

    private final class ImportListener implements ClickListener {
        private final TextField sourceUrlField;

        private ImportListener(final TextField sourceUrlField) {
            this.sourceUrlField = sourceUrlField;
        }

        @Override
        public void buttonClick(@SuppressWarnings("unused")
        final ClickEvent event) {
            if (sourceUrlField.getValue() instanceof String) {
                downloadAndIngestZipFile(sourceUrlField, (String) sourceUrlField.getValue());
            }
        }

        private void downloadAndIngestZipFile(final TextField sourceUrl, final String sourceUri) {
            try {
                final URLConnection connection = createConnection(new URI(sourceUri));
                connection.setConnectTimeout(5000);
                connection.connect();
                if (isZipFile(connection)) {
                    ingestZipFile(connection.getInputStream());
                }
                else {
                    router.getMainWindow().showNotification(sourceUrl.getValue() + " is not a zip file.",
                        Window.Notification.TYPE_WARNING_MESSAGE);
                }
            }
            catch (final SocketTimeoutException e) {
                showTimeoutWarning();
            }
            catch (final MalformedURLException e) {
                showErrorMessage(e);
            }
            catch (final IOException e) {
                showErrorMessage(e);
            }
            catch (final EscidocClientException e) {
                showErrorMessage(e);
            }
            catch (final URISyntaxException e) {
                showErrorMessage(e);
            }
        }

        private void showTimeoutWarning() {
            router.getApp().getMainWindow().showNotification("Connection Timeout",
                "Is HTTP Proxy properly configured?", Window.Notification.TYPE_WARNING_MESSAGE);
        }

        private boolean isZipFile(final URLConnection connection) {
            return connection.getContentType().equalsIgnoreCase("application/zip");
        }

        private URLConnection createConnection(final URI uri) throws MalformedURLException, IOException {
            if (Utils.findHttpProxy(uri) == null) {
                return uri.toURL().openConnection();
            }
            return uri.toURL().openConnection(Utils.createHttpProxy(uri));
        }

        private void ingestZipFile(final InputStream is) throws EscidocException, InternalClientException,
            TransportException, UnsupportedEncodingException, IOException {
            // TODO: show spinner and message
            final List<IngestResult> list = ingestRepository.ingestZip(is);
            final StringBuilder builder = new StringBuilder();
            for (final IngestResult ingestResult : list) {
                if (ingestResult.isSuccesful()) {
                    final SucessfulIngestResult sir = (SucessfulIngestResult) ingestResult;
                    builder.append(sir.getId()).append(", ");
                }
            }
            router.getMainWindow().showNotification("Succesfully ingested content model set", builder.toString(),
                Window.Notification.DELAY_FOREVER);
        }

        private void showErrorMessage(final Exception e) {
            router.getApp().getMainWindow().showNotification(ViewConstants.ERROR, e.getMessage(),
                Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private final IngestRepository ingestRepository;

    private final Router router;

    public ImportView(final Router router, final IngestRepository ingestRepository) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(ingestRepository, "iRepository is null: %s", ingestRepository);
        this.router = router;
        this.ingestRepository = ingestRepository;
        init();
    }

    private void init() {
        addHeader();
        addRuler();
        addContent();
        addSpace();
    }

    private void addSpace() {
        addComponent(Utils.createSpace());
    }

    private void addContent() {
        final Label urlLabel = new Label(ViewConstants.URL);
        final TextField sourceUrlField = new TextField();
        sourceUrlField.setWidth("400px");
        sourceUrlField.setValue(ViewConstants.DEFAULT_CONTENT_MODEL_URI);
        final Button importButton = new Button(ViewConstants.IMPORT);
        importButton.setStyleName(Reindeer.BUTTON_SMALL);
        importButton.addListener(new ImportListener(sourceUrlField));

        final HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.addComponent(urlLabel);
        hl.addComponent(sourceUrlField);
        hl.addComponent(importButton);
        addComponent(hl);
    }

    private void addRuler() {
        addComponent(new Style.Ruler());
    }

    private void addHeader() {
        final Label text = new H2(ViewConstants.IMPORT_CONTENT_MODEL);
        text.setContentMode(Label.CONTENT_XHTML);
        addComponent(text);
    }
}