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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.List;

import org.escidoc.browser.Utils;
import org.escidoc.browser.repository.IngestRepository;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.tools.Style.H2;
import org.escidoc.core.tme.IngestResult;
import org.escidoc.core.tme.SucessfulIngestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;

@SuppressWarnings("serial")
public final class ImportView extends VerticalLayout {

    public static final String DEFAULT_CONTENT_MODEL_URI = "http://dl.dropbox.com/u/419140/eLab-Content-Models.zip";

    private static final Logger LOG = LoggerFactory.getLogger(ImportView.class);

    private IngestRepository ingestRepository;

    private Router router;

    public ImportView(Router router, IngestRepository ingestRepository) {
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
        addComponent(new Label("<br/>", Label.CONTENT_XHTML));
    }

    private void addContent() {
        final Label urlLabel = new Label(ViewConstants.URL);
        final TextField sourceUrlField = new TextField();
        sourceUrlField.setWidth("400px");
        sourceUrlField.setValue(DEFAULT_CONTENT_MODEL_URI);
        Button importButton = new Button(ViewConstants.IMPORT);
        importButton.setStyleName(Reindeer.BUTTON_SMALL);
        importButton.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (sourceUrlField.getValue() instanceof String) {
                    downloadAndIngestZipFile(sourceUrlField, (String) sourceUrlField.getValue());
                }
            }

            private void downloadAndIngestZipFile(final TextField sourceUrl, String sourceUri) {
                try {
                    URLConnection connection = createConnection(new URI(sourceUri));
                    if (isZipFile(connection)) {
                        ingestZipFile(connection.getInputStream());
                    }
                    else {
                        router.getMainWindow().showNotification(sourceUrl.getValue() + " is not a zip file.",
                            Window.Notification.TYPE_WARNING_MESSAGE);
                    }
                }
                catch (MalformedURLException e) {
                    showErrorMessage(e);
                }
                catch (IOException e) {
                    showErrorMessage(e);
                }
                catch (EscidocClientException e) {
                    showErrorMessage(e);
                }
                catch (URISyntaxException e) {
                    showErrorMessage(e);
                }
            }

            private boolean isZipFile(URLConnection connection) {
                return connection.getContentType().equalsIgnoreCase("application/zip");
            }

            private URLConnection createConnection(URI uri) throws MalformedURLException, IOException {
                if (Utils.findHttpProxy(uri) == null) {
                    return uri.toURL().openConnection();
                }
                return uri.toURL().openConnection(Utils.createHttpProxy(uri));
            }

            private void ingestZipFile(InputStream is) throws EscidocException, InternalClientException,
                TransportException, UnsupportedEncodingException, IOException {
                // TODO: show spinner and message
                List<IngestResult> list = ingestRepository.ingestZip(is);
                StringBuilder builder = new StringBuilder();
                for (IngestResult ingestResult : list) {
                    if (ingestResult.isSuccesful()) {
                        SucessfulIngestResult sir = (SucessfulIngestResult) ingestResult;
                        builder.append(sir.getId()).append(" ,");
                    }

                }
                router.getMainWindow().showNotification("Succesfully ingest content model set", builder.toString(),
                    Window.Notification.POSITION_CENTERED_TOP);
            }

            private void showErrorMessage(Exception e) {
                router
                    .getApp().getMainWindow()
                    .showNotification(ViewConstants.ERROR, e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
            }
        });

        HorizontalLayout hl = new HorizontalLayout();
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
