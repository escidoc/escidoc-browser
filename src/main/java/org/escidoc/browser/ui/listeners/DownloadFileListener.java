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
 * Copyright ${year} Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui.listeners;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.escidoc.browser.model.EscidocServiceLocation;

import com.vaadin.Application;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;

import de.escidoc.core.resources.om.item.component.Component;

public class DownloadFileListener implements ClickListener {

    private final Window mainWindow;

    private final String filename;

    private final Component itemProperties;

    private final EscidocServiceLocation serviceLocation;

    public DownloadFileListener(Component itemProperties, Window mainWindow, EscidocServiceLocation serviceLocation)
        throws IOException {
        this.mainWindow = mainWindow;
        this.filename = itemProperties.getProperties().getFileName();
        this.itemProperties = itemProperties;
        this.serviceLocation = serviceLocation;

    }

    @Override
    public void buttonClick(ClickEvent event) {

        mainWindow.open(new FileDownloadResource(new File(serviceLocation.getEscidocUri()
            + itemProperties.getContent().getXLinkHref() + ".xml"), mainWindow.getApplication(), itemProperties
            .getProperties().getFileName(), serviceLocation, itemProperties), "_blank");

        // final Window subwindow = new Window("Relations");
        // subwindow.setWidth("600px");
        // subwindow.setModal(true);
        //
        // final Label msgWindow = new Label("Failed", Label.CONTENT_RAW);
        //
        // subwindow.addComponent(msgWindow);
        // if (subwindow.getParent() != null) {
        // mainWindow.showNotification("Window is already open");
        // }
        // else {
        // mainWindow.addWindow(subwindow);
        // }
    }
    // private InputStream downloadremote(String file) {
    //
    // // Convert the resource to a URL
    // URL url;
    // try {
    // url = new URL(file);
    // InputStream in;
    // try {
    // in = url.openStream();
    // return in;
    // }
    // catch (IOException e) {
    // System.out.println("Failed opening URL Stream");
    // e.printStackTrace();
    // }
    // }
    // catch (MalformedURLException e1) {
    // System.out.println("Malformed URL");
    // e1.printStackTrace();
    // }
    // return null;
    //
    // }
    //
    // private DownloadStream downloadFile() {
    // DownloadStream ds =
    // new DownloadStream(downloadremote(serviceLocation.getEscidocUri()
    // + itemProperties.getContent().getXLinkHref()), "text/plain", "test.txt");
    // ds.setParameter("Content-Disposition", "attachment; filename=test.txt");
    // return ds;
    // }

}

class FileDownloadResource extends FileResource {

    private final String filename;

    private final EscidocServiceLocation serviceLocation;

    private final Component itemProperties;

    public FileDownloadResource(File sourceFile, Application application, String filename,
        EscidocServiceLocation serviceLocation, Component itemProperties) {
        super(sourceFile, application);
        this.filename = filename;
        this.serviceLocation = serviceLocation;
        this.itemProperties = itemProperties;
    }

    @Override
    public DownloadStream getStream() {
        try {
            final DownloadStream ds =
                new DownloadStream(new FileInputStream(serviceLocation.getEscidocUri()
                    + itemProperties.getContent().getXLinkHref()), getMIMEType(), itemProperties
                    .getProperties().getFileName());
            ds.setParameter("Content-Disposition", "attachment; filename=" + filename);
            ds.setCacheTime(getCacheTime());
            return ds;
        }
        catch (final FileNotFoundException e) {
            // No logging for non-existing files at this level.
            return null;
        }
    }
}
