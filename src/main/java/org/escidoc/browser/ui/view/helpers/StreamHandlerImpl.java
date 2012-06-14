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
package org.escidoc.browser.ui.view.helpers;

import com.google.common.base.Preconditions;

import com.vaadin.Application;
import com.vaadin.terminal.StreamResource.StreamSource;
import com.vaadin.terminal.StreamVariable;
import com.vaadin.ui.Component;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import org.escidoc.browser.repository.Repositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.StagingHandlerClient;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;

@SuppressWarnings("serial")
public class StreamHandlerImpl implements StreamVariable {

    private final static Logger LOG = LoggerFactory.getLogger(StreamHandlerImpl.class);

    private static final String ESCIDOC_URI = "http://esfedrep1.fiz-karlsruhe.de:8080";

    private static final String USERNAME = "sysadmin";

    private static final String PASSWORD = "eSciDoc";

    private final ByteArrayOutputStream baos;

    private final Html5File dropFile;

    @SuppressWarnings("unused")
    private final Application app;

    private final Window window;

    private final VerticalLayout pane;

    private ProgressIndicator progresBar;

    private StagingHandlerClient client;

    private Repositories repositories;

    protected StreamHandlerImpl(ByteArrayOutputStream baos, Html5File dropFile, Application app, Window window,
        VerticalLayout layout, Repositories repositories) {
        Preconditions.checkNotNull(baos, "baos is null: %s", baos);
        Preconditions.checkNotNull(dropFile, "html5File is null: %s", dropFile);
        Preconditions.checkNotNull(app, "app is null: %s", app);
        Preconditions.checkNotNull(layout, "pane is null: %s", layout);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        this.baos = baos;
        this.dropFile = dropFile;

        // TODO refactor this to one component
        this.app = app;
        this.window = window;
        this.pane = layout;

        this.repositories = repositories;
    }

    @Override
    public OutputStream getOutputStream() {
        return baos;
    }

    @Override
    public boolean listenProgress() {
        return true;
    }

    @Override
    public void streamingStarted(@SuppressWarnings("unused") StreamingStartEvent event) {
        progresBar = new ProgressIndicator();
        // TODO what does this line mean?
        progresBar.setIndeterminate(false);

        progresBar.setVisible(true);
        progresBar.setEnabled(true);

        progresBar.setValue(new Float(0f));
        pane.addComponent(new Label(dropFile.getFileName() + "/" + dropFile.getFileSize()));
        pane.addComponent(progresBar);
    }

    @Override
    public void onProgress(StreamingProgressEvent event) {
        long received = event.getBytesReceived();
        long total = event.getContentLength();
        float percentage = (float) received / total;
        LOG.debug("Vaadin Server: " + received + " received, " + " from " + total + " total" + ", progress..."
            + percentage + " %");
        progresBar.setValue(Float.valueOf(percentage));
    }

    // when the file is finished stream.
    @Override
    public void streamingFinished(@SuppressWarnings("unused") StreamingEndEvent event) {
        onStreamFinish(dropFile.getFileName(), dropFile.getType(), baos);
    }

    private void onStreamFinish(String name, @SuppressWarnings("unused") String type, final ByteArrayOutputStream bas) {
        LOG.debug(name + " is uploaded");
        progresBar.setValue(Float.valueOf(1.0f));
        URL uploadedFileUrl = uploadToStagingArea(getStream(bas));
        if (uploadedFileUrl == null) {
            return;
        }

        // TODO what to do with this url.
        LOG.debug("The file can be found in: " + uploadedFileUrl);
        pane.addComponent(new Label(uploadedFileUrl.toString()));
        /*
         * StreamResource resource = new StreamResource(streamSource, name, app); // show the file contents - images
         * only for now Embedded embedded = new Embedded(name, resource); showComponent(embedded, name);
         */
    }

    private URL uploadToStagingArea(InputStream is) {
        try {

            URL serviceAddress = new URL(ESCIDOC_URI);
            client = new StagingHandlerClient(serviceAddress);
            client.setHandle(new Authentication(serviceAddress, USERNAME, PASSWORD).getHandle());
            return client.upload(is);
        }
        catch (EscidocException e) {
            String msg = "can not upload the file..., cause: " + e.getMessage();
            LOG.error(msg, e);
        }
        catch (InternalClientException e) {
            String msg = "can not upload the file..., cause: " + e.getMessage();
            LOG.error(msg, e);
        }
        catch (TransportException e) {
            String msg = "can not upload the file..., cause: " + e.getMessage();
            LOG.error(msg, e);
        }
        catch (MalformedURLException e) {
            String msg = "can not upload the file..., cause: " + e.getMessage();
            LOG.error(msg, e);
        }
        return null;
    }

    private static InputStream getStream(final ByteArrayOutputStream bas) {
        return new StreamSource() {
            @Override
            public InputStream getStream() {
                if (bas != null) {
                    return new ByteArrayInputStream(bas.toByteArray());
                }
                return null;
            }
        }.getStream();
    }

    @Override
    public void streamingFailed(@SuppressWarnings("unused") StreamingErrorEvent event) {
        progresBar.setVisible(false);
    }

    @Override
    public boolean isInterrupted() {
        return false;
    }

    private void showComponent(Component c, String name) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();
        layout.setMargin(true);
        Window w = new Window(name, layout);
        w.setSizeUndefined();
        c.setSizeUndefined();
        w.addComponent(c);
        window.addWindow(w);
    }
}